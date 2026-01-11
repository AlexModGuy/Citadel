package com.github.alexthe666.citadel.client.gui.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record TabulaRenderData(
    ResourceLocation model, ResourceLocation texture,
    int x, int y, double scale, int page,
    double rotX, double rotY, double rotZ, boolean followCursor
) {
    public static final Codec<TabulaRenderData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ResourceLocation.CODEC
                .fieldOf("model")
                .forGetter(TabulaRenderData::model),
            ResourceLocation.CODEC
                .fieldOf("texture")
                .forGetter(TabulaRenderData::texture),
            Codec.INT
                .fieldOf("x")
                .forGetter(TabulaRenderData::x),
            Codec.INT
                .fieldOf("y")
                .forGetter(TabulaRenderData::y),
            Codec.DOUBLE
                .optionalFieldOf("scale", 1.0)
                .forGetter(TabulaRenderData::scale),
            Codec.INT
                .optionalFieldOf("page", 0)
                .forGetter(TabulaRenderData::page),
            Codec.DOUBLE
                .optionalFieldOf("rot_x", 0.0)
                .forGetter(TabulaRenderData::rotX),
            Codec.DOUBLE
                .optionalFieldOf("rot_y", 0.0)
                .forGetter(TabulaRenderData::rotY),
            Codec.DOUBLE
                .optionalFieldOf("rot_z", 0.0)
                .forGetter(TabulaRenderData::rotZ),
            Codec.BOOL
                .optionalFieldOf("follow_cursor", false)
                .forGetter(TabulaRenderData::followCursor)
        )
            .apply(instance, TabulaRenderData::new)
    );
}
