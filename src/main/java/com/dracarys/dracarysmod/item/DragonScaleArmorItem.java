package com.dracarys.dracarysmod.item;
import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.dragon.DragonVariant;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
public class DragonScaleArmorItem extends ArmorItem {
    private final DragonVariant variant;
    public DragonScaleArmorItem(DragonVariant variant, Type type, Properties properties){super(ModArmorMaterials.DRAGON_SCALE,type,properties);this.variant=variant;}
    public DragonVariant variant(){return variant;}
    @Override public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type){
        int layer = slot == EquipmentSlot.LEGS ? 2 : 1;
        return DracarysMod.MOD_ID+":textures/models/armor/"+variant.id()+"_dragon_scale_layer_"+layer+".png";
    }
}
