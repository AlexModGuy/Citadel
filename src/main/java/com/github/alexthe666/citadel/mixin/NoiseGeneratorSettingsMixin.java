package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NoiseGeneratorSettings.class, priority = 500)
public class NoiseGeneratorSettingsMixin {

    @Shadow
    @Final
    private SurfaceRules.RuleSource surfaceRule;

    @Unique
    private SurfaceRules.RuleSource citadel$mergedSurfaceRule = null;

    @Unique
    private boolean citadel$hasMerged = false;

    @Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true)
    private void citadel$surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (!this.citadel$hasMerged) {
            this.citadel$mergedSurfaceRule = SurfaceRulesManager.mergeOverworldRules(this.surfaceRule);
            this.citadel$hasMerged = true;
        }
        if (this.citadel$mergedSurfaceRule != null) {
            cir.setReturnValue(this.citadel$mergedSurfaceRule);
        }
    }
}
