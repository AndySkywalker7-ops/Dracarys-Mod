package com.dracarys.dracarysmod.client.render;

import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Temporary render diagnostics for Step 4.0.8.
 *
 * This class never renders a dragon. It only records which render path handled
 * each real tracked entity during the current client frame.
 */
public final class DragonRenderDebug {
    private static final Map<UUID, State> STATES = new HashMap<>();
    private static long frameId;

    private DragonRenderDebug() {}

    public static void beginFrame() {
        frameId++;
    }

    public static long frameId() {
        return frameId;
    }

    public static void recordShouldRender(
            DracarysDragonEntity dragon,
            double distance,
            double customDistance,
            boolean insideCustomDistance,
            boolean frustumVisible,
            boolean result,
            AABB cullingBox
    ) {
        State state = state(dragon);
        state.distance = distance;
        state.customDistance = customDistance;
        state.insideCustomDistance = insideCustomDistance;
        state.frustumVisible = frustumVisible;
        state.rendererShouldRender = result;
        state.cullingBox = cullingBox;
    }

    public static void recordRender(DracarysDragonEntity dragon) {
        State state = state(dragon);
        state.lastRenderedFrame = frameId;
        state.renderCalls++;
    }

    public static void recordFallback(DracarysDragonEntity dragon) {
        State state = state(dragon);
        state.lastFallbackFrame = frameId;
        state.fallbackCalls++;
        // DracarysDragonRenderer.render(...) already records the render call.
        state.lastRenderedFrame = frameId;
    }

    public static boolean wasRenderedThisFrame(DracarysDragonEntity dragon) {
        State state = STATES.get(dragon.getUUID());
        return state != null && state.lastRenderedFrame == frameId;
    }

    public static State get(DracarysDragonEntity dragon) {
        return STATES.get(dragon.getUUID());
    }

    public static void forget(UUID id) {
        STATES.remove(id);
    }

    public static void clear() {
        STATES.clear();
        frameId = 0L;
    }

    private static State state(DracarysDragonEntity dragon) {
        return STATES.computeIfAbsent(dragon.getUUID(), ignored -> new State());
    }

    public static final class State {
        public double distance;
        public double customDistance;
        public boolean insideCustomDistance;
        public boolean frustumVisible;
        public boolean rendererShouldRender;
        public AABB cullingBox;
        public long renderCalls;
        public long fallbackCalls;
        public long lastRenderedFrame = -1L;
        public long lastFallbackFrame = -1L;
    }
}
