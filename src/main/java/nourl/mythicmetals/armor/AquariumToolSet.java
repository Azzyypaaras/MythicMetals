package nourl.mythicmetals.armor;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import nourl.mythicmetals.item.tools.ToolSet;
import nourl.mythicmetals.misc.RegistryHelper;

public class AquariumToolSet extends ToolSet {

    public AquariumToolSet(ToolMaterial material, int[] damage, float[] speed) {
        super(material, damage, speed);
    }

    @Override
    protected SwordItem makeSword(ToolMaterial material, int damage, float speed, Item.Settings settings) {
        return super.makeSword(material, damage, speed, (Item.Settings) settings.attributeModifiers(createAquaAffinityToolModifiers(material, damage, speed)));
    }

    @Override
    protected AxeItem makeAxe(ToolMaterial material, int damage, float speed, Item.Settings settings) {
        return super.makeAxe(material, damage, speed, (Item.Settings) settings.attributeModifiers(createAquaAffinityToolModifiers(material, damage, speed)));
    }

    @Override
    protected PickaxeItem makePickaxe(ToolMaterial material, int damage, float speed, Item.Settings settings) {
        return super.makePickaxe(material, damage, speed, (Item.Settings) settings.attributeModifiers(createAquaAffinityToolModifiers(material, damage, speed)));
    }

    @Override
    protected ShovelItem makeShovel(ToolMaterial material, int damage, float speed, Item.Settings settings) {
        return super.makeShovel(material, damage, speed, (Item.Settings) settings.attributeModifiers(createAquaAffinityToolModifiers(material, damage, speed)));
    }

    @Override
    protected HoeItem makeHoe(ToolMaterial material, int damage, float speed, Item.Settings settings) {
        return super.makeHoe(material, damage, speed, (Item.Settings) settings.attributeModifiers(createAquaAffinityToolModifiers(material, damage, speed)));
    }

    public static AttributeModifiersComponent createAquaAffinityToolModifiers(ToolMaterial material, double damage, float speed) {
        if (speed < 0.0f) {
            speed = 0;
        }
        return AttributeModifiersComponent.builder()
            .add(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, material.getAttackDamage() + damage, EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.MAINHAND
            )
            .add(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, -4.0 + speed, EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.MAINHAND
            )
            .add(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED,
                new EntityAttributeModifier(RegistryHelper.id("aquarium_tool_bonus"), 4, EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.MAINHAND
            )
            .build();
    }
}
