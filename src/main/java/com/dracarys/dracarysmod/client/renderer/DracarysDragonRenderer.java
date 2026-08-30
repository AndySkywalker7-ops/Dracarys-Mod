package com.dracarys.dracarysmod.client.renderer;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.model.anatomy.BalancedDragonModel;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Visual-only renderer. Step 4 intentionally selects BALANCED for every dragon;
 * anatomy selection will be introduced after the baseline model is validated.
 */
public class DracarysDragonRenderer extends MobRenderer<DracarysDragonEntity, BalancedDragonModel<DracarysDragonEntity>> {
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
}
