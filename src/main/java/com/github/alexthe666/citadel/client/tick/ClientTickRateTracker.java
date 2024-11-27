package com.github.alexthe666.citadel.client.tick;

import com.github.alexthe666.citadel.server.tick.TickRateTracker;
import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifier;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ClientTickRateTracker extends TickRateTracker {
    public static final Logger LOGGER = LogManager.getLogger("citadel-client-tick");
    private static Map<Minecraft, ClientTickRateTracker> dataMap = new HashMap<>();

    public Minecraft client;

    private static float MS_PER_TICK = 50F;

    public ClientTickRateTracker(Minecraft client) {
        this.client = client;

    }

    public void syncFromServer(CompoundTag tag){
        tickRateModifierList.clear();
        fromTag(tag);
    }

    public static ClientTickRateTracker getForClient(Minecraft minecraft){
        if(!dataMap.containsKey(minecraft)){
            ClientTickRateTracker tracker = new ClientTickRateTracker(minecraft);
            dataMap.put(minecraft, tracker);
            return tracker;
        }
        return dataMap.get(minecraft);
    }

    public void masterTick(){
        super.masterTick();
        if(client.getTimer() instanceof DeltaTracker.Timer timer){
            timer.msPerTick = getClientTickRate();

        }
    }

    public float getClientTickRate(){
        float f = MS_PER_TICK;
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) {
            return f;
        }
        for(TickRateModifier modifier : tickRateModifierList){
            if(modifier.appliesTo(Minecraft.getInstance().level, Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ())){
                f *= modifier.getTickRateMultiplier();
            }
        }
        return Math.max(1F, f * getEntityTickLengthModifier(Minecraft.getInstance().player));
    }

    public float modifySoundPitch(SoundInstance soundInstance) {
        float f = 1.0F;
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null){
            return f;
        }
        for(TickRateModifier modifier : tickRateModifierList){
            if(modifier.appliesTo(Minecraft.getInstance().level, Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ())){
                f /= modifier.getTickRateMultiplier();
            }
        }
        return Math.max(1F, f * getEntityTickLengthModifier(Minecraft.getInstance().player));
    }

    @Override
    public void tickEntityAtCustomRate(Entity entity) {
        if(entity.level().isClientSide && entity.level() instanceof ClientLevel){
            ((ClientLevel)entity.level()).tickNonPassenger(entity);
        }
    }
}
