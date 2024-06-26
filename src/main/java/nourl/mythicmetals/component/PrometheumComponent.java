package nourl.mythicmetals.component;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import nourl.mythicmetals.data.MythicTags;
import nourl.mythicmetals.misc.RegistryHelper;

public record PrometheumComponent(int durabilityRepaired) {
    public static final int OVERGROWN_THRESHOLD = 1200;
    public static final StructEndec<PrometheumComponent> ENDEC = StructEndecBuilder.of(
        StructEndec.INT.fieldOf("durability_repaired", PrometheumComponent::durabilityRepaired),
        PrometheumComponent::new
    );
    public static final Identifier ARMOR_BONUS_ID = RegistryHelper.id("prometheum_armor_bonus");
    public static final Identifier TOUGHNESS_BONUS_ID = RegistryHelper.id("prometheum_toughness_bonus");
    public static final Identifier DAMAGE_BONUS_ID = RegistryHelper.id("prometheum_damage_bonus");
    public static final PrometheumComponent DEFAULT = new PrometheumComponent(0);

    /**
     * Applies auto repair onto the item in question
     *
     * @param stack ItemStack to repair
     * @param world World where the ItemStack exists
     */
    public static void tickAutoRepair(ItemStack stack, World world) {
        if (!stack.isDamaged()) return; // Don't handle auto repair if item is fully repaired
        if (!stack.contains(MythicDataComponents.PROMETHEUM)) return;
        if (world.isClient()) return; // Desyncs if done on client
        var random = world.getRandom();

        var component = stack.get(MythicDataComponents.PROMETHEUM);
        assert component != null;

        var dmg = stack.getDamage();
        var rng = random.nextInt(200);

        if (rng != 177) return; // Roll for repair, ignore if roll fails. Number is arbitrary

        // Overgrown Items repair faster
        int damageToRepair = isOvergrown(stack) ? 2 : 1;

        // Extra repair speed if bound
        if (stack.isIn(MythicTags.PROMETHEUM_ARMOR) && stack.contains(EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE)) {
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
        var id = switch (slot.getType()) {
            case HAND -> DAMAGE_BONUS_ID;
            case HUMANOID_ARMOR, ANIMAL_ARMOR -> ARMOR_BONUS_ID;
        };
        var component = stack.getOrDefault(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT);
        int bonus = base;
        bonus += component.durabilityRepaired() > (OVERGROWN_THRESHOLD * 2) ? 2 : 1;
        return new EntityAttributeModifier(
            id,
            bonus,
            EntityAttributeModifier.Operation.ADD_VALUE);
    }

    public static EntityAttributeModifier createOvergrownToughnessModifier(ItemStack stack, int base) {
        var component = stack.getOrDefault(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT);
        int bonus = base;
        bonus += component.durabilityRepaired() > (OVERGROWN_THRESHOLD * 2) ? 2 : 1;
        return new EntityAttributeModifier(
            TOUGHNESS_BONUS_ID,
            bonus,
            EntityAttributeModifier.Operation.ADD_VALUE);
    }
}
