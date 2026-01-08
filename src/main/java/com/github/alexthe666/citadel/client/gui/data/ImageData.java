package com.github.alexthe666.citadel.client.gui.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record ImageData(
    ResourceLocation texture,
    int x,
    int y,
    int page,
    double scale,
    int u,
    int v,
    int width,
    int height
) {
    public static final Codec<ImageData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ResourceLocation.CODEC
                .fieldOf("texture")
                .forGetter(ImageData::texture),
            Codec.INT
                .fieldOf("x")
                .forGetter(ImageData::x),
            Codec.INT
                .fieldOf("y")
                .forGetter(ImageData::y),
            Codec.INT
                .optionalFieldOf("page", 0)
                .forGetter(ImageData::page),
            Codec.DOUBLE
                .optionalFieldOf("scale", 1.0)
                .forGetter(ImageData::scale),
            Codec.INT
                .fieldOf("u")
                .forGetter(ImageData::u),
            Codec.INT
                .fieldOf("v")
                .forGetter(ImageData::v),
            Codec.INT
                .fieldOf("width")
                .forGetter(ImageData::width),
            Codec.INT
                .fieldOf("height")
                .forGetter(ImageData::height)
        )
            .apply(instance, ImageData::new)
    );
}
