package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimaryLevelData.class)
public class PrimaryLevelDataMixin {

    @Inject(at = @At("HEAD"), remap = CitadelConstants.REMAPREFS,
            method = "Lnet/minecraft/world/level/storage/PrimaryLevelData;setTagData(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/nbt/CompoundTag;)V")
    private void citadel_preSetTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag1, CallbackInfo ci) {
        SurfaceRulesManager.setLevelBeingSaved(true);
    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS,
            method = "Lnet/minecraft/world/level/storage/PrimaryLevelData;setTagData(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/nbt/CompoundTag;)V")
    private void citadel_postSetTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag1, CallbackInfo ci) {
        SurfaceRulesManager.setLevelBeingSaved(false);
    }
}
