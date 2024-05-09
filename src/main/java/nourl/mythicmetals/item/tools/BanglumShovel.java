package nourl.mythicmetals.item.tools;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import nourl.mythicmetals.misc.BlockBreaker;
import nourl.mythicmetals.misc.MythicParticleSystem;
import nourl.mythicmetals.registry.RegisterCriteria;

public class BanglumShovel extends ShovelItem {

    public BanglumShovel(ToolMaterial material, OwoItemSettings settings) {
        super(material, settings);
    }

    /**
     * Method for the legendary banglum shovel breaking ability.
     * When the tool is used on a block, it breaks a bunch of blocks in a set radius.
     */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        boolean shouldPass = false;
        var world = context.getWorld();
        var player = context.getPlayer();

        if (player != null && !getCooldown(player, context.getStack()) && !world.isClient()) {
            var iterator = BlockBreaker.findBlocks(context, 5);

            for (BlockPos blockPos : iterator) {
                if (canBreak(context.getStack(), world, blockPos)) {
                    WorldOps.breakBlockWithItem(world, blockPos, context.getStack());
                    context.getStack().damage(2, player, EquipmentSlot.MAINHAND);
                    shouldPass = true;
                }
            }

        }

        if (shouldPass) {
            var pos = context.getBlockPos();
            var facing = context.getHorizontalPlayerFacing();
            var pos2 = context.getBlockPos().offset(facing, 5);
            MythicParticleSystem.EXPLOSION_TRAIL.spawn(world, Vec3d.of(pos), Vec3d.of(pos2));
            WorldOps.playSound(world, pos, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.PLAYERS);

            RegisterCriteria.USED_BLAST_MINING.trigger((ServerPlayerEntity) player);
            player.getItemCooldownManager().set(this, 100);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    public boolean canBreak(ItemStack stack, BlockView view, BlockPos pos) {
        return super.isCorrectForDrops(stack, view.getBlockState(pos));
    }

    public static boolean getCooldown(LivingEntity entity, ItemStack stack) {
        if (entity != null && entity.isPlayer()) {
            return ((PlayerEntity) entity).getItemCooldownManager().isCoolingDown(stack.getItem());
        }
        return false;
    }
}
