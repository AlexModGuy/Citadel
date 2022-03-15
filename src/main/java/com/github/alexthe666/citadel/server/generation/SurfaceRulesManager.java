package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.Citadel;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
/*
    Register new Surface Rules for biomes and dimensions.
    Example use case:
    SurfaceRulesManager.registerOverworldSurfaceRule(SurfaceRules.isBiome(Biomes.PLAINS), SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState()));

    This would replace the surface of plains biomes with course dirt rather than grass blocks.
 */
public class SurfaceRulesManager {

    private static final List<SurfaceRules.RuleSource> OVERWORLD_REGISTRY = new ArrayList<>();
    private static final List<SurfaceRules.RuleSource> NETHER_REGISTRY = new ArrayList<>();
    private static final List<SurfaceRules.RuleSource> END_REGISTRY = new ArrayList<>();
    private static final List<SurfaceRules.RuleSource> CAVE_REGISTRY = new ArrayList<>();
    private static SurfaceRules.RuleSource overworldRules;
    private static SurfaceRules.RuleSource netherRules;
    private static SurfaceRules.RuleSource endRules;
    private static SurfaceRules.RuleSource caveRules;
    private static boolean mergedOverworld;
    private static boolean mergedNether;
    private static boolean mergedEnd;
    private static boolean mergedCaves;


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

    public static SurfaceRules.RuleSource process(NoiseGeneratorSettings settings, SurfaceRules.RuleSource prev) {
        return prev;
    }
}
