package com.github.alexthe666.citadel.server.tick.modifier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class CelestialTickRateModifier extends TickRateModifier {

    public CelestialTickRateModifier(int durationInMasterTicks, float tickRateMultiplier) {
        super(TickRateModifierType.CELESTIAL, durationInMasterTicks, tickRateMultiplier);
    }

    public CelestialTickRateModifier(CompoundTag tag) {
        super(tag);
    }

    @Override
    public boolean appliesTo(Level level, double x, double y, double z) {
        return false;
    }
}
