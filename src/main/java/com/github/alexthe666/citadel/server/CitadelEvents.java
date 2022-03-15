package com.github.alexthe666.citadel.server;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class CitadelEvents {


    private int updateTimer;

    @SubscribeEvent
    public void onEntityUpdateDebug(LivingEvent.LivingUpdateEvent event) {
        if (Citadel.DEBUG) {
            if ((event.getEntityLiving() instanceof Player)) {
                CompoundTag tag = CitadelEntityData.getCitadelTag(event.getEntityLiving());
                tag.putInt("CitadelInt", tag.getInt("CitadelInt") + 1);
                Citadel.LOGGER.debug("Citadel Data Tag tracker example: " + tag.getInt("CitadelInt"));
            }
        }
    }

    @SubscribeEvent
    public void onLoadBiome(BiomeLoadingEvent event) {
        float probability = (float) (ServerConfig.chunkGenSpawnModifierVal) * event.getSpawns().getProbability();
        event.getSpawns().creatureGenerationProbability(probability);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() != null && CitadelEntityData.getCitadelTag(event.getOriginal()) != null) {
            CitadelEntityData.setCitadelTag(event.getEntityLiving(), CitadelEntityData.getCitadelTag(event.getOriginal()));
        }
    }
}
