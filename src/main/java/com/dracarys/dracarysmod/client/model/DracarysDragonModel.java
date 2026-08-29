package com.dracarys.dracarysmod.client.model;
import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
public class DracarysDragonModel<T extends DracarysDragonEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER=new ModelLayerLocation(DracarysMod.id("dracarys_dragon"),"main");
    private final ModelPart root,head,leftWing,rightWing,tail;
    public DracarysDragonModel(ModelPart root){this.root=root;this.head=root.getChild("head");this.leftWing=root.getChild("left_wing");this.rightWing=root.getChild("right_wing");this.tail=root.getChild("tail");}
    public static LayerDefinition createBodyLayer(){MeshDefinition m=new MeshDefinition();PartDefinition r=m.getRoot();r.addOrReplaceChild("body",CubeListBuilder.create().texOffs(0,0).addBox(-7,-6,-11,14,12,22),PartPose.offset(0,12,0));r.addOrReplaceChild("head",CubeListBuilder.create().texOffs(0,36).addBox(-5,-4,-10,10,8,10).texOffs(40,36).addBox(-3,-2,-14,6,4,5),PartPose.offset(0,8,-10));r.addOrReplaceChild("left_wing",CubeListBuilder.create().texOffs(0,60).addBox(0,-1,-3,22,2,12),PartPose.offset(6,8,-2));r.addOrReplaceChild("right_wing",CubeListBuilder.create().mirror().texOffs(0,60).addBox(-22,-1,-3,22,2,12),PartPose.offset(-6,8,-2));r.addOrReplaceChild("tail",CubeListBuilder.create().texOffs(0,80).addBox(-3,-3,0,6,6,24),PartPose.offset(0,12,10));r.addOrReplaceChild("left_leg",CubeListBuilder.create().texOffs(64,0).addBox(-2,0,-2,4,12,4),PartPose.offset(5,14,2));r.addOrReplaceChild("right_leg",CubeListBuilder.create().texOffs(64,0).addBox(-2,0,-2,4,12,4),PartPose.offset(-5,14,2));return LayerDefinition.create(m,128,128);}
    @Override public ModelPart root(){return root;}
    @Override public void setupAnim(T e,float limbSwing,float limbSwingAmount,float age,float yaw,float pitch){head.yRot=yaw*Mth.DEG_TO_RAD;head.xRot=pitch*Mth.DEG_TO_RAD*.4f;float flap=e.isFlying()?(Mth.sin(age*.45f)*.65f):(.08f+Mth.sin(age*.08f)*.05f);leftWing.zRot=-.15f-flap;rightWing.zRot=.15f+flap;tail.yRot=Mth.sin(age*.12f)*.18f;}
    @Override public void renderToBuffer(PoseStack p, VertexConsumer v,int light,int overlay,float r,float g,float b,float a){root.render(p,v,light,overlay,r,g,b,a);}
}
