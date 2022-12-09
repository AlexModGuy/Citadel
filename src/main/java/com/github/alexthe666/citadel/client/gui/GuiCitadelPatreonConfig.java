package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.client.rewards.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ForgeSlider;

@OnlyIn(Dist.CLIENT)
public class GuiCitadelPatreonConfig extends OptionsSubScreen {

    private ForgeSlider distSlider;
    private ForgeSlider speedSlider;
    private ForgeSlider heightSlider;
    private Button changeButton;
    private float rotateDist;
    private float rotateSpeed;
    private float rotateHeight;
    private String followType;

    public GuiCitadelPatreonConfig(Screen parentScreenIn, Options gameSettingsIn) {
        super(parentScreenIn, gameSettingsIn, Component.translatable("citadel.gui.patreon_customization"));
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
        float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2F;
        float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1;
        float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1F;
        rotateDist = roundTo(distance, 3);
        rotateSpeed = roundTo(speed, 3);
        rotateHeight = roundTo(height, 3);
        followType = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
    }

    private void setSliderValue(int i, float sliderValue) {
        boolean flag = false;
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
        if(i == 0){
            rotateDist = roundTo(sliderValue, 3);
            tag.putFloat("CitadelRotateDistance", rotateDist);
            //distSlider.isHovered = false;
        }else if(i == 1){
            rotateSpeed = roundTo(sliderValue, 3);
            tag.putFloat("CitadelRotateSpeed", rotateSpeed);
            //speedSlider.isHovered = false;
        }else{
            rotateHeight = roundTo(sliderValue, 3);
            tag.putFloat("CitadelRotateHeight", rotateHeight);
            //heightSlider.isHovered = false;
        }
        CitadelEntityData.setCitadelTag(Minecraft.getInstance().player, tag);
        Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", tag, Minecraft.getInstance().player.getId()));
    }

    public static float roundTo(float value, int places) {
        return value;
    }

    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void init() {
        super.init();
        int i = this.width / 2;
        int j = this.height / 6;
        Button doneButton = Button.builder(CommonComponents.GUI_DONE, (p_213079_1_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).size(200, 20).pos(i - 100, j+ 120).build();
        this.addRenderableWidget(doneButton);
        this.addRenderableWidget(distSlider = new ForgeSlider(i - 150 / 2 - 25, j + 30, 150, 20, Component.translatable("citadel.gui.orbit_dist").append(Component.translatable( ": ")), Component.translatable( ""), 0.125F, 5F, rotateDist, 0.1D, 1, true){
            @Override
            protected void applyValue() {
                GuiCitadelPatreonConfig.this.setSliderValue(0, (float)getValue());
            }
        });

        Button reset1Button = Button.builder(Component.translatable("citadel.gui.reset"), (p_213079_1_) -> {
            this.setSliderValue(0, 0.4F);
        }).size(40, 20).pos(i - 150 / 2 + 135, j+ 30).build();
        this.addRenderableWidget(reset1Button);

        this.addRenderableWidget(speedSlider = new ForgeSlider(i - 150 / 2 - 25, j + 60, 150, 20, Component.translatable("citadel.gui.orbit_speed").append(Component.translatable( ": ")), Component.translatable( ""), 0.0F, 5F, rotateSpeed, 0.1D, 2, true){
            @Override
            protected void applyValue() {
                GuiCitadelPatreonConfig.this.setSliderValue(1, (float)getValue());
            }
        });

        Button reset2Button = Button.builder(Component.translatable("citadel.gui.reset"), (p_213079_1_) -> {
            this.setSliderValue(1, 1F / 5F);
        }).size(40, 20).pos(i - 150 / 2 + 135, j+ 60).build();
        this.addRenderableWidget(reset2Button);

        this.addRenderableWidget(heightSlider = new ForgeSlider(i - 150 / 2 - 25, j + 90, 150, 20, Component.translatable("citadel.gui.orbit_height").append(Component.translatable( ": ")), Component.translatable( ""), 0.0F, 2F, rotateHeight, 0.1D, 2, true){
            @Override
            protected void applyValue() {
                GuiCitadelPatreonConfig.this.setSliderValue(2, (float)getValue());
            }
        });

        Button reset3Button = Button.builder(Component.translatable("citadel.gui.reset"), (p_213079_1_) -> {
            this.setSliderValue(2, 0.5F);
        }).size(40, 20).pos(i - 150 / 2 + 135, j+ 90).build();
        this.addRenderableWidget(reset3Button);

        changeButton = Button.builder(getTypeText(), (p_213079_1_) -> {
            this.followType = CitadelPatreonRenderer.getIdOfNext(followType);
            CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
            if(tag != null){
                tag.putString("CitadelFollowerType", followType);
                CitadelEntityData.setCitadelTag(Minecraft.getInstance().player, tag);
            }
            Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", tag, Minecraft.getInstance().player.getId()));
            changeButton.setMessage(getTypeText());
        }).size(200, 20).pos(i - 100, j).build();
        this.addRenderableWidget(changeButton);
    }

    private  Component getTypeText(){
        return Component.translatable("citadel.gui.follower_type").append(Component.translatable("citadel.follower." + followType));
    }
}
