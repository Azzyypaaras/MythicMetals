package nourl.mythicmetals.compat;

import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import nourl.mythicmetals.component.MythicDataComponents;
import nourl.mythicmetals.component.TidesingerPatternComponent;
import nourl.mythicmetals.data.MythicTags;
import nourl.mythicmetals.recipe.TidesingerCoralRecipe;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.List;

public class TidesingerEMIRecipe implements EmiRecipe {

    Ingredient template;
    Ingredient base;
    Ingredient addition;
    List<EmiIngredient> inputs;
    EmiStack outputs;
    Identifier id;

    public TidesingerEMIRecipe(TidesingerCoralRecipe recipe) {
        this.template = recipe.template();
        this.base = recipe.base();
        this.addition = recipe.addition();
        var outputStack = recipe.result();

        if (this.base != null && this.addition != null && outputStack != null) {
            var inputStack = Arrays.stream(this.base.getMatchingStacks()).findFirst().orElse(ItemStack.EMPTY).copy();
            inputs = List.of(
                    EmiIngredient.of(this.template),
                    EmiStack.of(inputStack),
                    EmiIngredient.of(this.addition)
            );

            var output = Arrays.stream(this.addition.getMatchingStacks()).findFirst().orElse(ItemStack.EMPTY).copy();
            if (output.isIn(MythicTags.TIDESINGER_CORAL)) {
                var outputWithComponents = outputStack.copyComponentsToNewStack(outputStack.getItem(), outputStack.getCount());
                outputWithComponents.set(MythicDataComponents.TIDESINGER, TidesingerPatternComponent.fromStack(outputStack));
                outputs = EmiStack.of(output);
            }
        }


    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.SMITHING;
    }

    @Override
    public @Nullable Identifier getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(outputs);
    }

    @Override
    public int getDisplayWidth() {
        return 112;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 1);
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addSlot(inputs.get(1), 18, 0);
        widgets.addSlot(inputs.get(2), 36, 0);
        widgets.addSlot(outputs, 94, 0).recipeContext(this);
    }
}
