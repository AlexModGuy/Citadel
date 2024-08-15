package com.github.alexthe666.citadel.server.world;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated(since = "2.6.0")
public class ExpandedBiomes {
    private static Map<ResourceKey<LevelStem>, List<ResourceKey<Biome>>> biomes = new HashMap<>();

    @Deprecated(since = "2.6.0")
    public static void addExpandedBiome(ResourceKey<Biome> biome, ResourceKey<LevelStem> dimension){
        List<ResourceKey<Biome>> list;
        if(!biomes.containsKey(dimension)){
            list = new ArrayList<>();
        }else{
            list = biomes.get(dimension);
        }
        if(!list.contains(biome)){
            list.add(biome);
        }
        biomes.put(dimension, list);
    }
}
