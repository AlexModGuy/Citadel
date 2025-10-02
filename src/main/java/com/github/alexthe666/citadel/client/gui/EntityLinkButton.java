package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.client.gui.data.EntityLinkData;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;


public class EntityLinkButton extends Button {

    private static final Map<String, Entity> renderedEntites = new HashMap<>();
    private static final Quaternionf ENTITY_ROTATION = (new Quaternionf()).rotationXYZ((float) Math.toRadians(30), (float) Math.toRadians(130), (float) Math.PI);
    private final EntityLinkData data;
    private final GuiBasicBook bookGUI;

    public EntityLinkButton(GuiBasicBook bookGUI, EntityLinkData linkData, int k, int l, Button.OnPress o) {
        super(k + linkData.getX() - 12, l + linkData.getY(), (int) (24 * linkData.getScale()), (int) (24 * linkData.getScale()), CommonComponents.EMPTY, o, DEFAULT_NARRATION);
        this.data = linkData;
        this.bookGUI = bookGUI;
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int lvt_5_1_ = 0;
        int lvt_6_1_ = 30;
        float f = (float) data.getScale();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0);
        guiGraphics.pose().scale(f, f, 1);
        this.drawBtn(false, guiGraphics, 0, 0, lvt_5_1_, lvt_6_1_, 24, 24);
        Entity model = null;
        EntityType type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(data.getEntity()));
        model = renderedEntites.putIfAbsent(data.getEntity(), type.create(Minecraft.getInstance().level));

        guiGraphics.enableScissor(this.getX() + Math.round(f * 4), this.getY() + Math.round(f * 4), this.getX() + Math.round(f * 20), this.getY() + Math.round(f * 20));
        if (model != null) {
            model.tickCount = Minecraft.getInstance().player.tickCount;
            float renderScale = (float) (data.getEntityScale() * f * 10);
            renderEntityInInventory(guiGraphics, 11 + (int) (data.getOffset_x() * data.getEntityScale()), 22 + (int) (data.getOffset_y() * data.getEntityScale()), renderScale, ENTITY_ROTATION, model);
        }
        guiGraphics.disableScissor();
        if (this.isHovered) {
            bookGUI.setEntityTooltip(this.data.getHoverText());
            lvt_5_1_ = 48;
        } else {
            lvt_5_1_ = 24;
        }
        this.drawBtn(!this.isHovered, guiGraphics, 0, 0, lvt_5_1_, lvt_6_1_, 24, 24);
        guiGraphics.pose().popPose();
    }

    public void drawBtn(boolean color, GuiGraphics guiGraphics, int p_238474_2_, int p_238474_3_, int p_238474_4_, int p_238474_5_, int p_238474_6_, int p_238474_7_) {
        if (color) {
            int widgetColor = bookGUI.getWidgetColor();
            int r = (widgetColor & 0xFF0000) >> 16;
            int g = (widgetColor & 0xFF00) >> 8;
            int b = (widgetColor & 0xFF);
            BookBlit.blitWithColor(guiGraphics, bookGUI.getBookWidgetTexture(),  p_238474_2_, p_238474_3_, 0, (float) p_238474_4_, (float) p_238474_5_, p_238474_6_, p_238474_7_, 256, 256, r, g, b, 255);
        } else {
            guiGraphics.blit(bookGUI.getBookWidgetTexture(), p_238474_2_, p_238474_3_, 0, (float) p_238474_4_, (float) p_238474_5_, p_238474_6_, p_238474_7_, 256, 256);
        }
    }


    public void renderEntityInInventory(GuiGraphics guiGraphics, int xPos, int yPos, float scale, Quaternionf rotation, Entity entity) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(xPos, yPos, 50.0D);
        guiGraphics.pose().mulPose((new Matrix4f()).scaling(scale, scale,  (-scale)));
        guiGraphics.pose().mulPose(rotation);

        Vector3f light0 = new Vector3f(1, -1.0F, -1.0F).normalize();
        Vector3f light1 = new Vector3f(-1, 1.0F, 1.0F).normalize();
        RenderSystem.setShaderLights(light0, light1);
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        entityrenderdispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, guiGraphics.pose(), guiGraphics.bufferSource(), 15728880));
        guiGraphics.flush();
        entityrenderdispatcher.setRenderShadow(true);
        guiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

}
