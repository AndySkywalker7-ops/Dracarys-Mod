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
 * Anatomy 01 — BALANCED v2, Step 5.1 refinement.
 *
 * <p>This revision keeps every compatibility bone consumed by
 * {@link AbstractDracarysDragonModel}, while rebuilding the visible anatomy
 * around stepped/tapered masses instead of long rectangular slabs.</p>
 *
 * <p>Main goals:</p>
 * <ul>
 *     <li>bat-inspired articulated wings with shoulder, arm, elbow, forearm,
 *     wrist/hand and four divergent digits;</li>
 *     <li>stable terrestrial grounding with feet/toes reaching the model
 *     ground plane;</li>
 *     <li>clear thorax -> waist -> pelvis mass distribution;</li>
 *     <li>less box-like skull, neck, limbs and tail;</li>
 *     <li>no changes to render scale, entity logic, multipart hitboxes or
 *     long-range rendering.</li>
 * </ul>
 */
public final class BalancedDragonModel<T extends DracarysDragonEntity>
        extends AbstractDracarysDragonModel<T> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            DracarysMod.id("dracarys_dragon_balanced"),
            "main"
    );

    private static final int TEXTURE_SIZE = 256;
    private static final int MEMBRANE_STEPS = 4;

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
     * Three visibly different trunk masses. The chest is intentionally the
     * dominant structure, the abdomen narrows, and the pelvis widens again.
     */
    private static PartDefinition createTorso(PartDefinition root) {
        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -18.0F, 1.0F)
        );

        PartDefinition thorax = body.addOrReplaceChild(
                "thorax",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-10.5F, -8.0F, -11.0F, 21.0F, 12.0F, 18.0F)
                        .texOffs(84, 0)
                        .addBox(-12.5F, -5.0F, -8.0F, 25.0F, 9.0F, 14.0F)
                        .texOffs(0, 32)
                        .addBox(-8.5F, 3.0F, -9.0F, 17.0F, 7.0F, 15.0F),
                PartPose.offset(0.0F, -1.0F, -10.0F)
        );

        thorax.addOrReplaceChild(
                "sternum",
                CubeListBuilder.create()
                        .texOffs(68, 32)
                        .addBox(-5.5F, -1.5F, -7.0F, 11.0F, 4.0F, 13.0F),
                PartPose.offsetAndRotation(0.0F, 7.5F, -1.0F, 0.08F, 0.0F, 0.0F)
        );

        thorax.addOrReplaceChild(
                "left_pectoral",
                CubeListBuilder.create()
                        .texOffs(116, 32)
                        .addBox(-1.0F, -3.0F, -5.0F, 6.0F, 7.0F, 11.0F),
                PartPose.offsetAndRotation(8.8F, 2.0F, -3.0F, 0.02F, -0.10F, -0.14F)
        );
        thorax.addOrReplaceChild(
                "right_pectoral",
                CubeListBuilder.create()
                        .texOffs(116, 32)
                        .mirror()
                        .addBox(-5.0F, -3.0F, -5.0F, 6.0F, 7.0F, 11.0F),
                PartPose.offsetAndRotation(-8.8F, 2.0F, -3.0F, 0.02F, 0.10F, 0.14F)
        );

        PartDefinition abdomen = body.addOrReplaceChild(
                "abdomen",
                CubeListBuilder.create()
                        .texOffs(0, 54)
                        .addBox(-7.5F, -5.5F, -9.0F, 15.0F, 10.5F, 18.0F)
                        .texOffs(64, 54)
                        .addBox(-6.2F, -7.0F, -7.0F, 12.4F, 4.0F, 14.0F)
                        .texOffs(64, 74)
                        .addBox(-5.5F, 3.5F, -6.0F, 11.0F, 4.5F, 13.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 4.0F, 0.03F, 0.0F, 0.0F)
        );

        abdomen.addOrReplaceChild(
                "waist_transition",
                CubeListBuilder.create()
                        .texOffs(112, 54)
                        .addBox(-6.5F, -4.5F, -5.0F, 13.0F, 9.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, 8.0F, -0.04F, 0.0F, 0.0F)
        );

        PartDefinition pelvis = body.addOrReplaceChild(
                "pelvis",
                CubeListBuilder.create()
                        .texOffs(158, 40)
                        .addBox(-9.5F, -6.5F, -6.5F, 19.0F, 12.5F, 14.0F)
                        .texOffs(158, 68)
                        .addBox(-10.5F, -4.0F, -1.0F, 21.0F, 8.0F, 11.0F)
                        .texOffs(158, 90)
                        .addBox(-7.0F, 4.0F, -3.0F, 14.0F, 5.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 16.0F, -0.02F, 0.0F, 0.0F)
        );

        pelvis.addOrReplaceChild(
                "tail_base_transition",
                CubeListBuilder.create()
                        .texOffs(208, 40)
                        .addBox(-7.5F, -5.0F, -2.0F, 15.0F, 10.0F, 9.0F)
                        .texOffs(208, 62)
                        .addBox(-6.2F, -4.0F, 5.0F, 12.4F, 8.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 0.04F, 0.0F, 0.0F)
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
                        .addBox(-2.0F, -2.5F, -7.0F, 7.0F, 6.0F, 14.0F)
                        .texOffs(196, 16)
                        .addBox(2.0F, -1.5F, -4.5F, 6.0F, 4.0F, 9.0F),
                PartPose.offsetAndRotation(8.5F, -5.0F, -10.0F, 0.02F, -0.16F, -0.18F)
        );
        body.addOrReplaceChild(
                "right_scapula",
                CubeListBuilder.create()
                        .texOffs(196, 0)
                        .mirror()
                        .addBox(-5.0F, -2.5F, -7.0F, 7.0F, 6.0F, 14.0F)
                        .texOffs(196, 16)
                        .mirror()
                        .addBox(-8.0F, -1.5F, -4.5F, 6.0F, 4.0F, 9.0F),
                PartPose.offsetAndRotation(-8.5F, -5.0F, -10.0F, 0.02F, 0.16F, 0.18F)
        );
    }

    private static void createDorsalSpines(PartDefinition body) {
        PartDefinition ridge = body.addOrReplaceChild(
                "dorsal_ridge",
                CubeListBuilder.create(),
                PartPose.ZERO
        );

        addDorsalSpine(ridge, "spine_01", -8.0F, -13.0F, 7.0F, 0);
        addDorsalSpine(ridge, "spine_02", -7.2F, -4.0F, 6.0F, 12);
        addDorsalSpine(ridge, "spine_03", -6.5F, 6.0F, 5.0F, 24);
        addDorsalSpine(ridge, "spine_04", -6.0F, 16.0F, 4.0F, 36);
    }

    private static void addDorsalSpine(
            PartDefinition parent,
            String name,
            float y,
            float z,
            float height,
            int texY
    ) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(232, texY)
                        .addBox(-1.0F, -height, -2.0F, 2.0F, height, 4.0F),
                PartPose.offsetAndRotation(0.0F, y, z, -0.10F, 0.0F, 0.0F)
        );
    }

    private static void createNeckAndHead(PartDefinition body) {
        PartDefinition neck01 = body.addOrReplaceChild(
                "neck_01",
                CubeListBuilder.create()
                        .texOffs(0, 96)
                        .addBox(-6.5F, -5.0F, -14.0F, 13.0F, 10.0F, 15.0F)
                        .texOffs(0, 122)
                        .addBox(-5.2F, -6.2F, -11.5F, 10.4F, 4.0F, 12.0F)
                        .texOffs(0, 140)
                        .addBox(-4.5F, 3.2F, -11.0F, 9.0F, 4.2F, 12.0F),
                PartPose.offset(0.0F, -4.0F, -20.0F)
        );
        addNeckSpines(neck01, 6.5F, "neck_01_spines");

        PartDefinition neck02 = neck01.addOrReplaceChild(
                "neck_02",
                CubeListBuilder.create()
                        .texOffs(64, 96)
                        .addBox(-5.5F, -4.4F, -13.0F, 11.0F, 8.8F, 14.0F)
                        .texOffs(64, 120)
                        .addBox(-4.3F, -5.4F, -10.5F, 8.6F, 3.5F, 11.0F)
                        .texOffs(64, 136)
                        .addBox(-3.8F, 2.8F, -10.0F, 7.6F, 3.7F, 11.0F),
                PartPose.offset(0.0F, -1.0F, -12.5F)
        );
        addNeckSpines(neck02, 5.5F, "neck_02_spines");

        PartDefinition neck03 = neck02.addOrReplaceChild(
                "neck_03",
                CubeListBuilder.create()
                        .texOffs(128, 96)
                        .addBox(-4.5F, -3.8F, -12.0F, 9.0F, 7.6F, 13.0F)
                        .texOffs(128, 118)
                        .addBox(-3.5F, -4.8F, -9.5F, 7.0F, 3.0F, 10.0F)
                        .texOffs(128, 134)
                        .addBox(-3.0F, 2.4F, -9.0F, 6.0F, 3.2F, 10.0F),
                PartPose.offset(0.0F, -1.0F, -11.5F)
        );
        addNeckSpines(neck03, 4.5F, "neck_03_spines");

        PartDefinition head = neck03.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 156)
                        .addBox(-6.5F, -5.0F, -10.5F, 13.0F, 10.0F, 11.0F)
                        .texOffs(54, 156)
                        .addBox(-7.5F, -4.0F, -2.0F, 15.0F, 8.0F, 7.0F)
                        .texOffs(0, 180)
                        .addBox(-5.0F, -3.5F, -19.5F, 10.0F, 7.0F, 10.0F)
                        .texOffs(44, 180)
                        .addBox(-3.8F, -2.7F, -26.0F, 7.6F, 5.4F, 7.0F)
                        .texOffs(76, 180)
                        .addBox(-3.0F, -4.0F, -22.5F, 6.0F, 2.0F, 8.0F),
                PartPose.offset(0.0F, -1.0F, -10.5F)
        );

        createHeadDetails(head);
    }

    private static void addNeckSpines(PartDefinition neck, float height, String name) {
        PartDefinition spines = neck.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.ZERO);
        spines.addOrReplaceChild(
                "spine_a",
                CubeListBuilder.create().texOffs(232, 48)
                        .addBox(-0.8F, -height, -1.5F, 1.6F, height, 3.0F),
                PartPose.offsetAndRotation(0.0F, -3.8F, -9.0F, -0.10F, 0.0F, 0.0F)
        );
        spines.addOrReplaceChild(
                "spine_b",
                CubeListBuilder.create().texOffs(240, 48)
                        .addBox(-0.8F, -(height - 1.0F), -1.5F, 1.6F, height - 1.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -3.8F, -2.5F, -0.08F, 0.0F, 0.0F)
        );
    }

    private static void createHeadDetails(PartDefinition head) {
        head.addOrReplaceChild(
                "left_cheek",
                CubeListBuilder.create().texOffs(104, 156)
                        .addBox(-1.5F, -2.5F, -4.0F, 3.5F, 5.5F, 8.0F),
                PartPose.offsetAndRotation(5.4F, 1.0F, -8.0F, 0.0F, -0.08F, -0.06F)
        );
        head.addOrReplaceChild(
                "right_cheek",
                CubeListBuilder.create().texOffs(104, 156)
                        .mirror()
                        .addBox(-2.0F, -2.5F, -4.0F, 3.5F, 5.5F, 8.0F),
                PartPose.offsetAndRotation(-5.4F, 1.0F, -8.0F, 0.0F, 0.08F, 0.06F)
        );

        head.addOrReplaceChild(
                "left_brow",
                CubeListBuilder.create().texOffs(132, 156)
                        .addBox(-2.8F, -1.0F, -3.0F, 5.6F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(3.2F, -4.8F, -10.0F, -0.08F, -0.12F, -0.08F)
        );
        head.addOrReplaceChild(
                "right_brow",
                CubeListBuilder.create().texOffs(132, 156)
                        .mirror()
                        .addBox(-2.8F, -1.0F, -3.0F, 5.6F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(-3.2F, -4.8F, -10.0F, -0.08F, 0.12F, 0.08F)
        );

        PartDefinition jaw = head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create()
                        .texOffs(158, 156)
                        .addBox(-4.8F, 0.0F, -12.0F, 9.6F, 3.2F, 13.0F)
                        .texOffs(158, 174)
                        .addBox(-3.8F, 0.8F, -17.0F, 7.6F, 2.8F, 5.5F),
                PartPose.offset(0.0F, 3.6F, -10.5F)
        );
        jaw.addOrReplaceChild(
                "jaw_keel",
                CubeListBuilder.create().texOffs(196, 156)
                        .addBox(-3.2F, 0.0F, -6.0F, 6.4F, 1.8F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 2.2F, -4.5F, 0.08F, 0.0F, 0.0F)
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
                        .addBox(-0.9F, -6.5F, -1.8F, 1.8F, 6.5F, 3.6F),
                PartPose.offsetAndRotation(0.0F, -4.5F, -7.5F, -0.12F, 0.0F, 0.0F)
        );
        crest.addOrReplaceChild(
                "crest_02",
                CubeListBuilder.create().texOffs(232, 76)
                        .addBox(-0.8F, -4.8F, -1.6F, 1.6F, 4.8F, 3.2F),
                PartPose.offsetAndRotation(0.0F, -4.5F, -1.0F, -0.10F, 0.0F, 0.0F)
        );
    }

    private static void createHorn(PartDefinition head, boolean left, boolean primary) {
        float side = left ? 1.0F : -1.0F;
        String name = primary
                ? (left ? "left_horn" : "right_horn")
                : (left ? "left_horn_secondary" : "right_horn_secondary");

        float x = side * (primary ? 5.2F : 5.7F);
        float y = primary ? -4.8F : -2.8F;
        float z = primary ? -2.0F : -8.0F;
        float rootThickness = primary ? 3.2F : 2.4F;
        float rootLength = primary ? 7.5F : 5.0F;
        float tipLength = primary ? 9.0F : 6.5F;

        CubeListBuilder rootBuilder = builder(primary ? 208 : 220, 176, left)
                .addBox(
                        -rootThickness * 0.5F,
                        -rootThickness * 0.5F,
                        -1.0F,
                        rootThickness,
                        rootThickness,
                        rootLength
                );

        PartDefinition horn = head.addOrReplaceChild(
                name,
                rootBuilder,
                PartPose.offsetAndRotation(
                        x,
                        y,
                        z,
                        primary ? -0.34F : -0.20F,
                        side * (primary ? 0.34F : 0.48F),
                        side * (primary ? -0.12F : -0.18F)
                )
        );

        float tipThickness = rootThickness * 0.56F;
        horn.addOrReplaceChild(
                "tip",
                builder(primary ? 208 : 220, 188, left)
                        .addBox(
                                -tipThickness * 0.5F,
                                -tipThickness * 0.5F,
                                0.0F,
                                tipThickness,
                                tipThickness,
                                tipLength
                        ),
                PartPose.offsetAndRotation(
                        0.0F,
                        0.0F,
                        rootLength - 1.0F,
                        primary ? -0.12F : -0.08F,
                        side * 0.12F,
                        side * -0.06F
                )
        );
    }

    /**
     * Anatomical wing chain. The animated compatibility bones remain:
     * wing_root -> upper_arm -> forearm. Hand and digits are additional child
     * articulation. Span is preserved by moving most reach into long digits
     * rather than rectangular membrane slabs.
     */
    private static void createWing(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String rootName = left ? "left_wing_root" : "right_wing_root";

        PartDefinition wingRoot = body.addOrReplaceChild(
                rootName,
                builder(0, 208, left)
                        .addBox(left ? 0.0F : -8.5F, -3.5F, -4.5F, 8.5F, 7.0F, 9.0F)
                        .texOffs(36, 208)
                        .addBox(left ? 2.0F : -11.0F, -2.4F, -3.2F, 9.0F, 4.8F, 6.4F),
                PartPose.offset( side * 10.0F, -6.0F, -9.0F)
        );

        wingRoot.addOrReplaceChild(
                "shoulder_cap",
                builder(64, 208, left)
                        .addBox(-3.5F, -3.5F, -3.5F, 7.0F, 7.0F, 7.0F),
                PartPose.offset(side * 6.5F, 0.0F, 0.0F)
        );

        PartDefinition upperArm = wingRoot.addOrReplaceChild(
                "upper_arm",
                builder(0, 224, left)
                        .addBox(left ? 0.0F : -17.0F, -2.7F, -2.7F, 17.0F, 5.4F, 5.4F)
                        .texOffs(48, 224)
                        .addBox(left ? 16.0F : -27.0F, -2.0F, -2.0F, 11.0F, 4.0F, 4.0F),
                PartPose.offset(side * 7.0F, 0.0F, 0.0F)
        );

        upperArm.addOrReplaceChild(
                "elbow_joint",
                builder(82, 224, left)
                        .addBox(-3.2F, -3.2F, -3.2F, 6.4F, 6.4F, 6.4F),
                PartPose.offset(side * 26.0F, 0.0F, 0.0F)
        );

        addTaperedMembrane(
                upperArm,
                "membrane_proximal",
                left,
                27.0F,
                11.0F,
                19.0F,
                96,
                224
        );

        PartDefinition forearm = upperArm.addOrReplaceChild(
                "forearm",
                builder(128, 208, left)
                        .addBox(left ? 0.0F : -21.0F, -2.3F, -2.3F, 21.0F, 4.6F, 4.6F)
                        .texOffs(128, 220)
                        .addBox(left ? 20.0F : -36.0F, -1.7F, -1.7F, 16.0F, 3.4F, 3.4F),
                PartPose.offset(side * 26.0F, 0.0F, 0.0F)
        );

        forearm.addOrReplaceChild(
                "wrist_joint",
                builder(176, 220, left)
                        .addBox(-2.7F, -2.7F, -2.7F, 5.4F, 5.4F, 5.4F),
                PartPose.offset(side * 35.0F, 0.0F, 0.0F)
        );

        addTaperedMembrane(
                forearm,
                "membrane_middle",
                left,
                36.0F,
                18.0F,
                28.0F,
                0,
                236
        );

        PartDefinition hand = forearm.addOrReplaceChild(
                "hand",
                builder(184, 208, left)
                        .addBox(left ? 0.0F : -15.0F, -1.8F, -1.8F, 15.0F, 3.6F, 3.6F)
                        .texOffs(184, 218)
                        .addBox(left ? 14.0F : -26.0F, -1.3F, -1.3F, 12.0F, 2.6F, 2.6F),
                PartPose.offsetAndRotation(
                        side * 34.0F,
                        0.0F,
                        0.0F,
                        0.03F,
                        side * 0.08F,
                        side * -0.05F
                )
        );

        addTaperedMembrane(
                hand,
                "membrane_distal",
                left,
                26.0F,
                25.0F,
                15.0F,
                64,
                236
        );

        createWingFingers(hand, left);
        createFingerMembranes(hand, left);

        hand.addOrReplaceChild(
                "wing_claw",
                builder(224, 208, left)
                        .addBox(-1.1F, -1.1F, -1.0F, 2.2F, 2.2F, 8.0F),
                PartPose.offsetAndRotation(
                        side * 20.0F,
                        -0.6F,
                        -1.0F,
                        -0.42F,
                        side * 0.16F,
                        side * -0.22F
                )
        );
    }

    /**
     * Builds a stepped trapezoid-like membrane from thin cuboids. Each step
     * changes trailing depth so the visible edge tapers instead of forming a
     * single rectangular panel.
     */
    private static void addTaperedMembrane(
            PartDefinition parent,
            String name,
            boolean left,
            float length,
            float startDepth,
            float endDepth,
            int texX,
            int texY
    ) {
        PartDefinition membrane = parent.addOrReplaceChild(
                name,
                CubeListBuilder.create(),
                PartPose.ZERO
        );

        float stepLength = length / MEMBRANE_STEPS;
        for (int i = 0; i < MEMBRANE_STEPS; i++) {
            float t = (i + 0.5F) / MEMBRANE_STEPS;
            float depth = lerp(startDepth, endDepth, t);
            float x = i * stepLength;
            float boxX = left ? x : -x - stepLength - 0.2F;

            membrane.addOrReplaceChild(
                    "panel_" + (i + 1),
                    builder(texX, texY, left)
                            .addBox(
                                    boxX,
                                    0.8F,
                                    0.0F,
                                    stepLength + 0.35F,
                                    0.55F,
                                    depth
                            ),
                    PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.02F, 0.0F, 0.0F)
            );
        }
    }

    private static void createWingFingers(PartDefinition hand, boolean left) {
        float side = left ? 1.0F : -1.0F;
        float[] lengths = {74.0F, 63.0F, 53.0F, 43.0F};
        float[] yawSweep = {0.10F, 0.22F, 0.36F, 0.52F};
        float[] roll = {0.02F, 0.05F, 0.08F, 0.12F};
        float[] rootZ = {1.0F, 5.5F, 10.5F, 16.0F};
        float[] thickness = {2.4F, 2.2F, 2.0F, 1.8F};

        for (int i = 0; i < lengths.length; i++) {
            float length = lengths[i];
            float thick = thickness[i];

            PartDefinition finger = hand.addOrReplaceChild(
                    "finger_" + (i + 1),
                    builder(0, 244, left)
                            .addBox(
                                    left ? 0.0F : -length,
                                    -thick * 0.5F,
                                    -thick * 0.5F,
                                    length,
                                    thick,
                                    thick
                            ),
                    PartPose.offsetAndRotation(
                            side * 24.0F,
                            0.4F + i * 0.15F,
                            rootZ[i],
                            0.02F + i * 0.025F,
                            side * yawSweep[i],
                            side * roll[i]
                    )
            );

            float tipThickness = Math.max(1.0F, thick * 0.62F);
            finger.addOrReplaceChild(
                    "tip",
                    builder(54, 244, left)
                            .addBox(
                                    left ? 0.0F : -(length * 0.20F),
                                    -tipThickness * 0.5F,
                                    -tipThickness * 0.5F,
                                    length * 0.20F,
                                    tipThickness,
                                    tipThickness
                            ),
                    PartPose.offsetAndRotation(
                            side * (length * 0.82F),
                            0.0F,
                            0.0F,
                            0.02F,
                            side * (0.04F + i * 0.018F),
                            side * (0.02F + i * 0.015F)
                    )
            );
        }
    }

    /**
     * Fan-shaped inter-digit membrane. These panels are thin, rotated and
     * progressively shorter so the trailing wing edge reads as organic.
     */
    private static void createFingerMembranes(PartDefinition hand, boolean left) {
        float side = left ? 1.0F : -1.0F;
        float[] lengths = {68.0F, 58.0F, 49.0F, 40.0F};
        float[] depths = {13.0F, 15.0F, 15.5F, 13.5F};
        float[] yaw = {0.08F, 0.19F, 0.31F, 0.44F};
        float[] z = {2.0F, 6.5F, 11.5F, 17.0F};

        for (int i = 0; i < lengths.length; i++) {
            float length = lengths[i];
            hand.addOrReplaceChild(
                    "finger_membrane_" + (i + 1),
                    builder(96 + i * 28, 244, left)
                            .addBox(
                                    left ? 0.0F : -length,
                                    0.85F,
                                    0.0F,
                                    length,
                                    0.45F,
                                    depths[i]
                            ),
                    PartPose.offsetAndRotation(
                            side * 23.0F,
                            0.0F,
                            z[i],
                            0.025F + i * 0.012F,
                            side * yaw[i],
                            side * (0.01F + i * 0.02F)
                    )
            );
        }
    }

    private static void createForeleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String name = left ? "left_foreleg" : "right_foreleg";

        PartDefinition leg = body.addOrReplaceChild(
                name,
                builder(0, 64, left)
                        .addBox(-4.5F, -3.0F, -4.5F, 9.0F, 9.0F, 9.0F)
                        .texOffs(38, 64)
                        .addBox(-3.5F, 4.0F, -3.2F, 7.0F, 13.0F, 6.4F),
                PartPose.offsetAndRotation(
                        side * 8.8F,
                        4.0F,
                        -11.0F,
                        -0.06F,
                        0.0F,
                        side * -0.08F
                )
        );

        leg.addOrReplaceChild(
                "elbow_mass",
                builder(66, 64, left)
                        .addBox(-3.2F, -2.6F, -3.0F, 6.4F, 5.2F, 6.0F),
                PartPose.offset(0.0F, 15.0F, 0.8F)
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                builder(92, 64, left)
                        .addBox(-3.1F, 0.0F, -2.8F, 6.2F, 9.0F, 5.6F)
                        .texOffs(118, 64)
                        .addBox(-2.6F, 8.0F, -2.3F, 5.2F, 5.0F, 4.6F),
                PartPose.offsetAndRotation(0.0F, 15.0F, 1.4F, 0.12F, 0.0F, 0.0F)
        );

        PartDefinition wrist = lowerLeg.addOrReplaceChild(
                "wrist",
                builder(140, 64, left)
                        .addBox(-2.3F, 0.0F, -2.2F, 4.6F, 7.5F, 4.4F),
                PartPose.offsetAndRotation(0.0F, 12.0F, -1.0F, -0.38F, 0.0F, 0.0F)
        );

        createFoot(wrist, left, false);
    }

    private static void createHindleg(PartDefinition body, boolean left) {
        float side = left ? 1.0F : -1.0F;
        String name = left ? "left_hindleg" : "right_hindleg";

        PartDefinition leg = body.addOrReplaceChild(
                name,
                builder(0, 82, left)
                        .addBox(-5.8F, -4.0F, -5.8F, 11.6F, 10.5F, 11.6F)
                        .texOffs(48, 82)
                        .addBox(-4.8F, 4.0F, -4.5F, 9.6F, 15.0F, 9.0F),
                PartPose.offsetAndRotation(
                        side * 8.2F,
                        4.0F,
                        14.0F,
                        0.05F,
                        0.0F,
                        side * -0.05F
                )
        );

        leg.addOrReplaceChild(
                "knee_mass",
                builder(88, 82, left)
                        .addBox(-4.2F, -3.0F, -4.0F, 8.4F, 6.0F, 8.0F),
                PartPose.offset(0.0F, 16.5F, 2.8F)
        );

        PartDefinition lowerLeg = leg.addOrReplaceChild(
                "lower_leg",
                builder(122, 82, left)
                        .addBox(-3.7F, 0.0F, -3.6F, 7.4F, 9.0F, 7.2F)
                        .texOffs(154, 82)
                        .addBox(-3.0F, 8.0F, -2.8F, 6.0F, 6.0F, 5.6F),
                PartPose.offsetAndRotation(0.0F, 14.0F, 3.5F, 0.16F, 0.0F, 0.0F)
        );

        PartDefinition ankle = lowerLeg.addOrReplaceChild(
                "ankle",
                builder(182, 82, left)
                        .addBox(-2.7F, 0.0F, -2.6F, 5.4F, 9.5F, 5.2F),
                PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, -0.50F, 0.0F, 0.0F)
        );

        createFoot(ankle, left, true);
    }

    /**
     * Grounded foot. Front and hind offsets are intentionally different so the
     * lowest toe/claw points settle at approximately the same model ground
     * plane despite different leg chains.
     */
    private static void createFoot(PartDefinition parent, boolean left, boolean hind) {
        float footWidth = hind ? 10.5F : 9.0F;
        float footLength = hind ? 13.0F : 11.0F;
        float y = hind ? 9.5F : 10.0F;
        float z = hind ? -4.2F : -3.2F;

        PartDefinition foot = parent.addOrReplaceChild(
                "foot",
                builder(hind ? 212 : 0, 102, left)
                        .addBox(
                                -footWidth * 0.5F,
                                -1.8F,
                                -footLength + 3.0F,
                                footWidth,
                                3.6F,
                                footLength
                        ),
                PartPose.offsetAndRotation(
                        0.0F,
                        y,
                        z,
                        hind ? 0.22F : 0.14F,
                        0.0F,
                        0.0F
                )
        );

        float toeSpread = hind ? 3.3F : 2.8F;
        float toeLength = hind ? 8.5F : 7.5F;
        for (int i = -1; i <= 1; i++) {
            float toeX = i * toeSpread;
            PartDefinition toe = foot.addOrReplaceChild(
                    "toe_" + (i + 2),
                    builder(hind ? 48 : 80, 102, left)
                            .addBox(-1.0F, -0.9F, -toeLength, 2.0F, 1.8F, toeLength),
                    PartPose.offsetAndRotation(
                            toeX,
                            0.4F,
                            -footLength + 4.0F,
                            -0.03F,
                            i * -0.08F,
                            0.0F
                    )
            );

            toe.addOrReplaceChild(
                    "claw",
                    builder(112, 102, left)
                            .addBox(-0.65F, -0.55F, -4.5F, 1.3F, 1.1F, 4.5F),
                    PartPose.offsetAndRotation(
                            0.0F,
                            0.2F,
                            -toeLength + 0.8F,
                            -0.18F,
                            0.0F,
                            0.0F
                    )
            );
        }

        PartDefinition outerToe = foot.addOrReplaceChild(
                "outer_toe",
                builder(144, 102, left)
                        .addBox(-0.9F, -0.8F, -6.5F, 1.8F, 1.6F, 6.5F),
                PartPose.offsetAndRotation(
                        (left ? 1.0F : -1.0F) * footWidth * 0.34F,
                        0.5F,
                        -footLength + 5.0F,
                        -0.02F,
                        (left ? 1.0F : -1.0F) * 0.18F,
                        0.0F
                )
        );
        outerToe.addOrReplaceChild(
                "claw",
                builder(172, 102, left)
                        .addBox(-0.6F, -0.5F, -4.0F, 1.2F, 1.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.2F, -5.5F, -0.18F, 0.0F, 0.0F)
        );

        foot.addOrReplaceChild(
                "rear_pad",
                builder(196, 102, left)
                        .addBox(-2.4F, -1.0F, -1.5F, 4.8F, 2.0F, 4.0F),
                PartPose.offset(0.0F, 0.4F, 1.0F)
        );
    }

    private static void createTail(PartDefinition body) {
        PartDefinition tail01 = body.addOrReplaceChild(
                "tail_01",
                CubeListBuilder.create()
                        .texOffs(0, 112)
                        .addBox(-6.8F, -5.8F, 0.0F, 13.6F, 11.6F, 16.0F)
                        .texOffs(0, 140)
                        .addBox(-5.8F, -5.0F, 14.0F, 11.6F, 10.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 21.0F, -0.02F, 0.0F, 0.0F)
        );
        addTailSpine(tail01, "spine", 5.8F, 8.0F);

        PartDefinition tail02 = tail01.addOrReplaceChild(
                "tail_02",
                CubeListBuilder.create()
                        .texOffs(64, 112)
                        .addBox(-5.3F, -4.6F, 0.0F, 10.6F, 9.2F, 16.0F)
                        .texOffs(64, 138)
                        .addBox(-4.5F, -3.9F, 14.0F, 9.0F, 7.8F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 22.0F, 0.03F, 0.0F, 0.0F)
        );
        addTailSpine(tail02, "spine", 4.8F, 9.0F);

        PartDefinition tail03 = tail02.addOrReplaceChild(
                "tail_03",
                CubeListBuilder.create()
                        .texOffs(128, 112)
                        .addBox(-4.0F, -3.6F, 0.0F, 8.0F, 7.2F, 17.0F)
                        .texOffs(128, 136)
                        .addBox(-3.2F, -2.8F, 15.0F, 6.4F, 5.6F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 23.0F, 0.03F, 0.0F, 0.0F)
        );

        PartDefinition tail04 = tail03.addOrReplaceChild(
                "tail_04",
                CubeListBuilder.create()
                        .texOffs(176, 112)
                        .addBox(-2.7F, -2.5F, 0.0F, 5.4F, 5.0F, 17.0F)
                        .texOffs(176, 134)
                        .addBox(-2.0F, -1.8F, 15.0F, 4.0F, 3.6F, 11.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 24.0F, 0.02F, 0.0F, 0.0F)
        );

        PartDefinition tip = tail04.addOrReplaceChild(
                "tail_tip",
                CubeListBuilder.create()
                        .texOffs(216, 112)
                        .addBox(-1.6F, -1.5F, 0.0F, 3.2F, 3.0F, 10.0F)
                        .texOffs(216, 126)
                        .addBox(-1.0F, -0.9F, 9.0F, 2.0F, 1.8F, 9.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 24.0F, 0.01F, 0.0F, 0.0F)
        );

        tip.addOrReplaceChild(
                "terminal_spike",
                CubeListBuilder.create().texOffs(240, 112)
                        .addBox(-0.6F, -0.6F, 0.0F, 1.2F, 1.2F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 17.0F, 0.02F, 0.0F, 0.0F)
        );
    }

    private static void addTailSpine(PartDefinition parent, String name, float height, float z) {
        parent.addOrReplaceChild(
                name,
                CubeListBuilder.create().texOffs(232, 96)
                        .addBox(-0.9F, -height, -2.0F, 1.8F, height, 4.0F),
                PartPose.offsetAndRotation(0.0F, -4.2F, z, -0.08F, 0.0F, 0.0F)
        );
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static CubeListBuilder builder(int texX, int texY, boolean left) {
        CubeListBuilder builder = CubeListBuilder.create().texOffs(texX, texY);
        if (!left) {
            builder.mirror();
        }
        return builder;
    }
}
