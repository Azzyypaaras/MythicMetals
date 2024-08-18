package nourl.mythicmetals.registry;

import io.wispforest.owo.registration.reflect.BlockEntityRegistryContainer;
import net.minecraft.block.entity.BlockEntityType;
import nourl.mythicmetals.blocks.EnchantedMidasGoldBlockEntity;
import nourl.mythicmetals.blocks.MythicBlocks;
import java.lang.reflect.Field;

public class RegisterBlockEntityTypes implements BlockEntityRegistryContainer {

    /*
    public static final BlockEntityType<AquariumStewardBlockEntity> AQUARIUM_STEWARD =
            FabricBlockEntityTypeBuilder.create(AquariumStewardBlockEntity::new, IndevBlocks.AQUARIUM_STEWARD).build();
    public static final BlockEntityType<AquariumResonatorBlockEntity> AQUARIUM_RESONATOR =
            FabricBlockEntityTypeBuilder.create(AquariumResonatorBlockEntity::new, IndevBlocks.AQUARIUM_RESONATOR).build();
     */

    public static final BlockEntityType<EnchantedMidasGoldBlockEntity> ENCHANTED_MIDAS_GOLD_BLOCK =
        BlockEntityType.Builder.create(EnchantedMidasGoldBlockEntity::new, MythicBlocks.ENCHANTED_MIDAS_GOLD_BLOCK).build();

    @Override
    public boolean shouldProcessField(BlockEntityType<?> value, String identifier, Field field) {
        /*
        if (value.equals(AQUARIUM_STEWARD) || value.equals(AQUARIUM_RESONATOR)) {
            return FabricLoader.getInstance().isDevelopmentEnvironment();
        }
         */
        return BlockEntityRegistryContainer.super.shouldProcessField(value, identifier, field);
    }
}
