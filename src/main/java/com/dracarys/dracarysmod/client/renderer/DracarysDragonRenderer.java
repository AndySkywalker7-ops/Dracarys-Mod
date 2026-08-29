package com.dracarys.dracarysmod.client.renderer;
import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.model.DracarysDragonModel;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
public class DracarysDragonRenderer extends MobRenderer<DracarysDragonEntity,DracarysDragonModel<DracarysDragonEntity>> {
    public DracarysDragonRenderer(EntityRendererProvider.Context c){super(c,new DracarysDragonModel<>(c.bakeLayer(DracarysDragonModel.LAYER)),1.2f);}
    @Override public ResourceLocation getTextureLocation(DracarysDragonEntity e){return DracarysMod.id("textures/entity/dragon/"+e.getVariant().id()+".png");}
    @Override protected void scale(DracarysDragonEntity e, PoseStack p,float partial){float s=e.renderScale();p.scale(s,s,s);}
}
