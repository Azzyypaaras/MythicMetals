package nourl.mythicmetals.abilities;

import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import nourl.mythicmetals.armor.MythicArmor;
import nourl.mythicmetals.blocks.MythicBlocks;
import nourl.mythicmetals.item.MythicItems;
import nourl.mythicmetals.item.tools.MythicTools;
import nourl.mythicmetals.item.tools.MythrilDrill;

import static nourl.mythicmetals.misc.UsefulSingletonForColorUtil.MetalColors;

/**
 * Truly hardcode abilities onto items. These act as enchantments, but they stack with them.
 * TODO - Move/make these into components, attributes, or move them to more appropriate places
 *
 * @author Noaaan
 */
public class Abilities {
    public static final Ability BETTER_WATER_PROTECTION = new Ability("water_protection", 4);
    public static final Ability BLAST_MINING = new Ability("blast_mining", 0, false);
    public static final Ability BLAST_PADDING = new Ability("blast_padding", 1, false);
    public static final Ability BLAST_PROTECTION = new Ability("blast_protection", 6);
    public static final Ability FEATHER_FALLING = new Ability("feather_falling", 3);
    public static final Ability FIRE_ASPECT = new Ability("fire_aspect", 4); // TODO - Move this tooltip to the RedAegisSword class
    public static final Ability FIRE_PROTECTION = new Ability("fire_protection", 1, false);
    public static final Ability HOT = new Ability("hot", 0, false);
    public static final Ability KNOCKBACK = new Ability("knockback", 3);
    public static final Ability UPGRADE_TOOLTIP = new Ability("upgrade_tooltip", 0, false);
    public static final Ability MATERIAL_TOOLTIP = new Ability("material_tooltip", 0, false);
    public static final Ability PROJECTILE_PROTECTION = new Ability("projectile_protection", 5);
    public static final Ability SMITE = new Ability("smite", 3);
    public static final Ability WATER_PROTECTION = new Ability("water_protection", 3);

    public static void init() {
        UniqueStaffBlocks.init();
        BLAST_PADDING.addArmorSet(MythicArmor.BANGLUM, MetalColors.GOLD_STYLE);
        BLAST_MINING.addItem(MythicTools.LEGENDARY_BANGLUM.getPickaxe(), MetalColors.GOLD_STYLE);
        BLAST_MINING.addItem(MythicTools.LEGENDARY_BANGLUM.getShovel(), MetalColors.GOLD_STYLE);
        BLAST_PROTECTION.addItem(MythicArmor.LEGENDARY_BANGLUM.getChestplate(), MetalColors.GOLD_STYLE);
        PROJECTILE_PROTECTION.addItem(MythicArmor.LEGENDARY_BANGLUM.getLeggings(), MetalColors.GOLD_STYLE);
        FEATHER_FALLING.addItem(MythicArmor.LEGENDARY_BANGLUM.getBoots(), MetalColors.GOLD_STYLE);
        FIRE_PROTECTION.addArmorSet(MythicArmor.PALLADIUM, MetalColors.PALLADIUM_STYLE);
        HOT.addToolSet(MythicTools.PALLADIUM, MetalColors.PALLADIUM_STYLE);
        KNOCKBACK.addItem(MythicTools.LEGENDARY_BANGLUM.getSword(), MetalColors.GOLD_STYLE);
        KNOCKBACK.addItem(MythicTools.LEGENDARY_BANGLUM.getAxe(), MetalColors.GOLD_STYLE);
        KNOCKBACK.addItem(MythicTools.LEGENDARY_BANGLUM.getHoe(), MetalColors.GOLD_STYLE);
        MythrilDrill.drillUpgrades.forEach((item, s) -> {
            if (item != Items.AIR) {
                UPGRADE_TOOLTIP.addItem(item, Style.EMPTY.withColor(MetalColors.MYTHRIL.rgb()));
            }
        });
        // Aegis Sword abilities
        FIRE_ASPECT.addItem(MythicTools.RED_AEGIS_SWORD, Style.EMPTY.withColor(MetalColors.RED_AEGIS.rgb()));
        SMITE.addItem(MythicTools.WHITE_AEGIS_SWORD, Style.EMPTY.withColor(Formatting.YELLOW));
        // Material Tooltips
        MATERIAL_TOOLTIP.addItem(MythicItems.Mats.AQUARIUM_PEARL, MetalColors.AQUA_STYLE);
        MATERIAL_TOOLTIP.addItem(MythicItems.Mats.BANGLUM_CHUNK, MetalColors.GOLD_STYLE);
        MATERIAL_TOOLTIP.addItem(MythicItems.Mats.CARMOT_STONE, MetalColors.CARMOT_STYLE);
        MATERIAL_TOOLTIP.addItem(MythicBlocks.ENCHANTED_MIDAS_GOLD_BLOCK.asItem(), MetalColors.GOLD_STYLE);
        MATERIAL_TOOLTIP.addItem(MythicItems.Mats.STORMYX_SHELL, Style.EMPTY.withColor(Formatting.LIGHT_PURPLE));
        // Mod compat specific abilities
        // TODO - Uncomment once Origins compat is updated and tested
        //if (FabricLoader.getInstance().isModLoaded("origins")) {
        //    WATER_PROTECTION.addItem(MythicArmor.AQUARIUM.getChestplate(), MetalColors.AQUA_STYLE);
        //    BETTER_WATER_PROTECTION.addItem(MythicArmor.TIDESINGER.getChestplate(), MetalColors.TIDESINGER_BLUE);
        //}
    }

}
