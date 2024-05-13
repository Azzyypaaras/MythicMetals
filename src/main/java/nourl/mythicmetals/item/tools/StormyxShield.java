package nourl.mythicmetals.item.tools;

import net.minecraft.client.item.TooltipType;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import nourl.mythicmetals.item.MythicItems;
import java.util.List;

public class StormyxShield extends ShieldItem {
    public static final int MAGIC_DAMAGE_REDUCTION = 2;
    public StormyxShield(Settings settings) {
        super(settings);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return super.getMaxUseTime(stack);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(MythicItems.STORMYX.getIngot());
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers(ItemStack stack) {
//        var mapnite = HashMultimap.create(this.getAttributeModifiers(slot));
//
//        mapnite.put(AdditionalEntityAttributes.MAGIC_PROTECTION,
//            new EntityAttributeModifier(UUID.fromString("82b91018-24a1-11ed-861d-0242ac120002"),
//                "Magic protection",
//                MAGIC_DAMAGE_REDUCTION,
//                EntityAttributeModifier.Operation.ADDITION));
        // FIXME - Add Magic Protection in both Main Hand and Off Hand
        return super.getAttributeModifiers(stack);
    }
}
