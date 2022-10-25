package com.github.alexthe666.citadel.server.world;

public interface ModifiableTickRateServer {

    void setGlobalTickLengthMs(long msPerTick);

    long getMasterMs();

    default void resetGlobalTickLengthMs(){
        setGlobalTickLengthMs(-1);
    }
}
