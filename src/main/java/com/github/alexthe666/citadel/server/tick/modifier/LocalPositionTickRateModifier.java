package com.github.alexthe666.citadel.server.tick.modifier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LocalPositionTickRateModifier extends LocalTickRateModifier {

    private Vec3 center;

    public LocalPositionTickRateModifier(Vec3 center, double range, ResourceKey<Level> dimension, int durationInMasterTicks, float tickRateMultiplier) {
        super(TickRateModifierType.LOCAL_POSITION, range, dimension, durationInMasterTicks, tickRateMultiplier);
        this.center = center;
    }

    public LocalPositionTickRateModifier(CompoundTag tag) {
        super(tag);
        this.center = new Vec3(tag.getDouble("CenterX"), tag.getDouble("CenterY"), tag.getDouble("CenterZ"));
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();
        tag.putDouble("CenterX", center.x);
        tag.putDouble("CenterY", center.y);
        tag.putDouble("CenterZ", center.z);
        return tag;
    }

    public Vec3 getCenter() {
        return center;
    }

    public Vec3 getCenter(Level level) {
        return getCenter();
    }

    public void setCenter(Vec3 center) {
        this.center = center;
    }


}
