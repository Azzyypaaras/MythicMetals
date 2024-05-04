package nourl.mythicmetals.item.tools;

import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class HammerBase extends PickaxeItem {

    private final int depth;

    public HammerBase(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings, int depth) {
        super(material, settings);
        this.depth = depth;
    }

    public boolean canBreak(ItemStack stack, BlockView view, BlockPos pos) {
        return super.isCorrectForDrops(stack, view.getBlockState(pos));
    }

    public int getDepth() {
        return depth;
    }
}
