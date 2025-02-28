package nourl.mythicmetals;

import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import nourl.mythicmetals.abilities.Abilities;
import nourl.mythicmetals.armor.*;
import nourl.mythicmetals.blocks.BanglumNukeHandler;
import nourl.mythicmetals.blocks.MythicBlocks;
import nourl.mythicmetals.command.MythicCommands;
import nourl.mythicmetals.component.MythicDataComponents;
import nourl.mythicmetals.component.PrometheumComponent;
import nourl.mythicmetals.conditions.MythicResourceConditions;
import nourl.mythicmetals.config.MythicMetalsConfig;
import nourl.mythicmetals.data.MythicOreKeys;
import nourl.mythicmetals.data.MythicTags;
import nourl.mythicmetals.effects.MythicStatusEffects;
import nourl.mythicmetals.entity.CombustionCooldown;
import nourl.mythicmetals.entity.MythicEntities;
import nourl.mythicmetals.item.MythicItems;
import nourl.mythicmetals.item.tools.MythicTools;
import nourl.mythicmetals.misc.*;
import nourl.mythicmetals.registry.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.*;

public class MythicMetals implements ModInitializer, EntityComponentInitializer {
    public static Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "mythicmetals";
    public static final int CONFIG_VERSION = 12;

    public static final AbstractMinecartEntity.Type BANGLUM_TNT = Enum.valueOf(AbstractMinecartEntity.Type.class, "BANGLUM_TNT");

    public static MythicMetalsConfig CONFIG = MythicMetalsConfig.createAndLoad();

    public static final OwoItemGroup TABBED_GROUP = OwoItemGroup.builder(RegistryHelper.id("main"), () -> Icon.of(MythicItems.STORMYX.getIngot()))
        .initializer(group -> {
            group.addTab(Icon.of(MythicItems.ADAMANTITE.getIngot()), "items", TagKey.of(RegistryKeys.ITEM, RegistryHelper.id("item_tab")), false);
            group.addTab(Icon.of(MythicBlocks.ADAMANTITE.getStorageBlock()), "blocks", TagKey.of(RegistryKeys.ITEM, RegistryHelper.id("blocks")), false);
            group.addTab(Icon.of(MythicTools.ADAMANTITE.getPickaxe()), "tools", TagKey.of(RegistryKeys.ITEM, RegistryHelper.id("tool_tab")), false);
            group.addTab(Icon.of(MythicArmor.ADAMANTITE.getChestplate()), "armor", TagKey.of(RegistryKeys.ITEM, RegistryHelper.id("armor_tab")), false);
            group.addButton(ItemGroupButton.github(group, "https://github.com/Noaaan/MythicMetals/issues"));
            group.addButton(ItemGroupButton.curseforge(group, "https://www.curseforge.com/minecraft/mc-mods/mythicmetals"));
            group.addButton(ItemGroupButton.modrinth(group, "https://modrinth.com/mod/mythicmetals"));
            group.addButton(ItemGroupButton.discord(group, "https://discord.gg/69cKvQWScC"));
        })
        .build();

    public static final ComponentKey<CarmotShield> CARMOT_SHIELD = ComponentRegistry.getOrCreate(RegistryHelper.id("carmot_shield"), CarmotShield.class);
    public static final ComponentKey<CombustionCooldown> COMBUSTION_COOLDOWN = ComponentRegistry.getOrCreate(RegistryHelper.id("combustion_cooldown"), CombustionCooldown.class);

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(RegisterSounds.class, MOD_ID, false);
        FieldRegistrationHandler.processSimple(MythicItems.class, false);
        FieldRegistrationHandler.register(MythicItems.Mats.class, MOD_ID, false);
        FieldRegistrationHandler.register(MythicItems.Templates.class, MOD_ID, false);
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            //FieldRegistrationHandler.register(IndevBlocks.class, MOD_ID, false);
            FieldRegistrationHandler.register(MythicItems.ParticleSticks.class, MOD_ID, false);
            RegisterPointOfInterests.init();
        }
        FieldRegistrationHandler.processSimple(MythicItems.Copper.class, false);
        FieldRegistrationHandler.register(MythicArmorMaterials.class, MOD_ID, false);
        FieldRegistrationHandler.processSimple(MythicTools.class, true);
        FieldRegistrationHandler.processSimple(MythicArmor.class, false);
        FieldRegistrationHandler.register(RegisterBlockEntityTypes.class, MOD_ID, false);
        MythicParticleSystem.init();
        MythicBlocks.init();
        MythicDataComponents.init();
        BanglumNukeHandler.init();
        MythicOreKeys.init();
        MythicCommands.init();
        MythicCommands.registerCommands();
        Abilities.init();
        RegisterEntityAttributes.init();
        MythicEntities.init();
        TABBED_GROUP.initialize();
        FuelRegistry.INSTANCE.add(MythicItems.Mats.MORKITE, 1200);
        FuelRegistry.INSTANCE.add(MythicBlocks.MORKITE.getStorageBlock(), 12800);
        MythicResourceConditions.init();
        RegisterLootConditions.init();
        MythicStatusEffects.init();
        RegisterRecipeSerializers.init();
        FieldRegistrationHandler.processSimple(RegisterCriteria.class, false);
        BlockBreaker.initHammerTime();
        MythicLootOps.init();
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC, 5, factories -> {
            factories.add(new TradeOffers.SellItemFactory(MythicItems.Templates.AEGIS_SMITHING_TEMPLATE, 48, 1, 2, 30));
        });
        registerDispenserBehaviour();
        registerPrometheumAttributeEvent();


        if (CONFIG.configVersion() < CONFIG_VERSION) {
            for (int i = 0; i < 5; i++) {
                LOGGER.warn("[Mythic Metals] Your config is outdated. Please update it manually in the file, or delete it so it can be re-generated.");
            }
        }

        if (FabricLoader.getInstance().isModLoaded("harvest_scythes")) {
            LOGGER.info("[Mythic Metals] I see HarvestScythes. I'll take care of DH so you don't have to");
        }
        if (FabricLoader.getInstance().isModLoaded("enhancedcraft")) {
            LOGGER.info("[Mythic Metals] Oh EnhancedCraft? If you ever see Spxctre tell him I said hi!");
        }
        if (FabricLoader.getInstance().isModLoaded("origins")) {
            LOGGER.info("[Mythic Metals] Have fun using Origins!");
        }
        if (FabricLoader.getInstance().isModLoaded("spectrum")) {
            LOGGER.info("[Mythic Metals] Spectrum is loaded! Good luck on finding all of its secrets...");
        }
        if (FabricLoader.getInstance().isModLoaded("jello")) {
            LOGGER.info("[Mythic Metals] Is that Jello? Here comes the colors, weeeeeee!");
        }
        if (FabricLoader.getInstance().isModLoaded("terralith")) {
            LOGGER.info("[Mythic Metals] Terralith detected. Please go over the config and disable Overworld Nether Ores");
            LOGGER.info("[Mythic Metals] Many ores spawn in unexpected ways due to the new overworld. Modpack devs, take note of this");
        }
        LOGGER.info("[Mythic Metals] Mythic Metals is now initialized.");
    }

    /**
     * Registers an event that modifies all armor items in the tag with bonus attributes when bound
     * When the item is in {@link MythicTags#COMMON_ARMOR} it will gain bonus protection.
     * Note that this has to be {@link net.minecraft.item.ArmorItem}, as otherwise it will not get the effect.
     * When the item is in {@link MythicTags#COMMON_TOOLS} it will gain bonus damage
     *
     * @see nourl.mythicmetals.mixin.ItemMixin
     */
    public static void registerPrometheumAttributeEvent() {
        DefaultItemComponentEvents.MODIFY.register(context -> {
            context.modify(item -> item.getDefaultStack().isIn(MythicTags.AUTO_REPAIR), (builder, item) -> {
                builder.add(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT);
            });
        });
    }

    private void registerDispenserBehaviour() {
        DispenserBlock.registerBehavior(() -> MythicTools.STAR_PLATINUM_ARROW, new ProjectileDispenserBehavior(MythicTools.STAR_PLATINUM_ARROW));
        DispenserBlock.registerBehavior(() -> MythicTools.RUNITE_ARROW, new ProjectileDispenserBehavior(MythicTools.RUNITE_ARROW));
        DispenserBlock.registerBehavior(() -> MythicTools.TIPPED_RUNITE_ARROW, new ProjectileDispenserBehavior(MythicTools.TIPPED_RUNITE_ARROW));
    }


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, COMBUSTION_COOLDOWN, CombustionCooldown::new);
        registry.registerForPlayers(CARMOT_SHIELD, CarmotShield::new, RespawnCopyStrategy.INVENTORY);
    }

}
