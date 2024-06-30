package nourl.mythicmetals.mixin;

import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import nourl.mythicmetals.armor.MythicArmorMaterials;
import nourl.mythicmetals.misc.RegistryHelper;
import nourl.mythicmetals.registry.RegisterEntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin {

    @Inject(method = "method_56689", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;ofVanilla(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void constructor(RegistryEntry<?> registryEntry, ArmorItem.Type type, CallbackInfoReturnable<AttributeModifiersComponent> cir, int i, float f, AttributeModifiersComponent.Builder builder, AttributeModifierSlot slot) {
        var material = registryEntry.value();
        boolean isChestOrLegs = type.equals(ArmorItem.Type.CHESTPLATE) || type.equals(ArmorItem.Type.LEGGINGS);
        if (material == MythicArmorMaterials.TIDESINGER) {
            if (type.equals(ArmorItem.Type.HELMET)) {
                mythicmetals$armorMapBuilder(builder, RegistryHelper.id("tidesinger_helmet_underwater_mining_bonus"), EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED, 0.1f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
                mythicmetals$armorMapBuilder(builder, RegistryHelper.id("tidesinger_helmet_vision_bonus"), AdditionalEntityAttributes.WATER_VISIBILITY, 0.3f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
            }
            if (isChestOrLegs) {
                mythicmetals$armorMapBuilder(builder, RegistryHelper.id("tidesinger_%s_oxygen_bonus".formatted(type.getName())), EntityAttributes.GENERIC_OXYGEN_BONUS, 2.0f, EntityAttributeModifier.Operation.ADD_VALUE, slot);
            }
            if (type.equals(ArmorItem.Type.BOOTS)) {
                mythicmetals$armorMapBuilder(builder, RegistryHelper.id("tidesinger_boots_depth_strider_bonus"), EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY, 1.0f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
            }
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("tidesinger_%s_swim_speed_bonus".formatted(type.getName())), AdditionalEntityAttributes.WATER_SPEED, 0.1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
        }
        if (material.equals(MythicArmorMaterials.AQUARIUM)) {
            if (isChestOrLegs) {
                mythicmetals$armorMapBuilder(builder, RegistryHelper.id("aquarium_%s_oxygen_bonus".formatted(type.getName())), EntityAttributes.GENERIC_OXYGEN_BONUS, 1.0f, EntityAttributeModifier.Operation.ADD_VALUE, slot);
            }
            if (type.equals(ArmorItem.Type.BOOTS)) {
                mythicmetals$armorMapBuilder(builder, RegistryHelper.id("aquarium_boots_depth_strider_bonus"), EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY, 0.5F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
            }
        }
        if (material.equals(MythicArmorMaterials.CELESTIUM)) {
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("celestium_%s_speed_bonus".formatted(type.getName())), EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("celestium_%s_damage_bonus".formatted(type.getName())), EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material.equals(MythicArmorMaterials.MIDAS_GOLD)) {
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("midas_%s_luck_bonus".formatted(type.getName())), EntityAttributes.GENERIC_LUCK, 1.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material.equals(MythicArmorMaterials.STAR_PLATINUM)) {
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("star_platinum_%s_attack_bonus".formatted(type.getName())), EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material.equals(MythicArmorMaterials.CARMOT)) {
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("carmot_%s_carmot_shield_bonus".formatted(type.getName())), RegisterEntityAttributes.CARMOT_SHIELD, 5.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("carmot_%s_health_bonus".formatted(type.getName())), EntityAttributes.GENERIC_MAX_HEALTH, 2.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material.equals(MythicArmorMaterials.STORMYX)) {
            float value = isChestOrLegs ? 2.0F : 1.0F;
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("stormyx_%s_magic_protection".formatted(type.getName())), AdditionalEntityAttributes.MAGIC_PROTECTION, value, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material.equals(MythicArmorMaterials.PALLADIUM)) {
            if (type.getEquipmentSlot().equals(EquipmentSlot.HEAD)) {
                mythicmetals$armorMapBuilder(builder, RegistryHelper.id("palladium_%s_lava_vision_bonus".formatted(type.getName())), AdditionalEntityAttributes.LAVA_VISIBILITY, 8.0f, EntityAttributeModifier.Operation.ADD_VALUE, slot);
            } else {
                mythicmetals$armorMapBuilder(builder, RegistryHelper.id("palladium_%s_lava_speed_bonus".formatted(type.getName())), AdditionalEntityAttributes.LAVA_SPEED, 0.1f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
            }
        }
    }

    @Unique
    private static void mythicmetals$armorMapBuilder(AttributeModifiersComponent.Builder builder, Identifier id, RegistryEntry<EntityAttribute> attributeEntry, float value, EntityAttributeModifier.Operation operation, AttributeModifierSlot slot) {
        builder.add(attributeEntry, new EntityAttributeModifier(id, value, operation), slot);
    }

}
