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
    private static void constructor(RegistryEntry registryEntry, ArmorItem.Type type, CallbackInfoReturnable<AttributeModifiersComponent> cir, int i, float f, AttributeModifiersComponent.Builder builder, AttributeModifierSlot slot) {
        var material = registryEntry.value();
        if (material == MythicArmorMaterials.TIDESINGER) {
            if (type.equals(ArmorItem.Type.HELMET)) {
                mythicmetals$armorMapBuilder(builder, RegistryHelper.id("tidesinger_vision_bonus"), AdditionalEntityAttributes.WATER_VISIBILITY, 0.25f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
            }
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("tidesinger_swim_speed_bonus"), AdditionalEntityAttributes.WATER_SPEED, 0.06F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
        }
        if (material == MythicArmorMaterials.CELESTIUM) {
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("celestium_speed_bonus"), EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("celestium_damage_bonus"), EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material == MythicArmorMaterials.MIDAS_GOLD) {
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("midas_luck_bonus"), EntityAttributes.GENERIC_LUCK, 1.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material == MythicArmorMaterials.STAR_PLATINUM) {
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("star_platinum_attack_bonus"), EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material == MythicArmorMaterials.CARMOT) {
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("carmot_shield"), RegisterEntityAttributes.CARMOT_SHIELD, 5.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("carmot_health_bonus"), EntityAttributes.GENERIC_MAX_HEALTH, 2.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material == MythicArmorMaterials.STORMYX) {
            float value = type.getEquipmentSlot().equals(EquipmentSlot.LEGS) || type.getEquipmentSlot().equals(EquipmentSlot.CHEST) ? 2.0F : 1.0F;
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("stormyx_magic_protection"), AdditionalEntityAttributes.MAGIC_PROTECTION, value, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material == MythicArmorMaterials.PALLADIUM && type.getEquipmentSlot().equals(EquipmentSlot.HEAD)) {
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("palladium_lava_vision_bonus"), AdditionalEntityAttributes.LAVA_VISIBILITY,8.0f, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        } else if (material == MythicArmorMaterials.PALLADIUM) {
            mythicmetals$armorMapBuilder(builder, RegistryHelper.id("palladium_lava_speed_bonus"), AdditionalEntityAttributes.LAVA_SPEED,0.1f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
        }
    }

    @Unique
    private static void mythicmetals$armorMapBuilder(AttributeModifiersComponent.Builder builder, Identifier id, RegistryEntry<EntityAttribute> attributeEntry, float value, EntityAttributeModifier.Operation operation, AttributeModifierSlot slot) {
        builder.add(attributeEntry, new EntityAttributeModifier(id, value, operation), slot);
    }

}
