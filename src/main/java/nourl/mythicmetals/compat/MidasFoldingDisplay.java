package nourl.mythicmetals.compat;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import nourl.mythicmetals.component.GoldFoldedComponent;
import nourl.mythicmetals.item.tools.MidasGoldSword;
import nourl.mythicmetals.recipe.MidasFoldingRecipe;
import java.util.Arrays;
import java.util.List;

import static nourl.mythicmetals.component.MythicDataComponents.GOLD_FOLDED;
import static nourl.mythicmetals.item.tools.MidasGoldSword.Type.*;


// TODO - Review/fix after updating tooltip handling in GoldFoldedComponent
public class MidasFoldingDisplay extends DefaultSmithingDisplay {
    Ingredient template;
    Ingredient base;
    Ingredient addition;
    ItemStack outputStack;

    public MidasFoldingDisplay(RecipeEntry<MidasFoldingRecipe> recipe) {
        super(
            recipe.value(),
            recipe.id(),
            List.of(
                EntryIngredients.ofIngredient(recipe.value().template()),
                EntryIngredients.ofIngredient(recipe.value().base()),
                EntryIngredients.ofIngredient(recipe.value().addition())
            )
        );

        this.template = recipe.value().template();
        this.base = recipe.value().base();
        this.addition = recipe.value().addition();
        this.outputStack = recipe.value().result();

    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        if (this.base != null && this.addition != null && outputStack != null) {
            var inputStack = Arrays.stream(this.base.getMatchingStacks()).findFirst().orElseGet(() -> new ItemStack(Items.AIR)).copy();
            // Handle folding recipes, which usually follow the pattern of "input + gold block = output"
            if (this.outputStack.isOf(inputStack.getItem())) {
                if (MidasGoldSword.Type.isOf(inputStack, ROYAL)) {
                    inputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(640));
//                    inputStack.put(MidasGoldSword.IS_ROYAL, true);
//                    inputStack.put(MidasGoldSword.IS_GILDED, true);
                } else if (MidasGoldSword.Type.isOf(inputStack, GILDED)) {
                    inputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(320));
//                    inputStack.put(MidasGoldSword.IS_GILDED, true);
                } else {
                    inputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(16));
                }
                return List.of(
                    EntryIngredients.ofIngredient(this.template),
                    EntryIngredients.of(inputStack),
                    EntryIngredients.ofIngredient(this.addition)
                );
            }
            // Handles transformation from regular midas to gilded midas
            if (MidasGoldSword.Type.isOf(inputStack, REGULAR) && MidasGoldSword.Type.isOf(outputStack, GILDED)) {
                inputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(319));
                return List.of(
                    EntryIngredients.ofIngredient(this.template),
                    EntryIngredients.of(inputStack),
                    EntryIngredients.ofIngredient(this.addition)
                );
            }
            // Transformation of gilded to royal midas
            if (MidasGoldSword.Type.isOf(inputStack, GILDED) && MidasGoldSword.Type.isOf(outputStack, ROYAL)) {
                inputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(640));
                //inputStack.put(MidasGoldSword.IS_GILDED, true);
                return List.of(
                    EntryIngredients.ofIngredient(this.template),
                    EntryIngredients.of(inputStack),
                    EntryIngredients.ofIngredient(this.addition)
                );
            }
        }

        return super.getInputEntries();
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        if (this.base != null && this.addition != null && this.outputStack != null) {
            // Handle folding recipes, which usually follow the pattern of "input + gold block = output"
            var inputStack = Arrays.stream(this.base.getMatchingStacks()).findFirst().orElseGet(() -> new ItemStack(Items.AIR)).copy();
            if (outputStack.getItem().equals(inputStack.getItem())) {

                if (MidasGoldSword.Type.isOf(outputStack, ROYAL)) {
                    outputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(641));
                } else if (MidasGoldSword.Type.isOf(outputStack, GILDED)) {
                    outputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(321));
                } else {
                    outputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(17));
                }

                return List.of(
                    EntryIngredients.of(outputStack)
                );
            }
            // Royal Midas Handler
            if (MidasGoldSword.Type.isOf(outputStack, ROYAL)) {
                var outputWithNbt = outputStack.copy();
                outputWithNbt.set(GOLD_FOLDED, GoldFoldedComponent.of(640));
//                outputWithNbt.put(MidasGoldSword.IS_GILDED, true);
//                outputWithNbt.put(MidasGoldSword.IS_ROYAL, true);
                return List.of(
                    EntryIngredients.of(outputWithNbt)
                );

            }
            // Gilded Midas Handler
            if (MidasGoldSword.Type.isOf(outputStack, GILDED)) {
                var outputWithNbt = outputStack.copy();
                outputWithNbt.set(GOLD_FOLDED, GoldFoldedComponent.of(320));
//                outputWithNbt.put(MidasGoldSword.IS_GILDED, true);
                return List.of(
                    EntryIngredients.of(outputWithNbt)
                );
            }
        }
        return super.getOutputEntries();
    }
}

