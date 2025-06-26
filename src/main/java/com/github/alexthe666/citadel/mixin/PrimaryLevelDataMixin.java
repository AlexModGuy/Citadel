package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.generation.NoiseGeneratorSettingsAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimaryLevelData.class)
public class PrimaryLevelDataMixin {

    @Inject(at = @At("HEAD"), remap = CitadelConstants.REMAPREFS,
            method = "Lnet/minecraft/world/level/storage/PrimaryLevelData;setTagData(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/nbt/CompoundTag;)V")
    private void citadel_preSetTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag1, CallbackInfo ci) {
        citadelUpdateSurfaceRules(registryAccess, true);
    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS,
            method = "Lnet/minecraft/world/level/storage/PrimaryLevelData;setTagData(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/nbt/CompoundTag;)V")
    private void citadel_postSetTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag1, CallbackInfo ci) {
        citadelUpdateSurfaceRules(registryAccess, false);
        CompoundTag test = compoundTag.getCompound("WorldGenSettings");
        if(test != null){
            compoundTag.put("WorldGenSettings", removeAnyCitadelTypes(test));
        }
    }

    @Unique
    private void citadelUpdateSurfaceRules(RegistryAccess registryAccess, boolean saving) {
        Registry<LevelStem> registry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        if (registry.containsKey(LevelStem.OVERWORLD)) {
            LevelStem levelstem = registry.get(LevelStem.OVERWORLD);
            if (levelstem.generator() instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator && noiseBasedChunkGenerator.settings.isBound() && (Object) noiseBasedChunkGenerator.settings.get() instanceof NoiseGeneratorSettingsAccessor accessor) {
                accessor.onSaveData(saving);
            }
        }
    }

    @Unique
    private CompoundTag removeAnyCitadelTypes(CompoundTag test) {
        CompoundTag dimensionTag = test.getCompound("dimensions");
        for(String dimension : dimensionTag.getAllKeys()){
            if(dimensionTag.contains(dimension, 10)){
                CompoundTag generatorTag = dimensionTag.getCompound(dimension).getCompound("generator");
                if(generatorTag.contains("settings", 10)){
                    CompoundTag settings = generatorTag.getCompound("settings");
                    CompoundTag surfaceRule = settings.getCompound("surface_rule");
                    removeAnyCitadelTypesFromTerrablenderSurfaceRule(settings, surfaceRule);
                }
            }
        }
        return test;
    }

    @Unique
    private void removeAnyCitadelTypesFromTerrablenderSurfaceRule(CompoundTag parentTag, CompoundTag surfaceRule) { //terrablender recursive packaging crisis of '25
        if(surfaceRule.getString("type").equals("citadel:citadel_wrapper")){
            parentTag.remove("base");
            return;
        }
        if(surfaceRule.contains("base", 10)){
            removeAnyCitadelTypesFromTerrablenderSurfaceRule(surfaceRule, surfaceRule.getCompound("base"));
        }
    }

}
