package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.entity.ICitadelDataEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ICitadelDataEntity {

    private static final EntityDataAccessor<CompoundTag> CITADEL_DATA = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.COMPOUND_TAG);

    protected LivingEntityMixin(EntityType<? extends Entity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, method = "Lnet/minecraft/world/entity/LivingEntity;defineSynchedData()V")
    private void citadel_registerData(CallbackInfo ci) {
        entityData.define(CITADEL_DATA, new CompoundTag());
    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, method = "Lnet/minecraft/world/entity/LivingEntity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    private void citadel_writeAdditional(CompoundTag compoundNBT, CallbackInfo ci) {
        CompoundTag citadelDat = getCitadelEntityData();
        if (citadelDat != null) {
            compoundNBT.put("CitadelData", citadelDat);
        }
    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, method = "Lnet/minecraft/world/entity/LivingEntity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    private void citadel_readAdditional(CompoundTag compoundNBT, CallbackInfo ci) {
        if (compoundNBT.contains("CitadelData")) {
            setCitadelEntityData(compoundNBT.getCompound("CitadelData"));
        }
    }

    public CompoundTag getCitadelEntityData() {
        return entityData.get(CITADEL_DATA);
    }

    public void setCitadelEntityData(CompoundTag nbt) {
        entityData.set(CITADEL_DATA, nbt);
    }
}
