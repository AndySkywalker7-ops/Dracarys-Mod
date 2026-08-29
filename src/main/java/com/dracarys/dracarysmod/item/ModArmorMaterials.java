package com.dracarys.dracarysmod.item;
import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.registry.ModTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.EnumMap;
public enum ModArmorMaterials implements ArmorMaterial {
    DRAGON_SCALE("dragon_scale", 45, 24, 4.0f, 0.15f);
    private final String name; private final int durabilityMult,enchant; private final float toughness, knockback;
    private final EnumMap<ArmorItem.Type,Integer> defense = new EnumMap<>(ArmorItem.Type.class);
    private final LazyLoadedValue<Ingredient> repair = new LazyLoadedValue<>(() -> Ingredient.of(ModTags.Items.DRAGON_SCALES));
    ModArmorMaterials(String name,int d,int enchant,float toughness,float knockback){
        this.name=name;this.durabilityMult=d;this.enchant=enchant;this.toughness=toughness;this.knockback=knockback;
        defense.put(ArmorItem.Type.HELMET,4); defense.put(ArmorItem.Type.CHESTPLATE,9); defense.put(ArmorItem.Type.LEGGINGS,7); defense.put(ArmorItem.Type.BOOTS,4);
    }
    public int getDurabilityForType(ArmorItem.Type type){return switch(type){case HELMET->11;case CHESTPLATE->16;case LEGGINGS->15;case BOOTS->13;}*durabilityMult;}
    public int getDefenseForType(ArmorItem.Type type){return defense.getOrDefault(type,0);}
    public int getEnchantmentValue(){return enchant;}
    public SoundEvent getEquipSound(){return SoundEvents.ARMOR_EQUIP_NETHERITE;}
    public Ingredient getRepairIngredient(){return repair.get();}
    public String getName(){return DracarysMod.MOD_ID+":"+name;}
    public float getToughness(){return toughness;}
    public float getKnockbackResistance(){return knockback;}
}
