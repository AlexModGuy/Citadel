package com.github.alexthe666.citadel.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.IItemRenderProperties;

public class CitadelItemRenderProperties implements IItemRenderProperties {

    private final BlockEntityWithoutLevelRenderer renderer = new CitadelItemstackRenderer();

    public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
        return renderer;
    }
}
