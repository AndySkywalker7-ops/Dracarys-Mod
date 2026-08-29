package com.dracarys.dracarysmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class DracarysConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue DOWNED_HEALTH;
    public static final ForgeConfigSpec.IntValue DOWNED_SECONDS;
    public static final ForgeConfigSpec.IntValue TAME_SMALL;
    public static final ForgeConfigSpec.IntValue TAME_MEDIUM;
    public static final ForgeConfigSpec.IntValue TAME_LARGE;
    public static final ForgeConfigSpec.IntValue TAME_GIANT;
    public static final ForgeConfigSpec.IntValue GROWTH_STAGE_TICKS;
    public static final ForgeConfigSpec.IntValue FEED_GROWTH_BONUS;
    public static final ForgeConfigSpec.BooleanValue FIRE_GRIEFING;
    public static final ForgeConfigSpec.DoubleValue FIRE_DAMAGE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue COLOSSAL_CHANCE;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("dragon");
        DOWNED_HEALTH = b.comment("Health at which an untamed dragon becomes downed.").defineInRange("downedHealth", 20.0, 1.0, 200.0);
        DOWNED_SECONDS = b.comment("Seconds a downed dragon remains unconscious.").defineInRange("downedSeconds", 60, 5, 600);
        TAME_SMALL = b.defineInRange("tamingMeatSmall", 16, 1, 512);
        TAME_MEDIUM = b.defineInRange("tamingMeatMedium", 24, 1, 512);
        TAME_LARGE = b.defineInRange("tamingMeatLarge", 40, 1, 512);
        TAME_GIANT = b.defineInRange("tamingMeatGiant", 64, 1, 512);
        GROWTH_STAGE_TICKS = b.comment("Base ticks per growth stage. 24000 ticks = one Minecraft day.").defineInRange("growthStageTicks", 72000, 1200, 2400000);
        FEED_GROWTH_BONUS = b.defineInRange("feedGrowthBonusTicks", 2400, 0, 72000);
        FIRE_GRIEFING = b.define("fireGriefing", true);
        FIRE_DAMAGE_MULTIPLIER = b.defineInRange("fireDamageMultiplier", 1.0, 0.0, 10.0);
        COLOSSAL_CHANCE = b.comment("Chance that a naturally spawned dragon has giant potential.").defineInRange("giantPotentialChance", 0.01, 0.0, 0.5);
        b.pop();
        SPEC = b.build();
    }
    private DracarysConfig() {}
}
