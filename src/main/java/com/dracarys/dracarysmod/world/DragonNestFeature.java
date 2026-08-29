package com.dracarys.dracarysmod.world;
import com.dracarys.dracarysmod.DracarysMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
public class DragonNestFeature extends Feature<NoneFeatureConfiguration> {
    public static final ResourceLocation LOOT=DracarysMod.id("chests/dragon_nest");
    public DragonNestFeature(Codec<NoneFeatureConfiguration> codec){super(codec);}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx){
        WorldGenLevel level=ctx.level(); BlockPos c=ctx.origin(); RandomSource r=ctx.random();
        for(int x=-5;x<=5;x++)for(int z=-5;z<=5;z++){
            double d=Math.sqrt(x*x+z*z); if(d>5.2||r.nextFloat()<0.18f)continue;
            BlockPos p=c.offset(x,-1,z); level.setBlock(p,(r.nextInt(8)==0?Blocks.OBSIDIAN:Blocks.BLACKSTONE).defaultBlockState(),3);
            if(d>3.5&&r.nextInt(5)==0) level.setBlock(p.above(),Blocks.BASALT.defaultBlockState(),3);
        }
        for(int i=0;i<3;i++){BlockPos g=c.offset(r.nextInt(7)-3,0,r.nextInt(7)-3);level.setBlock(g,Blocks.GOLD_BLOCK.defaultBlockState(),3);}
        level.setBlock(c,Blocks.CHEST.defaultBlockState(),3);
        if(level.getBlockEntity(c) instanceof ChestBlockEntity chest) chest.setLootTable(LOOT,r.nextLong());
        return true;
    }
}
