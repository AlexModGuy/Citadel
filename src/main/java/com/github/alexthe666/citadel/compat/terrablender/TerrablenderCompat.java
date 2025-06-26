package com.github.alexthe666.citadel.compat.terrablender;

import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class TerrablenderCompat {

    public static void setup(){
        SurfaceRules.RuleSource ruleSource = SurfaceRulesManager.getOverworldRules();
        if(ruleSource != null){
            terrablender.api.SurfaceRuleManager.addSurfaceRules(terrablender.api.SurfaceRuleManager.RuleCategory.OVERWORLD, "citadel", ruleSource);
        }
    }
}
