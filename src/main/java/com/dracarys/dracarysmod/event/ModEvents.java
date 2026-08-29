package com.dracarys.dracarysmod.event;
import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.dracarys.dracarysmod.registry.ModEntities;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=DracarysMod.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD)
public final class ModEvents {
    @SubscribeEvent public static void attributes(EntityAttributeCreationEvent e){e.put(ModEntities.DRAGON.get(), DracarysDragonEntity.createAttributes().build());}
    @SubscribeEvent public static void spawns(SpawnPlacementRegisterEvent e){e.register(ModEntities.DRAGON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DracarysDragonEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);}
}
