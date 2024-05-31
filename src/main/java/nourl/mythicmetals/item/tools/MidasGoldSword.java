package nourl.mythicmetals.item.tools;

import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import nourl.mythicmetals.MythicMetals;
import nourl.mythicmetals.component.GoldFoldedComponent;
import nourl.mythicmetals.component.MythicDataComponents;
import nourl.mythicmetals.item.MythicItems;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;

public class MidasGoldSword extends SwordItem {
    public static final UUID DAMAGE_BONUS_UUID = UUID.fromString("b05f5f65-6abf-4d78-85c5-8d690f0c55ee");

    public MidasGoldSword(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient && MythicMetals.CONFIG.midasGold() && stack.contains(MythicDataComponents.GOLD_FOLDED) && stack.get(MythicDataComponents.GOLD_FOLDED).isRoyal() && target.isDead()) {
            target.dropItem(MythicItems.MIDAS_GOLD.getRawOre());
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void postProcessComponents(ItemStack stack) {
        var original = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        int goldCount = stack.getOrDefault(MythicDataComponents.GOLD_FOLDED, GoldFoldedComponent.of(0)).goldFolded();
        int bonus = MathHelper.clamp(MathHelper.floor((float) goldCount / 64), 0, 6);
        if (goldCount >= 1280) {
            bonus += 1;
        }

        if (bonus > 0) {
            var modifier = new EntityAttributeModifier(DAMAGE_BONUS_UUID, "midas_gold_attack_bonus", bonus, EntityAttributeModifier.Operation.ADD_VALUE);
            var changedComponent = original.with(EntityAttributes.GENERIC_ATTACK_DAMAGE, modifier, AttributeModifierSlot.MAINHAND).withShowInTooltip(false);
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, changedComponent);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> lines, TooltipType type) {
        if (stack.contains(MythicDataComponents.GOLD_FOLDED)) {
            stack.get(MythicDataComponents.GOLD_FOLDED).appendTooltip(context, lines::add, type);
        }
    }

    public static float countGold(int goldCount) {
        if (goldCount >= 1280) return 1.0f;
        return switch (goldCount / 64) {
            case 1 -> 0.1f;
            case 2, 3 -> 0.2f;
            case 4 -> 0.3f;
            case 5, 6, 7, 8, 9 -> 0.4f;
            case 10, 11 -> 0.5f;
            case 12, 13 -> 0.6f;
            case 14, 15 -> 0.7f;
            case 16, 17 -> 0.8f;
            case 18 -> 0.9f;
            case 19 -> 1.0f;
            default -> 0.0f;
        };
    }

    /**
     * Calculates a level from intervals of 64.
     * Used for appending specific text to a Midas Gold Sword tooltip
     *
     * @param goldCount The amount of gold that is currently applied on this stack
     * @return amount of gold divided by 64, or 0 if less than 64 gold
     */
    public static int calculateSwordLevel(int goldCount) {
        if (goldCount < 64) return 0;
        return (goldCount / 64);
    }

    public enum Type {
        REGULAR,
        GILDED,
        ROYAL;

        @Nullable
        public static MidasGoldSword.Type getSwordType(ItemStack stack) {
            return getSwordType(stack.getItem());
        }

        @Nullable
        public static MidasGoldSword.Type getSwordType(Item item) {

            if (item.equals(MythicTools.MIDAS_GOLD_SWORD)) {
                return REGULAR;
            }
            if (item.equals(MythicTools.GILDED_MIDAS_GOLD_SWORD)) {
                return GILDED;
            }
            if (item.equals(MythicTools.ROYAL_MIDAS_GOLD_SWORD)) {
                return ROYAL;
            }
            return null;
        }

        public static boolean isOf(ItemStack stack, Type type) {
            var comparedType = getSwordType(stack);
            if (comparedType != null) {
                return comparedType.equals(type);
            }
            return false;
        }
    }
}
