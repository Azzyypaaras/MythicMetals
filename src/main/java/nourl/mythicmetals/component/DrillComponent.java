package nourl.mythicmetals.component;

import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.TooltipAppender;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import nourl.mythicmetals.misc.UsefulSingletonForColorUtil;
import java.util.function.Consumer;

public record DrillComponent(int fuel, boolean isActive) implements TooltipAppender {
    public static final Endec<DrillComponent> ENDEC = StructEndecBuilder.of(
        Endec.INT.fieldOf("fuel", DrillComponent::fuel),
        Endec.BOOLEAN.fieldOf("is_active", DrillComponent::isActive),
        DrillComponent::new
    );
    public static final DrillComponent DEFAULT = new DrillComponent(0, false);

    /**
     * A fully fueled drill should last 30 minutes
     */
    public static final int MAX_FUEL = 1000;
    /**
     * Each piece of Morkite will fuel the drill by this constant worth of units
     */
    public static final int FUEL_CONSTANT = 10;

    public boolean hasFuel() {
        return this.fuel > 0;
    }

    public static DrillComponent toggleActiveState(DrillComponent component) {
        return new DrillComponent(component.fuel(), !component.isActive());
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {

        // Activation Status
        if (this.isActive) {
            tooltip.accept(Text.translatable("tooltip.mythril_drill.activated").formatted(Formatting.AQUA));
        } else {
            tooltip.accept(Text.translatable("tooltip.mythril_drill.deactivated").setStyle(Style.EMPTY.withColor(Color.ofRgb(0x622622).rgb()).withFormatting(Formatting.ITALIC)));
        }
        if (this.fuel == 0) {
            tooltip.accept(Text.translatable("tooltip.mythril_drill.refuel").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        }
        // Fuel Gauge
        tooltip.accept(Text.translatable("tooltip.mythril_drill.fuel", this.fuel, MAX_FUEL)
            .fillStyle(Style.EMPTY.withColor(UsefulSingletonForColorUtil.getSlightlyDarkerOwoBlueToRedGradient(this.fuel, MAX_FUEL))));

    }
}
