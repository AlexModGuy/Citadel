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

@Mixin(value = NoiseGeneratorSettings.class, priority = 1500)
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

    @Override
    public void afterLoadOtherSurfaceRules(SurfaceRules.RuleSource initialRules) {
        if (!hasModifiedRules && !saving) { // initialized
            this.unmodifiedSurfaceRule = initialRules;
            this.citadelSurfaceRule = SurfaceRulesManager.mergeOverworldRules(initialRules);
            this.surfaceRule = citadelSurfaceRule;
            this.hasModifiedRules = true;
            Citadel.LOGGER.info("transformed surface rules from type {} --> {}", initialRules.getClass().getSimpleName(), surfaceRule.getClass().getSimpleName());
        }
    }

    @Override
    public void onSaveData(boolean saving) {
        this.saving = saving;
        if(this.hasModifiedRules){
            if(saving){
                this.swapSurfaceRule = this.surfaceRule;
                this.surfaceRule = this.unmodifiedSurfaceRule;
                Citadel.LOGGER.info("saving unmodified surface rules as type {}", surfaceRule.getClass().getSimpleName());
            }else{
                this.surfaceRule = this.swapSurfaceRule == null ? this.citadelSurfaceRule : this.swapSurfaceRule;
                Citadel.LOGGER.info("modified surface rules to type {}", surfaceRule.getClass().getSimpleName());
            }
        }
    }

    @Override
    public SurfaceRules.RuleSource getRuleSource() {
        return this.surfaceRule;
    }
}
