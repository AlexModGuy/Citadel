package com.github.alexthe666.citadel.server.message;


import com.github.alexthe666.citadel.client.render.pathfinding.PathfindingDebugRenderer;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.MNode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Message to sync some path over to the client.
 */
public class SyncePathMessage implements CustomPacketPayload{

    public static final CustomPacketPayload.Type<SyncePathMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("citadel", "sync_path"));
    public static final StreamCodec<FriendlyByteBuf, SyncePathMessage> CODEC = StreamCodec.ofMember(SyncePathMessage::write, SyncePathMessage::read);

    /**
     * Set of visited nodes.
     */
    public Set<MNode> lastDebugNodesVisited = new HashSet<>();

    /**
     * Set of not visited nodes.
     */
    public Set<MNode> lastDebugNodesNotVisited = new HashSet<>();

    /**
     * Set of chosen nodes for the path.
     */
    public Set<MNode> lastDebugNodesPath = new HashSet<>();

    /**
     * Create a new path message with the filled pathpoints.
     */
    public SyncePathMessage(final Set<MNode> lastDebugNodesVisited, final Set<MNode> lastDebugNodesNotVisited, final Set<MNode> lastDebugNodesPath) {
        super();
        this.lastDebugNodesVisited = lastDebugNodesVisited;
        this.lastDebugNodesNotVisited = lastDebugNodesNotVisited;
        this.lastDebugNodesPath = lastDebugNodesPath;
    }

    public void write(final FriendlyByteBuf buf) {
        buf.writeInt(lastDebugNodesVisited.size());
        for (final MNode MNode : lastDebugNodesVisited) {
            MNode.serializeToBuf(buf);
        }

        buf.writeInt(lastDebugNodesNotVisited.size());
        for (final MNode MNode : lastDebugNodesNotVisited) {
            MNode.serializeToBuf(buf);
        }

        buf.writeInt(lastDebugNodesPath.size());
        for (final MNode MNode : lastDebugNodesPath) {
            MNode.serializeToBuf(buf);
        }
    }

    public static SyncePathMessage read(final FriendlyByteBuf buf) {
        int size = buf.readInt();

        Set<MNode> lastDebugNodesVisited = new HashSet<>();
        for (int i = 0; i < size; i++) {
            lastDebugNodesVisited.add(new MNode(buf));
        }

        size = buf.readInt();
        Set<MNode> lastDebugNodesNotVisited = new HashSet<>();
        for (int i = 0; i < size; i++) {
            lastDebugNodesNotVisited.add(new MNode(buf));
        }

        size = buf.readInt();
        Set<MNode> lastDebugNodesPath = new HashSet<>();
        for (int i = 0; i < size; i++) {
            lastDebugNodesPath.add(new MNode(buf));
        }

        return new SyncePathMessage(lastDebugNodesVisited, lastDebugNodesNotVisited, lastDebugNodesPath);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final SyncePathMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                PathfindingDebugRenderer.lastDebugNodesVisited = message.lastDebugNodesVisited;
                PathfindingDebugRenderer.lastDebugNodesNotVisited = message.lastDebugNodesNotVisited;
                PathfindingDebugRenderer.lastDebugNodesPath = message.lastDebugNodesPath;
            }
        });
    }
}