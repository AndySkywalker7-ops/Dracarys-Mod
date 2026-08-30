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
 * Anatomy 01 — BALANCED v2.
 *
 * <p>A western quadrupedal dragon rebuilt around believable anatomical masses:
 * thorax, abdomen, pelvis, articulated neck, skull/snout/jaw, digitigrade limbs,
 * segmented tail and bat-inspired wings with visible arm/finger structure.</p>
 *
 * <p>The compatibility bones consumed by {@link AbstractDracarysDragonModel}
 * are intentionally preserved. New detail is added beneath those bones so the
 * current renderer, animation code, stage/size scaling and gameplay systems do
 * not need to change.</p>
 */
public final class BalancedDragonModel<T extends DracarysDragonEntity>
        extends AbstractDracarysDragonModel<T> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            DracarysMod.id("dracarys_dragon_balanced"),
            "main"
    );

    private static final int TEXTURE_SIZE = 256;

    public BalancedDragonModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = createTorso(root);
        createNeckAndHead(body);
        createWing(body, true);
        createWing(body, false);
        createForeleg(body, true);
        createForeleg(body, false);
        createHindleg(body, true);
        createHindleg(body, false);
        createTail(body);

        return LayerDefinition.create(mesh, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    /**
     * Builds the three major weight-bearing masses of the trunk:
     * broad thorax -> narrower abdomen -> broader pelvis.
     */
    private static PartDefinition createTorso(PartDefinition root) {
        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -18.0F, 1.0F)
        );

        body.addOrReplaceChild(
                "thorax",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-12.0F, -9.0F, -11.0F, 24.0F, 18.0F, 20.0F)
                        .texOffs(88, 0)
                        .addBox(-14.0F, -6.5F, -7.0F, 28.0F, 10.0F, 13.0F)
                        .texOffs(0, 40)
                        .addBox(-8.0F, 5.0F, -9.0F, 16.0F, 5.0F, 15.0F),
                PartPose.offset(0.0F, -1.0F, -10.0F)
        );

        body.addOrReplaceChild(
                "abdomen",
                CubeListBuilder.create()
                        .texOffs(64, 40)
                        .addBox(-8.5F, -6.0F, -9.0F, 17.0F, 12.0F, 20.0F)
                        .texOffs(64, 74)
                        .addBox(-7.0F, 4.0F, -6.0F, 14.0F, 4.0F, 16.0F),
                PartPose.offset(0.0F, 1.0F, 4.0F)
        );

        body.addOrReplaceChild(
                "pelvis",
                CubeListBuilder.create()
                        .texOffs(128, 40)
                        .addBox(-10.5F, -7.0F, -6.0F, 21.0F, 14.0F, 15.0F)
                        .texOffs(128, 72)
                        .addBox(-9.0F, -8.5F, -3.0F, 18.0F, 5.0F, 10.0F),
                PartPose.offset(0.0F, 1.0F, 16.0F)
        );

        createShoulderGirdle(body);
        createDorsalSpines(body);
        return body;
    }

    private static void createShoulderGirdle(PartDefinition body) {
        body.addOrReplaceChild(
                "left_scapula",
                CubeListBuilder.create()
                        .texOffs(196, 0)
                        .addBox(-2.0F, -3.0F, -7.0F, 7.0F, 7.0F, 14.0F),
                PartPose.offsetAndRotation(9.0F, -5.5F, -10.0F, 0.02F, -0.12F, -0.14F)
        );
        body.addOrReplaceChild(
                "right_scapula",
                CubeListBuilder.create()
                        .texOffs(196, 0)
                        .mirror()
                        .addBox(-5.0F, -3.0F, -7.0F, 7.0F, 7.0F, 14.0F),
                PartPose.offsetAndRotation(-9.0F, -5.5F, -10.0F, 0.02F, 0.12F, 0.14F)
        );
    }

    private static void createDorsalSpines(PartDefinition body) {
        PartDefinition ridge = body.addOrReplaceChild(
                "dorsal_ridge",
                CubeListBuilder.create(),
                PartPose.ZERO
        );

        ridge.addOrReplaceChild(
                "spine_01",
                CubeListBuilder.create().texOffs(232, 0)
                        .addBox(-1.0F, -7.0F, -2.0F, 2.0F, 7.0F, 4.0F),
                PartPose.offset(0.0F, -8.0F, -13.0F)
        );
        ridge.addOrReplaceChild(
                "spine_02",
                CubeListBuilder.create().texOffs(232, 12)
                        .addBox(-1.0F, -6.0F, -2.0F, 2.0F, 6.0F, 4.0F),
                PartPose.offset(0.0F, -7.0F, -3.0F)
        );
        ridge.addOrReplaceChild(
                "spine_03",
                CubeListBuilder.create().texOffs(232, 24)
                        .addBox(-1.0F, -5.0F, -2.0F, 2.0F, 5.0F, 4.0F),
                PartPose.offset(0.0F, -6.5F, 8.0F)
        );
        ridge.addOrReplaceChild(
                "spine_04",
                CubeListBuilder.create().texOffs(232, 36)
                        .addBox(-1.0F, -4.0F, -2.0F, 2.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, -6.0F, 18.0F)
        );
    }

    private static void createNeckAndHead(PartDefinition body) {
        PartDefinition neck01 = body.addOrReplaceChild(
                "neck_01",
                CubeListBuilder.create()
                        .texOffs(0, 96)
                        .addBox(-7.0F, -6.0F, -15.0F, 14.0F, 12.0F, 17.0F)
                        .texOffs(0, 126)
                        .addBox(-5.0F, 4.0F, -12.0F, 10.0F, 4.0F, 14.0F),
                PartPose.offsetAndRotation(0.0F, -4.0F, -20.0F, -0.10F, 0.0F, 0.0F)
        );
        addNeckSpines(neck01, 7.0F, "neck_01_spines");

        PartDefinition neck02 = neck01.addOrReplaceChild(
                "neck_02",
                CubeListBuilder.create()
                        .texOffs(64, 96)
                        .addBox(-6.0F, -5.0F, -14.0F, 12.0F, 10.0F, 16.0F)
                        .texOffs(64, 126)
                        .addBox(-4.5F, 3.5F, -11.5F, 9.0F, 3.5F, 13.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, -13.0F, -0.04F, 0.0F, 0.0F)
        );
        addNeckSpines(neck02, 6.0F, "neck_02_spines");

        PartDefinition neck03 = neck02.addOrReplaceChild(
                "neck_03",
                CubeListBuilder.create()
                        .texOffs(128, 96)
                        .addBox(-5.0F, -4.5F, -13.0F, 10.0F, 9.0F, 15.0F)
                        .texOffs(128, 126)
                        .addBox(-3.7F, 3.0F, -10.0F, 7.4F, 3.0F, 11.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, -12.0F, 0.02F, 0.0F, 0.0F)
        );
        addNeckSpines(neck03, 5.0F, "neck_03_spines");

        PartDefinition head = neck03.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 148)
                        .addBox(-7.5F, -5.5F, -14.0F, 15.0F, 11.0F, 14.0F)
                        .texOffs(64, 148)
                        .addBox(-8.5F, -5.0F, -5.0F, 17.0F, 10.0F, 7.0F)
                        .texOffs(0, 176)
                        .addBox(-5.5F, -3.5F, -25.0F, 11.0F, 7.0F, 12.0F)
                        .texOffs(48, 176)
                        .addBox(-4.5F, -2.8F, -30.0F, 9.0F, 5.6F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, -11.0F, 0.02F, 0.0F, 0.0F)
        );

        createHeadDetails(head);
    }

    private static void addNeckSpines(PartDefinition neck, float height, String name) {
        PartDefinition spines = neck.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.ZERO);
        spines.addOrReplaceChild(
                "spine_a",
                CubeListBuilder.create().texOffs(232, 48)
                        .addBox(-0.8F, -height, -1.5F, 1.6F, height, 3.0F),
                PartPose.offset(0.0F, -4.0F, -10.0F)
        );
        spines.addOrReplaceChild(
                "spine_b",
                CubeListBuilder.create().texOffs(240, 48)
                        .addBox(-0.8F, -(height - 1.0F), -1.5F, 1.6F, height - 1.0F, 3.0F),
                PartPose.offset(0.0F, -4.0F, -3.0F)
        );
    }

    private static void createHeadDetails(PartDefinition head) {
        head.addOrReplaceChild(
                "left_cheek",
                CubeListBuilder.create().texOffs(88, 176)
                        .addBox(-1.5F, -2.5F, -4.0F, 3.5F, 6.0F, 8.0F),
                PartPose.offset(6.0F, 1.0F, -9.0F)
        );
        head.addOrReplaceChild(
                "right_cheek",
                CubeListBuilder.create().texOffs(88, 176)
                        .mirror()
                        .addBox(-2.0F, -2.5F, -4.0F, 3.5F, 6.0F, 8.0F),
                PartPose.offset(-6.0F, 1.0F, -9.0F)
        );

        head.addOrReplaceChild(
                "left_brow",
                CubeListBuilder.create().texOffs(120, 176)
                        .addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(3.6F, -5.2F, -13.0F, -0.08F, -0.08F, -0.06F)
        );
        head.addOrReplaceChild(
                "right_brow",
                CubeListBuilder.create().texOffs(120, 176)
                        .mirror()
                        .addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(-3.6F, -5.2F, -13.0F, -0.08F, 0.08F, 0.06F)
        );

        PartDefinition jaw = head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create()
                        .texOffs(152, 176)
                        .addBox(-5.2F, 0.0F, -13.0F, 10.4F, 3.5F, 14.0F)
                        .texOffs(152, 196)
                        .addBox(-4.2F, 1.0F, -18.0F, 8.4F, 3.0F, 5.0F),
                PartPose.offset(0.0F, 3.8F, -12.0F)
        );
        jaw.addOrReplaceChild(
                "jaw_keel",
                CubeListBuilder.create().texOffs(196, 176)
                        .addBox(-3.5F, 0.0F, -6.0F, 7.0F, 2.0F, 7.0F),
                PartPose.offset(0.0F, 2.5F, -5.0F)
        );

        createHorn(head, true, true);
        createHorn(head, false, true);
        createHorn(head, true, false);
        createHorn(head, false, false);

        PartDefinition crest = head.addOrReplaceChild(
                "cranial_crest",
                CubeListBuilder.create(),
                PartPose.ZERO
        );
        crest.addOrReplaceChild(
                "crest_01",
                CubeListBuilder.create().texOffs(232, 64)
                        .addBox(-1.0F, -7.0F, -2.0F, 2.0F, 7.0F, 4.0F),
                PartPose.offset(0.0F, -5.0F, -8.0F)
        );
        crest.addOrReplaceChild(
                "crest_02",
                CubeListBuilder.create().texOffs(232, 76)
                        .addBox(-1.0F, -5.5F, -2.0F, 2.0F, 5.5F, 4.0F),
                PartPose.offset(0.0F, -5.0F, -1.0F)
        );
    }

    private static void createHorn(PartDefinition head, boolean left, boolean primary) {
        float side = left ? 1.0F : -1.0F;
        String name;
        float x;
        float y;
        float z;
        float length;
        float thickness;

        if (primary) {
            name = left ? "left_horn" : "right_horn";
            x = side * 5.5F;
            y = -5.2F;
            z = -3.5F;
            length = 15.0F;
            thickness = 2.8F;
        } else {
            name = left ? "left_horn_secondary" : "right_horn_secondary";
            x = side * 6.0F;
            y = -2.5F;
            z = -9.0F;
            length = 9.0F;
            thickness = 2.0F;
        }

        CubeListBuilder horn = CubeListBuilder.create().texOffs(primary ? 208 : 220, 176);
        if (!left) {
            horn.mirror();
        }

        head.addOrReplaceChild(
                name,
                horn.addBox(
                        -thickness * 0.5F,
                        -thickness * 0.5F,
                        -1.0F,
                        thickness,
                        thickness,
                        length
                ),
                PartPose.offsetAndRotation(
                        x,
                        y,
                        z,
                        primary ? -0.34F : -0.18F,
                        side * (primary ? 0.30F : 0.48F),
                        side * (primary ? -0.10F : -0.18F)
                )
        );
    }

    /**
     * Bat-inspired wing: shoulder socket -> upper arm -> forearm -> hand ->
     * four elongated digits. Membranes are separate thin sections following
     * the supporting bones instead of one rectangular plate.
     */
    private static void createWing(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String rootName = left ? "left_wing_root" : "right_wing_root";

        CubeListBuilder rootGeometry = builder(0, 208, left)
                .addBox(left ? 0.0F : -10.0F, -4.0F, -5.0F, 10.0F, 8.0F, 10.0F)
                .texOffs(40, 208)
                .addBox(left ? 1.0F : -13.0F, -2.5F, -3.5F, 12.0F, 5.0F, 7.0F);

        PartDefinition wingRoot = body.addOrReplaceChild(
                rootName,
                rootGeometry,
                PartPose.offsetAndRotation(
                        side * 10.5F,
                        -6.5F,
                        -9.0F,
                        -0.04F,
                        side * 0.10F,
                        side * -0.24F
                )
        );

        CubeListBuilder upperGeometry = builder(0, 226, left)
                .addBox(left ? 0.0F : -36.0F, -2.5F, -2.5F, 36.0F, 5.0F, 5.0F);

        PartDefinition upperArm = wingRoot.addOrReplaceChild(
                "upper_arm",
                upperGeometry,
                PartPose.offsetAndRotation(
                        side * 8.0F,
                        0.0F,
                        0.0F,
                        0.02F,
                        side * -0.08F,
                        side * -0.10F
                )
        );

        addMembraneSection(
                upperArm,
                "membrane_proximal",
                left,
                34.0F,
                17.0F,
                0,
                236
        );

        CubeListBuilder foreGeometry = builder(84, 208, left)
                .addBox(left ? 0.0F : -46.0F, -2.0F, -2.0F, 46.0F, 4.0F, 4.0F);

        PartDefinition forearm = upperArm.addOrReplaceChild(
                "forearm",
                foreGeometry,
                PartPose.offsetAndRotation(
                        side * 34.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        side * -0.10F,
                        side * -0.08F
                )
        );

        addMembraneSection(
                forearm,
                "membrane_middle",
                left,
                44.0F,
                23.0F,
                96,
                236
        );

        CubeListBuilder handGeometry = builder(156, 208, left)
                .addBox(left ? 0.0F : -36.0F, -1.6F, -1.6F, 36.0F, 3.2F, 3.2F);

        PartDefinition hand = forearm.addOrReplaceChild(
                "hand",
                handGeometry,
                PartPose.offsetAndRotation(
                        side * 44.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        side * -0.07F,
                        side * -0.04F
                )
        );

        addMembraneSection(
                hand,
                "membrane_distal",
                left,
                34.0F,
                18.0F,
                176,
                236
        );

        createWingFingers(hand, left);

        CubeListBuilder claw = builder(220, 208, left);
        hand.addOrReplaceChild(
                "wing_claw",
                claw.addBox(-1.2F, -1.2F, -1.0F, 2.4F, 2.4F, 9.0F),
                PartPose.offsetAndRotation(
                        side * 32.0F,
                        -0.5F,
                        -1.0F,
                        -0.38F,
                        side * 0.16F,
                        side * -0.20F
                )
        );
    }

    private static void addMembraneSection(
            PartDefinition parent,
            String name,
            boolean left,
            float length,
            float trailingDepth,
            int texX,
            int texY
    ) {
        parent.addOrReplaceChild(
                name,
                builder(texX, texY, left)
                        .addBox(
                                left ? 0.0F : -length,
                                0.0F,
                                -0.5F,
                                length,
                                0.8F,
                                trailingDepth
                        ),
                PartPose.ZERO
        );
    }

    private static void createWingFingers(PartDefinition hand, boolean left) {
        float side = left ? 1.0F : -1.0F;
        float[] lengths = {42.0F, 36.0F, 30.0F, 24.0F};
        float[] sweep = {0.08F, 0.17F, 0.27F, 0.38F};
        float[] membraneDepth = {16.0F, 14.0F, 11.0F, 8.0F};

        for (int i = 0; i < lengths.length; i++) {
            float length = lengths[i];
            CubeListBuilder fingerGeometry = builder(0, 244, left)
                    .addBox(
                            left ? 0.0F : -length,
                            -1.0F,
                            -1.0F,
                            length,
                            2.0F,
                            2.0F
                    )
                    .texOffs(96, 244)
                    .addBox(
                            left ? 0.0F : -length,
                            0.0F,
                            0.0F,
                            length,
                            0.7F,
                            membraneDepth[i]
                    );

            hand.addOrReplaceChild(
                    "finger_" + (i + 1),
                    fingerGeometry,
                    PartPose.offsetAndRotation(
                            side * 34.0F,
                            0.5F,
                            2.0F + i * 4.4F,
                            0.0F,
                            side * sweep[i],
                            side * (0.02F + i * 0.02F)
                    )
            );
        }
    }

    private static void createForeleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String name = left ? "left_foreleg" : "right_foreleg";

        CubeListBuilder upper = builder(0, 64, left)
                .addBox(-5.0F, -3.0F, -5.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(40, 64)
                .addBox(-4.0F, 4.0F, -3.5F, 8.0F, 15.0F, 7.0F);

        PartDefinition leg = body.addOrReplaceChild(
                name,
                upper,
                PartPose.offsetAndRotation(
                        side * 9.0F,
                        4.0F,
                        -11.0F,
                        -0.06F,
                        0.0F,
                        side * -0.08F
                )
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                builder(72, 64, left)
                        .addBox(-3.3F, 0.0F, -3.0F, 6.6F, 14.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 15.0F, 1.5F, 0.12F, 0.0F, 0.0F)
        );

        PartDefinition wrist = lowerLeg.addOrReplaceChild(
                "wrist",
                builder(100, 64, left)
                        .addBox(-2.6F, 0.0F, -2.5F, 5.2F, 8.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 12.0F, -1.0F, -0.38F, 0.0F, 0.0F)
        );

        createFoot(wrist, left, false);
    }

    private static void createHindleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String name = left ? "left_hindleg" : "right_hindleg";

        CubeListBuilder thigh = builder(128, 64, left)
                .addBox(-6.0F, -4.0F, -6.0F, 12.0F, 11.0F, 12.0F)
                .texOffs(176, 64)
                .addBox(-5.0F, 4.0F, -5.0F, 10.0F, 16.0F, 10.0F);

        PartDefinition leg = body.addOrReplaceChild(
                name,
                thigh,
                PartPose.offsetAndRotation(
                        side * 8.5F,
                        4.0F,
                        14.0F,
                        0.05F,
                        0.0F,
                        side * -0.05F
                )
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                builder(0, 82, left)
                        .addBox(-4.0F, 0.0F, -4.0F, 8.0F, 15.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 14.0F, 3.5F, 0.16F, 0.0F, 0.0F)
        );

        PartDefinition ankle = lowerLeg.addOrReplaceChild(
                "ankle",
                builder(40, 82, left)
                        .addBox(-3.0F, 0.0F, -3.0F, 6.0F, 10.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, -0.50F, 0.0F, 0.0F)
        );

        createFoot(ankle, left, true);
    }

    private static void createFoot(PartDefinition parent, boolean left, boolean hind) {
        float footWidth = hind ? 10.5F : 9.0F;
        float footLength = hind ? 13.0F : 11.0F;
        float y = hind ? 8.0F : 6.5F;
        float z = hind ? -4.0F : -3.0F;

        PartDefinition foot = parent.addOrReplaceChild(
                "foot",
                builder(hind ? 72 : 112, 82, left)
                        .addBox(
                                -footWidth * 0.5F,
                                -2.0F,
                                -footLength + 3.0F,
                                footWidth,
                                4.0F,
                                footLength
                        ),
                PartPose.offsetAndRotation(0.0F, y, z, hind ? 0.32F : 0.20F, 0.0F, 0.0F)
        );

        float toeSpread = hind ? 3.3F : 2.8F;
        float toeLength = hind ? 8.0F : 7.0F;
        for (int i = -1; i <= 1; i++) {
            float toeX = i * toeSpread;
            PartDefinition toe = foot.addOrReplaceChild(
                    "toe_" + (i + 2),
                    builder(hind ? 152 : 184, 82, left)
                            .addBox(-1.0F, -1.0F, -toeLength, 2.0F, 2.0F, toeLength),
                    PartPose.offsetAndRotation(
                            toeX,
                            0.0F,
                            -footLength + 4.0F,
                            0.0F,
                            i * -0.07F,
                            0.0F
                    )
            );

            toe.addOrReplaceChild(
                    "claw",
                    builder(216, 82, left)
                            .addBox(-0.7F, -0.7F, -4.5F, 1.4F, 1.4F, 4.5F),
                    PartPose.offsetAndRotation(0.0F, 0.0F, -toeLength + 1.0F, -0.18F, 0.0F, 0.0F)
            );
        }

        PartDefinition outerToe = foot.addOrReplaceChild(
                "outer_toe",
                builder(184, 92, left)
                        .addBox(-1.0F, -1.0F, -6.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(
                        (left ? 1.0F : -1.0F) * footWidth * 0.34F,
                        0.0F,
                        -footLength + 5.0F,
                        0.0F,
                        (left ? 1.0F : -1.0F) * 0.16F,
                        0.0F
                )
        );
        outerToe.addOrReplaceChild(
                "claw",
                builder(216, 92, left)
                        .addBox(-0.7F, -0.7F, -4.0F, 1.4F, 1.4F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, -0.18F, 0.0F, 0.0F)
        );
    }

    private static void createTail(PartDefinition body) {
        PartDefinition tail01 = body.addOrReplaceChild(
                "tail_01",
                CubeListBuilder.create()
                        .texOffs(0, 112)
                        .addBox(-7.0F, -6.0F, 0.0F, 14.0F, 12.0F, 23.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 21.0F, -0.02F, 0.0F, 0.0F)
        );
        addTailSpine(tail01, "spine", 6.0F, 8.0F);

        PartDefinition tail02 = tail01.addOrReplaceChild(
                "tail_02",
                CubeListBuilder.create()
                        .texOffs(64, 112)
                        .addBox(-5.6F, -5.0F, 0.0F, 11.2F, 10.0F, 23.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 21.0F, 0.03F, 0.0F, 0.0F)
        );
        addTailSpine(tail02, "spine", 5.0F, 9.0F);

        PartDefinition tail03 = tail02.addOrReplaceChild(
                "tail_03",
                CubeListBuilder.create()
                        .texOffs(128, 112)
                        .addBox(-4.2F, -4.0F, 0.0F, 8.4F, 8.0F, 24.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 21.0F, 0.03F, 0.0F, 0.0F)
        );

        PartDefinition tail04 = tail03.addOrReplaceChild(
                "tail_04",
                CubeListBuilder.create()
                        .texOffs(176, 112)
                        .addBox(-2.8F, -2.8F, 0.0F, 5.6F, 5.6F, 25.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 22.0F, 0.02F, 0.0F, 0.0F)
        );

        tail04.addOrReplaceChild(
                "tail_tip",
                CubeListBuilder.create()
                        .texOffs(216, 112)
                        .addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 18.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 23.0F, 0.01F, 0.0F, 0.0F)
        );
    }

    private static void addTailSpine(PartDefinition parent, String name, float height, float z) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(232, 96)
                        .addBox(-0.9F, -height, -2.0F, 1.8F, height, 4.0F),
                PartPose.offset(0.0F, -4.5F, z)
        );
    }

    private static CubeListBuilder builder(int texX, int texY, boolean left) {
        CubeListBuilder builder = CubeListBuilder.create().texOffs(texX, texY);
        if (!left) {
            builder.mirror();
        }
        return builder;
    }
}
