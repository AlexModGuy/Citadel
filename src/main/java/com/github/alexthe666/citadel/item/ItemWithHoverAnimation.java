package com.github.alexthe666.citadel.item;

import net.minecraft.world.item.ItemStack;

public interface ItemWithHoverAnimation {

    float getMaxHoverOverTime(ItemStack stack);
    boolean canHoverOver(ItemStack stack);
}
