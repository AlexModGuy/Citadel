package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventRenderSplashText;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Shadow
    private String splash;
    private int splashTextColor = -1;

    protected TitleScreenMixin(Component component) {
        super(component);
    }

    @Inject(
            method = {"Lnet/minecraft/client/gui/screens/TitleScreen;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/TitleScreen;drawCenteredString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V",
                    shift = At.Shift.BEFORE
            )
    )
    protected void citadel_preRenderSplashText(PoseStack poseStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        poseStack.pushPose();
        EventRenderSplashText.Pre event = new EventRenderSplashText.Pre(splash, poseStack, (TitleScreen) (Screen) this, partialTicks, splashTextColor);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.getResult() == Event.Result.ALLOW) {
            splash = event.getSplashText();
            splashTextColor = event.getSplashTextColor();
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/gui/screens/TitleScreen;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/TitleScreen;drawCenteredString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V",
                    shift = At.Shift.AFTER
            )
    )
    protected void citadel_postRenderSplashText(PoseStack poseStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        EventRenderSplashText.Post event = new EventRenderSplashText.Post(splash, poseStack, (TitleScreen) (Screen) this, partialTicks);
        MinecraftForge.EVENT_BUS.post(event);
        poseStack.popPose();
    }


    @ModifyConstant(
            method = {"Lnet/minecraft/client/gui/screens/TitleScreen;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V"},
            constant = @Constant(intValue = 16776960))
    private int citadel_splashTextColor(int value) {
        return splashTextColor == -1 ? value : splashTextColor;
    }
}
