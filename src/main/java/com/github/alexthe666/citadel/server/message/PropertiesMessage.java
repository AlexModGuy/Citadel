package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.server.entity.EntityProperties;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PropertiesMessage {
    private String propertyID;
    private CompoundNBT compound;
    private int entityID;

    public PropertiesMessage(String propertyID, CompoundNBT compound, int entityID) {
        this.propertyID = propertyID;
        this.compound = compound;
        this.entityID = entityID;
    }

    public PropertiesMessage(EntityProperties<?> properties, Entity entity) {
        this.propertyID = properties.getID();
        CompoundNBT compound = new CompoundNBT();
        properties.saveTrackingSensitiveData(compound);
        this.compound = compound;
        this.entityID = entity.getEntityId();
    }

    public static void write(PropertiesMessage message, PacketBuffer packetBuffer) {
        PacketBufferUtils.writeUTF8String(packetBuffer, message.propertyID);
        PacketBufferUtils.writeTag(packetBuffer, message.compound);
        packetBuffer.writeInt(message.entityID);
    }

    public static PropertiesMessage read(PacketBuffer packetBuffer) {
        return new PropertiesMessage(PacketBufferUtils.readUTF8String(packetBuffer), PacketBufferUtils.readTag(packetBuffer), packetBuffer.readInt());
    }

    public static class Handler {

        public static void handle(final PropertiesMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
        }
    }
}