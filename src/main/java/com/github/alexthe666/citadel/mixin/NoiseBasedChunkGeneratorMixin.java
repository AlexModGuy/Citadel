package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = NoiseBasedChunkGenerator.class, priority = 500)
public class NoiseBasedChunkGeneratorMixin {

    @Unique
    private SurfaceRules.RuleSource mergedSurfaceRules;

    private SurfaceRules.RuleSource getOrCreateMergedSurfaceRules(SurfaceRules.RuleSource ruleSource){
        if(mergedSurfaceRules == null){
            this.mergedSurfaceRules = SurfaceRulesManager.mergeOverworldRules(ruleSource);
        }
        return this.mergedSurfaceRules;
    }

    @ModifyArg(
            method = "Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/SurfaceSystem;buildSurface(Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;ZLnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/NoiseChunk;Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;)V"),
            index = 7
    )
    private SurfaceRules.RuleSource citadel_buildSurface_modifySurfaceRules(SurfaceRules.RuleSource ruleSource) {
        return getOrCreateMergedSurfaceRules(ruleSource);
    }

    @ModifyArg(
            method = "Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;applyCarvers(Lnet/minecraft/server/level/WorldGenRegion;JLnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/GenerationStep$Carving;)V",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/carver/CarvingContext;<init>(Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/NoiseChunk;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;)V"),
            index = 5
    )
    private SurfaceRules.RuleSource citadel_applyCarvers_modifySurfaceRules(SurfaceRules.RuleSource ruleSource) {
        return getOrCreateMergedSurfaceRules(ruleSource);
    }
}
