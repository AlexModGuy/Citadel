package com.github.alexthe666.citadel.server.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class SurfaceRulesManager {
    private static final List<SurfaceRules.RuleSource> OVERWORLD_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> NETHER_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> END_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> CAVE_REGISTRY = new ArrayList();
    private static SurfaceRules.RuleSource overworldRules;
    private static SurfaceRules.RuleSource netherRules;
    private static SurfaceRules.RuleSource endRules;
    private static SurfaceRules.RuleSource caveRules;
    private static boolean mergedOverworld;
    private static boolean mergedNether;
    private static boolean mergedEnd;
    private static boolean mergedCaves;

    public SurfaceRulesManager() {
    }

    public static void registerOverworldSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerOverworldSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    public static void registerOverworldSurfaceRule(SurfaceRules.RuleSource rule) {
        OVERWORLD_REGISTRY.add(rule);
    }

    public static void registerNetherSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerNetherSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    public static void registerNetherSurfaceRule(SurfaceRules.RuleSource rule) {
        NETHER_REGISTRY.add(rule);
    }

    public static void registerEndSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerEndSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    public static void registerEndSurfaceRule(SurfaceRules.RuleSource rule) {
        END_REGISTRY.add(rule);
    }

    public static void registerCaveSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerCaveSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    public static void registerCaveSurfaceRule(SurfaceRules.RuleSource rule) {
        CAVE_REGISTRY.add(rule);
    }


    private static boolean stable(NoiseGeneratorSettings settings, ResourceKey<NoiseGeneratorSettings> key) {
        return Objects.equals(settings, BuiltinRegistries.NOISE_GENERATOR_SETTINGS.get(key));
    }
    
    public static SurfaceRules.RuleSource process(NoiseGeneratorSettings settings, SurfaceRules.RuleSource prev) {
        SurfaceRules.RuleSource[] rules;
        if (stable(settings, NoiseGeneratorSettings.OVERWORLD) || stable(settings, NoiseGeneratorSettings.LARGE_BIOMES) || stable(settings, NoiseGeneratorSettings.AMPLIFIED) || stable(settings, NoiseGeneratorSettings.FLOATING_ISLANDS)) {
            if (!mergedOverworld) {
                if (!OVERWORLD_REGISTRY.isEmpty()) {
                    rules = (SurfaceRules.RuleSource[])OVERWORLD_REGISTRY.toArray(new SurfaceRules.RuleSource[0]);
                    overworldRules = SurfaceRules.sequence(new SurfaceRules.RuleSource[]{SurfaceRules.sequence(rules), prev});
                }

                mergedOverworld = true;
            }

            if (overworldRules != null) {
                return overworldRules;
            }
        }

        if (stable(settings, NoiseGeneratorSettings.NETHER)) {
            if (!mergedNether) {
                if (!NETHER_REGISTRY.isEmpty()) {
                    rules = (SurfaceRules.RuleSource[])NETHER_REGISTRY.toArray(new SurfaceRules.RuleSource[0]);
                    netherRules = SurfaceRules.sequence(new SurfaceRules.RuleSource[]{SurfaceRules.sequence(rules), prev});
                }

                mergedNether = true;
            }

            if (netherRules != null) {
                return netherRules;
            }
        }

        if (stable(settings, NoiseGeneratorSettings.END)) {
            if (!mergedEnd) {
                if (!END_REGISTRY.isEmpty()) {
                    rules = (SurfaceRules.RuleSource[])END_REGISTRY.toArray(new SurfaceRules.RuleSource[0]);
                    endRules = SurfaceRules.sequence(new SurfaceRules.RuleSource[]{SurfaceRules.sequence(rules), prev});
                }

                mergedEnd = true;
            }

            if (endRules != null) {
                return endRules;
            }
        }

        if (stable(settings, NoiseGeneratorSettings.CAVES)) {
            if (!mergedCaves) {
                if (!CAVE_REGISTRY.isEmpty()) {
                    rules = (SurfaceRules.RuleSource[])CAVE_REGISTRY.toArray(new SurfaceRules.RuleSource[0]);
                    caveRules = SurfaceRules.sequence(new SurfaceRules.RuleSource[]{SurfaceRules.sequence(rules), prev});
                }

                mergedCaves = true;
            }

            if (caveRules != null) {
                return caveRules;
            }
        }

        return prev;
    }
}
