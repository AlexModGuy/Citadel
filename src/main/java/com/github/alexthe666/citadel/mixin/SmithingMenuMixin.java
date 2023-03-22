package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.item.CitadelRecipes;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(SmithingMenu.class)
public class SmithingMenuMixin {

    @Redirect(
            method = "Lnet/minecraft/world/inventory/SmithingMenu;createResult()V",
            remap = CitadelConstants.REMAPREFS, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/List;")
    )
    private List<SmithingRecipe> citadel_getRecipesFor(RecipeManager recipeManager, RecipeType<SmithingRecipe> type, Container container, Level level) {
        List<SmithingRecipe> list = new ArrayList<>();
        list.addAll(recipeManager.getRecipesFor(type, container, level));
        if(type == RecipeType.SMITHING && container.getContainerSize() >= 2 && !container.getItem(0).isEmpty()&& !container.getItem(1).isEmpty()){
            list.addAll(CitadelRecipes.getSmithingRecipes());
        }
        return list;
    }
}