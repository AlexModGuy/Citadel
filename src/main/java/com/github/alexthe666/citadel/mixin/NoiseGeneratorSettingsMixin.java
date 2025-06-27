package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.compat.ModCompatBridge;
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
    private boolean requiresSurfaceRuleSwapping = false;
    @Unique
    private SurfaceRules.RuleSource swapSurfaceRule = null;


   @Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true)
    private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (!hasModifiedRules && !saving && !ModCompatBridge.usingTerrablender()) { // initialized
            this.unmodifiedSurfaceRule = surfaceRule;
            if(SurfaceRulesManager.hasOverworldModifications()){
                this.requiresSurfaceRuleSwapping = true;
                this.citadelSurfaceRule = SurfaceRulesManager.mergeOverworldRules(surfaceRule);
                this.surfaceRule = citadelSurfaceRule;
            }else{
                Citadel.LOGGER.info("vanilla surface rule behavior unchanged");
                this.requiresSurfaceRuleSwapping = false;
            }
            this.hasModifiedRules = true;
        }
        if(this.hasModifiedRules && this.requiresSurfaceRuleSwapping){
            cir.setReturnValue(this.surfaceRule);
        }
    }

    @Override
    public void onSaveData(boolean saving) {
        this.saving = saving;
        if(!ModCompatBridge.usingTerrablender() && this.requiresSurfaceRuleSwapping){
            if(this.hasModifiedRules){
                if(saving){
                    this.swapSurfaceRule = this.surfaceRule;
                    this.surfaceRule = this.unmodifiedSurfaceRule;
                    Citadel.LOGGER.debug("saving unmodified surface rules as type {}", surfaceRule.getClass().getSimpleName());
                }else{
                    this.surfaceRule = this.swapSurfaceRule == null ? this.citadelSurfaceRule : this.swapSurfaceRule;
                    Citadel.LOGGER.debug("modified surface rules to type {}", surfaceRule.getClass().getSimpleName());
                }
            }
        }
    }
}
