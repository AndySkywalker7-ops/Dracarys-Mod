package com.dracarys.dracarysmod.client.render;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.renderer.DracarysDragonRenderer;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.dracarys.dracarysmod.entity.DracarysDragonPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Step 4.0.8 — Clean Long Range Dragon Rendering.
 *
 * There is only one visual model: DracarysDragonRenderer + BalancedDragonModel.
 *
 * Normal Minecraft/Forge rendering is always preferred. AFTER_ENTITIES checks
 * whether that exact dragon was actually rendered during the current frame.
 * Only if it was skipped by the normal/modded entity pipeline do we invoke the
 * same renderer once as a fallback.
 *
 * No proxies, LODs, outlines, sprites, fog bypasses, glowing passes or alternate
 * textures are used here.
 */
@Mod.EventBusSubscriber(
        modid = DracarysMod.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public final class CleanLongRangeDragonRenderEvents {
    private static final Map<UUID, DracarysDragonEntity> TRACKED_DRAGONS =
            new LinkedHashMap<>();

    private CleanLongRangeDragonRenderEvents() {}

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide) {
            return;
        }

        if (event.getEntity() instanceof DracarysDragonEntity dragon) {
            TRACKED_DRAGONS.put(dragon.getUUID(), dragon);
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (!event.getLevel().isClientSide) {
            return;
        }

        if (event.getEntity() instanceof DracarysDragonEntity dragon) {
            TRACKED_DRAGONS.remove(dragon.getUUID());
            DragonRenderDebug.forget(dragon.getUUID());
        }
    }

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            DragonRenderDebug.beginFrame();
            return;
        }

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || TRACKED_DRAGONS.isEmpty()) {
            return;
        }

        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        boolean renderedFallback = false;

        for (DracarysDragonEntity dragon : TRACKED_DRAGONS.values()) {
            if (dragon == null
                    || dragon.level() != minecraft.level
                    || !dragon.isAlive()
                    || dragon.isDeadOrDying()) {
                continue;
            }

            Entity current = minecraft.level.getEntity(dragon.getId());
            if (current != dragon) {
                continue;
            }

            double dx = dragon.getX() - camera.x;
            double dy = dragon.getY() - camera.y;
            double dz = dragon.getZ() - camera.z;
            double distanceSqr = dx * dx + dy * dy + dz * dz;
            double maxDistance = DracarysDragonRenderer.customRenderDistance(dragon);

            if (distanceSqr > maxDistance * maxDistance) {
                continue;
            }

            AABB cullingBox = dragon.getBoundingBoxForCulling();
            if (!event.getFrustum().isVisible(cullingBox)) {
                continue;
            }

            // Primary rule: never draw a second representation in the same frame.
            if (DragonRenderDebug.wasRenderedThisFrame(dragon)) {
                continue;
            }

            renderSameFullModel(
                    minecraft,
                    dragon,
                    camera,
                    event,
                    poseStack,
                    buffers
            );

            DragonRenderDebug.recordFallback(dragon);
            renderedFallback = true;
        }

        if (renderedFallback) {
            buffers.endBatch();
        }

        if (minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            renderMultipartDebugBoxes(camera,poseStack,buffers);
        }
    }

    /**
     * Forge multipart parts are real hit targets, but vanilla F3+B does not
     * consistently draw custom PartEntity boxes. Draw them explicitly only while
     * hitbox debug is enabled so testing is deterministic and free in normal play.
     */
    private static void renderMultipartDebugBoxes(
            Vec3 camera,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffers
    ){
        VertexConsumer lines=buffers.getBuffer(RenderType.lines());

        for(DracarysDragonEntity dragon:TRACKED_DRAGONS.values()){
            if(dragon==null||!dragon.isAlive())continue;

            for(DracarysDragonPart part:dragon.getDragonParts()){
                AABB box=part.getBoundingBox().move(-camera.x,-camera.y,-camera.z);
                float[] color=partColor(part.getPartName());
                LevelRenderer.renderLineBox(
                        poseStack,
                        lines,
                        box,
                        color[0],color[1],color[2],1.0F
                );
            }
        }

        buffers.endBatch();
    }

    private static float[] partColor(String name){
        return switch(name){
            case "head" -> new float[]{1.0F,0.20F,0.20F};
            case "neck" -> new float[]{1.0F,0.55F,0.10F};
            case "left_wing" -> new float[]{0.15F,0.95F,0.95F};
            case "right_wing" -> new float[]{0.20F,0.45F,1.0F};
            case "tail" -> new float[]{0.75F,0.25F,1.0F};
            case "legs" -> new float[]{0.25F,1.0F,0.35F};
            default -> new float[]{1.0F,0.95F,0.25F};
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void renderSameFullModel(
            Minecraft minecraft,
            DracarysDragonEntity dragon,
            Vec3 camera,
            RenderLevelStageEvent event,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffers
    ) {
        poseStack.pushPose();
        poseStack.translate(
                dragon.getX() - camera.x,
                dragon.getY() - camera.y,
                dragon.getZ() - camera.z
        );

        EntityRenderer renderer = minecraft
                .getEntityRenderDispatcher()
                .getRenderer(dragon);

        int packedLight = renderer.getPackedLightCoords(
                dragon,
                event.getPartialTick()
        );

        // Directly call the exact registered renderer. No alternate mesh/texture.
        renderer.render(
                dragon,
                dragon.getYRot(),
                event.getPartialTick(),
                poseStack,
                buffers,
                packedLight
        );

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onRenderDebugHud(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || TRACKED_DRAGONS.isEmpty()) {
            return;
        }

        DracarysDragonEntity nearest = null;
        double nearestDistanceSqr = Double.MAX_VALUE;

        for (DracarysDragonEntity dragon : TRACKED_DRAGONS.values()) {
            if (dragon == null || dragon.level() != minecraft.level) {
                continue;
            }

            double d = minecraft.player.distanceToSqr(dragon);
            if (d < nearestDistanceSqr) {
                nearestDistanceSqr = d;
                nearest = dragon;
            }
        }

        if (nearest == null) {
            return;
        }

        DragonRenderDebug.State state = DragonRenderDebug.get(nearest);
        double distance = Math.sqrt(nearestDistanceSqr);
        double maxDistance = DracarysDragonRenderer.customRenderDistance(nearest);
        boolean realTracked = minecraft.level.getEntity(nearest.getId()) == nearest;

        GuiGraphics gui = event.getGuiGraphics();
        int x = 8;
        int y = 44;
        int line = 10;

        gui.fill(x - 4, y - 4, x + 420, y + 142, 0xA0000000);
        draw(gui, minecraft, "DRACARYS RENDER DEBUG - STEP 4.0.9B", x, y, 0xFFFFC857);
        y += line;
        draw(gui, minecraft, "Stage: " + nearest.getStage().name(), x, y, 0xFFFFFFFF);
        y += line;
        draw(gui, minecraft, "Size: " + nearest.getSizeTier().name(), x, y, 0xFFFFFFFF);
        y += line;
        draw(gui, minecraft, String.format("Distance: %.1f / %.1f blocks", distance, maxDistance), x, y, 0xFFFFFFFF);
        y += line;
        draw(gui, minecraft, "Real entity tracked: " + yesNo(realTracked), x, y, realTracked ? 0xFF55FF55 : 0xFFFF5555);
        y += line;

        if (state == null) {
            draw(gui, minecraft, "Renderer shouldRender: not sampled yet", x, y, 0xFFFFAA00);
            return;
        }

        draw(gui, minecraft, "Renderer shouldRender: " + yesNo(state.rendererShouldRender), x, y,
                state.rendererShouldRender ? 0xFF55FF55 : 0xFFFFAA00);
        y += line;
        draw(gui, minecraft, "Inside custom distance: " + yesNo(state.insideCustomDistance), x, y,
                state.insideCustomDistance ? 0xFF55FF55 : 0xFFFF5555);
        y += line;
        draw(gui, minecraft, "Frustum visible: " + yesNo(state.frustumVisible), x, y,
                state.frustumVisible ? 0xFF55FF55 : 0xFFFF5555);
        y += line;
        draw(gui, minecraft, "Culling AABB: " + formatAabb(state.cullingBox), x, y, 0xFFBBBBBB);
        y += line;
        draw(gui, minecraft, "Render calls: " + state.renderCalls + " | fallback: " + state.fallbackCalls, x, y, 0xFF55FFFF);
        y += line;
        boolean hitboxDebug=minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes();
        draw(gui, minecraft, "Multipart hitboxes: " + nearest.getDragonParts().length + " | F3+B: " + (hitboxDebug ? "ON" : "OFF"), x, y, hitboxDebug ? 0xFF55FF55 : 0xFFAAAAAA);
        y += line;
        draw(gui, minecraft, "Authority this frame: " + authority(state), x, y, 0xFFFFFFFF);
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        TRACKED_DRAGONS.clear();
        DragonRenderDebug.clear();
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

    private static String authority(DragonRenderDebug.State state) {
        if (state.lastFallbackFrame == DragonRenderDebug.frameId()) {
            return "FALLBACK_FULL (same renderer/model)";
        }
        if (state.lastRenderedFrame == DragonRenderDebug.frameId()) {
            return "NORMAL_FULL";
        }
        return "NONE";
    }

    private static String yesNo(boolean value) {
        return value ? "YES" : "NO";
    }

    private static String formatAabb(AABB box) {
        if (box == null) {
            return "not sampled";
        }
        return String.format(
                "[%.1f %.1f %.1f] -> [%.1f %.1f %.1f]",
                box.minX,
                box.minY,
                box.minZ,
                box.maxX,
                box.maxY,
                box.maxZ
        );
    }
}
