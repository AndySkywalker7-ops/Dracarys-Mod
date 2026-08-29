package com.dracarys.dracarysmod.registry;
import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.world.DragonNestFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(ForgeRegistries.FEATURES,DracarysMod.MOD_ID);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> DRAGON_NEST=FEATURES.register("dragon_nest",()->new DragonNestFeature(NoneFeatureConfiguration.CODEC));
    private ModFeatures(){}
}
