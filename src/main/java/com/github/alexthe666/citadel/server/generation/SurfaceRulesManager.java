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
                Citadel.LOGGER.info("added {} new surface rules to sequenced rule source", toMerge.size());
                builder.addAll(sequenceRuleSource.sequence());
            } else {
                Citadel.LOGGER.info("added {} new surface rules to new rule source", toMerge.size());
                builder.add(prev);
            }
            return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
        }
    }

    private static SurfaceRules.RuleSource stripPreviouslyAddedRules(SurfaceRules.RuleSource rulesIn) {
        if (rulesIn instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
            ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
            // rebuild surface rules with all Citadel modifications removed.
            sequenceRuleSource.sequence().stream().filter(ruleSource -> !(ruleSource instanceof CitadelSurfaceRuleWrapper)).forEach(builder::add);
            ImmutableList<SurfaceRules.RuleSource> list = builder.build();
            int j = sequenceRuleSource.sequence().size() - list.size();
            Citadel.LOGGER.info("stripped {} rules from surface rules", j);
            return SurfaceRules.sequence(list.toArray(SurfaceRules.RuleSource[]::new));
        }
        return rulesIn;
    }
}