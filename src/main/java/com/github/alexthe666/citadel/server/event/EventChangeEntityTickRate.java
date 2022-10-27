package com.github.alexthe666.citadel.server.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class EventChangeEntityTickRate extends Event {
    private Entity entity;
    private float targetTickRate;

    public EventChangeEntityTickRate(Entity entity, float targetTickRate) {
        this.entity = entity;
        this.targetTickRate = targetTickRate;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getTargetTickRate() {
        return targetTickRate;
    }
}
