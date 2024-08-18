package nourl.mythicmetals.armor;

import net.minecraft.item.Item;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;

public class RuniteArmorSet extends ArmorSet {

    public RuniteArmorSet(ArmorMaterial material, int duraMod) {
        super(material, duraMod);
    }

    @Override
    protected ArmorItem makeItem(ArmorMaterial material, ArmorItem.Type slot, Item.Settings settings) {
        if (slot != ArmorItem.Type.HELMET) return super.makeItem(material, slot, settings);
        return new RuniteArmor(slot, settings);
    }
}
