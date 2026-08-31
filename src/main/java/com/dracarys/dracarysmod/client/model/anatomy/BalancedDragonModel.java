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
 * Anatomy 01 — BALANCED v2, Step 5.3 reference-fidelity reconstruction.
 *
 * <p>Four terrestrial legs + two independent wings. Geometry is deliberately
 * overlapped at every major joint so the dragon reads as one continuous animal
 * rather than disconnected boxes. The compatibility bones used by
 * {@link AbstractDracarysDragonModel} remain unchanged.</p>
 *
 * <p>The visual target is a low, predatory western dragon: long curved neck,
 * deep thorax, narrow waist, compact pelvis, powerful articulated legs, huge
 * bat-like wings and a long tapered tail.</p>
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

    /* --------------------------------------------------------------------- */
    /* TORSO — DEEP CHEST -> NARROW WAIST -> COMPACT PELVIS                 */
    /* --------------------------------------------------------------------- */

    private static PartDefinition createTorso(PartDefinition root) {
        /*
         * Minecraft humanoid-style models use Y≈24 as the ground plane.
         * Body sits lower than Step 5.2 to match the reference's predatory
         * posture while still letting the articulated feet reach Y≈24.
         */
        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -15.0F, 1.0F)
        );

        PartDefinition thorax = body.addOrReplaceChild(
                "thorax",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -0.5F, -6.0F, -0.045F, 0.0F, 0.0F)
        );

        thorax.addOrReplaceChild(
                "chest_front",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-10.8F, -6.8F, -7.0F, 21.6F, 12.0F, 13.5F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -3.5F, -0.025F, 0.0F, 0.0F)
        );
        thorax.addOrReplaceChild(
                "chest_mid",
                CubeListBuilder.create().texOffs(72, 0)
                        .addBox(-9.7F, -5.8F, -6.5F, 19.4F, 11.0F, 13.0F),
                PartPose.offsetAndRotation(0.0F, 1.1F, 5.0F, 0.025F, 0.0F, 0.0F)
        );
        thorax.addOrReplaceChild(
                "chest_keel",
                CubeListBuilder.create().texOffs(142, 0)
                        .addBox(-6.6F, -2.0F, -6.0F, 13.2F, 7.0F, 12.0F),
                PartPose.offsetAndRotation(0.0F, 4.1F, 0.8F, 0.10F, 0.0F, 0.0F)
        );
        thorax.addOrReplaceChild(
                "left_lats",
                CubeListBuilder.create().texOffs(0, 28)
                        .addBox(-1.0F, -4.0F, -5.0F, 5.5F, 8.2F, 10.0F),
                PartPose.offsetAndRotation(9.0F, 0.9F, 1.2F, 0.04F, -0.08F, -0.10F)
        );
        thorax.addOrReplaceChild(
                "right_lats",
                CubeListBuilder.create().texOffs(0, 28).mirror()
                        .addBox(-4.5F, -4.0F, -5.0F, 5.5F, 8.2F, 10.0F),
                PartPose.offsetAndRotation(-9.0F, 0.9F, 1.2F, 0.04F, 0.08F, 0.10F)
        );

        PartDefinition abdomen = body.addOrReplaceChild(
                "abdomen",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.0F, 5.0F, 0.055F, 0.0F, 0.0F)
        );
        abdomen.addOrReplaceChild(
                "waist_front",
                CubeListBuilder.create().texOffs(38, 28)
                        .addBox(-7.6F, -4.5F, -5.0F, 15.2F, 8.5F, 11.0F),
                PartPose.offsetAndRotation(0.0F, 0.1F, 1.0F, 0.02F, 0.0F, 0.0F)
        );
        abdomen.addOrReplaceChild(
                "waist_mid",
                CubeListBuilder.create().texOffs(90, 28)
                        .addBox(-6.8F, -4.0F, -4.5F, 13.6F, 7.6F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 0.7F, 9.2F, 0.015F, 0.0F, 0.0F)
        );
        abdomen.addOrReplaceChild(
                "belly",
                CubeListBuilder.create().texOffs(138, 28)
                        .addBox(-5.2F, -1.2F, -5.0F, 10.4F, 4.2F, 11.0F),
                PartPose.offsetAndRotation(0.0F, 3.5F, 5.0F, 0.10F, 0.0F, 0.0F)
        );

        PartDefinition pelvis = body.addOrReplaceChild(
                "pelvis",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.5F, 16.0F, -0.035F, 0.0F, 0.0F)
        );
        pelvis.addOrReplaceChild(
                "pelvis_bridge",
                CubeListBuilder.create().texOffs(174, 26)
                        .addBox(-7.2F, -4.5F, -6.0F, 14.4F, 8.6F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 0.2F, -2.0F, -0.01F, 0.0F, 0.0F)
        );
        pelvis.addOrReplaceChild(
                "pelvis_core",
                CubeListBuilder.create().texOffs(0, 49)
                        .addBox(-9.0F, -5.2F, -5.5F, 18.0F, 10.2F, 11.5F),
                PartPose.offsetAndRotation(0.0F, 0.3F, 5.0F, -0.02F, 0.0F, 0.0F)
        );
        pelvis.addOrReplaceChild(
                "pelvis_rear",
                CubeListBuilder.create().texOffs(64, 49)
                        .addBox(-7.7F, -4.4F, -4.0F, 15.4F, 8.5F, 9.0F),
                PartPose.offsetAndRotation(0.0F, 0.2F, 12.0F, 0.02F, 0.0F, 0.0F)
        );
        pelvis.addOrReplaceChild(
                "tail_root_mass",
                CubeListBuilder.create().texOffs(120, 49)
                        .addBox(-6.2F, -3.6F, -3.0F, 12.4F, 7.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 0.1F, 18.0F, 0.035F, 0.0F, 0.0F)
        );

        createShoulders(body);
        createDorsalRidge(body);
        return body;
    }

    private static void createShoulders(PartDefinition body) {
        /* Wing girdle: high and behind the foreleg shoulders. */
        body.addOrReplaceChild(
                "left_wing_scapula",
                CubeListBuilder.create().texOffs(164, 49)
                        .addBox(-2.5F, -2.7F, -5.5F, 6.5F, 6.0F, 11.0F),
                PartPose.offsetAndRotation(8.8F, -5.8F, -5.0F, -0.08F, -0.16F, -0.18F)
        );
        body.addOrReplaceChild(
                "right_wing_scapula",
                CubeListBuilder.create().texOffs(164, 49).mirror()
                        .addBox(-4.0F, -2.7F, -5.5F, 6.5F, 6.0F, 11.0F),
                PartPose.offsetAndRotation(-8.8F, -5.8F, -5.0F, -0.08F, 0.16F, 0.18F)
        );

        /* Foreleg shoulder masses: lower and slightly farther forward. */
        body.addOrReplaceChild(
                "left_front_shoulder_mass",
                CubeListBuilder.create().texOffs(204, 49)
                        .addBox(-2.7F, -3.4F, -4.2F, 6.7F, 8.0F, 8.8F),
                PartPose.offsetAndRotation(8.2F, 1.6F, -9.5F, 0.04F, -0.05F, -0.08F)
        );
        body.addOrReplaceChild(
                "right_front_shoulder_mass",
                CubeListBuilder.create().texOffs(204, 49).mirror()
                        .addBox(-4.0F, -3.4F, -4.2F, 6.7F, 8.0F, 8.8F),
                PartPose.offsetAndRotation(-8.2F, 1.6F, -9.5F, 0.04F, 0.05F, 0.08F)
        );
    }

    private static void createDorsalRidge(PartDefinition body) {
        PartDefinition ridge = body.addOrReplaceChild(
                "dorsal_ridge",
                CubeListBuilder.create(),
                PartPose.ZERO
        );
        addSpine(ridge, "back_spine_01", -7.3F, -11.0F, 7.2F, 0);
        addSpine(ridge, "back_spine_02", -7.0F, -2.5F, 6.8F, 12);
        addSpine(ridge, "back_spine_03", -6.0F, 6.5F, 6.0F, 24);
        addSpine(ridge, "back_spine_04", -5.4F, 15.0F, 5.1F, 36);
        addSpine(ridge, "back_spine_05", -4.8F, 23.0F, 4.0F, 48);
    }

    private static void addSpine(
            PartDefinition parent,
            String name,
            float y,
            float z,
            float height,
            int texY
    ) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(236, texY)
                        .addBox(-0.8F, -height, -1.3F, 1.6F, height, 2.6F),
                PartPose.offsetAndRotation(0.0F, y, z, -0.22F, 0.0F, 0.0F)
        );
    }

    /* --------------------------------------------------------------------- */
    /* NECK + HEAD — FIVE VISIBLE MASSES ON THREE COMPATIBILITY BONES       */
    /* --------------------------------------------------------------------- */

    private static void createNeckAndHead(PartDefinition body) {
        PartDefinition neck01 = body.addOrReplaceChild(
                "neck_01",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -3.0F, -14.0F)
        );
        addForwardSegment(neck01, "neck_mass_01", 6.2F, 5.2F, 10.5F, 0.0F, 0.0F, 0.0F, 0, 76);
        addForwardSegment(neck01, "neck_mass_02", 5.6F, 4.7F, 9.5F, 0.0F, -1.1F, -8.5F, 44, 76);
        addNeckSpines(neck01, 6.6F, -6.4F);

        PartDefinition neck02 = neck01.addOrReplaceChild(
                "neck_02",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -2.2F, -15.0F)
        );
        addForwardSegment(neck02, "neck_mass_03", 5.0F, 4.2F, 9.2F, 0.0F, 0.0F, 0.0F, 82, 76);
        addForwardSegment(neck02, "neck_mass_04", 4.4F, 3.7F, 8.5F, 0.0F, -0.7F, -7.4F, 120, 76);
        addNeckSpines(neck02, 5.4F, -5.7F);

        PartDefinition neck03 = neck02.addOrReplaceChild(
                "neck_03",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -1.4F, -13.2F)
        );
        addForwardSegment(neck03, "neck_mass_05", 3.8F, 3.2F, 10.5F, 0.0F, 0.0F, -0.2F, 154, 76);
        addNeckSpines(neck03, 4.2F, -4.8F);

        PartDefinition head = neck03.addOrReplaceChild(
                "head",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -0.1F, -9.3F)
        );
        createHeadMasses(head);
        createHeadDetails(head);
    }

    private static void addForwardSegment(
            PartDefinition parent,
            String name,
            float halfWidth,
            float halfHeight,
            float length,
            float x,
            float y,
            float z,
            int texX,
            int texY
    ) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(texX, texY)
                        .addBox(
                                -halfWidth,
                                -halfHeight,
                                -length + 1.2F,
                                halfWidth * 2.0F,
                                halfHeight * 2.0F,
                                length + 1.8F
                        ),
                PartPose.offsetAndRotation(x, y, z, -0.055F, 0.0F, 0.0F)
        );
    }

    private static void addNeckSpines(PartDefinition parent, float height, float z) {
        PartDefinition ridge = parent.addOrReplaceChild(
                "neck_spine_ridge",
                CubeListBuilder.create(),
                PartPose.ZERO
        );
        ridge.addOrReplaceChild(
                "spine_a",
                CubeListBuilder.create().texOffs(206, 76)
                        .addBox(-0.75F, -height, -1.2F, 1.5F, height, 2.4F),
                PartPose.offsetAndRotation(0.0F, -4.5F, z, -0.28F, 0.0F, 0.0F)
        );
        ridge.addOrReplaceChild(
                "spine_b",
                CubeListBuilder.create().texOffs(216, 76)
                        .addBox(-0.65F, -(height - 1.2F), -1.1F, 1.3F, height - 1.2F, 2.2F),
                PartPose.offsetAndRotation(0.0F, -4.0F, z - 5.8F, -0.24F, 0.0F, 0.0F)
        );
    }

    private static void createHeadMasses(PartDefinition head) {
        head.addOrReplaceChild(
                "back_skull",
                CubeListBuilder.create().texOffs(0, 100)
                        .addBox(-6.8F, -4.5F, -4.5F, 13.6F, 9.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, -0.3F, 1.0F, -0.05F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "main_cranium",
                CubeListBuilder.create().texOffs(48, 100)
                        .addBox(-5.8F, -3.8F, -5.5F, 11.6F, 7.6F, 9.5F),
                PartPose.offsetAndRotation(0.0F, -0.1F, -6.0F, 0.02F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "snout_base",
                CubeListBuilder.create().texOffs(92, 100)
                        .addBox(-4.8F, -3.0F, -5.0F, 9.6F, 5.8F, 8.5F),
                PartPose.offsetAndRotation(0.0F, 0.6F, -12.5F, 0.055F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "snout_mid",
                CubeListBuilder.create().texOffs(132, 100)
                        .addBox(-3.8F, -2.4F, -4.6F, 7.6F, 4.6F, 7.5F),
                PartPose.offsetAndRotation(0.0F, 0.9F, -18.2F, 0.04F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "nose",
                CubeListBuilder.create().texOffs(166, 100)
                        .addBox(-3.0F, -1.9F, -4.1F, 6.0F, 3.8F, 6.3F),
                PartPose.offsetAndRotation(0.0F, 1.2F, -23.5F, 0.03F, 0.0F, 0.0F)
        );
    }

    private static void createHeadDetails(PartDefinition head) {
        head.addOrReplaceChild(
                "left_cheek",
                CubeListBuilder.create().texOffs(0, 122)
                        .addBox(-1.0F, -2.4F, -4.0F, 3.6F, 5.0F, 7.8F),
                PartPose.offsetAndRotation(5.0F, 1.1F, -6.8F, 0.03F, -0.13F, -0.10F)
        );
        head.addOrReplaceChild(
                "right_cheek",
                CubeListBuilder.create().texOffs(0, 122).mirror()
                        .addBox(-2.6F, -2.4F, -4.0F, 3.6F, 5.0F, 7.8F),
                PartPose.offsetAndRotation(-5.0F, 1.1F, -6.8F, 0.03F, 0.13F, 0.10F)
        );
        head.addOrReplaceChild(
                "left_brow",
                CubeListBuilder.create().texOffs(26, 122)
                        .addBox(-2.7F, -0.8F, -2.8F, 5.4F, 1.6F, 5.6F),
                PartPose.offsetAndRotation(2.9F, -3.6F, -8.8F, -0.12F, -0.14F, -0.08F)
        );
        head.addOrReplaceChild(
                "right_brow",
                CubeListBuilder.create().texOffs(26, 122).mirror()
                        .addBox(-2.7F, -0.8F, -2.8F, 5.4F, 1.6F, 5.6F),
                PartPose.offsetAndRotation(-2.9F, -3.6F, -8.8F, -0.12F, 0.14F, 0.08F)
        );

        PartDefinition jaw = head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create().texOffs(56, 122)
                        .addBox(-4.5F, -0.4F, -14.5F, 9.0F, 3.0F, 15.7F),
                PartPose.offsetAndRotation(0.0F, 3.2F, -8.5F, 0.025F, 0.0F, 0.0F)
        );
        jaw.addOrReplaceChild(
                "jaw_tip",
                CubeListBuilder.create().texOffs(106, 122)
                        .addBox(-3.3F, -0.2F, -5.6F, 6.6F, 2.3F, 6.5F),
                PartPose.offsetAndRotation(0.0F, 0.3F, -13.2F, -0.04F, 0.0F, 0.0F)
        );
        jaw.addOrReplaceChild(
                "jaw_chin",
                CubeListBuilder.create().texOffs(138, 122)
                        .addBox(-2.9F, -0.4F, -4.5F, 5.8F, 1.8F, 5.5F),
                PartPose.offsetAndRotation(0.0F, 2.2F, -5.5F, 0.12F, 0.0F, 0.0F)
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
        addCranialSpine(crest, "crest_01", -4.0F, 1.5F, 6.3F);
        addCranialSpine(crest, "crest_02", -4.1F, -4.0F, 5.6F);
        addCranialSpine(crest, "crest_03", -3.8F, -9.5F, 4.6F);
    }

    private static void addCranialSpine(
            PartDefinition parent,
            String name,
            float y,
            float z,
            float height
    ) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(218, 122)
                        .addBox(-0.75F, -height, -1.3F, 1.5F, height, 2.6F),
                PartPose.offsetAndRotation(0.0F, y, z, -0.22F, 0.0F, 0.0F)
        );
    }

    private static void createHorn(PartDefinition head, boolean left, boolean primary) {
        float side = left ? 1.0F : -1.0F;
        String name = primary
                ? (left ? "left_horn" : "right_horn")
                : (left ? "left_horn_secondary" : "right_horn_secondary");

        float rootThickness = primary ? 2.8F : 2.0F;
        float rootLength = primary ? 7.5F : 5.0F;

        PartDefinition horn = head.addOrReplaceChild(
                name,
                builder(primary ? 180 : 198, 122, left)
                        .addBox(
                                -rootThickness * 0.5F,
                                -rootThickness * 0.5F,
                                -1.0F,
                                rootThickness,
                                rootThickness,
                                rootLength
                        ),
                PartPose.offsetAndRotation(
                        side * (primary ? 5.0F : 5.5F),
                        primary ? -3.8F : -2.8F,
                        primary ? 0.5F : -5.0F,
                        primary ? -0.46F : -0.30F,
                        side * (primary ? 0.32F : 0.45F),
                        side * -0.10F
                )
        );

        float tipThickness = rootThickness * 0.55F;
        horn.addOrReplaceChild(
                "tip",
                builder(primary ? 180 : 198, 136, left)
                        .addBox(
                                -tipThickness * 0.5F,
                                -tipThickness * 0.5F,
                                0.0F,
                                tipThickness,
                                tipThickness,
                                primary ? 8.0F : 5.5F
                        ),
                PartPose.offsetAndRotation(
                        0.0F,
                        0.0F,
                        rootLength - 1.0F,
                        -0.14F,
                        side * 0.14F,
                        side * -0.04F
                )
        );
    }

    /* --------------------------------------------------------------------- */
    /* WINGS — HUGE, SWEPT-BACK, BAT-LIKE, THREE DOMINANT DIGITS            */
    /* --------------------------------------------------------------------- */

    private static void createWing(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String rootName = left ? "left_wing_root" : "right_wing_root";

        PartDefinition wingRoot = body.addOrReplaceChild(
                rootName,
                CubeListBuilder.create(),
                PartPose.offset(side * 8.9F, -5.8F, -4.5F)
        );

        wingRoot.addOrReplaceChild(
                "wing_shoulder",
                builder(0, 146, left)
                        .addBox(-3.8F, -3.2F, -4.0F, 7.6F, 6.4F, 8.5F),
                PartPose.offsetAndRotation(side * 2.5F, 0.0F, 0.5F, -0.08F, side * -0.12F, side * -0.06F)
        );

        /*
         * Compatibility anchor. Fixed anatomical sweep is carried by its
         * children so animation can safely add motion to this bone.
         */
        PartDefinition upperArm = wingRoot.addOrReplaceChild(
                "upper_arm",
                CubeListBuilder.create(),
                PartPose.offset(side * 3.8F, -0.5F, 1.0F)
        );

        addWingBone(upperArm, "humerus_01", left,
                15.5F, 4.6F,
                0.0F, 0.0F, 0.0F,
                -0.04F, side * -0.16F, side * -0.03F,
                34, 146);
        addWingBone(upperArm, "humerus_02", left,
                13.5F, 4.0F,
                side * 13.2F, -0.6F, 4.0F,
                -0.02F, side * -0.20F, side * -0.03F,
                76, 146);

        upperArm.addOrReplaceChild(
                "elbow_joint",
                builder(112, 146, left)
                        .addBox(-3.0F, -2.8F, -2.8F, 6.0F, 5.6F, 5.6F),
                PartPose.offset(side * 25.0F, -1.2F, 8.2F)
        );

        PartDefinition forearm = upperArm.addOrReplaceChild(
                "forearm",
                CubeListBuilder.create(),
                PartPose.offset(side * 24.5F, -1.0F, 8.0F)
        );

        addWingBone(forearm, "radius_ulna_01", left,
                17.5F, 3.8F,
                0.0F, 0.0F, 0.0F,
                -0.02F, side * -0.16F, side * -0.02F,
                138, 146);
        addWingBone(forearm, "radius_ulna_02", left,
                15.0F, 3.2F,
                side * 15.0F, -0.4F, 4.5F,
                0.0F, side * -0.20F, side * -0.02F,
                180, 146);

        forearm.addOrReplaceChild(
                "wrist_joint",
                builder(220, 146, left)
                        .addBox(-2.6F, -2.5F, -2.5F, 5.2F, 5.0F, 5.0F),
                PartPose.offset(side * 28.5F, -0.8F, 9.0F)
        );

        PartDefinition hand = forearm.addOrReplaceChild(
                "hand",
                CubeListBuilder.create(),
                PartPose.offset(side * 28.0F, -0.7F, 8.8F)
        );

        addWingBone(hand, "metacarpal", left,
                13.0F, 2.8F,
                0.0F, 0.0F, 0.0F,
                0.0F, side * -0.16F, 0.0F,
                0, 166);

        createWingDigits(hand, left);
        createWingMembranes(wingRoot, upperArm, forearm, hand, left);

        hand.addOrReplaceChild(
                "wing_thumb",
                builder(42, 166, left)
                        .addBox(-0.8F, -0.8F, -0.8F, 1.6F, 1.6F, 6.2F),
                PartPose.offsetAndRotation(
                        side * 7.0F,
                        0.5F,
                        -0.5F,
                        -0.55F,
                        side * 0.18F,
                        side * -0.16F
                )
        );
    }

    private static void addWingBone(
            PartDefinition parent,
            String name,
            boolean left,
            float length,
            float thickness,
            float x,
            float y,
            float z,
            float xRot,
            float yRot,
            float zRot,
            int texX,
            int texY
    ) {
        parent.addOrReplaceChild(
                name,
                builder(texX, texY, left)
                        .addBox(
                                left ? -0.8F : -length + 0.8F,
                                -thickness * 0.5F,
                                -thickness * 0.5F,
                                length,
                                thickness,
                                thickness
                        ),
                PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot)
        );
    }

    private static void createWingDigits(PartDefinition hand, boolean left) {
        float side = left ? 1.0F : -1.0F;
        float[] lengths = {54.0F, 46.0F, 37.0F};
        float[] yaw = {-0.14F, -0.34F, -0.56F};
        float[] rootZ = {4.0F, 10.0F, 15.5F};
        float[] rootX = {9.0F, 8.0F, 7.0F};
        float[] thickness = {2.1F, 1.8F, 1.55F};

        for (int i = 0; i < 3; i++) {
            float len = lengths[i];
            float thick = thickness[i];
            PartDefinition digit = hand.addOrReplaceChild(
                    "finger_" + (i + 1),
                    builder(68 + i * 40, 166, left)
                            .addBox(
                                    left ? -0.6F : -len + 0.6F,
                                    -thick * 0.5F,
                                    -thick * 0.5F,
                                    len,
                                    thick,
                                    thick
                            ),
                    PartPose.offsetAndRotation(
                            side * rootX[i],
                            0.1F + i * 0.18F,
                            rootZ[i],
                            0.015F + i * 0.015F,
                            side * yaw[i],
                            side * (0.015F + i * 0.025F)
                    )
            );

            float tipLength = len * 0.22F;
            float tipThickness = Math.max(0.85F, thick * 0.55F);
            digit.addOrReplaceChild(
                    "tip",
                    builder(68 + i * 40, 178, left)
                            .addBox(
                                    left ? -0.4F : -tipLength + 0.4F,
                                    -tipThickness * 0.5F,
                                    -tipThickness * 0.5F,
                                    tipLength,
                                    tipThickness,
                                    tipThickness
                            ),
                    PartPose.offsetAndRotation(
                            side * (len * 0.82F),
                            0.0F,
                            0.0F,
                            0.0F,
                            side * (-0.04F - i * 0.02F),
                            side * (0.015F + i * 0.015F)
                    )
            );
        }
    }

    /**
     * Stepped strips approximate the reference's concave bat-wing web without
     * resorting to a single giant rectangular slab.
     */
    private static void createWingMembranes(
            PartDefinition wingRoot,
            PartDefinition upperArm,
            PartDefinition forearm,
            PartDefinition hand,
            boolean left
    ) {
        float side = left ? 1.0F : -1.0F;

        addMembraneFan(wingRoot, "body_web", left,
                16.0F, 15.0F, 9.0F,
                side * 1.0F, 1.5F, 1.5F,
                side * -0.16F, 0, 192);

        addMembraneFan(upperArm, "upper_web", left,
                24.0F, 20.0F, 12.0F,
                0.0F, 1.3F, 1.0F,
                side * -0.19F, 52, 192);

        addMembraneFan(forearm, "forearm_web", left,
                28.0F, 24.0F, 15.0F,
                0.0F, 1.0F, 1.0F,
                side * -0.20F, 112, 192);

        addMembraneFan(hand, "digit_web_01", left,
                45.0F, 24.0F, 13.0F,
                side * 7.0F, 0.8F, 3.5F,
                side * -0.16F, 0, 216);

        addMembraneFan(hand, "digit_web_02", left,
                39.0F, 20.0F, 10.0F,
                side * 7.0F, 0.8F, 9.0F,
                side * -0.35F, 64, 216);

        addMembraneFan(hand, "digit_web_03", left,
                31.0F, 16.0F, 7.0F,
                side * 6.5F, 0.8F, 14.5F,
                side * -0.56F, 124, 216);
    }

    private static void addMembraneFan(
            PartDefinition parent,
            String name,
            boolean left,
            float length,
            float rootDepth,
            float tipDepth,
            float x,
            float y,
            float z,
            float yaw,
            int texX,
            int texY
    ) {
        PartDefinition membrane = parent.addOrReplaceChild(
                name,
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(x, y, z, 0.02F, yaw, 0.0F)
        );

        final int steps = 6;
        float stripLength = length / steps;

        for (int i = 0; i < steps; i++) {
            float t = (i + 0.5F) / steps;
            float depth = rootDepth + (tipDepth - rootDepth) * t;
            float start = i * stripLength;

            membrane.addOrReplaceChild(
                    "panel_" + (i + 1),
                    builder(texX, texY, left)
                            .addBox(
                                    left ? start - 0.25F : -start - stripLength + 0.25F,
                                    0.0F,
                                    i * 0.55F,
                                    stripLength + 0.5F,
                                    0.34F,
                                    depth
                            ),
                    PartPose.ZERO
            );
        }
    }

    /* --------------------------------------------------------------------- */
    /* FRONT LEGS — SHOULDER -> ARM -> ELBOW -> FOREARM -> WRIST -> FOOT    */
    /* --------------------------------------------------------------------- */

    private static void createForeleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String name = left ? "left_foreleg" : "right_foreleg";

        PartDefinition leg = body.addOrReplaceChild(
                name,
                CubeListBuilder.create(),
                PartPose.offset(side * 7.8F, 2.6F, -9.2F)
        );

        leg.addOrReplaceChild(
                "shoulder",
                builder(0, 72, left)
                        .addBox(-4.3F, -4.0F, -4.3F, 8.6F, 8.5F, 8.6F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.04F, 0.0F, side * -0.04F)
        );
        leg.addOrReplaceChild(
                "upper_arm_mass",
                builder(38, 72, left)
                        .addBox(-3.2F, -1.0F, -3.0F, 6.4F, 13.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 3.4F, 0.4F, 0.10F, 0.0F, side * 0.02F)
        );
        leg.addOrReplaceChild(
                "elbow_mass",
                builder(66, 72, left)
                        .addBox(-3.0F, -2.6F, -2.8F, 6.0F, 5.2F, 5.6F),
                PartPose.offset(0.0F, 13.5F, 1.8F)
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 12.0F, 1.8F)
        );
        lowerLeg.addOrReplaceChild(
                "forearm_upper",
                builder(92, 72, left)
                        .addBox(-2.8F, -1.0F, -2.5F, 5.6F, 10.5F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.05F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "forearm_lower",
                builder(116, 72, left)
                        .addBox(-2.3F, -0.8F, -2.1F, 4.6F, 9.5F, 4.2F),
                PartPose.offsetAndRotation(0.0F, 8.5F, -0.6F, -0.11F, 0.0F, 0.0F)
        );

        PartDefinition wrist = lowerLeg.addOrReplaceChild(
                "wrist",
                builder(138, 72, left)
                        .addBox(-2.2F, -2.0F, -2.2F, 4.4F, 4.5F, 4.4F),
                PartPose.offsetAndRotation(0.0F, 10.8F, -1.8F, -0.22F, 0.0F, 0.0F)
        );

        createFoot(wrist, left, false, 13.5F);
    }

    /* --------------------------------------------------------------------- */
    /* HIND LEGS — LARGE THIGH, BENT KNEE, LONG TARSUS                      */
    /* --------------------------------------------------------------------- */

    private static void createHindleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String name = left ? "left_hindleg" : "right_hindleg";

        PartDefinition leg = body.addOrReplaceChild(
                name,
                CubeListBuilder.create(),
                PartPose.offset(side * 7.4F, 2.0F, 16.5F)
        );

        leg.addOrReplaceChild(
                "hip_mass",
                builder(0, 88, left)
                        .addBox(-5.4F, -4.5F, -5.2F, 10.8F, 9.8F, 10.4F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.03F, 0.0F, side * -0.03F)
        );
        leg.addOrReplaceChild(
                "thigh",
                builder(46, 88, left)
                        .addBox(-4.4F, -1.0F, -4.0F, 8.8F, 14.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 3.8F, 1.0F, -0.20F, 0.0F, side * 0.02F)
        );
        leg.addOrReplaceChild(
                "knee_mass",
                builder(84, 88, left)
                        .addBox(-3.7F, -3.0F, -3.4F, 7.4F, 6.0F, 6.8F),
                PartPose.offset(0.0F, 13.5F, 4.0F)
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 12.0F, 3.8F)
        );
        lowerLeg.addOrReplaceChild(
                "shin",
                builder(116, 88, left)
                        .addBox(-3.2F, -1.0F, -3.0F, 6.4F, 10.5F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.04F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "tarsus_upper",
                builder(144, 88, left)
                        .addBox(-2.6F, -0.8F, -2.4F, 5.2F, 9.2F, 4.8F),
                PartPose.offsetAndRotation(0.0F, 8.5F, -1.8F, -0.28F, 0.0F, 0.0F)
        );

        PartDefinition ankle = lowerLeg.addOrReplaceChild(
                "ankle",
                builder(168, 88, left)
                        .addBox(-2.4F, -2.0F, -2.4F, 4.8F, 4.7F, 4.8F),
                PartPose.offsetAndRotation(0.0F, 10.8F, -3.2F, -0.36F, 0.0F, 0.0F)
        );

        createFoot(ankle, left, true, 14.0F);
    }

    private static void createFoot(
            PartDefinition parent,
            boolean left,
            boolean hind,
            float drop
    ) {
        float width = hind ? 10.0F : 8.8F;
        float length = hind ? 12.0F : 10.5F;
        float metapodialWidth = hind ? 3.8F : 3.3F;

        /* Fills the old empty gap between wrist/ankle and the foot. */
        parent.addOrReplaceChild(
                "metapodial",
                builder(hind ? 190 : 0, 88, left)
                        .addBox(
                                -metapodialWidth * 0.5F,
                                0.0F,
                                -metapodialWidth * 0.45F,
                                metapodialWidth,
                                drop + 1.0F,
                                metapodialWidth * 0.9F
                        ),
                PartPose.offsetAndRotation(0.0F, 0.8F, -0.5F, hind ? -0.12F : -0.05F, 0.0F, 0.0F)
        );

        PartDefinition foot = parent.addOrReplaceChild(
                "foot",
                builder(hind ? 190 : 0, 102, left)
                        .addBox(-width * 0.5F, -1.5F, -length + 3.0F, width, 3.0F, length),
                PartPose.offsetAndRotation(
                        0.0F,
                        drop,
                        hind ? -3.0F : -2.2F,
                        hind ? 0.12F : 0.07F,
                        0.0F,
                        0.0F
                )
        );

        float spread = hind ? 3.0F : 2.6F;
        float toeLength = hind ? 8.0F : 7.0F;

        for (int i = -1; i <= 1; i++) {
            PartDefinition toe = foot.addOrReplaceChild(
                    "toe_" + (i + 2),
                    builder(36, 102, left)
                            .addBox(-0.95F, -0.75F, -toeLength, 1.9F, 1.5F, toeLength),
                    PartPose.offsetAndRotation(
                            i * spread,
                            0.25F,
                            -length + 4.0F,
                            -0.035F,
                            i * -0.09F,
                            0.0F
                    )
            );
            toe.addOrReplaceChild(
                    "claw",
                    builder(68, 102, left)
                            .addBox(-0.55F, -0.45F, -4.0F, 1.1F, 0.9F, 4.0F),
                    PartPose.offsetAndRotation(0.0F, 0.1F, -toeLength + 0.8F, -0.20F, 0.0F, 0.0F)
            );
        }

        foot.addOrReplaceChild(
                "outer_toe",
                builder(92, 102, left)
                        .addBox(-0.8F, -0.7F, -5.7F, 1.6F, 1.4F, 5.7F),
                PartPose.offsetAndRotation(
                        (left ? 1.0F : -1.0F) * width * 0.34F,
                        0.3F,
                        -length + 4.8F,
                        -0.03F,
                        (left ? 1.0F : -1.0F) * 0.18F,
                        0.0F
                )
        );
        foot.addOrReplaceChild(
                "heel_pad",
                builder(114, 102, left)
                        .addBox(-2.0F, -0.8F, -1.4F, 4.0F, 1.6F, 3.5F),
                PartPose.offset(0.0F, 0.25F, 0.8F)
        );
    }

    /* --------------------------------------------------------------------- */
    /* TAIL — EIGHT OVERLAPPING MASSES ON FOUR COMPATIBILITY BONES          */
    /* --------------------------------------------------------------------- */

    private static void createTail(PartDefinition body) {
        PartDefinition tail01 = body.addOrReplaceChild(
                "tail_01",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.8F, 27.0F, -0.045F, 0.0F, 0.0F)
        );
        addTailMass(tail01, "tail_mass_01", 6.3F, 4.8F, 15.0F, 0.0F, 0.0F, 0.0F, 0, 114);
        addTailMass(tail01, "tail_mass_02", 5.6F, 4.2F, 14.0F, 0.0F, 0.2F, 11.5F, 52, 114);
        addTailSpine(tail01, "spine_01", 5.1F, 6.0F);

        PartDefinition tail02 = tail01.addOrReplaceChild(
                "tail_02",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.3F, 24.0F, -0.020F, 0.0F, 0.0F)
        );
        addTailMass(tail02, "tail_mass_03", 4.9F, 3.7F, 13.0F, 0.0F, 0.0F, 0.0F, 102, 114);
        addTailMass(tail02, "tail_mass_04", 4.2F, 3.2F, 12.0F, 0.0F, 0.2F, 10.0F, 146, 114);
        addTailSpine(tail02, "spine_02", 4.2F, 5.5F);

        PartDefinition tail03 = tail02.addOrReplaceChild(
                "tail_03",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.4F, 21.5F, 0.020F, 0.0F, 0.0F)
        );
        addTailMass(tail03, "tail_mass_05", 3.5F, 2.7F, 12.0F, 0.0F, 0.0F, 0.0F, 186, 114);
        addTailMass(tail03, "tail_mass_06", 2.8F, 2.2F, 11.0F, 0.0F, 0.2F, 9.0F, 220, 114);
        addTailSpine(tail03, "spine_03", 3.3F, 5.0F);

        PartDefinition tail04 = tail03.addOrReplaceChild(
                "tail_04",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.3F, 19.0F, 0.040F, 0.0F, 0.0F)
        );
        addTailMass(tail04, "tail_mass_07", 2.2F, 1.8F, 11.0F, 0.0F, 0.0F, 0.0F, 0, 132);
        addTailMass(tail04, "tail_mass_08", 1.45F, 1.2F, 10.0F, 0.0F, 0.1F, 8.5F, 34, 132);

        tail04.addOrReplaceChild(
                "terminal_spike",
                CubeListBuilder.create().texOffs(66, 132)
                        .addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.1F, 17.0F, 0.05F, 0.0F, 0.0F)
        );
    }

    private static void addTailMass(
            PartDefinition parent,
            String name,
            float halfWidth,
            float halfHeight,
            float length,
            float x,
            float y,
            float z,
            int texX,
            int texY
    ) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(texX, texY)
                        .addBox(
                                -halfWidth,
                                -halfHeight,
                                -1.5F,
                                halfWidth * 2.0F,
                                halfHeight * 2.0F,
                                length + 2.5F
                        ),
                PartPose.offsetAndRotation(x, y, z, 0.018F, 0.0F, 0.0F)
        );
    }

    private static void addTailSpine(
            PartDefinition parent,
            String name,
            float height,
            float z
    ) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(96, 132)
                        .addBox(-0.7F, -height, -1.2F, 1.4F, height, 2.4F),
                PartPose.offsetAndRotation(0.0F, -3.5F, z, -0.18F, 0.0F, 0.0F)
        );
    }

    private static CubeListBuilder builder(int texX, int texY, boolean left) {
        CubeListBuilder result = CubeListBuilder.create().texOffs(texX, texY);
        if (!left) {
            result.mirror();
        }
        return result;
    }
}
