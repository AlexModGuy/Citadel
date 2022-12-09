package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.server.entity.IDancesToJukebox;
import com.github.alexthe666.citadel.server.event.EventChangeEntityTickRate;
import com.github.alexthe666.citadel.server.event.EventMergeStructureSpawns;
import com.github.alexthe666.citadel.server.event.EventReplaceBiome;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import com.github.alexthe666.citadel.server.world.CitadelServerData;
import com.github.alexthe666.citadel.server.world.ExpandedBiomeSource;
import com.github.alexthe666.citadel.server.world.ModifiableTickRateServer;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    public void handleJukeboxPacket(Level level, int entityId, BlockPos jukeBox, boolean dancing) {
        Entity entity = level.getEntity(entityId);
        if (entity instanceof IDancesToJukebox dancer) {
            dancer.setDancing(dancing);
            dancer.setJukeboxPos(dancing ? jukeBox : null);
        }
    }


    public void openBookGUI(ItemStack book) {
    }

    public Object getISTERProperties() {
        return null;
    }

    public void onClientInit() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        ServerTickRateTracker tickRateTracker = CitadelServerData.get(event.getServer()).getOrCreateTickRateTracker();
        if (event.getServer() instanceof ModifiableTickRateServer modifiableServer && event.phase == TickEvent.Phase.START) {
            long l = tickRateTracker.getServerTickLengthMs();
            if (l == MinecraftServer.MS_PER_TICK) {
                modifiableServer.resetGlobalTickLengthMs();
            } else {
                modifiableServer.setGlobalTickLengthMs(tickRateTracker.getServerTickLengthMs());
            }
            tickRateTracker.masterTick();
        }
    }

    /*
        Biome gen example. Place
        ExpandedBiomes.addExpandedBiome(Biomes.WARPED_FOREST, LevelStem.OVERWORLD);
        In mod's constructor in order to work before trying something similar to this.

    @SubscribeEvent
    public void onReplaceBiome(EventReplaceBiome event){
        if(event.weirdness > 0.5F && event.weirdness < 1F && event.depth > 0.2F && event.depth < 0.9F){
            event.setResult(Event.Result.ALLOW);
            event.setBiomeToGenerate(event.getBiomeSource().getResourceKeyMap().get(Biomes.WARPED_FOREST));
        }
    }
    */

    public boolean canEntityTickClient(Level level, Entity entity) {
        return true;
    }

    public boolean canEntityTickServer(Level level, Entity entity) {
        if (level instanceof ServerLevel) {
            ServerTickRateTracker tracker = ServerTickRateTracker.getForServer(((ServerLevel) level).getServer());
            if (tracker.isTickingHandled(entity)) {
                return false;
            } else if (!tracker.hasNormalTickRate(entity)) {
                EventChangeEntityTickRate event = new EventChangeEntityTickRate(entity, tracker.getEntityTickLengthModifier(entity));
                MinecraftForge.EVENT_BUS.post(event);
                if (event.isCanceled()) {
                    return true;
                } else {
                    tracker.addTickBlockedEntity(entity);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isGamePaused() {
        return false;
    }

    public float getMouseOverProgress(ItemStack itemStack) {
        return 0.0F;
    }

    public Player getClientSidePlayer() {
        return null;
    }
}
