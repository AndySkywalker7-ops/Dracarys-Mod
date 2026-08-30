package com.dracarys.dracarysmod.client.lod;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Client-only hooks for the Step 4.0.5 far-dragon presence bridge. */
@Mod.EventBusSubscriber(modid = DracarysMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class FarDragonClientEvents {
    private FarDragonClientEvents() {}

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (!event.getLevel().isClientSide) return;
        if (event.getEntity() instanceof DracarysDragonEntity dragon) {
            FarDragonPresenceManager.remember(dragon, event.getLevel());
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide) return;
        if (event.getEntity() instanceof DracarysDragonEntity dragon) {
            // Vanilla is tracking the real entity again; never draw both copies.
            FarDragonPresenceManager.forget(dragon.getUUID());
        }
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
