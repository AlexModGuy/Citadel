package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.Citadel;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GenerationSettingsManager {

    public static Random random = new Random();
    private static final List<Pair<String, PlacedFeature>> REGISTRY = new ArrayList<>();

    public static void register(String biome, PlacedFeature feature) {
        REGISTRY.add(new Pair<>(biome, feature));
        Citadel.LOGGER.debug("registered feature " + feature.toString());
    }


    public static void onChunkPopulate(ChunkGenerator chunkGenerator, WorldGenLevel level, ChunkAccess access, StructureFeatureManager featureManager, BiomeSource biomeSource) {
        SectionPos sectionpos = SectionPos.of(access.getPos(), access.getMinSection());
        Set<Biome> set = new ObjectArraySet<>();
        ChunkPos.rangeClosed(sectionpos.chunk(), 1).forEach((p_196730_) -> {
            ChunkAccess chunkaccess = level.getChunk(p_196730_.x, p_196730_.z);
            for (LevelChunkSection levelchunksection : chunkaccess.getSections()) {
                levelchunksection.getBiomes().getAll((biomes) -> {
                    set.add(biomes.value());
                });
            }

        });
        //does not work for modded biomes
        //set.retainAll(biomeSource.possibleBiomes());
        for(Biome biome : set){
            for (Pair<String, PlacedFeature> pair : REGISTRY) {
                if (pair.getA().equals(biome.getRegistryName().toString())) {
                    BlockPos blockpos = sectionpos.origin();
                    pair.getB().placeWithBiomeCheck(level, chunkGenerator, random, blockpos);
                }
            }
        }

    }
}
