package com.github.alexthe666.citadel.client.texture;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

public class ColorMappedTexture extends SimpleTexture {

    private int[] colors;

    public ColorMappedTexture(ResourceLocation resourceLocation, int[] colors) {
        super(resourceLocation);
        this.colors = colors;
    }

    public void load(ResourceManager resourceManager) throws IOException {
        NativeImage nativeimage = getNativeImage(resourceManager, location);
        if(nativeimage != null){
            if(resourceManager.getResource(location).isPresent()){
                Resource resource = resourceManager.getResource(location).get();
                try {
                    ColorsMetadataSection section = resource.metadata().getSection(ColorsMetadataSection.SERIALIZER).orElse(new ColorsMetadataSection(null));
                    NativeImage nativeimage2 = getNativeImage(resourceManager, section.getColorRamp());
                    if(nativeimage2 != null){
                        processColorMap(nativeimage, nativeimage2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            TextureUtil.prepareImage(this.getId(), nativeimage.getWidth(), nativeimage.getHeight());
            this.bind();
            nativeimage.upload(0, 0, 0, false);
        }
    }

    private NativeImage getNativeImage(ResourceManager resourceManager, @Nullable ResourceLocation resourceLocation) {
        Resource resource = null;
        if(resourceLocation == null){
            return null;
        }
        try {
            resource = resourceManager.getResourceOrThrow(resourceLocation);
            InputStream inputstream = resource.open();
            NativeImage nativeimage = NativeImage.read(inputstream);
            if (inputstream != null) {
                inputstream.close();
            }
            return nativeimage;
        }catch (Throwable throwable1) {
            return null;
        }
    }

    private void processColorMap(NativeImage nativeImage, NativeImage colorMap) {
        int[] fromColorMap = new int[colorMap.getHeight()];
        for(int i = 0; i < fromColorMap.length; i++){
            fromColorMap[i] = colorMap.getPixelRGBA(0, i);
        }
        for (int i = 0; i < nativeImage.getWidth(); i++) {
            for (int j = 0; j < nativeImage.getHeight(); j++) {
                int colorAt = nativeImage.getPixelRGBA(i, j);
                if(FastColor.ABGR32.alpha(colorAt) == 0){
                    continue;
                }
                int replaceIndex = -1;
                for(int k = 0; k < fromColorMap.length; k++){
                    if(colorAt == fromColorMap[k]){
                        replaceIndex = k;
                    }
                }
                if (replaceIndex >= 0 && colors.length > replaceIndex) {
                    int r = colors[replaceIndex] >> 16 & 255;
                    int g = colors[replaceIndex] >> 8 & 255;
                    int b = colors[replaceIndex] & 255;
                    nativeImage.setPixelRGBA(i, j, FastColor.ABGR32.color(FastColor.ABGR32.alpha(colorAt), b, g, r));
                }
            }
        }
    }

    private static class ColorsMetadataSection {

        public static final ColorsMetadataSectionSerializer SERIALIZER = new ColorsMetadataSectionSerializer();

        private ResourceLocation colorRamp;
        public ColorsMetadataSection(ResourceLocation colorRamp) {
            this.colorRamp = colorRamp;
        }

        private boolean areColorsEqual(int color1, int color2){
            int r1 = color1 >> 16 & 255;
            int g1 = color1 >> 8 & 255;
            int b1 = color1 & 255;
            int r2 = color2 >> 16 & 255;
            int g2 = color2 >> 8 & 255;
            int b2 = color2 & 255;
            return r1 == r2 && g1 == g2 && b1 == b2;
        }

        public ResourceLocation getColorRamp(){
            return colorRamp;
        }
    }

    private static class ColorsMetadataSectionSerializer implements MetadataSectionSerializer<ColorsMetadataSection> {
        private ColorsMetadataSectionSerializer() {
        }

        public ColorsMetadataSection fromJson(JsonObject json) {

            return new ColorsMetadataSection(new ResourceLocation(GsonHelper.getAsString(json, "color_ramp")));
        }

        public String getMetadataSectionName() {
            return "colors";
        }
    }
}
