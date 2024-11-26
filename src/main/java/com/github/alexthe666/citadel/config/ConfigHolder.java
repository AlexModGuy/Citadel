package com.github.alexthe666.citadel.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {

    public static final ModConfigSpec SERVER_SPEC;
    public static final ServerConfig SERVER;

    static {
        {
            final Pair<ServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
            SERVER = specPair.getLeft();
            SERVER_SPEC = specPair.getRight();
        }
    }
}