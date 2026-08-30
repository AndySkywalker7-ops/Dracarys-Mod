package com.dracarys.dracarysmod.client.renderer.layer;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.client.model.anatomy.BalancedDragonModel;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

/**
 * Step 4.0.7B
 *
 * Long-range opaque reinforcement pass.
 *
 * The previous outline-only experiment helped when terrain existed behind the
 * dragon, but shader fog/sky could still wash out the interior of the model.
 *
 * This layer re-renders the SAME textured dragon model using an opaque
 * entity-cutout render type and full light. It does not enlarge the dragon and
 * it does not replace the normal texture.
 */
public final class FarOpaquePresenceLayer
        extends RenderLayer<DracarysDragonEntity, BalancedDragonModel<DracarysDragonEntity>> {

    private static final double START_DISTANCE = 90.0D;

    public FarOpaquePresenceLayer(
            RenderLayerParent<DracarysDragonEntity, BalancedDragonModel<DracarysDragonEntity>> parent
    ) {
        super(parent);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            DracarysDragonEntity dragon,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) return;

        Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();
        double distanceSqr = dragon.position().distanceToSqr(camera);

        if (distanceSqr < START_DISTANCE * START_DISTANCE) {
            return;
        }

        ResourceLocation texture = DracarysMod.id(
                "textures/entity/dragon/" + dragon.getVariant().id() + ".png"
        );

        /*
         * Opaque texture reinforcement.
         *
         * ZOffset avoids fighting with the normal dragon render that is already
         * occupying the same geometry.
         */
        VertexConsumer consumer = bufferSource.getBuffer(
                RenderType.entityCutoutNoCullZOffset(texture)
        );

        /*
         * Slight charcoal neutralization strengthens contrast against bright
         * sky/fog but preserves the original variant colors.
         *
         * Full alpha is important: this is specifically intended to solve the
         * "transparent against sky" appearance.
         */
        this.getParentModel().renderToBuffer(
                poseStack,
                consumer,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                0.82F,
                0.84F,
                0.88F,
                1.00F
        );
    }
}
