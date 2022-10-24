package com.github.alexthe666.citadel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
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

    public void openBookGUI(ItemStack book) {
    }

    public Object getISTERProperties() {
        return null;
    }

    public void onClientInit() {
    }
}
