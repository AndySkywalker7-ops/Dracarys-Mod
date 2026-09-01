package com.dracarys.dracarysmod.client.renderer.layer;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.model.anatomy.BalancedDragonModel;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * STEP 5.10R — renders only the authored polygon skull/jaw asset.
 *
 * The body keeps its existing model and variant texture. The head uses a
 * dedicated UV-mapped 256x256 texture selected from the same DragonVariant id.
 * This layer does not create a second dragon and does not alter culling/range.
 */
public final class DracarysHeadAssetLayer
        extends RenderLayer<DracarysDragonEntity, BalancedDragonModel<DracarysDragonEntity>> {

    public DracarysHeadAssetLayer(
            RenderLayerParent<DracarysDragonEntity, BalancedDragonModel<DracarysDragonEntity>> parent
    ) {
        super(parent);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            DracarysDragonEntity dragon,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ResourceLocation texture = DracarysMod.id(
                "textures/entity/dragon/head/" + dragon.getVariant().id() + ".png"
        );
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        getParentModel().renderAuthoredHead(
                poseStack,
                consumer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
    }
}
