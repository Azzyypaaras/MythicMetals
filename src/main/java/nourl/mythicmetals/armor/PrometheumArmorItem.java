package nourl.mythicmetals.armor;

import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nourl.mythicmetals.misc.PrometheumHandler;
import nourl.mythicmetals.misc.RegistryHelper;

public class PrometheumArmorItem extends ArmorItem {
    public PrometheumArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(RegistryHelper.getEntry(material), type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient()) PrometheumHandler.tickAutoRepair(stack, world.getRandom());
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
