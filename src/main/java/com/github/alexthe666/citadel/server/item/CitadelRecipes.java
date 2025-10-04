package com.github.alexthe666.citadel.server.item;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;

import java.util.ArrayList;
import java.util.List;

public class CitadelRecipes {
    private static final List<RecipeHolder<SmithingRecipe>> smithingRecipes = new ArrayList<>();

    public static void registerSmithingRecipe(RecipeHolder<SmithingRecipe> recipe){
        smithingRecipes.add(recipe);
    }

    public static List<RecipeHolder<SmithingRecipe>> getSmithingRecipes(){
        return smithingRecipes;
    }
}
