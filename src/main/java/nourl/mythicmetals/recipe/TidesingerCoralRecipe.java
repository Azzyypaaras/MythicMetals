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
import nourl.mythicmetals.component.MythicDataComponents;
import nourl.mythicmetals.component.TidesingerPatternComponent;
import nourl.mythicmetals.data.MythicTags;
import nourl.mythicmetals.registry.RegisterRecipeSerializers;

public record TidesingerCoralRecipe(Ingredient base, Ingredient addition, Ingredient template,
                                    ItemStack result) implements SmithingRecipe {

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

    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.template.test(inventory.getStack(0)) && this.base.test(inventory.getStack(1)) && this.addition.test(inventory.getStack(2));
    }

    @Override
    public ItemStack craft(Inventory inventory, RegistryWrapper.WrapperLookup lookup) {
        var armorStack = this.result.copy();
        var formerArmorItem = inventory.getStack(1).getItem();
        armorStack.copyComponentsToNewStack(formerArmorItem, 1);
        var additionStack = inventory.getStack(2);

        if (additionStack.isIn(MythicTags.TIDESINGER_CORAL)) {
            armorStack.set(MythicDataComponents.TIDESINGER, TidesingerPatternComponent.fromStack(additionStack));
        }

        return armorStack;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup lookup) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RegisterRecipeSerializers.TIDESINGER_CORAL_RECIPE;
    }

    public static class Serializer extends EndecRecipeSerializer<TidesingerCoralRecipe> {

        public static final StructEndec<TidesingerCoralRecipe> ENDEC = StructEndecBuilder.of(
            Endec.ofCodec(Ingredient.ALLOW_EMPTY_CODEC).fieldOf("base", recipe -> recipe.base),
            Endec.ofCodec(Ingredient.ALLOW_EMPTY_CODEC).fieldOf("addition", recipe -> recipe.addition),
            Endec.ofCodec(Ingredient.ALLOW_EMPTY_CODEC).fieldOf("template", recipe -> recipe.template),
            BuiltInEndecs.ITEM_STACK.fieldOf("result", recipe -> recipe.result),
            TidesingerCoralRecipe::new
        );

        public Serializer(StructEndec<TidesingerCoralRecipe> endec) {
            super(endec);
        }
    }
}
