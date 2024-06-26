package nourl.mythicmetals.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import nourl.mythicmetals.MythicMetals;
import nourl.mythicmetals.armor.CarmotShield;
import nourl.mythicmetals.blocks.MythicBlocks;
import nourl.mythicmetals.client.models.RainbowShieldModel;
import nourl.mythicmetals.component.DrillComponent;
import nourl.mythicmetals.component.MythicDataComponents;
import nourl.mythicmetals.item.tools.CarmotStaff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static nourl.mythicmetals.client.rendering.PlayerEnergySwirlFeatureRenderer.SWIRL_TEXTURE;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Unique
    private static final Identifier WORLD_BORDER = Identifier.of("textures/misc/forcefield.png");

    /**
     * Renders the Carmot Shield on the players arm
     */
    @Inject(method = "renderArm", at = @At("TAIL"))
    private void mythicmetals$renderShieldArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (player.getComponent(MythicMetals.CARMOT_SHIELD).shouldRenderShield()) {
            final var client = MinecraftClient.getInstance();
            // TODO - Review
            float f = player.age + (client.isPaused() ? 0 : client.getRenderTickCounter().getTickDelta(true));

            var shield = player.getComponent(MythicMetals.CARMOT_SHIELD);

            int pieces = (int) (shield.getMaxHealth() % 4 + 1);
            float health = pieces < 3 ? shield.shieldHealth / 80f : shield.shieldHealth / 110f;

            var consumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(SWIRL_TEXTURE, (f * .005f) % 1f, f * .005f % 1f));

            if (shield.cooldown > CarmotShield.MAX_COOLDOWN - 30) {
                matrices.scale(1.0625f, 1.0625f, 1.0625f);
                // FIXME - Color
                sleeve.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 0xAE0000FF);
            } else // Regular animation
                sleeve.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 0x226633FF);
        }
    }

    /**
     * Renders the Stormyx Shield around the player
     */
    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("TAIL"))
    private void mythicmetals$renderRainbowShield(AbstractClientPlayerEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        var stack = player.getStackInHand(Hand.MAIN_HAND);
        if (CarmotStaff.hasBlockInStaff(stack, MythicBlocks.STORMYX.getStorageBlock())) {
            if (!stack.getOrDefault(MythicDataComponents.IS_USED, false)) return; // Only render if the staff is actively being used
            matrixStack.push();
            double delta = System.currentTimeMillis() / 45.0;

            double hue = delta % 360.0;
            float saturation = 1;
            float constantValue = 1;

            int color = MathHelper.hsvToArgb((float) (hue / 360), saturation, constantValue, 128);

            var part = RainbowShieldModel.getTexturedModelData();

            part.createModel().render(
                    matrixStack,
                    vertexConsumerProvider.getBuffer(RenderLayer.getEnergySwirl(WORLD_BORDER, (float) ((delta * .005f) % 1f), (float) (delta * .005f % 1f))),
                    i,
                    OverlayTexture.DEFAULT_UV,
                    color);
            matrixStack.pop();
        }
    }
    @Inject(method = "getArmPose", at = @At("RETURN"), cancellable = true)
    private static void mythicmetals$mythrilDrillPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        var stack = player.getStackInHand(hand);
        if (stack.getOrDefault(MythicDataComponents.DRILL, DrillComponent.DEFAULT).isActive()) {
            cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
        }
    }
}
