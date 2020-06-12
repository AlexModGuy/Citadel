package com.github.alexthe666.citadel.server;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.*;
import com.github.alexthe666.citadel.server.entity.implementation.CitadelEntityProperties;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CitadelServerEvents {


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(new ResourceLocation("citadel", "extended_entity_data_citadel"), new ICapabilitySerializable() {
            @Override
            public INBT serializeNBT() {
                Capability<IEntityData> capability = Citadel.ENTITY_DATA_CAPABILITY;
                IEntityData instance = capability.getDefaultInstance();
                instance.init(event.getObject(), event.getObject().getEntityWorld(), false);
                return capability.getStorage().writeNBT(capability, instance, null);
            }

            @Override
            public void deserializeNBT(INBT nbt) {
                Capability<IEntityData> capability = Citadel.ENTITY_DATA_CAPABILITY;
                IEntityData instance = capability.getDefaultInstance();
                instance.init(event.getObject(), event.getObject().getEntityWorld(), true);
                capability.getStorage().readNBT(capability, instance, null, nbt);
            }

            private final LazyOptional<IEntityData> holder = LazyOptional.of(() -> new EntityDataCapabilityImplementation());

            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction side){
                if (capability == Citadel.ENTITY_DATA_CAPABILITY) {
                    return Citadel.ENTITY_DATA_CAPABILITY.orEmpty(capability, holder).cast();
                } else {
                    return null;
                }
            }
        });
    }

    
    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        boolean cached = EntityPropertiesHandler.INSTANCE.hasEntityInCache(event.getEntity().getClass());
        List<String> entityPropertiesIDCache = !cached ? new ArrayList<>() : null;
        EntityPropertiesHandler.INSTANCE.getRegisteredProperties().filter(propEntry -> propEntry.getKey().isAssignableFrom(event.getEntity().getClass())).forEach(propEntry -> {
            for (Class<? extends EntityProperties> propClass : propEntry.getValue()) {
                try {
                    Constructor<? extends EntityProperties> constructor = propClass.getConstructor();
                    EntityProperties prop = constructor.newInstance();
                    String propID = prop.getID();
                    EntityDataHandler.INSTANCE.registerExtendedEntityData(event.getEntity(), prop);
                    if (!cached) {
                        entityPropertiesIDCache.add(propID);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        if (!cached) {
            EntityPropertiesHandler.INSTANCE.addEntityToCache(event.getEntity().getClass(), entityPropertiesIDCache);
        }
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
                    Citadel.sendNonLocal(message, player);
                }
            }
        }
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            EntityPropertiesHandler.INSTANCE.addTracker(player, player);
        }
    }

    @SubscribeEvent
    public void onEntityStartTracking(PlayerEvent.StartTracking event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            EntityPropertiesHandler.INSTANCE.addTracker((ServerPlayerEntity) event.getPlayer(), event.getTarget());
        }
    }

    @SubscribeEvent
    public void onEntityStopTracking(PlayerEvent.StopTracking event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            EntityPropertiesHandler.INSTANCE.removeTracker((ServerPlayerEntity) event.getPlayer(), event.getTarget());
        }
    }

    private int updateTimer;

    
    @SubscribeEvent
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            this.updateTimer++;
            if (this.updateTimer > 20) {
                this.updateTimer = 0;
                Iterator<Map.Entry<ServerPlayerEntity, List<PropertiesTracker<?>>>> iterator = EntityPropertiesHandler.INSTANCE.getTrackerIterator();
                while (iterator.hasNext()) {
                    Map.Entry<ServerPlayerEntity, List<PropertiesTracker<?>>> trackerEntry = iterator.next();
                    ServerPlayerEntity player = trackerEntry.getKey();
                    ServerWorld playerWorld = player.world.getServer().getWorld(player.dimension);
                    if (player == null || player.removed || playerWorld == null || !playerWorld.getEntities().collect(Collectors.toList()).contains(player)) {
                        iterator.remove();
                        trackerEntry.getValue().forEach(PropertiesTracker::removeTracker);
                    } else {
                        Iterator<PropertiesTracker<?>> it = trackerEntry.getValue().iterator();
                        while (it.hasNext()) {
                            PropertiesTracker tracker = it.next();
                            Entity entity = tracker.getEntity();
                            ServerWorld entityWorld = entity.world.getServer().getWorld(player.dimension);
                            if (entity == null || entity.removed || entityWorld == null || !playerWorld.getEntities().collect(Collectors.toList()).contains(entity)) {
                                it.remove();
                                tracker.removeTracker();
                            }
                        }
                    }
                }
            }
        }
    }
}
