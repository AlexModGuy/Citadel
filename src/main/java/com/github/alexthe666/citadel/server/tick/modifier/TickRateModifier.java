package com.github.alexthe666.citadel.server.tick.modifier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public abstract class TickRateModifier {

    private TickRateModifierType type;
    private int durationInMasterTicks;
    private int passedMasterTicks;
    private float tickRateMultiplier;

    public TickRateModifier(TickRateModifierType type, int durationInMasterTicks, float tickRateMultiplier) {
        this.type = type;
        this.durationInMasterTicks = durationInMasterTicks;
        this.tickRateMultiplier = tickRateMultiplier;
    }

    public TickRateModifier(CompoundTag tag) {
        this.type = TickRateModifierType.values()[Mth.clamp(tag.getInt("Type"), 0, TickRateModifierType.values().length - 1)];
        this.durationInMasterTicks = tag.getInt("MasterTickDuration");
        this.tickRateMultiplier = tag.getInt("SpeedMultiplier");
        this.passedMasterTicks = tag.getInt("MasterTickPassed");
    }

    public TickRateModifierType getType() {
        return type;
    }

    public int getDurationInMasterTicks() {
        return durationInMasterTicks;
    }

    public float getTickRateMultiplier() {
        return tickRateMultiplier;
    }

    public void setDurationInMasterTicks(int durationInMasterTicks) {
        this.durationInMasterTicks = durationInMasterTicks;
    }

    public void setTickRateMultiplier(float tickRateMultiplier) {
        this.tickRateMultiplier = tickRateMultiplier;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Type", this.type.ordinal());
        tag.putInt("MasterTickDuration", durationInMasterTicks);
        tag.putInt("MasterTickPassed", passedMasterTicks);
        tag.putFloat("SpeedMultiplier", tickRateMultiplier);
        return tag;
    }

    public static TickRateModifier fromTag(CompoundTag tag) {
        TickRateModifierType typeFromNbt = TickRateModifierType.values()[Mth.clamp(tag.getInt("Type"), 0, TickRateModifierType.values().length - 1)];
        try {
            return typeFromNbt.getTickRateClass().getConstructor(CompoundTag.class).newInstance(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isGlobal(){
        return this.type.isGlobal();
    }

    public void tick(){
        passedMasterTicks++;
    }

    public boolean doRemove(){
        return passedMasterTicks >= durationInMasterTicks;
    }

}
