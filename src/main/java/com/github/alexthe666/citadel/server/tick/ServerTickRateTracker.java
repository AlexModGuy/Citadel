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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ServerTickRateTracker {
    public static final Logger LOGGER = LogManager.getLogger("citadel-server-tick");

    private float masterTickCount = -1;
    public MinecraftServer server;
    private long lastTimeTicked;
    public List<TickRateModifier> tickRateModifierList = new ArrayList<>();
    private long debugEffectMs = -1;

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

    public void masterTick() {
        masterTickCount++;
        for (TickRateModifier modifier : tickRateModifierList) {
            modifier.masterTick();
        }
        if (!tickRateModifierList.isEmpty()) {
            if (tickRateModifierList.removeIf(TickRateModifier::doRemove)) {
                sync();
            }
        }
        if(hasModifiersActive() && debugEffectMs == -1){
            debugEffectMs = System.currentTimeMillis();
        }
        if(!hasModifiersActive() && debugEffectMs != -1){
            System.out.println("MS DURATION: " + (System.currentTimeMillis() - debugEffectMs) * 0.001);
            debugEffectMs = -1;
        }
        lastTimeTicked = System.currentTimeMillis();
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
        if(i <= 0){
            return 1;
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
        if (tag.contains("TickRateModifiers", 9)) {
            ListTag list = tag.getList("TickRateModifiers", 9);
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
