package com.dracarys.dracarysmod.client.renderer;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.model.anatomy.BalancedDragonModel;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Dracarys dragon renderer.
 *
 * Step 4.0.6A2:
 * The diagnostics proved that the client still owns/tracks colossal dragons
 * after vanilla has already stopped drawing them. Therefore the far-LOD must
 * not be the first fix: we must allow the REAL renderer to remain visible for
 * as long as the client still has the entity.
 *
 * This override deliberately bypasses vanilla's generic entity render-distance
 * decision, but it still:
 *  - applies a finite Dracarys-specific maximum distance;
 *  - keeps frustum culling using the dragon's enlarged visual culling box;
 *  - does NOT disable culling globally;
 *  - does NOT change tracking, hitboxes, AI, gameplay or scale.
 *
 * Once the real entity finally leaves ClientLevel, the existing far-LOD bridge
 * can take over.
 */
public class DracarysDragonRenderer
        extends MobRenderer<DracarysDragonEntity, BalancedDragonModel<DracarysDragonEntity>> {

    public DracarysDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new BalancedDragonModel<>(context.bakeLayer(BalancedDragonModel.LAYER)), 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(DracarysDragonEntity dragon) {
        return DracarysMod.id("textures/entity/dragon/" + dragon.getVariant().id() + ".png");
    }

    @Override
    protected void scale(DracarysDragonEntity dragon, PoseStack poseStack, float partialTickTime) {
        float scale = dragon.renderScale();
        poseStack.scale(scale, scale, scale);
    }

    /**
     * Keep the real tracked dragon visible beyond vanilla's ordinary entity
     * rendering cutoff.
     *
     * The diagnostic build showed:
     *   Real entity tracked = YES
     *   LOD active = NO
     * while the dragon itself was already invisible.
     *
     * That proves the entity was being renderer-culled rather than network-
     * untracked.
     */
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

        double maxDistance = realRenderDistance(dragon);
        if (distanceSqr > maxDistance * maxDistance) {
            return false;
        }

        // Important: do NOT return true unconditionally.
        // We still avoid drawing a gigantic dragon when it is completely
        // outside the camera frustum.
        return dragon.noCulling || frustum.isVisible(dragon.getBoundingBoxForCulling());
    }

    private static double realRenderDistance(DracarysDragonEntity dragon) {
        double stageDistance = switch (dragon.getStage()) {
            case BABY -> 384.0D;
            case JUVENILE -> 800.0D;
            case ADOLESCENT -> 1000.0D;
            case ADULT -> 1400.0D;
            case ANCIENT -> 1800.0D;
            case COLOSSAL -> 2400.0D;
        };

        double sizeFactor = switch (dragon.getSizeTier()) {
            case SMALL -> 0.90D;
            case MEDIUM -> 1.00D;
            case LARGE -> 1.08D;
            case GIANT -> 1.15D;
        };

        return Math.min(2400.0D, stageDistance * sizeFactor);
    }
}
