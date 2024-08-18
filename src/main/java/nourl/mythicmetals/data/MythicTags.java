package nourl.mythicmetals.data;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import nourl.mythicmetals.misc.RegistryHelper;

public class MythicTags {

    public static final TagKey<Item> AUTO_REPAIR = TagKey.of(RegistryKeys.ITEM, RegistryHelper.id("abilities/auto_repair"));
    public static final TagKey<Item> BONUS_FORTUNE = TagKey.of(RegistryKeys.ITEM, RegistryHelper.id("abilities/bonus_fortune"));
    public static final TagKey<Item> BONUS_LOOTING = TagKey.of(RegistryKeys.ITEM, RegistryHelper.id("abilities/bonus_looting"));
    public static final TagKey<Item> CARMOT_STAFF_BLOCKS = TagKey.of(RegistryKeys.ITEM, RegistryHelper.id("carmot_staff_blocks"));
    public static final TagKey<Item> COMMON_ARMOR = TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "armor"));
    public static final TagKey<Item> COMMON_TOOLS = TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "tools"));
    public static final TagKey<Item> TIDESINGER_CORAL = TagKey.of(RegistryKeys.ITEM, RegistryHelper.id("tidesinger_coral"));

    public static final TagKey<Block> ANVILS = TagKey.of(RegistryKeys.BLOCK, RegistryHelper.id("anvils"));
    public static final TagKey<Block> MYTHIC_ORES = TagKey.of(RegistryKeys.BLOCK, RegistryHelper.id("ores"));
    public static final TagKey<Block> NUKE_CORES = TagKey.of(RegistryKeys.BLOCK, RegistryHelper.id("nuke_cores"));
    public static final TagKey<Block> CARMOT_NUKE_IGNORED = TagKey.of(RegistryKeys.BLOCK, RegistryHelper.id("carmot_nuke_ignored"));
    public static final TagKey<Block> SPONGABLES = TagKey.of(RegistryKeys.BLOCK, RegistryHelper.id("spongables"));
    public static final TagKey<Block> INCORRECT_FOR_UNOBTAINIUM_ALLOY_TOOLS = TagKey.of(RegistryKeys.BLOCK, RegistryHelper.id("incorrect_for_unobtainium_alloy_tools"));

    public static final TagKey<Biome> PROMETHEUM_BIOMES = TagKey.of(RegistryKeys.BIOME, RegistryHelper.id("prometheum_biomes"));
    public static final TagKey<Biome> OSMIUM_BIOMES = TagKey.of(RegistryKeys.BIOME, RegistryHelper.id("osmium_biomes"));
    public static final TagKey<Biome> MYTHIC_ORE_BIOMES = TagKey.of(RegistryKeys.BIOME, RegistryHelper.id("mythic_ore_biomes"));
    public static final TagKey<Enchantment> SILK_TOUCH_LIKE = TagKey.of(RegistryKeys.ENCHANTMENT, RegistryHelper.id("silk_touch_like"));
    public static final TagKey<Block> MINEABLE_MYTHRIL_DRILL = TagKey.of(RegistryKeys.BLOCK, RegistryHelper.id("mineable/mythril_drill"));
}


