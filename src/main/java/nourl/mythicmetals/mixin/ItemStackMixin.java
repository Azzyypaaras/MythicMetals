package nourl.mythicmetals.mixin;

//@Mixin(ItemStack.class)
public class ItemStackMixin {
//
//    /**
//     * @author noaaan
//     * @reason uses {@link java.util.UUID#equals(Object)} instead of reference equality, since otherwise it can break
//     */
//    @Overwrite
//    private void appendAttributeModifierTooltip(Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier
//    ) {
//        double d = modifier.value();
//        boolean bl = false;
//        if (player != null) {
//            // Overwritten lines
//            if (modifier.uuid().equals(Item.ATTACK_DAMAGE_MODIFIER_ID)) {
//                d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
//                d += EnchantmentHelper.getAttackDamage(((ItemStack) (Object) this), null);
//                bl = true;
//            } else if (modifier.uuid().equals(Item.ATTACK_SPEED_MODIFIER_ID)) {
//                d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED);
//                bl = true;
//            }
//        }
//
//        double e;
//        if (modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
//            || modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
//            e = d * 100.0;
//        } else if (attribute.matches(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)) {
//            e = d * 10.0;
//        } else {
//            e = d;
//        }
//
//        if (bl) {
//            textConsumer.accept(
//                ScreenTexts.space()
//                    .append(
//                        Text.translatable(
//                            "attribute.modifier.equals." + modifier.operation().getId(),
//                            AttributeModifiersComponent.DECIMAL_FORMAT.format(e),
//                            Text.translatable(attribute.value().getTranslationKey())
//                        )
//                    )
//                    .formatted(Formatting.DARK_GREEN)
//            );
//        } else if (d > 0.0) {
//            textConsumer.accept(
//                Text.translatable(
//                        "attribute.modifier.plus." + modifier.operation().getId(),
//                        AttributeModifiersComponent.DECIMAL_FORMAT.format(e),
//                        Text.translatable(attribute.value().getTranslationKey())
//                    )
//                    .formatted(Formatting.BLUE)
//            );
//        } else if (d < 0.0) {
//            textConsumer.accept(
//                Text.translatable(
//                        "attribute.modifier.take." + modifier.operation().getId(),
//                        AttributeModifiersComponent.DECIMAL_FORMAT.format(-e),
//                        Text.translatable(attribute.value().getTranslationKey())
//                    )
//                    .formatted(Formatting.RED)
//            );
//        }
//    }
}
