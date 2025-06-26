package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.Citadel;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.levelgen.SurfaceRules;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurfaceRulesManager {
    private static final List<SurfaceRules.RuleSource> OVERWORLD_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> NETHER_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> END_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> CAVE_REGISTRY = new ArrayList();

    public SurfaceRulesManager() {
    }

    public static void registerOverworldSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerOverworldSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    public static void registerOverworldSurfaceRule(SurfaceRules.RuleSource rule) {
        OVERWORLD_REGISTRY.add(rule);
    }

    @Deprecated
    public static void registerNetherSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerNetherSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    @Deprecated
    public static void registerNetherSurfaceRule(SurfaceRules.RuleSource rule) {
        NETHER_REGISTRY.add(rule);
    }

    @Deprecated
    public static void registerEndSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerEndSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    @Deprecated
    public static void registerEndSurfaceRule(SurfaceRules.RuleSource rule) {
        END_REGISTRY.add(rule);
    }

    @Deprecated
    public static void registerCaveSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerCaveSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    @Deprecated
    public static void registerCaveSurfaceRule(SurfaceRules.RuleSource rule) {
        CAVE_REGISTRY.add(rule);
    }

    public static SurfaceRules.RuleSource mergeOverworldRules(SurfaceRules.RuleSource rulesIn) {
        SurfaceRules.RuleSource ruleSource = getOverworldRules();
        return ruleSource == null ? rulesIn : mergeRules(rulesIn, ruleSource);
    }

    @Nullable
    private static SurfaceRules.RuleSource getOverworldRules() {
        return OVERWORLD_REGISTRY.isEmpty() ? null : SurfaceRules.sequence(OVERWORLD_REGISTRY.toArray(SurfaceRules.RuleSource[]::new));
    }

    /*
        Needed for terrablender compatibility
     */
    public static Map<String, SurfaceRules.RuleSource> getOverworldRulesByBiomeForTerrablender(boolean vanilla) {
        Map<String, SurfaceRules.RuleSource> map = new HashMap<>();
        for (SurfaceRules.RuleSource ruleSource : OVERWORLD_REGISTRY) {
            if (ruleSource instanceof SurfaceRules.TestRuleSource testRuleSource && testRuleSource.ifTrue() instanceof SurfaceRules.BiomeConditionSource biomeRule && !biomeRule.biomes.isEmpty()) {
                String namespace = biomeRule.biomes.get(0).location().getNamespace();
                boolean vanillaBiome = namespace.equals("minecraft");

                if (vanilla && vanillaBiome) {
                    map.put(namespace, testRuleSource);
                }
                if (!vanilla && !vanillaBiome) {
                    if (map.containsKey(namespace)) {
                        SurfaceRules.RuleSource ruleSource1 = map.get(namespace);
                        if (ruleSource1 instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
                            ImmutableList.Builder<SurfaceRules.RuleSource> ruleSources = ImmutableList.builder();
                            ruleSources.addAll(sequenceRuleSource.sequence());
                            ruleSources.add(testRuleSource);
                            map.put(namespace, SurfaceRules.sequence(ruleSources.build().toArray(SurfaceRules.RuleSource[]::new)));
                        } else {
                            map.put(namespace, SurfaceRules.sequence(ruleSource1, testRuleSource));
                        }
                    } else {
                        map.put(namespace, testRuleSource);
                    }
                }
            }
        }
        return map;
    }


    private static SurfaceRules.RuleSource mergeRules(SurfaceRules.RuleSource prev, SurfaceRules.RuleSource toMerge) {
        CitadelSurfaceRuleWrapper result;
        if (prev instanceof CitadelSurfaceRuleWrapper wrapper) {
            result = new CitadelSurfaceRuleWrapper(wrapper.vanillaRules(), toMerge);
        } else {
            result = new CitadelSurfaceRuleWrapper(prev, toMerge);
        }
        Citadel.LOGGER.debug("surface rule recursive depth: {}", calculateSurfaceRuleDepth(result, 1));
        return result;
    }

    private static int calculateSurfaceRuleDepth(SurfaceRules.RuleSource source, int depthIn) {
        if (source instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
            int j = depthIn;
            for (SurfaceRules.RuleSource ruleSource : sequenceRuleSource.sequence()) {
                j = Math.max(calculateSurfaceRuleDepth(ruleSource, depthIn + 1), j);
            }
            return j;
        } else if (source instanceof SurfaceRules.TestRuleSource testRuleSource) {
            depthIn = Math.max(calculateSurfaceRuleDepth(testRuleSource.thenRun(), depthIn + 1), depthIn);
        } else if (source instanceof CitadelSurfaceRuleWrapper citadelSurfaceRuleWrapper) {
            depthIn = Math.max(calculateSurfaceRuleDepth(citadelSurfaceRuleWrapper.vanillaRules(), depthIn + 1), depthIn);
        }
        return depthIn;
    }
}