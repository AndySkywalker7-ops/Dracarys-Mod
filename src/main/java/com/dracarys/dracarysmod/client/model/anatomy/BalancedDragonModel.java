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
 * BALANCED — Step 5.8 blueprint-locked high-fidelity anatomical reconstruction.
 *
 * <p>This is intentionally a sculptural rebuild instead of an incremental pass.
 * Cuboids are used as overlapping anatomical masses: rib cage, pectorals,
 * scapular flight muscles, jaw muscles, digitigrade limbs, tapered neck/tail,
 * and high-arched membrane wings. The target is the black reference dragon first
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
        /*
         * Step 5.8 blueprint lock:
         * X = lateral, Y = down, Z = tailward.
         * Torso reference length = 100 blueprint units ~= 64 model units.
         * The body spline runs from the low cervical root around Z=-20
         * through a deep thorax to a narrower waist and then a broad pelvis.
         */
        PartDefinition body = root.addOrReplaceChild(
                "body", CubeListBuilder.create(), PartPose.offset(0.0F, -10.4F, 0.0F)
        );

        PartDefinition thorax = body.addOrReplaceChild(
                "thorax", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -0.4F, -8.0F, -0.045F, 0.0F, 0.0F)
        );

        // Nine slices form a deep, rounded rib cage instead of one long rectangular chest.
        float[] chestW = {11.8F, 13.5F, 15.4F, 16.2F, 16.0F, 15.3F, 14.0F, 12.8F, 11.8F};
        float[] chestH = { 7.6F,  8.8F, 10.2F, 11.0F, 11.4F, 11.1F, 10.2F,  9.0F,  8.0F};
        float[] chestZ = {-10.5F, -7.0F, -3.5F, 0.0F, 3.5F, 7.0F, 10.5F, 14.0F, 17.5F};
        float[] chestY = { -0.6F, -0.5F, -0.35F, -0.15F, 0.0F, 0.20F, 0.45F, 0.70F, 0.95F};
        float[] chestR = { -0.060F,-0.050F,-0.038F,-0.020F,0.000F,0.014F,0.026F,0.036F,0.045F};
        for (int i = 0; i < chestW.length; i++) {
            addEllipsoidMass(thorax, "rib_slice_" + (i + 1), chestW[i], chestH[i], 6.4F,
                    0.0F, chestY[i], chestZ[i], chestR[i], (i * 26) % 224, 0);
        }

        // Dorsal and ventral keels break the rectangular silhouette and sell a real thoracic cage.
        addEllipsoidMass(thorax, "sternum_keel", 9.8F, 5.0F, 18.0F,
                0.0F, 8.3F, 2.5F, 0.085F, 8, 32);
        addEllipsoidMass(thorax, "dorsal_ridge_mass", 11.2F, 4.5F, 19.0F,
                0.0F, -8.8F, 3.5F, -0.055F, 68, 32);

        for (boolean left : new boolean[]{true, false}) {
            float side = left ? 1.0F : -1.0F;
            String q = left ? "left" : "right";

            addSideMass(thorax, q + "_pectoral_major", left, 10.8F, 9.0F, 12.5F,
                    side * 8.9F, 4.0F, -3.2F, 0.08F, side * -0.11F, side * -0.16F, 0, 56);
            addSideMass(thorax, q + "_pectoral_lower", left, 8.4F, 6.4F, 12.0F,
                    side * 7.3F, 7.0F, 1.0F, 0.10F, side * -0.07F, side * -0.10F, 44, 56);
            addSideMass(thorax, q + "_serratus", left, 8.6F, 6.3F, 14.0F,
                    side * 12.0F, 0.5F, 5.7F, -0.01F, side * 0.07F, side * -0.13F, 86, 56);
            addSideMass(thorax, q + "_latissimus", left, 9.2F, 5.8F, 15.5F,
                    side * 11.5F, -3.5F, 8.2F, -0.05F, side * 0.08F, side * -0.11F, 130, 56);
            addSideMass(thorax, q + "_trapezius", left, 8.5F, 5.5F, 11.5F,
                    side * 7.4F, -7.0F, -4.2F, -0.13F, side * -0.07F, side * -0.13F, 174, 56);
        }

        // Waist is unmistakably narrower than thorax and pelvis in top view.
        PartDefinition abdomen = body.addOrReplaceChild(
                "abdomen", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.0F, 13.0F, 0.030F, 0.0F, 0.0F)
        );
        float[] abW = {10.2F, 9.4F, 8.6F, 7.9F, 7.3F, 7.0F, 7.4F, 8.0F};
        float[] abH = { 6.7F, 6.2F, 5.8F, 5.4F, 5.0F, 4.8F, 4.9F, 5.2F};
        for (int i = 0; i < abW.length; i++) {
            addEllipsoidMass(abdomen, "abdomen_slice_" + (i + 1), abW[i], abH[i], 6.5F,
                    0.0F, i * 0.18F, i * 4.1F, 0.018F - i * 0.004F,
                    (i * 30) % 224, 84);
        }
        addEllipsoidMass(abdomen, "ventral_abdominal_mass", 6.9F, 3.3F, 27.0F,
                0.0F, 5.1F, 13.0F, 0.020F, 6, 110);

        // Pelvis widens again around the hip sockets and then narrows into the tail root.
        PartDefinition pelvis = body.addOrReplaceChild(
                "pelvis", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.6F, 34.0F, -0.025F, 0.0F, 0.0F)
        );
        float[] pelW = {8.5F, 9.6F, 10.8F, 12.0F, 12.5F, 12.0F, 10.9F, 9.5F};
        float[] pelH = {5.5F, 6.2F, 7.0F, 7.8F, 8.1F, 7.8F, 7.0F, 6.0F};
        for (int i = 0; i < pelW.length; i++) {
            addEllipsoidMass(pelvis, "pelvis_slice_" + (i + 1), pelW[i], pelH[i], 6.4F,
                    0.0F, 0.1F + i * 0.08F, -4.5F + i * 4.0F, -0.024F + i * 0.010F,
                    (i * 28) % 224, 124);
        }

        addGirdleMuscles(body);
        return body;
    }

    private static void addGirdleMuscles(PartDefinition body) {
        for (boolean left : new boolean[]{true, false}) {
            float side = left ? 1.0F : -1.0F;
            String q = left ? "left" : "right";

            // Wing girdle is visibly buried in the dorsal thorax rather than pasted onto the side.
            addSideMass(body, q + "_wing_scapular_mass", left, 12.8F, 9.0F, 16.5F,
                    side * 10.3F, -5.6F, -5.0F, -0.11F, side * -0.12F, side * -0.17F, 4, 152);
            addSideMass(body, q + "_wing_deltoid_mass", left, 10.5F, 8.0F, 13.8F,
                    side * 13.4F, -3.2F, -1.2F, -0.05F, side * -0.10F, side * -0.15F, 50, 152);
            addSideMass(body, q + "_wing_posterior_mass", left, 9.6F, 6.8F, 13.0F,
                    side * 12.6F, -2.3F, 6.5F, 0.01F, side * 0.07F, side * -0.11F, 96, 152);

            // Terrestrial shoulder remains lower and more anterior than the wing girdle.
            addSideMass(body, q + "_front_shoulder_mass", left, 11.2F, 10.5F, 12.5F,
                    side * 9.2F, 2.8F, -10.5F, 0.06F, side * -0.05F, side * -0.09F, 140, 152);
            addSideMass(body, q + "_front_chest_mass", left, 8.8F, 7.6F, 11.0F,
                    side * 7.9F, 6.0F, -6.2F, 0.09F, side * -0.04F, side * -0.07F, 184, 152);

            // Heavy iliac/gluteal masses give the hindquarters a reptilian load-bearing silhouette.
            addSideMass(body, q + "_iliac_mass", left, 12.6F, 10.8F, 14.0F,
                    side * 9.5F, 1.6F, 34.0F, -0.05F, side * 0.05F, side * 0.06F, 6, 180);
            addSideMass(body, q + "_gluteal_mass", left, 11.4F, 10.0F, 13.0F,
                    side * 10.0F, 0.8F, 39.0F, 0.03F, side * 0.06F, side * 0.05F, 52, 180);
            addSideMass(body, q + "_tail_base_muscle", left, 8.5F, 6.5F, 12.0F,
                    side * 6.8F, 0.6F, 44.0F, 0.06F, side * 0.05F, side * 0.03F, 100, 180);
        }
    }

    private static void addEllipsoidMass(
            PartDefinition parent, String name,
            float halfWidth, float halfHeight, float length,
            float x, float y, float z, float xRot,
            int texX, int texY
    ) {
        /*
         * Step 5.8: a "mass" is a blueprint-locked sculptural volume, not one giant rectangular prism.
         * Five overlapping contour volumes approximate a rounded muscular/organic
         * cross-section while keeping the model strictly voxel-based.
         */
        PartDefinition mass = parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(texX, texY)
                        .addBox(-halfWidth * 0.68F, -halfHeight * 0.70F, -length * 0.50F,
                                halfWidth * 1.36F, halfHeight * 1.40F, length * 0.92F),
                PartPose.offsetAndRotation(x, y, z, xRot, 0.0F, 0.0F)
        );

        float sideW = halfWidth * 0.52F;
        float sideH = halfHeight * 1.05F;
        float sideL = length * 0.84F;
        mass.addOrReplaceChild(
                "contour_left",
                CubeListBuilder.create().texOffs((texX + 12) % 224, texY)
                        .addBox(-sideW * 0.5F, -sideH * 0.5F, -sideL * 0.5F, sideW, sideH, sideL),
                PartPose.offsetAndRotation(-halfWidth * 0.68F, 0.1F, 0.1F, 0.0F, -0.035F, 0.115F)
        );
        mass.addOrReplaceChild(
                "contour_right",
                CubeListBuilder.create().texOffs((texX + 24) % 224, texY)
                        .addBox(-sideW * 0.5F, -sideH * 0.5F, -sideL * 0.5F, sideW, sideH, sideL),
                PartPose.offsetAndRotation(halfWidth * 0.68F, 0.1F, 0.1F, 0.0F, 0.035F, -0.115F)
        );

        float capW = halfWidth * 1.24F;
        float capH = Math.max(0.9F, halfHeight * 0.34F);
        float capL = length * 0.82F;
        mass.addOrReplaceChild(
                "contour_dorsal",
                CubeListBuilder.create().texOffs((texX + 36) % 224, texY)
                        .addBox(-capW * 0.5F, -capH * 0.5F, -capL * 0.5F, capW, capH, capL),
                PartPose.offsetAndRotation(0.0F, -halfHeight * 0.70F, -0.15F, -0.03F, 0.0F, 0.0F)
        );
        mass.addOrReplaceChild(
                "contour_ventral",
                CubeListBuilder.create().texOffs((texX + 48) % 224, texY)
                        .addBox(-capW * 0.46F, -capH * 0.5F, -capL * 0.46F, capW * 0.92F, capH, capL * 0.92F),
                PartPose.offsetAndRotation(0.0F, halfHeight * 0.72F, 0.25F, 0.045F, 0.0F, 0.0F)
        );
    }

    private static void addSideMass(
            PartDefinition parent, String name, boolean left,
            float width, float height, float length,
            float x, float y, float z,
            float xRot, float yRot, float zRot,
            int texX, int texY
    ) {
        /*
         * Muscles are sculpted from an overlapping core + three contour lobes.
         * The outer contour lobe is deliberately larger than the inner one so
         * shoulders, thighs and flight muscles read as volume instead of boxes.
         */
        PartDefinition mass = parent.addOrReplaceChild(
                name,
                builder(texX, texY, left)
                        .addBox(-width * 0.36F, -height * 0.37F, -length * 0.46F,
                                width * 0.72F, height * 0.74F, length * 0.92F),
                PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot)
        );

        float side = left ? 1.0F : -1.0F;
        mass.addOrReplaceChild(
                "outer_bulge",
                builder((texX + 14) % 224, texY, left)
                        .addBox(-width * 0.22F, -height * 0.31F, -length * 0.39F,
                                width * 0.44F, height * 0.62F, length * 0.78F),
                PartPose.offsetAndRotation(side * width * 0.35F, -height * 0.02F, -length * 0.02F,
                        0.015F, side * -0.045F, side * -0.085F)
        );
        mass.addOrReplaceChild(
                "inner_bulge",
                builder((texX + 28) % 224, texY, left)
                        .addBox(-width * 0.18F, -height * 0.26F, -length * 0.35F,
                                width * 0.36F, height * 0.52F, length * 0.70F),
                PartPose.offsetAndRotation(side * -width * 0.28F, height * 0.05F, length * 0.05F,
                        -0.01F, side * 0.035F, side * 0.055F)
        );
        mass.addOrReplaceChild(
                "dorsal_bulge",
                builder((texX + 42) % 224, texY, left)
                        .addBox(-width * 0.27F, -height * 0.13F, -length * 0.34F,
                                width * 0.54F, height * 0.26F, length * 0.68F),
                PartPose.offsetAndRotation(side * width * 0.08F, -height * 0.40F, 0.0F,
                        -0.025F, side * -0.025F, side * -0.035F)
        );
    }

    /* ====================================================================== */
    /* NECK + HEAD — twelve visual masses, anterolateral eyes, tapered skull   */
    /* ====================================================================== */

    private static void createNeckAndHead(PartDefinition body) {
        /*
         * Blueprint landmarks:
         * neck base  ~= (0,-2.2,-16)
         * neck mid   ~= (0,-0.8,-38)
         * upper neck ~= (0, 0.6,-58)
         * skull      ~= (0, 1.2,-76)
         * nose tip   ~= (0, 2.0,-101)
         *
         * The chain deliberately projects forward and only gently rises/falls;
         * idle animation supplies a low S, never the old skyward giraffe pose.
         */
        PartDefinition neck01 = body.addOrReplaceChild(
                "neck_01", CubeListBuilder.create(), PartPose.offset(0.0F, -2.2F, -16.0F)
        );
        addNeckChain(neck01, 1, 5, 8.5F, 7.2F, 0.925F, -0.045F, 0, 194);
        addNeckSpines(neck01, 8.8F, new float[]{-1.0F, -5.5F, -10.0F, -14.5F, -19.0F});

        PartDefinition neck02 = neck01.addOrReplaceChild(
                "neck_02", CubeListBuilder.create(), PartPose.offset(0.0F, 0.9F, -21.0F)
        );
        addNeckChain(neck02, 6, 5, 6.2F, 5.3F, 0.920F, 0.015F, 76, 194);
        addNeckSpines(neck02, 6.8F, new float[]{-1.0F, -5.5F, -10.0F, -14.5F});

        PartDefinition neck03 = neck02.addOrReplaceChild(
                "neck_03", CubeListBuilder.create(), PartPose.offset(0.0F, 1.2F, -20.0F)
        );
        addNeckChain(neck03, 11, 5, 4.5F, 3.9F, 0.915F, 0.035F, 150, 194);
        addNeckSpines(neck03, 5.2F, new float[]{-1.0F, -5.5F, -10.0F, -14.5F});

        PartDefinition head = neck03.addOrReplaceChild(
                "head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, -18.8F)
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
            float z = -i * 4.75F;
            float y = i * 0.10F;
            float rot = baseRot + i * 0.012F;
            addEllipsoidMass(parent, "neck_mass_" + (startIndex + i), w, h, 6.4F,
                    0.0F, y, z, rot, (texX + i * 24) % 224, texY);
        }
    }

    private static void addNeckSpines(PartDefinition parent, float maxHeight, float[] zPositions) {
        for (int i = 0; i < zPositions.length; i++) {
            float h = maxHeight * (1.0F - i * 0.11F);
            parent.addOrReplaceChild(
                    "neck_spine_" + (i + 1),
                    CubeListBuilder.create().texOffs((208 + i * 9) % 246, 184)
                            .addBox(-0.72F, -h, -1.15F, 1.44F, h, 2.30F),
                    PartPose.offsetAndRotation(0.0F, -4.4F + i * 0.20F, zPositions[i], -0.24F, 0.0F, 0.0F)
            );
        }
    }

    private static void createHead(PartDefinition head) {
        /*
         * Low, predatory skull: broad occiput behind anterolateral eyes,
         * then a four-stage taper into a narrow snout. The jaw is independent.
         */
        addEllipsoidMass(head, "occipital_mass", 9.0F, 5.8F, 10.5F,
                0.0F, -0.4F, 2.4F, -0.045F, 0, 218);
        addEllipsoidMass(head, "temporal_mass", 8.5F, 5.2F, 9.5F,
                0.0F, -0.3F, -3.6F, -0.030F, 38, 218);
        addEllipsoidMass(head, "orbital_cranium", 7.7F, 4.6F, 9.0F,
                0.0F, -0.1F, -9.2F, -0.010F, 76, 218);
        addEllipsoidMass(head, "snout_root", 6.2F, 3.7F, 9.4F,
                0.0F, 0.35F, -14.9F, 0.018F, 112, 218);
        addEllipsoidMass(head, "snout_mid", 5.1F, 3.0F, 8.5F,
                0.0F, 0.55F, -20.5F, 0.016F, 148, 218);
        addEllipsoidMass(head, "muzzle", 4.0F, 2.4F, 7.5F,
                0.0F, 0.70F, -25.6F, 0.012F, 182, 218);
        addEllipsoidMass(head, "nose_bridge", 3.2F, 1.9F, 5.5F,
                0.0F, 0.75F, -29.6F, 0.010F, 208, 218);

        for (boolean left : new boolean[]{true, false}) {
            float side = left ? 1.0F : -1.0F;
            String q = left ? "left" : "right";
            addSideMass(head, q + "_jaw_adductor", left, 5.8F, 7.2F, 10.0F,
                    side * 6.5F, 1.1F, -5.0F, 0.02F, side * -0.12F, side * -0.11F, 0, 236);
            addSideMass(head, q + "_cheek_mass", left, 5.0F, 5.8F, 8.8F,
                    side * 6.5F, 1.4F, -10.6F, 0.025F, side * -0.12F, side * -0.09F, 30, 236);

            // Explicit anterolateral placement: eye sits beyond the cranium side wall, protected by brow.
            head.addOrReplaceChild(
                    q + "_eye_orbit",
                    builder(60, 236, left)
                            .addBox(-1.8F, -1.35F, -2.0F, 3.6F, 2.7F, 4.0F),
                    PartPose.offsetAndRotation(side * 7.6F, -2.35F, -10.7F,
                            -0.055F, side * -0.22F, side * -0.11F)
            );
            head.addOrReplaceChild(
                    q + "_eye",
                    builder(80, 236, left)
                            .addBox(-0.72F, -0.68F, -0.82F, 1.44F, 1.36F, 1.64F),
                    PartPose.offsetAndRotation(side * 8.75F, -2.30F, -11.6F,
                            -0.025F, side * -0.28F, 0.0F)
            );
            head.addOrReplaceChild(
                    q + "_brow_ridge",
                    builder(92, 236, left)
                            .addBox(-3.4F, -0.9F, -3.8F, 6.8F, 1.8F, 7.6F),
                    PartPose.offsetAndRotation(side * 4.8F, -4.45F, -10.9F,
                            -0.16F, side * -0.13F, side * -0.13F)
            );

            createHorn(head, left, true);
            createHorn(head, left, false);
        }

        PartDefinition jaw = head.addOrReplaceChild(
                "jaw", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 3.7F, -9.4F, 0.01F, 0.0F, 0.0F)
        );
        addEllipsoidMass(jaw, "jaw_hinge_mass", 6.8F, 2.9F, 9.5F,
                0.0F, 0.4F, 0.5F, -0.015F, 114, 236);
        addEllipsoidMass(jaw, "jaw_body", 5.9F, 2.35F, 11.5F,
                0.0F, 0.35F, -5.7F, -0.010F, 150, 236);
        addEllipsoidMass(jaw, "jaw_mid", 4.8F, 1.9F, 10.5F,
                0.0F, 0.30F, -12.3F, -0.018F, 188, 236);
        addEllipsoidMass(jaw, "jaw_tip", 3.6F, 1.4F, 7.0F,
                0.0F, 0.25F, -18.0F, -0.030F, 4, 246);

        // Crown/neck continuity: taller near occiput, then taper forward.
        float[] crownZ = {3.5F, -0.5F, -4.5F, -8.5F, -12.5F, -16.5F};
        float[] crownH = {9.0F, 8.5F, 7.7F, 6.7F, 5.6F, 4.4F};
        for (int i = 0; i < crownZ.length; i++) {
            head.addOrReplaceChild(
                    "crown_spine_" + (i + 1),
                    CubeListBuilder.create().texOffs(26 + i * 14, 246)
                            .addBox(-0.75F, -crownH[i], -1.1F, 1.5F, crownH[i], 2.2F),
                    PartPose.offsetAndRotation(0.0F, -4.0F + i * 0.18F, crownZ[i], -0.28F, 0.0F, 0.0F)
            );
        }
    }

    private static void createHorn(PartDefinition head, boolean left, boolean primary) {
        float side = left ? 1.0F : -1.0F;
        float len1 = primary ? 10.0F : 6.8F;
        float thick = primary ? 2.7F : 1.9F;
        String name = (left ? "left" : "right") + (primary ? "_horn" : "_horn_secondary");
        PartDefinition horn = head.addOrReplaceChild(
                name,
                builder(primary ? 110 : 136, 246, left)
                        .addBox(-thick * 0.5F, -thick * 0.5F, -0.7F, thick, thick, len1),
                PartPose.offsetAndRotation(side * (primary ? 5.8F : 6.2F),
                        primary ? -4.1F : -3.2F,
                        primary ? 2.4F : -4.5F,
                        primary ? -0.46F : -0.32F,
                        side * (primary ? 0.38F : 0.50F),
                        side * -0.10F)
        );
        float t2 = thick * 0.54F;
        horn.addOrReplaceChild(
                "tip",
                builder(primary ? 152 : 170, 246, left)
                        .addBox(-t2 * 0.5F, -t2 * 0.5F, 0.0F, t2, t2, len1 * 0.68F),
                PartPose.offsetAndRotation(0.0F, 0.0F, len1 - 0.9F,
                        -0.22F, side * 0.10F, side * -0.04F)
        );
    }

    /* ====================================================================== */
    /* WINGS — blueprint-locked chiropteran/dragon skeleton + web sectors     */
    /* ====================================================================== */

    private static void createWing(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String q = left ? "left" : "right";

        PartDefinition wingRoot = body.addOrReplaceChild(
                q + "_wing_root", CubeListBuilder.create(),
                PartPose.offset(side * 10.8F, -6.2F, -5.5F)
        );

        /*
         * Flight girdle: broad and muscular at the body, then rapidly transitions
         * into a stout humerus. This keeps the root volumetric without turning the
         * whole wing into a bird-like slab.
         */
        addSideMass(wingRoot, "wing_root_core", left, 14.5F, 11.0F, 13.5F,
                side * 1.7F, 0.3F, 0.5F, -0.08F, side * -0.06F, side * -0.08F, 0, 0);
        addSideMass(wingRoot, "wing_root_dorsal", left, 12.8F, 8.2F, 15.5F,
                side * 2.2F, -4.5F, 2.5F, -0.13F, side * -0.08F, side * -0.10F, 50, 0);
        addSideMass(wingRoot, "wing_root_pectoral", left, 10.8F, 7.4F, 12.8F,
                side * 2.8F, 4.4F, 1.8F, 0.06F, side * -0.05F, side * -0.08F, 96, 0);

        PartDefinition upperArm = wingRoot.addOrReplaceChild(
                "upper_arm", CubeListBuilder.create(),
                PartPose.offset(side * 2.5F, -0.2F, 0.0F)
        );
        addWingBone(upperArm, "humerus_proximal", left, 10.5F, 6.8F,
                0.0F, 0.0F, 0.0F, side * -0.075F, 132, 0);
        addWingBone(upperArm, "humerus_mid", left, 10.0F, 6.0F,
                side * 8.2F, -0.2F, 1.7F, side * -0.105F, 168, 0);
        addWingBone(upperArm, "humerus_distal", left, 9.0F, 5.2F,
                side * 16.0F, -0.35F, 3.8F, side * -0.135F, 202, 0);
        addSideMass(upperArm, "humerus_muscle", left, 16.0F, 8.2F, 9.0F,
                side * 9.6F, 0.2F, 2.0F, 0.0F, side * -0.04F, side * -0.04F, 2, 28);

        upperArm.addOrReplaceChild(
                "elbow_joint",
                builder(54, 28, left).addBox(-4.2F, -3.9F, -3.9F, 8.4F, 7.8F, 7.8F),
                PartPose.offset(side * 25.2F, -0.35F, 6.0F)
        );

        PartDefinition forearm = upperArm.addOrReplaceChild(
                "forearm", CubeListBuilder.create(),
                PartPose.offset(side * 24.7F, -0.3F, 5.6F)
        );
        addWingBone(forearm, "radius_ulna_proximal", left, 12.5F, 4.8F,
                0.0F, 0.0F, 0.0F, side * -0.115F, 88, 28);
        addWingBone(forearm, "radius_ulna_mid", left, 12.0F, 4.2F,
                side * 10.2F, -0.15F, 2.8F, side * -0.145F, 124, 28);
        addWingBone(forearm, "radius_ulna_distal", left, 11.0F, 3.6F,
                side * 20.0F, -0.25F, 5.8F, side * -0.175F, 158, 28);
        addSideMass(forearm, "forearm_muscle", left, 16.8F, 6.0F, 7.2F,
                side * 12.0F, 0.0F, 2.8F, 0.0F, side * -0.05F, side * -0.025F, 194, 28);

        forearm.addOrReplaceChild(
                "wrist_joint",
                builder(224, 28, left).addBox(-3.7F, -3.5F, -3.5F, 7.4F, 7.0F, 7.0F),
                PartPose.offset(side * 31.6F, -0.25F, 8.0F)
        );

        PartDefinition hand = forearm.addOrReplaceChild(
                "hand", CubeListBuilder.create(),
                PartPose.offset(side * 31.0F, -0.2F, 7.7F)
        );
        addWingBone(hand, "metacarpal_proximal", left, 9.0F, 3.3F,
                0.0F, 0.0F, 0.0F, side * -0.18F, 32, 46);
        addWingBone(hand, "metacarpal_distal", left, 8.0F, 2.8F,
                side * 7.2F, 0.0F, 2.5F, side * -0.21F, 62, 46);

        createWingDigits(hand, left);
        createDragonWingMembrane(wingRoot, upperArm, forearm, hand, left);

        // Short anterior thumb/hook, never a feather ray.
        hand.addOrReplaceChild(
                "wing_thumb",
                builder(94, 46, left).addBox(-0.9F, -0.9F, -0.4F, 1.8F, 1.8F, 7.0F),
                PartPose.offsetAndRotation(side * 6.0F, 0.6F, -0.8F,
                        -0.64F, side * 0.20F, side * -0.10F)
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
                        .addBox(left ? -0.7F : -length + 0.7F,
                                -thickness * 0.5F, -thickness * 0.5F,
                                length, thickness, thickness),
                PartPose.offsetAndRotation(x, y, z, 0.0F, yaw, 0.0F)
        );
    }

    private static void createWingDigits(PartDefinition hand, boolean left) {
        float side = left ? 1.0F : -1.0F;

        /*
         * Distal region dominates the semi-span, like a bat/dragon hand.
         * D1 is longest/most anterior, D3 is shorter and strongly posterior.
         */
        float[] baseLen = {27.0F, 23.0F, 19.0F};
        float[] midLen  = {22.0F, 18.0F, 15.0F};
        float[] tipLen  = {17.0F, 14.0F, 11.0F};
        float[] rootX   = { 8.0F,  7.0F,  6.0F};
        float[] rootZ   = { 4.0F, 14.0F, 25.0F};
        float[] yaw     = {-0.10F, -0.40F, -0.72F};
        float[] thick   = { 2.9F,  2.45F, 2.05F};

        for (int i = 0; i < 3; i++) {
            float t = thick[i];
            PartDefinition finger = hand.addOrReplaceChild(
                    "finger_" + (i + 1),
                    builder((i * 56) % 220, 60, left)
                            .addBox(left ? -0.55F : -baseLen[i] + 0.55F,
                                    -t * 0.5F, -t * 0.5F, baseLen[i], t, t),
                    PartPose.offsetAndRotation(side * rootX[i], 0.0F, rootZ[i],
                            0.010F + i * 0.012F, side * yaw[i], side * (0.014F + i * 0.016F))
            );

            float t2 = t * 0.68F;
            PartDefinition mid = finger.addOrReplaceChild(
                    "middle",
                    builder((i * 56 + 22) % 220, 60, left)
                            .addBox(left ? -0.40F : -midLen[i] + 0.40F,
                                    -t2 * 0.5F, -t2 * 0.5F, midLen[i], t2, t2),
                    PartPose.offsetAndRotation(side * (baseLen[i] - 0.8F), 0.0F, 0.0F,
                            0.0F, side * (-0.08F - i * 0.045F), side * (0.010F + i * 0.010F))
            );

            float t3 = Math.max(0.72F, t2 * 0.53F);
            mid.addOrReplaceChild(
                    "distal",
                    builder((i * 56 + 40) % 220, 60, left)
                            .addBox(left ? -0.28F : -tipLen[i] + 0.28F,
                                    -t3 * 0.5F, -t3 * 0.5F, tipLen[i], t3, t3),
                    PartPose.offsetAndRotation(side * (midLen[i] - 0.6F), 0.0F, 0.0F,
                            0.0F, side * (-0.10F - i * 0.045F), side * (0.008F + i * 0.010F))
            );
        }
    }

    private static void createDragonWingMembrane(
            PartDefinition wingRoot,
            PartDefinition upperArm,
            PartDefinition forearm,
            PartDefinition hand,
            boolean left
    ) {
        /*
         * STEP 5.8: membrane is no longer one giant wingRoot plane.
         * Every web lives on the nearest articulated bone parent so it follows
         * the same transforms as the skeleton. Bones sit at Y=0 and each web
         * crosses Y=-0.55..+0.55, embedding the skeleton inside the membrane.
         */

        // Body/root web: deep near the torso, tapering toward the humerus.
        addLinearMembraneSector(wingRoot, "body_web", left,
                0.0F, -1.0F, 0.00F,
                24.0F, 39.0F, 30.0F, 7, 4.0F, 86);

        // Brachial web follows the upper-arm parent instead of floating in root space.
        addLinearMembraneSector(upperArm, "arm_web", left,
                0.0F, -1.5F, -0.02F,
                25.0F, 46.0F, 38.0F, 7, 4.0F, 96);

        // Forearm web is the deepest central bay.
        addLinearMembraneSector(forearm, "forearm_web", left,
                0.0F, 0.5F, -0.03F,
                31.0F, 55.0F, 44.0F, 8, 4.0F, 108);

        /*
         * Three radial hand sectors create a bat-like planform with visible
         * scallops between digit directions. They overlap at the wrist but
         * diverge toward the tips instead of forming a smooth bird ellipse.
         */
        addRadialMembraneSector(hand, "d1_web", left,
                3.0F, 2.0F, -0.08F,
                56.0F, 48.0F, 24.0F, 11, 4.6F, 120);
        addRadialMembraneSector(hand, "d1_d2_web", left,
                5.0F, 10.0F, -0.28F,
                49.0F, 45.0F, 20.0F, 10, 4.6F, 132);
        addRadialMembraneSector(hand, "d2_d3_web", left,
                5.0F, 20.0F, -0.54F,
                42.0F, 39.0F, 16.0F, 9, 4.6F, 144);
        addRadialMembraneSector(hand, "rear_web", left,
                3.0F, 28.0F, -0.72F,
                34.0F, 31.0F, 10.0F, 8, 4.4F, 156);
    }

    private static void addLinearMembraneSector(
            PartDefinition parent, String name, boolean left,
            float startX, float startZ, float yaw,
            float length, float rootDepth, float tipDepth,
            int strips, float step, int texY
    ) {
        float side = left ? 1.0F : -1.0F;
        PartDefinition sector = parent.addOrReplaceChild(
                name, CubeListBuilder.create(),
                PartPose.offsetAndRotation(side * startX, 0.0F, startZ, 0.0F, side * yaw, 0.0F)
        );

        float stripWidth = step + 0.65F;
        for (int i = 0; i < strips; i++) {
            float t = strips <= 1 ? 0.0F : i / (float) (strips - 1);
            float x = Math.min(length, i * step);
            float depth = rootDepth + (tipDepth - rootDepth) * t;

            sector.addOrReplaceChild(
                    "strip_" + (i + 1),
                    builder((i * 15) % 220, texY, left)
                            .addBox(left ? x - 0.30F : -x - stripWidth + 0.30F,
                                    -0.55F, 0.0F,
                                    stripWidth, 1.10F, Math.max(7.0F, depth)),
                    PartPose.ZERO
            );
        }
    }

    private static void addRadialMembraneSector(
            PartDefinition parent, String name, boolean left,
            float startX, float startZ, float yaw,
            float length, float rootDepth, float tipDepth,
            int strips, float step, int texY
    ) {
        float side = left ? 1.0F : -1.0F;
        PartDefinition sector = parent.addOrReplaceChild(
                name, CubeListBuilder.create(),
                PartPose.offsetAndRotation(side * startX, 0.0F, startZ, 0.0F, side * yaw, 0.0F)
        );

        float stripWidth = step + 0.55F;
        for (int i = 0; i < strips; i++) {
            float t = strips <= 1 ? 0.0F : i / (float) (strips - 1);
            float x = Math.min(length, i * step);
            // Concave trailing edge: strong early depth, sharper distal taper.
            float curve = 1.0F - t * t;
            float depth = tipDepth + (rootDepth - tipDepth) * curve;

            sector.addOrReplaceChild(
                    "strip_" + (i + 1),
                    builder((i * 17) % 220, texY, left)
                            .addBox(left ? x - 0.28F : -x - stripWidth + 0.28F,
                                    -0.56F, 0.0F,
                                    stripWidth, 1.12F, Math.max(6.0F, depth)),
                    PartPose.ZERO
            );
        }
    }

    private static float sideSign(boolean left) {
        return left ? 1.0F : -1.0F;
    }

    /* ====================================================================== */
    /* FORELEGS — low, flexed reptilian chain with separated muscle masses     */
    /* ====================================================================== */

    private static void createForeleg(PartDefinition body, boolean left) {
        float side = sideSign(left);
        String name = left ? "left_foreleg" : "right_foreleg";
        PartDefinition leg = body.addOrReplaceChild(
                name, CubeListBuilder.create(),
                PartPose.offset(side * 9.3F, 2.6F, -10.5F)
        );

        addSideMass(leg, "shoulder_cap", left, 11.2F, 10.8F, 11.5F,
                0.0F, 0.0F, 0.0F, 0.04F, side * -0.04F, side * -0.06F, 0, 136);
        addSideMass(leg, "upper_arm_biceps", left, 8.2F, 10.8F, 8.0F,
                side * -0.7F, 5.2F, 2.8F, 0.20F, 0.0F, side * -0.035F, 40, 136);
        addSideMass(leg, "upper_arm_triceps", left, 7.7F, 10.0F, 7.7F,
                side * 0.9F, 7.9F, 0.4F, 0.25F, 0.0F, side * 0.045F, 74, 136);
        addSideMass(leg, "elbow_mass", left, 7.1F, 6.5F, 7.0F,
                0.0F, 13.2F, 4.8F, 0.02F, 0.0F, 0.0F, 108, 136);

        PartDefinition lower = leg.addOrReplaceChild(
                "lower_leg", CubeListBuilder.create(),
                PartPose.offset(0.0F, 12.8F, 4.7F)
        );
        addSideMass(lower, "forearm_proximal", left, 6.3F, 9.4F, 6.4F,
                side * 0.6F, 3.5F, -1.6F, -0.17F, 0.0F, side * 0.025F, 138, 136);
        addSideMass(lower, "forearm_distal", left, 5.4F, 8.2F, 5.6F,
                side * -0.4F, 8.6F, -4.4F, -0.26F, 0.0F, side * -0.025F, 168, 136);
        addSideMass(lower, "wrist_mass", left, 5.4F, 4.6F, 5.2F,
                0.0F, 13.0F, -6.2F, -0.10F, 0.0F, 0.0F, 198, 136);

        createFoot(lower, left, false);
    }

    /* ====================================================================== */
    /* HINDLEGS — strong hip/thigh + explicit S/Z digitigrade architecture     */
    /* ====================================================================== */

    private static void createHindleg(PartDefinition body, boolean left) {
        float side = sideSign(left);
        String name = left ? "left_hindleg" : "right_hindleg";
        PartDefinition leg = body.addOrReplaceChild(
                name, CubeListBuilder.create(),
                PartPose.offset(side * 9.6F, 1.8F, 35.0F)
        );

        addSideMass(leg, "hip_socket_mass", left, 12.8F, 11.5F, 12.0F,
                0.0F, 0.0F, 0.0F, -0.04F, 0.0F, side * 0.045F, 0, 158);
        addSideMass(leg, "gluteus_mass", left, 11.6F, 10.0F, 10.8F,
                side * 0.9F, 1.7F, 3.8F, 0.14F, 0.0F, side * 0.055F, 46, 158);
        addSideMass(leg, "thigh_proximal", left, 10.6F, 12.8F, 9.8F,
                0.0F, 5.2F, 4.5F, 0.36F, 0.0F, side * 0.035F, 88, 158);
        addSideMass(leg, "thigh_lateral", left, 8.9F, 11.4F, 9.0F,
                side * 1.0F, 9.5F, 6.5F, 0.41F, 0.0F, side * 0.035F, 126, 158);
        addSideMass(leg, "thigh_distal", left, 8.3F, 9.8F, 8.1F,
                0.0F, 13.3F, 8.8F, 0.45F, 0.0F, side * 0.025F, 160, 158);
        addSideMass(leg, "knee_mass", left, 7.3F, 6.7F, 7.2F,
                0.0F, 18.0F, 11.2F, 0.04F, 0.0F, 0.0F, 194, 158);

        PartDefinition lower = leg.addOrReplaceChild(
                "lower_leg", CubeListBuilder.create(),
                PartPose.offset(0.0F, 17.2F, 10.8F)
        );

        // Shin folds rearward; hock/tarsus reverses toward the ground.
        addSideMass(lower, "shin_proximal", left, 6.6F, 9.6F, 6.6F,
                0.0F, 3.3F, -2.8F, -0.32F, 0.0F, side * 0.012F, 222, 158);
        addSideMass(lower, "shin_distal", left, 5.7F, 8.2F, 5.7F,
                0.0F, 9.0F, -6.6F, -0.43F, 0.0F, side * 0.012F, 0, 182);
        addSideMass(lower, "calf_mass", left, 6.2F, 7.3F, 6.0F,
                side * 0.6F, 10.5F, -4.8F, -0.36F, 0.0F, side * 0.02F, 28, 182);
        addSideMass(lower, "hock_mass", left, 5.9F, 5.5F, 5.8F,
                0.0F, 14.4F, -9.7F, -0.16F, 0.0F, 0.0F, 58, 182);
        addSideMass(lower, "tarsus_proximal", left, 4.8F, 8.8F, 4.8F,
                0.0F, 18.8F, -8.8F, 0.33F, 0.0F, 0.0F, 84, 182);
        addSideMass(lower, "tarsus_distal", left, 4.0F, 7.2F, 4.1F,
                0.0F, 23.8F, -5.2F, 0.41F, 0.0F, 0.0F, 108, 182);

        createFoot(lower, left, true);
    }

    private static void createFoot(PartDefinition parent, boolean left, boolean hind) {
        float side = sideSign(left);
        float footY = hind ? 28.0F : 16.4F;
        float footZ = hind ? -1.5F : -6.2F;

        PartDefinition foot = parent.addOrReplaceChild(
                "foot", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, footY, footZ,
                        hind ? 0.06F : 0.035F, 0.0F, 0.0F)
        );

        addSideMass(foot, "heel_pad", left,
                hind ? 7.2F : 6.3F, 2.8F, hind ? 6.6F : 5.8F,
                0.0F, -0.4F, 1.7F, -0.045F, 0.0F, 0.0F, 132, 182);
        addSideMass(foot, "central_pad", left,
                hind ? 7.8F : 6.8F, 2.4F, hind ? 7.0F : 6.3F,
                0.0F, 0.2F, -2.0F, 0.025F, 0.0F, 0.0F, 162, 182);

        float spread = hind ? 3.7F : 3.1F;
        float proxLen = hind ? 8.0F : 6.9F;
        float distalLen = hind ? 6.7F : 5.8F;

        for (int i = -1; i <= 1; i++) {
            float yaw = i * -0.19F;
            PartDefinition toe = foot.addOrReplaceChild(
                    "toe_" + (i + 2),
                    builder(194 + (i + 1) * 18, 182, left)
                            .addBox(-1.10F, -0.80F, -proxLen, 2.20F, 1.60F, proxLen),
                    PartPose.offsetAndRotation(i * spread, 0.15F, -4.5F,
                            -0.055F, yaw, i * 0.028F)
            );
            PartDefinition distal = toe.addOrReplaceChild(
                    "distal",
                    builder((i + 1) * 18, 194, left)
                            .addBox(-0.82F, -0.60F, -distalLen, 1.64F, 1.20F, distalLen),
                    PartPose.offsetAndRotation(0.0F, 0.0F, -proxLen + 0.7F,
                            -0.105F, yaw * 0.33F, 0.0F)
            );
            distal.addOrReplaceChild(
                    "claw",
                    builder(60 + (i + 1) * 16, 194, left)
                            .addBox(-0.46F, -0.40F, -4.6F, 0.92F, 0.80F, 4.6F),
                    PartPose.offsetAndRotation(0.0F, 0.0F, -distalLen + 0.5F,
                            -0.28F, 0.0F, 0.0F)
            );
        }

        PartDefinition dew = foot.addOrReplaceChild(
                "dewclaw",
                builder(112, 194, left)
                        .addBox(-0.75F, -0.60F, -4.5F, 1.50F, 1.20F, 4.5F),
                PartPose.offsetAndRotation(side * -4.0F, -0.1F, -1.4F,
                        -0.12F, side * 0.44F, 0.0F)
        );
        dew.addOrReplaceChild(
                "claw",
                builder(132, 194, left)
                        .addBox(-0.42F, -0.36F, -3.2F, 0.84F, 0.72F, 3.2F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.26F, 0.0F, 0.0F)
        );
    }

    /* ====================================================================== */
    /* TAIL — eighteen overlapping masses with progressive vertical/lateral arc */
    /* ====================================================================== */

    private static void createTail(PartDefinition body) {
        // 20 visible taper masses + terminal tip, distributed over the four compatible tail bones.
        PartDefinition tail01 = body.addOrReplaceChild(
                "tail_01", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.3F, 44.0F, -0.055F, 0.020F, 0.0F)
        );
        addTailRange(tail01, 1, 5, 8.6F, 6.4F, 8.6F, 0.915F, 0, 202);
        addTailSpine(tail01, "spine_01", 6.4F, 6.5F, 132, 202);

        PartDefinition tail02 = tail01.addOrReplaceChild(
                "tail_02", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.7F, 32.5F, -0.025F, 0.022F, 0.0F)
        );
        addTailRange(tail02, 6, 5, 5.5F, 4.2F, 7.8F, 0.895F, 154, 202);
        addTailSpine(tail02, "spine_02", 4.7F, 6.0F, 28, 216);

        PartDefinition tail03 = tail02.addOrReplaceChild(
                "tail_03", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.7F, 29.0F, 0.015F, -0.018F, 0.0F)
        );
        addTailRange(tail03, 11, 5, 3.4F, 2.6F, 6.8F, 0.865F, 60, 216);

        PartDefinition tail04 = tail03.addOrReplaceChild(
                "tail_04", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.6F, 24.0F, 0.045F, -0.020F, 0.0F)
        );
        addTailRange(tail04, 16, 5, 1.9F, 1.45F, 5.8F, 0.82F, 116, 216);
        tail04.addOrReplaceChild(
                "tail_tip",
                CubeListBuilder.create().texOffs(170, 216)
                        .addBox(-0.34F, -0.34F, -0.4F, 0.68F, 0.68F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.1F, 23.0F, 0.045F, -0.025F, 0.0F)
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
