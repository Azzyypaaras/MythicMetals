package nourl.mythicmetals.component;

import io.wispforest.owo.serialization.StructEndec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import java.util.function.Consumer;

public record GoldFoldedComponent(int goldFolded, boolean isRoyal, boolean showTooltip) implements TooltipAppender {

    public static final StructEndec<GoldFoldedComponent> ENDEC = StructEndecBuilder.of(
        StructEndec.INT.fieldOf("gold_folded", GoldFoldedComponent::goldFolded),
        StructEndec.BOOLEAN.fieldOf("is_royal", GoldFoldedComponent::isRoyal),
        StructEndec.BOOLEAN.fieldOf("show_tooltip", GoldFoldedComponent::showTooltip),
        GoldFoldedComponent::new
    );

    public static GoldFoldedComponent of(int folds, boolean isRoyal) {
        return new GoldFoldedComponent(folds, isRoyal, true);
    }

    public static GoldFoldedComponent of(int folds) {
        return new GoldFoldedComponent(folds, false, true);
    }

    public boolean isGilded() {
        return this.goldFolded >= 320;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (!showTooltip) return;
        // TODO - Format the text correctly depending on amount of Midas Gold folded
        tooltip.accept(Text.literal("heyo worldo"));
    }
}
