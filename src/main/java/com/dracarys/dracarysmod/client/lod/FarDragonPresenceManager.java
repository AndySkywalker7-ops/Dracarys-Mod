package com.dracarys.dracarysmod.client.lod;

import com.dracarys.dracarysmod.dragon.DragonStage;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.dracarys.dracarysmod.registry.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Reliable client-side bridge between vanilla entity tracking and far-dragon rendering.
 *
 * Step 4.0.5B changes the architecture from "capture only when the entity leaves"
 * to "observe while the entity is alive and already tracked". This means a valid
 * visual snapshot exists before vanilla removes the entity from the client.
 */
public final class FarDragonPresenceManager {
    private static final int MAX_ENTRIES = 32;
    private static final long ENTRY_TTL_MS = 10L * 60L * 1000L;
    private static final int FULL_SNAPSHOT_REFRESH_TICKS = 20;

    private static final Map<UUID, Entry> ENTRIES = new LinkedHashMap<>();

    private FarDragonPresenceManager() {}

    /**
     * Starts or refreshes tracking for a real client-side dragon.
     * Safe to call from EntityJoinLevelEvent and immediately before EntityLeaveLevelEvent.
     */
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

    /**
     * Called every client tick. Existing entries are refreshed by entity id while
     * vanilla still tracks the real dragon. Once getEntity(id) returns null, the
     * cached proxy simply remains at the last known position and becomes the LOD.
     */
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
            if (current instanceof DracarysDragonEntity live
                    && live.getUUID().equals(entry.uuid)
                    && live.isAlive()
                    && !live.isDeadOrDying()) {
                entry.ticksSinceFullRefresh++;
                boolean fullRefresh = entry.ticksSinceFullRefresh >= FULL_SNAPSHOT_REFRESH_TICKS;
                updateFromLive(entry, live, fullRefresh);
                if (fullRefresh) entry.ticksSinceFullRefresh = 0;
            } else if (now - entry.lastSeenLiveMs > ENTRY_TTL_MS) {
                iterator.remove();
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

    /**
     * Returns true only while vanilla still has the real entity in the ClientLevel.
     */
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

            // Critical Step 4.0.5B behavior:
            // while vanilla has the real dragon, never draw the proxy.
            // The instant vanilla stops tracking it, this becomes false and the proxy renders.
            if (realEntityPresent(minecraft, entry)) continue;

            double distanceSqr = entry.position.distanceToSqr(camera);
            if (distanceSqr > entry.maxDistance * entry.maxDistance) continue;

            // Visual animation only. No AI, collision or world ticking.
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

        if (renderedAny) buffers.endBatch();
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
