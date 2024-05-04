package nourl.mythicmetals.misc;

import com.mojang.serialization.MapCodec;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.util.RegistryAccess;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.item.*;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import nourl.mythicmetals.MythicMetals;

/**
 * A helper class containing methods for registering various blocks and items.
 * @author  Noaaan
 */
public class RegistryHelper {

    public static Identifier id(String path) {
        return new Identifier(MythicMetals.MOD_ID, path);
    }

    public static void item(String path, Item item) {
        Registry.register(Registries.ITEM, id(path), item);
    }

    public static void block(String path, Block block) {
        Registry.register(Registries.BLOCK, id(path), block);
        Registry.register(Registries.ITEM, id(path), new BlockItem(block, new OwoItemSettings().group(MythicMetals.TABBED_GROUP).tab(1)));
    }

    public static void block(String path, Block block, boolean fireproof) {
        if (fireproof) {
            Registry.register(Registries.BLOCK, id(path), block);
            Registry.register(Registries.ITEM, id(path), new BlockItem(block, new OwoItemSettings().group(MythicMetals.TABBED_GROUP).tab(1).fireproof()));
        } else {
            block(path, block);
        }
    }
    public static void block(String path, Block block, OwoItemGroup group) {
        Registry.register(Registries.BLOCK, id(path), block);
        Registry.register(Registries.ITEM, id(path), new BlockItem(block, new OwoItemSettings().group(group)));
    }

    public static void block(String path, Block block, OwoItemGroup group, boolean fireproof) {
        if (fireproof) {
            Registry.register(Registries.BLOCK, id(path), block);
            Registry.register(Registries.ITEM, id(path), new BlockItem(block, new OwoItemSettings().group(group).fireproof()));
        } else {
            block(path, block, group);
        }
    }

    public static void entityType(String path, EntityType<?> type){
        Registry.register(Registries.ENTITY_TYPE, RegistryHelper.id(path), type);
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureKey(String path) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, RegistryHelper.id(path));
    }

    public static LootConditionType lootConditionType(String path, MapCodec<? extends LootCondition> lootCodec) {
        return Registry.register(Registries.LOOT_CONDITION_TYPE, RegistryHelper.id(path), new LootConditionType(lootCodec));
    }

    public static void blockEntity(String path, BlockEntityType<?> type) {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, RegistryHelper.id(path), type);
    }

    public static RegistryEntry<EntityAttribute> entityAttribute(String path, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, id(path), attribute);
    }

    public static RegistryEntry<ArmorMaterial> getEntry(ArmorMaterial material) {
        return RegistryAccess.getEntry(Registries.ARMOR_MATERIAL, material);
    }
}
