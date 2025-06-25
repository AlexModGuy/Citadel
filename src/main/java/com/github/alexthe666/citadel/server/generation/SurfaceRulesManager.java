package com.github.alexthe666.citadel.server.generation;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;

import java.util.ArrayList;
import java.util.List;

public class SurfaceRulesManager {
    private static final List<SurfaceRules.RuleSource> OVERWORLD_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> NETHER_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> END_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> CAVE_REGISTRY = new ArrayList();
    private static long overworldRuleAdditionSeed = 0;

    public SurfaceRulesManager() {
    }

    public static void registerOverworldSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerOverworldSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    public static void registerOverworldSurfaceRule(SurfaceRules.RuleSource rule) {
        OVERWORLD_REGISTRY.add(rule);
        overworldRuleAdditionSeed = calculateOverworldSeed();
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

    public static SurfaceRules.RuleSource mergeRules(SurfaceRules.RuleSource prev, List<SurfaceRules.RuleSource> toMerge) {
        ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
        builder.addAll(toMerge);
        builder.add(prev);
        return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
    }

    public static SurfaceRules.RuleSource mergeOverworldRules(SurfaceRules.RuleSource rulesIn) {
        return mergeRules(rulesIn, OVERWORLD_REGISTRY);
    }

    private static long calculateOverworldSeed() {
        // merge all overworld rules into one surface rule
        SurfaceRules.RuleSource overworldRules = SurfaceRules.sequence(OVERWORLD_REGISTRY.toArray(SurfaceRules.RuleSource[]::new));
        // use the hash of the overworld seed to seed a random long
        return new Xoroshiro128PlusPlus(RandomSupport.seedFromHashOf(overworldRules.toString())).nextLong();
    }

    /**
       Used to avoid recursively adding the surface rules if they are identical
     */
    public static long getOverworldRuleAdditionSeed() {
        return overworldRuleAdditionSeed;
    }
}