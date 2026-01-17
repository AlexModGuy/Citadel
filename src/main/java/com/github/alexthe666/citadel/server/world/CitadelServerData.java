package com.github.alexthe666.citadel.server.world;

import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CitadelServerData extends SavedData {

    private static final String IDENTIFIER = "citadel_world_data";

    private final MinecraftServer server;

    private ServerTickRateTracker tickRateTracker = null;

    public CitadelServerData(MinecraftServer server) {
        super();
        this.server = server;
    }

    public CitadelServerData(MinecraftServer server, CompoundTag tag) {
        this(server);
        if (tag.contains("TickRateTracker")) {
            tickRateTracker = new ServerTickRateTracker(server, tag.getCompound("TickRateTracker"));
        } else {
            tickRateTracker = new ServerTickRateTracker(server);
        }
    }

    @NotNull
    public static CitadelServerData get(MinecraftServer server) {
        DimensionDataStorage storage = server.getLevel(Level.OVERWORLD).getDataStorage();
        CitadelServerData data = storage.computeIfAbsent((tag) -> new CitadelServerData(server, tag), () -> new CitadelServerData(server), IDENTIFIER);
        data.setDirty();
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        if (tickRateTracker != null) {
            tag.put("TickRateTracker", tickRateTracker.toTag());
        }
        return tag;
    }


    public ServerTickRateTracker getOrCreateTickRateTracker() {
        if (tickRateTracker == null) {
            tickRateTracker = new ServerTickRateTracker(server);
        }
        return tickRateTracker;
    }
}
