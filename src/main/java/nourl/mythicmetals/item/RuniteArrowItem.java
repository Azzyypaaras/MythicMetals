package nourl.mythicmetals.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import nourl.mythicmetals.entity.RuniteArrowEntity;

public class RuniteArrowItem extends ArrowItem {

    public RuniteArrowItem(Item.Settings settings) {
        super(settings);
    }

    // TODO - Review
    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        return new RuniteArrowEntity(shooter, world);
    }

}
