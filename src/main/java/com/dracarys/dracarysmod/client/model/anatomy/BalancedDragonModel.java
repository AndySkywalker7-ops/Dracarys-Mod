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
 * Anatomy 01 — BALANCED v2, Step 5.5 radical final-concept anatomical rebuild.
 *
 * <p>This revision is a radical full-body rebuild driven by the final reference silhouette.
 * It deliberately uses many overlapping tapered masses, articulated muscle groups,
 * and a broad bat-like membrane fan so the animal reads as anatomy first and voxels second. The dragon remains a six-limbed western dragon: four
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

        // STEP 5.5: the torso is authored as a volumetric spline, not as three boxes.
        // Every adjacent section overlaps the next by several model units.
        PartDefinition thorax = body.addOrReplaceChild(
                "thorax",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -0.8F, -7.5F, -0.075F, 0.0F, 0.0F)
        );
        addBodyMass(thorax, "thorax_01", 12.8F, 8.2F, 9.5F,  0.0F, -0.7F, -7.0F, -0.08F, 0, 0);
        addBodyMass(thorax, "thorax_02", 13.4F, 8.6F, 9.8F,  0.0F, -0.4F, -1.0F, -0.04F, 44, 0);
        addBodyMass(thorax, "thorax_03", 13.0F, 8.2F, 10.0F, 0.0F,  0.0F,  5.4F,  0.00F, 88, 0);
        addBodyMass(thorax, "thorax_04", 12.0F, 7.6F, 9.6F,  0.0F,  0.4F, 11.6F,  0.035F, 132, 0);
        addBodyMass(thorax, "thorax_05", 10.8F, 6.8F, 9.2F,  0.0F,  0.8F, 17.2F,  0.055F, 176, 0);

        // Deep ventral chest and paired pectoral masses give the front half visible muscle.
        thorax.addOrReplaceChild(
                "sternum_keel",
                CubeListBuilder.create().texOffs(0, 30)
                        .addBox(-7.5F, -1.8F, -7.0F, 15.0F, 8.0F, 19.5F),
                PartPose.offsetAndRotation(0.0F, 5.1F, 1.0F, 0.10F, 0.0F, 0.0F)
        );
        for (boolean left : new boolean[]{true, false}) {
            float side = left ? 1.0F : -1.0F;
            String prefix = left ? "left" : "right";
            thorax.addOrReplaceChild(
                    prefix + "_pectoral",
                    builder(62, 30, left)
                            .addBox(-4.2F, -3.4F, -5.7F, 8.7F, 7.2F, 12.0F),
                    PartPose.offsetAndRotation(side * 8.2F, 3.0F, -2.6F, 0.06F, side * -0.08F, side * -0.12F)
            );
            thorax.addOrReplaceChild(
                    prefix + "_latissimus",
                    builder(100, 30, left)
                            .addBox(-3.6F, -2.8F, -6.0F, 7.4F, 5.9F, 12.6F),
                    PartPose.offsetAndRotation(side * 9.4F, -2.4F, 7.8F, -0.04F, side * 0.05F, side * -0.10F)
            );
        }

        PartDefinition abdomen = body.addOrReplaceChild(
                "abdomen",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.5F, 8.8F, 0.045F, 0.0F, 0.0F)
        );
        addBodyMass(abdomen, "abdomen_01", 9.4F, 6.0F, 9.6F, 0.0F, 0.0F,  0.0F,  0.02F, 0, 52);
        addBodyMass(abdomen, "abdomen_02", 8.6F, 5.5F, 9.2F, 0.0F, 0.3F,  6.0F,  0.03F, 38, 52);
        addBodyMass(abdomen, "abdomen_03", 7.8F, 5.0F, 8.8F, 0.0F, 0.7F, 11.7F,  0.02F, 76, 52);
        addBodyMass(abdomen, "abdomen_04", 7.4F, 4.8F, 8.5F, 0.0F, 0.9F, 17.0F, -0.01F, 114, 52);
        abdomen.addOrReplaceChild(
                "belly_muscle",
                CubeListBuilder.create().texOffs(154, 52)
                        .addBox(-6.4F, -1.4F, -5.4F, 12.8F, 5.2F, 22.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, 7.5F, 0.06F, 0.0F, 0.0F)
        );

        PartDefinition pelvis = body.addOrReplaceChild(
                "pelvis",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.1F, 20.0F, -0.04F, 0.0F, 0.0F)
        );
        addBodyMass(pelvis, "pelvis_01", 8.2F, 5.4F, 8.8F, 0.0F, 0.0F, -2.0F, -0.015F, 0, 74);
        addBodyMass(pelvis, "pelvis_02", 9.4F, 6.3F, 9.4F, 0.0F, 0.0F,  3.8F,  0.00F, 38, 74);
        addBodyMass(pelvis, "pelvis_03",10.1F, 6.7F, 9.6F, 0.0F, 0.1F,  9.7F,  0.015F, 78, 74);
        addBodyMass(pelvis, "pelvis_04", 9.2F, 6.0F, 9.0F, 0.0F, 0.2F, 15.4F,  0.03F, 120, 74);
        addBodyMass(pelvis, "pelvis_05", 7.9F, 5.2F, 8.4F, 0.0F, 0.3F, 20.6F,  0.05F, 160, 74);

        addLimbGirdleMasses(body);
        return body;
    }

    private static void addLimbGirdleMasses(PartDefinition body) {
        for (boolean left : new boolean[]{true, false}) {
            float side = left ? 1.0F : -1.0F;
            String prefix = left ? "left" : "right";

            // Dorsal flight musculature.  These large masses make the wing roots feel
            // mechanically anchored to the rib cage instead of pasted onto its sides.
            body.addOrReplaceChild(
                    prefix + "_wing_scapula",
                    builder(0, 96, left)
                            .addBox(-5.0F, -4.2F, -7.2F, 10.2F, 8.6F, 14.8F),
                    PartPose.offsetAndRotation(side * 9.2F, -5.2F, -4.0F, -0.10F, side * -0.12F, side * -0.18F)
            );
            body.addOrReplaceChild(
                    prefix + "_wing_deltoid",
                    builder(44, 96, left)
                            .addBox(-4.4F, -3.6F, -5.8F, 8.8F, 7.5F, 12.0F),
                    PartPose.offsetAndRotation(side * 11.0F, -3.8F, -0.5F, -0.06F, side * -0.10F, side * -0.16F)
            );

            // Terrestrial shoulder is lower/forward and visually distinct from the wing.
            body.addOrReplaceChild(
                    prefix + "_front_shoulder_mass",
                    builder(84, 96, left)
                            .addBox(-4.8F, -4.4F, -5.6F, 9.8F, 9.4F, 11.5F),
                    PartPose.offsetAndRotation(side * 8.4F, 1.8F, -9.5F, 0.06F, side * -0.05F, side * -0.08F)
            );
            body.addOrReplaceChild(
                    prefix + "_hip_mass",
                    builder(126, 96, left)
                            .addBox(-5.8F, -5.2F, -6.0F, 11.8F, 10.8F, 12.4F),
                    PartPose.offsetAndRotation(side * 8.4F, 1.8F, 21.8F, -0.06F, side * 0.06F, side * 0.05F)
            );
            body.addOrReplaceChild(
                    prefix + "_gluteal_mass",
                    builder(174, 96, left)
                            .addBox(-4.7F, -4.1F, -5.0F, 9.6F, 8.6F, 10.4F),
                    PartPose.offsetAndRotation(side * 8.5F, 0.3F, 27.0F, 0.05F, side * 0.05F, side * 0.04F)
            );
        }
    }

    private static void addBodyMass(
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
                        .addBox(-halfWidth, -halfHeight, -length * 0.55F,
                                halfWidth * 2.0F, halfHeight * 2.0F, length),
                PartPose.offsetAndRotation(x, y, z, xRot, 0.0F, 0.0F)
        );
    }

    /* ===================================================================== */
    /* NECK + HEAD — 7 visual masses over three compatibility neck bones     */
    /* ===================================================================== */

    private static void createNeckAndHead(PartDefinition body) {
        // Three compatibility bones, ten overlapping visual masses.
        PartDefinition neck01 = body.addOrReplaceChild(
                "neck_01",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -2.6F, -14.8F)
        );
        addNeckMass(neck01, "neck_mass_01", 7.4F, 6.4F, 8.5F, 0.0F,  0.0F,  0.5F, -0.10F, 0, 112);
        addNeckMass(neck01, "neck_mass_02", 7.0F, 6.0F, 8.3F, 0.0F, -1.1F, -5.7F, -0.12F, 34, 112);
        addNeckMass(neck01, "neck_mass_03", 6.5F, 5.5F, 8.0F, 0.0F, -2.0F,-11.5F, -0.10F, 68, 112);
        addNeckSpines(neck01, 7.6F, -3.0F, -9.2F);

        PartDefinition neck02 = neck01.addOrReplaceChild(
                "neck_02",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -4.8F, -16.0F)
        );
        addNeckMass(neck02, "neck_mass_04", 5.9F, 5.0F, 7.8F, 0.0F,  0.0F,  0.0F, -0.06F, 102, 112);
        addNeckMass(neck02, "neck_mass_05", 5.4F, 4.6F, 7.6F, 0.0F, -0.8F, -5.8F,  0.00F, 134, 112);
        addNeckMass(neck02, "neck_mass_06", 4.9F, 4.2F, 7.4F, 0.0F, -1.0F,-11.2F,  0.06F, 164, 112);
        addNeckMass(neck02, "neck_mass_07", 4.4F, 3.9F, 7.1F, 0.0F, -0.8F,-16.2F,  0.09F, 194, 112);
        addNeckSpines(neck02, 6.2F, -4.0F, -11.0F);

        PartDefinition neck03 = neck02.addOrReplaceChild(
                "neck_03",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -1.8F, -19.3F)
        );
        addNeckMass(neck03, "neck_mass_08", 4.0F, 3.6F, 7.0F, 0.0F, 0.0F,  0.0F, 0.08F, 0, 132);
        addNeckMass(neck03, "neck_mass_09", 3.6F, 3.2F, 6.8F, 0.0F, 0.1F, -5.2F, 0.11F, 30, 132);
        addNeckMass(neck03, "neck_mass_10",3.2F, 2.9F, 6.5F, 0.0F, 0.2F,-10.0F, 0.12F, 58, 132);
        addNeckSpines(neck03, 4.8F, -2.0F, -7.2F);

        PartDefinition head = neck03.addOrReplaceChild(
                "head",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.5F, -14.2F)
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
        // Low predatory skull: broad temporal region, long tapering snout, powerful jaw.
        head.addOrReplaceChild(
                "occipital_mass",
                CubeListBuilder.create().texOffs(0, 148)
                        .addBox(-8.4F, -5.0F, -5.2F, 16.8F, 10.0F, 10.8F),
                PartPose.offsetAndRotation(0.0F, -0.6F, 2.4F, -0.08F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "back_skull",
                CubeListBuilder.create().texOffs(56, 148)
                        .addBox(-7.7F, -4.6F, -5.3F, 15.4F, 9.2F, 11.2F),
                PartPose.offsetAndRotation(0.0F, -0.5F, -3.7F, -0.05F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "temporal_cranium",
                CubeListBuilder.create().texOffs(108, 148)
                        .addBox(-6.9F, -4.0F, -5.0F, 13.8F, 8.1F, 10.3F),
                PartPose.offsetAndRotation(0.0F, -0.2F, -9.6F, -0.015F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "snout_base",
                CubeListBuilder.create().texOffs(156, 148)
                        .addBox(-5.7F, -3.2F, -5.2F, 11.4F, 6.4F, 9.8F),
                PartPose.offsetAndRotation(0.0F, 0.6F, -15.5F, 0.045F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "snout_mid",
                CubeListBuilder.create().texOffs(198, 148)
                        .addBox(-4.7F, -2.7F, -5.0F, 9.4F, 5.4F, 9.0F),
                PartPose.offsetAndRotation(0.0F, 0.9F, -22.0F, 0.035F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "nose",
                CubeListBuilder.create().texOffs(0, 170)
                        .addBox(-3.8F, -2.2F, -4.5F, 7.6F, 4.4F, 7.6F),
                PartPose.offsetAndRotation(0.0F, 1.1F, -27.8F, 0.02F, 0.0F, 0.0F)
        );

        for (boolean left : new boolean[]{true, false}) {
            float side = left ? 1.0F : -1.0F;
            String prefix = left ? "left" : "right";
            head.addOrReplaceChild(
                    prefix + "_jaw_muscle",
                    builder(36, 170, left)
                            .addBox(-2.2F, -3.0F, -4.4F, 4.6F, 6.3F, 9.0F),
                    PartPose.offsetAndRotation(side * 6.3F, 1.2F, -7.8F, 0.03F, side * -0.11F, side * -0.12F)
            );
            head.addOrReplaceChild(
                    prefix + "_cheek",
                    builder(58, 170, left)
                            .addBox(-2.0F, -2.6F, -4.0F, 4.2F, 5.4F, 8.4F),
                    PartPose.offsetAndRotation(side * 6.0F, 1.0F, -12.2F, 0.04F, side * -0.12F, side * -0.10F)
            );
            head.addOrReplaceChild(
                    prefix + "_brow",
                    builder(82, 170, left)
                            .addBox(-3.4F, -0.9F, -3.2F, 6.8F, 1.9F, 6.4F),
                    PartPose.offsetAndRotation(side * 3.6F, -4.1F, -12.5F, -0.15F, side * -0.13F, side * -0.10F)
            );
            createHorn(head, left, true);
            createHorn(head, left, false);
        }

        PartDefinition jaw = head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create().texOffs(112, 170)
                        .addBox(-5.7F, -0.5F, -18.2F, 11.4F, 3.8F, 20.0F),
                PartPose.offsetAndRotation(0.0F, 3.4F, -10.5F, 0.025F, 0.0F, 0.0F)
        );
        jaw.addOrReplaceChild(
                "jaw_tip",
                CubeListBuilder.create().texOffs(162, 170)
                        .addBox(-4.0F, -0.2F, -7.4F, 8.0F, 2.8F, 8.2F),
                PartPose.offsetAndRotation(0.0F, 0.2F, -17.2F, -0.045F, 0.0F, 0.0F)
        );
        jaw.addOrReplaceChild(
                "chin_keel",
                CubeListBuilder.create().texOffs(198, 170)
                        .addBox(-3.4F, -0.3F, -5.6F, 6.8F, 2.1F, 6.6F),
                PartPose.offsetAndRotation(0.0F, 2.4F, -10.4F, 0.10F, 0.0F, 0.0F)
        );

        addHeadSpine(head, "crown_01", -4.6F,  2.0F, 7.8F, 0);
        addHeadSpine(head, "crown_02", -4.8F, -3.8F, 7.0F, 8);
        addHeadSpine(head, "crown_03", -4.6F, -9.2F, 6.2F, 16);
        addHeadSpine(head, "crown_04", -4.1F,-14.5F, 5.0F, 20);
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
    /* WINGS — radical broad bat-wing fan + articulated dorsal skeleton           */
    /* ===================================================================== */

    private static void createWing(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String prefix = left ? "left" : "right";

        PartDefinition wingRoot = body.addOrReplaceChild(
                prefix + "_wing_root",
                CubeListBuilder.create(),
                PartPose.offset(side * 9.8F, -5.8F, -4.8F)
        );

        // Root musculature is deliberately large: the reference has a powerful
        // shoulder girdle supporting enormous flight surfaces.
        wingRoot.addOrReplaceChild(
                "wing_shoulder",
                builder(0, 188, left)
                        .addBox(-5.2F, -4.5F, -5.2F, 10.5F, 9.2F, 10.8F),
                PartPose.offsetAndRotation(side * 1.8F, 0.1F, 0.6F, -0.10F, side * -0.08F, side * -0.10F)
        );
        wingRoot.addOrReplaceChild(
                "wing_chest_tendon",
                builder(46, 188, left)
                        .addBox(-3.7F, -2.5F, -7.0F, 7.6F, 5.2F, 14.2F),
                PartPose.offsetAndRotation(side * -1.8F, 1.2F, 3.2F, 0.04F, side * 0.06F, side * -0.10F)
        );

        PartDefinition upperArm = wingRoot.addOrReplaceChild(
                "upper_arm",
                CubeListBuilder.create(),
                PartPose.offset(side * 2.4F, -0.2F, 0.8F)
        );
        addWingBoneMass(upperArm, "humerus_01", left, 12.0F, 5.6F, 0.0F, 0.0F, 0.0F, 0.00F, 78, 188);
        addWingBoneMass(upperArm, "humerus_02", left, 11.8F, 5.0F, side * 9.5F, -0.2F, 1.6F, side * -0.05F, 114, 188);
        addWingBoneMass(upperArm, "humerus_03", left, 10.8F, 4.4F, side * 18.2F,-0.3F, 3.4F, side * -0.07F, 150, 188);
        upperArm.addOrReplaceChild(
                "elbow_joint",
                builder(184, 188, left)
                        .addBox(-3.8F, -3.5F, -3.5F, 7.6F, 7.0F, 7.0F),
                PartPose.offset(side * 26.5F, -0.5F, 5.6F)
        );

        PartDefinition forearm = upperArm.addOrReplaceChild(
                "forearm",
                CubeListBuilder.create(),
                PartPose.offset(side * 26.0F, -0.4F, 5.4F)
        );
        addWingBoneMass(forearm, "radius_ulna_01", left, 13.5F, 4.3F, 0.0F, 0.0F, 0.0F, side * -0.04F, 0, 208);
        addWingBoneMass(forearm, "radius_ulna_02", left, 13.2F, 3.8F, side * 11.0F,-0.2F, 2.4F, side * -0.06F, 40, 208);
        addWingBoneMass(forearm, "radius_ulna_03", left, 12.0F, 3.3F, side * 21.0F,-0.3F, 5.0F, side * -0.08F, 80, 208);
        forearm.addOrReplaceChild(
                "wrist_joint",
                builder(118, 208, left)
                        .addBox(-3.1F, -2.9F, -3.0F, 6.2F, 5.8F, 6.0F),
                PartPose.offset(side * 31.0F, -0.5F, 8.1F)
        );

        PartDefinition hand = forearm.addOrReplaceChild(
                "hand",
                CubeListBuilder.create(),
                PartPose.offset(side * 30.5F, -0.4F, 7.9F)
        );
        addWingBoneMass(hand, "metacarpal_01", left, 10.0F, 3.0F, 0.0F, 0.0F, 0.0F, side * -0.05F, 146, 208);
        addWingBoneMass(hand, "metacarpal_02", left,  9.5F, 2.6F, side * 8.0F, 0.0F, 2.2F, side * -0.07F, 176, 208);

        createWingDigits(hand, left);
        createBroadWingMembrane(wingRoot, left);

        hand.addOrReplaceChild(
                "wing_thumb",
                builder(206, 208, left)
                        .addBox(-0.9F, -0.9F, -0.8F, 1.8F, 1.8F, 7.5F),
                PartPose.offsetAndRotation(side * 7.5F, 0.2F, -0.3F, -0.64F, side * 0.18F, side * -0.12F)
        );
    }

    private static void addWingBoneMass(
            PartDefinition parent,
            String name,
            boolean left,
            float length,
            float thickness,
            float x,
            float y,
            float z,
            float yaw,
            int texX,
            int texY
    ) {
        parent.addOrReplaceChild(
                name,
                builder(texX, texY, left)
                        .addBox(left ? -0.8F : -length + 0.8F,
                                -thickness * 0.5F,
                                -thickness * 0.5F,
                                length, thickness, thickness),
                PartPose.offsetAndRotation(x, y, z, 0.0F, yaw, 0.0F)
        );
    }



    private static void createWingDigits(PartDefinition hand, boolean left) {
        float side = left ? 1.0F : -1.0F;

        // Three long articulated rays. Each ray has three tapered sections.
        float[] baseLength = {40.0F, 35.0F, 30.0F};
        float[] midLength  = {29.0F, 25.0F, 21.0F};
        float[] tipLength  = {20.0F, 17.0F, 14.0F};
        float[] rootX      = {12.0F, 10.0F,  8.0F};
        float[] rootZ      = { 4.0F, 14.0F, 24.0F};
        float[] yaw        = {-0.10F,-0.34F,-0.63F};
        float[] thickness  = { 2.5F,  2.1F,  1.8F};

        for (int i = 0; i < 3; i++) {
            float t = thickness[i];
            PartDefinition finger = hand.addOrReplaceChild(
                    "finger_" + (i + 1),
                    builder((i * 58) % 210, 226, left)
                            .addBox(left ? -0.6F : -baseLength[i] + 0.6F,
                                    -t * 0.5F, -t * 0.5F,
                                    baseLength[i], t, t),
                    PartPose.offsetAndRotation(
                            side * rootX[i],
                            i * 0.12F,
                            rootZ[i],
                            0.015F + i * 0.012F,
                            side * yaw[i],
                            side * (0.02F + i * 0.02F)
                    )
            );

            float t2 = t * 0.68F;
            PartDefinition mid = finger.addOrReplaceChild(
                    "mid",
                    builder((i * 58 + 24) % 220, 226, left)
                            .addBox(left ? -0.45F : -midLength[i] + 0.45F,
                                    -t2 * 0.5F, -t2 * 0.5F,
                                    midLength[i], t2, t2),
                    PartPose.offsetAndRotation(
                            side * (baseLength[i] - 1.0F), 0.0F, 0.0F,
                            0.0F, side * (-0.055F - i * 0.03F), side * (0.01F + i * 0.01F)
                    )
            );

            float t3 = Math.max(0.7F, t2 * 0.58F);
            mid.addOrReplaceChild(
                    "tip",
                    builder((i * 58 + 40) % 224, 226, left)
                            .addBox(left ? -0.3F : -tipLength[i] + 0.3F,
                                    -t3 * 0.5F, -t3 * 0.5F,
                                    tipLength[i], t3, t3),
                    PartPose.offsetAndRotation(
                            side * (midLength[i] - 0.8F), 0.0F, 0.0F,
                            0.0F, side * (-0.07F - i * 0.035F), side * (0.01F + i * 0.01F)
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
    /**
     * STEP 5.5 broad wing membrane.
     *
     * Minecraft ModelPart provides cuboids, not arbitrary triangles, so the
     * membrane is approximated by 25 thin overlapping chord strips.  Their
     * leading and trailing edges follow a deliberate bat-wing polygon:
     * enormous chord near the middle, tapered root and tip, and three shallow
     * trailing-edge scallops.  At gameplay distance the strips read as one
     * broad membrane instead of a rectangular slab.
     */
    private static void createBroadWingMembrane(PartDefinition wingRoot, boolean left) {
        PartDefinition membrane = wingRoot.addOrReplaceChild(
                "broad_membrane",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.35F, 0.6F, 0.015F, 0.0F, 0.0F)
        );

        float[] leading = {
                -2.0F,-1.5F,-1.0F,-0.5F, 0.0F, 1.0F, 2.0F, 3.5F, 5.0F, 7.0F,
                 9.0F,11.0F,13.0F,15.0F,17.0F,19.0F,21.0F,23.0F,25.0F,27.0F,
                29.0F,31.5F,34.0F,37.0F,40.0F
        };
        float[] trailing = {
                28.0F,34.0F,40.0F,46.0F,52.0F,58.0F,64.0F,69.0F,73.0F,76.0F,
                79.0F,81.0F,83.0F,84.0F,83.0F,81.0F,82.0F,79.0F,75.0F,72.0F,
                66.0F,61.0F,54.0F,47.0F,40.5F
        };

        float stripWidth = 5.55F;
        float step = 5.0F;
        for (int i = 0; i < leading.length; i++) {
            float start = i * step;
            float depth = Math.max(1.2F, trailing[i] - leading[i]);
            membrane.addOrReplaceChild(
                    "membrane_strip_" + (i + 1),
                    builder((i * 17) % 220, 238 + (i % 2) * 8, left)
                            .addBox(
                                    left ? start - 0.35F : -start - stripWidth + 0.35F,
                                    0.0F,
                                    leading[i],
                                    stripWidth,
                                    0.34F,
                                    depth
                            ),
                    PartPose.ZERO
            );
        }

        // Inner root lobe closes the body-to-humerus triangle.
        membrane.addOrReplaceChild(
                "root_lobe",
                builder(0, 246, left)
                        .addBox(left ? -2.0F : -31.0F, 0.02F, 5.0F, 31.0F, 0.32F, 30.0F),
                PartPose.ZERO
        );

        // Reinforced trailing lobes create visible scallops instead of a ruler edge.
        membrane.addOrReplaceChild(
                "trailing_lobe_inner",
                builder(70, 246, left)
                        .addBox(left ? 43.0F : -76.0F, 0.02F, 66.0F, 33.0F, 0.32F, 12.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (left ? -1.0F : 1.0F) * 0.06F, 0.0F)
        );
        membrane.addOrReplaceChild(
                "trailing_lobe_mid",
                builder(142, 246, left)
                        .addBox(left ? 70.0F : -103.0F, 0.02F, 61.0F, 33.0F, 0.32F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (left ? -1.0F : 1.0F) * 0.12F, 0.0F)
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
                PartPose.offset(side * 8.5F, 1.8F, -9.0F)
        );

        leg.addOrReplaceChild(
                "shoulder",
                builder(0, 154, left)
                        .addBox(-5.1F, -4.7F, -5.2F, 10.2F, 10.0F, 10.8F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.05F, 0.0F, side * -0.05F)
        );
        leg.addOrReplaceChild(
                "triceps_mass",
                builder(42, 154, left)
                        .addBox(-3.9F, -2.0F, -4.0F, 7.8F, 9.8F, 8.2F),
                PartPose.offsetAndRotation(0.0F, 4.2F, 1.0F, 0.12F, 0.0F, side * -0.03F)
        );
        leg.addOrReplaceChild(
                "upper_arm",
                builder(76, 154, left)
                        .addBox(-3.5F, -1.0F, -3.5F, 7.0F, 10.8F, 7.2F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.6F, 0.13F, 0.0F, side * 0.02F)
        );
        leg.addOrReplaceChild(
                "elbow",
                builder(106, 154, left)
                        .addBox(-3.5F, -3.0F, -3.5F, 7.0F, 6.0F, 7.0F),
                PartPose.offset(0.0F, 12.4F, 2.5F)
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 11.8F, 2.2F)
        );
        lowerLeg.addOrReplaceChild(
                "forearm_proximal",
                builder(136, 154, left)
                        .addBox(-3.0F, -1.0F, -3.1F, 6.0F, 8.8F, 6.2F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.06F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "forearm_distal",
                builder(164, 154, left)
                        .addBox(-2.6F, -1.0F, -2.7F, 5.2F, 8.3F, 5.4F),
                PartPose.offsetAndRotation(0.0F, 6.4F, -1.0F, -0.10F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "wrist",
                builder(190, 154, left)
                        .addBox(-2.8F, -2.3F, -2.8F, 5.6F, 4.8F, 5.8F),
                PartPose.offset(0.0F, 13.2F, -2.3F)
        );

        createFoot(lowerLeg, left, false, 8.8F, -2.0F);
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
                PartPose.offset(side * 8.3F, 1.5F, 21.8F)
        );
        leg.addOrReplaceChild(
                "hip",
                builder(0, 182, left)
                        .addBox(-6.0F, -5.4F, -5.8F, 12.0F, 11.2F, 12.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.06F, 0.0F, side * 0.04F)
        );
        leg.addOrReplaceChild(
                "thigh_mass",
                builder(50, 182, left)
                        .addBox(-5.0F, -1.2F, -4.8F, 10.0F, 12.0F, 9.8F),
                PartPose.offsetAndRotation(0.0F, 3.8F, 1.4F, 0.20F, 0.0F, side * 0.03F)
        );
        leg.addOrReplaceChild(
                "thigh_distal",
                builder(92, 182, left)
                        .addBox(-4.1F, -1.0F, -4.0F, 8.2F, 9.4F, 8.2F),
                PartPose.offsetAndRotation(0.0F, 10.0F, 3.2F, 0.23F, 0.0F, side * 0.02F)
        );
        leg.addOrReplaceChild(
                "knee",
                builder(128, 182, left)
                        .addBox(-3.8F, -3.1F, -3.7F, 7.6F, 6.2F, 7.4F),
                PartPose.offset(0.0F, 15.2F, 5.2F)
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 14.6F, 4.8F)
        );
        lowerLeg.addOrReplaceChild(
                "shin_proximal",
                builder(160, 182, left)
                        .addBox(-3.4F, -1.0F, -3.3F, 6.8F, 8.6F, 6.6F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.14F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "shin_distal",
                builder(190, 182, left)
                        .addBox(-2.9F, -1.0F, -2.8F, 5.8F, 7.8F, 5.6F),
                PartPose.offsetAndRotation(0.0F, 6.0F, -2.0F, -0.22F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "hock",
                builder(216, 182, left)
                        .addBox(-2.9F, -2.5F, -2.8F, 5.8F, 5.2F, 5.6F),
                PartPose.offsetAndRotation(0.0F, 12.2F, -4.2F, -0.30F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "tarsus",
                builder(0, 202, left)
                        .addBox(-2.4F, -0.8F, -2.3F, 4.8F, 10.5F, 4.6F),
                PartPose.offsetAndRotation(0.0F, 12.7F, -5.0F, -0.34F, 0.0F, 0.0F)
        );

        createFoot(lowerLeg, left, true, 8.0F, -3.0F);
    }

    private static void createFoot(
            PartDefinition parent,
            boolean left,
            boolean hind,
            float ignoredY,
            float ignoredZ
    ) {
        float width = hind ? 10.8F : 9.4F;
        float length = hind ? 12.8F : 11.2F;
        float distalY = hind ? 9.0F : 9.0F;
        float distalZ = hind ? -4.2F : -2.8F;
        float distalLength = hind ? 14.0F : 12.0F;
        float distalRot = hind ? -0.28F : -0.12F;

        PartDefinition distal = parent.addOrReplaceChild(
                hind ? "distal_tarsus" : "metacarpal",
                builder(hind ? 34 : 76, 202, left)
                        .addBox(
                                -(hind ? 2.5F : 2.1F), -0.8F, -(hind ? 2.4F : 2.0F),
                                hind ? 5.0F : 4.2F, distalLength + 1.6F, hind ? 4.8F : 4.0F
                        ),
                PartPose.offsetAndRotation(0.0F, distalY, distalZ, distalRot, 0.0F, 0.0F)
        );

        PartDefinition foot = distal.addOrReplaceChild(
                "foot",
                builder(hind ? 110 : 150, 202, left)
                        .addBox(-width * 0.5F, -1.8F, -length + 3.2F, width, 3.6F, length),
                PartPose.offsetAndRotation(0.0F, distalLength, hind ? -3.2F : -2.4F, hind ? 0.16F : 0.10F, 0.0F, 0.0F)
        );

        float spread = hind ? 3.3F : 2.85F;
        float toeLength = hind ? 9.8F : 8.6F;
        for (int i = -1; i <= 1; i++) {
            PartDefinition toe = foot.addOrReplaceChild(
                    "toe_" + (i + 2),
                    builder((i + 1) * 26, 220, left)
                            .addBox(-1.0F, -0.75F, -toeLength, 2.0F, 1.5F, toeLength),
                    PartPose.offsetAndRotation(i * spread, 0.2F, -length + 4.2F,
                            -0.045F, i * -0.12F, i * 0.025F)
            );
            toe.addOrReplaceChild(
                    "claw",
                    builder(92, 220, left)
                            .addBox(-0.55F, -0.45F, -4.4F, 1.1F, 0.9F, 4.4F),
                    PartPose.offsetAndRotation(0.0F, 0.0F, -toeLength + 0.9F, -0.26F, 0.0F, 0.0F)
            );
        }

        PartDefinition outer = foot.addOrReplaceChild(
                "outer_toe",
                builder(118, 220, left)
                        .addBox(-0.85F, -0.65F, -7.0F, 1.7F, 1.3F, 7.0F),
                PartPose.offsetAndRotation(
                        (left ? 1.0F : -1.0F) * width * 0.38F,
                        0.25F,
                        -length + 4.8F,
                        -0.04F,
                        (left ? 1.0F : -1.0F) * 0.19F,
                        0.0F
                )
        );
        outer.addOrReplaceChild(
                "claw",
                builder(144, 220, left)
                        .addBox(-0.45F, -0.4F, -3.6F, 0.9F, 0.8F, 3.6F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, -0.24F, 0.0F, 0.0F)
        );
    }

    /* ===================================================================== */
    /* TAIL — 12 overlapping visual masses across four compatibility bones    */
    /* ===================================================================== */

    private static void createTail(PartDefinition body) {
        // Four compatibility bones, fourteen overlapping tapered masses.
        PartDefinition tail01 = body.addOrReplaceChild(
                "tail_01",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.0F, 31.0F, -0.07F, 0.0F, 0.0F)
        );
        addTailMass(tail01, "tail_mass_01", 7.4F, 5.5F, 10.0F, 0.0F,0.0F, 0.0F, 0, 232);
        addTailMass(tail01, "tail_mass_02", 6.8F, 5.0F,  9.8F, 0.0F,0.1F, 7.2F, 34, 232);
        addTailMass(tail01, "tail_mass_03", 6.2F, 4.6F,  9.6F, 0.0F,0.2F,14.2F, 68, 232);
        addTailMass(tail01, "tail_mass_04", 5.7F, 4.2F,  9.3F, 0.0F,0.3F,21.0F,102, 232);
        addTailSpine(tail01, "spine_01", 5.8F, 7.0F, 136, 232);

        PartDefinition tail02 = tail01.addOrReplaceChild(
                "tail_02",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.7F, 27.0F, -0.035F, 0.0F, 0.0F)
        );
        addTailMass(tail02, "tail_mass_05", 5.1F, 3.8F, 9.0F, 0.0F,0.0F, 0.0F, 164, 232);
        addTailMass(tail02, "tail_mass_06", 4.6F, 3.4F, 8.7F, 0.0F,0.2F, 6.6F, 194, 232);
        addTailMass(tail02, "tail_mass_07", 4.0F, 3.0F, 8.4F, 0.0F,0.3F,13.0F, 222, 232);
        addTailMass(tail02, "tail_mass_08", 3.5F, 2.6F, 8.0F, 0.0F,0.4F,19.0F,   0, 244);
        addTailSpine(tail02, "spine_02", 4.5F, 5.8F, 30, 244);

        PartDefinition tail03 = tail02.addOrReplaceChild(
                "tail_03",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.8F, 24.0F, 0.02F, 0.0F, 0.0F)
        );
        addTailMass(tail03, "tail_mass_09", 3.1F, 2.3F, 7.8F, 0.0F,0.0F, 0.0F, 58, 244);
        addTailMass(tail03, "tail_mass_10", 2.7F, 2.0F, 7.4F, 0.0F,0.2F, 5.8F, 84, 244);
        addTailMass(tail03, "tail_mass_11", 2.3F, 1.7F, 7.0F, 0.0F,0.3F,11.2F,108, 244);

        PartDefinition tail04 = tail03.addOrReplaceChild(
                "tail_04",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.8F, 18.5F, 0.065F, 0.0F, 0.0F)
        );
        addTailMass(tail04, "tail_mass_12", 1.9F, 1.45F, 6.8F, 0.0F,0.0F, 0.0F,132, 244);
        addTailMass(tail04, "tail_mass_13", 1.45F,1.10F, 6.4F, 0.0F,0.1F, 5.0F,156, 244);
        addTailMass(tail04, "tail_mass_14", 1.05F,0.82F, 6.0F, 0.0F,0.1F, 9.6F,178, 244);
        tail04.addOrReplaceChild(
                "tail_tip",
                CubeListBuilder.create().texOffs(202, 244)
                        .addBox(-0.42F, -0.42F, -0.5F, 0.84F, 0.84F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.1F, 15.0F, 0.07F, 0.0F, 0.0F)
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
        PartDefinition crest = body.addOrReplaceChild("dorsal_crest", CubeListBuilder.create(), PartPose.ZERO);
        float[] z = {-12.5F,-8.0F,-3.0F,2.0F,7.0F,12.0F,17.0F,22.0F,27.0F};
        float[] h = {  8.2F, 9.4F,10.2F,9.8F,9.0F, 8.0F, 6.8F, 5.6F, 4.4F};
        float[] w = {  1.9F, 2.0F, 2.1F,2.0F,1.9F, 1.8F, 1.6F, 1.45F,1.3F};
        for (int i = 0; i < z.length; i++) {
            crest.addOrReplaceChild(
                    "spine_" + (i + 1),
                    CubeListBuilder.create().texOffs((i * 20) % 220, 246)
                            .addBox(-w[i] * 0.5F, -h[i], -1.35F, w[i], h[i], 2.7F),
                    PartPose.offsetAndRotation(0.0F, -6.7F + i * 0.18F, z[i], -0.24F + i * 0.008F, 0.0F, 0.0F)
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
