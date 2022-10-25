package com.github.alexthe666.citadel.server.tick.modifier;

public enum TickRateModifierType {
    GLOBAL(GlobalTickRateModifier.class, true),
    CELESTIAL(CelestialTickRateModifier.class, true),
    LOCAL_POSITION(LocalPositionTickRateModifier.class, false),
    LOCAL_ENTITY(LocalEntityTickRateModifier.class, false);

    private Class<? extends TickRateModifier> clazz;
    private boolean global;

    TickRateModifierType(Class<? extends TickRateModifier> clazz, boolean global) {
        this.clazz = clazz;
        this.global = global;
    }

    public Class<? extends TickRateModifier> getTickRateClass(){
        return this.clazz;
    }

    public boolean isGlobal() {
        return global;
    }
}
