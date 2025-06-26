package com.github.alexthe666.citadel.compat.terrablender;

import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class TerrablenderCompat {

    public static void setup(){
        SurfaceRules.RuleSource ruleSource = SurfaceRulesManager.getOverworldRules();
        if(ruleSource != null){
            //Must use addToDefaultSurfaceRulesAtStage instead of addSurfaceRules since addSurfaceRules does not take into modifying all possible biomes
            terrablender.api.SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(terrablender.api.SurfaceRuleManager.RuleCategory.OVERWORLD, terrablender.api.SurfaceRuleManager.RuleStage.AFTER_BEDROCK, 1, ruleSource);
        }
    }
}
