package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.config.ServerConfig;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public class SpawnProbabilityModifier implements BiomeModifier {

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        float probability = (float) (ServerConfig.chunkGenSpawnModifierVal) * builder.getMobSpawnSettings().getProbability();
        if (phase == Phase.MODIFY) {
            builder.getMobSpawnSettings().creatureGenerationProbability(Mth.clamp(probability, 0F, 1F));
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return makeCodec();
    }

    public static MapCodec<SpawnProbabilityModifier> makeCodec() {
        return MapCodec.unit(SpawnProbabilityModifier::new);
    }
}
