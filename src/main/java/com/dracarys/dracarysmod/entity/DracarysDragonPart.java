package com.dracarys.dracarysmod.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

/**
 * One physical interaction zone belonging to a Dracarys dragon.
 *
 * Parts are not saved, spawned or simulated independently. The parent dragon
 * owns their life cycle, position and damage handling. They are intentionally
 * non-colliding so giant wings/tails do not become invisible walls.
 */
public final class DracarysDragonPart extends PartEntity<DracarysDragonEntity> {
    private final String partName;
    private EntityDimensions dimensions = EntityDimensions.fixed(1.0F, 1.0F);

    public DracarysDragonPart(DracarysDragonEntity parent, String partName) {
        super(parent);
        this.partName = partName;
        this.noPhysics = true;
    }

    public String getPartName() {
        return partName;
    }

    @Override
    protected void defineSynchedData() {
        // Parts do not own networked data. The parent dragon is authoritative.
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // Parts are never persisted independently.
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // Parts are never persisted independently.
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        return getParent().interact(player, hand);
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || getParent() == entity;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        return getParent().hurtFromPart(this, source, amount);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return dimensions;
    }

    /**
     * Updates an oriented local box, converted to a world-axis AABB for vanilla
     * entity hit testing. The box remains non-colliding; it is an interaction
     * and damage target only.
     */
    public void updateBox(
            Vec3 center,
            double halfRight,
            double halfY,
            double halfForward,
            double rightX,
            double rightZ,
            double forwardX,
            double forwardZ
    ) {
        setOldPosAndRot();

        double halfX = Math.abs(rightX) * halfRight
                + Math.abs(forwardX) * halfForward;
        double halfZ = Math.abs(rightZ) * halfRight
                + Math.abs(forwardZ) * halfForward;

        double width = Math.max(0.25D, Math.max(halfX, halfZ) * 2.0D);
        double height = Math.max(0.25D, halfY * 2.0D);
        dimensions = EntityDimensions.fixed((float) width, (float) height);

        // Entity position is kept at the lower center, matching normal AABB semantics.
        setPos(center.x, center.y - halfY, center.z);
        setBoundingBox(new AABB(
                center.x - halfX,
                center.y - halfY,
                center.z - halfZ,
                center.x + halfX,
                center.y + halfY,
                center.z + halfZ
        ));

        setYRot(getParent().getYRot());
        setXRot(getParent().getXRot());
        tickCount = getParent().tickCount;
    }
}
