package nourl.mythicmetals.client.models;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class TidesingerBipedModel extends BipedEntityModel<LivingEntity> {
    final EquipmentSlot slot;

    public TidesingerBipedModel(ModelPart root, EquipmentSlot slot) {
        super(root);
        this.slot = slot;
    }

    @Override
    public void animateModel(LivingEntity livingEntity, float f, float g, float h) {
        super.animateModel(livingEntity, f, g, h);
    }

    @Override
    public void setAngles(LivingEntity entity, float f, float g, float h, float i, float j) {
        if (!(entity instanceof ArmorStandEntity stand)) {
            super.setAngles(entity, f, g, h, i, j);
            return;
        }
        this.head.pitch = ((float) Math.PI / 180F) * stand.getHeadRotation().getPitch();
        this.head.yaw = ((float) Math.PI / 180F) * stand.getHeadRotation().getYaw();
        this.head.roll = ((float) Math.PI / 180F) * stand.getHeadRotation().getRoll();
        this.head.setPivot(0.0F, 1.0F, 0.0F);
        this.body.pitch = ((float) Math.PI / 180F) * stand.getBodyRotation().getPitch();
        this.body.yaw = ((float) Math.PI / 180F) * stand.getBodyRotation().getYaw();
        this.body.roll = ((float) Math.PI / 180F) * stand.getBodyRotation().getRoll();
        this.leftArm.pitch = ((float) Math.PI / 180F) * stand.getLeftArmRotation().getPitch();
        this.leftArm.yaw = ((float) Math.PI / 180F) * stand.getLeftArmRotation().getYaw();
        this.leftArm.roll = ((float) Math.PI / 180F) * stand.getLeftArmRotation().getRoll();
        this.rightArm.pitch = ((float) Math.PI / 180F) * stand.getRightArmRotation().getPitch();
        this.rightArm.yaw = ((float) Math.PI / 180F) * stand.getRightArmRotation().getYaw();
        this.rightArm.roll = ((float) Math.PI / 180F) * stand.getRightArmRotation().getRoll();
        this.leftLeg.pitch = ((float) Math.PI / 180F) * stand.getLeftLegRotation().getPitch();
        this.leftLeg.yaw = ((float) Math.PI / 180F) * stand.getLeftLegRotation().getYaw();
        this.leftLeg.roll = ((float) Math.PI / 180F) * stand.getLeftLegRotation().getRoll();
        this.leftLeg.setPivot(1.9F, 11.0F, 0.0F);
        this.rightLeg.pitch = ((float) Math.PI / 180F) * stand.getRightLegRotation().getPitch();
        this.rightLeg.yaw = ((float) Math.PI / 180F) * stand.getRightLegRotation().getYaw();
        this.rightLeg.roll = ((float) Math.PI / 180F) * stand.getRightLegRotation().getRoll();
        this.rightLeg.setPivot(-1.9F, 11.0F, 0.0F);
        this.hat.copyTransform(head);
    }

    @Override
    public void render(MatrixStack ms, VertexConsumer buffer, int light, int overlay, int color) {
        renderArmorPart(slot);
        super.render(ms, buffer, light, overlay, color);
    }

    private void renderArmorPart(EquipmentSlot slot) {
        setVisible(false);
        // Note - These are custom parts. By extending this you need these in your model,
        // otherwise you guarantee a crash.
        this.body.getChild("body_belt").visible = false;
        this.body.getChild("body_buckle").visible = false;
        this.body.getChild("body_crest").visible = false;
        this.body.getChild("body_armor").visible = false;

        this.rightLeg.getChild("right_boot").visible = false;
        this.rightLeg.getChild("right_leg_armor").visible = false;

        this.leftLeg.getChild("left_boot").visible = false;
        this.leftLeg.getChild("left_leg_armor").visible = false;


        switch (slot) {
            case HEAD -> head.visible = true;
            case CHEST -> {
                this.body.visible = true;
                this.rightArm.visible = true;
                this.leftArm.visible = true;
                this.body.getChild("body_crest").visible = true;
                this.body.getChild("body_armor").visible = true;
            }
            case LEGS -> {
                this.body.visible = true;
                this.body.getChild("body_belt").visible = true;
                this.body.getChild("body_buckle").visible = true;
                this.rightLeg.visible = true;
                this.leftLeg.visible = true;
                this.leftLeg.getChild("left_leg_armor").visible = true;
                this.rightLeg.getChild("right_leg_armor").visible = true;
            }
            case FEET -> {
                this.rightLeg.visible = true;
                this.leftLeg.visible = true;
                this.leftLeg.getChild("left_boot").visible = true;
                this.rightLeg.getChild("right_boot").visible = true;
            }
        }
    }


}
