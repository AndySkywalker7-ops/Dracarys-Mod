package com.dracarys.dracarysmod.entity;

import com.dracarys.dracarysmod.config.DracarysConfig;
import com.dracarys.dracarysmod.dragon.*;
import com.dracarys.dracarysmod.registry.ModItems;
import com.dracarys.dracarysmod.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerLevelAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import java.util.HashSet;
import java.util.Set;

public class DracarysDragonEntity extends TamableAnimal implements FlyingAnimal {
    private static final EntityDataAccessor<Integer> VARIANT=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STAGE=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SIZE_TIER=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_STAGE=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DOWNED=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DOWNED_TICKS=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FED=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FLYING=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> GENE_SIZE=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GENE_STRENGTH=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GENE_VITALITY=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GENE_SPEED=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GENE_FIRE=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GENE_GROWTH=SynchedEntityData.defineId(DracarysDragonEntity.class,EntityDataSerializers.FLOAT);
    private int growthTicks=0;
    private int fireCooldown=0;

    public DracarysDragonEntity(EntityType<? extends TamableAnimal> type, Level level){super(type,level);xpReward=30;}

    public static AttributeSupplier.Builder createAttributes(){return TamableAnimal.createMobAttributes().add(Attributes.MAX_HEALTH,80).add(Attributes.ATTACK_DAMAGE,12).add(Attributes.ARMOR,8).add(Attributes.MOVEMENT_SPEED,0.24).add(Attributes.FOLLOW_RANGE,48).add(Attributes.KNOCKBACK_RESISTANCE,0.35);}

    @Override protected void defineSynchedData(){super.defineSynchedData();entityData.define(VARIANT,DragonVariant.RED.ordinal());entityData.define(STAGE,DragonStage.ADULT.ordinal());entityData.define(SIZE_TIER,DragonSizeTier.SMALL.ordinal());entityData.define(MAX_STAGE,DragonStage.ANCIENT.ordinal());entityData.define(DOWNED,false);entityData.define(DOWNED_TICKS,0);entityData.define(FED,0);entityData.define(FLYING,false);entityData.define(GENE_SIZE,1f);entityData.define(GENE_STRENGTH,1f);entityData.define(GENE_VITALITY,1f);entityData.define(GENE_SPEED,1f);entityData.define(GENE_FIRE,1f);entityData.define(GENE_GROWTH,1f);}

    @Override protected void registerGoals(){
        goalSelector.addGoal(0,new FloatGoal(this));
        goalSelector.addGoal(1,new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(2,new MeleeAttackGoal(this,1.25,true));
        goalSelector.addGoal(5,new WaterAvoidingRandomStrollGoal(this,0.85));
        goalSelector.addGoal(6,new LookAtPlayerGoal(this,Player.class,16));
        goalSelector.addGoal(7,new RandomLookAroundGoal(this));
        targetSelector.addGoal(1,new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2,new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3,new HurtByTargetGoal(this));
        targetSelector.addGoal(4,new NearestAttackableTargetGoal<>(this,Player.class,10,true,false,p->!isTame()&&!isDowned()));
    }

    public static boolean canSpawn(EntityType<DracarysDragonEntity> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random){BlockPos floor=pos.below();return pos.getY()>20&&level.getFluidState(pos).isEmpty()&&level.getBlockState(pos).isAir()&&level.getBlockState(pos.above()).isAir()&&level.getBlockState(floor).isSolidRender(level,floor);}

    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag){
        SpawnGroupData result=super.finalizeSpawn(level,difficulty,reason,data,tag); randomizeGenes(); setVariant(DragonVariant.values()[random.nextInt(DragonVariant.values().length)]);
        double g=DracarysConfig.COLOSSAL_CHANCE.get(); double roll=random.nextDouble(); DragonSizeTier tier=roll<g?DragonSizeTier.GIANT:roll<g+0.05?DragonSizeTier.LARGE:roll<g+0.20?DragonSizeTier.MEDIUM:DragonSizeTier.SMALL;
        setSizeTier(tier); setStage(tier==DragonSizeTier.GIANT&&random.nextFloat()<0.2f?DragonStage.COLOSSAL:random.nextFloat()<0.22f?DragonStage.ANCIENT:DragonStage.ADULT); setMaxStage(random.nextFloat()<0.12f?DragonStage.COLOSSAL:DragonStage.ANCIENT); applyScaledAttributes(true); return result;
    }

    public void initializeHatchling(DragonVariant v, ServerPlayer owner){randomizeGenes();setVariant(v);double r=random.nextDouble();setSizeTier(r<0.03?DragonSizeTier.GIANT:r<0.15?DragonSizeTier.LARGE:r<0.45?DragonSizeTier.MEDIUM:DragonSizeTier.SMALL);setStage(DragonStage.BABY);setMaxStage(random.nextFloat()<0.15f?DragonStage.COLOSSAL:random.nextFloat()<0.55f?DragonStage.ANCIENT:DragonStage.ADULT);tame(owner);setOrderedToSit(true);applyScaledAttributes(true);}
    public void configureForCommand(DragonVariant v, DragonSizeTier size, DragonStage stage){randomizeGenes();setVariant(v);setSizeTier(size);setStage(stage);setMaxStage(DragonStage.COLOSSAL);applyScaledAttributes(true);}
    private void randomizeGenes(){setGene(GENE_SIZE,.86f+random.nextFloat()*.28f);setGene(GENE_STRENGTH,.86f+random.nextFloat()*.28f);setGene(GENE_VITALITY,.86f+random.nextFloat()*.28f);setGene(GENE_SPEED,.86f+random.nextFloat()*.28f);setGene(GENE_FIRE,.86f+random.nextFloat()*.28f);setGene(GENE_GROWTH,.86f+random.nextFloat()*.28f);}
    private void setGene(EntityDataAccessor<Float> key,float v){entityData.set(key,v);}

    public DragonVariant getVariant(){return DragonVariant.byId(entityData.get(VARIANT));} public void setVariant(DragonVariant v){entityData.set(VARIANT,v.ordinal());}
    public DragonStage getStage(){return DragonStage.byId(entityData.get(STAGE));} public void setStage(DragonStage s){entityData.set(STAGE,s.ordinal());refreshDimensions();}
    public DragonSizeTier getSizeTier(){return DragonSizeTier.byId(entityData.get(SIZE_TIER));} public void setSizeTier(DragonSizeTier s){entityData.set(SIZE_TIER,s.ordinal());refreshDimensions();}
    public DragonStage getMaxStage(){return DragonStage.byId(entityData.get(MAX_STAGE));} public void setMaxStage(DragonStage s){entityData.set(MAX_STAGE,s.ordinal());}
    public boolean isDowned(){return entityData.get(DOWNED);} public void setFlying(boolean b){entityData.set(FLYING,b);setNoGravity(b);}
    public float conceptualLength(){DragonSizeTier t=getSizeTier();float base=(t.minLength+t.maxLength)*0.5f*entityData.get(GENE_SIZE);return base*getStage().growth();}
    public float renderScale(){return Mth.clamp(conceptualLength()/8.0f,0.35f,8.0f);}

    @Override public EntityDimensions getDimensions(Pose pose){float s=Mth.clamp(conceptualLength()/10.0f,0.55f,3.25f);return super.getDimensions(pose).scale(s);}

    private void applyScaledAttributes(boolean heal){float p=getSizeTier().power*getStage().growth();setBase(Attributes.MAX_HEALTH,Math.max(24,90*p*entityData.get(GENE_VITALITY)));setBase(Attributes.ATTACK_DAMAGE,Math.max(5,14*p*entityData.get(GENE_STRENGTH)));setBase(Attributes.ARMOR,Math.min(30,5+7*p));setBase(Attributes.MOVEMENT_SPEED,Mth.clamp(0.28f*entityData.get(GENE_SPEED)/(0.8f+0.18f*p),0.14f,0.34f));setBase(Attributes.KNOCKBACK_RESISTANCE,Mth.clamp(0.18f+0.16f*p,0,0.95f));if(heal)setHealth(getMaxHealth());}
    private void setBase(Attribute a,double v){AttributeInstance i=getAttribute(a);if(i!=null)i.setBaseValue(v);}

    @Override public void tick(){super.tick();if(level().isClientSide)return;if(fireCooldown>0)fireCooldown--;
        if(isDowned()){int t=entityData.get(DOWNED_TICKS)-1;entityData.set(DOWNED_TICKS,t);setDeltaMovement(getDeltaMovement().multiply(.2,1,.2));if(t<=0)wakeUp();return;}
        if(isTame()&&getStage().ordinal()<getMaxStage().ordinal()){growthTicks++;int need=(int)(DracarysConfig.GROWTH_STAGE_TICKS.get()/entityData.get(GENE_GROWTH));if(growthTicks>=need){growthTicks=0;setStage(getStage().next());applyScaledAttributes(true);}}
        LivingEntity target=getTarget();if(target!=null&&target.isAlive()){double d=distanceTo(target);if(getStage().ordinal()>=DragonStage.ADOLESCENT.ordinal()&&d>11)setFlying(true);if(isFlying())flyToward(target);if(d>6&&d<30&&fireCooldown<=0){breatheFire(target);fireCooldown=Math.max(30,90-getStage().ordinal()*8);}}
        else if(!isVehicle()&&isFlying()&&onGround())setFlying(false);
    }

    private void flyToward(LivingEntity target){Vec3 delta=target.getEyePosition().subtract(position().add(0,getBbHeight()*.5,0));if(delta.lengthSqr()>1){Vec3 n=delta.normalize();setDeltaMovement(getDeltaMovement().scale(.86).add(n.scale(.055+0.012*getStage().ordinal())));move(MoverType.SELF,getDeltaMovement());}}

    private void breatheFire(LivingEntity target){if(!(level() instanceof ServerLevel sl))return;Vec3 start=getEyePosition();Vec3 dir=target.getEyePosition().subtract(start).normalize();double range=Math.min(34,10+conceptualLength()*.35);double radius=Math.min(4.5,0.9+getSizeTier().ordinal()*.8);Set<Integer> hit=new HashSet<>();
        for(double i=1.5;i<=range;i+=1.4){Vec3 p=start.add(dir.scale(i));sl.sendParticles(ParticleTypes.FLAME,p.x,p.y,p.z,5,0.25,0.25,0.25,0.02);AABB box=new AABB(p.x-radius,p.y-radius,p.z-radius,p.x+radius,p.y+radius,p.z+radius);for(LivingEntity e:sl.getEntitiesOfClass(LivingEntity.class,box,e->e!=this&&e.isAlive()&&!isAlliedTo(e))){if(hit.add(e.getId())){float dmg=(float)((4+4*getSizeTier().power)*entityData.get(GENE_FIRE)*DracarysConfig.FIRE_DAMAGE_MULTIPLIER.get());e.hurt(damageSources().mobAttack(this),dmg);e.setSecondsOnFire(4+getSizeTier().ordinal()*2);}}
            if(DracarysConfig.FIRE_GRIEFING.get()&&random.nextFloat()<0.05f){BlockPos bp=BlockPos.containing(p);if(sl.getBlockState(bp).isAir()&&sl.getBlockState(bp.below()).isSolidRender(sl,bp.below()))sl.setBlockAndUpdate(bp,Blocks.FIRE.defaultBlockState());}}
        playSound(SoundEvents.ENDER_DRAGON_GROWL,1.8f,0.85f+random.nextFloat()*.2f);
    }

    @Override public boolean doHurtTarget(Entity target){boolean ok=super.doHurtTarget(target);if(ok&&getSizeTier().ordinal()>=DragonSizeTier.LARGE.ordinal()){double r=2.5+getSizeTier().ordinal();for(LivingEntity e:level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(r),e->e!=this&&e!=target&&!isAlliedTo(e))){e.hurt(damageSources().mobAttack(this),(float)getAttributeValue(Attributes.ATTACK_DAMAGE)*0.35f);Vec3 k=e.position().subtract(position()).normalize().scale(1.0+getSizeTier().ordinal()*.35);e.push(k.x,0.35,k.z);}}return ok;}

    @Override public boolean hurt(DamageSource src,float amount){if(!level().isClientSide&&!isTame()&&!isDowned()){float threshold=DracarysConfig.DOWNED_HEALTH.get().floatValue();if(getHealth()-amount<=threshold){float adjusted=Math.max(0.1f,getHealth()-threshold);boolean r=super.hurt(src,adjusted);if(isAlive())enterDowned();return r;}}return super.hurt(src,amount);}
    private void enterDowned(){entityData.set(DOWNED,true);entityData.set(DOWNED_TICKS,DracarysConfig.DOWNED_SECONDS.get()*20);entityData.set(FED,0);setNoAi(true);setFlying(false);setTarget(null);playSound(SoundEvents.ENDER_DRAGON_HURT,1.5f,0.7f);}
    private void wakeUp(){entityData.set(DOWNED,false);entityData.set(FED,0);setNoAi(false);setHealth(Math.max(DracarysConfig.DOWNED_HEALTH.get().floatValue()+8,getMaxHealth()*.25f));}
    private int meatRequired(){return switch(getSizeTier()){case SMALL->DracarysConfig.TAME_SMALL.get();case MEDIUM->DracarysConfig.TAME_MEDIUM.get();case LARGE->DracarysConfig.TAME_LARGE.get();case GIANT->DracarysConfig.TAME_GIANT.get();};}

    @Override public InteractionResult mobInteract(Player player, InteractionHand hand){ItemStack stack=player.getItemInHand(hand);if(stack.is(ModTags.Items.DRAGON_MEATS)){
        if(isDowned()&&!isTame()){int need=meatRequired();int remaining=Math.max(0,need-entityData.get(FED));int consume=Math.min(remaining,stack.getCount());if(consume>0){if(!player.getAbilities().instabuild)stack.shrink(consume);entityData.set(FED,entityData.get(FED)+consume);playSound(SoundEvents.GENERIC_EAT,1,0.8f);if(entityData.get(FED)>=need){tame(player);entityData.set(DOWNED,false);setNoAi(false);setOrderedToSit(true);setHealth(Math.max(30,getMaxHealth()*.35f));level().broadcastEntityEvent(this,(byte)7);}return InteractionResult.sidedSuccess(level().isClientSide);}}
        if(isTame()&&isOwnedBy(player)&&getHealth()<getMaxHealth()){if(!player.getAbilities().instabuild)stack.shrink(1);heal(8);growthTicks+=DracarysConfig.FEED_GROWTH_BONUS.get();return InteractionResult.sidedSuccess(level().isClientSide);}}
        if(isTame()&&isOwnedBy(player)){if(player.isShiftKeyDown()){setOrderedToSit(!isOrderedToSit());setFlying(false);return InteractionResult.sidedSuccess(level().isClientSide);}if(getStage().ordinal()>=DragonStage.ADOLESCENT.ordinal()&&!player.isPassenger()){player.startRiding(this);setOrderedToSit(false);return InteractionResult.sidedSuccess(level().isClientSide);}}
        return super.mobInteract(player,hand);
    }

    @Override public void travel(Vec3 input){if(isVehicle()&&getFirstPassenger() instanceof Player rider&&isTame()&&isOwnedBy(rider)&&!isDowned()){
        setYRot(rider.getYRot());yRotO=getYRot();setXRot(rider.getXRot()*.35f);yBodyRot=getYRot();yHeadRot=getYRot();float forward=rider.zza;float strafe=rider.xxa*.5f;if(rider.getXRot()<-18&&forward>0)setFlying(true);
        if(isFlying()){setNoGravity(true);Vec3 look=rider.getLookAngle();double speed=(0.22+0.035*getSizeTier().ordinal())*Math.max(0.15,Math.abs(forward));Vec3 desired=new Vec3(look.x,look.y*0.8,look.z).normalize().scale(speed*Math.signum(forward==0?1:forward));setDeltaMovement(getDeltaMovement().scale(.72).add(desired));move(MoverType.SELF,getDeltaMovement());if(onGround()&&rider.getXRot()>10)setFlying(false);}else{setSpeed((float)getAttributeValue(Attributes.MOVEMENT_SPEED)*1.4f);super.travel(new Vec3(strafe,input.y,forward));}return;}super.travel(input);}
    @Override protected boolean canAddPassenger(Entity passenger){return getPassengers().isEmpty()&&passenger instanceof Player&&isTame();}
    @Override public double getPassengersRidingOffset(){return getBbHeight()*.72;}
    @Override public boolean isFlying(){return entityData.get(FLYING);}

    @Override protected void dropCustomDeathLoot(DamageSource source,int looting,boolean recentlyHit){super.dropCustomDeathLoot(source,looting,recentlyHit);int mult=1+getSizeTier().ordinal()*2+getStage().ordinal();spawnAtLocation(new ItemStack(ModItems.SCALES.get(getVariant()).get(),Math.min(64,4+random.nextInt(6)+mult*3)));spawnAtLocation(new ItemStack(ModItems.DRAGON_BONE.get(),3+random.nextInt(5)+mult));spawnAtLocation(new ItemStack(ModItems.RAW_DRAGON_MEAT.get(),4+random.nextInt(7)+mult));if(random.nextFloat()<.65f)spawnAtLocation(ModItems.DRAGON_FANG.get());if(random.nextFloat()<.65f)spawnAtLocation(ModItems.DRAGON_CLAW.get());if(random.nextFloat()<.45f)spawnAtLocation(ModItems.WING_MEMBRANE.get());if(random.nextFloat()<.28f)spawnAtLocation(ModItems.DRAGON_BLOOD.get());if(getSizeTier().ordinal()>=2&&random.nextFloat()<.22f)spawnAtLocation(ModItems.DRAGON_HEART.get());if(getStage().ordinal()>=DragonStage.ANCIENT.ordinal()&&random.nextFloat()<.06f*(1+getSizeTier().ordinal()))spawnAtLocation(ModItems.EGGS.get(getVariant()).get());}

    @Override public boolean isFood(ItemStack stack){return stack.is(ModTags.Items.DRAGON_MEATS);}
    @Override public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other){return null;}

    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("Variant",entityData.get(VARIANT));tag.putInt("Stage",entityData.get(STAGE));tag.putInt("SizeTier",entityData.get(SIZE_TIER));tag.putInt("MaxStage",entityData.get(MAX_STAGE));tag.putBoolean("Downed",isDowned());tag.putInt("DownedTicks",entityData.get(DOWNED_TICKS));tag.putInt("Fed",entityData.get(FED));tag.putInt("GrowthTicks",growthTicks);tag.putFloat("GeneSize",entityData.get(GENE_SIZE));tag.putFloat("GeneStrength",entityData.get(GENE_STRENGTH));tag.putFloat("GeneVitality",entityData.get(GENE_VITALITY));tag.putFloat("GeneSpeed",entityData.get(GENE_SPEED));tag.putFloat("GeneFire",entityData.get(GENE_FIRE));tag.putFloat("GeneGrowth",entityData.get(GENE_GROWTH));}
    @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);entityData.set(VARIANT,tag.getInt("Variant"));entityData.set(STAGE,tag.getInt("Stage"));entityData.set(SIZE_TIER,tag.getInt("SizeTier"));entityData.set(MAX_STAGE,tag.getInt("MaxStage"));entityData.set(DOWNED,tag.getBoolean("Downed"));entityData.set(DOWNED_TICKS,tag.getInt("DownedTicks"));entityData.set(FED,tag.getInt("Fed"));growthTicks=tag.getInt("GrowthTicks");if(tag.contains("GeneSize")){entityData.set(GENE_SIZE,tag.getFloat("GeneSize"));entityData.set(GENE_STRENGTH,tag.getFloat("GeneStrength"));entityData.set(GENE_VITALITY,tag.getFloat("GeneVitality"));entityData.set(GENE_SPEED,tag.getFloat("GeneSpeed"));entityData.set(GENE_FIRE,tag.getFloat("GeneFire"));entityData.set(GENE_GROWTH,tag.getFloat("GeneGrowth"));}setNoAi(isDowned());applyScaledAttributes(false);refreshDimensions();}
}
