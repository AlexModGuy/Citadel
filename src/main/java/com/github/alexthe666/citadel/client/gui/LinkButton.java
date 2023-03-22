package com.github.alexthe666.citadel.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;


public class LinkButton extends Button {

    public ItemStack previewStack;
    public GuiBasicBook book;

    public LinkButton(GuiBasicBook book, int x, int y, int width, int height, Component component, ItemStack previewStack, net.minecraft.client.gui.components.Button.OnPress onPress) {
        super(x, y, width + (previewStack.isEmpty() ? 0 : 6), height, component, onPress, Button.DEFAULT_NARRATION);
        this.previewStack = previewStack;
        this.book = book;
    }

    public LinkButton(GuiBasicBook book, int x, int y, int width, int height, Component component, net.minecraft.client.gui.components.Button.OnPress onPress) {
        this(book, x, y, width, height, component, ItemStack.EMPTY, onPress);
    }

    @Override
    public int getFGColor() {
        return this.isHovered ? book.getWidgetColor() : this.active ? 0X94745A : 10526880;
    }

    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }

        return 46 + i * 20;
    }


    @Override
    public void renderWidget(PoseStack poseStack, int guiX, int guiY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, book.getBookButtonsTexture());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getTextureY();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();



        this.blit(poseStack, this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height);
        this.blit(poseStack, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        if(this.isHovered){
            int color = book.getWidgetColor();
            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0xFF00) >> 8;
            int b = (color & 0xFF);
            BookBlit.setRGB(r, g, b, 255);
            i = 3;
            BookBlit.blit(poseStack, this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height, 256, 256);
            BookBlit.blit(poseStack, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height, 256, 256);
        }

        int j = getFGColor();
        int itemTextOffset = previewStack.isEmpty() ? 0 : 8;
        if(!previewStack.isEmpty()){
            ItemRenderer itemRenderer =  Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderAndDecorateItem(poseStack, previewStack, this.getX() + 2, this.getY() + 1);
        }
        drawTextOf(poseStack, font, this.getMessage(), this.getX() + itemTextOffset + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public static void drawTextOf(PoseStack poseStack, Font font, Component component, int x, int y, int color) {
        FormattedCharSequence formattedcharsequence = component.getVisualOrderText();
        font.draw(poseStack, formattedcharsequence, (float)(x - font.width(formattedcharsequence) / 2), (float)y, color);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

}
