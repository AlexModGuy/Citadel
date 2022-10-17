package com.github.alexthe666.citadel.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface SpecialRecipeInGuideBook {
    NonNullList<Ingredient> getDisplayIngredients();
    ItemStack getDisplayResultFor(NonNullList<ItemStack> stacks);
}
