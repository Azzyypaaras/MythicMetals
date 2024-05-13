package nourl.mythicmetals.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import nourl.mythicmetals.component.GoldFoldedComponent;
import nourl.mythicmetals.item.MythicItems;
import nourl.mythicmetals.item.tools.MythicTools;
import nourl.mythicmetals.registry.RegisterRecipeSerializers;

import static nourl.mythicmetals.component.MythicDataComponents.GOLD_FOLDED;

public record MidasFoldingRecipe(Ingredient base, Ingredient addition, Ingredient template,
                                 ItemStack result) implements SmithingRecipe {

    @Override
    public boolean matches(Inventory inventory, World world) {
        if (this.template.test(inventory.getStack(0)) && this.base.test(inventory.getStack(1)) && this.addition.test(inventory.getStack(2))) {
            var stack = inventory.getStack(1);

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

        // TODO - Handle the component not being present

        // Gilded Midas Gold Sword handler
        if (swordInputStack.getItem().equals(MythicTools.GILDED_MIDAS_GOLD_SWORD)) {
            int goldCount = swordInputStack.contains(GOLD_FOLDED) ? swordInputStack.get(GOLD_FOLDED).goldFolded() : 0;
            // Allow you to max out a Gilded Midas Sword
            if (goldCount < 640) {
                swordInputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(goldCount + 1));
            }

            // Transform into Royal Midas Gold Sword
            if (goldCount >= 640) {
                var swordnite = new ItemStack(MythicTools.ROYAL_MIDAS_GOLD_SWORD);
                //swordInputStack.put(MidasGoldSword.IS_ROYAL, true);
                swordnite.applyComponentsFrom(swordInputStack.getComponents());
                return swordnite;
            }
        }

        // Handle Midas Gold Sword, transform if you fold and it at least has 320 gold on it
        if (swordInputStack.getItem().equals(MythicTools.MIDAS_GOLD_SWORD)) {
            int goldCount = swordInputStack.contains(GOLD_FOLDED) ? swordInputStack.get(GOLD_FOLDED).goldFolded() : 0;

            if (goldCount < 640) {

                swordInputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(goldCount + 1));
            }

            // Transform Midas Gold Sword into Gilded Midas Gold Sword
            if (goldCount >= 319) {
                var swordnite = new ItemStack(MythicTools.GILDED_MIDAS_GOLD_SWORD);
                //swordInputStack.put(MidasGoldSword.IS_GILDED, true);
                swordnite.applyComponentsFrom(swordInputStack.getComponents());
                return swordnite;
            }
        }

        // Handle Royal Midas Gold Sword
        if (swordInputStack.getItem().equals(MythicTools.ROYAL_MIDAS_GOLD_SWORD)) {
            int goldCount = swordInputStack.contains(GOLD_FOLDED) ? swordInputStack.get(GOLD_FOLDED).goldFolded() : 0;
            if (goldCount < 10000) {

                swordInputStack.set(GOLD_FOLDED, GoldFoldedComponent.of(goldCount + 1));
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

    // TODO - Convert to use Endec instead of Codec
    public static class Serializer implements RecipeSerializer<MidasFoldingRecipe> {
        private static final MapCodec<MidasFoldingRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                    Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition),
                    Ingredient.ALLOW_EMPTY_CODEC.fieldOf("template").forGetter(recipe -> recipe.template),
                    ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                )
                .apply(instance, MidasFoldingRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, MidasFoldingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            MidasFoldingRecipe.Serializer::write, MidasFoldingRecipe.Serializer::read
        );

        @Override
        public PacketCodec<RegistryByteBuf, MidasFoldingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        @Override
        public MapCodec<MidasFoldingRecipe> codec() {
            return CODEC;
        }

        private static MidasFoldingRecipe read(RegistryByteBuf buf) {
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient ingredient2 = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient ingredient3 = Ingredient.PACKET_CODEC.decode(buf);
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
            return new MidasFoldingRecipe(ingredient, ingredient2, ingredient3, itemStack);
        }

        private static void write(RegistryByteBuf buf, MidasFoldingRecipe smithingRecipe) {
            Ingredient.PACKET_CODEC.encode(buf, smithingRecipe.template);
            Ingredient.PACKET_CODEC.encode(buf, smithingRecipe.base);
            Ingredient.PACKET_CODEC.encode(buf, smithingRecipe.addition);
            ItemStack.PACKET_CODEC.encode(buf, smithingRecipe.result);
        }
    }
}
