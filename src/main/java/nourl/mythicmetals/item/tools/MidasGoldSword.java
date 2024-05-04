package nourl.mythicmetals.item.tools;

import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import nourl.mythicmetals.MythicMetals;
import nourl.mythicmetals.item.MythicItems;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class MidasGoldSword extends SwordItem {
    /**
     * Counter that tracks how much gold is folded on the sword.
     * Used for dynamically changing damage and transforming the sword
     * @deprecated will be replaced by "mm_gold_folded"
     */
    @Deprecated
    public static final KeyedEndec<Integer> GOLD_FOLDED = new KeyedEndec<>("GoldFolded", Endec.INT, 0);
    /**
     * Tracks if the sword is gilded, so that the upgrade text after transforming into a Royal Midas Gold Sword changes
     * @deprecated will be replaced by "mm_is_gilded_midas"
     */
    @Deprecated
    public static final KeyedEndec<Boolean> IS_GILDED = new KeyedEndec<>("IsGilded", Endec.BOOLEAN, false);
    /**
     * Tracks if the sword is royal, which causes the sword to drop Raw Midas Gold on mob kills
     * @deprecated will be replaced by "mm_is_royal_midas"
     */
    @Deprecated
    public static final KeyedEndec<Boolean> IS_ROYAL = new KeyedEndec<>("IsRoyal", Endec.BOOLEAN, false);

    public MidasGoldSword(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient && MythicMetals.CONFIG.midasGold() && stack.has(IS_ROYAL) && stack.get(IS_ROYAL) && target.isDead()) {
            target.dropItem(MythicItems.MIDAS_GOLD.getRawOre());
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers(ItemStack stack) {
        // TODO - Reimplement attack damage increase on folded gold
//        Multimap<EntityAttribute, EntityAttributeModifier> mapnite = this.getAttributeModifiers(slot);
//
//        int goldCount = stack.get(GOLD_FOLDED);
//        if (goldCount > 0) {
//
//            mapnite = HashMultimap.create(mapnite);
//
//            // Store and clear so that we can modify the vanilla attack damage modifier independently
//            var damageValues = mapnite.get(EntityAttributes.GENERIC_ATTACK_DAMAGE);
//            mapnite.removeAll(EntityAttributes.GENERIC_ATTACK_DAMAGE);
//
//            float baseDamage = getAttackDamage();
//            int bonus = MathHelper.clamp(MathHelper.floor((float) goldCount / 64), 0, 6);
//            if (goldCount >= 1280) {
//                bonus += 1;
//            }
//            mapnite.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(Item.ATTACK_DAMAGE_MODIFIER_ID, "Damage modifier", baseDamage + bonus, EntityAttributeModifier.Operation.ADDITION));
//
//            var finalMapnite = mapnite;
//            damageValues.forEach(entityAttributeModifier -> finalMapnite.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, entityAttributeModifier));
//        }
//        return slot == EquipmentSlot.MAINHAND ? mapnite : super.getAttributeModifiers(slot);
        return super.getAttributeModifiers(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> lines, TooltipType type) {
        int lineIndex = 1;

        if (lines.size() > 2) {
            var enchantCount = stack.getEnchantments().getSize();
            lineIndex = enchantCount + 1;
        }

        int goldCount = stack.get(MidasGoldSword.GOLD_FOLDED);
        int level = calculateSwordLevel(goldCount);

        if (level > 20) {
            level = 20 + level / 6;
        }

        if (goldCount < 704 && stack.get(MidasGoldSword.IS_ROYAL)) {
            level = 11;
        }

        // Spout fun facts and lore while leveling up the sword
        lines.add(lineIndex, Text.translatable("tooltip.midas_gold.level." + level).formatted(Formatting.GOLD));
        if (goldCount == 0) {
            return;
        }

        // Remove the cap from tooltip when maxed
        if (goldCount >= 1280) {
            if (goldCount == 10000) {
                // e.g. **⭐10000 FOLDS - MAXED⭐**
                lines.add(lineIndex + 1, Text.translatable("tooltip.midas_gold.maxed", goldCount).formatted(Formatting.GOLD, Formatting.BOLD));
            } else {
                // e.g. Folds: 2500
                lines.add(lineIndex + 1, Text.translatable("tooltip.midas_gold.fold_counter", goldCount).formatted(Formatting.GOLD));
            }
            return;
        }

        // Handle the cap format
        if (stack.has(IS_ROYAL)) {
            // e.g. 63/1280
            lines.add(lineIndex + 1, Text.literal(goldCount + " / " + 1280).formatted(Formatting.GOLD));
        } else if (stack.has(IS_GILDED)) {
            // e.g. 63/640
            lines.add(lineIndex + 1, Text.literal(goldCount + " / " + 640).formatted(Formatting.GOLD));
        } else {
            // e.g. 63/128
            lines.add(lineIndex + 1, Text.literal(goldCount + " / " + (64 + level * 64)).formatted(Formatting.GOLD));
        }
    }

    public static float countGold(int goldCount) {
        if (goldCount >= 1280) return 1.0f;
        return switch (goldCount / 64) {
            case 1 -> 0.1f;
            case 2, 3 -> 0.2f;
            case 4 -> 0.3f;
            case 5, 6, 7, 8, 9 -> 0.4f;
            case 10,11 -> 0.5f;
            case 12,13 -> 0.6f;
            case 14,15 -> 0.7f;
            case 16,17 -> 0.8f;
            case 18 -> 0.9f;
            case 19 -> 1.0f;
            default -> 0.0f;
        };
    }

    /**
     * Calculates a level from intervals of 64.
     * Used for appending specific text to a Midas Gold Sword tooltip
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
