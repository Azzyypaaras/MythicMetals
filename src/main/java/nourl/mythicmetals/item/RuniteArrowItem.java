package nourl.mythicmetals.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.*;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import nourl.mythicmetals.entity.RuniteArrowEntity;

public class RuniteArrowItem extends ArrowItem {

    public RuniteArrowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        return new RuniteArrowEntity(world, shooter, stack);
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        var entity = new RuniteArrowEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copyWithCount(1));
        entity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        return entity;
    }

    @Override
    public ProjectileItem.Settings getProjectileSettings() {
        return new ProjectileItem.Settings.Builder()
            .power(1.4f)
            .uncertainty(5.0f)
            .build();
    }
}
