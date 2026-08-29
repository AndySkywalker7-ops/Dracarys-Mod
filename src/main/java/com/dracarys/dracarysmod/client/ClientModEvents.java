package com.dracarys.dracarysmod.client;
import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.model.DracarysDragonModel;
import com.dracarys.dracarysmod.client.renderer.DracarysDragonRenderer;
import com.dracarys.dracarysmod.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=DracarysMod.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ClientModEvents {
 @SubscribeEvent public static void layers(EntityRenderersEvent.RegisterLayerDefinitions e){e.registerLayerDefinition(DracarysDragonModel.LAYER,DracarysDragonModel::createBodyLayer);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerEntityRenderer(ModEntities.DRAGON.get(),DracarysDragonRenderer::new);}
}
