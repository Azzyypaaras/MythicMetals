package nourl.mythicmetals.data;

import io.wispforest.owo.util.ReflectionUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import nourl.mythicmetals.armor.ArmorSet;
import nourl.mythicmetals.armor.MythicArmor;
import nourl.mythicmetals.blocks.BlockSet;
import nourl.mythicmetals.blocks.MythicBlocks;
import nourl.mythicmetals.conditions.DustLoadedCondition;
import nourl.mythicmetals.conditions.NuggetsLoadedCondition;
import nourl.mythicmetals.item.ItemSet;
import nourl.mythicmetals.item.MythicItems;
import nourl.mythicmetals.misc.RegistryHelper;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MythicRecipeProviders extends FabricRecipeProvider {

    public MythicRecipeProviders(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void generate(RecipeExporter exporter) {
        var itemSets = new HashMap<String, ItemSet>();

        // Handle items first, as they store whether the items need blasting to be smelted
        ReflectionUtils.iterateAccessibleStaticFields(MythicItems.class, ItemSet.class, (itemSet, name, field) -> {
            itemSets.put(name, itemSet);
            if (itemSet.getRawOre() != null) {
                if (!itemSet.requiresBlasting()) {
                    CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(itemSet.getRawOre()), RecipeCategory.MISC, itemSet.getIngot(), itemSet.getXp(), 200)
                        .criterion("has_material", conditionsFromItem(itemSet.getIngot()))
                        .offerTo(exporter, RegistryHelper.id("smelting/" + name.toLowerCase(Locale.ROOT) + "_from_raw_ore"));
                }
                CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(itemSet.getRawOre()), RecipeCategory.MISC, itemSet.getIngot(), itemSet.getXp(), 100)
                    .criterion("has_material", conditionsFromItem(itemSet.getIngot()))
                    .offerTo(exporter, RegistryHelper.id("blasting/" + name.toLowerCase(Locale.ROOT) + "_from_raw_ore"));
            }
            if (itemSet.getDust() != null) {
                if (!itemSet.requiresBlasting()) {
                    CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(itemSet.getDust()), RecipeCategory.MISC, itemSet.getIngot(), itemSet.getXp(), 200)
                        .criterion("has_material", conditionsFromItem(itemSet.getIngot()))
                        .offerTo(exporter, RegistryHelper.id("smelting/" + name.toLowerCase(Locale.ROOT) + "_from_dust"));
                }
                CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(itemSet.getDust()), RecipeCategory.MISC, itemSet.getIngot(), itemSet.getXp(), 100)
                    .criterion("has_material", conditionsFromItem(itemSet.getIngot()))
                    .offerTo(withConditions(exporter, new DustLoadedCondition()), RegistryHelper.id("blasting/" + name.toLowerCase(Locale.ROOT) + "_from_dust"));
            }
        });

        ReflectionUtils.iterateAccessibleStaticFields(MythicBlocks.class, BlockSet.class, (blockSet, name, field) -> {
            if (blockSet.getOre() != null && itemSets.containsKey(name)) {
                var oreList = new ArrayList<>(blockSet.getOreVariants());
                oreList.add(blockSet.getOre());
                var items = oreList.stream().map(Block::asItem).toList().toArray(new Item[0]);

                var itemSet = itemSets.get(name);
                var ingot = itemSet.getIngot();
                var xp = itemSet.getXp();
                boolean requiresBlasting = itemSet.requiresBlasting();

                if (!requiresBlasting) {
                    CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(items), RecipeCategory.MISC, ingot, xp, 200)
                        .criterion("has_material", conditionsFromItem(itemSets.get(name).getIngot()))
                        .offerTo(exporter, RegistryHelper.id("smelting/" + name.toLowerCase(Locale.ROOT) + "_from_ores"));
                }
                CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(items), RecipeCategory.MISC, ingot, xp, 100)
                    .criterion("has_material", conditionsFromItem(itemSet.getIngot()))
                    .offerTo(exporter, RegistryHelper.id("blasting/" + name.toLowerCase(Locale.ROOT) + "_from_ores"));
            }
        });

        ReflectionUtils.iterateAccessibleStaticFields(MythicArmor.class, ArmorSet.class, (armorSet, name, field) -> {
            if (itemSets.containsKey(name) && itemSets.get(name).getNugget() != null) {
                var itemSet = itemSets.get(name);
                boolean requiresBlasting = itemSet.requiresBlasting();
                var nugget = itemSet.getNugget();
                ItemConvertible[] armorItems = new ItemConvertible[0];
                armorItems = armorSet.getArmorItems().toArray(armorItems);

                if (!requiresBlasting) {
                    CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(armorItems), RecipeCategory.MISC, nugget, 0.1f, 200)
                        .criterion("has_material", conditionsFromItem(nugget))
                        .offerTo(exporter, RegistryHelper.id("smelting/" + name.toLowerCase(Locale.ROOT) + "_nugget_from_armor"));
                }
                CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(armorItems), RecipeCategory.MISC, nugget, 0.1f, 100)
                    .criterion("has_material", conditionsFromItem(nugget))
                    .offerTo(withConditions(exporter, new NuggetsLoadedCondition()), RegistryHelper.id("blasting/" + name.toLowerCase(Locale.ROOT) + "_nugget_from_armor"));
            }
        });

    }
}
