package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AnimationMessage {

    private int entityID;
    private int index;

    public AnimationMessage(int entityID, int index) {
        this.entityID = entityID;
        this.index = index;
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(AnimationMessage message, Supplier<NetworkEvent.Context> context) {
            Citadel.PROXY.handleAnimationPacket(message.entityID, message.index);
            context.get().setPacketHandled(true);
        }
    }

    public static AnimationMessage read(PacketBuffer buf) {
        return new AnimationMessage(buf.readInt(), buf.readInt());
    }

    public static void write(AnimationMessage message, PacketBuffer buf) {
        buf.writeInt(message.entityID);
        buf.writeInt(message.index);
    }
}
