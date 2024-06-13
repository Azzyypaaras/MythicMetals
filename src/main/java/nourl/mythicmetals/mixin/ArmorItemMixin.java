package nourl.mythicmetals.mixin;

import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import nourl.mythicmetals.armor.MythicArmorMaterials;
import nourl.mythicmetals.registry.RegisterEntityAttributes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.EnumMap;
import java.util.UUID;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin {

    @Shadow
    @Final
    private static EnumMap<ArmorItem.Type, UUID> MODIFIERS;

    @Inject(method = "method_56689", at = @At(value = "INVOKE", target = "Ljava/util/EnumMap;get(Ljava/lang/Object;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void constructor(RegistryEntry<ArmorMaterial> registryEntry, ArmorItem.Type type, CallbackInfoReturnable<AttributeModifiersComponent> cir, int i, float f, AttributeModifiersComponent.Builder builder, AttributeModifierSlot slot) {
        UUID uUID = MODIFIERS.get(type);
        var material = registryEntry.value();
        if (material == MythicArmorMaterials.TIDESINGER) {
            if (type.equals(ArmorItem.Type.HELMET)) {
                mythicmetals$armorMapBuilder(builder, uUID, AdditionalEntityAttributes.WATER_VISIBILITY, "Tidesinger Vision bonus", 0.25f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
            }
            mythicmetals$armorMapBuilder(builder, uUID, AdditionalEntityAttributes.WATER_SPEED, "Tidesinger Swim Speed bonus", 0.06F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
        }
        if (material == MythicArmorMaterials.CELESTIUM) {
            mythicmetals$armorMapBuilder(builder, uUID, EntityAttributes.GENERIC_MOVEMENT_SPEED, "Celestium speed bonus", 0.1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
            mythicmetals$armorMapBuilder(builder, uUID, EntityAttributes.GENERIC_ATTACK_DAMAGE, "Celestium damage bonus", 1.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material == MythicArmorMaterials.MIDAS_GOLD) {
            mythicmetals$armorMapBuilder(builder, uUID, EntityAttributes.GENERIC_LUCK, "Luck bonus", 1.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material == MythicArmorMaterials.STAR_PLATINUM) {
            mythicmetals$armorMapBuilder(builder, uUID, EntityAttributes.GENERIC_ATTACK_DAMAGE, "Star Platinum attack bonus", 1.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material == MythicArmorMaterials.CARMOT) {
            mythicmetals$armorMapBuilder(builder, uUID, RegisterEntityAttributes.CARMOT_SHIELD, "Carmot health bonus", 5.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
            mythicmetals$armorMapBuilder(builder, uUID, EntityAttributes.GENERIC_MAX_HEALTH, "Carmot health bonus", 2.0F, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material == MythicArmorMaterials.STORMYX) {
            float value = type.getEquipmentSlot().equals(EquipmentSlot.LEGS) || type.getEquipmentSlot().equals(EquipmentSlot.CHEST) ? 2.0F : 1.0F;
            mythicmetals$armorMapBuilder(builder, uUID, AdditionalEntityAttributes.MAGIC_PROTECTION, "Stormyx magic protection", value, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        }
        if (material == MythicArmorMaterials.PALLADIUM && type.getEquipmentSlot().equals(EquipmentSlot.HEAD)) {
            mythicmetals$armorMapBuilder(builder, uUID, AdditionalEntityAttributes.LAVA_VISIBILITY, "Palladium lava vision bonus", 8.0f, EntityAttributeModifier.Operation.ADD_VALUE, slot);
        } else if (material == MythicArmorMaterials.PALLADIUM) {
            mythicmetals$armorMapBuilder(builder, uUID, AdditionalEntityAttributes.LAVA_SPEED, "Palladium lava swim speed bonus", 0.1f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slot);
        }
    }

    @Unique
    private static void mythicmetals$armorMapBuilder(AttributeModifiersComponent.Builder builder, UUID uUID, RegistryEntry<EntityAttribute> attributeEntry, String name, float value, EntityAttributeModifier.Operation operation, AttributeModifierSlot slot) {
        builder.add(attributeEntry, new EntityAttributeModifier(uUID, name, value, operation), slot);
    }

}
