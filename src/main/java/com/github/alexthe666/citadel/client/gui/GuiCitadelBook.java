package com.github.alexthe666.citadel.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GuiCitadelBook extends GuiBasicBook {

    public GuiCitadelBook(ItemStack bookStack) {
        super(bookStack, Component.translatable("citadel_guide_book.title"));
    }

    @Override
    protected int getBindingColor() {
        return 0X64A27B;
    }

    @Override
    public ResourceLocation getRootPage() {
        return ResourceLocation.fromNamespaceAndPath("citadel", "book/citadel_book/root.json");
    }

    @Override
    public ResourceLocation getTextFileDirectory() {
        return ResourceLocation.fromNamespaceAndPath("citadel", "book/citadel_book/");
    }
}
