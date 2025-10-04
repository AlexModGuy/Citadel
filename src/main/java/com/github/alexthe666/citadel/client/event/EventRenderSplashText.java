package com.github.alexthe666.citadel.client.event;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.util.TriState;

public class EventRenderSplashText extends Event {
    private String splashText;

    private GuiGraphics guiGraphics;
    private float partialTicks;

    public EventRenderSplashText(String splashText, GuiGraphics guiGraphics, float partialTicks) {
        this.splashText = splashText;
        this.guiGraphics = guiGraphics;
        this.partialTicks = partialTicks;
    }

    public String getSplashText() {
        return splashText;
    }

    public void setSplashText(String splashText) {
        this.splashText = splashText;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public static class Pre extends EventRenderSplashText {
        private TriState result = TriState.DEFAULT;

        private int splashTextColor;

        public Pre(String splashText, GuiGraphics guiGraphics, float partialTicks, int splashTextColor) {
            super(splashText, guiGraphics, partialTicks);
            this.splashTextColor = splashTextColor;
        }

        public int getSplashTextColor() {
            return splashTextColor;
        }

        public void setSplashTextColor(int splashTextColor) {
            this.splashTextColor = splashTextColor;
        }

        public void setResult(TriState result) {
            this.result = result;
        }

        public TriState getResult() {
            return result;
        }
    }

    public static class Post extends EventRenderSplashText {

        public Post(String splashText, GuiGraphics guiGraphics, float partialTicks) {
            super(splashText, guiGraphics, partialTicks);
        }
    }

}
