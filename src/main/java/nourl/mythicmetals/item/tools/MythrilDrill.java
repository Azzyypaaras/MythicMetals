package nourl.mythicmetals.item.tools;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import nourl.mythicmetals.abilities.DrillUpgrades;
import nourl.mythicmetals.blocks.MythicBlocks;
import nourl.mythicmetals.component.*;
import nourl.mythicmetals.item.MythicItems;
import nourl.mythicmetals.misc.PrometheumHandler;
import nourl.mythicmetals.registry.RegisterSounds;

import static nourl.mythicmetals.component.DrillComponent.*;

public class MythrilDrill extends PickaxeItem {
//    /**
//     * NbtKey that stores the amount of fuel inside the drill
//     *
//     * @deprecated Will be replaced with "mm_fuel" (lowercase)
//     */
//    @Deprecated
//    public static final KeyedEndec<Integer> FUEL = new KeyedEndec<>("Fuel", Endec.INT, 0);
//    /**
//     * KeyedEndec that determines whether the drill should consume fuel and mine faster
//     */
//    public static final KeyedEndec<Boolean> IS_ACTIVE = new KeyedEndec<>("mm_is_active", Endec.BOOLEAN, false);
//    /**
//     * Holds an item, which determines what upgrades the drill has
//     *
//     * @deprecated Will be replaced by "mm_upgrade_slot_one"
//     */
//    @Deprecated
//    public static final KeyedEndec<Item> UPGRADE_SLOT_ONE = new KeyedEndec<>("UpgradeSlot1", BuiltInEndecs.ofRegistry(Registries.ITEM), Items.AIR);
//    /**
//     * Holds another item, which determines what upgrades the drill has
//     *
//     * @deprecated Will be replaced by "mm_upgrade_slot_two"
//     */
//    public static final KeyedEndec<Item> UPGRADE_SLOT_TWO = new KeyedEndec<>("UpgradeSlot2", BuiltInEndecs.ofRegistry(Registries.ITEM), Items.AIR);

    public MythrilDrill(ToolMaterial material, Settings settings) {
        super(material, settings);
        settings.component(MythicDataComponents.DRILL, new DrillComponent(0, false));
        settings.component(MythicDataComponents.UPGRADES, UpgradeComponent.empty(2));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // If the Drill is in offhand, handle it normally when used on a block
        if (context.getHand().equals(Hand.OFF_HAND)) {
            return super.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
        }
        // If the Drill is used on block in mainhand, cancel the action if a block is in the offhand and use the block instead
        if (context.getHand().equals(Hand.MAIN_HAND) && context.getPlayer() != null) {
            var offhandStack = context.getPlayer().getStackInHand(Hand.OFF_HAND);
            if (offhandStack != null && offhandStack.getItem() != null && offhandStack.getItem() instanceof BlockItem blockItem) {
                blockItem.useOnBlock(new ItemUsageContext(context.getWorld(), context.getPlayer(), Hand.OFF_HAND, offhandStack, new BlockHitResult(context.getHitPos(), context.getSide(), context.getBlockPos(), context.hitsInsideBlock())));
                context.getPlayer().swingHand(Hand.OFF_HAND);
                return ActionResult.CONSUME_PARTIAL;
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        var offHandStack = user.getStackInHand(Hand.OFF_HAND);

        if (user.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            return TypedActionResult.pass(stack);
        }

        if (!offHandStack.equals(stack) && (respectFood(offHandStack, user)) || offHandStack.getUseAction().equals(UseAction.DRINK)) {
            return TypedActionResult.pass(stack);
        }

        // If you have fuel, toggle the state of the drill
        if (stack.getOrDefault(MythicDataComponents.DRILL, DEFAULT).hasFuel()) {
            toggleDrillState(world, user, stack);
            return TypedActionResult.success(stack);
        }

        if (world.isClient) {
            user.sendMessage(Text.translatable("tooltip.mythril_drill.out_of_fuel"), true);
            user.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), 0.8f, 0.5f);
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public boolean onStackClicked(ItemStack drill, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType == ClickType.RIGHT) {
            var drillComponent = drill.getOrDefault(MythicDataComponents.DRILL, DEFAULT);
            // If right-clicking Drill onto Morkite, try to fuel it
            if (slot.getStack().getItem().equals(MythicItems.Mats.MORKITE)) {
                int morkiteCount = slot.getStack().getCount();
                if (slot.tryTakeStackRange((MAX_FUEL - drillComponent.fuel()) / FUEL_CONSTANT, morkiteCount, player).isPresent()) {
                    int fuel = MathHelper.clamp(drillComponent.fuel() + (morkiteCount * FUEL_CONSTANT), 0, MAX_FUEL);
                    drill.set(MythicDataComponents.DRILL, new DrillComponent(fuel, drillComponent.isActive()));
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public boolean onClicked(ItemStack drill, ItemStack cursorStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT) {
            var cursorItem = cursorStack.getItem();
            // If right-clicking with Morkite on Drill, try to fuel it
            if (cursorItem.equals(MythicItems.Mats.MORKITE)) {
                var drillComponent = drill.getOrDefault(MythicDataComponents.DRILL, DEFAULT);

                // Don't bother interacting if the Drills fuel is full
                if (drillComponent.fuel() >= MAX_FUEL) return false;

                // Greedily take all the morkite if we can, otherwise calculate how much to take
                int morkiteCount = cursorStack.getCount();
                if (morkiteCount * FUEL_CONSTANT < (MAX_FUEL) - drillComponent.fuel()) {
                    int fuel = MathHelper.clamp(drillComponent.fuel() + (morkiteCount * FUEL_CONSTANT), 0, MAX_FUEL);
                    cursorStack.decrement(morkiteCount);
                    drill.set(MythicDataComponents.DRILL, new DrillComponent(fuel, drillComponent.isActive()));
                    return true;
                }
                // Manually calculate how much Morkite to take
                if (morkiteCount * FUEL_CONSTANT >= (MAX_FUEL) - drillComponent.fuel()) {
                    int morkiteToTake = (MAX_FUEL / FUEL_CONSTANT) - (drillComponent.fuel() / FUEL_CONSTANT);
                    int fuel = MathHelper.clamp(drillComponent.fuel() + (morkiteToTake * FUEL_CONSTANT), 0, MAX_FUEL);
                    cursorStack.decrement(morkiteToTake);
                    drill.set(MythicDataComponents.DRILL, new DrillComponent(fuel, drillComponent.isActive()));
                    return true;
                }
            }

            if (!drill.contains(MythicDataComponents.UPGRADES)) return false;

            var upgrades = drill.get(MythicDataComponents.UPGRADES);
            if (upgrades.hasFreeSlots()) {
                if (cursorItem.equals(Items.AIR)) return false;
                if (!DrillUpgrades.MAP.containsKey(cursorItem) || upgrades.hasUpgrade(cursorItem)) return false;
                // Apply drill upgrade
                cursorStack.decrement(1);
                drill.set(MythicDataComponents.UPGRADES, UpgradeComponent.put(upgrades, cursorItem));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
            // Randomly cancel damage while active
            var random = world.getRandom();
            var drillComponent = stack.getOrDefault(MythicDataComponents.DRILL, DEFAULT);
            var upgradeComponent = stack.getOrDefault(MythicDataComponents.UPGRADES, UpgradeComponent.empty(2));

            if (drillComponent.isActive() && random.nextInt(10) > 3) return true;
            stack.damage(1, miner, EquipmentSlot.MAINHAND);

            if (!stack.contains(DataComponentTypes.ENCHANTMENTS) && stack.get(DataComponentTypes.ENCHANTMENTS).getEnchantments().contains(Registries.ENCHANTMENT.getEntry(Enchantments.SILK_TOUCH)) && state.isIn(ConventionalBlockTags.ORES)) {
                // Restore air when mining ores underwater
                if (upgradeComponent.hasUpgrade(MythicItems.Mats.AQUARIUM_PEARL)) {
                    miner.setAir(Math.min(miner.getAir() + 24, miner.getMaxAir()));
                }
                // Randomly drop gold from midas gold
                if (upgradeComponent.hasUpgrade(MythicBlocks.ENCHANTED_MIDAS_GOLD_BLOCK.asItem()) && random.nextInt(40) == 27) {
                    miner.dropItem(Items.RAW_GOLD);
                }
            }
        }

        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient()) {
            if (stack.get(MythicDataComponents.DRILL) == null) return;
            if (stack.get(MythicDataComponents.UPGRADES) == null) return;
            var drillComponent = stack.getOrDefault(MythicDataComponents.DRILL, DEFAULT);
            var upgradeComponent = stack.getOrDefault(MythicDataComponents.UPGRADES, UpgradeComponent.empty(2));
            if (drillComponent.hasFuel() && drillComponent.isActive() && world.getTime() % (4 * FUEL_CONSTANT) == 1) {
                stack.set(MythicDataComponents.DRILL, new DrillComponent(drillComponent.fuel() - 1, true));
            }
            if (!drillComponent.hasFuel()) {
                stack.set(MythicDataComponents.DRILL, DrillComponent.DEFAULT);
            }

            if (upgradeComponent.hasUpgrade(MythicItems.Mats.PROMETHEUM_BOUQUET)) {
                PrometheumHandler.tickAutoRepair(stack, world.getRandom());
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        // TODO - Implement once fuel component is added
        return super.allowComponentsUpdateAnimation(player, hand, oldStack, newStack);
    }


    @Override
    public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        // TODO - Review if purely checking components is effective
        // Allow you to break blocks when fuel ticks down
        return oldStack.contains(MythicDataComponents.DRILL) && newStack.contains(MythicDataComponents.DRILL) || oldStack.getDamage() != newStack.getDamage();
    }

    @Override
    public boolean isCorrectForDrops(ItemStack stack, BlockState state) {
        if (stack.contains(MythicDataComponents.DRILL) && stack.get(MythicDataComponents.DRILL).isActive()) {
            if (state.isIn(BlockTags.SHOVEL_MINEABLE))
                return true;
        }
        return super.isCorrectForDrops(stack, state);
    }

//  TODO - Redundant? Can be overridden by ToolComponent Rules
//    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
//        if (isActive(stack)) {
//            if (state.isIn(BlockTags.SHOVEL_MINEABLE) && this.isSuitableFor(stack, state)) {
//                return this.miningSpeed;
//            } else return super.getMiningSpeedMultiplier(stack, state);
//        }
//        return super.getMiningSpeedMultiplier(stack, state) / 2.5F;
//    }

    /**
     * Handles applying the drill active state by toggling its NBT keys
     * Includes playing sound effects and applying a cooldown to prevent spam
     */
    public void toggleDrillState(World world, PlayerEntity user, ItemStack drill) {
        var drillComponent = drill.getOrDefault(MythicDataComponents.DRILL, DEFAULT);
        if (world.isClient) {
            var sound = drillComponent.isActive() ? RegisterSounds.MYTHRIL_DRILL_DEACTIVATE : RegisterSounds.MYTHRIL_DRILL_ACTIVATE;
            user.playSound(sound, 1.0f, 1.0f);
        }
        user.getItemCooldownManager().set(drill.getItem(), 20);
        drill.set(MythicDataComponents.DRILL, DrillComponent.toggleActiveState(drillComponent));
    }

    /**
     * Used for prioritizing eating food in off-hand over activating the drill
     *
     * @return whether you can eat the food-related itemstack in question
     */
    private boolean respectFood(ItemStack foodStack, PlayerEntity user) {
        return user.canConsume(foodStack.getItem().getComponents() != null && foodStack.getItem().getComponents().contains(DataComponentTypes.FOOD));
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers(ItemStack stack) {
//        var component = this.getAttributeModifiers(stack);
//        if (hasUpgradeItem(stack, MythicBlocks.ENCHANTED_MIDAS_GOLD_BLOCK.asItem())) {
//            mapnite.put(EntityAttributes.GENERIC_LUCK, new EntityAttributeModifier(UUID.fromString("dc61bf90-67b4-414e-8ecf-994065208b3e"), "Drill Luck", 2.0f, EntityAttributeModifier.Operation.ADDITION));
//        }
//        return slot == EquipmentSlot.MAINHAND ? mapnite : super.getAttributeModifiers(slot);
        // TODO - Reimplement adding luck on Enchanted Midas Gold Block Upgrade
        return super.getAttributeModifiers(stack);
    }
}
