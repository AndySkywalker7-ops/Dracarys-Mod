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
 * BALANCED — Step 5.10 reference-locked final skull/head reconstruction.
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
        createBodySurfaceDetail(body);

        return LayerDefinition.create(mesh, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    /* ====================================================================== */
    /* TORSO — overlapping rib-cage spline with explicit muscular girdles      */
    /* ====================================================================== */

    private static PartDefinition createTorso(PartDefinition root) {
        /*
         * Step 5.9 anatomical blueprint:
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
         * STEP 5.9 organic voxel sculpt:
         * Never allow one cuboid to define the silhouette. The core is surrounded
         * by six smaller rotated contour lobes. From normal game distances they
         * read as one rounded/tapered anatomical mass rather than a rectangular box.
         */
        PartDefinition mass = parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(texX, texY)
                        .addBox(-halfWidth * 0.55F, -halfHeight * 0.57F, -length * 0.46F,
                                halfWidth * 1.10F, halfHeight * 1.14F, length * 0.92F),
                PartPose.offsetAndRotation(x, y, z, xRot, 0.0F, 0.0F)
        );

        float sideW = Math.max(0.85F, halfWidth * 0.42F);
        float sideH = Math.max(0.80F, halfHeight * 0.82F);
        float sideL = Math.max(1.2F, length * 0.80F);
        for (int sideIndex = -1; sideIndex <= 1; sideIndex += 2) {
            float side = sideIndex;
            mass.addOrReplaceChild(
                    side < 0 ? "contour_left" : "contour_right",
                    CubeListBuilder.create().texOffs((texX + (side < 0 ? 12 : 24)) % 224, texY)
                            .addBox(-sideW * 0.5F, -sideH * 0.5F, -sideL * 0.5F,
                                    sideW, sideH, sideL),
                    PartPose.offsetAndRotation(side * halfWidth * 0.60F, 0.15F, 0.0F,
                            0.0F, side * 0.045F, -side * 0.14F)
            );
        }

        float capW = Math.max(1.0F, halfWidth * 1.05F);
        float capH = Math.max(0.70F, halfHeight * 0.28F);
        float capL = Math.max(1.2F, length * 0.74F);
        mass.addOrReplaceChild(
                "contour_dorsal",
                CubeListBuilder.create().texOffs((texX + 36) % 224, texY)
                        .addBox(-capW * 0.5F, -capH * 0.5F, -capL * 0.5F, capW, capH, capL),
                PartPose.offsetAndRotation(0.0F, -halfHeight * 0.59F, -0.18F, -0.045F, 0.0F, 0.0F)
        );
        mass.addOrReplaceChild(
                "contour_ventral",
                CubeListBuilder.create().texOffs((texX + 48) % 224, texY)
                        .addBox(-capW * 0.44F, -capH * 0.5F, -capL * 0.44F,
                                capW * 0.88F, capH, capL * 0.88F),
                PartPose.offsetAndRotation(0.0F, halfHeight * 0.61F, 0.20F, 0.050F, 0.0F, 0.0F)
        );

        float diagW = Math.max(0.75F, halfWidth * 0.35F);
        float diagH = Math.max(0.65F, halfHeight * 0.35F);
        float diagL = Math.max(1.0F, length * 0.68F);
        mass.addOrReplaceChild(
                "contour_upper_left",
                CubeListBuilder.create().texOffs((texX + 60) % 224, texY)
                        .addBox(-diagW * 0.5F, -diagH * 0.5F, -diagL * 0.5F, diagW, diagH, diagL),
                PartPose.offsetAndRotation(-halfWidth * 0.47F, -halfHeight * 0.45F, -0.05F,
                        -0.025F, -0.035F, 0.22F)
        );
        mass.addOrReplaceChild(
                "contour_upper_right",
                CubeListBuilder.create().texOffs((texX + 72) % 224, texY)
                        .addBox(-diagW * 0.5F, -diagH * 0.5F, -diagL * 0.5F, diagW, diagH, diagL),
                PartPose.offsetAndRotation(halfWidth * 0.47F, -halfHeight * 0.45F, -0.05F,
                        -0.025F, 0.035F, -0.22F)
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
         * Muscle volumes use a narrow core plus four overlapping lobes. The lobes
         * rotate on different axes so thighs, shoulders and flight muscles read as
         * flesh wrapping a skeleton instead of stacked boxes.
         */
        PartDefinition mass = parent.addOrReplaceChild(
                name,
                builder(texX, texY, left)
                        .addBox(-width * 0.31F, -height * 0.33F, -length * 0.42F,
                                width * 0.62F, height * 0.66F, length * 0.84F),
                PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot)
        );

        float side = left ? 1.0F : -1.0F;
        mass.addOrReplaceChild(
                "outer_bulge",
                builder((texX + 14) % 224, texY, left)
                        .addBox(-width * 0.21F, -height * 0.28F, -length * 0.36F,
                                width * 0.42F, height * 0.56F, length * 0.72F),
                PartPose.offsetAndRotation(side * width * 0.34F, -height * 0.02F, -length * 0.03F,
                        0.035F, side * -0.060F, side * -0.11F)
        );
        mass.addOrReplaceChild(
                "inner_bulge",
                builder((texX + 28) % 224, texY, left)
                        .addBox(-width * 0.17F, -height * 0.23F, -length * 0.32F,
                                width * 0.34F, height * 0.46F, length * 0.64F),
                PartPose.offsetAndRotation(side * -width * 0.27F, height * 0.07F, length * 0.05F,
                        -0.025F, side * 0.050F, side * 0.075F)
        );
        mass.addOrReplaceChild(
                "dorsal_bulge",
                builder((texX + 42) % 224, texY, left)
                        .addBox(-width * 0.25F, -height * 0.12F, -length * 0.31F,
                                width * 0.50F, height * 0.24F, length * 0.62F),
                PartPose.offsetAndRotation(side * width * 0.07F, -height * 0.38F, -length * 0.02F,
                        -0.050F, side * -0.035F, side * -0.055F)
        );
        mass.addOrReplaceChild(
                "ventral_transition",
                builder((texX + 54) % 224, texY, left)
                        .addBox(-width * 0.22F, -height * 0.10F, -length * 0.28F,
                                width * 0.44F, height * 0.20F, length * 0.56F),
                PartPose.offsetAndRotation(side * -width * 0.03F, height * 0.37F, length * 0.07F,
                        0.055F, side * 0.025F, side * 0.040F)
        );
    }

    /* ====================================================================== */
    /* NECK + HEAD — neck preserved; skull rebuilt from approved turnaround      */
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
         * STEP 5.10 REFERENCE-LOCKED HEAD.
         *
         * Local coordinate convention:
         * X = lateral, +left / -right
         * Y = down
         * Z = rearward; negative values project toward the snout
         *
         * The approved turnaround sheet is the authority. The skull is built as
         * overlapping wedges/masses instead of one decorated box. Overall head
         * size intentionally stays compact relative to the body.
         */

        // Primary skull spline: broad posterior cranium -> narrow low muzzle.
        addEllipsoidMass(head, "occipital_mass", 6.10F, 4.45F, 6.8F,
                0.0F, -0.35F, 2.1F, -0.070F, 0, 212);
        addEllipsoidMass(head, "rear_skull", 5.95F, 4.15F, 6.4F,
                0.0F, -0.40F, -1.1F, -0.060F, 30, 212);
        addEllipsoidMass(head, "main_cranium", 5.65F, 3.80F, 6.4F,
                0.0F, -0.38F, -4.4F, -0.042F, 60, 212);
        addEllipsoidMass(head, "front_cranium", 5.15F, 3.35F, 5.7F,
                0.0F, -0.20F, -7.4F, -0.018F, 90, 212);

        // Tapered wedge snout. Each mass is shorter, lower and narrower toward the nose.
        addEllipsoidMass(head, "snout_root", 4.30F, 2.65F, 5.8F,
                0.0F, 0.25F, -10.3F, 0.020F, 120, 212);
        addEllipsoidMass(head, "snout_middle", 3.55F, 2.10F, 5.4F,
                0.0F, 0.45F, -13.9F, 0.018F, 148, 212);
        addEllipsoidMass(head, "muzzle", 2.90F, 1.65F, 4.7F,
                0.0F, 0.58F, -17.2F, 0.014F, 176, 212);
        addEllipsoidMass(head, "nose_mass", 2.38F, 1.36F, 3.7F,
                0.0F, 0.62F, -20.0F, 0.010F, 202, 212);

        // Posterior jaw muscles, cheeks and real orbital structures.
        for (boolean left : new boolean[]{true, false}) {
            float side = sideSign(left);
            String q = left ? "left" : "right";

            addSideMass(head, q + "_temporal_mass", left, 4.8F, 4.7F, 6.6F,
                    side * 4.55F, -0.35F, -2.4F,
                    -0.030F, side * -0.11F, side * -0.10F, 2, 232);

            addSideMass(head, q + "_jaw_adductor", left, 4.4F, 5.2F, 6.8F,
                    side * 4.35F, 1.10F, -3.7F,
                    0.035F, side * -0.12F, side * -0.11F, 28, 232);

            addSideMass(head, q + "_cheek_mass", left, 3.8F, 4.25F, 5.9F,
                    side * 4.65F, 1.25F, -6.6F,
                    0.025F, side * -0.14F, side * -0.12F, 54, 232);

            addSideMass(head, q + "_orbital_mass", left, 3.3F, 3.25F, 4.8F,
                    side * 4.55F, -1.35F, -7.2F,
                    -0.070F, side * -0.17F, side * -0.13F, 80, 232);

            head.addOrReplaceChild(
                    q + "_brow_outer",
                    builder(110, 232, left)
                            .addBox(-2.15F, -0.46F, -2.10F, 4.30F, 0.92F, 4.20F),
                    PartPose.offsetAndRotation(side * 4.18F, -3.20F, -7.15F,
                            -0.18F, side * -0.17F, side * -0.18F)
            );
            head.addOrReplaceChild(
                    q + "_brow_inner",
                    builder(130, 232, left)
                            .addBox(-1.55F, -0.38F, -1.65F, 3.10F, 0.76F, 3.30F),
                    PartPose.offsetAndRotation(side * 2.25F, -3.05F, -8.25F,
                            -0.15F, side * -0.08F, side * -0.12F)
            );

            head.addOrReplaceChild(
                    q + "_eye",
                    builder(150, 232, left)
                            .addBox(-0.46F, -0.44F, -0.54F, 0.92F, 0.88F, 1.08F),
                    PartPose.offsetAndRotation(side * 5.25F, -1.75F, -7.85F,
                            -0.025F, side * -0.33F, 0.0F)
            );

            createHorn(head, left, true);
            createHorn(head, left, false);
            createTemporalHorn(head, left);
            createCheekSpikes(head, left);
            createSubmandibularSpikes(head, left);
        }

        PartDefinition upperJaw = head.addOrReplaceChild(
                "upper_jaw", CubeListBuilder.create(),
                PartPose.offset(0.0F, 1.45F, -7.6F)
        );
        addEllipsoidMass(upperJaw, "upper_jaw_rear", 4.65F, 1.55F, 6.8F,
                0.0F, 0.05F, 0.0F, -0.010F, 0, 246);
        addEllipsoidMass(upperJaw, "upper_jaw_mid", 3.65F, 1.25F, 7.2F,
                0.0F, 0.02F, -4.8F, -0.012F, 34, 246);
        addEllipsoidMass(upperJaw, "upper_jaw_front", 2.70F, 0.95F, 5.6F,
                0.0F, -0.02F, -9.4F, -0.015F, 68, 246);

        // Lower jaw pivots from the posterior hinge, not the middle of the muzzle.
        PartDefinition jaw = head.addOrReplaceChild(
                "jaw", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 2.92F, -4.15F, 0.006F, 0.0F, 0.0F)
        );
        addEllipsoidMass(jaw, "jaw_hinge_mass", 4.95F, 1.90F, 6.2F,
                0.0F, 0.25F, -0.4F, -0.025F, 100, 246);
        addEllipsoidMass(jaw, "jaw_body", 4.15F, 1.55F, 7.4F,
                0.0F, 0.28F, -4.1F, -0.020F, 132, 246);
        addEllipsoidMass(jaw, "jaw_mid", 3.25F, 1.22F, 6.7F,
                0.0F, 0.25F, -8.6F, -0.024F, 164, 246);
        addEllipsoidMass(jaw, "jaw_tip", 2.35F, 0.88F, 4.9F,
                0.0F, 0.18F, -12.4F, -0.032F, 196, 246);

        jaw.addOrReplaceChild(
                "tongue",
                CubeListBuilder.create().texOffs(8, 250)
                        .addBox(-2.15F, -0.28F, -10.8F, 4.30F, 0.56F, 10.9F),
                PartPose.offsetAndRotation(0.0F, -0.95F, -0.6F, -0.020F, 0.0F, 0.0F)
        );
        jaw.addOrReplaceChild(
                "oral_floor",
                CubeListBuilder.create().texOffs(56, 250)
                        .addBox(-2.55F, -0.24F, -11.3F, 5.10F, 0.48F, 11.8F),
                PartPose.offset(0.0F, -0.40F, -0.4F)
        );

        createTeeth(upperJaw, jaw);
        createNostrilRidges(head);
        createHeadCrown(head);
        createFacialScales(head);
        createCranialPlates(head);
    }


    private static void createHorn(PartDefinition head, boolean left, boolean primary) {
        float side = sideSign(left);
        String q = left ? "left" : "right";
        String name = q + (primary ? "_main_horn" : "_secondary_horn");

        float rootLen = primary ? 5.7F : 4.4F;
        float rootT = primary ? 1.75F : 1.28F;

        PartDefinition rootHorn = head.addOrReplaceChild(
                name,
                builder(primary ? 108 : 136, 246, left)
                        .addBox(-rootT * 0.5F, -rootT * 0.5F, -0.45F,
                                rootT, rootT, rootLen),
                PartPose.offsetAndRotation(
                        side * (primary ? 4.15F : 4.75F),
                        primary ? -3.55F : -2.85F,
                        primary ? 0.8F : -3.6F,
                        primary ? -0.43F : -0.34F,
                        side * (primary ? 0.38F : 0.50F),
                        side * (primary ? -0.12F : -0.10F)
                )
        );

        float t2 = rootT * 0.74F;
        float l2 = primary ? 5.1F : 3.7F;
        PartDefinition seg2 = rootHorn.addOrReplaceChild(
                "segment_02",
                builder(primary ? 152 : 172, 246, left)
                        .addBox(-t2 * 0.5F, -t2 * 0.5F, 0.0F, t2, t2, l2),
                PartPose.offsetAndRotation(0.0F, 0.0F, rootLen - 0.55F,
                        -0.20F, side * 0.10F, side * -0.035F)
        );

        float t3 = t2 * 0.68F;
        float l3 = primary ? 4.2F : 2.9F;
        PartDefinition seg3 = seg2.addOrReplaceChild(
                "segment_03",
                builder(primary ? 188 : 198, 246, left)
                        .addBox(-t3 * 0.5F, -t3 * 0.5F, 0.0F, t3, t3, l3),
                PartPose.offsetAndRotation(0.0F, 0.0F, l2 - 0.35F,
                        -0.16F, side * 0.07F, side * -0.025F)
        );

        float t4 = Math.max(0.34F, t3 * 0.58F);
        float l4 = primary ? 3.4F : 2.2F;
        seg3.addOrReplaceChild(
                "tip",
                builder(primary ? 216 : 228, 246, left)
                        .addBox(-t4 * 0.5F, -t4 * 0.5F, 0.0F, t4, t4, l4),
                PartPose.offsetAndRotation(0.0F, 0.0F, l3 - 0.22F,
                        -0.12F, side * 0.05F, 0.0F)
        );
    }


    private static void createTemporalHorn(PartDefinition head, boolean left) {
        float side = sideSign(left);
        String q = left ? "left" : "right";
        PartDefinition horn = head.addOrReplaceChild(
                q + "_temporal_horn",
                builder(194, 236, left)
                        .addBox(-0.58F, -0.58F, 0.0F, 1.16F, 1.16F, 4.6F),
                PartPose.offsetAndRotation(side * 5.15F, -1.05F, 0.1F,
                        -0.18F, side * 0.66F, side * -0.13F)
        );
        horn.addOrReplaceChild(
                "tip",
                builder(210, 236, left)
                        .addBox(-0.32F, -0.32F, 0.0F, 0.64F, 0.64F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.25F,
                        -0.11F, side * 0.08F, 0.0F)
        );
    }


    private static void createTeeth(PartDefinition upperJaw, PartDefinition lowerJaw) {
        for (boolean left : new boolean[]{true, false}) {
            float side = sideSign(left);
            String q = left ? "l" : "r";
            for (int i = 0; i < 6; i++) {
                float z = -1.2F - i * 1.75F;
                float x = side * (3.55F - i * 0.22F);
                float len = (i == 1 || i == 2) ? 1.55F : 1.18F;
                upperJaw.addOrReplaceChild(
                        q + "_upper_tooth_" + (i + 1),
                        builder((18 + i * 9) % 224, 252, left)
                                .addBox(-0.28F, 0.0F, -0.30F, 0.56F, len, 0.60F),
                        PartPose.offsetAndRotation(x, 0.86F, z,
                                0.06F, side * -0.04F, side * 0.08F)
                );

                lowerJaw.addOrReplaceChild(
                        q + "_lower_tooth_" + (i + 1),
                        builder((82 + i * 9) % 224, 252, left)
                                .addBox(-0.27F, -len, -0.29F, 0.54F, len, 0.58F),
                        PartPose.offsetAndRotation(side * (3.72F - i * 0.23F), -0.73F, -2.2F - i * 1.75F,
                                -0.04F, side * 0.035F, side * -0.07F)
                );
            }

            upperJaw.addOrReplaceChild(
                    q + "_upper_fang",
                    builder(146, 252, left)
                            .addBox(-0.34F, 0.0F, -0.36F, 0.68F, 2.15F, 0.72F),
                    PartPose.offsetAndRotation(side * 3.05F, 0.78F, -4.0F,
                            0.10F, side * -0.03F, side * 0.06F)
            );
            lowerJaw.addOrReplaceChild(
                    q + "_lower_fang",
                    builder(160, 252, left)
                            .addBox(-0.32F, -1.85F, -0.34F, 0.64F, 1.85F, 0.68F),
                    PartPose.offsetAndRotation(side * 3.12F, -0.62F, -4.6F,
                            -0.08F, side * 0.03F, side * -0.05F)
            );
        }

        for (int i = -1; i <= 1; i += 2) {
            upperJaw.addOrReplaceChild(
                    "upper_front_tooth_" + (i < 0 ? "l" : "r"),
                    CubeListBuilder.create().texOffs(176, 252)
                            .addBox(-0.26F, 0.0F, -0.28F, 0.52F, 1.25F, 0.56F),
                    PartPose.offset(i * 0.95F, 0.70F, -11.15F)
            );
            lowerJaw.addOrReplaceChild(
                    "lower_front_tooth_" + (i < 0 ? "l" : "r"),
                    CubeListBuilder.create().texOffs(184, 252)
                            .addBox(-0.24F, -1.10F, -0.26F, 0.48F, 1.10F, 0.52F),
                    PartPose.offset(i * 0.90F, -0.55F, -13.0F)
            );
        }
    }


    private static void createNostrilRidges(PartDefinition head) {
        for (boolean left : new boolean[]{true, false}) {
            float side = sideSign(left);
            String q = left ? "left" : "right";
            head.addOrReplaceChild(
                    q + "_nostril_ridge",
                    builder(206, 238, left)
                            .addBox(-0.72F, -0.24F, -0.90F, 1.44F, 0.48F, 1.80F),
                    PartPose.offsetAndRotation(side * 1.25F, -0.72F, -20.15F,
                            -0.13F, side * -0.10F, side * -0.04F)
            );
            head.addOrReplaceChild(
                    q + "_nasal_plate",
                    builder(220, 238, left)
                            .addBox(-0.92F, -0.20F, -1.25F, 1.84F, 0.40F, 2.50F),
                    PartPose.offsetAndRotation(side * 1.35F, -1.05F, -18.75F,
                            -0.10F, side * -0.08F, side * -0.05F)
            );
        }
    }


    private static void createCranialPlates(PartDefinition head) {
        float[] z = {-5.0F, -7.2F, -9.3F, -11.4F, -13.4F, -15.3F};
        float[] w = {4.2F, 4.0F, 3.7F, 3.35F, 3.0F, 2.65F};
        for (int i = 0; i < z.length; i++) {
            head.addOrReplaceChild(
                    "forehead_plate_" + (i + 1),
                    CubeListBuilder.create().texOffs((20 + i * 18) % 220, 240)
                            .addBox(-w[i] * 0.5F, -0.22F, -1.15F, w[i], 0.44F, 2.30F),
                    PartPose.offsetAndRotation(0.0F, -3.32F + i * 0.18F, z[i],
                            -0.07F + i * 0.010F, 0.0F, 0.0F)
            );
        }

        for (boolean left : new boolean[]{true, false}) {
            float side = sideSign(left);
            String q = left ? "l" : "r";
            for (int i = 0; i < 6; i++) {
                float width = 2.25F - i * 0.12F;
                head.addOrReplaceChild(
                        q + "_orbital_plate_" + (i + 1),
                        builder((128 + i * 14) % 224, 240, left)
                                .addBox(-width * 0.5F, -0.20F, -1.05F, width, 0.40F, 2.10F),
                        PartPose.offsetAndRotation(
                                side * (4.75F - i * 0.25F),
                                -2.15F + i * 0.42F,
                                -5.2F - i * 1.85F,
                                -0.05F,
                                side * -0.12F,
                                side * (0.22F - i * 0.02F)
                        )
                );
            }
        }
    }

    /* ====================================================================== */
    /* WINGS — chiropteran/dragon skeleton + continuous pleated web     */
    /* ====================================================================== */


    private static void createWing(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String q = left ? "left" : "right";

        PartDefinition wingRoot = body.addOrReplaceChild(
                q + "_wing_root", CubeListBuilder.create(),
                PartPose.offset(side * 10.2F, -6.0F, -5.0F)
        );

        addSideMass(wingRoot, "wing_root_core", left, 13.4F, 10.2F, 12.4F,
                side * 1.3F, 0.2F, 0.4F, -0.09F, side * -0.07F, side * -0.09F, 0, 0);
        addSideMass(wingRoot, "wing_root_dorsal", left, 11.6F, 7.7F, 14.0F,
                side * 1.8F, -4.1F, 2.2F, -0.14F, side * -0.09F, side * -0.12F, 46, 0);
        addSideMass(wingRoot, "wing_root_pectoral", left, 9.8F, 7.0F, 11.5F,
                side * 2.1F, 4.0F, 1.4F, 0.07F, side * -0.05F, side * -0.09F, 88, 0);

        PartDefinition upperArm = wingRoot.addOrReplaceChild(
                "upper_arm", CubeListBuilder.create(),
                PartPose.offset(side * 2.2F, -0.2F, 0.0F)
        );
        addWingBone(upperArm, "humerus_proximal", left, 9.5F, 6.3F,
                0.0F, 0.0F, 0.0F, side * -0.07F, 126, 0);
        addWingBone(upperArm, "humerus_mid", left, 9.0F, 5.6F,
                side * 7.4F, -0.2F, 1.6F, side * -0.10F, 160, 0);
        addWingBone(upperArm, "humerus_distal", left, 8.2F, 4.8F,
                side * 14.4F, -0.35F, 3.5F, side * -0.13F, 192, 0);
        addSideMass(upperArm, "humerus_muscle", left, 14.8F, 7.8F, 8.3F,
                side * 8.7F, 0.2F, 1.9F, 0.0F, side * -0.04F, side * -0.05F, 2, 28);

        upperArm.addOrReplaceChild(
                "elbow_joint",
                builder(50, 28, left).addBox(-3.7F, -3.5F, -3.5F, 7.4F, 7.0F, 7.0F),
                PartPose.offset(side * 22.8F, -0.35F, 5.3F)
        );

        PartDefinition forearm = upperArm.addOrReplaceChild(
                "forearm", CubeListBuilder.create(),
                PartPose.offset(side * 22.3F, -0.3F, 5.0F)
        );
        addWingBone(forearm, "radius_ulna_proximal", left, 12.8F, 4.5F,
                0.0F, 0.0F, 0.0F, side * -0.11F, 82, 28);
        addWingBone(forearm, "radius_ulna_mid", left, 12.2F, 3.9F,
                side * 10.3F, -0.15F, 2.6F, side * -0.145F, 118, 28);
        addWingBone(forearm, "radius_ulna_distal", left, 11.2F, 3.3F,
                side * 20.0F, -0.25F, 5.6F, side * -0.18F, 152, 28);
        addSideMass(forearm, "forearm_muscle", left, 15.2F, 5.5F, 6.8F,
                side * 11.5F, 0.0F, 2.6F, 0.0F, side * -0.05F, side * -0.03F, 188, 28);

        forearm.addOrReplaceChild(
                "wrist_joint",
                builder(220, 28, left).addBox(-3.3F, -3.1F, -3.1F, 6.6F, 6.2F, 6.2F),
                PartPose.offset(side * 31.4F, -0.25F, 7.8F)
        );

        PartDefinition hand = forearm.addOrReplaceChild(
                "hand", CubeListBuilder.create(),
                PartPose.offset(side * 30.8F, -0.2F, 7.5F)
        );
        addWingBone(hand, "metacarpal_proximal", left, 8.6F, 3.0F,
                0.0F, 0.0F, 0.0F, side * -0.18F, 30, 46);
        addWingBone(hand, "metacarpal_distal", left, 7.8F, 2.55F,
                side * 6.9F, 0.0F, 2.4F, side * -0.22F, 58, 46);

        createWingDigits(hand, left);
        createDragonWingMembrane(wingRoot, upperArm, forearm, hand, left);
        createWingFoldRibs(upperArm, forearm, hand, left);

        hand.addOrReplaceChild(
                "wing_thumb",
                builder(92, 46, left).addBox(-0.72F, -0.72F, -0.4F, 1.44F, 1.44F, 6.0F),
                PartPose.offsetAndRotation(side * 5.6F, 0.6F, -0.6F,
                        -0.70F, side * 0.24F, side * -0.12F)
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
        float[] baseLen = {29.0F, 26.0F, 22.5F, 18.5F};
        float[] midLen  = {23.0F, 20.0F, 17.0F, 13.8F};
        float[] tipLen  = {17.5F, 15.0F, 12.2F,  9.5F};
        float[] rootX   = { 7.4F,  6.8F,  6.1F,  5.2F};
        float[] rootZ   = { 2.8F, 10.5F, 18.8F, 27.0F};
        float[] yaw     = {-0.08F, -0.28F, -0.52F, -0.79F};
        float[] thick   = { 2.7F,  2.35F, 2.0F, 1.70F};

        for (int i = 0; i < 4; i++) {
            float t = thick[i];
            PartDefinition finger = hand.addOrReplaceChild(
                    "finger_" + (i + 1),
                    builder((i * 48) % 220, 60, left)
                            .addBox(left ? -0.50F : -baseLen[i] + 0.50F,
                                    -t * 0.5F, -t * 0.5F, baseLen[i], t, t),
                    PartPose.offsetAndRotation(side * rootX[i], 0.0F, rootZ[i],
                            0.012F + i * 0.014F, side * yaw[i], side * (0.012F + i * 0.014F))
            );

            float t2 = t * 0.66F;
            PartDefinition mid = finger.addOrReplaceChild(
                    "middle",
                    builder((i * 48 + 18) % 220, 60, left)
                            .addBox(left ? -0.36F : -midLen[i] + 0.36F,
                                    -t2 * 0.5F, -t2 * 0.5F, midLen[i], t2, t2),
                    PartPose.offsetAndRotation(side * (baseLen[i] - 0.7F), 0.0F, 0.0F,
                            0.0F, side * (-0.065F - i * 0.040F), side * (0.008F + i * 0.008F))
            );

            float t3 = Math.max(0.58F, t2 * 0.50F);
            PartDefinition distal = mid.addOrReplaceChild(
                    "distal",
                    builder((i * 48 + 34) % 220, 60, left)
                            .addBox(left ? -0.24F : -tipLen[i] + 0.24F,
                                    -t3 * 0.5F, -t3 * 0.5F, tipLen[i], t3, t3),
                    PartPose.offsetAndRotation(side * (midLen[i] - 0.5F), 0.0F, 0.0F,
                            0.0F, side * (-0.085F - i * 0.038F), side * (0.006F + i * 0.008F))
            );
            distal.addOrReplaceChild(
                    "finger_claw",
                    builder((i * 48 + 44) % 220, 60, left)
                            .addBox(-0.25F, -0.24F, -0.2F, 0.50F, 0.48F, 3.1F),
                    PartPose.offsetAndRotation(side * (tipLen[i] - 0.2F), 0.0F, 0.0F,
                            0.0F, side * -0.16F, side * 0.03F)
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
         * STEP 5.9 continuous pleated membrane.
         * Sectors deliberately overlap by several model units and use shallow
         * alternating pitch to create folds. There are no exposed gaps between
         * panels; the visible read is one connected skin stretched over bones.
         */
        addPleatedMembraneSector(wingRoot, "body_web", left,
                0.0F, -3.0F, -0.02F,
                25.0F, 42.0F, 34.0F, 9, 3.2F, 84, 0.060F);

        addPleatedMembraneSector(upperArm, "arm_web", left,
                -1.5F, -4.0F, -0.04F,
                28.0F, 48.0F, 40.0F, 10, 3.0F, 94, 0.070F);

        addPleatedMembraneSector(forearm, "forearm_web", left,
                -2.0F, -4.5F, -0.08F,
                35.0F, 57.0F, 46.0F, 12, 3.0F, 106, 0.075F);

        addPleatedRadialSector(hand, "digit_web_1", left,
                1.0F, -1.0F, -0.10F,
                61.0F, 51.0F, 27.0F, 14, 3.4F, 120, 0.085F);
        addPleatedRadialSector(hand, "digit_web_2", left,
                2.0F, 7.0F, -0.30F,
                57.0F, 48.0F, 23.0F, 13, 3.4F, 132, 0.090F);
        addPleatedRadialSector(hand, "digit_web_3", left,
                2.0F, 15.0F, -0.53F,
                50.0F, 43.0F, 18.0F, 12, 3.3F, 144, 0.095F);
        addPleatedRadialSector(hand, "digit_web_4", left,
                1.0F, 23.0F, -0.76F,
                42.0F, 36.0F, 11.0F, 10, 3.3F, 156, 0.100F);

        addMembraneBridge(upperArm, "elbow_web_bridge", left, 18.5F, -2.0F, 13.5F, 33.0F, -0.13F, 168);
        addMembraneBridge(forearm, "wrist_web_bridge", left, 23.0F, -1.0F, 14.0F, 38.0F, -0.20F, 176);
    }


    private static void addPleatedMembraneSector(
            PartDefinition parent, String name, boolean left,
            float startX, float startZ, float yaw,
            float length, float rootDepth, float tipDepth,
            int strips, float step, int texY, float fold
    ) {
        float side = left ? 1.0F : -1.0F;
        PartDefinition sector = parent.addOrReplaceChild(
                name, CubeListBuilder.create(),
                PartPose.offsetAndRotation(side * startX, 0.0F, startZ, 0.0F, side * yaw, 0.0F)
        );

        float stripWidth = step + 1.75F;
        for (int i = 0; i < strips; i++) {
            float t = strips <= 1 ? 0.0F : i / (float) (strips - 1);
            float x = Math.min(length, i * step);
            float smooth = t * t * (3.0F - 2.0F * t);
            float depth = rootDepth + (tipDepth - rootDepth) * smooth;
            float pleat = (i % 2 == 0 ? 1.0F : -1.0F) * fold * (0.35F + 0.65F * (1.0F - t));
            float lift = sinApprox(t * 3.1415927F) * 0.28F;

            sector.addOrReplaceChild(
                    "membrane_fold_" + (i + 1),
                    builder((i * 13) % 220, texY, left)
                            .addBox(left ? x - 0.45F : -x - stripWidth + 0.45F,
                                    -0.48F, 0.0F,
                                    stripWidth, 0.96F, Math.max(7.0F, depth)),
                    PartPose.offsetAndRotation(0.0F, lift, 0.0F, pleat, 0.0F, 0.0F)
            );
        }
    }


    private static void addPleatedRadialSector(
            PartDefinition parent, String name, boolean left,
            float startX, float startZ, float yaw,
            float length, float rootDepth, float tipDepth,
            int strips, float step, int texY, float fold
    ) {
        float side = left ? 1.0F : -1.0F;
        PartDefinition sector = parent.addOrReplaceChild(
                name, CubeListBuilder.create(),
                PartPose.offsetAndRotation(side * startX, 0.0F, startZ, 0.0F, side * yaw, 0.0F)
        );

        float stripWidth = step + 1.65F;
        for (int i = 0; i < strips; i++) {
            float t = strips <= 1 ? 0.0F : i / (float) (strips - 1);
            float x = Math.min(length, i * step);
            float concave = 1.0F - t * t;
            float depth = tipDepth + (rootDepth - tipDepth) * concave;
            float pleat = (i % 2 == 0 ? 1.0F : -1.0F) * fold * (0.30F + concave * 0.70F);
            float lift = sinApprox(t * 3.1415927F) * 0.34F;

            sector.addOrReplaceChild(
                    "membrane_fold_" + (i + 1),
                    builder((i * 17) % 220, texY, left)
                            .addBox(left ? x - 0.42F : -x - stripWidth + 0.42F,
                                    -0.50F, 0.0F,
                                    stripWidth, 1.00F, Math.max(6.0F, depth)),
                    PartPose.offsetAndRotation(0.0F, lift, 0.0F, pleat, 0.0F, side * (t - 0.5F) * 0.018F)
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
                PartPose.offset(side * 8.7F, 2.4F, -11.0F)
        );

        addSideMass(leg, "shoulder_cap", left, 9.2F, 9.4F, 10.0F,
                0.0F, 0.0F, 0.0F, 0.06F, side * -0.05F, side * -0.07F, 0, 136);
        addSideMass(leg, "upper_arm_flexor", left, 6.8F, 9.0F, 7.1F,
                side * -0.6F, 4.7F, 2.7F, 0.26F, 0.0F, side * -0.04F, 38, 136);
        addSideMass(leg, "upper_arm_extensor", left, 6.2F, 8.2F, 6.8F,
                side * 0.7F, 7.2F, 0.2F, 0.30F, 0.0F, side * 0.05F, 70, 136);
        addSideMass(leg, "elbow_mass", left, 5.6F, 5.2F, 5.9F,
                0.0F, 11.5F, 4.2F, 0.02F, 0.0F, 0.0F, 102, 136);

        PartDefinition lower = leg.addOrReplaceChild(
                "lower_leg", CubeListBuilder.create(),
                PartPose.offset(0.0F, 11.2F, 4.0F)
        );
        addSideMass(lower, "forearm_proximal", left, 5.2F, 8.2F, 5.5F,
                side * 0.5F, 3.2F, -1.5F, -0.20F, 0.0F, side * 0.03F, 132, 136);
        addSideMass(lower, "forearm_distal", left, 4.4F, 7.1F, 4.8F,
                side * -0.3F, 7.8F, -4.0F, -0.31F, 0.0F, side * -0.03F, 160, 136);
        addSideMass(lower, "wrist_mass", left, 4.5F, 3.9F, 4.5F,
                0.0F, 11.7F, -5.6F, -0.12F, 0.0F, 0.0F, 188, 136);

        createGraspingHand(lower, left);
    }

    /* ====================================================================== */
    /* HINDLEGS — strong hip/thigh + explicit S/Z digitigrade architecture     */
    /* ====================================================================== */


    private static void createHindleg(PartDefinition body, boolean left) {
        float side = sideSign(left);
        String name = left ? "left_hindleg" : "right_hindleg";
        PartDefinition leg = body.addOrReplaceChild(
                name, CubeListBuilder.create(),
                PartPose.offset(side * 9.8F, 1.6F, 35.5F)
        );

        addSideMass(leg, "hip_socket_mass", left, 14.2F, 12.6F, 13.2F,
                0.0F, 0.0F, 0.0F, -0.05F, 0.0F, side * 0.05F, 0, 158);
        addSideMass(leg, "gluteus_mass", left, 13.4F, 11.8F, 12.6F,
                side * 1.1F, 1.5F, 3.8F, 0.16F, 0.0F, side * 0.06F, 46, 158);
        addSideMass(leg, "thigh_proximal", left, 12.2F, 14.6F, 11.3F,
                0.0F, 5.2F, 4.7F, 0.39F, 0.0F, side * 0.04F, 88, 158);
        addSideMass(leg, "thigh_lateral", left, 10.7F, 13.0F, 10.3F,
                side * 1.2F, 9.6F, 6.8F, 0.45F, 0.0F, side * 0.04F, 126, 158);
        addSideMass(leg, "thigh_distal", left, 9.4F, 11.0F, 9.2F,
                0.0F, 13.7F, 9.1F, 0.49F, 0.0F, side * 0.03F, 160, 158);
        addSideMass(leg, "knee_mass", left, 7.8F, 7.2F, 7.8F,
                0.0F, 18.4F, 11.6F, 0.05F, 0.0F, 0.0F, 194, 158);

        PartDefinition lower = leg.addOrReplaceChild(
                "lower_leg", CubeListBuilder.create(),
                PartPose.offset(0.0F, 17.6F, 11.2F)
        );

        addSideMass(lower, "shin_proximal", left, 6.9F, 10.0F, 6.8F,
                0.0F, 3.2F, -2.8F, -0.35F, 0.0F, side * 0.015F, 222, 158);
        addSideMass(lower, "shin_distal", left, 5.9F, 8.5F, 5.9F,
                0.0F, 9.0F, -6.7F, -0.46F, 0.0F, side * 0.015F, 0, 182);
        addSideMass(lower, "calf_mass", left, 6.8F, 7.9F, 6.4F,
                side * 0.7F, 10.4F, -4.8F, -0.39F, 0.0F, side * 0.02F, 28, 182);
        addSideMass(lower, "hock_mass", left, 6.1F, 5.7F, 6.0F,
                0.0F, 14.4F, -9.8F, -0.17F, 0.0F, 0.0F, 58, 182);
        addSideMass(lower, "tarsus_proximal", left, 4.9F, 9.0F, 4.9F,
                0.0F, 18.8F, -8.8F, 0.35F, 0.0F, 0.0F, 84, 182);
        addSideMass(lower, "tarsus_distal", left, 4.1F, 7.4F, 4.2F,
                0.0F, 23.9F, -5.1F, 0.43F, 0.0F, 0.0F, 108, 182);

        createHindFoot(lower, left);
    }



    private static void createGraspingHand(PartDefinition parent, boolean left) {
        float side = sideSign(left);
        PartDefinition hand = parent.addOrReplaceChild(
                "foot", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 15.5F, -5.8F, -0.13F, 0.0F, 0.0F)
        );
        addSideMass(hand, "palm", left, 5.6F, 2.2F, 5.3F,
                0.0F, 0.0F, -1.5F, 0.02F, 0.0F, 0.0F, 140, 182);

        float[] x = {-2.7F, -0.9F, 0.9F, 2.7F};
        float[] yaw = {0.26F, 0.09F, -0.09F, -0.26F};
        for (int i = 0; i < 4; i++) {
            PartDefinition finger = hand.addOrReplaceChild(
                    "grasp_finger_" + (i + 1),
                    builder((i * 18) % 220, 206, left)
                            .addBox(-0.62F, -0.55F, -5.9F, 1.24F, 1.10F, 5.9F),
                    PartPose.offsetAndRotation(x[i], 0.1F, -3.4F, -0.18F, yaw[i], (i - 1.5F) * 0.025F)
            );
            PartDefinition tip = finger.addOrReplaceChild(
                    "hook",
                    builder((72 + i * 16) % 220, 206, left)
                            .addBox(-0.40F, -0.38F, -4.0F, 0.80F, 0.76F, 4.0F),
                    PartPose.offsetAndRotation(0.0F, 0.0F, -5.3F, -0.42F, yaw[i] * 0.28F, 0.0F)
            );
            tip.addOrReplaceChild(
                    "claw",
                    builder((136 + i * 14) % 220, 206, left)
                            .addBox(-0.25F, -0.24F, -2.7F, 0.50F, 0.48F, 2.7F),
                    PartPose.offsetAndRotation(0.0F, 0.0F, -3.6F, -0.45F, 0.0F, 0.0F)
            );
        }

        PartDefinition thumb = hand.addOrReplaceChild(
                "grasp_thumb",
                builder(196, 206, left)
                        .addBox(-0.55F, -0.50F, -4.4F, 1.10F, 1.00F, 4.4F),
                PartPose.offsetAndRotation(side * -3.2F, -0.2F, -1.4F, -0.26F, side * 0.52F, 0.0F)
        );
        thumb.addOrReplaceChild(
                "claw",
                builder(216, 206, left)
                        .addBox(-0.28F, -0.26F, -2.5F, 0.56F, 0.52F, 2.5F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -3.8F, -0.42F, 0.0F, 0.0F)
        );
    }

    private static void createHindFoot(PartDefinition parent, boolean left) {
        float side = sideSign(left);
        PartDefinition foot = parent.addOrReplaceChild(
                "foot", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 28.2F, -1.3F, 0.07F, 0.0F, 0.0F)
        );

        addSideMass(foot, "heel_pad", left, 7.5F, 2.9F, 6.8F,
                0.0F, -0.4F, 1.7F, -0.05F, 0.0F, 0.0F, 132, 182);
        addSideMass(foot, "central_pad", left, 8.1F, 2.5F, 7.3F,
                0.0F, 0.2F, -2.0F, 0.03F, 0.0F, 0.0F, 162, 182);

        float[] spread = {-4.0F, 0.0F, 4.0F};
        float[] yaw = {0.21F, 0.0F, -0.21F};
        for (int i = 0; i < 3; i++) {
            PartDefinition toe = foot.addOrReplaceChild(
                    "toe_" + (i + 1),
                    builder(194 + i * 18, 182, left)
                            .addBox(-1.05F, -0.78F, -8.2F, 2.10F, 1.56F, 8.2F),
                    PartPose.offsetAndRotation(spread[i], 0.12F, -4.6F,
                            -0.065F, yaw[i], (i - 1) * 0.025F)
            );
            PartDefinition distal = toe.addOrReplaceChild(
                    "distal",
                    builder(i * 18, 194, left)
                            .addBox(-0.78F, -0.58F, -6.8F, 1.56F, 1.16F, 6.8F),
                    PartPose.offsetAndRotation(0.0F, 0.0F, -7.4F, -0.12F, yaw[i] * 0.30F, 0.0F)
            );
            distal.addOrReplaceChild(
                    "claw",
                    builder(58 + i * 16, 194, left)
                            .addBox(-0.42F, -0.36F, -4.8F, 0.84F, 0.72F, 4.8F),
                    PartPose.offsetAndRotation(0.0F, 0.0F, -6.2F, -0.34F, 0.0F, 0.0F)
            );
        }

        PartDefinition dew = foot.addOrReplaceChild(
                "dewclaw",
                builder(112, 194, left)
                        .addBox(-0.70F, -0.55F, -4.2F, 1.40F, 1.10F, 4.2F),
                PartPose.offsetAndRotation(side * -4.2F, -0.2F, -1.2F,
                        -0.16F, side * 0.48F, 0.0F)
        );
        dew.addOrReplaceChild(
                "claw",
                builder(132, 194, left)
                        .addBox(-0.40F, -0.34F, -3.0F, 0.80F, 0.68F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -3.8F, -0.30F, 0.0F, 0.0F)
        );
    }

    /* ====================================================================== */
    /* TAIL — twenty overlapping masses with progressive vertical/lateral arc */
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
        float[] z = {-13.0F,-10.0F,-7.0F,-4.0F,-1.0F,2.2F,5.5F,9.0F,12.8F,16.8F,21.0F,25.5F,30.0F,34.5F,38.5F};
        float[] h = {  5.0F, 6.2F, 7.4F, 8.6F, 9.5F,10.0F,9.7F,9.1F,8.3F,7.5F,6.6F,5.7F,4.8F,3.9F,3.1F};
        for (int i = 0; i < z.length; i++) {
            float w = Math.max(0.75F, 1.65F - i * 0.045F);
            float depth = Math.max(1.2F, 2.25F - i * 0.035F);
            crest.addOrReplaceChild(
                    "spine_" + (i + 1),
                    CubeListBuilder.create().texOffs((i * 16) % 224, 228)
                            .addBox(-w * 0.5F, -h[i], -depth * 0.5F, w, h[i], depth),
                    PartPose.offsetAndRotation(0.0F, -6.4F + i * 0.13F, z[i],
                            -0.33F + i * 0.008F, 0.0F, (i % 2 == 0 ? 0.018F : -0.018F))
            );
        }
    }



    private static void createHeadCrown(PartDefinition head) {
        float[] z = {2.8F, 0.7F, -1.5F, -3.8F, -6.0F, -8.2F, -10.4F, -12.5F};
        float[] h = {5.4F, 6.2F, 6.7F, 6.3F, 5.7F, 4.9F, 4.0F, 3.1F};
        for (int i = 0; i < z.length; i++) {
            head.addOrReplaceChild(
                    "crown_spine_" + (i + 1),
                    CubeListBuilder.create().texOffs((12 + i * 14) % 224, 246)
                            .addBox(-0.46F, -h[i], -0.72F, 0.92F, h[i], 1.44F),
                    PartPose.offsetAndRotation(0.0F, -3.10F + i * 0.10F, z[i],
                            -0.31F + i * 0.012F, 0.0F,
                            (i % 2 == 0 ? 0.018F : -0.018F))
            );
        }

        for (boolean left : new boolean[]{true, false}) {
            float side = sideSign(left);
            String q = left ? "l" : "r";
            for (int i = 0; i < 6; i++) {
                float len = 4.9F - i * 0.42F;
                head.addOrReplaceChild(
                        q + "_crown_lateral_" + (i + 1),
                        builder((132 + i * 12) % 224, 246, left)
                                .addBox(-0.40F, -0.40F, 0.0F, 0.80F, 0.80F, len),
                        PartPose.offsetAndRotation(
                                side * (2.25F + i * 0.43F),
                                -3.20F + i * 0.26F,
                                0.6F - i * 2.15F,
                                -0.34F + i * 0.025F,
                                side * (0.32F + i * 0.055F),
                                side * -0.10F
                        )
                );
            }
        }
    }

    private static void createCheekSpikes(PartDefinition head, boolean left) {
        float side = sideSign(left);
        String q = left ? "left" : "right";
        float[] z = {-3.8F, -5.8F, -7.8F, -9.7F};
        for (int i = 0; i < z.length; i++) {
            float len = 4.0F - i * 0.55F;
            head.addOrReplaceChild(
                    q + "_cheek_spike_" + (i + 1),
                    builder((112 + i * 12) % 224, 246, left)
                            .addBox(-0.34F, -0.34F, 0.0F, 0.68F, 0.68F, len),
                    PartPose.offsetAndRotation(
                            side * (5.05F - i * 0.10F),
                            0.55F + i * 0.42F,
                            z[i],
                            0.17F,
                            side * (0.72F + i * 0.05F),
                            side * -0.12F
                    )
            );
        }
    }

    private static void createSubmandibularSpikes(PartDefinition head, boolean left) {
        float side = sideSign(left);
        String q = left ? "left" : "right";
        for (int i = 0; i < 3; i++) {
            float len = 2.8F - i * 0.45F;
            head.addOrReplaceChild(
                    q + "_submandibular_spike_" + (i + 1),
                    builder((164 + i * 11) % 224, 246, left)
                            .addBox(-0.30F, 0.0F, -0.28F, 0.60F, len, 0.56F),
                    PartPose.offsetAndRotation(
                            side * (3.75F - i * 0.40F),
                            2.95F,
                            -5.1F - i * 2.45F,
                            0.04F,
                            side * 0.04F,
                            side * 0.13F
                    )
            );
        }
    }

    private static void createFacialScales(PartDefinition head) {
        for (boolean left : new boolean[]{true, false}) {
            float side = sideSign(left);
            String q = left ? "l" : "r";

            for (int i = 0; i < 7; i++) {
                float w = 2.15F - i * 0.12F;
                head.addOrReplaceChild(
                        q + "_cheek_scale_" + (i + 1),
                        builder((i * 16) % 220, 232, left)
                                .addBox(-w * 0.5F, -0.22F, -1.05F, w, 0.44F, 2.10F),
                        PartPose.offsetAndRotation(
                                side * (4.80F - i * 0.18F),
                                -0.25F + i * 0.38F,
                                -2.7F - i * 2.25F,
                                -0.04F,
                                side * -0.10F,
                                side * (0.24F - i * 0.022F)
                        )
                );
            }

            for (int i = 0; i < 5; i++) {
                float w = 1.85F - i * 0.10F;
                head.addOrReplaceChild(
                        q + "_jaw_plate_" + (i + 1),
                        builder((120 + i * 17) % 220, 234, left)
                                .addBox(-w * 0.5F, -0.18F, -1.0F, w, 0.36F, 2.0F),
                        PartPose.offsetAndRotation(
                                side * (4.25F - i * 0.25F),
                                2.35F + i * 0.10F,
                                -5.2F - i * 2.45F,
                                0.08F,
                                side * -0.08F,
                                side * (0.18F - i * 0.018F)
                        )
                );
            }
        }
    }

    private static void createWingFoldRibs(
            PartDefinition upperArm, PartDefinition forearm, PartDefinition hand, boolean left
    ) {
        float side = sideSign(left);
        addFoldRib(upperArm, "brachial_fold", left, side * 6.0F, -0.62F, 5.0F, 23.0F, side * -0.10F, 184);
        addFoldRib(forearm, "antebrachial_fold_1", left, side * 7.0F, -0.62F, 8.0F, 31.0F, side * -0.16F, 194);
        addFoldRib(forearm, "antebrachial_fold_2", left, side * 17.0F, -0.60F, 10.0F, 28.0F, side * -0.24F, 202);
        addFoldRib(hand, "digital_fold_1", left, side * 8.0F, -0.58F, 8.0F, 43.0F, side * -0.18F, 210);
        addFoldRib(hand, "digital_fold_2", left, side * 7.0F, -0.56F, 17.0F, 38.0F, side * -0.43F, 218);
        addFoldRib(hand, "digital_fold_3", left, side * 6.0F, -0.54F, 25.0F, 32.0F, side * -0.69F, 226);
    }

    private static void addFoldRib(
            PartDefinition parent, String name, boolean left,
            float x, float y, float z, float length, float yaw, int texY
    ) {
        float side = sideSign(left);
        parent.addOrReplaceChild(
                name,
                builder(0, texY, left)
                        .addBox(left ? -0.35F : -length + 0.35F, -0.32F, -0.32F,
                                length, 0.64F, 0.64F),
                PartPose.offsetAndRotation(x, y, z, 0.02F, yaw, side * 0.018F)
        );
    }

    private static void addMembraneBridge(
            PartDefinition parent, String name, boolean left,
            float x, float z, float width, float depth, float yaw, int texY
    ) {
        float side = sideSign(left);
        parent.addOrReplaceChild(
                name,
                builder(0, texY, left)
                        .addBox(-width * 0.5F, -0.52F, -depth * 0.10F, width, 1.04F, depth),
                PartPose.offsetAndRotation(side * x, 0.0F, z, 0.045F, side * yaw, side * -0.02F)
        );
    }

    private static float sinApprox(float value) {
        return (float) Math.sin(value);
    }

    private static void createBodySurfaceDetail(PartDefinition body) {
        for (boolean left : new boolean[]{true, false}) {
            float side = sideSign(left);
            String q = left ? "left" : "right";
            for (int i = 0; i < 8; i++) {
                float w = 4.4F - i * 0.20F;
                body.addOrReplaceChild(
                        q + "_shoulder_scale_" + (i + 1),
                        builder((i * 18) % 220, 218, left)
                                .addBox(-w * 0.5F, -0.30F, -2.2F, w, 0.60F, 4.4F),
                        PartPose.offsetAndRotation(side * (9.6F + i * 0.28F),
                                -5.0F + i * 0.22F, -10.0F + i * 3.6F,
                                -0.12F, side * -0.06F, side * (0.28F - i * 0.025F))
                );
            }
            for (int i = 0; i < 6; i++) {
                float w = 4.1F - i * 0.20F;
                body.addOrReplaceChild(
                        q + "_hip_scale_" + (i + 1),
                        builder((120 + i * 16) % 220, 218, left)
                                .addBox(-w * 0.5F, -0.28F, -2.0F, w, 0.56F, 4.0F),
                        PartPose.offsetAndRotation(side * (9.0F + i * 0.18F),
                                -3.0F + i * 0.20F, 30.0F + i * 3.1F,
                                0.05F, side * 0.05F, side * (-0.24F + i * 0.028F))
                );
            }
        }

        for (int i = 0; i < 14; i++) {
            float width = 8.6F - Math.min(i, 10) * 0.28F;
            body.addOrReplaceChild(
                    "belly_scute_" + (i + 1),
                    CubeListBuilder.create().texOffs((i * 14) % 220, 240)
                            .addBox(-width * 0.5F, -0.22F, -1.8F, width, 0.44F, 3.6F),
                    PartPose.offsetAndRotation(0.0F, 7.0F + i * 0.10F, -8.0F + i * 3.5F,
                            0.04F, 0.0F, 0.0F)
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
