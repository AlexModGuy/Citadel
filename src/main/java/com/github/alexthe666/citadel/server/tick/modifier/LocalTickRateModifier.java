package com.github.alexthe666.citadel.server.tick.modifier;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public abstract class LocalTickRateModifier extends TickRateModifier {

    private double range;
    private ResourceKey<Level> dimension;

    public LocalTickRateModifier(TickRateModifierType localPosition, double range, ResourceKey<Level> dimension, int durationInMasterTicks, float tickRateMultiplier) {
        super(localPosition, durationInMasterTicks, tickRateMultiplier);
        this.range = range;
        this.dimension = dimension;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();
        tag.putDouble("Range", range);
        tag.putString("Dimension", dimension.location().toString());
        return tag;
    }

    public LocalTickRateModifier(CompoundTag tag) {
        super(tag);
        this.range = tag.getDouble("Range");
        ResourceKey<Level> dimFromTag = Level.OVERWORLD;
        if(tag.contains("Dimension")){
            dimFromTag = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString("dimension")));
        }
        this.dimension = dimFromTag;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public abstract Vec3 getCenter(Level level);

    @Override
    public boolean appliesTo(Level level, double x, double y, double z) {
        Vec3 center = getCenter(level);
        return center.distanceToSqr(x, y, z) < range * range;
    }
}
