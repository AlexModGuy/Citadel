package com.github.alexthe666.citadel.server.event;

import com.github.alexthe666.citadel.server.world.ExpandedBiomeSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
public class EventReplaceBiome extends Event {

    public Holder<Biome> biomeToGenerate;
    public ExpandedBiomeSource biomeSource;
    public float continentalness;
    public float erosion;
    public float temperature;
    public float humidity;
    public float weirdness;
    public float depth;

    private int x;
    private int y;
    private int z;
    public EventReplaceBiome(ExpandedBiomeSource biomeSource, Holder<Biome> biomeIn, int x, int y, int z, float continentalness, float erosion, float temperature, float humidity, float weirdness, float depth) {
        this.biomeSource = biomeSource;
        this.biomeToGenerate = biomeIn;
        this.continentalness = continentalness;
        this.erosion = erosion;
        this.temperature = temperature;
        this.humidity = humidity;
        this.weirdness = weirdness;
        this.depth = depth;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Holder<Biome> getBiomeToGenerate() {
        return biomeToGenerate;
    }

    public float getContinentalness() {
        return continentalness;
    }

    public float getErosion() {
        return erosion;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getWeirdness() {
        return weirdness;
    }

    public float getDepth() {
        return depth;
    }

    public ExpandedBiomeSource getBiomeSource(){
        return biomeSource;
    }

    public void setBiomeToGenerate(Holder<Biome> biome){
        biomeToGenerate = biome;
    }

    public BlockPos getSamplePosition(){
        return new BlockPos(x, y, z);
    }
}
