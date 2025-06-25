package com.github.alexthe666.citadel.server.world;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class CitadelServer {

    private static MinecraftServer lastServer;

    @Nullable
    public static MinecraftServer getLastServer() {
        return lastServer;
    }

    public static void setLastServer(MinecraftServer lastServer) {
        CitadelServer.lastServer = lastServer;
    }


}
