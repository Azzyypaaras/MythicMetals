package nourl.mythicmetals.component;

import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import nourl.mythicmetals.MythicMetals;
import nourl.mythicmetals.abilities.DrillUpgrades;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record UpgradeComponent(List<Item> items, int size) implements TooltipAppender {

    public UpgradeComponent(int size) {
        this(Util.make(new ArrayList<>(), items1 -> {
            for (int i = 0; i < size; i++) {
                items1.add(Items.AIR);
            }
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

    // TODO - Consider refactoring to use a builder or some nicer pattern
    public static UpgradeComponent addItem(UpgradeComponent oldComponent, Item item) {
        var newList = new ArrayList<>(oldComponent.items);
        newList.remove(Items.AIR);
        newList.addFirst(item);
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
            MythicMetals.LOGGER.warn("Upgrade Component is larger than the initial item list");
            return;
        }

        for (int i = 0; i < this.size; i++) {
            var item = this.items.get(i);
            tooltip.accept(Text.translatable("tooltip.mythril_drill.upgrade_slot", i + 1, Text.translatable("tooltip.mythril_drill.upgrade." + DrillUpgrades.MAP.get(item))));
        }

    }
}
