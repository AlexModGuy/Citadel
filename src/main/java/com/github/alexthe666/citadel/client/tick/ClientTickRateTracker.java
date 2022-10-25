package com.github.alexthe666.citadel.client.tick;

import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientTickRateTracker {
    public static final Logger LOGGER = LogManager.getLogger("citadel-client-tick");
    private static Map<Minecraft, ClientTickRateTracker> dataMap = new HashMap<>();

    public Minecraft client;
    public List<TickRateModifier> tickRateModifierList = new ArrayList<>();

    private static float MS_PER_TICK = 50f;
    public ClientTickRateTracker(Minecraft client) {
        this.client = client;

    }

    public void syncFromServer(CompoundTag tag){
        tickRateModifierList.clear();
        if (tag.contains("TickRateModifiers")) {
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
        debug();
    }

    private void debug(){
        LOGGER.debug("client tick length in MS: " + client.timer.msPerTick);
    }

    public float getClientTickRate(){
        float f = MS_PER_TICK;
        for(TickRateModifier modifier : tickRateModifierList){
            if(modifier.appliesTo(Minecraft.getInstance().level, Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ())){
                f *= modifier.getTickRateMultiplier();
            }
        }
        if(f <= 0){
            return 1;
        }
        return f;
    }

    public float modifySoundPitch(SoundInstance soundInstance) {
        float f = 1.0F;
        for(TickRateModifier modifier : tickRateModifierList){
            if(modifier.appliesTo(Minecraft.getInstance().level, Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ())){
                f /= modifier.getTickRateMultiplier();
            }
        }
        if(f <= 0){
            return 1;
        }
        return f;
    }
}
