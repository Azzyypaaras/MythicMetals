package nourl.mythicmetals.armor;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import nourl.mythicmetals.component.MythicDataComponents;
import nourl.mythicmetals.component.PrometheumComponent;

public class PrometheumArmorSet extends ArmorSet {
    public PrometheumArmorSet(ArmorMaterial material, int duraMod) {
        super(material, duraMod);
    }

    @Override
    protected ArmorItem makeItem(ArmorMaterial material, ArmorItem.Type slot, OwoItemSettings settings) {
        return new PrometheumArmorItem(material, slot, settings.component(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT));
    }
}
