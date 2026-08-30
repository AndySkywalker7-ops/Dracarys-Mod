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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Client-side bridge between vanilla entity tracking and the future true LOD system.
 *
 * <p>When a live Dracarys dragon leaves the client level because vanilla tracking
 * dropped it, a lightweight cache entry keeps a frozen visual proxy. The proxy is
 * never added to the level: it has no hitbox, AI, collision, sounds, particles or
 * gameplay authority. It exists only so a distant dragon does not instantly vanish.</p>
 *
 * <p>Step 4.0.5 intentionally reuses the existing dragon renderer as a proof of
 * concept. A later step will replace this proxy with a genuinely simplified LOD
 * mesh and server-fed position snapshots.</p>
 */
public final class FarDragonPresenceManager {
    private static final int MAX_ENTRIES = 32;
    private static final long ENTRY_TTL_MS = 20L * 60L * 1000L;
    private static final Map<UUID, Entry> ENTRIES = new LinkedHashMap<>();

    private FarDragonPresenceManager() {}

    public static void remember(DracarysDragonEntity dragon, Level level) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!level.isClientSide || minecraft.level != level || minecraft.player == null) return;
        if (!dragon.isAlive() || dragon.isDeadOrDying()) return;
        if (dragon.getStage().ordinal() < DragonStage.JUVENILE.ordinal()) return;

        CompoundTag tag = dragon.saveWithoutId(new CompoundTag());
        DracarysDragonEntity proxy = new DracarysDragonEntity(ModEntities.DRAGON.get(), level);
        proxy.load(tag);
        proxy.moveTo(dragon.getX(), dragon.getY(), dragon.getZ(), dragon.getYRot(), dragon.getXRot());
        proxy.setFlying(dragon.isFlying());
        proxy.tickCount = dragon.tickCount;

        Entry entry = new Entry(
                level.dimension(),
                proxy,
                dragon.position(),
                System.currentTimeMillis(),
                maxDistanceFor(dragon.getStage())
        );
        ENTRIES.put(dragon.getUUID(), entry);
        trimToLimit();
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

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            clear();
            return;
        }
        if (ENTRIES.isEmpty()) return;

        long now = System.currentTimeMillis();
        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();

        Iterator<Entry> iterator = ENTRIES.values().iterator();
        boolean renderedAny = false;
        while (iterator.hasNext()) {
            Entry entry = iterator.next();

            if (now - entry.capturedAtMs > ENTRY_TTL_MS) {
                iterator.remove();
                continue;
            }
            if (!minecraft.level.dimension().equals(entry.dimension)) continue;

            double distanceSqr = entry.position.distanceToSqr(camera);
            if (distanceSqr > entry.maxDistance * entry.maxDistance) continue;

            // Keep passive/flight animation alive without ticking AI or gameplay.
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
                    entry.proxy.getYRot(),
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
        private final ResourceKey<Level> dimension;
        private final DracarysDragonEntity proxy;
        private final Vec3 position;
        private final long capturedAtMs;
        private final double maxDistance;

        private Entry(ResourceKey<Level> dimension, DracarysDragonEntity proxy,
                      Vec3 position, long capturedAtMs, double maxDistance) {
            this.dimension = dimension;
            this.proxy = proxy;
            this.position = position;
            this.capturedAtMs = capturedAtMs;
            this.maxDistance = maxDistance;
        }
    }
}
