package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import com.github.alexthe666.citadel.server.world.CitadelServer;
import com.github.alexthe666.citadel.server.world.CitadelServerData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin {

    @Mutable
    @Final
    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    @Inject(method = "surfaceRule", at = @At("HEAD"))
    private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        MinecraftServer server = CitadelServer.getLastServer();
        CitadelServerData citadelServerData = server == null ? null : CitadelServerData.get(server);

        if (citadelServerData != null && !citadelServerData.isUsingLatestSurfaceRules()) {
            this.surfaceRule = SurfaceRulesManager.mergeOverworldRules(surfaceRule);
            citadelServerData.onModifySurfaceRules();
            Citadel.LOGGER.info("Merged surface rules with surface rules addition seed {}", SurfaceRulesManager.getOverworldRuleAdditionSeed());
            //not replacing the return result for compatibility with TerraBlender
        }
    }
}
