package com.github.alexthe666.citadel.server.world;

import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import javax.annotation.Nonnull;

public class CitadelServerData extends SavedData {

    private static final String IDENTIFIER = "citadel_world_data";

    private MinecraftServer server;

    private ServerTickRateTracker tickRateTracker = null;

    public CitadelServerData(MinecraftServer server) {
        super();
        this.server = server;
    }


    public static SavedData.Factory<CitadelServerData> factory(MinecraftServer level) {
        return new SavedData.Factory<>(() -> new CitadelServerData(level), (tag, provider) -> load(level, tag), null);
    }

    @Nonnull
    public static CitadelServerData get(MinecraftServer server) {
        DimensionDataStorage storage = server.getLevel(Level.OVERWORLD).getDataStorage();
        CitadelServerData data = storage.computeIfAbsent(factory(server), IDENTIFIER);
        data.setDirty();
        return data;
    }

    public static CitadelServerData load(MinecraftServer server, CompoundTag tag) {
        CitadelServerData data = new CitadelServerData(server);
        if(tag.contains("TickRateTracker")){
            data.tickRateTracker = new ServerTickRateTracker(server, tag.getCompound("TickRateTracker"));
        }else{
            data.tickRateTracker = new ServerTickRateTracker(server);
        }
        return data;
    }

    public ServerTickRateTracker getOrCreateTickRateTracker(){
        if(tickRateTracker == null){
            tickRateTracker = new ServerTickRateTracker(server);
        }
        return tickRateTracker;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        if(tickRateTracker != null){
            tag.put("TickRateTracker", tickRateTracker.toTag());
        }
        return tag;
    }
}
