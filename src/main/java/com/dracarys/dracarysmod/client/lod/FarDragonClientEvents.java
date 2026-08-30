package com.dracarys.dracarysmod.client.lod;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-only hooks for true world-space 3D dragon LOD.
 *
 * The old screen-space impostor hook is intentionally gone.
 */
@Mod.EventBusSubscriber(
        modid = DracarysMod.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public final class FarDragonClientEvents {

    private FarDragonClientEvents() {}

    @SubscribeEvent
    public static void onEntityJoin(
            EntityJoinLevelEvent event
    ) {
        if (!event.getLevel().isClientSide) {
            return;
        }

        if (event.getEntity()
                instanceof DracarysDragonEntity dragon) {
            FarDragonPresenceManager.observe(
                    dragon,
                    event.getLevel()
            );
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(
            EntityLeaveLevelEvent event
    ) {
        if (!event.getLevel().isClientSide) {
            return;
        }

        if (event.getEntity()
                instanceof DracarysDragonEntity dragon) {
            FarDragonPresenceManager.observe(
                    dragon,
                    event.getLevel()
            );
        }
    }

    @SubscribeEvent
    public static void onClientTick(
            TickEvent.ClientTickEvent event
    ) {
        if (event.phase
                != TickEvent.Phase.END) {
            return;
        }

        FarDragonPresenceManager
                .clientTick();
    }

    @SubscribeEvent
    public static void onRenderLevel(
            RenderLevelStageEvent event
    ) {
        FarDragonPresenceManager
                .render(event);
    }

    @SubscribeEvent
    public static void onRenderGuiPost(
            RenderGuiEvent.Post event
    ) {
        FarDragonPresenceManager
                .renderDebugHud(event);
    }

    @SubscribeEvent
    public static void onLogout(
            ClientPlayerNetworkEvent.LoggingOut event
    ) {
        FarDragonPresenceManager.clear();
    }
}
