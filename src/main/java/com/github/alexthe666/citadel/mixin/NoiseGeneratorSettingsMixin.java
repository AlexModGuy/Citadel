package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.generation.NoiseGeneratorSettingsAccessor;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NoiseGeneratorSettings.class, priority = 300)
public class NoiseGeneratorSettingsMixin implements NoiseGeneratorSettingsAccessor {

    @Mutable
    @Final
    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    @Unique
    private SurfaceRules.RuleSource unmodifiedSurfaceRule;
    @Unique
    private SurfaceRules.RuleSource citadelSurfaceRule = null;
    @Unique
    private boolean hasModifiedRules = false;
    @Unique
    private boolean saving = false;
    @Unique
    private SurfaceRules.RuleSource swapSurfaceRule = null;

    @Inject(method = "surfaceRule", at = @At("HEAD"))
    private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (!hasModifiedRules) { // initialized
            this.unmodifiedSurfaceRule = surfaceRule;
            this.citadelSurfaceRule = SurfaceRulesManager.mergeOverworldRules(surfaceRule);
            this.surfaceRule = citadelSurfaceRule;
            this.hasModifiedRules = true;
        }
    }

    @Override
    public void onSaveData(boolean saving) {
        this.saving = saving;
        if(this.hasModifiedRules){
            if(saving){
                this.swapSurfaceRule = this.surfaceRule;
                this.surfaceRule = this.unmodifiedSurfaceRule;
                Citadel.LOGGER.info("saving unmodified surface rules as {}", surfaceRule.getClass().getSimpleName());
            }else{
                this.surfaceRule = this.swapSurfaceRule == null ? this.citadelSurfaceRule : this.swapSurfaceRule;
                Citadel.LOGGER.info("using modified surface rules as {}", surfaceRule.getClass().getSimpleName());
            }
        }
    }
}
