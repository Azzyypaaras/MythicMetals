package nourl.mythicmetals.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import nourl.mythicmetals.entity.StarPlatinumArrowEntity;

public class StarPlatinumArrowItem extends ArrowItem {

    public StarPlatinumArrowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        return new StarPlatinumArrowEntity(shooter, world);
    }
}
