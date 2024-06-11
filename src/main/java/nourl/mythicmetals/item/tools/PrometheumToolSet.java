package nourl.mythicmetals.item.tools;

import io.wispforest.owo.nbt.NbtKey;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import nourl.mythicmetals.data.MythicTags;
import java.util.UUID;

/**
 * Prometheum Tools use the {@link AutoRepairable} interface and
 * {@link MythicTags#PROMETHEUM_TOOLS} tag in order to provide their functionality
 */
public class PrometheumToolSet extends ToolSet {
    public static final NbtKey<Integer> DURABILITY_REPAIRED = new NbtKey<>("DurabilityRepaired", NbtKey.Type.INT);
    public static final int OVERGROWN_THRESHOLD = 1200;

    public PrometheumToolSet(ToolMaterial material, int[] damage, float[] speed) {
        super(material, damage, speed);
    }

    @Override
    protected PickaxeItem makePickaxe(ToolMaterial material, int damage, float speed, Item.Settings settings) {
        return new PrometheumPick(material, damage, speed, settings);
    }

    @Override
    protected SwordItem makeSword(ToolMaterial material, int damage, float speed, Item.Settings settings) {
        return new PrometheumSword(material, damage, speed, settings);
    }

    @Override
    protected AxeItem makeAxe(ToolMaterial material, int damage, float speed, Item.Settings settings) {
        return new PrometheumAxe(material, damage, speed, settings);
    }

    @Override
    protected HoeItem makeHoe(ToolMaterial material, int damage, float speed, Item.Settings settings) {
        return new PrometheumHoe(material, damage, speed, settings);
    }

    @Override
    protected ShovelItem makeShovel(ToolMaterial material, int damage, float speed, Item.Settings settings) {
        return new PrometheumShovel(material, damage, speed, settings);
    }

    public static class PrometheumAxe extends AxeItem implements AutoRepairable {
        public PrometheumAxe(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
            super(material, attackDamage, attackSpeed, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            tickAutoRepair(stack, world);
            super.inventoryTick(stack, world, entity, slot, selected);
        }
    }

    public static class PrometheumHoe extends HoeItem implements AutoRepairable {
        public PrometheumHoe(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
            super(material, attackDamage, attackSpeed, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            tickAutoRepair(stack, world);
            super.inventoryTick(stack, world, entity, slot, selected);
        }
    }

    public static class PrometheumPick extends PickaxeItem implements AutoRepairable {
        public PrometheumPick(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
            super(material, attackDamage, attackSpeed, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            tickAutoRepair(stack, world);
            super.inventoryTick(stack, world, entity, slot, selected);
        }
    }

    public static class PrometheumShovel extends ShovelItem implements AutoRepairable {
        public PrometheumShovel(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
            super(material, attackDamage, attackSpeed, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            tickAutoRepair(stack, world);
            super.inventoryTick(stack, world, entity, slot, selected);
        }
    }

    public static class PrometheumSword extends SwordItem implements AutoRepairable {
        public PrometheumSword(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
            super(material, attackDamage, attackSpeed, settings);
        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            tickAutoRepair(stack, world);
            super.inventoryTick(stack, world, entity, slot, selected);
        }
    }

    public static void incrementRepairCounter(ItemStack stack, int value) {
        int counter = stack.get(DURABILITY_REPAIRED);
        if (counter < (Integer.MAX_VALUE / 2)) {
            stack.put(DURABILITY_REPAIRED, counter + value);
        }
    }

    public static boolean isOvergrown(ItemStack stack) {
        return stack.has(DURABILITY_REPAIRED) && stack.get(DURABILITY_REPAIRED) > OVERGROWN_THRESHOLD;
    }

    /**
     * Applies auto repair onto the item in question
     *
     * @param stack ItemStack to repair
     * @param world The world the item stack exists in
     */
    public static void tickAutoRepair(ItemStack stack, World world) {
        if (world.isClient()) return;
        var random = world.getRandom();
        if (!stack.isDamaged()) return; // Don't handle auto repair if item is fully repaired

        if (!stack.has(PrometheumToolSet.DURABILITY_REPAIRED)) {
            stack.put(PrometheumToolSet.DURABILITY_REPAIRED, 0);
        }

        var dmg = stack.getDamage();
        var rng = random.nextInt(200);

        if (rng != 177) return; // Roll for repair, ignore if roll fails. Number is arbitrary

        // Overgrown Items repair faster
        int damageToRepair = PrometheumToolSet.isOvergrown(stack) ? 2 : 1;

        // Extra repair speed if bound
        if (stack.isIn(MythicTags.PROMETHEUM_ARMOR) && EnchantmentHelper.hasBindingCurse(stack)) {
            damageToRepair += 1;
        }

        int newDamage = MathHelper.clamp(dmg - damageToRepair, 0, Integer.MAX_VALUE);
        stack.setDamage(newDamage);

        PrometheumToolSet.incrementRepairCounter(stack, damageToRepair);
    }

    /**
     * Create and return a modifier for extra attack damage depending on durability repaired
     * @param stack Used to query the NBT on the tool
     */
    public static EntityAttributeModifier createToolModifier(ItemStack stack) {
        return new EntityAttributeModifier(
                UUID.fromString("69def8b1-1baa-401e-a7cb-b27ab9a55558"),
                "Overgrown Prometheum bonus",
                (stack.get(DURABILITY_REPAIRED) > (OVERGROWN_THRESHOLD * 2)) ? 2 : 1,
                EntityAttributeModifier.Operation.ADDITION);
    }

}
