package com.github.alexthe666.citadel.server.entity;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class EntityDataCapabilityImplementation implements IEntityData {
    private Entity entity;

    public static IEntityData getCapability(Entity entity) {
        return entity.getCapability(Citadel.ENTITY_DATA_CAPABILITY, null).orElseGet(null);
    }

    @Override
    public void init(Entity entity, World world) {

    }

    @Override
    public void init(Entity entity, World world, boolean init) {
        this.entity = entity;
        if (init) {
            for (IEntityData entityData : EntityDataHandler.INSTANCE.getEntityData(entity)) {
                entityData.init(entity, world);
            }
        }
    }

    @Override
    public void saveNBTData(CompoundNBT compound) {
        for (IEntityData entityData : EntityDataHandler.INSTANCE.getEntityData(this.entity)) {
            CompoundNBT managerTag = new CompoundNBT();
            entityData.saveNBTData(managerTag);
            compound.put(entityData.getID(), managerTag);
        }
    }

    @Override
    public void loadNBTData(CompoundNBT compound) {
        for (IEntityData entityData : EntityDataHandler.INSTANCE.getEntityData(this.entity)) {
            CompoundNBT managerTag = compound.getCompound(entityData.getID());
            entityData.loadNBTData(managerTag);
        }
    }

    @Override
    public String getID() {
        return "data_cap_citadel";
    }
}