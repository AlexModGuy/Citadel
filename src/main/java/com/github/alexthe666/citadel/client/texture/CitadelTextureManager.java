package com.github.alexthe666.citadel.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class CitadelTextureManager {

    private static final Map<ResourceLocation, ResourceLocation> COLOR_MAPPED_TEXTURES = new HashMap<>();

    public static ResourceLocation getColorMappedTexture(ResourceLocation textureLoc, int[] colors){
        return getColorMappedTexture(textureLoc, textureLoc, colors);
    }

    public static ResourceLocation getColorMappedTexture(ResourceLocation namespace, ResourceLocation textureLoc, int[] colors){
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture abstracttexture = textureManager.getTexture(namespace, MissingTextureAtlasSprite.getTexture());
        if (abstracttexture == MissingTextureAtlasSprite.getTexture()) {
            textureManager.register(namespace, new ColorMappedTexture(textureLoc, colors));
        }
        return namespace;
    }

    public static VideoFrameTexture getVideoTexture(ResourceLocation namespace, int defaultWidth, int defaultHeight){
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture abstracttexture = textureManager.getTexture(namespace, MissingTextureAtlasSprite.getTexture());
        if (abstracttexture == MissingTextureAtlasSprite.getTexture()) {
            abstracttexture = new VideoFrameTexture(new NativeImage(defaultWidth, defaultHeight, false));
            textureManager.register(namespace, abstracttexture);
        }
        return abstracttexture instanceof VideoFrameTexture ? (VideoFrameTexture) abstracttexture : null;
    }
}
