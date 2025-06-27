package com.github.alexthe666.citadel.compat.terrablender;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Map;

public class TerrablenderCompat {

    public static void setup(){
        Map<String, SurfaceRules.RuleSource> vanillaBiomeRules = SurfaceRulesManager.getOverworldRulesByBiomeForTerrablender(true);
        for(Map.Entry<String, SurfaceRules.RuleSource> entry : vanillaBiomeRules.entrySet()){
            terrablender.api.SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(terrablender.api.SurfaceRuleManager.RuleCategory.OVERWORLD, terrablender.api.SurfaceRuleManager.RuleStage.BEFORE_BEDROCK, 0, entry.getValue());
        }
        Citadel.LOGGER.info("Added {} vanilla biome surface rule types via terrablender", vanillaBiomeRules.size());
        Map<String, SurfaceRules.RuleSource> moddedBiomeRules = SurfaceRulesManager.getOverworldRulesByBiomeForTerrablender(false);
        for(Map.Entry<String, SurfaceRules.RuleSource> entry : moddedBiomeRules.entrySet()){
            terrablender.api.SurfaceRuleManager.addSurfaceRules(terrablender.api.SurfaceRuleManager.RuleCategory.OVERWORLD, entry.getKey(), entry.getValue());
        }
        Citadel.LOGGER.info("Added {} modded biome surface rule types via terrablender", moddedBiomeRules.size());
    }
}
