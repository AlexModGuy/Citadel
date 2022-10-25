package com.github.alexthe666.citadel.client.tick;

import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifier;
import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifierType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientTickRateTracker {
    private static Map<Minecraft, ClientTickRateTracker> dataMap = new HashMap<>();

    public Minecraft client;
    public List<TickRateModifier> tickRateModifierList = new ArrayList<>();

    private static float MS_PER_TICK = 50f;
    public ClientTickRateTracker(Minecraft client) {
        this.client = client;

    }

    public void syncFromServer(CompoundTag tag){
        tickRateModifierList.clear();
        if (tag.contains("TickRateModifiers", 10)) {
            ListTag list = tag.getList("TickRateModifiers", 10);
            for(int i = 0; i < list.size(); ++i) {
                CompoundTag tag1 = list.getCompound(i);
                TickRateModifier modifier = TickRateModifier.fromTag(tag1);
                if(!modifier.doRemove()){
                    tickRateModifierList.add(modifier);
                }
            }
        }
    }

    public static ClientTickRateTracker getForClient(Minecraft minecraft){
        if(!dataMap.containsKey(minecraft)){
            ClientTickRateTracker tracker = new ClientTickRateTracker(minecraft);
            dataMap.put(minecraft, tracker);
            return tracker;
        }
        return dataMap.get(minecraft);
    }

    public void tick(){
        client.timer.msPerTick = getClientTickRate();
    }

    public float getClientTickRate(){
        float f = MS_PER_TICK;
        for(TickRateModifier modifier : tickRateModifierList){
            if(modifier.isGlobal()){
                f *= modifier.getTickRateMultiplier();
            }
        }
        return f;
    }

    public float modifySoundPitch(SoundInstance soundInstance) {
        float f = 1.0F;
        for(TickRateModifier modifier : tickRateModifierList){
            if(modifier.isGlobal()){
                f *= modifier.getTickRateMultiplier();
            }
        }
        return f;
    }
}
