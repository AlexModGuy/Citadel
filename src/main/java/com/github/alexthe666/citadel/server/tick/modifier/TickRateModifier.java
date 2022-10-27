package com.github.alexthe666.citadel.server.tick.modifier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public abstract class TickRateModifier {

    private TickRateModifierType type;
    private float maxDuration;
    private float duration;
    private float tickRateMultiplier;

    public TickRateModifier(TickRateModifierType type, int maxDuration, float tickRateMultiplier) {
        this.type = type;
        this.maxDuration = maxDuration;
        this.tickRateMultiplier = tickRateMultiplier;
    }

    public TickRateModifier(CompoundTag tag) {
        this.type = TickRateModifierType.fromId(tag.getInt("TickRateType"));
        this.maxDuration = tag.getFloat("MaxDuration");
        this.duration = tag.getFloat("Duration");
        this.tickRateMultiplier = tag.getFloat("SpeedMultiplier");
    }

    public TickRateModifierType getType() {
        return type;
    }

    public float getMaxDuration() {
        return maxDuration;
    }

    public float getTickRateMultiplier() {
        return tickRateMultiplier;
    }

    public void setMaxDuration(float maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void setTickRateMultiplier(float tickRateMultiplier) {
        this.tickRateMultiplier = tickRateMultiplier;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("TickRateType", this.type.toId());
        tag.putFloat("MaxDuration", maxDuration);
        tag.putFloat("Duration", duration);
        tag.putFloat("SpeedMultiplier", tickRateMultiplier);
        return tag;
    }

    public static TickRateModifier fromTag(CompoundTag tag) {
        TickRateModifierType typeFromNbt = TickRateModifierType.fromId(tag.getInt("TickRateType"));
        try {
            return typeFromNbt.getTickRateClass().getConstructor(CompoundTag.class).newInstance(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isGlobal() {
        return this.type.isLocal();
    }

    public void masterTick() {
        duration++;
    }


    public boolean doRemove() {
        float f = tickRateMultiplier == 0 || this.getType() == TickRateModifierType.CELESTIAL ? 1.0F : 1F / tickRateMultiplier;
        return duration >= maxDuration * f;
    }

    public abstract boolean appliesTo(Level level, double x, double y, double z);
}
