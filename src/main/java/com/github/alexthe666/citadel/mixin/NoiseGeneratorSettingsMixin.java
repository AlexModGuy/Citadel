package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NoiseGeneratorSettings.class, priority = 500)
public class NoiseGeneratorSettingsMixin {
    @Mutable
    @Final
    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    private boolean mergedSurfaceRules = false;

    @Inject(method = "surfaceRule", at = @At("HEAD"))
    private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (!this.mergedSurfaceRules) {
            this.surfaceRule = SurfaceRulesManager.mergeOverworldRules(surfaceRule);
            this.mergedSurfaceRules = true;
            //not replacing the return result for compatibility with TerraBlender
        }
    }
}
