package nourl.mythicmetals.component;

import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import nourl.mythicmetals.abilities.DrillUpgrades;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record UpgradeComponent(List<Item> items, int size) implements TooltipAppender {

    public UpgradeComponent(List<Item> items, int size) {
        this.size = size;
        this.items = Util.make(new ArrayList<>(size), items1 -> {
            items1.add(Items.AIR);
        });
    }

    public UpgradeComponent(int size) {
        this(Util.make(new ArrayList<>(size), items1 -> {
            items1.add(Items.AIR);
        }), size);
    }

    public static final Endec<UpgradeComponent> ENDEC = StructEndecBuilder.of(
        BuiltInEndecs.ofRegistry(Registries.ITEM).listOf().fieldOf("items", UpgradeComponent::items),
        Endec.INT.fieldOf("size", UpgradeComponent::size),
        UpgradeComponent::new
    );

    public static UpgradeComponent empty(int size) {
        return new UpgradeComponent(size);
    }

    // TODO - Refactor to use a builder or some nicer pattern
    public static UpgradeComponent put(UpgradeComponent oldComponent, Item item) {
        var newList = new ArrayList<>(oldComponent.items);
        newList.remove(Items.AIR);
        newList.add(item);
        return new UpgradeComponent(newList, oldComponent.size);
    }

    /**
     * Check if any upgrade is installed in a specified slot
     */
    public boolean hasUpgrade(Item upgradeItem) {
        return this.items.contains(upgradeItem);
    }

    public boolean hasFreeSlots() {
        return this.items.contains(Items.AIR);
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (this.size > this.items.size()) {
            // TODO - Handle this case
        } else {
            for (int i = 0; i < this.items.size(); i++) {
                var item = this.items.get(i);
                tooltip.accept(Text.translatable("tooltip.mythril_drill.upgrade_slot", i, Text.translatable("tooltip.mythril_drill.upgrade." + DrillUpgrades.MAP.get(item))));
            }
        }

    }
}
