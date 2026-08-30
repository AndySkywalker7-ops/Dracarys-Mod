package com.dracarys.dracarysmod.client.lod;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.model.lod.FarBalancedDragonModel;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

/**
 * Draws the real 3D LOD mesh directly in world space.
 *
 * It never calls the full MobRenderer, so it does not recursively render the
 * normal dragon model or its old far-presence layers.
 */
public final class FarDragonWorldRenderer {

    private static final ResourceLocation FAR_TEXTURE =
            DracarysMod.id(
                    "textures/entity/dragon/far_lod_neutral.png"
            );

    private static FarBalancedDragonModel<DracarysDragonEntity> model;

    private FarDragonWorldRenderer() {}

    public static void render(
            Minecraft minecraft,
            DracarysDragonEntity dragon,
            Vec3 position,
            float yRot,
            Vec3 cameraPosition,
            float partialTick,
            double distance,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffers
    ) {
        FarBalancedDragonModel<DracarysDragonEntity> farModel =
                model(minecraft);

        FarDragonLodProfile.Level level =
                FarDragonLodProfile.levelFor(
                        dragon.getStage(),
                        distance
                );

        if (level != FarDragonLodProfile.Level.FAR_3D
                && level != FarDragonLodProfile.Level.VERY_FAR_3D) {
            return;
        }

        farModel.setVeryFar(
                level == FarDragonLodProfile.Level.VERY_FAR_3D
        );

        float ageInTicks =
                dragon.tickCount + partialTick;

        farModel.setupAnim(
                dragon,
                0.0F,
                0.0F,
                ageInTicks,
                0.0F,
                dragon.getXRot()
        );

        int color = dragon.getVariant().color();

        float red =
                ((color >> 16) & 0xFF) / 255.0F;
        float green =
                ((color >> 8) & 0xFF) / 255.0F;
        float blue =
                (color & 0xFF) / 255.0F;

        /*
         * Keep black dragons visible without turning every variant pastel.
         */
        float bodyRed = floor(red * 0.92F, 0.12F);
        float bodyGreen = floor(green * 0.92F, 0.12F);
        float bodyBlue = floor(blue * 0.92F, 0.14F);

        float wingRed = floor(red * 0.66F, 0.08F);
        float wingGreen = floor(green * 0.66F, 0.08F);
        float wingBlue = floor(blue * 0.66F, 0.10F);

        poseStack.pushPose();

        poseStack.translate(
                position.x - cameraPosition.x,
                position.y - cameraPosition.y,
                position.z - cameraPosition.z
        );

        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        180.0F - yRot
                )
        );

        float renderScale = dragon.renderScale();

        poseStack.scale(
                renderScale,
                renderScale,
                renderScale
        );

        /*
         * Match the standard LivingEntityRenderer model-space convention.
         */
        poseStack.scale(
                -1.0F,
                -1.0F,
                1.0F
        );

        poseStack.translate(
                0.0D,
                -1.501D,
                0.0D
        );

        VertexConsumer consumer =
                buffers.getBuffer(
                        RenderType.entityCutoutNoCull(
                                FAR_TEXTURE
                        )
                );

        farModel.renderBody(
                poseStack,
                consumer,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                bodyRed,
                bodyGreen,
                bodyBlue,
                1.0F
        );

        farModel.renderWings(
                poseStack,
                consumer,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                wingRed,
                wingGreen,
                wingBlue,
                1.0F
        );

        poseStack.popPose();
    }

    private static FarBalancedDragonModel<DracarysDragonEntity> model(
            Minecraft minecraft
    ) {
        if (model == null) {
            model = new FarBalancedDragonModel<>(
                    minecraft.getEntityModels()
                            .bakeLayer(
                                    FarBalancedDragonModel.LAYER
                            )
            );
        }

        return model;
    }

    private static float floor(
            float value,
            float minimum
    ) {
        return Math.max(
                minimum,
                Math.min(1.0F, value)
        );
    }
}
