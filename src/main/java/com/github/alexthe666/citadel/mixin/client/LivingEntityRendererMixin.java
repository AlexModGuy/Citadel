package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventLivingRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {

    @Shadow
    protected EntityModel model;

    @Inject(
            method = "setupRotations",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "RETURN")
    )
    protected void citadel_setupRotations(T entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale, CallbackInfo ci) {
        EventLivingRenderer.SetupRotations event = new EventLivingRenderer.SetupRotations(entity, model, poseStack, yBodyRot, partialTick);
        NeoForge.EVENT_BUS.post(event);

    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V",
                    shift = At.Shift.BEFORE
            )
    )
    protected void citadel_render_setupAnim_before(LivingEntity livingEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        EventLivingRenderer.PreSetupAnimations event = new EventLivingRenderer.PreSetupAnimations(livingEntity, model, poseStack, yaw, partialTicks, bufferSource, packedLight);
        NeoForge.EVENT_BUS.post(event);
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            remap = true,

            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V",
                    shift = At.Shift.AFTER
            )
    )
    protected void citadel_render_setupAnim_after(LivingEntity livingEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        EventLivingRenderer.PostSetupAnimations event = new EventLivingRenderer.PostSetupAnimations(livingEntity, model, poseStack, yaw, partialTicks, bufferSource, packedLight);
        NeoForge.EVENT_BUS.post(event);
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "RETURN")
    )
    protected void citadel_render_renderToBuffer(LivingEntity livingEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        EventLivingRenderer.PostRenderModel event = new EventLivingRenderer.PostRenderModel(livingEntity, model, poseStack, yaw, partialTicks, bufferSource, packedLight);
        NeoForge.EVENT_BUS.post(event);
    }
}
