package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncClientTickRateMessage {
    private CompoundTag compound;

    public SyncClientTickRateMessage(CompoundTag compound) {
        this.compound = compound;
    }

    public static void write(SyncClientTickRateMessage message, FriendlyByteBuf packetBuffer) {
        PacketBufferUtils.writeTag(packetBuffer, message.compound);
    }

    public static SyncClientTickRateMessage read(FriendlyByteBuf packetBuffer) {
        return new SyncClientTickRateMessage(PacketBufferUtils.readTag(packetBuffer));
    }

    public static class Handler {

        public static void handle(final SyncClientTickRateMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                    Citadel.PROXY.handleClientTickRatePacket(message.compound);

                }
            });
        }
    }
}