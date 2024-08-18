package nourl.mythicmetals.item.tools;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import nourl.mythicmetals.MythicMetals;
import nourl.mythicmetals.data.MythicTags;

/**
 * Used to handle Auto Repair behavior regarding Prometheum Tools more gracefully
 * <br>
 * Adding your item to {@link MythicTags#AUTO_REPAIR} is enough to cause auto repair, as this adds
 * the component in {@link MythicMetals#registerPrometheumAttributeEvent()}
 */
public interface AutoRepairable extends FabricItem {

    // Don't interrupt mining if durability is repaired
    // Might affect mending as a side effect
    @Override
    default boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getDamage() != newStack.getDamage();
    }

    // Don't update the item in hand if durability is repaired
    // Might affect mending as a side effect
    @Override
    default boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getDamage() == newStack.getDamage();
    }
}
