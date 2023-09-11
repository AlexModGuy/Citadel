package com.github.alexthe666.citadel.server.tick.modifier;

import com.github.alexthe666.citadel.server.entity.IModifiesTime;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SingleEntityTickRateModifier extends LocalTickRateModifier {

    private int entityId;

    public SingleEntityTickRateModifier(int entityId, ResourceKey<Level> dimension, int durationInMasterTicks, float tickRateMultiplier) {
        super(TickRateModifierType.SINGLE_ENTITY, 0, dimension, durationInMasterTicks, tickRateMultiplier);
        this.entityId = entityId;
    }

    public SingleEntityTickRateModifier(CompoundTag tag) {
        super(tag);
        this.entityId = tag.getInt("EntityId");
    }

    @Override
    public Vec3 getCenter(Level level) {
        Entity entity = level.getEntity(this.entityId);
        /*if(isEntityValid(level) && entity != null){
            return entity.position();
        }*/
        return Vec3.ZERO;
    }

    @Override
    public boolean appliesTo(Level level, double x, double y, double z) {
        return false;
    }

    public boolean isEntityValid(Level level, Entity entity) {
        Entity entity2 = level.getEntity(this.entityId);
        return entity2 == entity && entity.isAlive() && (!(entity instanceof IModifiesTime) || ((IModifiesTime) entity).isTimeModificationValid(this));
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();
        tag.putInt("EntityId", entityId);
        return tag;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}
