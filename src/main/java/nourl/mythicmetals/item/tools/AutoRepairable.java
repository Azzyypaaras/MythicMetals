package nourl.mythicmetals.item.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import static nourl.mythicmetals.item.tools.PrometheumToolSet.*;

/**
 * Used to handle Auto Repair behavior regarding Prometheum Tools
 * <br>
 * Override {@link Item#inventoryTick(ItemStack, World, Entity, int, boolean)}
 * and call {@link PrometheumToolSet#tickAutoRepair(ItemStack, World)} for auto repair handling
 */
public interface AutoRepairable extends FabricItem {

    @Override
    default boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getDamage() != newStack.getDamage();
    }

    @Override
    default boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getDamage() == newStack.getDamage();
    }

    /**
     * Adds extra attack damage depending on the {@link PrometheumToolSet#OVERGROWN_THRESHOLD}
     * @see PrometheumToolSet#createToolModifier(ItemStack)
     */
    @Override
    default Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND || (stack.get(DURABILITY_REPAIRED) < OVERGROWN_THRESHOLD)) return FabricItem.super.getAttributeModifiers(stack, slot);

        var modifiers = HashMultimap.create(FabricItem.super.getAttributeModifiers(stack, slot));

        modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, createToolModifier(stack));

        return modifiers;
    }
}
