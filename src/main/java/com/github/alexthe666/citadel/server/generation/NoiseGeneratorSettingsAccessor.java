package com.github.alexthe666.citadel.server.generation;

import net.minecraft.world.level.levelgen.SurfaceRules;

public interface NoiseGeneratorSettingsAccessor {

    void afterLoadOtherSurfaceRules(SurfaceRules.RuleSource initialRules);

    void onSaveData(boolean saving);

    SurfaceRules.RuleSource getRuleSource();
}
