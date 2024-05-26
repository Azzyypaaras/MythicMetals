package nourl.mythicmetals.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import nourl.mythicmetals.entity.RuniteArrowEntity;

public class TippedRuniteArrowItem extends TippedArrowItem {

    public TippedRuniteArrowItem(Item.Settings settings) {
        super(settings);
    }

    // TODO - Review
    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        return new RuniteArrowEntity(shooter, world);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey(stack);
    }
}
