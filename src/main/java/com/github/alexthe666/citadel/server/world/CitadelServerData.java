package com.github.alexthe666.citadel.server.world;

import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;

public class CitadelServerData extends SavedData {
    private static Map<MinecraftServer, CitadelServerData> dataMap = new HashMap<>();

    private static final String IDENTIFIER = "citadel_world_data";

    private MinecraftServer server;

    private ServerTickRateTracker tickRateTracker = null;

    public CitadelServerData(MinecraftServer server) {
        super();
        this.server = server;
    }


    public static CitadelServerData get(MinecraftServer server) {
        CitadelServerData fromMap = dataMap.get(server);
        if(fromMap == null){
            DimensionDataStorage storage = server.getLevel(Level.OVERWORLD).getDataStorage();
            CitadelServerData data = storage.computeIfAbsent((tag) -> load(server, tag), () -> new CitadelServerData(server), IDENTIFIER);
            if (data != null) {
                data.setDirty();
            }
            dataMap.put(server, data);
            return data;
        }
        return fromMap;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        if(tickRateTracker != null){
            tag.put("TickRateTracker", tickRateTracker.toTag());
        }
        return tag;
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

}
