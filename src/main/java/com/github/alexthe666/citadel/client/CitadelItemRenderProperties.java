package com.github.alexthe666.citadel.client;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class CitadelItemRenderProperties implements IClientItemExtensions {

    private final BlockEntityWithoutLevelRenderer renderer = new CitadelItemstackRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return renderer;
    }
}
