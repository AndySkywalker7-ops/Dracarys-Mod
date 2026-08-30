package com.dracarys.dracarysmod.client.renderer;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.lod.FarDragonLodProfile;
import com.dracarys.dracarysmod.client.model.anatomy.BalancedDragonModel;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Full-detail renderer.
 *
 * It intentionally stops volunteering the full mesh after the LOD boundary.
 * The world-space FarDragonWorldRenderer owns longer distances.
 */
public class DracarysDragonRenderer
        extends MobRenderer<
        DracarysDragonEntity,
        BalancedDragonModel<DracarysDragonEntity>> {

    public DracarysDragonRenderer(
            EntityRendererProvider.Context context
    ) {
        super(
                context,
                new BalancedDragonModel<>(
                        context.bakeLayer(
                                BalancedDragonModel.LAYER
                        )
                ),
                1.2F
        );
    }

    @Override
    public ResourceLocation getTextureLocation(
            DracarysDragonEntity dragon
    ) {
        return DracarysMod.id(
                "textures/entity/dragon/"
                        + dragon.getVariant().id()
                        + ".png"
        );
    }

    @Override
    protected void scale(
            DracarysDragonEntity dragon,
            PoseStack poseStack,
            float partialTickTime
    ) {
        float scale =
                dragon.renderScale();

        poseStack.scale(
                scale,
                scale,
                scale
        );
    }

    @Override
    public boolean shouldRender(
            DracarysDragonEntity dragon,
            Frustum frustum,
            double cameraX,
            double cameraY,
            double cameraZ
    ) {
        double dx =
                dragon.getX() - cameraX;
        double dy =
                dragon.getY() - cameraY;
        double dz =
                dragon.getZ() - cameraZ;

        double distanceSqr =
                dx * dx
                        + dy * dy
                        + dz * dz;

        double fullEnd =
                FarDragonLodProfile
                        .fullModelEnd(
                                dragon.getStage()
                        );

        if (distanceSqr
                > fullEnd * fullEnd) {
            return false;
        }

        return dragon.noCulling
                || frustum.isVisible(
                        dragon
                                .getBoundingBoxForCulling()
                );
    }
}
