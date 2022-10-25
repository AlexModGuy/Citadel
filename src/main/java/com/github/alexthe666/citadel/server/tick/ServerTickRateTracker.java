package com.github.alexthe666.citadel.server.tick;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.message.SyncClientTickRateMessage;
import com.github.alexthe666.citadel.server.world.CitadelServerData;
import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifier;
import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifierType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ServerTickRateTracker {

    private float lastMasterTick = -1;
    public MinecraftServer server;
    public List<TickRateModifier> tickRateModifierList = new ArrayList<>();

    public ServerTickRateTracker(MinecraftServer server) {
        this.server = server;
    }

    public ServerTickRateTracker(MinecraftServer server, CompoundTag tag) {
        this(server);
        fromTag(tag);
    }

    public boolean hasModifiersActive() {
        return !tickRateModifierList.isEmpty();
    }

    public void addTickRateModifier(TickRateModifier modifier) {
        tickRateModifierList.add(modifier);
        sync();
    }

    public void masterTick(long masterTickMs) {
        float f = masterTickMs / (float)MinecraftServer.MS_PER_TICK;

        if (f % 1F == 0.0F && f != lastMasterTick) {
            for (TickRateModifier modifier : tickRateModifierList) {
                modifier.tick();
            }
            if (!tickRateModifierList.isEmpty()) {
                if (tickRateModifierList.removeIf(TickRateModifier::doRemove)) {
                    sync();
                }
            }
            lastMasterTick = f;
        }
    }

    private void sync() {
        Citadel.sendMSGToAll(new SyncClientTickRateMessage(toTag()));
    }

    public int getServerTickLengthMs() {
        int i = MinecraftServer.MS_PER_TICK;
        for (TickRateModifier modifier : tickRateModifierList) {
            if (modifier.getType() == TickRateModifierType.GLOBAL) {
                i *= modifier.getTickRateMultiplier();
            }
        }
        return i;
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
        if (tag.contains("TickRateModifiers", 10)) {
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

    public static ServerTickRateTracker getForServer(MinecraftServer server) {
        return CitadelServerData.get(server).getOrCreateTickRateTracker();
    }

    public static void modifyTickRate(Level level, TickRateModifier modifier) {
        if (level instanceof ServerLevel serverLevel) {
            getForServer(serverLevel.getServer()).addTickRateModifier(modifier);
        }
    }
}
