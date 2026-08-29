package com.dracarys.dracarysmod.item;
import com.dracarys.dracarysmod.registry.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
public final class ModTiers {
    public static final Tier DRAGON_BONE = new ForgeTier(4, 2400, 10.0f, 4.0f, 18, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ModItems.DRAGON_BONE.get()));
    private ModTiers(){}
}
