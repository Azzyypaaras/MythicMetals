package nourl.mythicmetals;

import io.wispforest.owo.ui.util.Delta;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import nourl.mythicmetals.armor.*;
import nourl.mythicmetals.blocks.MythicBlocks;
import nourl.mythicmetals.client.CarmotShieldHudHandler;
import nourl.mythicmetals.client.models.MythicModelHandler;
import nourl.mythicmetals.client.rendering.*;
import nourl.mythicmetals.compat.IsometricArmorStandExporter;
import nourl.mythicmetals.component.*;
import nourl.mythicmetals.data.MythicTags;
import nourl.mythicmetals.entity.MythicEntities;
import nourl.mythicmetals.item.tools.*;
import nourl.mythicmetals.misc.*;
import nourl.mythicmetals.mixin.WorldRendererInvoker;
import nourl.mythicmetals.registry.RegisterBlockEntityTypes;
import java.util.ArrayList;
import java.util.Calendar;

public class MythicMetalsClient implements ClientModInitializer {
    private long lastTime;
    private float time;
    public static ModelTransformationMode mode;

    @Override
    public void onInitializeClient() {
        MythicModelHandler.init((loc, def) -> EntityModelLayerRegistry.registerModelLayer(loc, () -> def));

        renderHammerOutline();
        registerArmorRenderer();
        registerModelPredicates();
        registerSwirlRenderer();

        LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(player -> !CelestiumElytra.isWearing(player));

        EntityRendererRegistry.register(MythicEntities.BANGLUM_TNT_MINECART_ENTITY_TYPE, BanglumTntMinecartEntityRenderer::new);
        EntityRendererRegistry.register(MythicEntities.BANGLUM_TNT_ENTITY_TYPE, BanglumTntEntityRenderer::new);
        EntityRendererRegistry.register(MythicEntities.BANGLUM_NUKE_ENTITY_TYPE, BanglumNukeEntityRenderer::new);
        EntityRendererRegistry.register(MythicEntities.STAR_PLATINUM_ARROW_ENTITY_TYPE, StarPlatinumArrowEntityRenderer::new);
        EntityRendererRegistry.register(MythicEntities.RUNITE_ARROW_ENTITY_TYPE, RuniteArrowEntityRenderer::new);

        BlockEntityRendererFactories.register(RegisterBlockEntityTypes.ENCHANTED_MIDAS_GOLD_BLOCK, EnchantedMidasBlockEntityRenderer::new);

        BuiltinItemRendererRegistry.INSTANCE.register(MythicTools.CARMOT_STAFF, new CarmotStaffBlockRenderer());
        ModelLoadingPlugin.register(new CarmotStaffBlockRenderer());
        ColorProviderRegistry.ITEM.register(UsefulSingletonForColorUtil::potionColor, MythicTools.TIPPED_RUNITE_ARROW);

        CarmotShieldHudHandler.init();
        ClientTickEvents.END_CLIENT_TICK.register(client -> CarmotShieldHudHandler.tick());

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            //BlockRenderLayerMap.INSTANCE.putBlock(IndevBlocks.AQUARIUM_GLASS, RenderLayer.getTranslucent());
        }

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), MythicBlocks.KYBER.getStorageBlock());

        if (FabricLoader.getInstance().isModLoaded("isometric-renders")) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
                IsometricArmorStandExporter.register(dispatcher);
            });
        }

        registerTooltipCallbacks();
    }

    @SuppressWarnings("unchecked")
    private void registerSwirlRenderer() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityType != EntityType.PLAYER) return;
            registrationHelper.register(
                new PlayerEnergySwirlFeatureRenderer(
                    (FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>) entityRenderer,
                    context.getModelLoader()));
        });
    }

    /**
     * Renders the outline of a {@link HammerBase hammer item.}
     */
    private void renderHammerOutline() {
        WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
            if (!blockOutlineContext.entity().isPlayer()) return true;
            var player = (PlayerEntity) blockOutlineContext.entity();

            // Only render the outline if you are hovering over something the hammer can break
            var stack = player.getMainHandStack();
            if (stack.getItem() instanceof HammerBase hammer
                && !blockOutlineContext.blockState().isAir()
                && hammer.isCorrectForDrops(stack, blockOutlineContext.blockState())) {

                var reach = BlockBreaker.getReachDistance(player);
                BlockHitResult blockHitResult = (BlockHitResult) player.raycast(reach, 1, false);

                var facing = blockHitResult.getSide().getOpposite();
                var blocks = BlockBreaker.findBlocks(facing, blockOutlineContext.blockPos(), hammer.getDepth());
                var originalPos = blockOutlineContext.blockPos();

                // Create VoxelShapes out of the block positions and put them in a list
                var voxels = new ArrayList<VoxelShape>();

                for (BlockPos blockPos : blocks) {
                    var blockState = player.getWorld().getBlockState(blockPos);
                    if (!blockState.isAir() && hammer.isCorrectForDrops(stack, blockState)) {
                        voxels.add(blockState.getOutlineShape(
                                worldRenderContext.world(),
                                blockPos,
                                ShapeContext.of(blockOutlineContext.entity())
                            ).offset(blockPos.getX() - originalPos.getX(),
                                blockPos.getY() - originalPos.getY(),
                                blockPos.getZ() - originalPos.getZ())
                        );
                    }
                }

                // Combine and render the full shape
                var outlineOptional = voxels.stream().reduce(VoxelShapes::union);
                if (outlineOptional.isEmpty()) return true;

                var outlineShape = outlineOptional.get();

                WorldRendererInvoker.mythicmetals$drawShapeOutline(
                    worldRenderContext.matrixStack(),
                    worldRenderContext.consumers().getBuffer(RenderLayer.getLines()),
                    outlineShape,
                    originalPos.getX() - blockOutlineContext.cameraX(),
                    originalPos.getY() - blockOutlineContext.cameraY(),
                    originalPos.getZ() - blockOutlineContext.cameraZ(),
                    0, 0, 0, 0.4F //RGBA
                );
                // Cancel the event to prevent the middle outline from rendering
                return false;
            }

            // Keep moving along if we reach this point
            return true;
        });
    }

    private void registerArmorRenderer() {
        Item[] armors = Registries.ITEM.stream()
            .filter(i -> i instanceof HallowedArmor
                         && Registries.ITEM.getKey(i).get().getValue().getNamespace().equals(MythicMetals.MOD_ID))
            .toArray(Item[]::new);

        ArmorRenderer renderer = (matrices, vertexConsumer, stack, entity, slot, light, original) -> {

            HallowedArmor armor = (HallowedArmor) stack.getItem();
            var model = armor.getArmorModel();
            var texture = armor.getArmorTexture(stack, slot);
            original.copyBipedStateTo(model);
            ArmorRenderer.renderPart(matrices, vertexConsumer, light, stack, model, texture);

            // Armor trim time
            if (!stack.isOf(MythicArmor.HALLOWED.getHelmet())) {
                var trimComponent = stack.get(DataComponentTypes.TRIM);
                if (trimComponent != null) {
                    var atlas = MinecraftClient.getInstance().getSpriteAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
                    Sprite sprite = atlas.apply(slot == EquipmentSlot.LEGS ? trimComponent.getLeggingsModelId(armor.getMaterial()) : trimComponent.getGenericModelId(armor.getMaterial()));
                    VertexConsumer trimVertexConsumer = sprite.getTextureSpecificVertexConsumer(
                        ItemRenderer.getDirectItemGlintConsumer(vertexConsumer, TexturedRenderLayers.getArmorTrims(trimComponent.getPattern().value().decal()), true, stack.hasGlint())
                    );
                    model.render(matrices, trimVertexConsumer, light, OverlayTexture.DEFAULT_UV);
                }
            }
        };
        ArmorRenderer.register(renderer, armors);
    }

    private void registerModelPredicates() {
        ModelPredicateProviderRegistry.register(
            MythicTools.LEGENDARY_BANGLUM.getPickaxe(), RegistryHelper.id("is_primed"),
            (stack, world, entity, seed) -> BanglumPick.getCooldown(entity, stack) ? 0 : 1
        );

        ModelPredicateProviderRegistry.register(
            MythicTools.LEGENDARY_BANGLUM.getShovel(), RegistryHelper.id("is_primed"),
            (stack, world, entity, seed) -> BanglumShovel.getCooldown(entity, stack) ? 0 : 1
        );

        ModelPredicateProviderRegistry.register(
            MythicTools.MYTHRIL_DRILL, RegistryHelper.id("is_active"),
            (stack, world, entity, seed) -> stack.getOrDefault(MythicDataComponents.DRILL, DrillComponent.DEFAULT).isActive() ? 0 : 1
        );

        registerMidasPredicates(MythicTools.MIDAS_GOLD_SWORD);
        registerMidasPredicates(MythicTools.GILDED_MIDAS_GOLD_SWORD);
        registerMidasPredicates(MythicTools.ROYAL_MIDAS_GOLD_SWORD);

        ModelPredicateProviderRegistry.register(RegistryHelper.id("in_world"), (itemStack, world, livingEntity, i) -> {
            if (mode == null) {
                return 1.0f;
            }

            return mode.equals(ModelTransformationMode.GUI) ? 0.0F : 1.0f;
        });

        ModelPredicateProviderRegistry.register(MythicTools.STORMYX_SHIELD, RegistryHelper.id("blocking"), new ShieldUsePredicate());

        ModelPredicateProviderRegistry.register(RegistryHelper.id("funny_day"), (stack, world, entity, seed) ->
            (Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1 && !MythicMetals.CONFIG.disableFunny()) ? 1 : 0);

        ModelPredicateProviderRegistry.register(MythicTools.PLATINUM_WATCH, RegistryHelper.id("time"), (stack, world, entity, seed) -> {
            if (entity == null || entity.getWorld() == null) {
                return 0.0F;
            }
            return this.getTime(entity.getWorld());
        });

    }

    public void registerTooltipCallbacks() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            int index = 1;

            if (stack.isIn(MythicTags.CARMOT_TOOLS)) {
                if (stack.isIn(ConventionalItemTags.MELEE_WEAPON_TOOLS)) {
                    lines.add(index, Text.translatable("abilities.mythicmetals.bonus_looting").withColor(UsefulSingletonForColorUtil.MetalColors.CARMOT.rgb()));
                } else {
                    lines.add(index, Text.translatable("abilities.mythicmetals.bonus_fortune").withColor(UsefulSingletonForColorUtil.MetalColors.CARMOT.rgb()));
                }
            }

            if (lines.size() > 2) {
                index += stack.getEnchantments().getSize();
            }

            if (stack.contains(MythicDataComponents.PROMETHEUM)) {
                var component = stack.getOrDefault(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT);
                if (type.isAdvanced()) {
                    lines.add(index, Text.translatable("tooltip.prometheum.repaired", component.durabilityRepaired())
                        .withColor(UsefulSingletonForColorUtil.MetalColors.PROMETHEUM.rgb())
                    );
                }

                lines.add(index, Text.translatable("tooltip.prometheum.regrowth").withColor(UsefulSingletonForColorUtil.MetalColors.PROMETHEUM.rgb()));
                if (component.isOvergrown()) {
                    lines.add(index, Text.translatable("tooltip.prometheum.overgrown").withColor(UsefulSingletonForColorUtil.MetalColors.PROMETHEUM.rgb()));
                }
                if (stack.contains(EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE)) {
                    lines.add(index, Text.translatable("tooltip.prometheum.engrained").withColor(UsefulSingletonForColorUtil.MetalColors.PROMETHEUM.rgb()));
                }
            }
        });


    }

    private float getTime(World world) {
        if (world.getTimeOfDay() != this.lastTime) {
            this.lastTime = world.getTimeOfDay();
            this.time += Delta.compute(
                this.time,
                (world.getTimeOfDay()) / 24000.0f,
                MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration() / 2.0f
            );
        }

        return this.time;
    }

    public void registerMidasPredicates(Item item) {
        ModelPredicateProviderRegistry.register(item, RegistryHelper.id("midas_gold_count"),
            (stack, world, entity, seed) -> {
                int goldCount = stack.getOrDefault(MythicDataComponents.GOLD_FOLDED, GoldFoldedComponent.of(0)).goldFolded();
                return MidasGoldSword.countGold(goldCount);
            });
    }

}
