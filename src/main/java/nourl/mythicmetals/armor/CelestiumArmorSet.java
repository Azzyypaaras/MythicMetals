package nourl.mythicmetals.armor;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import java.util.function.Consumer;

public class CelestiumArmorSet extends ArmorSet {

    public CelestiumArmorSet(ArmorMaterial material, int duraMod, Consumer<OwoItemSettings> settingsConsumer) {
        super(material, duraMod, settingsConsumer);
    }

    @Override
    protected ArmorItem makeItem(ArmorMaterial material, ArmorItem.Type slot, OwoItemSettings settings) {
        return new CelestiumArmor(material, slot, settings);
    }
}
