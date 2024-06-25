package nourl.mythicmetals.recipe;

import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.StructEndec;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import io.wispforest.owo.serialization.util.EndecRecipeSerializer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import nourl.mythicmetals.component.GoldFoldedComponent;
import nourl.mythicmetals.item.MythicItems;
import nourl.mythicmetals.item.tools.MythicTools;
import nourl.mythicmetals.registry.RegisterRecipeSerializers;

import static nourl.mythicmetals.component.MythicDataComponents.GOLD_FOLDED;

public record MidasFoldingRecipe(Ingredient template, Ingredient base, Ingredient addition,
                                 ItemStack result) implements SmithingRecipe {

    @Override
    public boolean matches(Inventory inventory, World world) {
        if (this.template.test(inventory.getStack(0)) && this.base.test(inventory.getStack(1)) && this.addition.test(inventory.getStack(2))) {
            var stack = inventory.getStack(1);

            if (!stack.contains(GOLD_FOLDED)) return false;
            int goldCount = stack.contains(GOLD_FOLDED) ? stack.get(GOLD_FOLDED).goldFolded() : 0;

            if (inventory.getStack(0).getItem().equals(MythicItems.Templates.ROYAL_MIDAS_SMITHING_TEMPLATE)) {
                return goldCount >= 640;
            }

            if (stack.getItem().equals(MythicTools.ROYAL_MIDAS_GOLD_SWORD)) {
                return goldCount >= 640 && goldCount < 10000;
            }

            return goldCount < 640;
        }

        return false;
    }

    @Override
    public ItemStack craft(Inventory inventory, RegistryWrapper.WrapperLookup lookup) {
        var swordInputStack = inventory.getStack(1).copy();

        int goldCount = swordInputStack.get(GOLD_FOLDED).goldFolded();
        swordInputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(goldCount + 1));

        // Gilded Midas Gold Sword handler
        if (swordInputStack.getItem().equals(MythicTools.GILDED_MIDAS_GOLD_SWORD)) {

            // Transform into Royal Midas Gold Sword
            if (goldCount >= 640) {
                var swordnite = swordInputStack.copyComponentsToNewStack(MythicTools.ROYAL_MIDAS_GOLD_SWORD, 1);
                swordnite.set(GOLD_FOLDED, GoldFoldedComponent.of(goldCount + 1, true));
                return swordnite;
            }
        }

        // Handle Midas Gold Sword, transform if you fold and it at least has 320 gold on it
        if (swordInputStack.getItem().equals(MythicTools.MIDAS_GOLD_SWORD)) {

            // Transform Midas Gold Sword into Gilded Midas Gold Sword
            if (goldCount >= 319) {
                var swordnite = swordInputStack.copyComponentsToNewStack(MythicTools.GILDED_MIDAS_GOLD_SWORD, 1);
                swordnite.set(GOLD_FOLDED, GoldFoldedComponent.of(goldCount + 1));
                return swordnite;
            }
        }

        return swordInputStack;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup lookup) {
        return this.result;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return RegisterRecipeSerializers.MIDAS_FOLDING_RECIPE;
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return this.addition.test(stack);
    }

    public static class Serializer extends EndecRecipeSerializer<MidasFoldingRecipe> {
        private static final StructEndec<MidasFoldingRecipe> ENDEC = StructEndecBuilder.of(
            Endec.ofCodec(Ingredient.ALLOW_EMPTY_CODEC).fieldOf("template", recipe -> recipe.template),
            Endec.ofCodec(Ingredient.ALLOW_EMPTY_CODEC).fieldOf("base", recipe -> recipe.base),
            Endec.ofCodec(Ingredient.ALLOW_EMPTY_CODEC).fieldOf("addition", recipe -> recipe.addition),
            BuiltInEndecs.ITEM_STACK.fieldOf("result", recipe -> recipe.result),
            MidasFoldingRecipe::new
        );

        public Serializer() {
            super(ENDEC);
        }
    }
}
