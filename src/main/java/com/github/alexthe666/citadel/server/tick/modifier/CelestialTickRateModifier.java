package com.github.alexthe666.citadel.server.tick.modifier;

import net.minecraft.nbt.CompoundTag;

public class CelestialTickRateModifier extends TickRateModifier {

    public CelestialTickRateModifier(int durationInMasterTicks, float tickRateMultiplier) {
        super(TickRateModifierType.CELESTIAL, durationInMasterTicks, tickRateMultiplier);
    }

    public CelestialTickRateModifier(CompoundTag tag) {
        super(tag);
    }

}
