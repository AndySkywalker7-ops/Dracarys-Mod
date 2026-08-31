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
 * Anatomy 01 — BALANCED v2, Step 5.4 high-fidelity anatomical rebuild.
 *
 * <p>This revision deliberately replaces the box-chain silhouette from the
 * previous passes. The dragon remains a six-limbed western dragon: four
 * terrestrial legs plus two fully independent dorsal wings. Every major
 * anatomical transition overlaps its neighbour so the body reads as one
 * continuous animal instead of a collection of disconnected cuboids.</p>
 *
 * <p>The compatibility bones consumed by {@link AbstractDracarysDragonModel}
 * are preserved exactly: body, neck_01..03, head, jaw, both wing roots with
 * upper_arm/forearm, four terrestrial leg roots with lower_leg, and tail_01..04.</p>
 */
public final class BalancedDragonModel<T extends DracarysDragonEntity>
        extends AbstractDracarysDragonModel<T> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            DracarysMod.id("dracarys_dragon_balanced"),
            "main"
    );

    private static final int TEXTURE_SIZE = 256;
    private static final float GROUND_Y = 24.0F;

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
        createDorsalCrest(body);

        return LayerDefinition.create(mesh, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    /* ===================================================================== */
    /* TORSO — rib cage -> waist -> pelvis, with deliberate volume overlap   */
    /* ===================================================================== */

    private static PartDefinition createTorso(PartDefinition root) {
        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -11.0F, 2.0F)
        );

        PartDefinition thorax = body.addOrReplaceChild(
                "thorax",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -0.6F, -6.2F, -0.045F, 0.0F, 0.0F)
        );

        // Forward rib cage: deepest and widest mass.
        thorax.addOrReplaceChild(
                "ribcage_front",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-11.8F, -6.9F, -7.8F, 23.6F, 13.8F, 14.8F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -3.6F, -0.055F, 0.0F, 0.0F)
        );
        thorax.addOrReplaceChild(
                "ribcage_mid",
                CubeListBuilder.create().texOffs(78, 0)
                        .addBox(-10.7F, -6.0F, -6.8F, 21.4F, 12.1F, 13.8F),
                PartPose.offsetAndRotation(0.0F, 0.8F, 5.6F, 0.025F, 0.0F, 0.0F)
        );
        thorax.addOrReplaceChild(
                "sternum",
                CubeListBuilder.create().texOffs(150, 0)
                        .addBox(-7.0F, -1.4F, -7.0F, 14.0F, 7.0F, 13.8F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.10F, 0.0F, 0.0F)
        );
        thorax.addOrReplaceChild(
                "shoulder_arch",
                CubeListBuilder.create().texOffs(0, 30)
                        .addBox(-12.6F, -4.5F, -5.0F, 25.2F, 8.2F, 10.2F),
                PartPose.offsetAndRotation(0.0F, -1.6F, -4.0F, -0.035F, 0.0F, 0.0F)
        );

        // Waist overlaps the rear rib cage by ~15%, preventing an air gap.
        PartDefinition abdomen = body.addOrReplaceChild(
                "abdomen",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.9F, 6.6F, 0.045F, 0.0F, 0.0F)
        );
        abdomen.addOrReplaceChild(
                "waist_front",
                CubeListBuilder.create().texOffs(70, 30)
                        .addBox(-8.3F, -4.7F, -5.8F, 16.6F, 9.4F, 11.8F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.015F, 0.0F, 0.0F)
        );
        abdomen.addOrReplaceChild(
                "waist_core",
                CubeListBuilder.create().texOffs(126, 30)
                        .addBox(-7.2F, -4.0F, -5.2F, 14.4F, 8.1F, 10.7F),
                PartPose.offsetAndRotation(0.0F, 0.5F, 8.4F, 0.025F, 0.0F, 0.0F)
        );
        abdomen.addOrReplaceChild(
                "ventral_abdomen",
                CubeListBuilder.create().texOffs(174, 30)
                        .addBox(-5.8F, -1.4F, -5.2F, 11.6F, 4.8F, 11.0F),
                PartPose.offsetAndRotation(0.0F, 3.3F, 4.5F, 0.085F, 0.0F, 0.0F)
        );

        // Pelvis regains width and overlaps the waist by several model pixels.
        PartDefinition pelvis = body.addOrReplaceChild(
                "pelvis",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.5F, 17.4F, -0.045F, 0.0F, 0.0F)
        );
        pelvis.addOrReplaceChild(
                "pelvis_front",
                CubeListBuilder.create().texOffs(0, 52)
                        .addBox(-8.4F, -4.7F, -6.2F, 16.8F, 9.4F, 11.4F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -2.2F, -0.02F, 0.0F, 0.0F)
        );
        pelvis.addOrReplaceChild(
                "pelvis_core",
                CubeListBuilder.create().texOffs(58, 52)
                        .addBox(-9.6F, -5.4F, -5.8F, 19.2F, 10.8F, 12.0F),
                PartPose.offsetAndRotation(0.0F, 0.2F, 5.3F, 0.0F, 0.0F, 0.0F)
        );
        pelvis.addOrReplaceChild(
                "pelvis_rear",
                CubeListBuilder.create().texOffs(126, 52)
                        .addBox(-8.1F, -4.5F, -5.0F, 16.2F, 9.0F, 10.8F),
                PartPose.offsetAndRotation(0.0F, 0.3F, 12.6F, 0.03F, 0.0F, 0.0F)
        );
        pelvis.addOrReplaceChild(
                "sacrum",
                CubeListBuilder.create().texOffs(184, 52)
                        .addBox(-6.8F, -3.8F, -4.5F, 13.6F, 7.6F, 9.8F),
                PartPose.offsetAndRotation(0.0F, 0.2F, 19.0F, 0.04F, 0.0F, 0.0F)
        );

        // Scapular and hip transition masses fill the four limb insertions.
        addLimbGirdleMasses(body);
        return body;
    }

    private static void addLimbGirdleMasses(PartDefinition body) {
        for (boolean left : new boolean[]{true, false}) {
            float side = left ? 1.0F : -1.0F;
            String prefix = left ? "left" : "right";

            body.addOrReplaceChild(
                    prefix + "_wing_scapula",
                    builder(0, 76, left)
                            .addBox(-3.4F, -3.0F, -6.0F, 7.0F, 6.2F, 12.2F),
                    PartPose.offsetAndRotation(
                            side * 9.7F, -5.2F, -5.2F,
                            -0.10F, side * -0.12F, side * -0.16F
                    )
            );
            body.addOrReplaceChild(
                    prefix + "_front_shoulder_mass",
                    builder(32, 76, left)
                            .addBox(-3.6F, -3.7F, -4.7F, 7.4F, 8.6F, 9.8F),
                    PartPose.offsetAndRotation(
                            side * 8.7F, 1.5F, -9.0F,
                            0.04F, side * -0.06F, side * -0.06F
                    )
            );
            body.addOrReplaceChild(
                    prefix + "_hip_mass",
                    builder(66, 76, left)
                            .addBox(-4.6F, -4.4F, -5.2F, 9.4F, 9.2F, 10.5F),
                    PartPose.offsetAndRotation(
                            side * 8.0F, 2.0F, 19.5F,
                            -0.04F, side * 0.06F, side * 0.04F
                    )
            );
        }
    }

    /* ===================================================================== */
    /* NECK + HEAD — 7 visual masses over three compatibility neck bones     */
    /* ===================================================================== */

    private static void createNeckAndHead(PartDefinition body) {
        PartDefinition neck01 = body.addOrReplaceChild(
                "neck_01",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -2.6F, -14.0F)
        );
        addNeckMass(neck01, "neck_mass_01", 6.8F, 5.8F, 9.4F, 0.0F, 0.0F, 0.0F, -0.08F, 0, 90);
        addNeckMass(neck01, "neck_mass_02", 6.2F, 5.2F, 9.0F, 0.0F, -1.5F, -7.5F, -0.11F, 42, 90);
        addNeckSpines(neck01, 6.8F, -3.5F, -9.0F);

        PartDefinition neck02 = neck01.addOrReplaceChild(
                "neck_02",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -3.2F, -14.2F)
        );
        addNeckMass(neck02, "neck_mass_03", 5.5F, 4.6F, 8.8F, 0.0F, 0.0F, 0.0F, -0.06F, 82, 90);
        addNeckMass(neck02, "neck_mass_04", 5.0F, 4.1F, 8.3F, 0.0F, -1.0F, -7.0F, 0.02F, 120, 90);
        addNeckMass(neck02, "neck_mass_05", 4.5F, 3.7F, 7.8F, 0.0F, -1.2F, -13.2F, 0.06F, 154, 90);
        addNeckSpines(neck02, 5.8F, -4.0F, -10.0F);

        PartDefinition neck03 = neck02.addOrReplaceChild(
                "neck_03",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -2.0F, -17.0F)
        );
        addNeckMass(neck03, "neck_mass_06", 4.0F, 3.4F, 8.2F, 0.0F, 0.0F, 0.0F, 0.08F, 190, 90);
        addNeckMass(neck03, "neck_mass_07", 3.5F, 3.0F, 7.5F, 0.0F, 0.2F, -6.7F, 0.11F, 220, 90);
        addNeckSpines(neck03, 4.6F, -3.2F, -8.0F);

        PartDefinition head = neck03.addOrReplaceChild(
                "head",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.1F, -12.5F)
        );
        createHead(head);
    }

    private static void addNeckMass(
            PartDefinition parent,
            String name,
            float halfWidth,
            float halfHeight,
            float length,
            float x,
            float y,
            float z,
            float xRot,
            int texX,
            int texY
    ) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(texX, texY)
                        .addBox(
                                -halfWidth,
                                -halfHeight,
                                -length + 1.5F,
                                halfWidth * 2.0F,
                                halfHeight * 2.0F,
                                length + 2.0F
                        ),
                PartPose.offsetAndRotation(x, y, z, xRot, 0.0F, 0.0F)
        );
    }

    private static void addNeckSpines(PartDefinition parent, float height, float z1, float z2) {
        parent.addOrReplaceChild(
                "spine_a",
                CubeListBuilder.create().texOffs(232, 90)
                        .addBox(-0.8F, -height, -1.2F, 1.6F, height, 2.4F),
                PartPose.offsetAndRotation(0.0F, -4.0F, z1, -0.26F, 0.0F, 0.0F)
        );
        parent.addOrReplaceChild(
                "spine_b",
                CubeListBuilder.create().texOffs(240, 90)
                        .addBox(-0.7F, -(height * 0.82F), -1.1F, 1.4F, height * 0.82F, 2.2F),
                PartPose.offsetAndRotation(0.0F, -3.7F, z2, -0.23F, 0.0F, 0.0F)
        );
    }

    private static void createHead(PartDefinition head) {
        // Wide temporal region flowing into a low, progressively tapered snout.
        head.addOrReplaceChild(
                "back_skull",
                CubeListBuilder.create().texOffs(0, 112)
                        .addBox(-7.4F, -4.5F, -4.7F, 14.8F, 9.0F, 10.6F),
                PartPose.offsetAndRotation(0.0F, -0.5F, 1.4F, -0.07F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "temporal_cranium",
                CubeListBuilder.create().texOffs(52, 112)
                        .addBox(-6.4F, -4.0F, -5.0F, 12.8F, 8.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, -0.4F, -5.6F, -0.02F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "snout_base",
                CubeListBuilder.create().texOffs(98, 112)
                        .addBox(-5.2F, -3.0F, -5.0F, 10.4F, 6.0F, 9.0F),
                PartPose.offsetAndRotation(0.0F, 0.4F, -12.6F, 0.05F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "snout_mid",
                CubeListBuilder.create().texOffs(140, 112)
                        .addBox(-4.2F, -2.4F, -4.7F, 8.4F, 4.8F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 0.7F, -18.8F, 0.04F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "nose",
                CubeListBuilder.create().texOffs(176, 112)
                        .addBox(-3.4F, -2.0F, -4.2F, 6.8F, 4.0F, 6.8F),
                PartPose.offsetAndRotation(0.0F, 0.9F, -24.2F, 0.025F, 0.0F, 0.0F)
        );

        for (boolean left : new boolean[]{true, false}) {
            float side = left ? 1.0F : -1.0F;
            String prefix = left ? "left" : "right";
            head.addOrReplaceChild(
                    prefix + "_cheek",
                    builder(0, 134, left)
                            .addBox(-1.1F, -2.6F, -4.0F, 4.0F, 5.3F, 8.2F),
                    PartPose.offsetAndRotation(side * 5.5F, 1.1F, -6.7F, 0.04F, side * -0.12F, side * -0.10F)
            );
            head.addOrReplaceChild(
                    prefix + "_brow",
                    builder(28, 134, left)
                            .addBox(-3.0F, -0.8F, -3.0F, 6.0F, 1.7F, 6.0F),
                    PartPose.offsetAndRotation(side * 3.2F, -3.7F, -9.5F, -0.14F, side * -0.12F, side * -0.08F)
            );
            createHorn(head, left, true);
            createHorn(head, left, false);
        }

        PartDefinition jaw = head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create().texOffs(62, 134)
                        .addBox(-4.9F, -0.4F, -15.0F, 9.8F, 3.2F, 16.8F),
                PartPose.offsetAndRotation(0.0F, 3.2F, -8.6F, 0.02F, 0.0F, 0.0F)
        );
        jaw.addOrReplaceChild(
                "jaw_tip",
                CubeListBuilder.create().texOffs(116, 134)
                        .addBox(-3.6F, -0.2F, -6.4F, 7.2F, 2.5F, 7.2F),
                PartPose.offsetAndRotation(0.0F, 0.2F, -13.5F, -0.045F, 0.0F, 0.0F)
        );
        jaw.addOrReplaceChild(
                "chin_keel",
                CubeListBuilder.create().texOffs(150, 134)
                        .addBox(-3.0F, -0.3F, -4.8F, 6.0F, 1.8F, 5.8F),
                PartPose.offsetAndRotation(0.0F, 2.0F, -7.2F, 0.10F, 0.0F, 0.0F)
        );

        // Crown spines diminish toward the snout.
        addHeadSpine(head, "crown_01", -4.3F, 1.6F, 6.8F, 0);
        addHeadSpine(head, "crown_02", -4.3F, -4.2F, 5.9F, 10);
        addHeadSpine(head, "crown_03", -4.0F, -9.8F, 4.8F, 20);
    }

    private static void createHorn(PartDefinition head, boolean left, boolean primary) {
        float side = left ? 1.0F : -1.0F;
        float rootLength = primary ? 8.5F : 5.8F;
        float rootThickness = primary ? 2.8F : 2.0F;
        String name = (left ? "left" : "right") + (primary ? "_horn" : "_horn_secondary");

        PartDefinition horn = head.addOrReplaceChild(
                name,
                builder(primary ? 180 : 202, 134, left)
                        .addBox(
                                -rootThickness * 0.5F,
                                -rootThickness * 0.5F,
                                -0.8F,
                                rootThickness,
                                rootThickness,
                                rootLength
                        ),
                PartPose.offsetAndRotation(
                        side * (primary ? 5.4F : 5.8F),
                        primary ? -3.8F : -2.8F,
                        primary ? 1.5F : -4.8F,
                        primary ? -0.48F : -0.32F,
                        side * (primary ? 0.34F : 0.46F),
                        side * -0.10F
                )
        );

        float tipThickness = rootThickness * 0.52F;
        horn.addOrReplaceChild(
                "tip",
                builder(primary ? 180 : 202, 146, left)
                        .addBox(
                                -tipThickness * 0.5F,
                                -tipThickness * 0.5F,
                                0.0F,
                                tipThickness,
                                tipThickness,
                                rootLength * 0.58F
                        ),
                PartPose.offsetAndRotation(0.0F, 0.0F, rootLength - 1.0F, -0.22F, side * 0.08F, side * -0.04F)
        );
    }

    private static void addHeadSpine(PartDefinition head, String name, float y, float z, float height, int texShift) {
        head.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(224 + (texShift % 24), 134)
                        .addBox(-0.75F, -height, -1.2F, 1.5F, height, 2.4F),
                PartPose.offsetAndRotation(0.0F, y, z, -0.25F, 0.0F, 0.0F)
        );
    }

    /* ===================================================================== */
    /* WINGS — continuous fan surface + articulated dorsal skeleton           */
    /* ===================================================================== */

    private static void createWing(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String prefix = left ? "left" : "right";

        PartDefinition wingRoot = body.addOrReplaceChild(
                prefix + "_wing_root",
                CubeListBuilder.create(),
                PartPose.offset(side * 9.4F, -5.4F, -4.6F)
        );

        wingRoot.addOrReplaceChild(
                "wing_shoulder",
                builder(0, 158, left)
                        .addBox(-3.9F, -3.2F, -4.2F, 7.8F, 6.4F, 8.8F),
                PartPose.offsetAndRotation(side * 1.9F, 0.0F, 0.6F, -0.08F, side * -0.10F, side * -0.08F)
        );

        // Compatibility bone. Geometry originates at its pivot and overlaps the shoulder.
        PartDefinition upperArm = wingRoot.addOrReplaceChild(
                "upper_arm",
                CubeListBuilder.create(),
                PartPose.offset(side * 2.0F, -0.2F, 0.8F)
        );
        addWingSegment(upperArm, "humerus_base", left, 11.0F, 4.8F, 0.0F, 0.0F, 0.0F, 36, 158);
        addWingSegment(upperArm, "humerus_distal", left, 10.5F, 4.0F, side * 9.0F, -0.3F, 1.8F, 70, 158);
        upperArm.addOrReplaceChild(
                "elbow_joint",
                builder(102, 158, left)
                        .addBox(-3.2F, -3.0F, -3.0F, 6.4F, 6.0F, 6.0F),
                PartPose.offset(side * 18.0F, -0.6F, 3.7F)
        );

        PartDefinition forearm = upperArm.addOrReplaceChild(
                "forearm",
                CubeListBuilder.create(),
                PartPose.offset(side * 17.5F, -0.5F, 3.6F)
        );
        addWingSegment(forearm, "radius_ulna_base", left, 13.0F, 3.8F, 0.0F, 0.0F, 0.0F, 132, 158);
        addWingSegment(forearm, "radius_ulna_distal", left, 12.5F, 3.2F, side * 11.0F, -0.2F, 2.2F, 170, 158);
        forearm.addOrReplaceChild(
                "wrist_joint",
                builder(206, 158, left)
                        .addBox(-2.7F, -2.6F, -2.6F, 5.4F, 5.2F, 5.2F),
                PartPose.offset(side * 22.0F, -0.4F, 4.6F)
        );

        PartDefinition hand = forearm.addOrReplaceChild(
                "hand",
                CubeListBuilder.create(),
                PartPose.offset(side * 21.6F, -0.3F, 4.5F)
        );
        addWingSegment(hand, "metacarpal", left, 12.5F, 2.7F, 0.0F, 0.0F, 0.0F, 0, 176);

        createWingDigits(hand, left);
        createContinuousWingMembrane(wingRoot, left);

        hand.addOrReplaceChild(
                "wing_thumb",
                builder(34, 176, left)
                        .addBox(-0.8F, -0.8F, -0.8F, 1.6F, 1.6F, 6.0F),
                PartPose.offsetAndRotation(side * 6.5F, 0.3F, -0.4F, -0.62F, side * 0.16F, side * -0.12F)
        );
    }

    private static void addWingSegment(
            PartDefinition parent,
            String name,
            boolean left,
            float length,
            float thickness,
            float x,
            float y,
            float z,
            int texX,
            int texY
    ) {
        parent.addOrReplaceChild(
                name,
                builder(texX, texY, left)
                        .addBox(
                                left ? -0.9F : -length + 0.9F,
                                -thickness * 0.5F,
                                -thickness * 0.5F,
                                length,
                                thickness,
                                thickness
                        ),
                PartPose.offset(x, y, z)
        );
    }

    private static void createWingDigits(PartDefinition hand, boolean left) {
        float side = left ? 1.0F : -1.0F;
        float[] lengths = {52.0F, 42.5F, 32.0F};
        float[] yaw = {-0.12F, -0.34F, -0.58F};
        float[] z = {3.5F, 9.5F, 15.0F};
        float[] x = {8.5F, 7.3F, 6.0F};
        float[] thickness = {2.05F, 1.75F, 1.45F};

        for (int i = 0; i < 3; i++) {
            float len = lengths[i];
            float thick = thickness[i];
            PartDefinition finger = hand.addOrReplaceChild(
                    "finger_" + (i + 1),
                    builder(60 + i * 42, 176, left)
                            .addBox(
                                    left ? -0.6F : -len + 0.6F,
                                    -thick * 0.5F,
                                    -thick * 0.5F,
                                    len,
                                    thick,
                                    thick
                            ),
                    PartPose.offsetAndRotation(
                            side * x[i],
                            0.0F + i * 0.10F,
                            z[i],
                            0.01F + i * 0.01F,
                            side * yaw[i],
                            side * (0.015F + i * 0.018F)
                    )
            );

            float tipLength = len * 0.24F;
            float tipThickness = Math.max(0.75F, thick * 0.52F);
            finger.addOrReplaceChild(
                    "tip",
                    builder(60 + i * 42, 188, left)
                            .addBox(
                                    left ? -0.35F : -tipLength + 0.35F,
                                    -tipThickness * 0.5F,
                                    -tipThickness * 0.5F,
                                    tipLength,
                                    tipThickness,
                                    tipThickness
                            ),
                    PartPose.offsetAndRotation(
                            side * (len * 0.80F),
                            0.0F,
                            0.0F,
                            0.0F,
                            side * (-0.05F - i * 0.025F),
                            side * (0.01F + i * 0.012F)
                    )
            );
        }
    }

    /**
     * One contiguous blocky bat-wing surface authored in wing-root space.
     * Adjacent strips overlap so there are no sky gaps between "panels".
     * The trailing edge uses a controlled series of concavities instead of a
     * rectangular slab, while the articulated skeleton renders on top.
     */
    private static void createContinuousWingMembrane(PartDefinition wingRoot, boolean left) {
        float side = left ? 1.0F : -1.0F;
        PartDefinition membrane = wingRoot.addOrReplaceChild(
                "continuous_membrane",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.05F, 1.0F, 0.018F, 0.0F, 0.0F)
        );

        // xStart, width, leadingZ, depth. Values outline a broad concave fan.
        float[][] strips = {
                {  0.0F, 12.5F,  0.0F, 27.0F},
                { 11.5F, 12.5F,  1.0F, 31.5F},
                { 23.0F, 12.5F,  2.5F, 34.0F},
                { 34.5F, 12.5F,  4.0F, 35.0F},
                { 46.0F, 12.5F,  6.5F, 33.0F},
                { 57.5F, 12.5F,  9.0F, 29.0F},
                { 69.0F, 11.5F, 12.0F, 24.0F},
                { 79.5F, 10.0F, 15.0F, 18.0F},
                { 88.5F,  8.0F, 18.0F, 12.0F}
        };

        for (int i = 0; i < strips.length; i++) {
            float start = strips[i][0];
            float width = strips[i][1];
            float leadingZ = strips[i][2];
            float depth = strips[i][3];
            membrane.addOrReplaceChild(
                    "web_strip_" + (i + 1),
                    builder((i * 24) % 216, 204 + (i % 2) * 18, left)
                            .addBox(
                                    left ? start - 0.5F : -start - width + 0.5F,
                                    0.0F,
                                    leadingZ,
                                    width + 1.0F,
                                    0.42F,
                                    depth
                            ),
                    PartPose.ZERO
            );
        }

        // Three shallow scallop fillers follow the main digit fan and keep the
        // surface continuous around the concave trailing edge.
        membrane.addOrReplaceChild(
                "scallop_inner",
                builder(0, 238, left)
                        .addBox(left ? 48.0F : -69.0F, 0.02F, 28.0F, 21.0F, 0.38F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, side * -0.10F, 0.0F)
        );
        membrane.addOrReplaceChild(
                "scallop_mid",
                builder(64, 238, left)
                        .addBox(left ? 63.0F : -83.0F, 0.02F, 27.0F, 20.0F, 0.38F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, side * -0.20F, 0.0F)
        );
        membrane.addOrReplaceChild(
                "scallop_outer",
                builder(126, 238, left)
                        .addBox(left ? 76.0F : -94.0F, 0.02F, 24.0F, 18.0F, 0.38F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, side * -0.34F, 0.0F)
        );
    }

    /* ===================================================================== */
    /* FORELEGS — muscular shoulder, visible elbow, planted four-toed foot   */
    /* ===================================================================== */

    private static void createForeleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String name = left ? "left_foreleg" : "right_foreleg";

        PartDefinition leg = body.addOrReplaceChild(
                name,
                CubeListBuilder.create(),
                PartPose.offset(side * 8.2F, 1.9F, -8.6F)
        );
        leg.addOrReplaceChild(
                "shoulder",
                builder(0, 154, left)
                        .addBox(-4.2F, -4.0F, -4.4F, 8.4F, 8.8F, 9.2F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.02F, 0.0F, side * -0.04F)
        );
        leg.addOrReplaceChild(
                "upper_arm",
                builder(38, 154, left)
                        .addBox(-3.5F, -1.2F, -3.6F, 7.0F, 12.8F, 7.2F),
                PartPose.offsetAndRotation(0.0F, 3.6F, 0.5F, 0.10F, 0.0F, side * 0.02F)
        );
        leg.addOrReplaceChild(
                "elbow_mass",
                builder(70, 154, left)
                        .addBox(-3.2F, -2.6F, -3.0F, 6.4F, 5.2F, 6.0F),
                PartPose.offset(0.0F, 13.1F, 2.8F)
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 12.2F, 2.6F)
        );
        lowerLeg.addOrReplaceChild(
                "forearm",
                builder(100, 154, left)
                        .addBox(-2.7F, -0.8F, -2.7F, 5.4F, 10.8F, 5.4F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "wrist_mass",
                builder(126, 154, left)
                        .addBox(-2.4F, -2.0F, -2.4F, 4.8F, 4.4F, 4.8F),
                PartPose.offsetAndRotation(0.0F, 10.0F, -2.1F, -0.18F, 0.0F, 0.0F)
        );

        createFoot(lowerLeg, left, false, 11.2F, -2.3F);
    }

    /* ===================================================================== */
    /* HINDLEGS — pronounced digitigrade Z with large thigh and long tarsus  */
    /* ===================================================================== */

    private static void createHindleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String name = left ? "left_hindleg" : "right_hindleg";

        PartDefinition leg = body.addOrReplaceChild(
                name,
                CubeListBuilder.create(),
                PartPose.offset(side * 7.7F, 2.2F, 18.6F)
        );
        leg.addOrReplaceChild(
                "hip",
                builder(0, 182, left)
                        .addBox(-5.2F, -4.5F, -5.0F, 10.4F, 9.5F, 10.2F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.05F, 0.0F, side * 0.03F)
        );
        leg.addOrReplaceChild(
                "thigh",
                builder(44, 182, left)
                        .addBox(-4.3F, -1.0F, -4.1F, 8.6F, 13.0F, 8.2F),
                PartPose.offsetAndRotation(0.0F, 3.8F, 0.8F, 0.12F, 0.0F, side * 0.02F)
        );
        leg.addOrReplaceChild(
                "knee",
                builder(82, 182, left)
                        .addBox(-3.7F, -2.8F, -3.5F, 7.4F, 5.6F, 7.0F),
                PartPose.offset(0.0F, 12.5F, 4.0F)
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 11.8F, 3.8F)
        );
        lowerLeg.addOrReplaceChild(
                "shin",
                builder(114, 182, left)
                        .addBox(-3.1F, -1.0F, -3.0F, 6.2F, 9.4F, 6.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "hock",
                builder(142, 182, left)
                        .addBox(-2.7F, -2.2F, -2.6F, 5.4F, 4.8F, 5.2F),
                PartPose.offsetAndRotation(0.0F, 8.2F, -2.4F, -0.24F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "tarsus",
                builder(168, 182, left)
                        .addBox(-2.3F, -0.8F, -2.2F, 4.6F, 9.5F, 4.4F),
                PartPose.offsetAndRotation(0.0F, 8.6F, -3.0F, -0.30F, 0.0F, 0.0F)
        );

        createFoot(lowerLeg, left, true, 11.0F, -5.0F);
    }

    private static void createFoot(
            PartDefinition parent,
            boolean left,
            boolean hind,
            float ignoredY,
            float ignoredZ
    ) {
        float width = hind ? 9.8F : 8.8F;
        float length = hind ? 11.5F : 10.4F;
        float distalY = hind ? 8.0F : 8.8F;
        float distalZ = hind ? -3.0F : -2.0F;
        float distalLength = hind ? 18.0F : 14.0F;
        float distalRot = hind ? -0.25F : -0.10F;

        // The distal metapodial/tarsus is a real visible bridge from wrist/hock
        // to foot.  It removes the floating-foot gap from the previous model.
        PartDefinition distal = parent.addOrReplaceChild(
                hind ? "distal_tarsus" : "metacarpal",
                builder(hind ? 194 : 150, hind ? 182 : 154, left)
                        .addBox(
                                -(hind ? 2.2F : 1.9F),
                                -0.8F,
                                -(hind ? 2.1F : 1.8F),
                                hind ? 4.4F : 3.8F,
                                distalLength + 1.4F,
                                hind ? 4.2F : 3.6F
                        ),
                PartPose.offsetAndRotation(0.0F, distalY, distalZ, distalRot, 0.0F, 0.0F)
        );

        PartDefinition foot = distal.addOrReplaceChild(
                "foot",
                builder(hind ? 194 : 150, hind ? 202 : 174, left)
                        .addBox(-width * 0.5F, -1.5F, -length + 3.0F, width, 3.0F, length),
                PartPose.offsetAndRotation(
                        0.0F,
                        distalLength,
                        hind ? -2.5F : -2.0F,
                        hind ? 0.12F : 0.08F,
                        0.0F,
                        0.0F
                )
        );

        float spread = hind ? 3.0F : 2.65F;
        float toeLength = hind ? 8.4F : 7.4F;
        for (int i = -1; i <= 1; i++) {
            PartDefinition toe = foot.addOrReplaceChild(
                    "toe_" + (i + 2),
                    builder(0 + (i + 1) * 22, 218, left)
                            .addBox(-0.9F, -0.65F, -toeLength, 1.8F, 1.3F, toeLength),
                    PartPose.offsetAndRotation(
                            i * spread,
                            0.15F,
                            -length + 4.0F,
                            -0.035F,
                            i * -0.11F,
                            0.0F
                    )
            );
            toe.addOrReplaceChild(
                    "claw",
                    builder(72, 218, left)
                            .addBox(-0.5F, -0.4F, -3.8F, 1.0F, 0.8F, 3.8F),
                    PartPose.offsetAndRotation(0.0F, 0.0F, -toeLength + 0.8F, -0.23F, 0.0F, 0.0F)
            );
        }

        foot.addOrReplaceChild(
                "outer_toe",
                builder(94, 218, left)
                        .addBox(-0.75F, -0.6F, -5.8F, 1.5F, 1.2F, 5.8F),
                PartPose.offsetAndRotation(
                        (left ? 1.0F : -1.0F) * width * 0.35F,
                        0.2F,
                        -length + 4.8F,
                        -0.03F,
                        (left ? 1.0F : -1.0F) * 0.17F,
                        0.0F
                )
        );
    }

    /* ===================================================================== */
    /* TAIL — 12 overlapping visual masses across four compatibility bones    */
    /* ===================================================================== */

    private static void createTail(PartDefinition body) {
        PartDefinition tail01 = body.addOrReplaceChild(
                "tail_01",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.2F, 28.0F, -0.055F, 0.0F, 0.0F)
        );
        addTailMass(tail01, "tail_mass_01", 6.8F, 5.0F, 11.5F, 0.0F, 0.0F, 0.0F, 0, 232);
        addTailMass(tail01, "tail_mass_02", 6.1F, 4.5F, 10.8F, 0.0F, 0.1F, 8.8F, 40, 232);
        addTailMass(tail01, "tail_mass_03", 5.5F, 4.0F, 10.0F, 0.0F, 0.2F, 17.0F, 78, 232);
        addTailSpine(tail01, "spine_01", 5.0F, 6.5F, 118, 232);

        PartDefinition tail02 = tail01.addOrReplaceChild(
                "tail_02",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.4F, 24.0F, -0.020F, 0.0F, 0.0F)
        );
        addTailMass(tail02, "tail_mass_04", 4.9F, 3.6F, 9.6F, 0.0F, 0.0F, 0.0F, 134, 232);
        addTailMass(tail02, "tail_mass_05", 4.3F, 3.2F, 9.0F, 0.0F, 0.2F, 7.6F, 168, 232);
        addTailMass(tail02, "tail_mass_06", 3.8F, 2.8F, 8.6F, 0.0F, 0.3F, 14.6F, 198, 232);
        addTailSpine(tail02, "spine_02", 4.0F, 5.5F, 226, 232);

        PartDefinition tail03 = tail02.addOrReplaceChild(
                "tail_03",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.5F, 20.8F, 0.025F, 0.0F, 0.0F)
        );
        addTailMass(tail03, "tail_mass_07", 3.3F, 2.5F, 8.4F, 0.0F, 0.0F, 0.0F, 0, 246);
        addTailMass(tail03, "tail_mass_08", 2.8F, 2.1F, 7.8F, 0.0F, 0.2F, 6.8F, 30, 246);
        addTailMass(tail03, "tail_mass_09", 2.3F, 1.8F, 7.2F, 0.0F, 0.2F, 12.8F, 58, 246);

        PartDefinition tail04 = tail03.addOrReplaceChild(
                "tail_04",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.4F, 18.0F, 0.055F, 0.0F, 0.0F)
        );
        addTailMass(tail04, "tail_mass_10", 1.9F, 1.5F, 7.0F, 0.0F, 0.0F, 0.0F, 84, 246);
        addTailMass(tail04, "tail_mass_11", 1.45F, 1.15F, 6.5F, 0.0F, 0.1F, 5.8F, 110, 246);
        addTailMass(tail04, "tail_mass_12", 1.0F, 0.85F, 6.0F, 0.0F, 0.1F, 10.8F, 134, 246);
        tail04.addOrReplaceChild(
                "tail_tip",
                CubeListBuilder.create().texOffs(158, 246)
                        .addBox(-0.45F, -0.45F, -0.5F, 0.9F, 0.9F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.1F, 16.0F, 0.06F, 0.0F, 0.0F)
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
                        .addBox(-halfWidth, -halfHeight, -1.8F, halfWidth * 2.0F, halfHeight * 2.0F, length + 2.8F),
                PartPose.offsetAndRotation(x, y, z, 0.022F, 0.0F, 0.0F)
        );
    }

    private static void addTailSpine(PartDefinition parent, String name, float height, float z, int texX, int texY) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(texX, texY)
                        .addBox(-0.65F, -height, -1.1F, 1.3F, height, 2.2F),
                PartPose.offsetAndRotation(0.0F, -3.0F, z, -0.18F, 0.0F, 0.0F)
        );
    }

    /* ===================================================================== */
    /* DORSAL CREST — large at neck/shoulder, progressively reduced to tail  */
    /* ===================================================================== */

    private static void createDorsalCrest(PartDefinition body) {
        PartDefinition crest = body.addOrReplaceChild(
                "dorsal_crest",
                CubeListBuilder.create(),
                PartPose.ZERO
        );
        float[] z = {-11.0F, -4.0F, 3.5F, 11.0F, 18.0F, 25.0F};
        float[] h = {7.5F, 8.4F, 7.6F, 6.5F, 5.3F, 4.2F};
        for (int i = 0; i < z.length; i++) {
            crest.addOrReplaceChild(
                    "spine_" + (i + 1),
                    CubeListBuilder.create().texOffs(190 + (i % 3) * 16, 246)
                            .addBox(-0.8F, -h[i], -1.25F, 1.6F, h[i], 2.5F),
                    PartPose.offsetAndRotation(0.0F, -6.3F + i * 0.25F, z[i], -0.22F, 0.0F, 0.0F)
            );
        }
    }

    private static CubeListBuilder builder(int texX, int texY, boolean left) {
        CubeListBuilder result = CubeListBuilder.create().texOffs(texX, texY);
        if (!left) {
            result.mirror();
        }
        return result;
    }
}
