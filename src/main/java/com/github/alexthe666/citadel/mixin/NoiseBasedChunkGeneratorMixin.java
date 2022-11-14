package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {

    @Shadow protected Holder<NoiseGeneratorSettings> settings;

    @Inject(
            at = {@At("TAIL")},
            remap = true,
            method = {"Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;<init>(Lnet/minecraft/core/Registry;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/core/Holder;)V"},
            cancellable = true
    )
    private void citadel_noiseBasedChunkGenerator_inot(Registry structureRegistry, Registry noiseRegistry, BiomeSource biomeSource, Holder<NoiseGeneratorSettings> noiseGeneratorSettingsHolder, CallbackInfo ci) {
       settings = Holder.direct(copyOfWithRules(noiseGeneratorSettingsHolder.value(), SurfaceRulesManager.replaceRulesOf(noiseGeneratorSettingsHolder)));

    }


    private static NoiseGeneratorSettings copyOfWithRules(NoiseGeneratorSettings settings, SurfaceRules.RuleSource rules){
        return new NoiseGeneratorSettings(settings.noiseSettings(), settings.defaultBlock(), settings.defaultFluid(), settings.noiseRouter(), rules, settings.spawnTarget(), settings.seaLevel(), settings.disableMobGeneration(), settings.aquifersEnabled(), settings.oreVeinsEnabled(), settings.useLegacyRandomSource());
    }
}
