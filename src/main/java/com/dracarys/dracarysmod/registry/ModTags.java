package com.dracarys.dracarysmod.registry;
import com.dracarys.dracarysmod.DracarysMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
public final class ModTags {
    public static final class Items {
        public static final TagKey<Item> DRAGON_MEATS = TagKey.create(Registries.ITEM, DracarysMod.id("dragon_meats"));
        public static final TagKey<Item> DRAGON_SCALES = TagKey.create(Registries.ITEM, DracarysMod.id("dragon_scales"));
    }
    private ModTags(){}
}
