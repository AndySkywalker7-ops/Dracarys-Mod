package com.dracarys.dracarysmod.client.renderer;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.model.anatomy.BalancedDragonModel;
import com.dracarys.dracarysmod.client.renderer.layer.DracarysHeadAssetLayer;
import com.dracarys.dracarysmod.client.render.DragonRenderDebug;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/**
 * Step 4.0.8 — single authoritative full-detail dragon renderer.
 *
 * There is no FAR model, outline layer, fog override, impostor or alternate
 * texture. Every visible dragon uses BalancedDragonModel and its normal variant
 * texture at every supported distance.
 */
public class DracarysDragonRenderer
        extends MobRenderer<DracarysDragonEntity, BalancedDragonModel<DracarysDragonEntity>> {

    public DracarysDragonRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new BalancedDragonModel<>(context.bakeLayer(BalancedDragonModel.LAYER)),
                1.2F
        );
        this.addLayer(new DracarysHeadAssetLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(DracarysDragonEntity dragon) {
        return DracarysMod.id(
                "textures/entity/dragon/" + dragon.getVariant().id() + ".png"
        );
    }

    @Override
    protected void scale(
            DracarysDragonEntity dragon,
            PoseStack poseStack,
            float partialTickTime
    ) {
        float scale = dragon.renderScale();
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public boolean shouldRender(
            DracarysDragonEntity dragon,
            Frustum frustum,
            double cameraX,
            double cameraY,
            double cameraZ
    ) {
        double dx = dragon.getX() - cameraX;
        double dy = dragon.getY() - cameraY;
        double dz = dragon.getZ() - cameraZ;
        double distanceSqr = dx * dx + dy * dy + dz * dz;
        double distance = Math.sqrt(distanceSqr);
        double maxDistance = customRenderDistance(dragon);
        boolean insideCustomDistance = distanceSqr <= maxDistance * maxDistance;

        AABB cullingBox = dragon.getBoundingBoxForCulling();
        boolean frustumVisible = dragon.noCulling || frustum.isVisible(cullingBox);
        boolean result = insideCustomDistance && frustumVisible;

        DragonRenderDebug.recordShouldRender(
                dragon,
                distance,
                maxDistance,
                insideCustomDistance,
                frustumVisible,
                result,
                cullingBox
        );

        return result;
    }

    @Override
    public void render(
            DracarysDragonEntity dragon,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        DragonRenderDebug.recordRender(dragon);
        super.render(
                dragon,
                entityYaw,
                partialTicks,
                poseStack,
                bufferSource,
                packedLight
        );
    }

    /**
     * Practical long-range targets for the full model.
     *
     * These values intentionally match the current 32-chunk style use case
     * instead of requesting multi-kilometer rendering that the client cannot
     * reliably keep tracked. Size tier adds modest extra range for larger forms.
     */
    public static double customRenderDistance(DracarysDragonEntity dragon) {
        double stageDistance = switch (dragon.getStage()) {
            case BABY -> 128.0D;
            case JUVENILE -> 192.0D;
            case ADOLESCENT -> 256.0D;
            case ADULT -> 320.0D;
            case ANCIENT -> 448.0D;
            case COLOSSAL -> 512.0D;
        };

        double sizeFactor = switch (dragon.getSizeTier()) {
            case SMALL -> 0.90D;
            case MEDIUM -> 1.00D;
            case LARGE -> 1.10D;
            case GIANT -> 1.20D;
        };

        return Math.min(640.0D, stageDistance * sizeFactor);
    }
}
