package com.github.alexthe666.citadel.server.entity;

import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifier;

public interface IModifiesTime {

    boolean isTimeModificationValid(TickRateModifier modifier);

}
