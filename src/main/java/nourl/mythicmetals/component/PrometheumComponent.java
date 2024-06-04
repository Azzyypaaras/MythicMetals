package nourl.mythicmetals.component;

import io.wispforest.owo.serialization.StructEndec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.client.item.TooltipType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import nourl.mythicmetals.data.MythicTags;
import java.util.UUID;
import java.util.function.Consumer;

public record PrometheumComponent(int durabilityRepaired) implements TooltipAppender {
    public static final int OVERGROWN_THRESHOLD = 1200;
    public static final StructEndec<PrometheumComponent> ENDEC = StructEndecBuilder.of(
        StructEndec.INT.fieldOf("durability_repaired", PrometheumComponent::durabilityRepaired),
        PrometheumComponent::new
    );
    public static final UUID ARMOR_BONUS_ID = UUID.fromString("30d14ea3-8513-4914-8e5b-54084d6a450b");
    public static final UUID TOUGHNESS_BONUS_ID = UUID.fromString("6d75e1c4-eec6-4a6a-aeec-815d7fa06c90");
    public static final UUID DAMAGE_BONUS_ID = UUID.fromString("b8474d4a-0b81-47f2-ae59-44aeb18738e6");
    public static final PrometheumComponent DEFAULT = new PrometheumComponent(0);

    /**
     * Applies auto repair onto the item in question
     * Only call this on the server, not on the client!
     *
     * @param stack ItemStack to repair
     * @param r     Any Minecraft Math {@link Random}
     */
    public static void tickAutoRepair(ItemStack stack, Random r) {
        if (!stack.isDamaged()) return; // Don't handle auto repair if item is fully repaired
        if (!stack.contains(MythicDataComponents.PROMETHEUM)) return;

        var component = stack.get(MythicDataComponents.PROMETHEUM);
        assert component != null;

        var dmg = stack.getDamage();
        var rng = r.nextInt(200);

        if (rng != 177) return; // Roll for repair, ignore if roll fails. Number is arbitrary

        // Overgrown Items repair faster
        int damageToRepair = isOvergrown(stack) ? 2 : 1;

        // Extra repair speed if bound
        if (stack.isIn(MythicTags.PROMETHEUM_ARMOR) && EnchantmentHelper.hasBindingCurse(stack)) {
            damageToRepair += 1;
        }

        int newDamage = MathHelper.clamp(dmg - damageToRepair, 0, Integer.MAX_VALUE);
        stack.setDamage(newDamage);
        stack.set(MythicDataComponents.PROMETHEUM, component.increase(damageToRepair));
    }

    public PrometheumComponent increase(int durabilityRepaired) {
        return new PrometheumComponent(this.durabilityRepaired + durabilityRepaired);
    }

    public boolean isOvergrown() {
        return this.durabilityRepaired > OVERGROWN_THRESHOLD;
    }

    public static boolean isOvergrown(ItemStack stack) {
        return stack.getOrDefault(MythicDataComponents.PROMETHEUM, DEFAULT).durabilityRepaired > OVERGROWN_THRESHOLD;
    }

    public static EntityAttributeModifier createOvergrownModifier(ItemStack stack, int base) {
        return createOvergrownModifier(stack, base, EquipmentSlot.MAINHAND);
    }

    public static EntityAttributeModifier createOvergrownModifier(ItemStack stack, int base, EquipmentSlot slot) {
        var uuid = switch (slot.getType()) {
            case HAND -> DAMAGE_BONUS_ID;
            case ARMOR, BODY -> ARMOR_BONUS_ID;
        };
        var component = stack.getOrDefault(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT);
        int bonus = base;
        bonus += component.durabilityRepaired() > (OVERGROWN_THRESHOLD * 2) ? 2 : 1;
        return new EntityAttributeModifier(
            uuid,
            "Overgrown Prometheum bonus",
            bonus,
            EntityAttributeModifier.Operation.ADD_VALUE);
    }

    public static EntityAttributeModifier createOvergrownToughnessModifier(ItemStack stack, int base) {
        var component = stack.getOrDefault(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT);
        int bonus = base;
        bonus += component.durabilityRepaired() > (OVERGROWN_THRESHOLD * 2) ? 2 : 1;
        return new EntityAttributeModifier(
            TOUGHNESS_BONUS_ID,
            "Overgrown Prometheum bonus",
            bonus,
            EntityAttributeModifier.Operation.ADD_VALUE);
    }

    // TODO - Use TooltipCallback to render this tooltip on tagged entries
    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Text.translatable("abilities.mythicmetals.mending"));
        if (this.isOvergrown()) {
            tooltip.accept(Text.translatable("tooltip.prometheum.overgrown"));
        }
        // FIXME - Move engrained to a TooltipCallback?
        //if (EnchantmentHelper.hasBindingCurse(stack)) {
        //    Ability.addTooltipOnStack(stack, lines, Style.EMPTY.withColor(UsefulSingletonForColorUtil.MetalColors.PROMETHEUM.rgb()), "tooltip.prometheum.engrained");
        //}
    }
}
