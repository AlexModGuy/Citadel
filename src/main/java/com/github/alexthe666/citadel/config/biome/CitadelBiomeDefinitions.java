package com.github.alexthe666.citadel.config.biome;

public class CitadelBiomeDefinitions {

    public static final SpawnBiomeData TEST = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:nether_wastes", 1);

    public static final SpawnBiomeData TERRALITH_TEST = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_badlands", 0);
}
