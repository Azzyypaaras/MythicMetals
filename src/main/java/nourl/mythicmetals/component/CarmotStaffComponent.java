package nourl.mythicmetals.component;

import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.StructEndec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.TooltipAppender;
import net.minecraft.text.Text;
import java.util.function.Consumer;

public record CarmotStaffComponent(Block block, boolean showTooltip) implements TooltipAppender {

    public static final CarmotStaffComponent DEFAULT = new CarmotStaffComponent(Blocks.AIR, true);

    public static final StructEndec<CarmotStaffComponent> ENDEC = StructEndecBuilder.of(
        Endec.ofCodec(Block.CODEC.codec()).fieldOf("block", CarmotStaffComponent::getBlock),
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
        tooltip.accept(Text.literal("This is a custom tooltip"));
        tooltip.accept(Text.literal("Hello world!"));
    }
}
