package com.github.alexthe666.citadel.server;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CitadelEvents {

    private int updateTimer;

    @SubscribeEvent
    public void onEntityUpdateDebug(LivingEvent.LivingTickEvent event) {
        if (CitadelConstants.DEBUG) {
            if ((event.getEntity() instanceof Player)) {
                CompoundTag tag = CitadelEntityData.getCitadelTag(event.getEntity());
                tag.putInt("CitadelInt", tag.getInt("CitadelInt") + 1);
                Citadel.LOGGER.debug("Citadel Data Tag tracker example: " + tag.getInt("CitadelInt"));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() != null && CitadelEntityData.getCitadelTag(event.getOriginal()) != null) {
            CitadelEntityData.setCitadelTag(event.getEntity(), CitadelEntityData.getCitadelTag(event.getOriginal()));
        }
    }
}
