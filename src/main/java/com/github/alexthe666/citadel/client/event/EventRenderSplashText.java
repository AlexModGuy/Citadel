package com.github.alexthe666.citadel.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.eventbus.api.Event;

public class EventRenderSplashText extends Event {
    private String splashText;
    private PoseStack poseStack;

    private TitleScreen titleScreen;
    private float partialTicks;

    public EventRenderSplashText(String splashText, PoseStack poseStack, TitleScreen titleScreen, float partialTicks) {
        this.splashText = splashText;
        this.poseStack = poseStack;
        this.titleScreen = titleScreen;
        this.partialTicks = partialTicks;
    }

    public String getSplashText() {
        return splashText;
    }

    public void setSplashText(String splashText) {
        this.splashText = splashText;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public TitleScreen getTitleScreen() {
        return titleScreen;
    }

    public float getPartialTicks() {
        return partialTicks;
    }


    @Event.HasResult
    public static class Pre extends EventRenderSplashText {

        private int splashTextColor;

        public Pre(String splashText, PoseStack poseStack, TitleScreen titleScreen, float partialTicks, int splashTextColor) {
            super(splashText, poseStack, titleScreen, partialTicks);
            this.splashTextColor = splashTextColor;
        }

        public int getSplashTextColor() {
            return splashTextColor;
        }

        public void setSplashTextColor(int splashTextColor) {
            this.splashTextColor = splashTextColor;
        }
    }

    @Event.HasResult
    public static class Post extends EventRenderSplashText {

        public Post(String splashText, PoseStack poseStack, TitleScreen titleScreen, float partialTicks) {
            super(splashText, poseStack, titleScreen, partialTicks);
        }
    }

}
