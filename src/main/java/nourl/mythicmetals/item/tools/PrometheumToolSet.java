package nourl.mythicmetals.item.tools;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import nourl.mythicmetals.misc.PrometheumHandler;

public class PrometheumToolSet extends ToolSet {

    public PrometheumToolSet(ToolMaterial material, int[] damage, float[] speed) {
        super(material, damage, speed);
    }

    @Override
    protected PickaxeItem makePickaxe(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new PrometheumPick(material, (OwoItemSettings) settings.attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed)));
    }

    @Override
    protected SwordItem makeSword(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new PrometheumSword(material, (OwoItemSettings) settings.attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed)));
    }

    @Override
    protected AxeItem makeAxe(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new PrometheumAxe(material, (OwoItemSettings) settings.attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed)));
    }

    @Override
    protected HoeItem makeHoe(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new PrometheumHoe(material, (OwoItemSettings) settings.attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed)));
    }

    @Override
    protected ShovelItem makeShovel(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new PrometheumShovel(material, (OwoItemSettings) settings.attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed)));
    }

    public static class PrometheumAxe extends AxeItem implements AutoRepairable {
        public PrometheumAxe(ToolMaterial material, OwoItemSettings settings) {
            super(material, settings);
        }

//        @Override
//        public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
//            if (slot != EquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot);
//
//            var modifiers = HashMultimap.create(super.getAttributeModifiers(slot));
//
//            if (PrometheumHandler.isOvergrown(stack)) {
//                modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, PrometheumHandler.createToolModifier(stack));
//            }
//
//            return modifiers;
//        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            if (!world.isClient()) PrometheumHandler.tickAutoRepair(stack, world.getRandom());
            super.inventoryTick(stack, world, entity, slot, selected);
        }
    }

    public static class PrometheumHoe extends HoeItem implements AutoRepairable {
        public PrometheumHoe(ToolMaterial material, OwoItemSettings settings) {
            super(material, settings);
        }

//        @Override
//        public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
//            if (slot != EquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot);
//
//            var modifiers = HashMultimap.create(super.getAttributeModifiers(slot));
//
//            if (PrometheumHandler.isOvergrown(stack)) {
//                modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, PrometheumHandler.createToolModifier(stack));
//            }
//
//            return modifiers;
//        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            if (!world.isClient()) PrometheumHandler.tickAutoRepair(stack, world.getRandom());
            super.inventoryTick(stack, world, entity, slot, selected);
        }
    }

    public static class PrometheumPick extends PickaxeItem implements AutoRepairable {
        public PrometheumPick(ToolMaterial material, OwoItemSettings settings) {
            super(material, settings);
        }

//        @Override
//        public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
//            if (slot != EquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot);
//
//            var modifiers = HashMultimap.create(super.getAttributeModifiers(slot));
//
//            if (PrometheumHandler.isOvergrown(stack)) {
//                modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, PrometheumHandler.createToolModifier(stack));
//            }
//
//            return modifiers;
//        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            if (!world.isClient()) PrometheumHandler.tickAutoRepair(stack, world.getRandom());
            super.inventoryTick(stack, world, entity, slot, selected);
        }
    }

    public static class PrometheumShovel extends ShovelItem implements AutoRepairable {
        public PrometheumShovel(ToolMaterial material, OwoItemSettings settings) {
            super(material, settings);
        }

//        @Override
//        public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
//            if (slot != EquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot);
//
//            var modifiers = HashMultimap.create(super.getAttributeModifiers(slot));
//
//            if (PrometheumHandler.isOvergrown(stack)) {
//                modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, PrometheumHandler.createToolModifier(stack));
//            }
//
//            return modifiers;
//        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            if (!world.isClient()) PrometheumHandler.tickAutoRepair(stack, world.getRandom());
            super.inventoryTick(stack, world, entity, slot, selected);
        }
    }

    public static class PrometheumSword extends SwordItem implements AutoRepairable {
        public PrometheumSword(ToolMaterial material, OwoItemSettings settings) {
            super(material, settings);
        }

//        @Override
//        public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
//            if (slot != EquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot);
//
//            var modifiers = HashMultimap.create(super.getAttributeModifiers(slot));
//
//            if (PrometheumHandler.isOvergrown(stack)) {
//                modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, PrometheumHandler.createToolModifier(stack));
//            }
//
//            return modifiers;
//        }

        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            if (!world.isClient()) PrometheumHandler.tickAutoRepair(stack, world.getRandom());
            super.inventoryTick(stack, world, entity, slot, selected);
        }
    }

}
