package com.github.alexthe666.citadel.client.gui.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public record EntityLinkData(
    ResourceKey<EntityType<?>> entity,
    int x, int y, double scale, double entityScale,
    int page, String linkedPage,
    Component hoverText,
    float offsetX, float offsetY
) {
    public static final Codec<EntityLinkData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ResourceKey.codec(Registries.ENTITY_TYPE)
                .fieldOf("entity")
                .forGetter(EntityLinkData::entity),
            Codec.INT
                .fieldOf("x")
                .forGetter(EntityLinkData::x),
            Codec.INT
                .fieldOf("y")
                .forGetter(EntityLinkData::y),
            Codec.DOUBLE
                .optionalFieldOf("entity_scale", 1.0)
                .forGetter(EntityLinkData::entityScale),
            Codec.DOUBLE
                .optionalFieldOf("scale", 1.0)
                .forGetter(EntityLinkData::scale),
            Codec.INT
                .optionalFieldOf("page", 0)
                .forGetter(EntityLinkData::page),
            Codec.STRING
                .fieldOf("linked_page")
                .forGetter(EntityLinkData::linkedPage),
            ComponentSerialization.CODEC
                .fieldOf("hover_text")
                .forGetter(EntityLinkData::hoverText),
            Codec.FLOAT
                .optionalFieldOf("offset_x", 0f)
                .forGetter(EntityLinkData::offsetX),
            Codec.FLOAT
                .optionalFieldOf("offset_y", 0f)
                .forGetter(EntityLinkData::offsetY)
        )
            .apply(instance, EntityLinkData::new)
    );
}
