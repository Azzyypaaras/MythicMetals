package nourl.mythicmetals.compat;

import dev.emi.emi.api.*;
import dev.emi.emi.api.stack.EmiStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import nourl.mythicmetals.item.tools.MythicTools;
import nourl.mythicmetals.recipe.MidasFoldingRecipe;
import nourl.mythicmetals.recipe.TidesingerCoralRecipe;

@EmiEntrypoint
public class MythicMetalsEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        for (RecipeEntry<SmithingRecipe> recipe : registry.getRecipeManager().listAllOfType(RecipeType.SMITHING)) {
            if (recipe.value() instanceof MidasFoldingRecipe foldingRecipe) {
                registry.addRecipe(new MidasFoldingEMIRecipe(foldingRecipe));
            }
            if (recipe.value() instanceof TidesingerCoralRecipe tidesingerCoralRecipe) {
                registry.addRecipe(new TidesingerEMIRecipe(tidesingerCoralRecipe));
            }
        }
        registry.removeEmiStacks(EmiStack.of(MythicTools.Frogery.FROGE));
        registry.removeEmiStacks(EmiStack.of(MythicTools.Frogery.DOGE));
    }
}
