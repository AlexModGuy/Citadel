package com.github.alexthe666.citadel.server.tick.modifier;

public enum TickRateModifierType {
    GLOBAL(GlobalTickRateModifier.class, false, 0),
    CELESTIAL(CelestialTickRateModifier.class, false, 1),
    LOCAL_POSITION(LocalPositionTickRateModifier.class, true, 2),
    LOCAL_ENTITY(LocalEntityTickRateModifier.class, true, 3);

    private Class<? extends TickRateModifier> clazz;
    private boolean local;
    private int id;

    TickRateModifierType(Class<? extends TickRateModifier> clazz, boolean local, int id) {
        this.clazz = clazz;
        this.local = local;
        this.id = id;
    }

    public Class<? extends TickRateModifier> getTickRateClass(){
        return this.clazz;
    }

    public boolean isLocal() {
        return local;
    }

    public int toId(){
        return id;
    }

    public static TickRateModifierType fromId(int id){
        for(TickRateModifierType type : TickRateModifierType.values()){
            if(type.id == id){
                return type;
            }
        }
        return TickRateModifierType.CELESTIAL;
    }
}
