package com.github.alexthe666.citadel.server.world;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.Set;

public interface ExpandedBiomeSource {

    void setResourceKeyMap(Map<ResourceKey<Biome>, Holder<Biome>> map);
    Map<ResourceKey<Biome>, Holder<Biome>> getResourceKeyMap();
    void expandBiomesWith(Set<Holder<Biome>> newGenBiomes);
}
