package com.github.alexthe666.citadel.server.tick.modifier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.level.Level;

public class LocalEntityTickRateModifier extends LocalTickRateModifier {

    private int entityId;
    private EntityType expectedEntityType;

    public LocalEntityTickRateModifier(int entityId, EntityType expectedEntityType, double range, ResourceKey<Level> dimension, int durationInMasterTicks, float tickRateMultiplier) {
        super(TickRateModifierType.LOCAL_POSITION, range, dimension, durationInMasterTicks, tickRateMultiplier);
        this.entityId = entityId;
        this.expectedEntityType = expectedEntityType;
    }

    public LocalEntityTickRateModifier(CompoundTag tag) {
        super(tag);
        this.entityId = tag.getInt("EntityId");
        EntityType type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(tag.getString("EntityType")));
        this.expectedEntityType = type == null ? EntityType.PIG : type;
    }

    @Override
    public Vec3 getCenter(Level level) {
        Entity entity = level.getEntity(this.entityId);
        if(entity != null && entity.getType().equals(expectedEntityType)){
            return entity.position();
        }
        return Vec3.ZERO;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();
        tag.putInt("EntityId", entityId);
        ResourceLocation resourcelocation = ForgeRegistries.ENTITY_TYPES.getKey(this.expectedEntityType);
        if (resourcelocation != null) {
            tag.putString("EntityType", resourcelocation.toString());
        }
        return tag;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public EntityType getExpectedEntityType() {
        return expectedEntityType;
    }

    public void setExpectedEntityType(EntityType expectedEntityType) {
        this.expectedEntityType = expectedEntityType;
    }
}
