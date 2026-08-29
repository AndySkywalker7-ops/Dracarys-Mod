package com.dracarys.dracarysmod.event;
import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.command.DracarysCommands;
import com.dracarys.dracarysmod.item.DragonScaleArmorItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=DracarysMod.MOD_ID,bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEvents {
    @SubscribeEvent public static void commands(RegisterCommandsEvent e){DracarysCommands.register(e.getDispatcher());}
    @SubscribeEvent public static void playerTick(TickEvent.PlayerTickEvent e){if(e.phase!=TickEvent.Phase.END||e.player.level().isClientSide||e.player.tickCount%40!=0)return;Player p=e.player;DragonScaleArmorItem first=null;for(EquipmentSlot s:new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}){ItemStack st=p.getItemBySlot(s);if(!(st.getItem() instanceof DragonScaleArmorItem a))return;if(first==null)first=a;else if(a.variant()!=first.variant())return;}p.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,80,0,false,false,true));}
}
