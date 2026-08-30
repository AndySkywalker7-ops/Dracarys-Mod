package com.dracarys.dracarysmod.client.renderer;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.model.anatomy.BalancedDragonModel;
import com.dracarys.dracarysmod.client.renderer.layer.FarOpaquePresenceLayer;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Dracarys dragon renderer.
 *
 * Keeps the long-range shouldRender override and adds an opaque far-presence
 * render layer that reinforces the textured body against sky/fog.
 */
public class DracarysDragonRenderer
        extends MobRenderer<DracarysDragonEntity, BalancedDragonModel<DracarysDragonEntity>> {

    public DracarysDragonRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new BalancedDragonModel<>(context.bakeLayer(BalancedDragonModel.LAYER)),
                1.2F
        );

        this.addLayer(new FarOpaquePresenceLayer(this));
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

        double maxDistance = realRenderDistance(dragon);

        if (distanceSqr > maxDistance * maxDistance) {
            return false;
        }

        return dragon.noCulling
                || frustum.isVisible(dragon.getBoundingBoxForCulling());
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
