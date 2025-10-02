package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    public AbstractClientPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @ModifyReturnValue(method = "getSkin", at = @At("TAIL"))
    private PlayerSkin citadel_getSkin(PlayerSkin original) {
        return Optional.ofNullable(CitadelCapes.getCurrentCape(this))
                .map(CitadelCapes.Cape::getTexture)
                .map(resourceLocation -> new PlayerSkin(original.texture(), original.textureUrl(), resourceLocation, resourceLocation, original.model(), original.secure()))
                .orElse(original);
    }
}
