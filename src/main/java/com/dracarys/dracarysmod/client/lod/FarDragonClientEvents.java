package com.dracarys.dracarysmod.client.lod;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-only hooks for the Step 4.0.5B reliable far-dragon presence bridge.
 */
@Mod.EventBusSubscriber(modid = DracarysMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class FarDragonClientEvents {
    private FarDragonClientEvents() {}

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide) return;
        if (event.getEntity() instanceof DracarysDragonEntity dragon) {
            // Important: keep the snapshot from the moment the dragon becomes known
            // to the client instead of deleting it.
            FarDragonPresenceManager.observe(dragon, event.getLevel());
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (!event.getLevel().isClientSide) return;
        if (event.getEntity() instanceof DracarysDragonEntity dragon) {
            // One last refresh before vanilla removes the live entity, when this
            // event is available. The LOD no longer depends on this event firing.
            FarDragonPresenceManager.observe(dragon, event.getLevel());
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        FarDragonPresenceManager.clientTick();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        FarDragonPresenceManager.render(event);
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        FarDragonPresenceManager.clear();
    }
}
