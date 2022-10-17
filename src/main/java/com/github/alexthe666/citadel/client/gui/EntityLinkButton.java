package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.client.gui.data.EntityLinkData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;


public class EntityLinkButton extends Button {

    private static final Map<String, Entity> renderedEntites = new HashMap<>();
    private final EntityLinkData data;
    private final GuiBasicBook bookGUI;
    private final EnttyRenderWindow window = new EnttyRenderWindow();

    public EntityLinkButton(GuiBasicBook bookGUI, EntityLinkData linkData, int k, int l, Button.OnPress o) {
        super(k + linkData.getX() - 12, l + linkData.getY(), (int) (24 * linkData.getScale()), (int) (24 * linkData.getScale()), CommonComponents.EMPTY, o);
        this.data = linkData;
        this.bookGUI = bookGUI;
    }

    public void renderButton(PoseStack posestack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int lvt_5_1_ = 0;
        int lvt_6_1_ = 30;
        float f = (float) data.getScale();
        RenderSystem.setShaderTexture(0, bookGUI.getBookWidgetTexture());
        posestack.pushPose();
        posestack.translate(this.x, this.y, 0);
        posestack.scale(f, f, 1);
        this.drawBtn(false, posestack, 0, 0, lvt_5_1_, lvt_6_1_, 24, 24);
        Entity model = null;
        EntityType type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(data.getEntity()));
        if (type != null) {
            model = renderedEntites.putIfAbsent(data.getEntity(), type.create(Minecraft.getInstance().level));
        }

        posestack.pushPose();
        if (model != null) {
            window.renderEntityWindow(posestack, x, y, model, (float) data.getEntityScale() * f, data.getOffset_x() * f, data.getOffset_y() * f, 2, 2, 22, 22);
        }
        posestack.popPose();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        if (this.isHovered) {
            bookGUI.setEntityTooltip(this.data.getHoverText());
            lvt_5_1_ = 48;
        } else {
            lvt_5_1_ = 24;
        }
        int color = bookGUI.getWidgetColor();
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF);
        BookBlit.setRGB(r, g, b, 255);
        RenderSystem.setShaderTexture(0, bookGUI.getBookWidgetTexture());
        this.drawBtn(!this.isHovered, posestack, 0, 0, lvt_5_1_, lvt_6_1_, 24, 24);
        posestack.popPose();
    }

    public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {

    }

    public void drawBtn(boolean color, PoseStack p_238474_1_, int p_238474_2_, int p_238474_3_, int p_238474_4_, int p_238474_5_, int p_238474_6_, int p_238474_7_) {
        if (color) {
            BookBlit.blit(p_238474_1_, p_238474_2_, p_238474_3_, this.getBlitOffset(), (float) p_238474_4_, (float) p_238474_5_, p_238474_6_, p_238474_7_, 256, 256);
        } else {
            blit(p_238474_1_, p_238474_2_, p_238474_3_, this.getBlitOffset(), (float) p_238474_4_, (float) p_238474_5_, p_238474_6_, p_238474_7_, 256, 256);
        }
    }

    private class EnttyRenderWindow extends GuiComponent {


        public void renderEntityWindow(PoseStack matrixStack, float x, float y, Entity toRender, float renderScale, float offsetX, float offsetY, int minX, int minY, int maxX, int maxY) {
            matrixStack.pushPose();
            matrixStack.translate(0, 0, -1F);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            PoseStack posestack = matrixStack;

            posestack.pushPose();
            RenderSystem.enableDepthTest();
            posestack.translate(0.0F, 0.0F, 950.0F);
            RenderSystem.colorMask(false, false, false, false);
            fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
            RenderSystem.colorMask(true, true, true, true);
            posestack.translate(0.0F, 0.0F, -950.0F);
            RenderSystem.depthFunc(518);
            fill(matrixStack, 22, 22, 2, 2, -16777216);
            RenderSystem.depthFunc(515);
            RenderSystem.setShaderTexture(0, bookGUI.getBookWidgetTexture());
            blit(matrixStack, 0, 0, 0, 30, 24, 24, 256, 256);
            if (toRender != null) {
                toRender.tickCount = Minecraft.getInstance().player.tickCount;
                float transitional = Math.max(0.0F, renderScale - 1.0F) * 8;
                bookGUI.drawEntityOnScreen(matrixStack, (int) (12 * renderScale + transitional + (x + offsetX)), (int) (24 * renderScale - transitional + y + offsetY), 10 * renderScale, false, 30, -130, 0, 0, 0, toRender);
            }
            RenderSystem.depthFunc(518);
            posestack.translate(0.0F, 0.0F, -950.0F);
            RenderSystem.colorMask(false, false, false, false);
            fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
            RenderSystem.colorMask(true, true, true, true);
            posestack.translate(0.0F, 0.0F, 950.0F);
            RenderSystem.depthFunc(515);
            posestack.popPose();
            matrixStack.popPose();
        }


    }
}
