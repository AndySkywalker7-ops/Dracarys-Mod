package com.dracarys.dracarysmod.registry;
import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.dragon.DragonVariant;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB,DracarysMod.MOD_ID);
    public static final RegistryObject<CreativeModeTab> MAIN=TABS.register("dracarys",()->CreativeModeTab.builder().title(Component.translatable("itemGroup.dracarysmod")).icon(()->ModItems.EGGS.get(DragonVariant.RED).get().getDefaultInstance()).displayItems((params,out)->{
        out.accept(ModItems.DRAGON_BONE.get());out.accept(ModItems.DRAGON_FANG.get());out.accept(ModItems.DRAGON_CLAW.get());out.accept(ModItems.DRAGON_HEART.get());out.accept(ModItems.DRAGON_BLOOD.get());out.accept(ModItems.WING_MEMBRANE.get());out.accept(ModItems.RAW_DRAGON_MEAT.get());out.accept(ModItems.DRAGONBONE_SWORD.get());out.accept(ModItems.DRAGONBONE_PICKAXE.get());out.accept(ModItems.DRAGONBONE_AXE.get());out.accept(ModItems.DRAGON_FANG_DAGGER.get());
        for(DragonVariant v:DragonVariant.values()){out.accept(ModItems.SCALES.get(v).get());out.accept(ModItems.EGGS.get(v).get());for(ArmorItem.Type t:ArmorItem.Type.values())out.accept(ModItems.ARMOR.get(v).get(t).get());}
    }).build());
    private ModCreativeTabs(){}
}
