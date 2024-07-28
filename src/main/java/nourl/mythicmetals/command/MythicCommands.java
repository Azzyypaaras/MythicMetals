package nourl.mythicmetals.command;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.wispforest.owo.util.ReflectionUtils;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.*;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.loot.context.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.command.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nourl.mythicmetals.MythicMetals;
import nourl.mythicmetals.armor.ArmorSet;
import nourl.mythicmetals.armor.MythicArmor;
import nourl.mythicmetals.blocks.BlockSet;
import nourl.mythicmetals.blocks.MythicBlocks;
import nourl.mythicmetals.config.MythicOreConfigs;
import nourl.mythicmetals.config.OreConfig;
import nourl.mythicmetals.item.tools.MythicTools;
import nourl.mythicmetals.misc.RegistryHelper;
import nourl.mythicmetals.misc.StringUtilsAtHome;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"UnstableApiUsage", "CodeBlock2Expr", "RedundantSuppression"})
public final class MythicCommands {

    public static final String ITEM_SCALE = "{ .sized-image style=\"--image-width: 40%;\" }";
    public static final String RECIPE_SCALE = "{ .sized-recipe style=\"--image-width: 40%;\" }";
    public static final String ICON_SCALE = "{ .sized-image style=\"--image-width: 8%;\" }";
    public static final String BR = "<br>\n";
    public static final String TAB = "    "; // MkDocs likes spaces over tabs
    public static BiMap<String, OreConfig> ORECONFIG = HashBiMap.create();

    private MythicCommands() {
    }

    @SuppressWarnings("UnreachableCode")
    public static void init() {
        ReflectionUtils.iterateAccessibleStaticFields(MythicOreConfigs.class, OreConfig.class, (value, name, field) -> {
            ORECONFIG.put(name, value);
        });
        ArgumentTypeRegistry.registerArgumentType(RegistryHelper.id("toolset"), ToolSetArgumentType.class, ConstantArgumentSerializer.of(ToolSetArgumentType::toolSet));
        ArgumentTypeRegistry.registerArgumentType(RegistryHelper.id("armorset"), ArmorSetArgumentType.class, ConstantArgumentSerializer.of(ArmorSetArgumentType::armorSet));
        ArgumentTypeRegistry.registerArgumentType(RegistryHelper.id("ore-config"), OreConfigArgumentType.class, ConstantArgumentSerializer.of(OreConfigArgumentType::oreConfig));
        ArgumentTypeRegistry.registerArgumentType(RegistryHelper.id("blockset"), BlockSetArgumentType.class, ConstantArgumentSerializer.of(BlockSetArgumentType::blockSet));
    }

    // TODO - Add new command for grabbing the data-generated ore features, and create a datapack skeleton
    public static void registerCommands() {
        var mythicRoot = CommandManager.literal("mythicmetals").requires(src -> src.hasPermissionLevel(2)).build();
        var range = CommandManager.literal("range").build();
        var tools = CommandManager.literal("tools").build();
        var ores = CommandManager.literal("ores").build();
        var armor = CommandManager.literal("armor").build();
        var wiki = CommandManager.literal("wiki").build();
        var armorStand = CommandManager.literal("armor-stand").build();
        var loot = CommandManager.literal("test-loot-table").build();
        var placeBlocks = CommandManager.literal("place-all-blocks").executes(context -> placeAllBlocksets(context, Map.of()))
            .build();

        // TODO - Make this useful command more useful for current use-cases:
        // TODO -- Allow dumping output in a spreadsheet friendly format
        var rangeType = CommandManager.argument("type", StringArgumentType.word())
            .suggests(MythicCommands::dumpType)
            .executes(MythicCommands::dumpAllOreConfigs)
            .build();

        var exportOres = CommandManager.argument("ore-config", OreConfigArgumentType.oreConfig())
            .executes(MythicCommands::exportOreAdmonition)
            .build();
        var exportTools = CommandManager.argument("toolset", ToolSetArgumentType.toolSet())
            .executes(MythicCommands::exportTools)
            .build();

        var exportArmor = CommandManager.argument("armorset", ArmorSetArgumentType.armorSet())
            .executes(MythicCommands::exportArmor)
            .build();

        var lootTables = CommandManager.argument("loot_table", IdentifierArgumentType.identifier())
            .suggests(LootCommand.SUGGESTION_PROVIDER)
            .then(CommandManager.argument("rolls", IntegerArgumentType.integer())
                .executes(MythicCommands::testLootTable))
            .build();

        var trimPattern = CommandManager.argument("trim_pattern", StringArgumentType.word())
            .suggests(MythicCommands::trimTypes)
            .executes(context -> {
                String matQuery = StringArgumentType.getString(context, "material");
                String trimQuery = StringArgumentType.getString(context, "trim_pattern");
                return armorStandCommand(context, matQuery, trimQuery);
            });

        var summonTrims = CommandManager.argument("material", StringArgumentType.word())
            .suggests(MythicCommands::armorMaterial)
            .executes(context -> {
                String mat = StringArgumentType.getString(context, "material");
                return armorStandCommand(context, mat, null);
            })
            .then(trimPattern)
            .build();

        // Wiki nodes
        ores.addChild(exportOres);
        tools.addChild(exportTools);
        armor.addChild(exportArmor);
        wiki.addChild(ores);
        wiki.addChild(tools);
        wiki.addChild(armor);

        // Misc nodes
        range.addChild(rangeType);
        loot.addChild(lootTables);
        armorStand.addChild(summonTrims);

        // Add commands to root
        mythicRoot.addChild(range);
        mythicRoot.addChild(wiki);
        mythicRoot.addChild(armorStand);
        mythicRoot.addChild(loot);
        mythicRoot.addChild(placeBlocks);

        CommandRegistrationCallback.EVENT.register((dispatcher, access, env) -> {
            dispatcher.getRoot().addChild(mythicRoot);
        });
    }

    /**
     * Place every block set from {@link MythicBlocks} across the YZ axis
     *
     * @param context     ServerCommandSource Command Context
     * @param extraBlocks Map which can be used to insert extra blocks for a specific block set
     */
    public static int placeAllBlocksets(CommandContext<ServerCommandSource> context, Map<String, ArrayList<Block>> extraBlocks) {
        var source = context.getSource();
        var world = source.getWorld();
        AtomicInteger x = new AtomicInteger(((int) source.getPosition().x));
        AtomicInteger y = new AtomicInteger(((int) source.getPosition().y));
        int z = ((int) source.getPosition().z);
        ReflectionUtils.iterateAccessibleStaticFields(MythicBlocks.class, BlockSet.class, (blockSet, name, field) -> {
            y.set(((int) source.getPosition().y));
            if (blockSet.getOre() != null) {
                world.setBlockState(BlockPos.ofFloored(x.get(), y.getAndIncrement(), z), blockSet.getOre().getDefaultState());
            }
            blockSet.getOreVariants().forEach(block -> {
                world.setBlockState(BlockPos.ofFloored(x.get(), y.getAndIncrement(), z), block.getDefaultState());
            });
            if (blockSet.getOreStorageBlock() != null) {
                world.setBlockState(BlockPos.ofFloored(x.get(), y.getAndIncrement(), z), blockSet.getOreStorageBlock().getDefaultState());
            }
            if (blockSet.getStorageBlock() != null) {
                world.setBlockState(BlockPos.ofFloored(x.get(), y.getAndIncrement(), z), blockSet.getStorageBlock().getDefaultState());
            }
            if (blockSet.getAnvil() != null) {
                world.setBlockState(BlockPos.ofFloored(x.get(), y.getAndIncrement(), z), blockSet.getAnvil().getDefaultState());
            }
            if (extraBlocks.containsKey(name)) {
                extraBlocks.get(name).forEach(extraBlock -> {
                    world.setBlockState(BlockPos.ofFloored(x.get(), y.getAndIncrement(), z), extraBlock.getDefaultState());
                });
            }
            x.incrementAndGet();
        });
        source.sendFeedback(() -> Text.literal("Placed all blocksets starting at %s,%s,%s".formatted(source.getPosition().x, source.getPosition().y, source.getPosition().z)), true);
        return 0;
    }

    /**
     * Command which generates loot from a loot table X amount of times, and prints the output to the console
     */
    private static int testLootTable(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        try {
            var lootTable = RegistryEntryArgumentType.LootTableArgumentType.getLootTable(ctx, "loot_table");

        int rolls = IntegerArgumentType.getInteger(ctx, "rolls");

        LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(ctx.getSource().getWorld())
            .addOptional(LootContextParameters.THIS_ENTITY, source.getEntity())
            .add(LootContextParameters.ORIGIN, source.getPosition())
            .build(LootContextTypes.CHEST);

        HashMap<Item, Integer> map = new HashMap<>();

        for (int i = 0; i < rolls; i++) {
            List<ItemStack> list = lootTable.value().generateLoot(lootContextParameterSet);
            list.forEach(itemStack -> {
                int count = map.getOrDefault(itemStack.getItem(), 0);
                map.put(itemStack.getItem(), itemStack.getCount() + count);
            });
        }

        map.forEach((item, integer) -> {
            source.sendFeedback(() -> (Text.literal(item + ": " + integer.toString())), false);
        });
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    /**
     * ArmorSet exporter for the Mythic Metals Wiki
     * <br><br>
     * Note that it does not handle abilities or custom attributes, like Lava Swim Speed
     */
    private static int exportArmor(CommandContext<ServerCommandSource> context) {
        var armorSet = ArmorSetArgumentType.getArmorSet(context, "armorset");
        var source = context.getSource();

        StringBuilder output = new StringBuilder();

        // Armor Model on top of the admonition
        String armorMaterial = Registries.ITEM.getId(armorSet.getHelmet()).getPath().split("_helmet")[0];
        String armorTypeName = StringUtilsAtHome.toProperCase(armorMaterial.replace("_", " ") + " Armor");

        output.append("\n");
        output.append("<center class=tooltip>").append("\n");
        output.append("<h3>**").append(armorTypeName).append("**</h3>").append("\n");
        output.append("![Image of %s model](../assets/armor-models/256/%s.png)".formatted(armorTypeName, armorMaterial + "_256")).append("\n");

        for (var armor : armorSet.getArmorItems()) {
            /* TODO - The string handling is a bit bad, although it does have to be very specific in regards
             * to be implemented correctly into MkDocs. Try to improve this later.
             */
            String id = Registries.ITEM.getId(armor).getPath();
            String name = StringUtilsAtHome.toProperCase(id.replace('_', ' '));

            int protection = armor.getProtection();

            output.append("\n");
            output.append("<center class=tooltip>").append("\n");
            output.append("<h4>**").append(name).append("**</h4>").append("\n");
            output.append("![Image of %s](../assets/mythicmetals/%s.png)".formatted(name, id)).append(ITEM_SCALE).append(BR);
            for (int i = 1; i < protection; i = i + 2) {
                output.append("![armor](../assets/icon/full_armor_icon.png)").append(ICON_SCALE).append("\n");
            }
            if ((protection & 1) == 1) {
                output.append("![armor](../assets/icon/half_armor_icon.png)").append(ICON_SCALE).append("\n");
            }
            output.append(BR);
            // +5 Armor, +2 Toughness
            output.append("+%s Armor".formatted(protection));
            if (armor.getToughness() > 0) {
                output.append(", +%s Toughness".formatted(armor.getToughness()));
            }
            output.append(BR);
            if (armor.getMaterial().value().knockbackResistance() > 0) {
                output.append("+%s Knockback Resistance").append(BR);
            }
            // 350 Durability
            output.append("%s Durability".formatted(armor.getDefaultStack().getMaxDamage())).append(BR);
        }

        // armor recipes
        output.append("\n").append("===RECIPE===").append("\n");
        for (var armor : armorSet.getArmorItems()) {
            String id = Registries.ITEM.getId(armor).getPath();
            String name = StringUtilsAtHome.toProperCase(id.replace('_', ' '));
            output.append("![Image of the recipe for %s](../assets/mythicmetals/recipes/armor/%s.png)".formatted(name, id)).append(RECIPE_SCALE).append(BR);
        }
        output.append("\n").append("===RECIPE END===").append("\n");

        MythicMetals.LOGGER.info(output);
        source.sendFeedback(() -> Text.literal("Exported armor to wiki format in logs"), false);
        return 0;
    }

    /**
     * Ore/Material exporter for the Mythic Metals Wiki
     */
    private static int exportOreAdmonition(CommandContext<ServerCommandSource> context) {
        var oreConfig = OreConfigArgumentType.getOreConfig(context, "ore-config");
        var source = context.getSource();
        var blockSet = MythicBlocks.BLOCKSET_MAP.get(ORECONFIG.inverse().get(oreConfig));

        StringBuilder output = new StringBuilder();

        if (blockSet == null || blockSet.getOre() == null) {
            output.append("\n--- ORE STATS ---\n");
            output.append(TAB).append("**Mining Level**: X (Y for variant)").append(BR);
            output.append(TAB).append("**Max Vein Size**: %s".formatted(oreConfig.veinSize)).append(BR);
            output.append(TAB).append("**Spawn Range**: %s to %s".formatted(
                    oreConfig.bottom + (oreConfig.offset ? "(Offset)" : ""),
                    oreConfig.top + (oreConfig.trapezoid ? " (Triangle Range)" : "")
                )
            ).append(BR);
            output.append(TAB).append("**Discard Chance**: %s".formatted(
                oreConfig.discardChance == 0 ? "Never discarded" : oreConfig.discardChance * 100 + "%")
            ).append(BR);
            MythicMetals.LOGGER.info(output);
            source.sendFeedback(() -> Text.literal("Exported stats for the provided OreConfig"), false);
            return 1;
        }

        String oreName = StringUtilsAtHome.toProperCase(blockSet.getName() + " Ore");

        // Ore names and images
        output.append("!!! info inline end \"\"").append("\n");
        output.append(TAB).append("<center class=tooltip>").append("\n");
        output.append(TAB).append("<h3>**").append(oreName).append("**</h3>").append("\n");
        output.append(TAB).append("![WRITE ALT TEXT HERE](%s)"
            .formatted("../assets/mythicmetals/" + blockSet.getName() + "_ore.png")
        ).append(BR);
        blockSet.getOreVariantsMap().forEach((variantName, block) -> {
            String variantOreName = StringUtilsAtHome.toProperCase(variantName + " " + blockSet.getName() + " Ore");

            output.append(TAB).append("<h3>**").append(variantOreName).append("**</h3>").append("\n");
            output.append(TAB).append("![WRITE ALT TEXT HERE](%s)"
                .formatted("../assets/mythicmetals/" + variantName + "_" + blockSet.getName() + "_ore.png")
            ).append("\n");
        });
        // Ore Stats
        output.append(TAB).append("---\n");
        output.append(TAB).append("**Mining Level**: X (Y for variant)").append(BR);
        output.append(TAB).append("**Max Vein Size**: %s".formatted(oreConfig.veinSize)).append(BR);
        output.append(TAB).append("**Spawn Range**: %s to %s".formatted(
                oreConfig.bottom + (oreConfig.offset ? "(Offset)" : ""),
                oreConfig.top + (oreConfig.trapezoid ? " (Triangle Range)" : "")
            )
        ).append(BR);
        output.append(TAB).append("**Discard Chance**: %s".formatted(
            oreConfig.discardChance == 0 ? "Never discarded" : oreConfig.discardChance * 100 + "%")
        ).append(BR);
        output.append("\n");
        // Headers
        output.append("## Generation");
        output.append("## Usages");
        output.append("## Trivia");
        output.append("## History");

        source.sendFeedback(() -> Text.literal("Exported ore stats for %s to wiki format".formatted(oreName)), false);
        MythicMetals.LOGGER.info(output);

        return 2;
    }

    /**
     * Tool exporter for the Mythic Metals Wiki
     */
    private static int exportTools(CommandContext<ServerCommandSource> context) {
        var toolset = ToolSetArgumentType.getToolSet(context, "toolset");

        var source = context.getSource();

        StringBuilder output = new StringBuilder();

        Deque<Integer> damageDeque = new ArrayDeque<>(Arrays.stream(MythicTools.DEFAULT_DAMAGE).boxed().toList());
        Stack<Float> atkSpd = new Stack<>();
        atkSpd.addAll(toolset.getAttackSpeed());

        String setName = StringUtilsAtHome.toProperCase(toolset.getSword().getMaterial().toString());

        output.append("<center class=tooltip>").append("\n");
        output.append("<h3>**").append(setName).append("**</h3>").append("\n");

        for (ToolItem tool : toolset.get()) {
            String id = Registries.ITEM.getId(tool).getPath();
            String name = StringUtilsAtHome.toProperCase(id.replace('_', ' '));

            output.append("\n");
            output.append("<h4>**").append(name).append("**</h4>").append("\n");
            output.append("![Image of %s](../assets/mythicmetals/%s.png)".formatted(name, id)).append(ITEM_SCALE).append(BR);
            output.append("+%s Attack Damage, %s Attack Speed".formatted(
                tool.getMaterial().getAttackDamage() + damageDeque.pop() + 1,
                BigDecimal.valueOf(4.0f + atkSpd.pop()).setScale(1, RoundingMode.HALF_UP).toPlainString()
            )).append(BR);
            output.append("%s Durability".formatted(tool.getDefaultStack().getMaxDamage())).append(BR);
        }
        // tool recipes
        output.append("\n").append("===RECIPE===").append("\n");
        for (ToolItem tool : toolset.get()) {
            String id = Registries.ITEM.getId(tool).getPath();
            String name = StringUtilsAtHome.toProperCase(id.replace('_', ' '));
            output.append("![Image of the recipe for %s](../assets/mythicmetals/recipes/tools/%s.png)".formatted(name, id)).append(RECIPE_SCALE).append(BR);
        }
        output.append("\n").append("===RECIPE END===").append("\n");

        MythicMetals.LOGGER.info(output);
        source.sendFeedback(() -> Text.literal("Exported tools to wiki format"), false);
        return 0;
    }

    /**
     * Summons an armor stand with a specific armor set and trim on top of the world <br>
     * Create the {@link ArmorTrim} using a pattern from {@link net.minecraft.item.trim.ArmorTrimPatterns}
     * and a material from {@link net.minecraft.item.trim.ArmorTrimMaterials}
     *
     * @param world    The world where you want to summon the armor stand, needs to be on the server
     * @param trim     {@link ArmorTrim} you wish to use on the armor.
     * @param armorSet {@link ArmorSet} that you wish to equip on the armor stand.
     * @param x        x-coordinate where the armor stand should spawn
     * @param z        z-coordinate where the armor stand should spawn
     * @return Returns whether the armor set was successfully created and summoned
     * @see ArmorTrim
     * @see MythicArmor
     * @see ArmorSet
     */
    public static boolean summonArmorStandWithTrim(World world, @Nullable ArmorTrim trim, ArmorSet armorSet, float x, float z) {
        if (world.isClient) return false;
        if (armorSet.equals(MythicArmor.TIDESINGER)) return false; // This has custom "trims", ignore it
        AtomicBoolean success = new AtomicBoolean(true);

        var armorStand = new ArmorStandEntity(world, x, world.getTopY() - 50, z);
        armorSet.getArmorItems().forEach(armorItem -> {
            var armorStack = new ItemStack(armorItem);
            if (!armorStack.isIn(ItemTags.TRIMMABLE_ARMOR)) {
                MythicMetals.LOGGER.debug("Armor Item %s is not trimmable".formatted(armorStack.getName()));
            }
            if (trim != null) {
                armorStack.set(DataComponentTypes.TRIM, trim);
            }
            if (success.get()) armorStand.equipStack(armorItem.getSlotType(), armorStack);
        });
        world.spawnEntity(armorStand);
        return success.get();
    }

    /**
     * @param world The world to get the DynamicRegistryManager from. This should only happen on the server
     * @return Returns all armor trims in a sorted ArrayList
     */
    public static ArrayList<ArmorTrim> getAllArmorTrims(World world) {
        if (world.isClient) return new ArrayList<>();

        var list = new ArrayList<ArmorTrim>();
        world.getRegistryManager().get(RegistryKeys.TRIM_MATERIAL).streamEntries().forEach(armorMaterialEntry -> {
            world.getRegistryManager().get(RegistryKeys.TRIM_PATTERN).streamEntries().forEach(armorTrimEntry -> {
                list.add(new ArmorTrim(armorMaterialEntry, armorTrimEntry));
            });
        });
        return list;
    }

    /**
     * @param world The world to get the DynamicRegistryManager from. This should only happen on the server
     * @return Returns all the trim patterns in the registry as a list of strings
     */
    public static ArrayList<String> getAllTrimPatternStrs(World world) {
        if (world.isClient) return new ArrayList<>();

        var list = new ArrayList<String>();
        world.getRegistryManager().get(RegistryKeys.TRIM_PATTERN).streamEntries().forEach(armorTrimEntry -> list.add(armorTrimEntry.value().assetId().getPath()));
        return list;
    }

    /**
     * Suggest which format/location to use when dumping all the ore configs in {@link MythicOreConfigs}
     */
    private static CompletableFuture<Suggestions> dumpType(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder suggestion) {
        suggestion.suggest("console");
        return suggestion.buildFuture();
    }

    /**
     * Suggests armor materials from all the armor sets defined in {@link MythicArmor}
     * Includes one extra suggestion for "all"
     */
    private static CompletableFuture<Suggestions> armorMaterial(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder suggestion) {
        MythicArmor.ARMOR_MAP.forEach((s, armorSet) -> suggestion.suggest(s));
        suggestion.suggest("all");
        return suggestion.buildFuture();
    }

    /**
     * Suggests all the armor trim types available in the dynamic registry
     * Includes two extra suggestions for "all" and "none"
     */
    private static CompletableFuture<Suggestions> trimTypes(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder suggestion) {
        var list = new ArrayList<ArmorTrimPattern>();
        ctx.getSource().getRegistryManager().get(RegistryKeys.TRIM_PATTERN).streamEntries().forEach(armorTrimEntry -> list.add(armorTrimEntry.value()));
        list.forEach(trimPattern -> suggestion.suggest(trimPattern.assetId().getPath()));
        suggestion.suggest("all").suggest("none");
        return suggestion.buildFuture();
    }

    private static int dumpAllOreConfigs(CommandContext<ServerCommandSource> context) {
        if (StringArgumentType.getString(context, "type").equals("console")) {
            ReflectionUtils.iterateAccessibleStaticFields(MythicOreConfigs.class, OreConfig.class, (feature, name, field) -> {
                if (!feature.offset && !feature.trapezoid) {
                    context.getSource().sendFeedback(() -> Text.literal(
                        name.toUpperCase(Locale.ROOT)
                        + " has the range between "
                        + feature.bottom
                        + " to "
                        + feature.top
                        + ", with a discard chance of "
                        + feature.discardChance * 100 + "%"), false);
                }
                if (feature.offset) {
                    context.getSource().sendFeedback(() -> Text.literal(
                        name.toUpperCase(Locale.ROOT)
                        + " has the range between "
                        + feature.bottom
                        + "(offset) to "
                        + feature.top
                        + ", with a discard chance of "
                        + feature.discardChance * 100 + "%"), false);
                }
                if (feature.trapezoid) {
                    context.getSource().sendFeedback(() -> Text.literal(
                        name.toUpperCase(Locale.ROOT)
                        + " has a triangle range between "
                        + feature.bottom
                        + " to "
                        + feature.top
                        + ", where the sweet spot is at Y = "
                        + ((feature.bottom + feature.top) / 2)
                        + " with a discard chance of "
                        + feature.discardChance * 100 + "%"), false);
                }

            });
            return 1;
        }
        return -1;
    }

    private static int armorStandCommand(CommandContext<ServerCommandSource> context, @NotNull String material, @Nullable String rawTrim) {
        var armorTrims = new ArrayList<ArmorTrim>();
        var world = context.getSource().getWorld();
        var pos = context.getSource().getPosition();
        float x = (int) pos.x + 0.5f;
        float z = (int) pos.z + 0.5f;

        final String trimQuery = rawTrim == null ? "none" : rawTrim;

        if (trimQuery.equals("none")) {
            // Summon all permutations of the armor at once. Not recommended due to the sheer density of them
            if (material.equals("all")) {

                int count = 0;
                var armorSetStrings = new TreeSet<>(MythicArmor.ARMOR_MAP.keySet());
                for (var armorSetName : armorSetStrings) {
                    if (summonArmorStandWithTrim(world, null, MythicArmor.ARMOR_MAP.get(armorSetName), x, z)) {
                        x++;
                        count++;
                    }
                }
                int finalCount = count;
                context.getSource().sendFeedback(() -> Text.literal("Summoned and dropping %d armorstands".formatted(finalCount)), true);
                return finalCount;
            } else {
                if (summonArmorStandWithTrim(world, null, MythicArmor.ARMOR_MAP.get(material), x, z)) {
                    context.getSource().sendFeedback(() -> Text.literal("Summoned and dropping one armorstand"), true);
                    return 1;
                } else {
                    context.getSource().sendFeedback(() -> Text.literal("Unable to summon the armor stand. It might be untrimmable"), false);
                }
            }
            return -1;
        }

        if (material.equals("all")) {
            if (MythicArmor.ARMOR_MAP.isEmpty()) {
                context.getSource().sendFeedback(() -> Text.literal("Unable to summon. Somehow the armor map is empty..."), false);
                return -1; // "how did this happen?" "a long time ago, actually never..."
            }

            if (trimQuery.equals("all")) {
                armorTrims.addAll(
                    getAllArmorTrims(world).stream().toList()
                );
            } else if (getAllTrimPatternStrs(world).contains(trimQuery)) {
                armorTrims.addAll(getAllArmorTrims(world).stream().filter(trim -> trim.getPattern().value().assetId().getPath().equals(trimQuery)).toList());
            }

            // lambda moment
            MutableInt mutX = new MutableInt(pos.x);
            MutableInt mutZ = new MutableInt(pos.z);
            MutableInt count = new MutableInt(0);

            var armorSetStrings = new TreeSet<>(MythicArmor.ARMOR_MAP.keySet());
            for (var armorSetName : armorSetStrings) {
                armorTrims.forEach(armorTrim -> {
                    if (summonArmorStandWithTrim(world, armorTrim, MythicArmor.ARMOR_MAP.get(armorSetName), mutX.getValue(), mutZ.getValue())) {
                        mutX.increment();
                        count.increment();
                    }
                });
                mutZ.increment();
                mutX.setValue(pos.x);
            }
            context.getSource().sendFeedback(() -> Text.literal("Summoned and dropping %d armorstands with trims".formatted(count.getValue())), true);

            return count.getValue();
        } else if (MythicArmor.ARMOR_MAP.get(material) != null) {
            if (trimQuery.equals("all")) {
                armorTrims = getAllArmorTrims(world);
            } else {
                var trims = getAllArmorTrims(world).stream().filter(trim -> trim.getPattern().value().assetId().getPath().equals(trimQuery)).toList();
                armorTrims.addAll(trims);
            }

            // Split the armor stands into groups using these offsets
            int splitPoint = armorTrims.size() / world.getRegistryManager().get(RegistryKeys.TRIM_MATERIAL).size();

            // flip-flop
            int xOffset = 0;
            int zOffset = 0;
            int count = 0;

            for (int i = 0; i < armorTrims.size(); i++) {
                if (i % splitPoint == 0) {
                    xOffset += 2;
                    zOffset = 0;
                }
                var armorSet = MythicArmor.ARMOR_MAP.get(material);
                if (summonArmorStandWithTrim(world, armorTrims.get(i), armorSet, (int) pos.x + xOffset, (int) pos.z + zOffset)) {
                    count++;
                    zOffset += 2;
                } else {
                    xOffset -= 2;
                }
            }
            String feedback = "Summoned and dropping %d armorstands".formatted(count);
            context.getSource().sendFeedback(() -> Text.literal(feedback), true);
            return count;
        }
        return -1;
    }
}
