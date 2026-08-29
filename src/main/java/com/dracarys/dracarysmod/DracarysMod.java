package com.dracarys.dracarysmod;

import com.dracarys.dracarysmod.config.DracarysConfig;
import com.dracarys.dracarysmod.registry.ModCreativeTabs;
import com.dracarys.dracarysmod.registry.ModEntities;
import com.dracarys.dracarysmod.registry.ModFeatures;
import com.dracarys.dracarysmod.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DracarysMod.MOD_ID)
public final class DracarysMod {
    public static final String MOD_ID = "dracarysmod";

    public DracarysMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModFeatures.FEATURES.register(bus);
        ModCreativeTabs.TABS.register(bus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DracarysConfig.SPEC);
    }

    public static ResourceLocation id(String path) { return new ResourceLocation(MOD_ID, path); }
}
