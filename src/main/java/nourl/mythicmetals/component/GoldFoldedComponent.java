package nourl.mythicmetals.component;

import io.wispforest.owo.serialization.StructEndec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.TooltipAppender;
import net.minecraft.text.Text;
import java.util.function.Consumer;

public class GoldFoldedComponent implements TooltipAppender {

    public static final StructEndec<GoldFoldedComponent> ENDEC = StructEndecBuilder.of(
        StructEndec.INT.fieldOf("gold_folded", GoldFoldedComponent::goldFolded),
        StructEndec.BOOLEAN.fieldOf("show_tooltip", GoldFoldedComponent::showTooltip),
        GoldFoldedComponent::new
    );

    final int goldFolded;
    final boolean showTooltip;

    public GoldFoldedComponent(int goldFolded, boolean showTooltip) {
        this.goldFolded = goldFolded;
        this.showTooltip = showTooltip;
    }

    public static GoldFoldedComponent of(int folds) {
        return new GoldFoldedComponent(folds, true);
    }

    public int goldFolded() {
        return this.goldFolded;
    }

    public boolean showTooltip() {
        return showTooltip;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (!showTooltip) return;
        // TODO - Format the text correctly depending on amount of Midas Gold folded
        tooltip.accept(Text.literal("heyo worldo"));
    }
}
