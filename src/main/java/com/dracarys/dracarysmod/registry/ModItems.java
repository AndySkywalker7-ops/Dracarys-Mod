package com.dracarys.dracarysmod.registry;
import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.dragon.DragonVariant;
import com.dracarys.dracarysmod.item.*;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.EnumMap;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS, DracarysMod.MOD_ID);
    public static final RegistryObject<Item> DRAGON_BONE=ITEMS.register("dragon_bone",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_FANG=ITEMS.register("dragon_fang",()->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> DRAGON_CLAW=ITEMS.register("dragon_claw",()->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> DRAGON_HEART=ITEMS.register("dragon_heart",()->new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(16)));
    public static final RegistryObject<Item> DRAGON_BLOOD=ITEMS.register("dragon_blood",()->new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(16)));
    public static final RegistryObject<Item> WING_MEMBRANE=ITEMS.register("wing_membrane",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_DRAGON_MEAT=ITEMS.register("raw_dragon_meat",()->new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.8f).meat().build())));
    public static final RegistryObject<Item> DRAGONBONE_SWORD=ITEMS.register("dragonbone_sword",()->new SwordItem(ModTiers.DRAGON_BONE,4,-2.4f,new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> DRAGONBONE_PICKAXE=ITEMS.register("dragonbone_pickaxe",()->new PickaxeItem(ModTiers.DRAGON_BONE,1,-2.8f,new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> DRAGONBONE_AXE=ITEMS.register("dragonbone_axe",()->new AxeItem(ModTiers.DRAGON_BONE,6.0f,-3.0f,new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> DRAGON_FANG_DAGGER=ITEMS.register("dragon_fang_dagger",()->new SwordItem(ModTiers.DRAGON_BONE,2,-1.6f,new Item.Properties().durability(1800).rarity(Rarity.RARE)));
    public static final EnumMap<DragonVariant,RegistryObject<Item>> SCALES=new EnumMap<>(DragonVariant.class);
    public static final EnumMap<DragonVariant,RegistryObject<Item>> EGGS=new EnumMap<>(DragonVariant.class);
    public static final EnumMap<DragonVariant,EnumMap<ArmorItem.Type,RegistryObject<Item>>> ARMOR=new EnumMap<>(DragonVariant.class);
    static {
        for(DragonVariant v:DragonVariant.values()){
            SCALES.put(v,ITEMS.register(v.id()+"_dragon_scale",()->new Item(new Item.Properties().rarity(Rarity.UNCOMMON))));
            EGGS.put(v,ITEMS.register(v.id()+"_dragon_egg",()->new DragonEggItem(v,new Item.Properties().stacksTo(1).rarity(Rarity.EPIC))));
            EnumMap<ArmorItem.Type,RegistryObject<Item>> set=new EnumMap<>(ArmorItem.Type.class);
            for(ArmorItem.Type t:ArmorItem.Type.values()){
                String suffix=switch(t){case HELMET->"helmet";case CHESTPLATE->"chestplate";case LEGGINGS->"leggings";case BOOTS->"boots";};
                set.put(t,ITEMS.register(v.id()+"_dragon_scale_"+suffix,()->new DragonScaleArmorItem(v,t,new Item.Properties().rarity(Rarity.RARE))));
            }
            ARMOR.put(v,set);
        }
    }
    private ModItems(){}
}
