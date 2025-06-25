package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.Citadel;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.ArrayList;
import java.util.List;

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
        return mergeRules(stripPreviouslyAddedRules(rulesIn), OVERWORLD_REGISTRY);
    }

    private static SurfaceRules.RuleSource mergeRules(SurfaceRules.RuleSource prev, List<SurfaceRules.RuleSource> toMerge) {
        if (toMerge.isEmpty()) {
            return prev;
        } else {
            ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
            toMerge.forEach(newRule -> builder.add(new CitadelSurfaceRuleWrapper(newRule)));
            if (prev instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) { // usual case
                Citadel.LOGGER.info("added {} new surface rule(s) to sequenced rule source", toMerge.size());
                builder.addAll(sequenceRuleSource.sequence());
            } else {
                Citadel.LOGGER.info("added {} new surface rule(s) to new rule source", toMerge.size());
                builder.add(prev);
            }
            SurfaceRules.RuleSource ruleSource = SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
            Citadel.LOGGER.info("surface rule recursive depth: {}", calculateSurfaceRuleDepth(ruleSource, 0));
            return ruleSource;
        }
    }

    private static SurfaceRules.RuleSource stripPreviouslyAddedRules(SurfaceRules.RuleSource rulesIn) {
        if (rulesIn instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
            ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
            // rebuild surface rules with all Citadel modifications removed.
            sequenceRuleSource.sequence().stream().filter(ruleSource -> !(ruleSource instanceof CitadelSurfaceRuleWrapper)).forEach(builder::add);
            ImmutableList<SurfaceRules.RuleSource> list = builder.build();
            int j = sequenceRuleSource.sequence().size() - list.size();
            Citadel.LOGGER.info("stripped {} rule(s) from surface rules", j);
            if (j > 0) {
                return SurfaceRules.sequence(list.toArray(SurfaceRules.RuleSource[]::new));
            }
        }
        return rulesIn;
    }

    private static int calculateSurfaceRuleDepth(SurfaceRules.RuleSource source, int depthIn){
        if(source instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource){
            int j = depthIn;
            for(SurfaceRules.RuleSource ruleSource : sequenceRuleSource.sequence()){
                j = Math.max(calculateSurfaceRuleDepth(ruleSource, depthIn + 1), j);
            }
            return j;
        }else if(source instanceof SurfaceRules.TestRuleSource testRuleSource){
            depthIn = Math.max(calculateSurfaceRuleDepth(testRuleSource.thenRun(), depthIn + 1), depthIn);
        }else if(source instanceof CitadelSurfaceRuleWrapper citadelSurfaceRuleWrapper){
            depthIn = Math.max(calculateSurfaceRuleDepth(citadelSurfaceRuleWrapper.wrappedRule(), depthIn + 1), depthIn);
        }
        return depthIn;
    }
}