package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin {

    @Inject(
            at = {@At("TAIL")},
            remap = true,
            method = {"Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;surfaceRule()Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;"},
            cancellable = true
    )
    private void citadel_surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        SurfaceRules.RuleSource prev = (SurfaceRules.RuleSource)cir.getReturnValue();
        cir.setReturnValue(SurfaceRulesManager.process((NoiseGeneratorSettings)(Object)this, prev));
    }

}
