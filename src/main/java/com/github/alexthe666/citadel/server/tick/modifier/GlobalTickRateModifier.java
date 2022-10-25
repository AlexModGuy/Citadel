package com.github.alexthe666.citadel.server.tick.modifier;

import net.minecraft.nbt.CompoundTag;

public class GlobalTickRateModifier extends TickRateModifier {

    public GlobalTickRateModifier(int durationInMasterTicks, float tickRateMultiplier) {
        super(TickRateModifierType.GLOBAL, durationInMasterTicks, tickRateMultiplier);
    }

    public GlobalTickRateModifier(CompoundTag tag) {
        super(tag);
    }

}
