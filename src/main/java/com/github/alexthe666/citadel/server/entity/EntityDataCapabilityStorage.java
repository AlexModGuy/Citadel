package com.github.alexthe666.citadel.server.entity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class EntityDataCapabilityStorage implements Capability.IStorage<IEntityData> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IEntityData> capability, IEntityData instance, Direction side) {
        CompoundNBT compound = new CompoundNBT();
        instance.saveNBTData(compound);
        return compound;
    }

    @Override
    public void readNBT(Capability<IEntityData> capability, IEntityData instance, Direction side, INBT nbt) {
        instance.loadNBTData((CompoundNBT) nbt);

    }
}