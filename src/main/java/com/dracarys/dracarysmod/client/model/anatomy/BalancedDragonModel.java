package com.dracarys.dracarysmod.client.model.anatomy;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.model.AbstractDracarysDragonModel;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Anatomy 01 — BALANCED.
 * Four legs, two long wings, articulated neck and segmented tail.
 * The intrinsic mesh is intentionally large; DracarysDragonRenderer applies
 * the existing renderScale() so visual length continues to track dragon size.
 */
public final class BalancedDragonModel<T extends DracarysDragonEntity> extends AbstractDracarysDragonModel<T> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(DracarysMod.id("dracarys_dragon_balanced"), "main");

    public BalancedDragonModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-10.0F, -8.0F, -20.0F, 20.0F, 16.0F, 42.0F)
                        .texOffs(0, 64).addBox(-8.0F, 6.0F, -16.0F, 16.0F, 5.0F, 31.0F),
                PartPose.offset(0.0F, -18.0F, 1.0F));

        body.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(128, 0).addBox(-12.0F, -10.0F, -12.0F, 24.0F, 20.0F, 22.0F),
                PartPose.offset(0.0F, -1.0F, -11.0F));

        body.addOrReplaceChild("belly",
                CubeListBuilder.create().texOffs(128, 64).addBox(-8.5F, -1.5F, -12.0F, 17.0F, 5.0F, 26.0F),
                PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition neck01 = body.addOrReplaceChild("neck_01",
                CubeListBuilder.create().texOffs(0, 104).addBox(-7.0F, -6.0F, -16.0F, 14.0F, 12.0F, 18.0F),
                PartPose.offsetAndRotation(0.0F, -2.0F, -19.0F, -0.10F, 0.0F, 0.0F));

        PartDefinition neck02 = neck01.addOrReplaceChild("neck_02",
                CubeListBuilder.create().texOffs(64, 104).addBox(-6.0F, -5.5F, -15.0F, 12.0F, 11.0F, 17.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, -14.0F, -0.04F, 0.0F, 0.0F));

        PartDefinition neck03 = neck02.addOrReplaceChild("neck_03",
                CubeListBuilder.create().texOffs(120, 104).addBox(-5.0F, -5.0F, -13.0F, 10.0F, 10.0F, 15.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, -13.0F, 0.02F, 0.0F, 0.0F));

        PartDefinition head = neck03.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(128, 0).addBox(-8.5F, -7.0F, -20.0F, 17.0F, 14.0F, 22.0F)
                        .texOffs(128, 40).addBox(-5.5F, -4.0F, -29.0F, 11.0F, 8.0F, 12.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -11.0F, 0.02F, 0.0F, 0.0F));

        head.addOrReplaceChild("jaw",
                CubeListBuilder.create().texOffs(128, 72).addBox(-5.5F, 0.0F, -12.0F, 11.0F, 3.5F, 13.0F),
                PartPose.offset(0.0F, 4.0F, -17.0F));

        head.addOrReplaceChild("left_horn",
                CubeListBuilder.create().texOffs(0, 194).addBox(-1.5F, -1.5F, -13.0F, 3.0F, 3.0F, 14.0F),
                PartPose.offsetAndRotation(5.5F, -5.5F, -4.0F, -0.30F, -0.32F, 0.12F));
        head.addOrReplaceChild("right_horn",
                CubeListBuilder.create().texOffs(0, 194).mirror().addBox(-1.5F, -1.5F, -13.0F, 3.0F, 3.0F, 14.0F),
                PartPose.offsetAndRotation(-5.5F, -5.5F, -4.0F, -0.30F, 0.32F, -0.12F));

        head.addOrReplaceChild("crest",
                CubeListBuilder.create()
                        .texOffs(48, 194).addBox(-1.0F, -8.0F, -3.0F, 2.0F, 9.0F, 5.0F)
                        .texOffs(64, 194).addBox(-1.0F, -7.0F, 3.0F, 2.0F, 8.0F, 5.0F),
                PartPose.offset(0.0F, -5.0F, -4.0F));

        addWing(body, true);
        addWing(body, false);
        addForeleg(body, true);
        addForeleg(body, false);
        addHindleg(body, true);
        addHindleg(body, false);
        addTail(body);

        return LayerDefinition.create(mesh, 256, 256);
    }

    private static void addWing(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String rootName = left ? "left_wing_root" : "right_wing_root";

        CubeListBuilder rootCube = CubeListBuilder.create().texOffs(0, 136);
        CubeListBuilder upperCube = CubeListBuilder.create().texOffs(0, 150);
        CubeListBuilder foreCube = CubeListBuilder.create().texOffs(0, 164);
        if (!left) {
            rootCube.mirror();
            upperCube.mirror();
            foreCube.mirror();
        }

        PartDefinition wingRoot = body.addOrReplaceChild(rootName,
                rootCube.addBox(left ? 0.0F : -24.0F, -3.0F, -4.0F, 24.0F, 6.0F, 8.0F),
                PartPose.offsetAndRotation(side * 9.0F, -6.0F, -10.0F, -0.04F, side * 0.10F, side * -0.24F));

        PartDefinition upperArm = wingRoot.addOrReplaceChild("upper_arm",
                upperCube.addBox(left ? 0.0F : -34.0F, -2.5F, -3.0F, 34.0F, 5.0F, 6.0F)
                        .texOffs(0, 224).addBox(left ? 0.0F : -34.0F, 0.0F, -2.0F, 34.0F, 1.0F, 20.0F),
                PartPose.offsetAndRotation(side * 22.0F, 0.0F, 0.0F, 0.02F, side * -0.08F, side * -0.10F));

        PartDefinition forearm = upperArm.addOrReplaceChild("forearm",
                foreCube.addBox(left ? 0.0F : -38.0F, -2.0F, -2.5F, 38.0F, 4.0F, 5.0F)
                        .texOffs(88, 224).addBox(left ? 0.0F : -38.0F, 0.0F, -1.0F, 38.0F, 1.0F, 24.0F),
                PartPose.offsetAndRotation(side * 32.0F, 0.0F, 0.0F, 0.0F, side * -0.10F, side * -0.08F));

        for (int i = 0; i < 4; i++) {
            float z = 1.5F + i * 5.5F;
            float length = 34.0F - i * 4.5F;
            CubeListBuilder finger = CubeListBuilder.create().texOffs(96, 150);
            if (!left) finger.mirror();
            forearm.addOrReplaceChild("finger_" + (i + 1),
                    finger.addBox(left ? 0.0F : -length, -1.0F, -1.0F, length, 2.0F, 2.0F)
                            .texOffs(0, 224).addBox(left ? 0.0F : -length, 0.0F, 0.0F, length, 1.0F, Math.max(4.0F, 18.0F - i * 3.5F)),
                    PartPose.offsetAndRotation(side * 35.0F, 0.5F, z, 0.0F, side * (0.06F + i * 0.035F), side * (0.02F + i * 0.025F)));
        }
    }

    private static void addForeleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        CubeListBuilder upper = CubeListBuilder.create().texOffs(128, 96);
        CubeListBuilder lower = CubeListBuilder.create().texOffs(164, 96);
        CubeListBuilder foot = CubeListBuilder.create().texOffs(198, 96);
        if (!left) { upper.mirror(); lower.mirror(); foot.mirror(); }

        PartDefinition leg = body.addOrReplaceChild(left ? "left_foreleg" : "right_foreleg",
                upper.addBox(-4.0F, -2.0F, -4.0F, 8.0F, 20.0F, 8.0F),
                PartPose.offsetAndRotation(side * 8.5F, 5.5F, -11.0F, -0.06F, 0.0F, side * -0.08F));
        PartDefinition lowerLeg = leg.addOrReplaceChild("lower_leg",
                lower.addBox(-3.5F, 0.0F, -3.5F, 7.0F, 17.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.12F, 0.0F, 0.0F));
        lowerLeg.addOrReplaceChild("foot",
                foot.addBox(-4.5F, -2.0F, -9.0F, 9.0F, 5.0F, 12.0F)
                        .texOffs(128, 194).addBox(-4.0F, 0.0F, -13.0F, 2.0F, 2.0F, 6.0F)
                        .texOffs(128, 194).addBox(-1.0F, 0.0F, -14.0F, 2.0F, 2.0F, 7.0F)
                        .texOffs(128, 194).addBox(2.0F, 0.0F, -13.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, 15.0F, -1.0F));
    }

    private static void addHindleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        CubeListBuilder upper = CubeListBuilder.create().texOffs(128, 120);
        CubeListBuilder lower = CubeListBuilder.create().texOffs(176, 120);
        CubeListBuilder foot = CubeListBuilder.create().texOffs(208, 120);
        if (!left) { upper.mirror(); lower.mirror(); foot.mirror(); }

        PartDefinition leg = body.addOrReplaceChild(left ? "left_hindleg" : "right_hindleg",
                upper.addBox(-5.0F, -4.0F, -5.0F, 10.0F, 23.0F, 10.0F),
                PartPose.offsetAndRotation(side * 8.0F, 5.0F, 13.0F, 0.05F, 0.0F, side * -0.05F));
        PartDefinition lowerLeg = leg.addOrReplaceChild("lower_leg",
                lower.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 19.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 17.0F, 1.0F, 0.16F, 0.0F, 0.0F));
        lowerLeg.addOrReplaceChild("foot",
                foot.addBox(-5.0F, -2.0F, -10.0F, 10.0F, 5.0F, 14.0F)
                        .texOffs(128, 194).addBox(-4.5F, 0.0F, -14.0F, 2.0F, 2.0F, 6.0F)
                        .texOffs(128, 194).addBox(-1.0F, 0.0F, -15.0F, 2.0F, 2.0F, 7.0F)
                        .texOffs(128, 194).addBox(2.5F, 0.0F, -14.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, 17.0F, -1.0F));
    }

    private static void addTail(PartDefinition body) {
        PartDefinition tail01 = body.addOrReplaceChild("tail_01",
                CubeListBuilder.create().texOffs(128, 148).addBox(-7.0F, -6.0F, 0.0F, 14.0F, 12.0F, 22.0F),
                PartPose.offset(0.0F, 0.5F, 19.0F));
        PartDefinition tail02 = tail01.addOrReplaceChild("tail_02",
                CubeListBuilder.create().texOffs(128, 148).addBox(-5.5F, -5.0F, 0.0F, 11.0F, 10.0F, 22.0F),
                PartPose.offset(0.0F, 0.0F, 19.0F));
        PartDefinition tail03 = tail02.addOrReplaceChild("tail_03",
                CubeListBuilder.create().texOffs(128, 148).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 23.0F),
                PartPose.offset(0.0F, 0.0F, 20.0F));
        PartDefinition tail04 = tail03.addOrReplaceChild("tail_04",
                CubeListBuilder.create().texOffs(128, 148).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 27.0F)
                        .texOffs(64, 194).addBox(-1.0F, -7.0F, 4.0F, 2.0F, 8.0F, 5.0F)
                        .texOffs(64, 194).addBox(-1.0F, -6.0F, 14.0F, 2.0F, 7.0F, 5.0F),
                PartPose.offset(0.0F, 0.0F, 20.0F));
        tail04.addOrReplaceChild("tail_tip",
                CubeListBuilder.create().texOffs(0, 194).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 16.0F),
                PartPose.offset(0.0F, 0.0F, 25.0F));
    }
}
