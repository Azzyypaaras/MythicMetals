package nourl.mythicmetals.component;

import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.StructEndec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.TooltipAppender;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import nourl.mythicmetals.abilities.UniqueStaffBlocks;
import java.util.function.Consumer;

public record CarmotStaffComponent(Block block, boolean showTooltip) implements TooltipAppender {

    public static final CarmotStaffComponent DEFAULT = new CarmotStaffComponent(Blocks.AIR, true);

    public static final StructEndec<CarmotStaffComponent> ENDEC = StructEndecBuilder.of(
        Endec.ofCodec(Registries.BLOCK.getCodec()).fieldOf("block", CarmotStaffComponent::getBlock),
        Endec.BOOLEAN.fieldOf("show", carmotStaffComponent -> carmotStaffComponent.showTooltip),
        CarmotStaffComponent::new
    );

    public CarmotStaffComponent(Block block) {
        this(block, true);
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (this.block.equals(Blocks.AIR)) {
            tooltip.accept(Text.translatable("tooltip.carmot_staff.empty_staff").setStyle(Style.EMPTY.withColor(Color.ofDye(DyeColor.LIGHT_GRAY).rgb())));
        } else if (UniqueStaffBlocks.hasUniqueBlockInStaff(block)) {
            tooltip.accept(UniqueStaffBlocks.getBlockTranslationKey(block));
        }

    }
}
