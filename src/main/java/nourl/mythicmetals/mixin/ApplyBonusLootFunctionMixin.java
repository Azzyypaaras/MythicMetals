package nourl.mythicmetals.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import nourl.mythicmetals.component.MythicDataComponents;
import nourl.mythicmetals.component.UpgradeComponent;
import nourl.mythicmetals.data.MythicTags;
import nourl.mythicmetals.item.MythicItems;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ApplyBonusLootFunction.class)
public class ApplyBonusLootFunctionMixin {

    @Shadow
    @Final
    private RegistryEntry<Enchantment> enchantment;

    @ModifyVariable(method = "process",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/loot/context/LootContext;getRandom()Lnet/minecraft/util/math/random/Random;",
            shift = At.Shift.BEFORE),
        ordinal = 0
    )
    private int mythicmetals$increaseFortune(int level, ItemStack drop, LootContext lootCtx) {
        // Only increase drops from Fortune
        if (!this.enchantment.matches((enchantmentRegistryKey) -> enchantmentRegistryKey.equals(Enchantments.FORTUNE))) {
            return level;
        }

        // Return early if there is no item
        var toolCtxStack = lootCtx.get(LootContextParameters.TOOL);
        if (toolCtxStack == null) {
            return level;
        }

        if (toolCtxStack.isIn(MythicTags.CARMOT_TOOLS)) {
            return level + 1;
        }

        if (toolCtxStack.getOrDefault(MythicDataComponents.UPGRADES, UpgradeComponent.empty(2)).hasUpgrade(MythicItems.Mats.CARMOT_STONE)) {
            return level + 1;
        }
        return level;
    }
}
