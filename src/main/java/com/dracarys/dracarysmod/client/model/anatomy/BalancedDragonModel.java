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
 * Anatomy 01 — BALANCED v2, Step 5.2 base-model fidelity pass.
 *
 * <p>This model deliberately preserves the compatibility bones consumed by
 * {@link AbstractDracarysDragonModel}: body, neck_01..03, head, jaw,
 * left/right_wing_root -> upper_arm -> forearm, four terrestrial legs and
 * tail_01..04. Visible child geometry is rebuilt around those anchors so the
 * silhouette can change substantially without touching entity/render systems.</p>
 *
 * <p>Core anatomical rule: four terrestrial legs + two independent wings.</p>
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
    /* TORSO                                                                 */
    /* --------------------------------------------------------------------- */

    private static PartDefinition createTorso(PartDefinition root) {
        /*
         * Y=24 is the model ground plane. Keeping body at -18 gives enough
         * vertical room for the articulated limbs without moving the entity.
         */
        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -18.0F, 1.0F)
        );

        PartDefinition thorax = body.addOrReplaceChild(
                "thorax",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -1.0F, -9.0F, 0.02F, 0.0F, 0.0F)
        );

        /* Wide upper ribcage, deeper central chest, narrower sternum. */
        thorax.addOrReplaceChild(
                "ribcage_upper",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-11.8F, -7.0F, -9.5F, 23.6F, 8.0F, 17.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.03F, 0.0F, 0.0F)
        );
        thorax.addOrReplaceChild(
                "ribcage_mid",
                CubeListBuilder.create().texOffs(84, 0)
                        .addBox(-10.2F, -4.2F, -10.0F, 20.4F, 9.2F, 18.0F),
                PartPose.offsetAndRotation(0.0F, 1.8F, 0.6F, 0.02F, 0.0F, 0.0F)
        );
        thorax.addOrReplaceChild(
                "sternum",
                CubeListBuilder.create().texOffs(0, 30)
                        .addBox(-6.2F, -1.4F, -8.5F, 12.4F, 5.6F, 15.0F),
                PartPose.offsetAndRotation(0.0F, 5.0F, -0.3F, 0.09F, 0.0F, 0.0F)
        );

        /* Pectoral masses separate front-leg shoulder from wing shoulder. */
        thorax.addOrReplaceChild(
                "left_pectoral",
                CubeListBuilder.create().texOffs(54, 30)
                        .addBox(-1.5F, -3.3F, -4.8F, 6.4F, 7.2F, 10.5F),
                PartPose.offsetAndRotation(8.8F, 2.7F, -4.8F, 0.03F, -0.12F, -0.13F)
        );
        thorax.addOrReplaceChild(
                "right_pectoral",
                CubeListBuilder.create().texOffs(54, 30).mirror()
                        .addBox(-4.9F, -3.3F, -4.8F, 6.4F, 7.2F, 10.5F),
                PartPose.offsetAndRotation(-8.8F, 2.7F, -4.8F, 0.03F, 0.12F, 0.13F)
        );

        /* Narrow waist/abdomen. */
        PartDefinition abdomen = body.addOrReplaceChild(
                "abdomen",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.2F, 6.0F, 0.04F, 0.0F, 0.0F)
        );
        abdomen.addOrReplaceChild(
                "abdomen_front",
                CubeListBuilder.create().texOffs(92, 30)
                        .addBox(-7.8F, -5.0F, -8.0F, 15.6F, 9.0F, 13.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.02F, 0.0F, 0.0F)
        );
        abdomen.addOrReplaceChild(
                "abdomen_rear",
                CubeListBuilder.create().texOffs(92, 52)
                        .addBox(-6.5F, -4.2F, -4.8F, 13.0F, 8.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 0.8F, 8.0F, -0.04F, 0.0F, 0.0F)
        );
        abdomen.addOrReplaceChild(
                "ventral_keel",
                CubeListBuilder.create().texOffs(138, 30)
                        .addBox(-4.8F, -1.0F, -5.5F, 9.6F, 3.6F, 11.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, 2.0F, 0.10F, 0.0F, 0.0F)
        );

        /* Pelvis widens again and begins the long tail transition. */
        PartDefinition pelvis = body.addOrReplaceChild(
                "pelvis",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 1.2F, 18.0F, -0.02F, 0.0F, 0.0F)
        );
        pelvis.addOrReplaceChild(
                "pelvis_core",
                CubeListBuilder.create().texOffs(154, 0)
                        .addBox(-9.2F, -5.8F, -6.5F, 18.4F, 10.8F, 13.0F),
                PartPose.ZERO
        );
        pelvis.addOrReplaceChild(
                "pelvis_rear",
                CubeListBuilder.create().texOffs(154, 24)
                        .addBox(-8.0F, -4.7F, -3.0F, 16.0F, 8.8F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, 0.03F, 0.0F, 0.0F)
        );
        pelvis.addOrReplaceChild(
                "tail_socket",
                CubeListBuilder.create().texOffs(154, 44)
                        .addBox(-6.8F, -4.0F, -1.5F, 13.6F, 7.6F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -0.2F, 13.0F, 0.04F, 0.0F, 0.0F)
        );

        createShoulderArchitecture(body);
        createDorsalSpines(body);
        return body;
    }

    private static void createShoulderArchitecture(PartDefinition body) {
        /* Wing shoulder: higher and slightly behind front-leg shoulder. */
        body.addOrReplaceChild(
                "left_wing_scapula",
                CubeListBuilder.create().texOffs(196, 0)
                        .addBox(-2.2F, -2.3F, -6.5F, 7.0F, 5.6F, 13.0F),
                PartPose.offsetAndRotation(8.8F, -5.8F, -7.0F, 0.02F, -0.18F, -0.20F)
        );
        body.addOrReplaceChild(
                "right_wing_scapula",
                CubeListBuilder.create().texOffs(196, 0).mirror()
                        .addBox(-4.8F, -2.3F, -6.5F, 7.0F, 5.6F, 13.0F),
                PartPose.offsetAndRotation(-8.8F, -5.8F, -7.0F, 0.02F, 0.18F, 0.20F)
        );

        /* Front-leg shoulder: lower and farther forward. */
        body.addOrReplaceChild(
                "left_front_shoulder_mass",
                CubeListBuilder.create().texOffs(196, 18)
                        .addBox(-2.7F, -3.0F, -4.5F, 6.5F, 7.0F, 9.0F),
                PartPose.offsetAndRotation(8.5F, 2.2F, -12.0F, 0.02F, -0.08F, -0.10F)
        );
        body.addOrReplaceChild(
                "right_front_shoulder_mass",
                CubeListBuilder.create().texOffs(196, 18).mirror()
                        .addBox(-3.8F, -3.0F, -4.5F, 6.5F, 7.0F, 9.0F),
                PartPose.offsetAndRotation(-8.5F, 2.2F, -12.0F, 0.02F, 0.08F, 0.10F)
        );
    }

    private static void createDorsalSpines(PartDefinition body) {
        PartDefinition ridge = body.addOrReplaceChild("dorsal_ridge", CubeListBuilder.create(), PartPose.ZERO);
        addSpine(ridge, "spine_01", -9.5F, -14.0F, 7.2F, 0);
        addSpine(ridge, "spine_02", -8.8F, -5.0F, 6.7F, 12);
        addSpine(ridge, "spine_03", -7.5F, 5.0F, 5.8F, 24);
        addSpine(ridge, "spine_04", -6.8F, 15.0F, 4.8F, 36);
        addSpine(ridge, "spine_05", -6.2F, 24.0F, 3.8F, 48);
    }

    private static void addSpine(PartDefinition parent, String name, float y, float z, float height, int texY) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(232, texY)
                        .addBox(-0.9F, -height, -1.6F, 1.8F, height, 3.2F),
                PartPose.offsetAndRotation(0.0F, y, z, -0.14F, 0.0F, 0.0F)
        );
    }

    /* --------------------------------------------------------------------- */
    /* NECK + HEAD                                                           */
    /* --------------------------------------------------------------------- */

    private static void createNeckAndHead(PartDefinition body) {
        /* Compatibility chain remains neck_01 -> neck_02 -> neck_03. */
        PartDefinition neck01 = body.addOrReplaceChild(
                "neck_01",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -4.0F, -18.5F, -0.08F, 0.0F, 0.0F)
        );
        addNeckMass(neck01, "neck_mass_01", 6.4F, 5.4F, 10.5F, 0.0F, -1.0F, -5.0F, 0, 84);
        addNeckMass(neck01, "neck_mass_02", 5.8F, 4.9F, 9.5F, 0.0F, -1.6F, -13.0F, 46, 84);
        addNeckSpines(neck01, 6.5F, -5.0F, -13.0F);

        PartDefinition neck02 = neck01.addOrReplaceChild(
                "neck_02",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -2.0F, -17.5F, -0.06F, 0.0F, 0.0F)
        );
        addNeckMass(neck02, "neck_mass_03", 5.1F, 4.3F, 9.0F, 0.0F, -1.0F, -4.5F, 88, 84);
        addNeckMass(neck02, "neck_mass_04", 4.5F, 3.8F, 8.2F, 0.0F, -1.6F, -11.7F, 128, 84);
        addNeckSpines(neck02, 5.2F, -4.0F, -11.5F);

        PartDefinition neck03 = neck02.addOrReplaceChild(
                "neck_03",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -1.0F, -15.5F, 0.02F, 0.0F, 0.0F)
        );
        addNeckMass(neck03, "neck_mass_05", 3.9F, 3.3F, 10.0F, 0.0F, -0.8F, -5.0F, 164, 84);
        addNeckSpines(neck03, 4.2F, -3.4F, -6.0F);

        PartDefinition head = neck03.addOrReplaceChild(
                "head",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -0.8F, -12.5F, 0.02F, 0.0F, 0.0F)
        );

        createHeadMasses(head);
        createHeadDetails(head);
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
            int texX,
            int texY
    ) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(texX, texY)
                        .addBox(-halfWidth, -halfHeight, -length * 0.5F, halfWidth * 2.0F, halfHeight * 2.0F, length),
                PartPose.offsetAndRotation(x, y, z, -0.08F, 0.0F, 0.0F)
        );
    }

    private static void addNeckSpines(PartDefinition parent, float height, float y, float z) {
        PartDefinition ridge = parent.addOrReplaceChild("neck_spine_ridge", CubeListBuilder.create(), PartPose.ZERO);
        ridge.addOrReplaceChild(
                "spine_a",
                CubeListBuilder.create().texOffs(224, 78)
                        .addBox(-0.75F, -height, -1.3F, 1.5F, height, 2.6F),
                PartPose.offsetAndRotation(0.0F, y, z, -0.18F, 0.0F, 0.0F)
        );
        ridge.addOrReplaceChild(
                "spine_b",
                CubeListBuilder.create().texOffs(232, 78)
                        .addBox(-0.65F, -(height - 1.0F), -1.2F, 1.3F, height - 1.0F, 2.4F),
                PartPose.offsetAndRotation(0.0F, y + 0.3F, z + 6.0F, -0.16F, 0.0F, 0.0F)
        );
    }

    private static void createHeadMasses(PartDefinition head) {
        /* Low, long and progressively narrower toward the snout. */
        head.addOrReplaceChild(
                "back_skull",
                CubeListBuilder.create().texOffs(0, 112)
                        .addBox(-7.2F, -4.3F, -5.5F, 14.4F, 8.6F, 9.5F),
                PartPose.offsetAndRotation(0.0F, -0.4F, 0.0F, -0.03F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "main_cranium",
                CubeListBuilder.create().texOffs(52, 112)
                        .addBox(-6.0F, -3.8F, -7.5F, 12.0F, 7.6F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -8.5F, 0.02F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "snout_base",
                CubeListBuilder.create().texOffs(98, 112)
                        .addBox(-4.8F, -3.0F, -7.0F, 9.6F, 6.0F, 9.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, -16.0F, 0.04F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "snout_mid",
                CubeListBuilder.create().texOffs(136, 112)
                        .addBox(-3.9F, -2.5F, -6.5F, 7.8F, 5.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 0.8F, -22.0F, 0.03F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "nose",
                CubeListBuilder.create().texOffs(168, 112)
                        .addBox(-3.0F, -2.0F, -5.5F, 6.0F, 4.0F, 6.5F),
                PartPose.offsetAndRotation(0.0F, 1.0F, -27.5F, 0.02F, 0.0F, 0.0F)
        );
    }

    private static void createHeadDetails(PartDefinition head) {
        head.addOrReplaceChild(
                "left_cheek",
                CubeListBuilder.create().texOffs(0, 132)
                        .addBox(-1.2F, -2.5F, -4.5F, 3.8F, 5.2F, 8.5F),
                PartPose.offsetAndRotation(5.2F, 1.0F, -8.5F, 0.02F, -0.10F, -0.08F)
        );
        head.addOrReplaceChild(
                "right_cheek",
                CubeListBuilder.create().texOffs(0, 132).mirror()
                        .addBox(-2.6F, -2.5F, -4.5F, 3.8F, 5.2F, 8.5F),
                PartPose.offsetAndRotation(-5.2F, 1.0F, -8.5F, 0.02F, 0.10F, 0.08F)
        );

        head.addOrReplaceChild(
                "left_brow",
                CubeListBuilder.create().texOffs(28, 132)
                        .addBox(-2.8F, -0.9F, -3.0F, 5.6F, 1.8F, 6.0F),
                PartPose.offsetAndRotation(3.0F, -3.8F, -10.5F, -0.10F, -0.12F, -0.08F)
        );
        head.addOrReplaceChild(
                "right_brow",
                CubeListBuilder.create().texOffs(28, 132).mirror()
                        .addBox(-2.8F, -0.9F, -3.0F, 5.6F, 1.8F, 6.0F),
                PartPose.offsetAndRotation(-3.0F, -3.8F, -10.5F, -0.10F, 0.12F, 0.08F)
        );

        /* Animated jaw remains a direct child named exactly "jaw". */
        PartDefinition jaw = head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create().texOffs(58, 132)
                        .addBox(-4.6F, -0.4F, -15.5F, 9.2F, 3.0F, 16.5F),
                PartPose.offsetAndRotation(0.0F, 3.1F, -11.5F, 0.01F, 0.0F, 0.0F)
        );
        jaw.addOrReplaceChild(
                "jaw_tip",
                CubeListBuilder.create().texOffs(108, 132)
                        .addBox(-3.4F, -0.2F, -6.0F, 6.8F, 2.4F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.4F, -14.0F, -0.03F, 0.0F, 0.0F)
        );
        jaw.addOrReplaceChild(
                "jaw_keel",
                CubeListBuilder.create().texOffs(140, 132)
                        .addBox(-2.9F, -0.5F, -5.0F, 5.8F, 1.8F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 2.3F, -6.5F, 0.12F, 0.0F, 0.0F)
        );

        createHorn(head, true, true);
        createHorn(head, false, true);
        createHorn(head, true, false);
        createHorn(head, false, false);

        PartDefinition crest = head.addOrReplaceChild("cranial_crest", CubeListBuilder.create(), PartPose.ZERO);
        addCranialSpine(crest, "crest_01", 0.0F, -3.5F, 1.0F, 6.5F);
        addCranialSpine(crest, "crest_02", 0.0F, -3.8F, -5.5F, 5.8F);
        addCranialSpine(crest, "crest_03", 0.0F, -3.8F, -11.5F, 4.6F);
    }

    private static void addCranialSpine(PartDefinition parent, String name, float x, float y, float z, float height) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(220, 132)
                        .addBox(-0.8F, -height, -1.4F, 1.6F, height, 2.8F),
                PartPose.offsetAndRotation(x, y, z, -0.16F, 0.0F, 0.0F)
        );
    }

    private static void createHorn(PartDefinition head, boolean left, boolean primary) {
        float side = left ? 1.0F : -1.0F;
        String name = primary
                ? (left ? "left_horn" : "right_horn")
                : (left ? "left_horn_secondary" : "right_horn_secondary");

        float x = side * (primary ? 5.4F : 5.8F);
        float y = primary ? -3.8F : -2.4F;
        float z = primary ? -1.0F : -7.5F;
        float rootThickness = primary ? 3.0F : 2.2F;
        float rootLength = primary ? 8.0F : 5.5F;
        float tipLength = primary ? 9.5F : 6.5F;

        PartDefinition horn = head.addOrReplaceChild(
                name,
                builder(primary ? 184 : 204, 132, left)
                        .addBox(-rootThickness * 0.5F, -rootThickness * 0.5F, -1.0F,
                                rootThickness, rootThickness, rootLength),
                PartPose.offsetAndRotation(
                        x, y, z,
                        primary ? -0.40F : -0.24F,
                        side * (primary ? 0.30F : 0.46F),
                        side * (primary ? -0.10F : -0.16F)
                )
        );

        float tipThickness = rootThickness * 0.55F;
        horn.addOrReplaceChild(
                "tip",
                builder(primary ? 184 : 204, 146, left)
                        .addBox(-tipThickness * 0.5F, -tipThickness * 0.5F, 0.0F,
                                tipThickness, tipThickness, tipLength),
                PartPose.offsetAndRotation(
                        0.0F, 0.0F, rootLength - 1.0F,
                        -0.12F, side * 0.14F, side * -0.05F
                )
        );
    }

    /* --------------------------------------------------------------------- */
    /* WINGS                                                                 */
    /* --------------------------------------------------------------------- */

    private static void createWing(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String rootName = left ? "left_wing_root" : "right_wing_root";

        /* Compatibility root. Higher and behind the foreleg shoulder. */
        PartDefinition wingRoot = body.addOrReplaceChild(
                rootName,
                CubeListBuilder.create(),
                PartPose.offset(side * 9.8F, -6.8F, -5.8F)
        );

        /* Visual shoulder mass; not reset by the animation contract. */
        wingRoot.addOrReplaceChild(
                "wing_shoulder",
                builder(0, 158, left)
                        .addBox(-4.0F, -3.5F, -4.5F, 8.0F, 7.0F, 9.0F),
                PartPose.offsetAndRotation(side * 3.2F, 0.0F, 0.8F, 0.02F, side * -0.12F, side * -0.06F)
        );

        /* Animated compatibility upper-arm anchor. */
        PartDefinition upperArm = wingRoot.addOrReplaceChild(
                "upper_arm",
                CubeListBuilder.create(),
                PartPose.offset(side * 5.5F, -0.4F, 1.5F)
        );
        addWingBone(
                upperArm,
                "humerus_01",
                left,
                17.0F,
                5.2F,
                0.0F,
                0.0F,
                0.0F,
                side * -0.12F,
                34,
                158
        );
        addWingBone(
                upperArm,
                "humerus_02",
                left,
                13.0F,
                4.3F,
                side * 15.0F,
                -0.2F,
                4.6F,
                side * -0.18F,
                78,
                158
        );
        upperArm.addOrReplaceChild(
                "elbow_joint",
                builder(116, 158, left)
                        .addBox(-3.2F, -3.0F, -3.0F, 6.4F, 6.0F, 6.0F),
                PartPose.offset(side * 27.0F, -0.5F, 8.5F)
        );

        /* Compatibility forearm anchor. Static offset sweeps it backward. */
        PartDefinition forearm = upperArm.addOrReplaceChild(
                "forearm",
                CubeListBuilder.create(),
                PartPose.offset(side * 27.0F, -0.8F, 8.5F)
        );
        addWingBone(
                forearm,
                "radius_ulna_01",
                left,
                19.0F,
                4.2F,
                0.0F,
                0.0F,
                0.0F,
                side * -0.12F,
                142,
                158
        );
        addWingBone(
                forearm,
                "radius_ulna_02",
                left,
                15.0F,
                3.4F,
                side * 17.0F,
                -0.2F,
                5.0F,
                side * -0.16F,
                188,
                158
        );
        forearm.addOrReplaceChild(
                "wrist_joint",
                builder(222, 158, left)
                        .addBox(-2.7F, -2.6F, -2.6F, 5.4F, 5.2F, 5.2F),
                PartPose.offset(side * 32.0F, -0.6F, 9.5F)
        );

        PartDefinition hand = forearm.addOrReplaceChild(
                "hand",
                CubeListBuilder.create(),
                PartPose.offset(side * 31.5F, -0.5F, 9.2F)
        );
        addWingBone(
                hand,
                "metacarpal",
                left,
                16.0F,
                3.0F,
                0.0F,
                0.0F,
                0.0F,
                side * -0.18F,
                0,
                178
        );

        createWingDigits(hand, left);
        createWingMembranes(wingRoot, upperArm, forearm, hand, left);

        /* Small thumb/claw, separate from the three dominant digits. */
        hand.addOrReplaceChild(
                "wing_thumb",
                builder(50, 178, left)
                        .addBox(-0.9F, -0.9F, -1.0F, 1.8F, 1.8F, 7.0F),
                PartPose.offsetAndRotation(
                        side * 9.0F, 0.4F, -1.0F,
                        -0.48F, side * 0.20F, side * -0.18F
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
            float yaw,
            int texX,
            int texY
    ) {
        parent.addOrReplaceChild(
                name,
                builder(texX, texY, left)
                        .addBox(
                                left ? 0.0F : -length,
                                -thickness * 0.5F,
                                -thickness * 0.5F,
                                length,
                                thickness,
                                thickness
                        ),
                PartPose.offsetAndRotation(x, y, z, 0.02F, yaw, 0.0F)
        );
    }

    private static void createWingDigits(PartDefinition hand, boolean left) {
        float side = left ? 1.0F : -1.0F;
        float[] lengths = {68.0F, 57.0F, 46.0F};
        float[] yaws = {0.18F, 0.34F, 0.52F};
        float[] rootZ = {4.0F, 10.5F, 17.0F};
        float[] thickness = {2.2F, 1.9F, 1.6F};

        for (int i = 0; i < 3; i++) {
            float length = lengths[i];
            float thick = thickness[i];
            PartDefinition digit = hand.addOrReplaceChild(
                    "finger_" + (i + 1),
                    builder(76 + i * 38, 178, left)
                            .addBox(
                                    left ? 0.0F : -length,
                                    -thick * 0.5F,
                                    -thick * 0.5F,
                                    length,
                                    thick,
                                    thick
                            ),
                    PartPose.offsetAndRotation(
                            side * 13.0F,
                            0.2F + i * 0.25F,
                            rootZ[i],
                            0.02F + i * 0.02F,
                            side * yaws[i],
                            side * (0.03F + i * 0.03F)
                    )
            );

            float tipLength = length * 0.22F;
            float tipThickness = Math.max(0.9F, thick * 0.58F);
            digit.addOrReplaceChild(
                    "tip",
                    builder(76 + i * 38, 190, left)
                            .addBox(
                                    left ? 0.0F : -tipLength,
                                    -tipThickness * 0.5F,
                                    -tipThickness * 0.5F,
                                    tipLength,
                                    tipThickness,
                                    tipThickness
                            ),
                    PartPose.offsetAndRotation(
                            side * (length * 0.84F),
                            0.0F,
                            0.0F,
                            0.01F,
                            side * (0.05F + i * 0.02F),
                            side * (0.02F + i * 0.02F)
                    )
            );
        }
    }

    /**
     * Thin stepped membrane masses. The panels are intentionally split by
     * anatomical region so the wing reads as bones supporting a web rather
     * than one rectangular slab.
     */
    private static void createWingMembranes(
            PartDefinition wingRoot,
            PartDefinition upperArm,
            PartDefinition forearm,
            PartDefinition hand,
            boolean left
    ) {
        float side = left ? 1.0F : -1.0F;

        addSteppedMembrane(wingRoot, "body_web", left, 20.0F, 11.0F, 6.0F,
                side * 1.0F, 1.2F, 4.0F, side * -0.10F, 0, 202);
        addSteppedMembrane(upperArm, "upper_web", left, 28.0F, 17.0F, 10.0F,
                0.0F, 1.0F, 2.0F, side * -0.12F, 62, 202);
        addSteppedMembrane(forearm, "forearm_web", left, 34.0F, 23.0F, 14.0F,
                0.0F, 0.8F, 2.0F, side * -0.14F, 130, 202);

        /* Three digit webs fan progressively farther backward. */
        addSteppedMembrane(hand, "digit_web_01", left, 55.0F, 23.0F, 13.0F,
                side * 10.0F, 0.8F, 4.0F, side * 0.16F, 0, 220);
        addSteppedMembrane(hand, "digit_web_02", left, 48.0F, 21.0F, 11.0F,
                side * 11.0F, 0.8F, 10.0F, side * 0.30F, 70, 220);
        addSteppedMembrane(hand, "digit_web_03", left, 39.0F, 18.0F, 8.0F,
                side * 12.0F, 0.8F, 16.0F, side * 0.46F, 134, 220);
    }

    private static void addSteppedMembrane(
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
                PartPose.offsetAndRotation(x, y, z, 0.03F, yaw, 0.0F)
        );

        final int steps = 5;
        float stepLength = length / steps;
        for (int i = 0; i < steps; i++) {
            float t = (i + 0.5F) / steps;
            float depth = rootDepth + (tipDepth - rootDepth) * t;
            float x0 = i * stepLength;
            membrane.addOrReplaceChild(
                    "panel_" + (i + 1),
                    builder(texX, texY, left)
                            .addBox(
                                    left ? x0 : -x0 - stepLength - 0.15F,
                                    0.0F,
                                    0.0F,
                                    stepLength + 0.25F,
                                    0.42F,
                                    depth
                            ),
                    PartPose.offsetAndRotation(0.0F, 0.0F, i * 0.75F, 0.0F, 0.0F, 0.0F)
            );
        }
    }

    /* --------------------------------------------------------------------- */
    /* FRONT LEGS                                                            */
    /* --------------------------------------------------------------------- */

    private static void createForeleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String name = left ? "left_foreleg" : "right_foreleg";

        PartDefinition leg = body.addOrReplaceChild(
                name,
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(side * 8.2F, 2.0F, -12.0F, -0.06F, 0.0F, side * -0.06F)
        );

        leg.addOrReplaceChild(
                "shoulder",
                builder(0, 52, left)
                        .addBox(-4.4F, -4.0F, -4.5F, 8.8F, 9.0F, 9.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.03F, 0.0F, 0.0F)
        );
        leg.addOrReplaceChild(
                "upper_arm_visual",
                builder(38, 52, left)
                        .addBox(-3.4F, 0.0F, -3.2F, 6.8F, 13.0F, 6.4F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.2F, 0.10F, 0.0F, side * 0.02F)
        );
        leg.addOrReplaceChild(
                "elbow_mass",
                builder(66, 52, left)
                        .addBox(-3.1F, -2.5F, -2.9F, 6.2F, 5.0F, 5.8F),
                PartPose.offset(0.0F, 15.0F, 1.5F)
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 15.0F, 1.8F, 0.14F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "forearm_upper",
                builder(92, 52, left)
                        .addBox(-2.9F, 0.0F, -2.6F, 5.8F, 8.5F, 5.2F),
                PartPose.ZERO
        );
        lowerLeg.addOrReplaceChild(
                "forearm_lower",
                builder(116, 52, left)
                        .addBox(-2.4F, 0.0F, -2.2F, 4.8F, 7.0F, 4.4F),
                PartPose.offsetAndRotation(0.0F, 7.5F, -0.4F, -0.08F, 0.0F, 0.0F)
        );

        PartDefinition wrist = lowerLeg.addOrReplaceChild(
                "wrist",
                builder(138, 52, left)
                        .addBox(-2.2F, -1.8F, -2.2F, 4.4F, 4.6F, 4.4F),
                PartPose.offsetAndRotation(0.0F, 13.0F, -1.2F, -0.30F, 0.0F, 0.0F)
        );

        createFoot(wrist, left, false);
    }

    /* --------------------------------------------------------------------- */
    /* HIND LEGS                                                             */
    /* --------------------------------------------------------------------- */

    private static void createHindleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String name = left ? "left_hindleg" : "right_hindleg";

        PartDefinition leg = body.addOrReplaceChild(
                name,
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(side * 7.6F, 2.0F, 17.0F, 0.05F, 0.0F, side * -0.04F)
        );

        leg.addOrReplaceChild(
                "hip_mass",
                builder(0, 68, left)
                        .addBox(-5.5F, -4.5F, -5.5F, 11.0F, 10.0F, 11.0F),
                PartPose.ZERO
        );
        leg.addOrReplaceChild(
                "thigh",
                builder(46, 68, left)
                        .addBox(-4.5F, 0.0F, -4.2F, 9.0F, 14.0F, 8.4F),
                PartPose.offsetAndRotation(0.0F, 4.5F, 1.0F, -0.16F, 0.0F, side * 0.03F)
        );
        leg.addOrReplaceChild(
                "knee_mass",
                builder(84, 68, left)
                        .addBox(-3.8F, -3.0F, -3.6F, 7.6F, 6.0F, 7.2F),
                PartPose.offset(0.0F, 15.0F, 4.0F)
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 14.0F, 4.3F, 0.22F, 0.0F, 0.0F)
        );
        lowerLeg.addOrReplaceChild(
                "shin_upper",
                builder(116, 68, left)
                        .addBox(-3.3F, 0.0F, -3.2F, 6.6F, 9.0F, 6.4F),
                PartPose.ZERO
        );
        lowerLeg.addOrReplaceChild(
                "tarsus",
                builder(144, 68, left)
                        .addBox(-2.7F, 0.0F, -2.6F, 5.4F, 8.5F, 5.2F),
                PartPose.offsetAndRotation(0.0F, 8.0F, -1.0F, -0.40F, 0.0F, 0.0F)
        );

        PartDefinition ankle = lowerLeg.addOrReplaceChild(
                "ankle",
                builder(168, 68, left)
                        .addBox(-2.4F, -1.8F, -2.4F, 4.8F, 4.8F, 4.8F),
                PartPose.offsetAndRotation(0.0F, 14.0F, -3.6F, -0.52F, 0.0F, 0.0F)
        );

        createFoot(ankle, left, true);
    }

    private static void createFoot(PartDefinition parent, boolean left, boolean hind) {
        float width = hind ? 10.5F : 9.0F;
        float length = hind ? 12.5F : 10.5F;
        float y = hind ? 10.5F : 11.0F;
        float z = hind ? -4.0F : -3.0F;

        PartDefinition foot = parent.addOrReplaceChild(
                "foot",
                builder(hind ? 194 : 0, 68, left)
                        .addBox(-width * 0.5F, -1.6F, -length + 3.0F, width, 3.2F, length),
                PartPose.offsetAndRotation(0.0F, y, z, hind ? 0.16F : 0.10F, 0.0F, 0.0F)
        );

        float spread = hind ? 3.2F : 2.7F;
        float toeLength = hind ? 8.2F : 7.2F;
        for (int i = -1; i <= 1; i++) {
            PartDefinition toe = foot.addOrReplaceChild(
                    "toe_" + (i + 2),
                    builder(hind ? 0 : 36, 80, left)
                            .addBox(-1.0F, -0.8F, -toeLength, 2.0F, 1.6F, toeLength),
                    PartPose.offsetAndRotation(
                            i * spread,
                            0.35F,
                            -length + 4.0F,
                            -0.04F,
                            i * -0.08F,
                            0.0F
                    )
            );
            toe.addOrReplaceChild(
                    "claw",
                    builder(68, 80, left)
                            .addBox(-0.6F, -0.5F, -4.2F, 1.2F, 1.0F, 4.2F),
                    PartPose.offsetAndRotation(0.0F, 0.15F, -toeLength + 0.8F, -0.18F, 0.0F, 0.0F)
            );
        }

        foot.addOrReplaceChild(
                "outer_toe",
                builder(92, 80, left)
                        .addBox(-0.85F, -0.75F, -6.0F, 1.7F, 1.5F, 6.0F),
                PartPose.offsetAndRotation(
                        (left ? 1.0F : -1.0F) * width * 0.35F,
                        0.4F,
                        -length + 5.0F,
                        -0.03F,
                        (left ? 1.0F : -1.0F) * 0.18F,
                        0.0F
                )
        );
        foot.addOrReplaceChild(
                "heel_pad",
                builder(116, 80, left)
                        .addBox(-2.2F, -0.9F, -1.5F, 4.4F, 1.8F, 3.8F),
                PartPose.offset(0.0F, 0.35F, 1.0F)
        );
    }

    /* --------------------------------------------------------------------- */
    /* TAIL — 8 VISIBLE MASSES, 4 COMPATIBILITY BONES                       */
    /* --------------------------------------------------------------------- */

    private static void createTail(PartDefinition body) {
        PartDefinition tail01 = body.addOrReplaceChild(
                "tail_01",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.5F, 28.0F, -0.02F, 0.0F, 0.0F)
        );
        addTailMass(tail01, "tail_mass_01", 6.5F, 5.4F, 12.0F, 0.0F, 0.0F, 5.5F, 0, 92);
        addTailMass(tail01, "tail_mass_02", 5.8F, 4.8F, 11.0F, 0.0F, 0.1F, 15.5F, 48, 92);
        addTailSpine(tail01, "spine_01", 5.5F, 7.0F);

        PartDefinition tail02 = tail01.addOrReplaceChild(
                "tail_02",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.1F, 25.0F, 0.03F, 0.0F, 0.0F)
        );
        addTailMass(tail02, "tail_mass_03", 5.0F, 4.1F, 11.0F, 0.0F, 0.0F, 5.0F, 92, 92);
        addTailMass(tail02, "tail_mass_04", 4.4F, 3.6F, 10.0F, 0.0F, 0.1F, 14.5F, 134, 92);
        addTailSpine(tail02, "spine_02", 4.4F, 7.0F);

        PartDefinition tail03 = tail02.addOrReplaceChild(
                "tail_03",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.0F, 23.0F, 0.03F, 0.0F, 0.0F)
        );
        addTailMass(tail03, "tail_mass_05", 3.7F, 3.0F, 10.5F, 0.0F, 0.0F, 4.8F, 172, 92);
        addTailMass(tail03, "tail_mass_06", 3.0F, 2.5F, 9.5F, 0.0F, 0.1F, 13.8F, 208, 92);
        addTailSpine(tail03, "spine_03", 3.3F, 6.5F);

        PartDefinition tail04 = tail03.addOrReplaceChild(
                "tail_04",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.0F, 21.5F, 0.02F, 0.0F, 0.0F)
        );
        addTailMass(tail04, "tail_mass_07", 2.4F, 2.0F, 10.0F, 0.0F, 0.0F, 4.5F, 0, 104);
        addTailMass(tail04, "tail_mass_08", 1.6F, 1.4F, 10.0F, 0.0F, 0.1F, 13.5F, 34, 104);

        tail04.addOrReplaceChild(
                "terminal_spike",
                CubeListBuilder.create().texOffs(70, 104)
                        .addBox(-0.55F, -0.55F, 0.0F, 1.1F, 1.1F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 22.0F, 0.02F, 0.0F, 0.0F)
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
                        .addBox(-halfWidth, -halfHeight, -length * 0.5F,
                                halfWidth * 2.0F, halfHeight * 2.0F, length),
                PartPose.offsetAndRotation(x, y, z, 0.02F, 0.0F, 0.0F)
        );
    }

    private static void addTailSpine(PartDefinition parent, String name, float height, float z) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(96, 104)
                        .addBox(-0.75F, -height, -1.4F, 1.5F, height, 2.8F),
                PartPose.offsetAndRotation(0.0F, -3.8F, z, -0.12F, 0.0F, 0.0F)
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
