package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin extends Model {

    public HumanoidModelMixin(Function<ResourceLocation, RenderType> p_103110_) {
        super(p_103110_);
    }

    @Inject(at = @At("HEAD"), remap = CitadelConstants.REMAPREFS, method = "Lnet/minecraft/client/model/HumanoidModel;poseRightArm(Lnet/minecraft/world/entity/LivingEntity;)V", cancellable = true)
    private void citadel_poseRightArm(LivingEntity entity, CallbackInfo ci) {
        EventPosePlayerHand event = new EventPosePlayerHand(entity, (HumanoidModel) ((Model) this), false);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Event.Result.ALLOW) {
            ci.cancel();
        }
    }


    @Inject(at = @At("HEAD"), remap = CitadelConstants.REMAPREFS, method = "Lnet/minecraft/client/model/HumanoidModel;poseLeftArm(Lnet/minecraft/world/entity/LivingEntity;)V", cancellable = true)
    private void citadel_poseLeftArm(LivingEntity entity, CallbackInfo ci) {
        EventPosePlayerHand event = new EventPosePlayerHand(entity, (HumanoidModel) ((Model) this), true);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Event.Result.ALLOW) {
            ci.cancel();
        }
    }


}
