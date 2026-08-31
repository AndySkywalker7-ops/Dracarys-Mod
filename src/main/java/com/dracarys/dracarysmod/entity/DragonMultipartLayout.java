package com.dracarys.dracarysmod.entity;

import net.minecraft.util.Mth;

/**
 * Step 5.3 — model-space multipart calibration for Anatomy 01 BALANCED.
 *
 * <p>The BALANCED reference-fidelity mesh is authored in Minecraft model pixels (16 px = 1 block)
 * and then multiplied by {@link DracarysDragonEntity#renderScale()} in the
 * renderer. The multipart hitboxes therefore use the same model-space landmarks
 * instead of the much smaller physical EntityDimensions.</p>
 *
 * <p>This class is intentionally data-driven so later anatomy revisions can
 * recalibrate the hitboxes without spreading magic numbers through the entity.</p>
 */
public final class DragonMultipartLayout {
    public static final double MODEL_PIXELS_PER_BLOCK = 16.0D;

    /**
     * Minecraft living models are effectively grounded around model Y=24.
     * BALANCED was authored around that convention.
     */
    public static final double MODEL_GROUND_Y_PX = 24.0D;

    /**
     * Maximum broad-phase radius required by the largest current BALANCED wings.
     * It is only raised as a dragon actually grows large enough to need it.
     */
    public static final double MAX_QUERY_RADIUS = 112.0D;

    // Static anatomical zones, measured directly against BalancedDragonModel.
    /*
     * Step 5.3 model-space landmarks. These are deliberately broad interaction
     * zones around the new continuous anatomy; the multipart architecture and
     * damage forwarding are unchanged.
     */
    public static final PartSpec BODY = new PartSpec(
            0.0D, -13.5D, 4.0D,
            12.5D, 11.5D, 28.0D
    );

    public static final PartSpec NECK = new PartSpec(
            0.0D, -19.0D, -37.0D,
            7.5D, 9.0D, 24.0D
    );

    public static final PartSpec HEAD = new PartSpec(
            0.0D, -21.0D, -68.0D,
            9.0D, 9.5D, 19.0D
    );

    public static final PartSpec TAIL = new PartSpec(
            0.0D, -11.5D, 69.0D,
            8.0D, 8.0D, 45.0D
    );

    public static final PartSpec LEGS = new PartSpec(
            0.0D, 4.0D, 4.0D,
            12.0D, 21.0D, 24.0D
    );

    /** Approximate center of each wing's full visible span in model pixels. */
    public static final double WING_CENTER_RIGHT_PX = 68.0D;
    public static final double WING_CENTER_Y_PX = -20.5D;
    public static final double WING_CENTER_Z_PX = 12.0D;
    public static final double WING_HALF_LENGTH_PX = 64.0D;
    public static final double WING_BASE_HALF_HEIGHT_PX = 8.0D;
    public static final double WING_HALF_FORWARD_PX = 36.0D;

    private DragonMultipartLayout() {}

    public static double worldPerModelPixel(DracarysDragonEntity dragon) {
        return dragon.renderScale() / MODEL_PIXELS_PER_BLOCK;
    }

    public static double worldYForModelY(DracarysDragonEntity dragon, double modelY) {
        return dragon.getY()
                + (MODEL_GROUND_Y_PX - modelY) * worldPerModelPixel(dragon);
    }

    public static double localRightWorld(DracarysDragonEntity dragon, double modelRightPx) {
        return modelRightPx * worldPerModelPixel(dragon);
    }

    /**
     * BALANCED's head is authored toward negative model Z, while our entity
     * local "forward" is positive. Therefore model Z is negated here.
     */
    public static double localForwardWorld(DracarysDragonEntity dragon, double modelZPx) {
        return -modelZPx * worldPerModelPixel(dragon);
    }

    public static double halfExtentWorld(DracarysDragonEntity dragon, double modelPixels) {
        return modelPixels * worldPerModelPixel(dragon);
    }

    /**
     * Mirrors AbstractDracarysDragonModel's visible wing-root animation closely
     * enough that the broad wing AABB rises/falls with the rendered wing instead
     * of remaining underneath it.
     */
    public static double leftWingRoll(DracarysDragonEntity dragon) {
        float age = dragon.tickCount;

        if (dragon.isDowned()) {
            return -0.72D;
        }

        if (dragon.isFlying()) {
            return -0.30D - Mth.sin(age * 0.42F) * 0.55D;
        }

        double idleWing = 0.035D + Mth.sin(age * 0.06F) * 0.018D;
        return -0.10D - idleWing;
    }

    public static double rightWingRoll(DracarysDragonEntity dragon) {
        if (dragon.isDowned()) {
            return 0.25D;
        }
        return -leftWingRoll(dragon);
    }

    /**
     * Radius needed for projectile/entity broad-phase searches to discover the
     * parent while testing a far wing/tail PartEntity.
     */
    public static double requiredQueryRadius(DracarysDragonEntity dragon) {
        double wingTipPx = WING_CENTER_RIGHT_PX + WING_HALF_LENGTH_PX + 8.0D;
        double tailTipPx = Math.abs(TAIL.modelZPx()) + TAIL.halfForwardPx();
        double maxPx = Math.max(wingTipPx, tailTipPx);
        return Mth.clamp(
                maxPx * worldPerModelPixel(dragon) + 4.0D,
                8.0D,
                MAX_QUERY_RADIUS
        );
    }

    public record PartSpec(
            double modelRightPx,
            double modelYPx,
            double modelZPx,
            double halfRightPx,
            double halfYPx,
            double halfForwardPx
    ) {}
}
