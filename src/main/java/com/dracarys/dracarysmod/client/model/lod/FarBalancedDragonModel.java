package com.dracarys.dracarysmod.client.model.lod;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * True 3D low-detail model used only at long range.
 *
 * It intentionally keeps the large silhouette-defining pieces:
 * body, neck/head, two wings and tail. Four simple legs remain in FAR_3D
 * but are omitted from VERY_FAR_3D.
 *
 * It is NOT a sprite and it renders inside the 3D world with depth.
 */
public final class FarBalancedDragonModel<T extends DracarysDragonEntity>
        extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(
                    DracarysMod.id("dracarys_dragon_balanced_far"),
                    "main"
            );

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart tailTip;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart leftForeleg;
    private final ModelPart rightForeleg;
    private final ModelPart leftHindleg;
    private final ModelPart rightHindleg;

    private boolean veryFar;

    public FarBalancedDragonModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.neck = root.getChild("neck");
        this.head = root.getChild("head");
        this.tail = root.getChild("tail");
        this.tailTip = tail.getChild("tail_tip");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
        this.leftForeleg = root.getChild("left_foreleg");
        this.rightForeleg = root.getChild("right_foreleg");
        this.leftHindleg = root.getChild("left_hindleg");
        this.rightHindleg = root.getChild("right_hindleg");
    }

    @Override
    public ModelPart root() {
        return root;
    }

    public void setVeryFar(boolean veryFar) {
        this.veryFar = veryFar;
    }

    @Override
    public void setupAnim(
            T dragon,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        body.xRot = 0.0F;
        body.yRot = 0.0F;
        body.zRot = 0.0F;

        neck.xRot = -0.08F;
        neck.yRot = Mth.clamp(netHeadYaw, -35.0F, 35.0F)
                * Mth.DEG_TO_RAD * 0.28F;

        head.xRot = Mth.clamp(headPitch, -25.0F, 25.0F)
                * Mth.DEG_TO_RAD * 0.35F;
        head.yRot = neck.yRot * 0.55F;

        float tailWave = Mth.sin(ageInTicks * 0.065F);
        tail.yRot = tailWave * 0.08F;
        tailTip.yRot = Mth.sin(ageInTicks * 0.065F - 0.8F) * 0.12F;

        if (dragon.isFlying()) {
            float flap = Mth.sin(ageInTicks * 0.28F);

            body.xRot = -0.08F;
            leftWing.zRot = -0.24F - flap * 0.42F;
            rightWing.zRot = 0.24F + flap * 0.42F;

            leftWing.yRot = -0.08F;
            rightWing.yRot = 0.08F;

            leftForeleg.xRot = 0.55F;
            rightForeleg.xRot = 0.55F;
            leftHindleg.xRot = 0.70F;
            rightHindleg.xRot = 0.70F;
        } else {
            float idle = Mth.sin(ageInTicks * 0.055F) * 0.025F;
            leftWing.zRot = -0.20F - idle;
            rightWing.zRot = 0.20F + idle;

            float walk = Mth.cos(limbSwing * 0.65F)
                    * 0.32F
                    * Mth.clamp(limbSwingAmount, 0.0F, 1.0F);

            leftForeleg.xRot = walk;
            rightForeleg.xRot = -walk;
            leftHindleg.xRot = -walk;
            rightHindleg.xRot = walk;
        }
    }

    /**
     * Render the body group in a primary tint.
     */
    public void renderBody(
            PoseStack poseStack,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        body.render(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );

        neck.render(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );

        head.render(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );

        tail.render(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );

        if (!veryFar) {
            leftForeleg.render(
                    poseStack, consumer, packedLight, packedOverlay,
                    red, green, blue, alpha
            );
            rightForeleg.render(
                    poseStack, consumer, packedLight, packedOverlay,
                    red, green, blue, alpha
            );
            leftHindleg.render(
                    poseStack, consumer, packedLight, packedOverlay,
                    red, green, blue, alpha
            );
            rightHindleg.render(
                    poseStack, consumer, packedLight, packedOverlay,
                    red, green, blue, alpha
            );
        }
    }

    /**
     * Wings receive a slightly darker secondary tint so a distant dragon is
     * not a single flat color.
     */
    public void renderWings(
            PoseStack poseStack,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        leftWing.render(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );

        rightWing.render(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );
    }

    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        renderBody(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );

        renderWings(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(
                                -10.0F, -8.0F, -20.0F,
                                20.0F, 16.0F, 42.0F
                        )
                        .texOffs(0, 62)
                        .addBox(
                                -11.0F, -7.0F, -15.0F,
                                22.0F, 14.0F, 22.0F
                        ),
                PartPose.offset(0.0F, -18.0F, 1.0F)
        );

        root.addOrReplaceChild(
                "neck",
                CubeListBuilder.create()
                        .texOffs(96, 0)
                        .addBox(
                                -6.0F, -5.0F, -22.0F,
                                12.0F, 10.0F, 24.0F
                        ),
                PartPose.offsetAndRotation(
                        0.0F, -20.0F, -17.0F,
                        -0.08F, 0.0F, 0.0F
                )
        );

        root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(96, 38)
                        .addBox(
                                -8.0F, -6.0F, -18.0F,
                                16.0F, 12.0F, 20.0F
                        )
                        .texOffs(96, 74)
                        .addBox(
                                -5.0F, -3.5F, -27.0F,
                                10.0F, 7.0F, 11.0F
                        ),
                PartPose.offset(
                        0.0F, -20.0F, -40.0F
                )
        );

        PartDefinition tail = root.addOrReplaceChild(
                "tail",
                CubeListBuilder.create()
                        .texOffs(0, 104)
                        .addBox(
                                -6.0F, -5.0F, 0.0F,
                                12.0F, 10.0F, 34.0F
                        ),
                PartPose.offset(
                        0.0F, -18.0F, 20.0F
                )
        );

        tail.addOrReplaceChild(
                "tail_tip",
                CubeListBuilder.create()
                        .texOffs(0, 150)
                        .addBox(
                                -3.5F, -3.0F, 0.0F,
                                7.0F, 6.0F, 38.0F
                        ),
                PartPose.offset(
                        0.0F, 0.0F, 31.0F
                )
        );

        addWing(root, true);
        addWing(root, false);
        addLeg(root, true, true);
        addLeg(root, false, true);
        addLeg(root, true, false);
        addLeg(root, false, false);

        return LayerDefinition.create(
                mesh,
                256,
                256
        );
    }

    private static void addWing(
            PartDefinition root,
            boolean left
    ) {
        float side = left ? 1.0F : -1.0F;
        String name = left ? "left_wing" : "right_wing";

        CubeListBuilder builder =
                CubeListBuilder.create()
                        .texOffs(0, 194);

        if (!left) {
            builder.mirror();
        }

        float startX = left ? 0.0F : -78.0F;

        /*
         * One thick structural arm plus stepped membrane blocks.
         * Low cube count, strong silhouette.
         */
        builder.addBox(
                startX,
                -2.0F,
                -3.0F,
                78.0F,
                4.0F,
                6.0F
        );

        builder.texOffs(0, 210).addBox(
                left ? 6.0F : -70.0F,
                0.0F,
                -1.0F,
                64.0F,
                1.5F,
                14.0F
        );

        builder.texOffs(0, 228).addBox(
                left ? 18.0F : -62.0F,
                0.0F,
                11.0F,
                44.0F,
                1.5F,
                13.0F
        );

        builder.texOffs(128, 194).addBox(
                left ? 32.0F : -56.0F,
                0.0F,
                22.0F,
                24.0F,
                1.5F,
                11.0F
        );

        root.addOrReplaceChild(
                name,
                builder,
                PartPose.offsetAndRotation(
                        side * 8.0F,
                        -24.0F,
                        -8.0F,
                        -0.03F,
                        side * -0.05F,
                        side * -0.20F
                )
        );
    }

    private static void addLeg(
            PartDefinition root,
            boolean left,
            boolean fore
    ) {
        String name;

        if (fore) {
            name = left
                    ? "left_foreleg"
                    : "right_foreleg";
        } else {
            name = left
                    ? "left_hindleg"
                    : "right_hindleg";
        }

        float side = left ? 1.0F : -1.0F;
        float z = fore ? -10.0F : 13.0F;

        CubeListBuilder builder =
                CubeListBuilder.create()
                        .texOffs(176, 0);

        if (!left) {
            builder.mirror();
        }

        builder.addBox(
                -4.0F,
                -1.0F,
                -4.0F,
                8.0F,
                31.0F,
                8.0F
        );

        builder.texOffs(176, 42).addBox(
                -5.0F,
                27.0F,
                -10.0F,
                10.0F,
                5.0F,
                13.0F
        );

        root.addOrReplaceChild(
                name,
                builder,
                PartPose.offsetAndRotation(
                        side * 8.0F,
                        -12.0F,
                        z,
                        fore ? -0.04F : 0.08F,
                        0.0F,
                        side * -0.04F
                )
        );
    }
}
