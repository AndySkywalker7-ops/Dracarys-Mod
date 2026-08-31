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
 * BALANCED — Step 5.6 final anatomy sculpt.
 *
 * <p>This is intentionally a sculptural rebuild instead of an incremental pass.
 * Cuboids are used as overlapping anatomical masses: rib cage, pectorals,
 * scapular flight muscles, jaw muscles, digitigrade limbs, tapered neck/tail,
 * and broad bat-like wings. The target is a believable western dragon first
 * and a voxel construction second.</p>
 *
 * <p>Compatibility bones used by the shared animation rig are preserved:
 * body; neck_01..03; head/jaw; left/right wing roots with upper_arm/forearm;
 * four terrestrial leg roots with lower_leg; tail_01..04.</p>
 */
public final class BalancedDragonModel<T extends DracarysDragonEntity>
        extends AbstractDracarysDragonModel<T> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            DracarysMod.id("dracarys_dragon_balanced"), "main"
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
        createDorsalCrest(body);

        return LayerDefinition.create(mesh, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    /* ====================================================================== */
    /* TORSO — overlapping rib-cage spline with explicit muscular girdles      */
    /* ====================================================================== */

    private static PartDefinition createTorso(PartDefinition root) {
        PartDefinition body = root.addOrReplaceChild(
                "body", CubeListBuilder.create(), PartPose.offset(0.0F, -11.0F, 1.0F)
        );

        PartDefinition thorax = body.addOrReplaceChild(
                "thorax", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -0.8F, -7.5F, -0.10F, 0.0F, 0.0F)
        );

        // Seven rib-cage slices: widest/deepest around the shoulder girdle.
        float[] chestW = {10.6F, 12.4F, 14.1F, 15.0F, 14.4F, 13.0F, 11.5F};
        float[] chestH = { 7.0F,  8.0F,  9.0F,  9.5F,  9.0F,  8.0F,  7.0F};
        float[] chestZ = {-9.0F, -5.0F, -1.0F, 3.2F, 7.4F, 11.5F, 15.3F};
        float[] chestY = {-0.7F, -0.5F, -0.2F, 0.1F, 0.5F, 0.9F, 1.3F};
        for (int i = 0; i < chestW.length; i++) {
            addEllipsoidMass(thorax, "rib_slice_" + (i + 1), chestW[i], chestH[i], 6.8F,
                    0.0F, chestY[i], chestZ[i], -0.055F + i * 0.017F,
                    (i * 32) % 224, 0);
        }

        // Ventral sternum/keel and paired pectoral masses create depth rather than a boxy belly.
        addEllipsoidMass(thorax, "sternum_keel", 9.0F, 4.6F, 16.0F,
                0.0F, 7.3F, 2.8F, 0.12F, 0, 28);
        addEllipsoidMass(thorax, "upper_back_mass", 10.8F, 3.8F, 17.0F,
                0.0F, -7.3F, 3.3F, -0.08F, 64, 28);

        for (boolean left : new boolean[]{true, false}) {
            float side = left ? 1.0F : -1.0F;
            String p = left ? "left" : "right";
            addSideMass(thorax, p + "_pectoral_major", left, 9.6F, 7.8F, 11.0F,
                    side * 8.5F, 3.6F, -2.5F, 0.08F, side * -0.12F, side * -0.15F, 0, 52);
            addSideMass(thorax, p + "_serratus", left, 7.6F, 5.6F, 13.0F,
                    side * 11.5F, 0.0F, 6.5F, -0.02F, side * 0.08F, side * -0.12F, 44, 52);
            addSideMass(thorax, p + "_latissimus", left, 8.2F, 5.0F, 14.0F,
                    side * 10.7F, -3.2F, 9.0F, -0.05F, side * 0.09F, side * -0.10F, 86, 52);
            addSideMass(thorax, p + "_trapezius", left, 7.2F, 4.8F, 10.0F,
                    side * 6.6F, -6.5F, -4.0F, -0.15F, side * -0.08F, side * -0.12F, 128, 52);
        }

        PartDefinition abdomen = body.addOrReplaceChild(
                "abdomen", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.7F, 9.5F, 0.045F, 0.0F, 0.0F)
        );
        float[] abW = {9.8F, 9.0F, 8.2F, 7.5F, 7.2F, 7.6F};
        float[] abH = {6.2F, 5.8F, 5.3F, 4.9F, 4.7F, 4.9F};
        for (int i = 0; i < abW.length; i++) {
            addEllipsoidMass(abdomen, "abdomen_slice_" + (i + 1), abW[i], abH[i], 7.0F,
                    0.0F, i * 0.22F, i * 4.6F, 0.02F - i * 0.007F,
                    (i * 36) % 220, 76);
        }
        addEllipsoidMass(abdomen, "ventral_abdominal_mass", 6.5F, 3.1F, 24.0F,
                0.0F, 4.9F, 10.8F, 0.035F, 0, 100);

        PartDefinition pelvis = body.addOrReplaceChild(
                "pelvis", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.3F, 29.0F, -0.035F, 0.0F, 0.0F)
        );
        float[] pelW = {8.0F, 9.2F, 10.5F, 11.3F, 10.9F, 9.8F, 8.4F};
        float[] pelH = {5.2F, 5.9F, 6.8F, 7.3F, 7.0F, 6.3F, 5.5F};
        for (int i = 0; i < pelW.length; i++) {
            addEllipsoidMass(pelvis, "pelvis_slice_" + (i + 1), pelW[i], pelH[i], 6.8F,
                    0.0F, 0.15F + i * 0.10F, -4.0F + i * 4.5F, -0.025F + i * 0.012F,
                    (i * 32) % 224, 118);
        }

        addGirdleMuscles(body);
        return body;
    }

    private static void addGirdleMuscles(PartDefinition body) {
        for (boolean left : new boolean[]{true, false}) {
            float side = left ? 1.0F : -1.0F;
            String p = left ? "left" : "right";

            // Flight muscles: broad dorsal/side masses that bury the wing root inside the torso.
            addSideMass(body, p + "_wing_scapular_mass", left, 11.0F, 8.0F, 15.0F,
                    side * 9.7F, -5.4F, -4.0F, -0.12F, side * -0.13F, side * -0.18F, 0, 144);
            addSideMass(body, p + "_wing_deltoid_mass", left, 9.0F, 7.1F, 12.0F,
                    side * 12.4F, -3.0F, -0.6F, -0.05F, side * -0.10F, side * -0.16F, 46, 144);
            addSideMass(body, p + "_wing_posterior_mass", left, 8.2F, 5.8F, 11.0F,
                    side * 11.7F, -2.4F, 6.8F, 0.02F, side * 0.08F, side * -0.12F, 86, 144);

            // Terrestrial shoulder is lower/forward and visually independent from the wing root.
            addSideMass(body, p + "_front_shoulder_mass", left, 10.0F, 9.2F, 11.5F,
                    side * 8.8F, 2.2F, -9.0F, 0.06F, side * -0.05F, side * -0.08F, 126, 144);
            addSideMass(body, p + "_front_chest_mass", left, 8.0F, 6.8F, 10.0F,
                    side * 7.6F, 5.2F, -5.6F, 0.09F, side * -0.04F, side * -0.07F, 168, 144);

            // Hip/gluteal volumes are deliberately large to sell a powerful reptilian hindquarter.
            addSideMass(body, p + "_iliac_mass", left, 11.5F, 9.8F, 13.0F,
                    side * 8.8F, 1.6F, 31.0F, -0.05F, side * 0.06F, side * 0.06F, 0, 170);
            addSideMass(body, p + "_gluteal_mass", left, 10.2F, 8.8F, 12.0F,
                    side * 9.4F, 0.4F, 36.0F, 0.04F, side * 0.07F, side * 0.05F, 46, 170);
            addSideMass(body, p + "_tail_base_muscle", left, 7.5F, 5.6F, 11.0F,
                    side * 6.2F, 0.5F, 40.5F, 0.07F, side * 0.06F, side * 0.03F, 90, 170);
        }
    }

    private static void addEllipsoidMass(
            PartDefinition parent, String name,
            float halfWidth, float halfHeight, float length,
            float x, float y, float z, float xRot,
            int texX, int texY
    ) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(texX, texY)
                        .addBox(-halfWidth, -halfHeight, -length * 0.55F,
                                halfWidth * 2.0F, halfHeight * 2.0F, length),
                PartPose.offsetAndRotation(x, y, z, xRot, 0.0F, 0.0F)
        );
    }

    private static void addSideMass(
            PartDefinition parent, String name, boolean left,
            float width, float height, float length,
            float x, float y, float z,
            float xRot, float yRot, float zRot,
            int texX, int texY
    ) {
        parent.addOrReplaceChild(
                name,
                builder(texX, texY, left)
                        .addBox(-width * 0.5F, -height * 0.5F, -length * 0.5F,
                                width, height, length),
                PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot)
        );
    }

    /* ====================================================================== */
    /* NECK + HEAD — twelve visual masses, anterolateral eyes, tapered skull   */
    /* ====================================================================== */

    private static void createNeckAndHead(PartDefinition body) {
        PartDefinition neck01 = body.addOrReplaceChild(
                "neck_01", CubeListBuilder.create(), PartPose.offset(0.0F, -2.8F, -14.5F)
        );
        addNeckChain(neck01, 1, 4, 7.8F, 6.6F, 0.90F, -0.13F, 0, 194);
        addNeckSpines(neck01, 8.5F, new float[]{-1.0F, -6.0F, -11.0F, -16.0F});

        PartDefinition neck02 = neck01.addOrReplaceChild(
                "neck_02", CubeListBuilder.create(), PartPose.offset(0.0F, -5.5F, -19.0F)
        );
        addNeckChain(neck02, 5, 4, 5.9F, 5.0F, 0.89F, -0.02F, 72, 194);
        addNeckSpines(neck02, 6.7F, new float[]{-1.0F, -6.0F, -11.0F});

        PartDefinition neck03 = neck02.addOrReplaceChild(
                "neck_03", CubeListBuilder.create(), PartPose.offset(0.0F, -1.4F, -19.0F)
        );
        addNeckChain(neck03, 9, 4, 4.2F, 3.6F, 0.88F, 0.10F, 144, 194);
        addNeckSpines(neck03, 5.1F, new float[]{-1.0F, -6.0F, -11.0F});

        PartDefinition head = neck03.addOrReplaceChild(
                "head", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -16.8F)
        );
        createHead(head);
    }

    private static void addNeckChain(
            PartDefinition parent, int startIndex, int count,
            float startW, float startH, float taper,
            float baseRot, int texX, int texY
    ) {
        for (int i = 0; i < count; i++) {
            float f = (float) Math.pow(taper, i);
            float w = startW * f;
            float h = startH * f;
            float z = -i * 5.2F;
            float y = -i * 0.35F;
            float rot = baseRot + i * 0.025F;
            addEllipsoidMass(parent, "neck_mass_" + (startIndex + i), w, h, 7.2F,
                    0.0F, y, z, rot, (texX + i * 26) % 224, texY);
        }
    }

    private static void addNeckSpines(PartDefinition parent, float maxHeight, float[] zPositions) {
        for (int i = 0; i < zPositions.length; i++) {
            float h = maxHeight * (1.0F - i * 0.13F);
            parent.addOrReplaceChild(
                    "neck_spine_" + (i + 1),
                    CubeListBuilder.create().texOffs((210 + i * 10) % 246, 184)
                            .addBox(-0.75F, -h, -1.25F, 1.5F, h, 2.5F),
                    PartPose.offsetAndRotation(0.0F, -4.3F + i * 0.25F, zPositions[i], -0.28F, 0.0F, 0.0F)
            );
        }
    }

    private static void createHead(PartDefinition head) {
        // Cranium is widest behind the eyes; snout narrows in several overlapping stages.
        addEllipsoidMass(head, "occipital_mass", 8.6F, 5.4F, 10.5F, 0.0F, -0.6F, 2.0F, -0.07F, 0, 218);
        addEllipsoidMass(head, "temporal_mass", 7.9F, 4.9F, 10.0F, 0.0F, -0.5F, -4.5F, -0.04F, 40, 218);
        addEllipsoidMass(head, "orbital_cranium", 7.2F, 4.3F, 9.0F, 0.0F, -0.2F, -10.2F, -0.01F, 80, 218);
        addEllipsoidMass(head, "snout_root", 5.9F, 3.4F, 9.5F, 0.0F, 0.5F, -16.0F, 0.035F, 116, 218);
        addEllipsoidMass(head, "snout_mid", 4.8F, 2.8F, 9.0F, 0.0F, 0.8F, -22.1F, 0.03F, 152, 218);
        addEllipsoidMass(head, "muzzle", 3.9F, 2.3F, 8.0F, 0.0F, 1.0F, -27.7F, 0.02F, 188, 218);

        for (boolean left : new boolean[]{true, false}) {
            float side = left ? 1.0F : -1.0F;
            String p = left ? "left" : "right";
            addSideMass(head, p + "_jaw_adductor", left, 5.1F, 6.7F, 9.5F,
                    side * 6.3F, 1.0F, -5.7F, 0.02F, side * -0.14F, side * -0.10F, 0, 236);
            addSideMass(head, p + "_cheek_mass", left, 4.5F, 5.4F, 8.5F,
                    side * 6.2F, 1.2F, -11.5F, 0.03F, side * -0.13F, side * -0.08F, 28, 236);

            // Eyes are anterolateral: clearly on the sides of the skull, not on the front face.
            head.addOrReplaceChild(
                    p + "_eye_orbit",
                    builder(58, 236, left)
                            .addBox(-1.7F, -1.3F, -2.1F, 3.4F, 2.6F, 4.2F),
                    PartPose.offsetAndRotation(side * 7.1F, -2.2F, -11.8F, -0.08F, side * -0.20F, side * -0.12F)
            );
            head.addOrReplaceChild(
                    p + "_eye",
                    builder(76, 236, left)
                            .addBox(-0.75F, -0.70F, -0.85F, 1.5F, 1.4F, 1.7F),
                    PartPose.offsetAndRotation(side * 8.15F, -2.15F, -12.7F, -0.04F, side * -0.24F, 0.0F)
            );
            head.addOrReplaceChild(
                    p + "_brow_ridge",
                    builder(88, 236, left)
                            .addBox(-3.2F, -0.9F, -3.6F, 6.4F, 1.8F, 7.2F),
                    PartPose.offsetAndRotation(side * 4.4F, -4.3F, -11.8F, -0.18F, side * -0.14F, side * -0.12F)
            );
            createHorn(head, left, true);
            createHorn(head, left, false);
        }

        PartDefinition jaw = head.addOrReplaceChild(
                "jaw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.4F, -10.3F, 0.02F, 0.0F, 0.0F)
        );
        addEllipsoidMass(jaw, "jaw_hinge_mass", 6.3F, 2.7F, 9.5F, 0.0F, 0.6F, 0.5F, -0.02F, 112, 236);
        addEllipsoidMass(jaw, "jaw_body", 5.5F, 2.2F, 12.0F, 0.0F, 0.4F, -6.0F, -0.01F, 148, 236);
        addEllipsoidMass(jaw, "jaw_mid", 4.5F, 1.8F, 11.0F, 0.0F, 0.4F, -13.0F, -0.02F, 188, 236);
        addEllipsoidMass(jaw, "jaw_tip", 3.5F, 1.4F, 8.0F, 0.0F, 0.3F, -19.2F, -0.04F, 0, 246);

        for (int i = 0; i < 5; i++) {
            float h = 8.2F - i * 0.9F;
            head.addOrReplaceChild(
                    "crown_spine_" + (i + 1),
                    CubeListBuilder.create().texOffs(28 + i * 14, 246)
                            .addBox(-0.8F, -h, -1.2F, 1.6F, h, 2.4F),
                    PartPose.offsetAndRotation(0.0F, -4.1F + i * 0.25F, 2.2F - i * 5.2F, -0.30F, 0.0F, 0.0F)
            );
        }
    }

    private static void createHorn(PartDefinition head, boolean left, boolean primary) {
        float side = left ? 1.0F : -1.0F;
        float len1 = primary ? 9.0F : 6.3F;
        float thick = primary ? 2.8F : 2.0F;
        String name = (left ? "left" : "right") + (primary ? "_horn" : "_horn_secondary");
        PartDefinition horn = head.addOrReplaceChild(
                name,
                builder(primary ? 106 : 132, 246, left)
                        .addBox(-thick * 0.5F, -thick * 0.5F, -0.8F, thick, thick, len1),
                PartPose.offsetAndRotation(side * (primary ? 5.6F : 6.0F), primary ? -4.0F : -3.0F,
                        primary ? 1.2F : -5.0F, primary ? -0.50F : -0.34F,
                        side * (primary ? 0.36F : 0.48F), side * -0.10F)
        );
        float t2 = thick * 0.56F;
        horn.addOrReplaceChild(
                "tip",
                builder(primary ? 150 : 168, 246, left)
                        .addBox(-t2 * 0.5F, -t2 * 0.5F, 0.0F, t2, t2, len1 * 0.65F),
                PartPose.offsetAndRotation(0.0F, 0.0F, len1 - 1.0F, -0.24F, side * 0.09F, side * -0.04F)
        );
    }

    /* ====================================================================== */
    /* WINGS — huge broad planform with embedded skeleton and 48 membrane ribs */
    /* ====================================================================== */

    private static void createWing(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String p = left ? "left" : "right";

        PartDefinition wingRoot = body.addOrReplaceChild(
                p + "_wing_root", CubeListBuilder.create(), PartPose.offset(side * 10.2F, -6.0F, -4.0F)
        );

        // Root muscle cluster wraps around the skeleton. These are intentionally bulky.
        addSideMass(wingRoot, "wing_root_core", left, 12.0F, 9.5F, 12.5F,
                side * 1.8F, 0.0F, 1.0F, -0.08F, side * -0.08F, side * -0.10F, 0, 0);
        addSideMass(wingRoot, "wing_root_dorsal", left, 10.5F, 6.5F, 15.0F,
                side * 2.5F, -4.3F, 4.0F, -0.11F, side * -0.08F, side * -0.10F, 52, 0);
        addSideMass(wingRoot, "wing_root_ventral", left, 8.5F, 5.8F, 12.0F,
                side * 2.8F, 4.2F, 4.2F, 0.06F, side * -0.05F, side * -0.08F, 98, 0);

        PartDefinition upperArm = wingRoot.addOrReplaceChild(
                "upper_arm", CubeListBuilder.create(), PartPose.offset(side * 2.0F, -0.1F, 1.0F)
        );
        addWingBone(upperArm, "humerus_proximal", left, 13.5F, 6.6F, 0.0F, 0.0F, 0.0F, side * -0.035F, 136, 0);
        addWingBone(upperArm, "humerus_mid", left, 13.5F, 5.9F, side * 11.5F, -0.1F, 2.2F, side * -0.055F, 174, 0);
        addWingBone(upperArm, "humerus_distal", left, 12.0F, 5.0F, side * 22.5F, -0.2F, 5.2F, side * -0.075F, 210, 0);
        addSideMass(upperArm, "humerus_muscle", left, 19.0F, 7.0F, 8.2F,
                side * 13.5F, 0.0F, 2.2F, 0.0F, side * -0.04F, side * -0.03F, 0, 26);
        upperArm.addOrReplaceChild(
                "elbow_joint",
                builder(56, 26, left).addBox(-4.3F, -4.0F, -4.0F, 8.6F, 8.0F, 8.0F),
                PartPose.offset(side * 34.0F, -0.3F, 8.2F)
        );

        PartDefinition forearm = upperArm.addOrReplaceChild(
                "forearm", CubeListBuilder.create(), PartPose.offset(side * 33.3F, -0.2F, 7.9F)
        );
        addWingBone(forearm, "radius_ulna_proximal", left, 15.0F, 4.8F, 0.0F, 0.0F, 0.0F, side * -0.055F, 92, 26);
        addWingBone(forearm, "radius_ulna_mid", left, 15.0F, 4.2F, side * 13.0F, -0.1F, 3.0F, side * -0.075F, 132, 26);
        addWingBone(forearm, "radius_ulna_distal", left, 14.0F, 3.6F, side * 25.5F, -0.2F, 6.5F, side * -0.095F, 172, 26);
        addSideMass(forearm, "forearm_muscle", left, 20.0F, 5.3F, 7.0F,
                side * 15.0F, 0.0F, 3.0F, 0.0F, side * -0.05F, side * -0.025F, 210, 26);
        forearm.addOrReplaceChild(
                "wrist_joint",
                builder(0, 44, left).addBox(-3.8F, -3.5F, -3.5F, 7.6F, 7.0F, 7.0F),
                PartPose.offset(side * 40.0F, -0.3F, 10.0F)
        );

        PartDefinition hand = forearm.addOrReplaceChild(
                "hand", CubeListBuilder.create(), PartPose.offset(side * 39.5F, -0.2F, 9.7F)
        );
        addWingBone(hand, "metacarpal_proximal", left, 12.0F, 3.2F, 0.0F, 0.0F, 0.0F, side * -0.06F, 36, 44);
        addWingBone(hand, "metacarpal_distal", left, 11.0F, 2.7F, side * 10.0F, 0.0F, 2.5F, side * -0.08F, 70, 44);

        createWingDigits(hand, left);
        createIntegratedWingMembrane(wingRoot, left);

        hand.addOrReplaceChild(
                "wing_thumb",
                builder(104, 44, left).addBox(-0.9F, -0.9F, -0.6F, 1.8F, 1.8F, 8.0F),
                PartPose.offsetAndRotation(side * 8.0F, 0.1F, -0.5F, -0.62F, side * 0.20F, side * -0.10F)
        );
    }

    private static void addWingBone(
            PartDefinition parent, String name, boolean left,
            float length, float thickness,
            float x, float y, float z, float yaw,
            int texX, int texY
    ) {
        parent.addOrReplaceChild(
                name,
                builder(texX, texY, left)
                        .addBox(left ? -0.8F : -length + 0.8F,
                                -thickness * 0.5F, -thickness * 0.5F,
                                length, thickness, thickness),
                PartPose.offsetAndRotation(x, y, z, 0.0F, yaw, 0.0F)
        );
    }

    private static void createWingDigits(PartDefinition hand, boolean left) {
        float side = left ? 1.0F : -1.0F;
        // Three very long rays; all are INSIDE the final membrane planform.
        float[] baseLen = {46.0F, 41.0F, 36.0F};
        float[] midLen  = {37.0F, 32.0F, 27.0F};
        float[] tipLen  = {29.0F, 24.0F, 20.0F};
        float[] rootX   = {14.0F, 12.0F, 10.0F};
        float[] rootZ   = { 5.0F, 16.0F, 28.0F};
        float[] yaw     = {-0.11F, -0.34F, -0.62F};
        float[] thick   = { 2.8F,  2.35F, 2.0F};

        for (int i = 0; i < 3; i++) {
            float t = thick[i];
            PartDefinition finger = hand.addOrReplaceChild(
                    "finger_" + (i + 1),
                    builder((i * 58) % 220, 58, left)
                            .addBox(left ? -0.6F : -baseLen[i] + 0.6F,
                                    -t * 0.5F, -t * 0.5F, baseLen[i], t, t),
                    PartPose.offsetAndRotation(side * rootX[i], 0.0F, rootZ[i],
                            0.01F + i * 0.012F, side * yaw[i], side * (0.02F + i * 0.015F))
            );
            float t2 = t * 0.68F;
            PartDefinition mid = finger.addOrReplaceChild(
                    "middle",
                    builder((i * 58 + 24) % 220, 58, left)
                            .addBox(left ? -0.45F : -midLen[i] + 0.45F,
                                    -t2 * 0.5F, -t2 * 0.5F, midLen[i], t2, t2),
                    PartPose.offsetAndRotation(side * (baseLen[i] - 1.0F), 0.0F, 0.0F,
                            0.0F, side * (-0.07F - i * 0.03F), side * (0.01F + i * 0.01F))
            );
            float t3 = Math.max(0.75F, t2 * 0.56F);
            mid.addOrReplaceChild(
                    "distal",
                    builder((i * 58 + 42) % 220, 58, left)
                            .addBox(left ? -0.3F : -tipLen[i] + 0.3F,
                                    -t3 * 0.5F, -t3 * 0.5F, tipLen[i], t3, t3),
                    PartPose.offsetAndRotation(side * (midLen[i] - 0.8F), 0.0F, 0.0F,
                            0.0F, side * (-0.09F - i * 0.04F), side * (0.01F + i * 0.01F))
            );
        }
    }

    private static void createIntegratedWingMembrane(PartDefinition wingRoot, boolean left) {
        PartDefinition membrane = wingRoot.addOrReplaceChild(
                "integrated_membrane", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        // 48 overlapping spanwise strips: root/chord/tip profile resembles a broad bat/dragon wing.
        // Membrane plane intersects the skeleton's centerline so bones read as embedded ridges.
        final int strips = 48;
        final float step = 4.55F;
        final float stripWidth = 5.15F;
        for (int i = 0; i < strips; i++) {
            float x = i * step;
            float t = i / (float) (strips - 1);

            // Leading edge sweeps progressively rearward.
            float leading = -3.0F + 22.0F * t + 7.0F * t * t;

            // Huge chord through the inner/middle wing, then a graceful taper to the tip.
            float bell = (float) Math.pow(Math.sin(Math.PI * t), 0.58D);
            float chord = 34.0F + 86.0F * bell;
            chord *= (1.0F - 0.34F * t);

            // Three shallow scallops in the trailing edge rather than a ruler-straight slab.
            float scallop = (float) (6.0D * Math.sin(t * Math.PI * 3.0D) * Math.sin(Math.PI * t));
            float depth = Math.max(10.0F, chord + scallop);

            membrane.addOrReplaceChild(
                    "membrane_strip_" + (i + 1),
                    builder((i * 13) % 220, 82 + (i % 3) * 8, left)
                            .addBox(left ? x - 0.35F : -x - stripWidth + 0.35F,
                                    -0.42F, leading, stripWidth, 0.84F, depth),
                    PartPose.ZERO
            );
        }

        // Deep body root lobe fills the armpit and visually merges the membrane into the back.
        membrane.addOrReplaceChild(
                "root_web",
                builder(0, 110, left)
                        .addBox(left ? -3.0F : -48.0F, -0.45F, 1.0F, 48.0F, 0.90F, 54.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, sideSign(left) * -0.035F, 0.0F)
        );

        // Rear body web creates the broad triangular trailing attachment visible in the reference.
        membrane.addOrReplaceChild(
                "rear_body_web",
                builder(72, 110, left)
                        .addBox(left ? 20.0F : -78.0F, -0.43F, 43.0F, 58.0F, 0.86F, 52.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, sideSign(left) * -0.075F, 0.0F)
        );
    }

    private static float sideSign(boolean left) {
        return left ? 1.0F : -1.0F;
    }

    /* ====================================================================== */
    /* FORELEGS — bent reptilian chain + muscular upper limb + articulated foot */
    /* ====================================================================== */

    private static void createForeleg(PartDefinition body, boolean left) {
        float side = sideSign(left);
        String name = left ? "left_foreleg" : "right_foreleg";
        PartDefinition leg = body.addOrReplaceChild(
                name, CubeListBuilder.create(), PartPose.offset(side * 8.8F, 2.0F, -8.8F)
        );

        addSideMass(leg, "shoulder_cap", left, 10.2F, 10.0F, 10.5F,
                0.0F, 0.0F, 0.0F, 0.04F, side * -0.04F, side * -0.05F, 0, 134);
        addSideMass(leg, "upper_arm_biceps", left, 7.5F, 10.5F, 7.8F,
                side * -0.5F, 5.5F, 2.0F, 0.19F, 0.0F, side * -0.03F, 38, 134);
        addSideMass(leg, "upper_arm_triceps", left, 7.0F, 9.8F, 7.3F,
                side * 0.8F, 7.8F, 0.0F, 0.22F, 0.0F, side * 0.04F, 70, 134);
        addSideMass(leg, "elbow_mass", left, 6.8F, 6.2F, 6.8F,
                0.0F, 13.2F, 4.1F, 0.0F, 0.0F, 0.0F, 100, 134);

        PartDefinition lower = leg.addOrReplaceChild(
                "lower_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 12.8F, 4.0F)
        );
        addSideMass(lower, "forearm_extensor", left, 5.9F, 9.2F, 6.1F,
                side * 0.5F, 3.5F, -1.5F, -0.16F, 0.0F, side * 0.02F, 130, 134);
        addSideMass(lower, "forearm_flexor", left, 5.2F, 8.0F, 5.4F,
                side * -0.5F, 8.6F, -3.8F, -0.24F, 0.0F, side * -0.02F, 158, 134);
        addSideMass(lower, "wrist_mass", left, 5.5F, 4.8F, 5.5F,
                0.0F, 13.3F, -5.2F, -0.08F, 0.0F, 0.0F, 184, 134);

        createFoot(lower, left, false);
    }

    /* ====================================================================== */
    /* HINDLEGS — pronounced S/Z reptilian digitigrade chain                  */
    /* ====================================================================== */

    private static void createHindleg(PartDefinition body, boolean left) {
        float side = sideSign(left);
        String name = left ? "left_hindleg" : "right_hindleg";
        PartDefinition leg = body.addOrReplaceChild(
                name, CubeListBuilder.create(), PartPose.offset(side * 8.8F, 1.8F, 31.0F)
        );

        addSideMass(leg, "hip_socket_mass", left, 11.8F, 10.8F, 11.5F,
                0.0F, 0.0F, 0.0F, -0.05F, 0.0F, side * 0.04F, 0, 154);
        addSideMass(leg, "gluteus_mass", left, 10.5F, 9.0F, 10.0F,
                side * 0.8F, 1.8F, 3.5F, 0.15F, 0.0F, side * 0.05F, 44, 154);
        addSideMass(leg, "thigh_proximal", left, 9.5F, 12.0F, 9.0F,
                0.0F, 5.0F, 4.0F, 0.34F, 0.0F, side * 0.03F, 84, 154);
        addSideMass(leg, "thigh_distal", left, 8.1F, 10.0F, 8.0F,
                0.0F, 12.0F, 7.8F, 0.42F, 0.0F, side * 0.02F, 122, 154);
        addSideMass(leg, "knee_mass", left, 7.2F, 6.8F, 7.2F,
                0.0F, 17.8F, 10.5F, 0.03F, 0.0F, 0.0F, 156, 154);

        PartDefinition lower = leg.addOrReplaceChild(
                "lower_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 17.0F, 10.0F)
        );
        // The shin folds back toward the body before the hock/tarsus returns toward the ground.
        addSideMass(lower, "shin_proximal", left, 6.4F, 9.5F, 6.5F,
                0.0F, 3.5F, -2.5F, -0.30F, 0.0F, side * 0.01F, 188, 154);
        addSideMass(lower, "shin_distal", left, 5.5F, 8.2F, 5.6F,
                0.0F, 9.2F, -6.0F, -0.40F, 0.0F, side * 0.01F, 216, 154);
        addSideMass(lower, "hock_mass", left, 5.8F, 5.6F, 5.9F,
                0.0F, 14.7F, -9.0F, -0.18F, 0.0F, 0.0F, 0, 178);
        addSideMass(lower, "tarsus_proximal", left, 4.6F, 9.0F, 4.8F,
                0.0F, 19.0F, -8.0F, 0.30F, 0.0F, 0.0F, 26, 178);
        addSideMass(lower, "tarsus_distal", left, 3.9F, 7.8F, 4.1F,
                0.0F, 24.3F, -4.7F, 0.38F, 0.0F, 0.0F, 50, 178);

        createFoot(lower, left, true);
    }

    private static void createFoot(PartDefinition parent, boolean left, boolean hind) {
        float side = sideSign(left);
        float footY = hind ? 29.2F : 17.0F;
        float footZ = hind ? -1.0F : -5.5F;

        PartDefinition foot = parent.addOrReplaceChild(
                "foot", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, footY, footZ, hind ? 0.08F : 0.05F, 0.0F, 0.0F)
        );

        // Compact organic pads replace the old rectangular shoe-like block.
        addSideMass(foot, "heel_pad", left, hind ? 7.0F : 6.2F, 3.0F, hind ? 6.5F : 5.8F,
                0.0F, -0.3F, 1.5F, -0.05F, 0.0F, 0.0F, 78, 178);
        addSideMass(foot, "central_pad", left, hind ? 7.8F : 6.8F, 2.6F, hind ? 7.2F : 6.5F,
                0.0F, 0.3F, -2.4F, 0.03F, 0.0F, 0.0F, 108, 178);

        float spread = hind ? 3.5F : 3.0F;
        float proxLen = hind ? 7.8F : 6.8F;
        float distalLen = hind ? 6.5F : 5.7F;
        for (int i = -1; i <= 1; i++) {
            float yaw = i * -0.18F;
            PartDefinition toe = foot.addOrReplaceChild(
                    "toe_" + (i + 2),
                    builder(140 + (i + 1) * 22, 178, left)
                            .addBox(-1.15F, -0.85F, -proxLen, 2.3F, 1.7F, proxLen),
                    PartPose.offsetAndRotation(i * spread, 0.1F, -4.8F, -0.05F, yaw, i * 0.025F)
            );
            PartDefinition distal = toe.addOrReplaceChild(
                    "distal",
                    builder(206 + (i + 1) * 12, 178, left)
                            .addBox(-0.85F, -0.65F, -distalLen, 1.7F, 1.3F, distalLen),
                    PartPose.offsetAndRotation(0.0F, 0.0F, -proxLen + 0.7F, -0.10F, yaw * 0.35F, 0.0F)
            );
            distal.addOrReplaceChild(
                    "claw",
                    builder(0 + (i + 1) * 16, 188, left)
                            .addBox(-0.5F, -0.45F, -4.8F, 1.0F, 0.9F, 4.8F),
                    PartPose.offsetAndRotation(0.0F, 0.0F, -distalLen + 0.6F, -0.26F, 0.0F, 0.0F)
            );
        }

        // Rear/inner dewclaw for a less rectangular and more reptilian footprint.
        PartDefinition dew = foot.addOrReplaceChild(
                "dewclaw",
                builder(54, 188, left)
                        .addBox(-0.8F, -0.65F, -5.0F, 1.6F, 1.3F, 5.0F),
                PartPose.offsetAndRotation(side * -4.3F, -0.2F, -1.8F, -0.12F, side * 0.42F, 0.0F)
        );
        dew.addOrReplaceChild(
                "claw",
                builder(76, 188, left)
                        .addBox(-0.45F, -0.4F, -3.5F, 0.9F, 0.8F, 3.5F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -4.4F, -0.25F, 0.0F, 0.0F)
        );
    }

    /* ====================================================================== */
    /* TAIL — eighteen overlapping masses with progressive vertical/lateral arc */
    /* ====================================================================== */

    private static void createTail(PartDefinition body) {
        PartDefinition tail01 = body.addOrReplaceChild(
                "tail_01", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.5F, 40.0F, -0.08F, 0.018F, 0.0F)
        );
        addTailRange(tail01, 1, 5, 7.8F, 5.8F, 9.0F, 0.90F, 0, 200);
        addTailSpine(tail01, "spine_01", 6.0F, 7.0F, 130, 200);

        PartDefinition tail02 = tail01.addOrReplaceChild(
                "tail_02", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.8F, 34.0F, -0.035F, 0.018F, 0.0F)
        );
        addTailRange(tail02, 6, 5, 4.8F, 3.7F, 8.0F, 0.88F, 154, 200);
        addTailSpine(tail02, "spine_02", 4.5F, 6.0F, 28, 214);

        PartDefinition tail03 = tail02.addOrReplaceChild(
                "tail_03", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.8F, 30.0F, 0.02F, -0.015F, 0.0F)
        );
        addTailRange(tail03, 11, 4, 2.9F, 2.25F, 7.0F, 0.84F, 62, 214);

        PartDefinition tail04 = tail03.addOrReplaceChild(
                "tail_04", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.7F, 23.0F, 0.055F, -0.018F, 0.0F)
        );
        addTailRange(tail04, 15, 4, 1.55F, 1.20F, 6.0F, 0.78F, 112, 214);
        tail04.addOrReplaceChild(
                "tail_tip",
                CubeListBuilder.create().texOffs(166, 214)
                        .addBox(-0.4F, -0.4F, -0.5F, 0.8F, 0.8F, 7.5F),
                PartPose.offsetAndRotation(0.0F, 0.1F, 19.0F, 0.05F, -0.02F, 0.0F)
        );
    }

    private static void addTailRange(
            PartDefinition parent, int startIndex, int count,
            float startW, float startH, float length, float taper,
            int texX, int texY
    ) {
        for (int i = 0; i < count; i++) {
            float f = (float) Math.pow(taper, i);
            addEllipsoidMass(parent, "tail_mass_" + (startIndex + i), startW * f, startH * f, length,
                    0.0F, i * 0.16F, i * (length - 1.8F), 0.018F + i * 0.006F,
                    (texX + i * 26) % 224, texY);
        }
    }

    private static void addTailSpine(PartDefinition parent, String name, float height, float z, int texX, int texY) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(texX, texY)
                        .addBox(-0.7F, -height, -1.1F, 1.4F, height, 2.2F),
                PartPose.offsetAndRotation(0.0F, -3.2F, z, -0.18F, 0.0F, 0.0F)
        );
    }

    /* ====================================================================== */
    /* DORSAL CREST — progressive, integrated rather than identical pegs      */
    /* ====================================================================== */

    private static void createDorsalCrest(PartDefinition body) {
        PartDefinition crest = body.addOrReplaceChild("dorsal_crest", CubeListBuilder.create(), PartPose.ZERO);
        float[] z = {-12.5F,-9.0F,-5.5F,-2.0F,1.5F,5.0F,8.8F,12.8F,17.0F,21.5F,26.0F,31.0F,36.0F};
        float[] h = {  7.4F, 8.5F, 9.8F,10.8F,11.2F,10.6F,9.7F, 8.6F, 7.5F, 6.4F, 5.3F, 4.2F, 3.2F};
        for (int i = 0; i < z.length; i++) {
            float w = Math.max(1.1F, 2.3F - i * 0.09F);
            crest.addOrReplaceChild(
                    "spine_" + (i + 1),
                    CubeListBuilder.create().texOffs((i * 18) % 224, 228)
                            .addBox(-w * 0.5F, -h[i], -1.35F, w, h[i], 2.7F),
                    PartPose.offsetAndRotation(0.0F, -6.6F + i * 0.16F, z[i], -0.27F + i * 0.006F, 0.0F, 0.0F)
            );
        }
    }

    private static CubeListBuilder builder(int texX, int texY, boolean left) {
        CubeListBuilder b = CubeListBuilder.create().texOffs(texX, texY);
        if (!left) {
            b.mirror();
        }
        return b;
    }
}
