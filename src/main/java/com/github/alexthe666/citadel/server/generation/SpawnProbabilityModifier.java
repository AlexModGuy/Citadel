package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.config.ServerConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SpawnProbabilityModifier implements BiomeModifier {

    private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(new ResourceLocation("citadel:mob_spawn_probability"), ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, "citadel");

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        float probability = (float) (ServerConfig.chunkGenSpawnModifierVal) * builder.getMobSpawnSettings().getProbability();
        if (phase == Phase.MODIFY) {
            builder.getMobSpawnSettings().creatureGenerationProbability(probability);
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    public static Codec<SpawnProbabilityModifier> makeCodec(){
        return Codec.unit(SpawnProbabilityModifier::new);
    }
}
