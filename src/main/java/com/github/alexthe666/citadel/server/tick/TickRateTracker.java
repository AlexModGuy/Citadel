package com.github.alexthe666.citadel.server.tick;

import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifier;
import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifierType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public abstract class TickRateTracker {

    public List<TickRateModifier> tickRateModifierList = new ArrayList<>();
    public List<Entity> specialTickRateEntities = new ArrayList<>();

    private long masterTickCount;

    public void masterTick() {
        masterTickCount++;
        specialTickRateEntities.forEach(this::tickBlockedEntity);
        specialTickRateEntities.removeIf(this::hasNormalTickRate);
        for (TickRateModifier modifier : tickRateModifierList) {
            modifier.masterTick();
        }
        if (!tickRateModifierList.isEmpty()) {
            if (tickRateModifierList.removeIf(TickRateModifier::doRemove)) {
                sync();
            }
        }
    }

    protected void sync() {
    }

    public boolean hasModifiersActive() {
        return !tickRateModifierList.isEmpty();
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (TickRateModifier modifier : tickRateModifierList) {
            if (!modifier.doRemove()) {
                list.add(modifier.toTag());
            }
        }
        tag.put("TickRateModifiers", list);
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        if (tag.contains("TickRateModifiers")) {
            ListTag list = tag.getList("TickRateModifiers", 10);
            for (int i = 0; i < list.size(); ++i) {
                CompoundTag tag1 = list.getCompound(i);
                TickRateModifier modifier = TickRateModifier.fromTag(tag1);
                if (!modifier.doRemove()) {
                    tickRateModifierList.add(modifier);
                }
            }
        }
    }

    public long getDayTimeIncrement(long timeIn) {
        float f = 1F;
        for (TickRateModifier modifier : tickRateModifierList) {
            if (modifier.getType() == TickRateModifierType.CELESTIAL) {
                f *= modifier.getTickRateMultiplier();
            }
        }
        if (f < 1F && f > 0F) {
            int inverse = (int) (1 / f);
            return masterTickCount % inverse == 0 ? timeIn : 0;
        }
        return (long) (timeIn * f);
    }

    public float getEntityTickLengthModifier(Entity entity) {
        float f = 1.0F;
        for (TickRateModifier modifier : tickRateModifierList) {
            if (modifier.getType().isLocal() && modifier.appliesTo(entity.level, entity.getX(), entity.getY(), entity.getZ())) {
                f *= modifier.getTickRateMultiplier();
            }
        }
        return f;
    }

    public boolean hasNormalTickRate(Entity entity) {
        return getEntityTickLengthModifier(entity) == 1.0F;
    }

    public boolean isTickingHandled(Entity entity){
        return specialTickRateEntities.contains(entity);
    }

    public void addTickBlockedEntity(Entity entity) {
        if (!isTickingHandled(entity)) {
            specialTickRateEntities.add(entity);
        }
    }

    protected void tickBlockedEntity(Entity entity){
        tickEntityAtCustomRate(entity);
    }


    protected abstract void tickEntityAtCustomRate(Entity entity);

}
