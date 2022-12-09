package com.github.alexthe666.citadel.server.world;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.*;

public class ExpandedBiomes {
    private static Map<ResourceKey<LevelStem>, List<ResourceKey<Biome>>> biomes = new HashMap<>();

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

    public static Set<Holder<Biome>> buildBiomeList(RegistryAccess registryAccess, ResourceKey<LevelStem> dimension){
        List<ResourceKey<Biome>> list = biomes.get(dimension);
        if(list == null || list.isEmpty()){
            return Set.of();
        }
        Registry<Biome> allBiomes = registryAccess.registryOrThrow(Registries.BIOME);
        ImmutableSet.Builder<Holder<Biome>> biomeHolders = ImmutableSet.builder();
        for(ResourceKey<Biome> biomeResourceKey : list){
            Optional<Holder.Reference<Biome>> holderOptional = allBiomes.getHolder(biomeResourceKey);
            holderOptional.ifPresent(biomeHolders::add);
        }
        return biomeHolders.build();
    }
}
