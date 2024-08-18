package nourl.mythicmetals.armor;

import net.minecraft.item.Item;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import java.util.function.Consumer;

public class CelestiumArmorSet extends ArmorSet {

    public CelestiumArmorSet(ArmorMaterial material, int duraMod, Consumer<Item.Settings> settingsConsumer) {
        super(material, duraMod, settingsConsumer);
    }

    @Override
    protected ArmorItem makeItem(ArmorMaterial material, ArmorItem.Type slot, Item.Settings settings) {
        return new CelestiumArmor(material, slot, settings);
    }
}
