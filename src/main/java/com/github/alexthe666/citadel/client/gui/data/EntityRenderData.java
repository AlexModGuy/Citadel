package com.github.alexthe666.citadel.client.gui.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public record EntityRenderData(
    ResourceKey<EntityType<?>> entity,
    int x, int y, double scale, int page,
    double rotX, double rotY, double rotZ,
    boolean followCursor, String entityData
) {
    public static final Codec<EntityRenderData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ResourceKey.codec(Registries.ENTITY_TYPE)
                .fieldOf("entity")
                .forGetter(EntityRenderData::entity),
            Codec.INT
                .fieldOf("x")
                .forGetter(EntityRenderData::x),
            Codec.INT
                .fieldOf("y")
                .forGetter(EntityRenderData::y),
            Codec.DOUBLE
                .optionalFieldOf("scale", 1.0)
                .forGetter(EntityRenderData::scale),
            Codec.INT
                .optionalFieldOf("page", 0)
                .forGetter(EntityRenderData::page),
            Codec.DOUBLE
                .optionalFieldOf("rot_x", 0.0)
                .forGetter(EntityRenderData::rotX),
            Codec.DOUBLE
                .optionalFieldOf("rot_y", 0.0)
                .forGetter(EntityRenderData::rotY),
            Codec.DOUBLE
                .optionalFieldOf("rot_z", 0.0)
                .forGetter(EntityRenderData::rotZ),
            Codec.BOOL
                .optionalFieldOf("follow_cursor", false)
                .forGetter(EntityRenderData::followCursor),
            Codec.STRING
                .optionalFieldOf("entity_data", "")
                .forGetter(EntityRenderData::entityData)
        )
            .apply(instance, EntityRenderData::new)
    );
}
