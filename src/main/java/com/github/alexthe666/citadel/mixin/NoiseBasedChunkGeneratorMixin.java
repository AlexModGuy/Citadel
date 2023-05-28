package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {

    private boolean mergedSurfaceRules = false;
    private SurfaceRules.RuleSource mergedSurfaceRule = null;

    @Redirect(
            method = {"Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;surfaceRule()Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;")
    )
    private SurfaceRules.RuleSource citadel_buildSurface_surfaceRuleRedirect(NoiseGeneratorSettings noiseGeneratorSettings) {
        return getMergedSurfaceRule(noiseGeneratorSettings.surfaceRule());
    }

    @Redirect(
            method = {"Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;applyCarvers(Lnet/minecraft/server/level/WorldGenRegion;JLnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/GenerationStep$Carving;)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;surfaceRule()Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;")
    )
    private SurfaceRules.RuleSource citadel_applyCarvers_surfaceRuleRedirect(NoiseGeneratorSettings noiseGeneratorSettings) {
        return getMergedSurfaceRule(noiseGeneratorSettings.surfaceRule());
    }

    private SurfaceRules.RuleSource getMergedSurfaceRule(SurfaceRules.RuleSource rules) {
        if (!mergedSurfaceRules) {
            mergedSurfaceRules = true;
            mergedSurfaceRule = SurfaceRulesManager.mergeOverworldRules(rules);
        }
        return mergedSurfaceRule == null ? rules : mergedSurfaceRule;
    }
}
