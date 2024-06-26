package nourl.mythicmetals.recipe;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.EndecRecipeSerializer;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SmithingRecipeInput;
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
    public boolean matches(SmithingRecipeInput input, World world) {
        return this.template.test(input.template()) && this.base.test(input.base()) && this.addition.test(input.addition());
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        var armorStack = this.result.copy();
        var formerArmorItem = input.base().getItem();
        armorStack.copyComponentsToNewStack(formerArmorItem, 1);

        if (input.addition().isIn(MythicTags.TIDESINGER_CORAL)) {
            armorStack.set(MythicDataComponents.TIDESINGER, TidesingerPatternComponent.fromStack(input.addition()));
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
            CodecUtils.toEndec(Ingredient.ALLOW_EMPTY_CODEC).fieldOf("base", recipe -> recipe.base),
            CodecUtils.toEndec(Ingredient.ALLOW_EMPTY_CODEC).fieldOf("addition", recipe -> recipe.addition),
            CodecUtils.toEndec(Ingredient.ALLOW_EMPTY_CODEC).fieldOf("template", recipe -> recipe.template),
            MinecraftEndecs.ITEM_STACK.fieldOf("result", recipe -> recipe.result),
            TidesingerCoralRecipe::new
        );

        public Serializer(StructEndec<TidesingerCoralRecipe> endec) {
            super(endec);
        }
    }
}
