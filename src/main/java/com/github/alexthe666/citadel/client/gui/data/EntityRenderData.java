package com.github.alexthe666.citadel.client.gui.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public record EntityRenderData(
    EntityType<?> entity,
    int x, int y, double scale, int page,
    double rotX, double rotY, double rotZ,
    boolean followCursor,
    Optional<String> entityData
) {
    public static final Codec<EntityRenderData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec()
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
                .optionalFieldOf("entity_data")
                .forGetter(EntityRenderData::entityData)
        )
            .apply(instance, EntityRenderData::new)
    );
}
