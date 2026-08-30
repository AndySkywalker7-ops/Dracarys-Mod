package com.dracarys.dracarysmod.client.lod;

import com.dracarys.dracarysmod.dragon.DragonStage;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.dracarys.dracarysmod.registry.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Client-side bridge between vanilla entity tracking and far-dragon rendering.
 *
 * Step 4.0.6A adds explicit diagnostics so in-game testing can distinguish:
 *  - snapshot/cache creation failure
 *  - vanilla tracking loss
 *  - LOD eligibility/range failure
 *  - render hook/renderer failure
 *
 * This is intentionally a temporary diagnostic build.
 */
public final class FarDragonPresenceManager {
    private static final int MAX_ENTRIES = 32;
    private static final long ENTRY_TTL_MS = 10L * 60L * 1000L;
    private static final int FULL_SNAPSHOT_REFRESH_TICKS = 20;

    private static final Map<UUID, Entry> ENTRIES = new LinkedHashMap<>();

    private static long renderStageCalls;
    private static long hudFrames;

    private FarDragonPresenceManager() {}

    public static void observe(DracarysDragonEntity dragon, Level level) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!level.isClientSide || minecraft.level != level || minecraft.player == null) return;

        if (!dragon.isAlive() || dragon.isDeadOrDying()) {
            forget(dragon.getUUID());
            return;
        }

        if (dragon.getStage().ordinal() < DragonStage.JUVENILE.ordinal()) return;

        Entry entry = ENTRIES.get(dragon.getUUID());
        if (entry == null || !entry.dimension.equals(level.dimension())) {
            CompoundTag tag = dragon.saveWithoutId(new CompoundTag());

            DracarysDragonEntity proxy = new DracarysDragonEntity(ModEntities.DRAGON.get(), level);
            proxy.load(tag);
            proxy.moveTo(dragon.getX(), dragon.getY(), dragon.getZ(), dragon.getYRot(), dragon.getXRot());
            proxy.setFlying(dragon.isFlying());
            proxy.tickCount = dragon.tickCount;

            entry = new Entry(
                    dragon.getUUID(),
                    dragon.getId(),
                    level.dimension(),
                    proxy,
                    dragon.position(),
                    dragon.getYRot(),
                    dragon.getXRot(),
                    System.currentTimeMillis(),
                    maxDistanceFor(dragon.getStage())
            );

            ENTRIES.put(dragon.getUUID(), entry);
            trimToLimit();
        } else {
            updateFromLive(entry, dragon, false);
        }
    }

    public static void clientTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            clear();
            return;
        }

        long now = System.currentTimeMillis();
        Iterator<Entry> iterator = ENTRIES.values().iterator();

        while (iterator.hasNext()) {
            Entry entry = iterator.next();

            if (!minecraft.level.dimension().equals(entry.dimension)) {
                iterator.remove();
                continue;
            }

            Entity current = minecraft.level.getEntity(entry.entityId);
            boolean realPresent = current instanceof DracarysDragonEntity live
                    && live.getUUID().equals(entry.uuid)
                    && live.isAlive()
                    && !live.isDeadOrDying();

            if (realPresent) {
                DracarysDragonEntity live = (DracarysDragonEntity) current;

                if (!entry.realPresentLastTick) {
                    entry.trackingRestoredMs = now;
                }
                entry.realPresentLastTick = true;

                entry.ticksSinceFullRefresh++;
                boolean fullRefresh = entry.ticksSinceFullRefresh >= FULL_SNAPSHOT_REFRESH_TICKS;
                updateFromLive(entry, live, fullRefresh);
                if (fullRefresh) entry.ticksSinceFullRefresh = 0;
            } else {
                if (entry.realPresentLastTick) {
                    entry.trackingLostMs = now;
                }
                entry.realPresentLastTick = false;

                if (now - entry.lastSeenLiveMs > ENTRY_TTL_MS) {
                    iterator.remove();
                }
            }
        }
    }

    private static void updateFromLive(Entry entry, DracarysDragonEntity dragon, boolean fullRefresh) {
        if (fullRefresh) {
            CompoundTag tag = dragon.saveWithoutId(new CompoundTag());
            entry.proxy.load(tag);
        }

        entry.entityId = dragon.getId();
        entry.position = dragon.position();
        entry.yRot = dragon.getYRot();
        entry.xRot = dragon.getXRot();
        entry.lastSeenLiveMs = System.currentTimeMillis();
        entry.maxDistance = maxDistanceFor(dragon.getStage());

        entry.proxy.moveTo(
                entry.position.x,
                entry.position.y,
                entry.position.z,
                entry.yRot,
                entry.xRot
        );
        entry.proxy.setFlying(dragon.isFlying());
        entry.proxy.tickCount = dragon.tickCount;
    }

    public static void forget(UUID dragonId) {
        ENTRIES.remove(dragonId);
    }

    public static void clear() {
        ENTRIES.clear();
    }

    public static int cachedCount() {
        return ENTRIES.size();
    }

    private static boolean realEntityPresent(Minecraft minecraft, Entry entry) {
        if (minecraft.level == null) return false;

        Entity current = minecraft.level.getEntity(entry.entityId);
        return current instanceof DracarysDragonEntity dragon
                && dragon.getUUID().equals(entry.uuid)
                && dragon.isAlive()
                && !dragon.isDeadOrDying();
    }

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        renderStageCalls++;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            clear();
            return;
        }
        if (ENTRIES.isEmpty()) return;

        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();

        boolean renderedAny = false;

        for (Entry entry : ENTRIES.values()) {
            if (!minecraft.level.dimension().equals(entry.dimension)) continue;

            boolean realPresent = realEntityPresent(minecraft, entry);
            double distance = Math.sqrt(entry.position.distanceToSqr(camera));
            boolean inRange = distance <= entry.maxDistance;

            entry.lastDistance = distance;
            entry.lastRealPresent = realPresent;
            entry.lastLodEligible = !realPresent && inRange;

            if (realPresent || !inRange) continue;

            entry.renderAttempts++;
            entry.lastRenderAttemptMs = System.currentTimeMillis();
            entry.lastRenderDistance = distance;

            // Visual-only proxy animation. No AI, collision, combat or world ticking.
            entry.proxy.tickCount++;

            poseStack.pushPose();
            poseStack.translate(
                    entry.position.x - camera.x,
                    entry.position.y - camera.y,
                    entry.position.z - camera.z
            );

            EntityRenderer<? super DracarysDragonEntity> renderer =
                    minecraft.getEntityRenderDispatcher().getRenderer(entry.proxy);

            renderer.render(
                    entry.proxy,
                    entry.yRot,
                    event.getPartialTick(),
                    poseStack,
                    buffers,
                    LightTexture.FULL_BRIGHT
            );

            poseStack.popPose();
            renderedAny = true;
        }

        if (renderedAny) {
            buffers.endBatch();
        }
    }

    /**
     * Temporary on-screen diagnostics for Step 4.0.6A.
     * Intentionally always visible while a world is open so we can diagnose
     * tracking loss without guessing.
     */
    public static void renderDebugHud(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        hudFrames++;

        GuiGraphics gui = event.getGuiGraphics();

        int x = 8;
        int y = 44;
        int lineHeight = 10;

        Entry nearest = nearestEntry(minecraft);

        int panelWidth = 300;
        int lines = nearest == null ? 5 : 13;
        int panelHeight = lines * lineHeight + 10;

        gui.fill(x - 4, y - 4, x + panelWidth, y + panelHeight, 0xA0000000);

        draw(gui, minecraft, "DRACARYS LOD DEBUG - STEP 4.0.6A", x, y, 0xFFFFC857);
        y += lineHeight;

        draw(gui, minecraft, "Cache entries: " + ENTRIES.size(), x, y,
                ENTRIES.isEmpty() ? 0xFFFF5555 : 0xFF55FF55);
        y += lineHeight;

        draw(gui, minecraft, "Render-stage calls: " + renderStageCalls, x, y, 0xFFFFFFFF);
        y += lineHeight;

        draw(gui, minecraft, "HUD frames: " + hudFrames, x, y, 0xFFAAAAAA);
        y += lineHeight;

        if (nearest == null) {
            draw(gui, minecraft, "Nearest cached dragon: NONE", x, y, 0xFFFF5555);
            return;
        }

        boolean realPresent = realEntityPresent(minecraft, nearest);
        double playerDistance = minecraft.player.position().distanceTo(nearest.position);
        boolean inRange = playerDistance <= nearest.maxDistance;
        boolean lodActive = !realPresent && inRange;

        draw(gui, minecraft, "Dragon: " + shortUuid(nearest.uuid)
                + "  Stage: " + nearest.proxy.getStage().name(), x, y, 0xFFFFFFFF);
        y += lineHeight;

        draw(gui, minecraft, "Real entity tracked: " + yesNo(realPresent), x, y,
                realPresent ? 0xFF55FF55 : 0xFFFF5555);
        y += lineHeight;

        draw(gui, minecraft, "LOD cached: YES", x, y, 0xFF55FF55);
        y += lineHeight;

        draw(gui, minecraft, "LOD eligible/active: " + yesNo(lodActive), x, y,
                lodActive ? 0xFF55FFFF : 0xFFFFAA00);
        y += lineHeight;

        draw(gui, minecraft, String.format("Distance: %.1f / %.1f blocks",
                playerDistance, nearest.maxDistance), x, y,
                inRange ? 0xFFFFFFFF : 0xFFFF5555);
        y += lineHeight;

        draw(gui, minecraft, "Render attempts: " + nearest.renderAttempts, x, y,
                nearest.renderAttempts > 0 ? 0xFF55FFFF : 0xFFFFAA00);
        y += lineHeight;

        draw(gui, minecraft, String.format("Last render-attempt distance: %.1f",
                nearest.lastRenderDistance), x, y, 0xFFAAAAAA);
        y += lineHeight;

        draw(gui, minecraft, "Tracking lost: " + ageText(nearest.trackingLostMs), x, y, 0xFFAAAAAA);
        y += lineHeight;

        draw(gui, minecraft, String.format("Snapshot XYZ: %.0f %.0f %.0f",
                nearest.position.x, nearest.position.y, nearest.position.z), x, y, 0xFFAAAAAA);
    }

    private static void draw(
            GuiGraphics gui,
            Minecraft minecraft,
            String text,
            int x,
            int y,
            int color
    ) {
        gui.drawString(minecraft.font, text, x, y, color, true);
    }

    private static Entry nearestEntry(Minecraft minecraft) {
        if (minecraft.player == null || ENTRIES.isEmpty()) return null;

        Entry nearest = null;
        double nearestSqr = Double.MAX_VALUE;

        for (Entry entry : ENTRIES.values()) {
            if (minecraft.level == null || !minecraft.level.dimension().equals(entry.dimension)) continue;

            double distanceSqr = minecraft.player.position().distanceToSqr(entry.position);
            if (distanceSqr < nearestSqr) {
                nearestSqr = distanceSqr;
                nearest = entry;
            }
        }

        return nearest;
    }

    private static String shortUuid(UUID uuid) {
        String value = uuid.toString();
        return value.substring(0, 8);
    }

    private static String yesNo(boolean value) {
        return value ? "YES" : "NO";
    }

    private static String ageText(long timestampMs) {
        if (timestampMs <= 0L) return "never";

        long age = Math.max(0L, System.currentTimeMillis() - timestampMs);
        return String.format("%.1fs ago", age / 1000.0D);
    }

    private static double maxDistanceFor(DragonStage stage) {
        return switch (stage) {
            case BABY -> 384.0D;
            case JUVENILE -> 800.0D;
            case ADOLESCENT -> 1000.0D;
            case ADULT -> 1400.0D;
            case ANCIENT -> 1800.0D;
            case COLOSSAL -> 2400.0D;
        };
    }

    private static void trimToLimit() {
        while (ENTRIES.size() > MAX_ENTRIES) {
            Iterator<UUID> iterator = ENTRIES.keySet().iterator();
            if (!iterator.hasNext()) return;

            iterator.next();
            iterator.remove();
        }
    }

    private static final class Entry {
        private final UUID uuid;
        private int entityId;
        private final ResourceKey<Level> dimension;
        private final DracarysDragonEntity proxy;

        private Vec3 position;
        private float yRot;
        private float xRot;
        private long lastSeenLiveMs;
        private double maxDistance;
        private int ticksSinceFullRefresh;

        private boolean realPresentLastTick = true;
        private long trackingLostMs;
        private long trackingRestoredMs;

        private boolean lastRealPresent = true;
        private boolean lastLodEligible;
        private double lastDistance;

        private long renderAttempts;
        private long lastRenderAttemptMs;
        private double lastRenderDistance;

        private Entry(
                UUID uuid,
                int entityId,
                ResourceKey<Level> dimension,
                DracarysDragonEntity proxy,
                Vec3 position,
                float yRot,
                float xRot,
                long lastSeenLiveMs,
                double maxDistance
        ) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.dimension = dimension;
            this.proxy = proxy;
            this.position = position;
            this.yRot = yRot;
            this.xRot = xRot;
            this.lastSeenLiveMs = lastSeenLiveMs;
            this.maxDistance = maxDistance;
        }
    }
}
