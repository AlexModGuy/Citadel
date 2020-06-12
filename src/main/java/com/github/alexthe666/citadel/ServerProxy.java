package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.server.entity.EntityPropertiesHandler;
import com.github.alexthe666.citadel.server.entity.PropertiesTracker;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerProxy {

    public void onPreInit() {
    }

    public void handleAnimationPacket(int entityId, int index){

    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> registry) {
        registry.getRegistry().register(Citadel.DEBUG_ITEM);
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().world.isRemote || !(event.getEntity() instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
        List<PropertiesTracker<?>> trackers = EntityPropertiesHandler.INSTANCE.getEntityTrackers(player);
        if (trackers != null && trackers.size() > 0) {
            boolean hasPlayer = false;
            for (PropertiesTracker tracker : trackers) {
                if (hasPlayer = tracker.getEntity() == player) {
                    break;
                }
            }
            if (!hasPlayer) {
                EntityPropertiesHandler.INSTANCE.addTracker(player, player);
            }
            for (PropertiesTracker<?> tracker : trackers) {
                tracker.updateTracker();
                if (tracker.isTrackerReady()) {
                    tracker.onSync();
                    PropertiesMessage message = new PropertiesMessage(tracker.getProperties(), tracker.getEntity());
                    Citadel.NETWORK_WRAPPER.sendTo(message, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
                }
            }
        }
    }
}
