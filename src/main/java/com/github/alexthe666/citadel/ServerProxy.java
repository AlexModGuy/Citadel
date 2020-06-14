package com.github.alexthe666.citadel;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerProxy {

    public void onPreInit() {
    }

    public void handleAnimationPacket(int entityId, int index) {

    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> registry) {
        registry.getRegistry().register(Citadel.DEBUG_ITEM);
    }

    public void handlePropertiesPacket(String propertyID, CompoundNBT compound, int entityID) {
    }
}
