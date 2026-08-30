package com.dracarys.dracarysmod.registry;
import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,DracarysMod.MOD_ID);
    public static final RegistryObject<EntityType<DracarysDragonEntity>> DRAGON=ENTITIES.register("dracarys_dragon",()->EntityType.Builder.of(DracarysDragonEntity::new, MobCategory.CREATURE).sized(2.4f,2.0f).clientTrackingRange(96).updateInterval(2).build("dracarys_dragon"));
    private ModEntities(){}
}
