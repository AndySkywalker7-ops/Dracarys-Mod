package com.dracarys.dracarysmod.client.lod;

import com.dracarys.dracarysmod.dragon.DragonStage;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.dracarys.dracarysmod.registry.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
 * Step 4.0.7D — True 3D far dragon LOD.
 *
 * Near:
 *   normal renderer + the working 40-block manual bridge.
 *
 * Far:
 *   dedicated low-detail 3D model in world space.
 *
 * Untracked:
 *   cached proxy uses the same 3D LOD model.
 *
 * The discarded screen-space impostor is not used.
 */
public final class FarDragonPresenceManager {

    private static final int MAX_ENTRIES = 32;
    private static final long ENTRY_TTL_MS =
            10L * 60L * 1000L;
    private static final int FULL_SNAPSHOT_REFRESH_TICKS = 20;

    private static final double FORCED_REAL_RENDER_START =
            40.0D;

    private static final Map<UUID, Entry> ENTRIES =
            new LinkedHashMap<>();

    private static long renderStageCalls;
    private static long manualFullAttempts;
    private static long far3dAttempts;
    private static long veryFar3dAttempts;

    private FarDragonPresenceManager() {}

    public static void observe(
            DracarysDragonEntity dragon,
            Level level
    ) {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (!level.isClientSide
                || minecraft.level != level
                || minecraft.player == null) {
            return;
        }

        if (!dragon.isAlive()
                || dragon.isDeadOrDying()) {
            forget(dragon.getUUID());
            return;
        }

        if (dragon.getStage().ordinal()
                < DragonStage.JUVENILE.ordinal()) {
            return;
        }

        Entry entry =
                ENTRIES.get(dragon.getUUID());

        if (entry == null
                || !entry.dimension.equals(
                        level.dimension())) {

            CompoundTag tag =
                    dragon.saveWithoutId(
                            new CompoundTag()
                    );

            DracarysDragonEntity proxy =
                    new DracarysDragonEntity(
                            ModEntities.DRAGON.get(),
                            level
                    );

            proxy.load(tag);
            proxy.moveTo(
                    dragon.getX(),
                    dragon.getY(),
                    dragon.getZ(),
                    dragon.getYRot(),
                    dragon.getXRot()
            );
            proxy.setFlying(
                    dragon.isFlying()
            );
            proxy.tickCount =
                    dragon.tickCount;

            entry = new Entry(
                    dragon.getUUID(),
                    dragon.getId(),
                    level.dimension(),
                    proxy,
                    dragon.position(),
                    dragon.getYRot(),
                    dragon.getXRot(),
                    System.currentTimeMillis()
            );

            ENTRIES.put(
                    dragon.getUUID(),
                    entry
            );

            trimToLimit();
        } else {
            updateFromLive(
                    entry,
                    dragon,
                    false
            );
        }
    }

    public static void clientTick() {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null
                || minecraft.player == null) {
            clear();
            return;
        }

        long now =
                System.currentTimeMillis();

        Iterator<Entry> iterator =
                ENTRIES.values().iterator();

        while (iterator.hasNext()) {
            Entry entry =
                    iterator.next();

            if (!minecraft.level.dimension()
                    .equals(entry.dimension)) {
                iterator.remove();
                continue;
            }

            Entity current =
                    minecraft.level.getEntity(
                            entry.entityId
                    );

            boolean realPresent =
                    current
                            instanceof DracarysDragonEntity live
                    && live.getUUID()
                            .equals(entry.uuid)
                    && live.isAlive()
                    && !live.isDeadOrDying();

            if (realPresent) {
                DracarysDragonEntity live =
                        (DracarysDragonEntity) current;

                if (!entry.realPresentLastTick) {
                    entry.trackingRestoredMs =
                            now;
                }

                entry.realPresentLastTick =
                        true;

                entry.ticksSinceFullRefresh++;

                boolean fullRefresh =
                        entry.ticksSinceFullRefresh
                                >= FULL_SNAPSHOT_REFRESH_TICKS;

                updateFromLive(
                        entry,
                        live,
                        fullRefresh
                );

                if (fullRefresh) {
                    entry.ticksSinceFullRefresh =
                            0;
                }
            } else {
                if (entry.realPresentLastTick) {
                    entry.trackingLostMs =
                            now;
                }

                entry.realPresentLastTick =
                        false;

                if (now - entry.lastSeenLiveMs
                        > ENTRY_TTL_MS) {
                    iterator.remove();
                }
            }
        }
    }

    private static void updateFromLive(
            Entry entry,
            DracarysDragonEntity dragon,
            boolean fullRefresh
    ) {
        if (fullRefresh) {
            CompoundTag tag =
                    dragon.saveWithoutId(
                            new CompoundTag()
                    );
            entry.proxy.load(tag);
        }

        entry.entityId =
                dragon.getId();
        entry.position =
                dragon.position();
        entry.yRot =
                dragon.getYRot();
        entry.xRot =
                dragon.getXRot();
        entry.lastSeenLiveMs =
                System.currentTimeMillis();

        entry.proxy.moveTo(
                entry.position.x,
                entry.position.y,
                entry.position.z,
                entry.yRot,
                entry.xRot
        );

        entry.proxy.setFlying(
                dragon.isFlying()
        );

        entry.proxy.tickCount =
                dragon.tickCount;
    }

    public static void forget(
            UUID dragonId
    ) {
        ENTRIES.remove(dragonId);
    }

    public static void clear() {
        ENTRIES.clear();
    }

    private static DracarysDragonEntity getLiveEntity(
            Minecraft minecraft,
            Entry entry
    ) {
        if (minecraft.level == null) {
            return null;
        }

        Entity current =
                minecraft.level.getEntity(
                        entry.entityId
                );

        if (current
                instanceof DracarysDragonEntity dragon
                && dragon.getUUID()
                        .equals(entry.uuid)
                && dragon.isAlive()
                && !dragon.isDeadOrDying()) {
            return dragon;
        }

        return null;
    }

    public static void render(
            RenderLevelStageEvent event
    ) {
        if (event.getStage()
                != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        renderStageCalls++;

        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null
                || minecraft.player == null) {
            clear();
            return;
        }

        if (ENTRIES.isEmpty()) {
            return;
        }

        Vec3 camera =
                event.getCamera().getPosition();

        PoseStack poseStack =
                event.getPoseStack();

        MultiBufferSource.BufferSource buffers =
                minecraft.renderBuffers()
                        .bufferSource();

        boolean renderedAny =
                false;

        for (Entry entry : ENTRIES.values()) {
            if (!minecraft.level.dimension()
                    .equals(entry.dimension)) {
                continue;
            }

            DracarysDragonEntity live =
                    getLiveEntity(
                            minecraft,
                            entry
                    );

            DracarysDragonEntity visualDragon =
                    live != null
                            ? live
                            : entry.proxy;

            Vec3 visualPosition =
                    live != null
                            ? live.position()
                            : entry.position;

            float visualYRot =
                    live != null
                            ? live.getYRot()
                            : entry.yRot;

            double distance =
                    visualPosition.distanceTo(
                            camera
                    );

            FarDragonLodProfile.Level level =
                    FarDragonLodProfile.levelFor(
                            visualDragon.getStage(),
                            distance
                    );

            entry.lastDistance =
                    distance;
            entry.lastLevel =
                    level;

            if (level
                    == FarDragonLodProfile.Level.NONE) {
                continue;
            }

            if (!event.getFrustum().isVisible(
                    visualDragon
                            .getBoundingBoxForCulling())) {
                continue;
            }

            /*
             * FULL MODEL MANUAL BRIDGE
             *
             * Minecraft's ordinary pass was empirically seen dropping the
             * dragon at ~65-75 blocks. We keep the proven direct renderer from
             * 40 blocks until the full-model LOD boundary.
             */
            if (live != null
                    && level
                    == FarDragonLodProfile.Level.FULL
                    && distance
                    >= FORCED_REAL_RENDER_START) {

                manualFullAttempts++;
                entry.manualFullAttempts++;

                renderFullEntityDirect(
                        minecraft,
                        live,
                        live.position(),
                        live.getYRot(),
                        camera,
                        event,
                        poseStack,
                        buffers
                );

                renderedAny = true;
                continue;
            }

            /*
             * If live still exists inside 40 blocks, normal Minecraft render
             * owns it.
             */
            if (live != null
                    && level
                    == FarDragonLodProfile.Level.FULL) {
                continue;
            }

            /*
             * FAR + VERY FAR
             *
             * No full MobRenderer here. The dedicated low-detail 3D mesh is
             * rendered directly in world space.
             */
            if (level
                    == FarDragonLodProfile.Level.FAR_3D
                    || level
                    == FarDragonLodProfile.Level.VERY_FAR_3D) {

                if (level
                        == FarDragonLodProfile.Level.FAR_3D) {
                    far3dAttempts++;
                    entry.far3dAttempts++;
                } else {
                    veryFar3dAttempts++;
                    entry.veryFar3dAttempts++;
                }

                FarDragonWorldRenderer.render(
                        minecraft,
                        visualDragon,
                        visualPosition,
                        visualYRot,
                        camera,
                        event.getPartialTick(),
                        distance,
                        poseStack,
                        buffers
                );

                renderedAny = true;
            }
        }

        if (renderedAny) {
            buffers.endBatch();
        }
    }

    @SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    private static void renderFullEntityDirect(
            Minecraft minecraft,
            DracarysDragonEntity dragon,
            Vec3 position,
            float yRot,
            Vec3 camera,
            RenderLevelStageEvent event,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffers
    ) {
        poseStack.pushPose();

        poseStack.translate(
                position.x - camera.x,
                position.y - camera.y,
                position.z - camera.z
        );

        EntityRenderer renderer =
                minecraft
                        .getEntityRenderDispatcher()
                        .getRenderer(dragon);

        int packedLight =
                renderer.getPackedLightCoords(
                        dragon,
                        event.getPartialTick()
                );

        renderer.render(
                dragon,
                yRot,
                event.getPartialTick(),
                poseStack,
                buffers,
                packedLight
        );

        poseStack.popPose();
    }

    public static void renderDebugHud(
            RenderGuiEvent.Post event
    ) {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null
                || minecraft.player == null) {
            return;
        }

        GuiGraphics gui =
                event.getGuiGraphics();

        int x = 8;
        int y = 44;
        int lineHeight = 10;

        Entry nearest =
                nearestEntry(minecraft);

        gui.fill(
                x - 4,
                y - 4,
                x + 345,
                y + 165,
                0xA0000000
        );

        draw(
                gui,
                minecraft,
                "DRACARYS LOD DEBUG - STEP 4.0.7D",
                x,
                y,
                0xFFFFC857
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "Cache entries: "
                        + ENTRIES.size(),
                x,
                y,
                ENTRIES.isEmpty()
                        ? 0xFFFF5555
                        : 0xFF55FF55
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "Manual FULL attempts: "
                        + manualFullAttempts,
                x,
                y,
                0xFFFFFFFF
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "FAR 3D attempts: "
                        + far3dAttempts,
                x,
                y,
                far3dAttempts > 0
                        ? 0xFF55FFFF
                        : 0xFFFFAA00
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "VERY FAR 3D attempts: "
                        + veryFar3dAttempts,
                x,
                y,
                veryFar3dAttempts > 0
                        ? 0xFF55FFFF
                        : 0xFFAAAAAA
        );
        y += lineHeight;

        if (nearest == null) {
            draw(
                    gui,
                    minecraft,
                    "Nearest cached dragon: NONE",
                    x,
                    y,
                    0xFFFF5555
            );
            return;
        }

        DracarysDragonEntity live =
                getLiveEntity(
                        minecraft,
                        nearest
                );

        DracarysDragonEntity visualDragon =
                live != null
                        ? live
                        : nearest.proxy;

        Vec3 visualPosition =
                live != null
                        ? live.position()
                        : nearest.position;

        double distance =
                minecraft.player.position()
                        .distanceTo(
                                visualPosition
                        );

        FarDragonLodProfile.Level level =
                FarDragonLodProfile.levelFor(
                        visualDragon.getStage(),
                        distance
                );

        draw(
                gui,
                minecraft,
                "Stage: "
                        + visualDragon
                        .getStage()
                        .name()
                        + "  Real tracked: "
                        + yesNo(live != null),
                x,
                y,
                live != null
                        ? 0xFF55FF55
                        : 0xFFFF5555
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "Current LOD: "
                        + level.name(),
                x,
                y,
                level
                        == FarDragonLodProfile.Level.FULL
                        ? 0xFFFFFFFF
                        : 0xFF55FFFF
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                String.format(
                        "Distance: %.1f / %.1f blocks",
                        distance,
                        FarDragonLodProfile
                                .maxDistance(
                                        visualDragon
                                                .getStage()
                                )
                ),
                x,
                y,
                0xFFFFFFFF
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                String.format(
                        "FULL ends: %.0f | FAR ends: %.0f",
                        FarDragonLodProfile
                                .fullModelEnd(
                                        visualDragon
                                                .getStage()
                                ),
                        FarDragonLodProfile
                                .farModelEnd(
                                        visualDragon
                                                .getStage()
                                )
                ),
                x,
                y,
                0xFFAAAAAA
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "This dragon: FULL="
                        + nearest.manualFullAttempts
                        + " FAR="
                        + nearest.far3dAttempts
                        + " VFAR="
                        + nearest.veryFar3dAttempts,
                x,
                y,
                0xFFAAAAAA
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "Tracking lost: "
                        + ageText(
                                nearest.trackingLostMs
                        ),
                x,
                y,
                0xFFAAAAAA
        );
    }

    private static void draw(
            GuiGraphics gui,
            Minecraft minecraft,
            String text,
            int x,
            int y,
            int color
    ) {
        gui.drawString(
                minecraft.font,
                text,
                x,
                y,
                color,
                true
        );
    }

    private static Entry nearestEntry(
            Minecraft minecraft
    ) {
        if (minecraft.player == null
                || ENTRIES.isEmpty()) {
            return null;
        }

        Entry nearest =
                null;

        double nearestSqr =
                Double.MAX_VALUE;

        for (Entry entry : ENTRIES.values()) {
            if (minecraft.level == null
                    || !minecraft.level
                    .dimension()
                    .equals(entry.dimension)) {
                continue;
            }

            double distanceSqr =
                    minecraft.player
                            .position()
                            .distanceToSqr(
                                    entry.position
                            );

            if (distanceSqr
                    < nearestSqr) {
                nearestSqr =
                        distanceSqr;
                nearest =
                        entry;
            }
        }

        return nearest;
    }

    private static String yesNo(
            boolean value
    ) {
        return value
                ? "YES"
                : "NO";
    }

    private static String ageText(
            long timestampMs
    ) {
        if (timestampMs <= 0L) {
            return "never";
        }

        long age =
                Math.max(
                        0L,
                        System.currentTimeMillis()
                                - timestampMs
                );

        return String.format(
                "%.1fs ago",
                age / 1000.0D
        );
    }

    private static void trimToLimit() {
        while (ENTRIES.size()
                > MAX_ENTRIES) {

            Iterator<UUID> iterator =
                    ENTRIES.keySet()
                            .iterator();

            if (!iterator.hasNext()) {
                return;
            }

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
        private int ticksSinceFullRefresh;

        private boolean realPresentLastTick = true;
        private long trackingLostMs;
        private long trackingRestoredMs;

        private double lastDistance;
        private FarDragonLodProfile.Level lastLevel =
                FarDragonLodProfile.Level.FULL;

        private long manualFullAttempts;
        private long far3dAttempts;
        private long veryFar3dAttempts;

        private Entry(
                UUID uuid,
                int entityId,
                ResourceKey<Level> dimension,
                DracarysDragonEntity proxy,
                Vec3 position,
                float yRot,
                float xRot,
                long lastSeenLiveMs
        ) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.dimension = dimension;
            this.proxy = proxy;
            this.position = position;
            this.yRot = yRot;
            this.xRot = xRot;
            this.lastSeenLiveMs =
                    lastSeenLiveMs;
        }
    }
}
