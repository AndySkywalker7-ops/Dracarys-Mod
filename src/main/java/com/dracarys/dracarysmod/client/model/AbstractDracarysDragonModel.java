package com.dracarys.dracarysmod.client.model;

import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * Shared articulated rig contract for Dracarys dragon anatomies.
 *
 * <p>The model owns visual animation only. Gameplay state, AI, combat,
 * genetics, growth, hitboxes and taming stay in DracarysDragonEntity.</p>
 */
public abstract class AbstractDracarysDragonModel<T extends DracarysDragonEntity> extends HierarchicalModel<T> {
    protected final ModelPart root;
    protected final ModelPart body;
    protected final ModelPart neck01;
    protected final ModelPart neck02;
    protected final ModelPart neck03;
    protected final ModelPart head;
    protected final ModelPart jaw;
    protected final ModelPart leftWingRoot;
    protected final ModelPart leftUpperArm;
    protected final ModelPart leftForearm;
    protected final ModelPart rightWingRoot;
    protected final ModelPart rightUpperArm;
    protected final ModelPart rightForearm;
    protected final ModelPart leftForeleg;
    protected final ModelPart leftForeLower;
    protected final ModelPart rightForeleg;
    protected final ModelPart rightForeLower;
    protected final ModelPart leftHindleg;
    protected final ModelPart leftHindLower;
    protected final ModelPart rightHindleg;
    protected final ModelPart rightHindLower;
    protected final ModelPart tail01;
    protected final ModelPart tail02;
    protected final ModelPart tail03;
    protected final ModelPart tail04;

    protected AbstractDracarysDragonModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");

        this.neck01 = body.getChild("neck_01");
        this.neck02 = neck01.getChild("neck_02");
        this.neck03 = neck02.getChild("neck_03");
        this.head = neck03.getChild("head");
        this.jaw = head.getChild("jaw");

        this.leftWingRoot = body.getChild("left_wing_root");
        this.leftUpperArm = leftWingRoot.getChild("upper_arm");
        this.leftForearm = leftUpperArm.getChild("forearm");
        this.rightWingRoot = body.getChild("right_wing_root");
        this.rightUpperArm = rightWingRoot.getChild("upper_arm");
        this.rightForearm = rightUpperArm.getChild("forearm");

        this.leftForeleg = body.getChild("left_foreleg");
        this.leftForeLower = leftForeleg.getChild("lower_leg");
        this.rightForeleg = body.getChild("right_foreleg");
        this.rightForeLower = rightForeleg.getChild("lower_leg");
        this.leftHindleg = body.getChild("left_hindleg");
        this.leftHindLower = leftHindleg.getChild("lower_leg");
        this.rightHindleg = body.getChild("right_hindleg");
        this.rightHindLower = rightHindleg.getChild("lower_leg");

        this.tail01 = body.getChild("tail_01");
        this.tail02 = tail01.getChild("tail_02");
        this.tail03 = tail02.getChild("tail_03");
        this.tail04 = tail03.getChild("tail_04");
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(T dragon, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetAnimatedPose();

        float yaw = Mth.clamp(netHeadYaw, -65.0F, 65.0F) * Mth.DEG_TO_RAD;
        float pitch = Mth.clamp(headPitch, -45.0F, 45.0F) * Mth.DEG_TO_RAD;

        neck01.yRot = yaw * 0.18F;
        neck02.yRot = yaw * 0.25F;
        neck03.yRot = yaw * 0.25F;
        head.yRot = yaw * 0.32F;
        neck02.xRot = pitch * 0.10F;
        neck03.xRot = pitch * 0.20F;
        head.xRot = pitch * 0.45F;

        float breathing = Mth.sin(ageInTicks * 0.08F) * 0.025F;
        body.xRot += breathing;
        neck01.xRot -= breathing * 0.65F;

        animateTail(ageInTicks);

        if (dragon.isDowned()) {
            animateDowned();
        } else if (dragon.isFlying()) {
            animateFlight(ageInTicks);
        } else {
            animateGround(limbSwing, limbSwingAmount, ageInTicks);
        }
    }

    protected void animateGround(float limbSwing, float limbSwingAmount, float ageInTicks) {
        float amount = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
        float fore = Mth.cos(limbSwing * 0.72F) * 0.60F * amount;
        float hind = Mth.cos(limbSwing * 0.72F + Mth.PI) * 0.68F * amount;

        leftForeleg.xRot += fore;
        rightForeleg.xRot -= fore;
        leftHindleg.xRot += hind;
        rightHindleg.xRot -= hind;

        leftForeLower.xRot += Math.max(0.0F, -fore) * 0.45F;
        rightForeLower.xRot += Math.max(0.0F, fore) * 0.45F;
        leftHindLower.xRot += Math.max(0.0F, -hind) * 0.55F;
        rightHindLower.xRot += Math.max(0.0F, hind) * 0.55F;

        float idleWing = 0.035F + Mth.sin(ageInTicks * 0.06F) * 0.018F;
        leftWingRoot.zRot -= idleWing;
        rightWingRoot.zRot += idleWing;
    }

    protected void animateFlight(float ageInTicks) {
        body.xRot -= 0.10F;
        neck01.xRot += 0.08F;
        neck02.xRot += 0.05F;

        float flap = Mth.sin(ageInTicks * 0.42F);
        float fold = Mth.cos(ageInTicks * 0.42F);

        // Flight opens the swept-back idle wings toward a broad lifting plane.
        leftWingRoot.yRot = -0.04F;
        rightWingRoot.yRot = 0.04F;
        leftUpperArm.yRot = -0.03F;
        rightUpperArm.yRot = 0.03F;
        leftForearm.yRot = -0.05F;
        rightForearm.yRot = 0.05F;

        leftWingRoot.zRot = -0.24F - flap * 0.52F;
        rightWingRoot.zRot = 0.24F + flap * 0.52F;
        leftUpperArm.zRot = -0.08F - flap * 0.18F;
        rightUpperArm.zRot = 0.08F + flap * 0.18F;
        leftForearm.zRot = -0.06F - fold * 0.10F;
        rightForearm.zRot = 0.06F + fold * 0.10F;

        leftForeleg.xRot = 0.55F;
        rightForeleg.xRot = 0.55F;
        leftHindleg.xRot = 0.72F;
        rightHindleg.xRot = 0.72F;
        leftForeLower.xRot = 0.55F;
        rightForeLower.xRot = 0.55F;
        leftHindLower.xRot = 0.60F;
        rightHindLower.xRot = 0.60F;
    }

    protected void animateTail(float ageInTicks) {
        tail01.yRot = Mth.sin(ageInTicks * 0.08F) * 0.08F;
        tail02.yRot = Mth.sin(ageInTicks * 0.08F - 0.55F) * 0.11F;
        tail03.yRot = Mth.sin(ageInTicks * 0.08F - 1.10F) * 0.14F;
        tail04.yRot = Mth.sin(ageInTicks * 0.08F - 1.65F) * 0.18F;
    }

    protected void animateDowned() {
        body.zRot = 1.20F;
        neck01.xRot = 0.32F;
        neck02.xRot = 0.28F;
        neck03.xRot = 0.18F;
        head.xRot = 0.22F;
        jaw.xRot = 0.08F;

        leftWingRoot.zRot = -0.72F;
        rightWingRoot.zRot = 0.25F;
        leftForeleg.xRot = 0.85F;
        rightForeleg.xRot = -0.25F;
        leftHindleg.xRot = 0.75F;
        rightHindleg.xRot = -0.15F;
    }

    private void resetAnimatedPose() {
        /*
         * Step 5.5 final-concept neutral pose.
         *
         * The geometry is intentionally muscular and broad.  The neutral rig
         * must preserve that anatomy instead of straightening it: low chest,
         * S-curved neck, four planted articulated legs, and enormous wings
         * held broadly open enough to show their real planform while still
         * swept slightly rearward from a full flight pose.
         */
        body.xRot = -0.075F;
        body.yRot = 0.0F;
        body.zRot = 0.0F;

        // Strong S-curve matching the reference's forward-reaching predatory neck.
        neck01.xRot = -0.34F;
        neck01.yRot = 0.0F;
        neck02.xRot = 0.15F;
        neck02.yRot = 0.0F;
        neck03.xRot = 0.14F;
        neck03.yRot = 0.0F;
        head.xRot = 0.035F;
        head.yRot = 0.0F;
        jaw.xRot = 0.0F;

        // Broad, long wings.  Only a modest rear sweep is used in idle so the
        // huge chord and finger fan remain visible instead of collapsing into
        // thin blades from above.
        leftWingRoot.xRot = -0.045F;
        leftWingRoot.yRot = -0.18F;
        leftWingRoot.zRot = -0.13F;
        leftUpperArm.xRot = -0.02F;
        leftUpperArm.yRot = -0.055F;
        leftUpperArm.zRot = -0.055F;
        leftForearm.xRot = 0.01F;
        leftForearm.yRot = -0.10F;
        leftForearm.zRot = -0.035F;

        rightWingRoot.xRot = -0.045F;
        rightWingRoot.yRot = 0.18F;
        rightWingRoot.zRot = 0.13F;
        rightUpperArm.xRot = -0.02F;
        rightUpperArm.yRot = 0.055F;
        rightUpperArm.zRot = 0.055F;
        rightForearm.xRot = 0.01F;
        rightForearm.yRot = 0.10F;
        rightForearm.zRot = 0.035F;

        // Front legs: elbows break backward while the distal forearm returns
        // toward the ground. This avoids the "four straight columns" silhouette.
        leftForeleg.xRot = 0.20F;
        rightForeleg.xRot = 0.20F;
        leftForeLower.xRot = -0.42F;
        rightForeLower.xRot = -0.42F;

        // Hind legs: powerful digitigrade Z profile.
        leftHindleg.xRot = 0.40F;
        rightHindleg.xRot = 0.40F;
        leftHindLower.xRot = -0.72F;
        rightHindLower.xRot = -0.72F;

        tail01.yRot = 0.0F;
        tail02.yRot = 0.0F;
        tail03.yRot = 0.0F;
        tail04.yRot = 0.0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
