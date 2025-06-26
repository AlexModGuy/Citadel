package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NoiseGeneratorSettings.class, priority = 500)
public class NoiseGeneratorSettingsMixin {

    @Mutable
    @Final
    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    @Unique
    private SurfaceRules.RuleSource unmodifiedSurfaceRule;
    @Unique
    private SurfaceRules.RuleSource citadelSurfaceRule = null;


    @Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true)
    private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (citadelSurfaceRule == null) { // initialized
            this.unmodifiedSurfaceRule = surfaceRule;
            this.citadelSurfaceRule = SurfaceRulesManager.mergeOverworldRules(surfaceRule);
        }
        if(SurfaceRulesManager.isLevelBeingSaved()){
            Citadel.LOGGER.info("saving unmodified surface rules...");
            surfaceRule = unmodifiedSurfaceRule;
        }else if(this.surfaceRule != citadelSurfaceRule){
            Citadel.LOGGER.info("restored modified surface rules");
            surfaceRule = citadelSurfaceRule;
        }
        cir.setReturnValue(surfaceRule);
    }
}
