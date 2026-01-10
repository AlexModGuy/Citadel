package com.github.alexthe666.citadel.client.gui.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record RecipeData(
    ResourceLocation recipe, boolean shapeless,
    int x, int y, double scale, int page
) {
    public static final Codec<RecipeData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ResourceLocation.CODEC
                .fieldOf("recipe")
                .forGetter(RecipeData::recipe),
            Codec.BOOL
                .fieldOf("shapeless")
                .forGetter(RecipeData::shapeless),
            Codec.INT
                .fieldOf("x")
                .forGetter(RecipeData::x),
            Codec.INT
                .fieldOf("y")
                .forGetter(RecipeData::y),
            Codec.DOUBLE
                .optionalFieldOf("scale", 1.0)
                .forGetter(RecipeData::scale),
            Codec.INT
                .optionalFieldOf("page", 0)
                .forGetter(RecipeData::page)
        )
            .apply(instance, RecipeData::new)
    );
}
