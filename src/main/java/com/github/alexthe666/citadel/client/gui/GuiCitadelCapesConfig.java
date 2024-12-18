package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.ClientProxy;
import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

import javax.annotation.Nullable;

public class GuiCitadelCapesConfig extends OptionsSubScreen {

    @Nullable
    private String capeType;
    private Button button;


    public GuiCitadelCapesConfig(Screen parentScreenIn, Options gameSettingsIn) {
        super(parentScreenIn, gameSettingsIn, Component.translatable("citadel.gui.capes"));
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
        capeType = tag.contains("CitadelCapeType") && !tag.getString("CitadelCapeType").isEmpty() ? tag.getString("CitadelCapeType") : null;
    }


    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        int i = this.width / 2;
        int j = this.height / 6;
        guiGraphics.pose().pushPose();
        ClientProxy.hideFollower = true;
        renderBackwardsEntity(i, j + 144, 60, 0, 0, Minecraft.getInstance().player);
        ClientProxy.hideFollower = false;
        guiGraphics.pose().popPose();
    }

    public static void renderBackwardsEntity(int x, int y, int size, float angleXComponent, float angleYComponent, LivingEntity entity) {
        float f = angleXComponent;
        float f1 = angleYComponent;
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.translate(x, y, 1050.0F);
        matrix4fStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.translate(0.0D, 0.0D, 1000.0D);
        posestack1.scale((float)size, (float)size, (float)size);
        Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf quaternion1 = Axis.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        quaternion.mul(Axis.YP.rotationDegrees(180.0F));
        posestack1.mulPose(quaternion);
        float f2 = entity.yBodyRot;
        float f3 = entity.getYRot();
        float f4 = entity.getXRot();
        float f5 = entity.yHeadRotO;
        float f6 = entity.yHeadRot;
        entity.yBodyRot = 180.0F + f * 20.0F;
        entity.setYRot(180.0F + f * 40.0F);
        entity.setXRot(-f1 * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conjugate();
        entityrenderdispatcher.overrideCameraOrientation(quaternion1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, posestack1, multibuffersource$buffersource, 15728880);
        });
        multibuffersource$buffersource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        entity.yBodyRot = f2;
        entity.setYRot(f3);
        entity.setXRot(f4);
        entity.yHeadRotO = f5;
        entity.yHeadRot = f6;
        matrix4fStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }


    protected void init() {
        super.init();
        int i = this.width / 2;
        int j = this.height / 6;
        Button doneButton = Button.builder(CommonComponents.GUI_DONE, (p_213079_1_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).size(200, 20).pos(i - 100, j+ 160).build();
        this.addRenderableWidget(doneButton);
        button = Button.builder(getTypeText(), (p_213079_1_) -> {
            CitadelCapes.Cape nextCape = CitadelCapes.getNextCape(capeType, Minecraft.getInstance().player.getUUID());
            this.capeType = nextCape == null ? null : nextCape.getIdentifier();
            CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
            if(tag != null){
                if(capeType == null){
                    tag.putString("CitadelCapeType", "");
                    tag.putBoolean("CitadelCapeDisabled", true);
                }else{
                    tag.putString("CitadelCapeType", capeType);
                    tag.putBoolean("CitadelCapeDisabled", false);
                }
                CitadelEntityData.setCitadelTag(Minecraft.getInstance().player, tag);
            }
            PacketDistributor.sendToServer(new PropertiesMessage("CitadelTagUpdate", tag, Minecraft.getInstance().player.getId()));
            button.setMessage(getTypeText());
        }).size(200, 20).pos(i - 100, j).build();
        this.addRenderableWidget(button);

    }

    @Override
    protected void addOptions() {

    }

    private Component getTypeText(){
        Component suffix;

        if(capeType == null){
            suffix = Component.translatable("citadel.gui.no_cape");
        }else{

            CitadelCapes.Cape cape = CitadelCapes.getById(capeType);
            if(cape == null){
                suffix = Component.translatable("citadel.gui.no_cape");
            }else{
                suffix = Component.translatable("cape." + cape.getIdentifier());
            }
        }
        return Component.translatable("citadel.gui.cape_type").append(" ").append(suffix);
    }
}
