package com.dracarys.dracarysmod.client.lod;

import com.dracarys.dracarysmod.dragon.DragonStage;

/**
 * Centralized distance policy for dragon visual LOD.
 *
 * No gameplay behavior belongs here.
 */
public final class FarDragonLodProfile {

    public enum Level {
        FULL,
        FAR_3D,
        VERY_FAR_3D,
        NONE
    }

    private FarDragonLodProfile() {}

    /**
     * Full articulated model ends here. The manual full-model bridge still
     * overlaps from 40 blocks up to this distance.
     */
    public static double fullModelEnd(
            DragonStage stage
    ) {
        return switch (stage) {
            case BABY -> 90.0D;
            case JUVENILE -> 105.0D;
            case ADOLESCENT -> 115.0D;
            case ADULT -> 120.0D;
            case ANCIENT -> 135.0D;
            case COLOSSAL -> 150.0D;
        };
    }

    /**
     * Beyond this point the same 3D far model removes leg detail.
     */
    public static double farModelEnd(
            DragonStage stage
    ) {
        return switch (stage) {
            case BABY -> 180.0D;
            case JUVENILE -> 220.0D;
            case ADOLESCENT -> 260.0D;
            case ADULT -> 320.0D;
            case ANCIENT -> 420.0D;
            case COLOSSAL -> 560.0D;
        };
    }

    public static double maxDistance(
            DragonStage stage
    ) {
        return switch (stage) {
            case BABY -> 384.0D;
            case JUVENILE -> 800.0D;
            case ADOLESCENT -> 1000.0D;
            case ADULT -> 1400.0D;
            case ANCIENT -> 1800.0D;
            case COLOSSAL -> 2400.0D;
        };
    }

    public static Level levelFor(
            DragonStage stage,
            double distance
    ) {
        if (distance < fullModelEnd(stage)) {
            return Level.FULL;
        }

        if (distance < farModelEnd(stage)) {
            return Level.FAR_3D;
        }

        if (distance <= maxDistance(stage)) {
            return Level.VERY_FAR_3D;
        }

        return Level.NONE;
    }
}
