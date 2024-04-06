package nourl.mythicmetals.data;

import io.wispforest.owo.util.ReflectionUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import nourl.mythicmetals.armor.ArmorSet;
import nourl.mythicmetals.armor.MythicArmor;
import nourl.mythicmetals.blocks.BlockSet;
import nourl.mythicmetals.blocks.MythicBlocks;
import nourl.mythicmetals.item.ItemSet;
import nourl.mythicmetals.item.MythicItems;
import nourl.mythicmetals.item.tools.MythicTools;
import nourl.mythicmetals.item.tools.ToolSet;

import java.util.concurrent.CompletableFuture;

public class MythicItemDataProvider extends FabricTagProvider.ItemTagProvider {

    public MythicItemDataProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        ReflectionUtils.iterateAccessibleStaticFields(MythicBlocks.class, BlockSet.class, (blockSet, name, field) -> {
            if (blockSet.getOre() != null) {
                var string = name + "_ores";
                var modTag = MythicMetalsData.createModItemTag(string);
                var commonTag = MythicMetalsData.createCommonItemTag(string);
                var tagBuilder = getOrCreateTagBuilder(modTag).add(blockSet.getOre().asItem());
                getOrCreateTagBuilder(commonTag).addTag(modTag);

                if (!blockSet.getOreVariants().isEmpty()) {
                    blockSet.getOreVariants().forEach(block -> tagBuilder.add(block.asItem()));
                }
            }

            if (blockSet.getOreStorageBlock() != null) {
                var string = "raw_" + name + "_blocks";
                var modTag = MythicMetalsData.createModItemTag(string);
                var commonTag = MythicMetalsData.createCommonItemTag(string);
                getOrCreateTagBuilder(modTag).add(blockSet.getOreStorageBlock().asItem());
                getOrCreateTagBuilder(commonTag).addTag(modTag);
            }

            if (blockSet.getStorageBlock() != null) {
                var string = name + "_blocks";
                var modTag = MythicMetalsData.createModItemTag(string);
                var commonTag = MythicMetalsData.createCommonItemTag(string);
                getOrCreateTagBuilder(modTag).add(blockSet.getStorageBlock().asItem());
                getOrCreateTagBuilder(commonTag).addTag(modTag);
            }
        });

        ReflectionUtils.iterateAccessibleStaticFields(MythicItems.class, ItemSet.class, (itemSet, name, field) -> {
            /*
             * Create ingot tags. Example:
             * Adamantite Ingot is added to the following:
             * #mythicmetals:adamantite_ingots
             * #c:adamantite_ingots
             * #mythicmetals:ingots
             * At the end #mythicmetals:ingots is nested into #c:ingots
             */
            var modIngotTag = MythicMetalsData.createModItemTag("ingots");
            var commonIngotTag = MythicMetalsData.createCommonItemTag("ingots");
            if (itemSet.getIngot() != null) {
                // Star Platinum is explicitly named, so this is for handling that edge case
                var string = itemSet.equals(MythicItems.STAR_PLATINUM) ? name : name + "_ingots";
                var modTag = MythicMetalsData.createModItemTag(string);
                var commonTag = MythicMetalsData.createCommonItemTag(string);
                getOrCreateTagBuilder(modTag).add(itemSet.getIngot());
                getOrCreateTagBuilder(commonTag).addTag(modTag);
                getOrCreateTagBuilder(modIngotTag).add(itemSet.getIngot());
            }
            getOrCreateTagBuilder(commonIngotTag).addTag(modIngotTag);

            /*
             * Create raw ore tags. Example:
             * Raw Adamantite is added to the following:
             * #mythicmetals:raw_adamantite_ores
             * #c:raw_adamantite_ores
             * #mythicmetals:raw_ores
             */
            if (itemSet.getRawOre() != null) {
                var string = "raw_" + name + "_ores";
                var modRawOreTag = MythicMetalsData.createModItemTag("raw_ores");

                var modTag = MythicMetalsData.createModItemTag(string);
                var commonTag = MythicMetalsData.createCommonItemTag(string);
                getOrCreateTagBuilder(modTag).add(itemSet.getRawOre());
                getOrCreateTagBuilder(modRawOreTag).add(itemSet.getRawOre());
                getOrCreateTagBuilder(commonTag).addTag(modTag);
            }
        });

        ReflectionUtils.iterateAccessibleStaticFields(MythicItems.Mats.class, Item.class, (item, name, field) -> {
            if (item.equals(MythicItems.Mats.STARRITE) || item.equals(MythicItems.Mats.UNOBTAINIUM) || item.equals(MythicItems.Mats.MORKITE)) {
                var modTag = MythicMetalsData.createModItemTag(name);
                var commonTag = MythicMetalsData.createCommonItemTag(name);
                getOrCreateTagBuilder(modTag).add(item);
                getOrCreateTagBuilder(commonTag).addTag(modTag);
            } else {
                var rareMaterials = MythicMetalsData.createModItemTag("rare_materials");
                getOrCreateTagBuilder(rareMaterials).add(item);
            }
        });

        ReflectionUtils.iterateAccessibleStaticFields(MythicTools.class, ToolSet.class, (toolSet, name, field) -> {
            var modTag = MythicMetalsData.createModItemTag(name + "_tools");
            var modEquipmentTag = MythicMetalsData.createModItemTag(name + "_equipment");
            var modToolsTag = MythicMetalsData.createModItemTag("tools");
            var commonTag = MythicMetalsData.createCommonItemTag("tools");
            var commonEquipmentTag = MythicMetalsData.createCommonItemTag("equipment");

            getOrCreateTagBuilder(modTag).add(toolSet.getSword());
            getOrCreateTagBuilder(modToolsTag).add(toolSet.getSword());
            getOrCreateTagBuilder(modEquipmentTag).add(toolSet.getSword());
            getOrCreateTagBuilder(MythicMetalsData.createModItemTag("swords")).add(toolSet.getSword());
            getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("swords")).add(toolSet.getSword());

            getOrCreateTagBuilder(modTag).add(toolSet.getPickaxe());
            getOrCreateTagBuilder(modToolsTag).add(toolSet.getPickaxe());
            getOrCreateTagBuilder(modEquipmentTag).add(toolSet.getPickaxe());
            getOrCreateTagBuilder(MythicMetalsData.createModItemTag("pickaxes")).add(toolSet.getPickaxe());
            getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("pickaxes")).add(toolSet.getPickaxe());

            getOrCreateTagBuilder(modTag).add(toolSet.getAxe());
            getOrCreateTagBuilder(modToolsTag).add(toolSet.getAxe());
            getOrCreateTagBuilder(modEquipmentTag).add(toolSet.getAxe());
            getOrCreateTagBuilder(MythicMetalsData.createModItemTag("axes")).add(toolSet.getAxe());
            getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("axes")).add(toolSet.getAxe());

            getOrCreateTagBuilder(modTag).add(toolSet.getShovel());
            getOrCreateTagBuilder(modToolsTag).add(toolSet.getShovel());
            getOrCreateTagBuilder(modEquipmentTag).add(toolSet.getShovel());
            getOrCreateTagBuilder(MythicMetalsData.createModItemTag("shovels")).add(toolSet.getShovel());
            getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("shovels")).add(toolSet.getShovel());

            getOrCreateTagBuilder(modTag).add(toolSet.getHoe());
            getOrCreateTagBuilder(modToolsTag).add(toolSet.getHoe());
            getOrCreateTagBuilder(modEquipmentTag).add(toolSet.getHoe());
            getOrCreateTagBuilder(MythicMetalsData.createModItemTag("hoes")).add(toolSet.getHoe());
            getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("hoes")).add(toolSet.getHoe());

            getOrCreateTagBuilder(commonTag).addTag(modTag);
            getOrCreateTagBuilder(commonEquipmentTag).addTag(modEquipmentTag);
        });

        /*
         * Edge cases from Mythic Tools
         */
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("swords")).add(MythicTools.RED_AEGIS_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("swords")).add(MythicTools.RED_AEGIS_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("swords")).add(MythicTools.WHITE_AEGIS_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("swords")).add(MythicTools.WHITE_AEGIS_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("tools")).add(MythicTools.RED_AEGIS_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("tools")).add(MythicTools.RED_AEGIS_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("tools")).add(MythicTools.WHITE_AEGIS_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("tools")).add(MythicTools.WHITE_AEGIS_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("swords")).add(MythicTools.MIDAS_GOLD_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("swords")).add(MythicTools.MIDAS_GOLD_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("swords")).add(MythicTools.GILDED_MIDAS_GOLD_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("swords")).add(MythicTools.GILDED_MIDAS_GOLD_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("swords")).add(MythicTools.ROYAL_MIDAS_GOLD_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("swords")).add(MythicTools.ROYAL_MIDAS_GOLD_SWORD);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("pickaxes")).add(MythicTools.ORICHALCUM_HAMMER);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("pickaxes")).add(MythicTools.ORICHALCUM_HAMMER);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("pickaxes")).add(MythicTools.MYTHRIL_DRILL);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("pickaxes")).add(MythicTools.MYTHRIL_DRILL);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("arrows")).add(MythicTools.STAR_PLATINUM_ARROW);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("arrows")).add(MythicTools.STAR_PLATINUM_ARROW);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("arrows")).add(MythicTools.RUNITE_ARROW);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("arrows")).add(MythicTools.RUNITE_ARROW);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("arrows")).add(MythicTools.TIPPED_RUNITE_ARROW);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("arrows")).add(MythicTools.TIPPED_RUNITE_ARROW);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("shields")).add(MythicTools.STORMYX_SHIELD);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("shields")).add(MythicTools.STORMYX_SHIELD);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("tools")).add(MythicTools.CARMOT_STAFF);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("tools")).add(MythicTools.CARMOT_STAFF);

        ReflectionUtils.iterateAccessibleStaticFields(MythicArmor.class, ArmorSet.class, (armorSet, name, field) -> {
            var modTag = MythicMetalsData.createModItemTag(name + "_armor");
            var modArmorTag = MythicMetalsData.createModItemTag("armor");
            var modEquipmentTag = MythicMetalsData.createModItemTag(name + "_equipment");
            var commonTag = MythicMetalsData.createCommonItemTag("armor");
            var commonEquipmentTag = MythicMetalsData.createCommonItemTag("equipment");
            armorSet.getArmorItems().forEach(armorItem -> {
                getOrCreateTagBuilder(modTag).add(armorItem);
                getOrCreateTagBuilder(modEquipmentTag).add(armorItem);
            });
            getOrCreateTagBuilder(modArmorTag).addTag(modTag);
            getOrCreateTagBuilder(commonTag).addTag(modTag);
            getOrCreateTagBuilder(commonEquipmentTag).addTag(modEquipmentTag);
        });

        /*
         * Edge cases for Mythic Armor (The Celestium Elytra)
         */
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("celestium_equipment")).add(MythicArmor.CELESTIUM_ELYTRA);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("celestium_armor")).add(MythicArmor.CELESTIUM_ELYTRA);
        getOrCreateTagBuilder(MythicMetalsData.createModItemTag("elytra")).add(MythicArmor.CELESTIUM_ELYTRA);
        getOrCreateTagBuilder(MythicMetalsData.createCommonItemTag("elytra")).add(MythicArmor.CELESTIUM_ELYTRA);

        ReflectionUtils.iterateAccessibleStaticFields(MythicItems.Templates.class, Item.class, (item, name, field) -> {
            var modTag = MythicMetalsData.createModItemTag("smithing_templates");
            var commonTag = MythicMetalsData.createCommonItemTag("smithing_templates");
            getOrCreateTagBuilder(modTag).add(item);
            getOrCreateTagBuilder(commonTag).add(item);
        });

    }
}
