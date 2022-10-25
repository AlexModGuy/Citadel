package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.server.world.CitadelServerData;
import com.github.alexthe666.citadel.server.world.ModifiableTickRateServer;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerProxy {


    public ServerProxy() {

    }

    public void onPreInit() {
    }

    public void handleAnimationPacket(int entityId, int index) {

    }

    public void handlePropertiesPacket(String propertyID, CompoundTag compound, int entityID) {
    }

    public void handleClientTickRatePacket(CompoundTag compound) {
    }

    public void openBookGUI(ItemStack book) {
    }

    public Object getISTERProperties() {
        return null;
    }

    public void onClientInit() {
    }
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event){
        ServerTickRateTracker tickRateTracker = CitadelServerData.get(event.getServer()).getOrCreateTickRateTracker();
        if(event.getServer() instanceof ModifiableTickRateServer modifiableServer){
            long l = tickRateTracker.getServerTickLengthMs();
            if(l == MinecraftServer.MS_PER_TICK){
                modifiableServer.resetGlobalTickLengthMs();
            }else{
                modifiableServer.setGlobalTickLengthMs(tickRateTracker.getServerTickLengthMs());
            }
            tickRateTracker.masterTick();
        }
    }

}
