package com.dracarys.dracarysmod.item;
import com.dracarys.dracarysmod.dragon.DragonStage;
import com.dracarys.dracarysmod.dragon.DragonVariant;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.dracarys.dracarysmod.registry.ModEntities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
public class DragonEggItem extends Item {
    private final DragonVariant variant;
    public DragonEggItem(DragonVariant variant, Properties properties){super(properties);this.variant=variant;}
    public DragonVariant variant(){return variant;}
    @Override public InteractionResult useOn(UseOnContext ctx){
        Level level=ctx.getLevel();
        if(!level.isClientSide && ctx.getPlayer() instanceof ServerPlayer player){
            DracarysDragonEntity d=ModEntities.DRAGON.get().create(level);
            if(d!=null){
                var pos=ctx.getClickedPos().relative(ctx.getClickedFace()).getCenter();
                d.moveTo(pos.x, pos.y, pos.z, player.getYRot(), 0.0F);
                d.initializeHatchling(variant, player);
                level.addFreshEntity(d);
                if(!player.getAbilities().instabuild) ctx.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
