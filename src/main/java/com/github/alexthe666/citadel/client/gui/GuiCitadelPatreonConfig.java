package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.client.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.Slider;

@OnlyIn(Dist.CLIENT)
public class GuiCitadelPatreonConfig extends OptionsSubScreen {

    private Slider distSlider;
    private Slider speedSlider;
    private Slider heightSlider;
    private Button changeButton;
    private final Slider.ISlider distSliderResponder;
    private final Slider.ISlider speedSliderResponder;
    private final Slider.ISlider heightSliderResponder;
    private float rotateDist;
    private float rotateSpeed;
    private float rotateHeight;
    private String followType = "citadel";

    public GuiCitadelPatreonConfig(Screen parentScreenIn, Options gameSettingsIn) {
        super(parentScreenIn, gameSettingsIn, new TranslatableComponent("citadel.gui.patreon_customization"));
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
        float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2F;
        float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1;
        float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1F;
        rotateDist = roundTo(distance, 3);
        rotateSpeed = roundTo(speed, 3);
        rotateHeight = roundTo(height, 3);
        followType = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
        distSliderResponder = new Slider.ISlider() {
            @Override
            public void onChangeSliderValue(Slider slider){
                GuiCitadelPatreonConfig.this.setSliderValue(0, (float)slider.sliderValue);
            }
        };
        speedSliderResponder = new Slider.ISlider() {
            @Override
            public void onChangeSliderValue(Slider slider){
                GuiCitadelPatreonConfig.this.setSliderValue(1, (float)slider.sliderValue);
            }
        };
        heightSliderResponder = new Slider.ISlider() {
            @Override
            public void onChangeSliderValue(Slider slider){
                GuiCitadelPatreonConfig.this.setSliderValue(2, (float)slider.sliderValue);
            }
        };
    }

    private void setSliderValue(int i, float sliderValue) {
        boolean flag = false;
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
        if(i == 0){
            rotateDist = roundTo(sliderValue * 5F, 3);
            tag.putFloat("CitadelRotateDistance", rotateDist);
            distSlider.dragging = false;
        }else if(i == 1){
            rotateSpeed = roundTo(sliderValue * 5F, 3);
            tag.putFloat("CitadelRotateSpeed", rotateSpeed);
            speedSlider.dragging = false;
        }else{
            rotateHeight = roundTo(sliderValue * 2F, 3);
            tag.putFloat("CitadelRotateHeight", rotateHeight);
            heightSlider.dragging = false;

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
        this.addRenderableWidget(new Button(i - 100, j+ 120, 200, 20, CommonComponents.GUI_DONE, (p_213079_1_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }));
        this.addRenderableWidget(distSlider = new Slider(i - 150 / 2 - 25, j + 30, 150, 20, new TranslatableComponent("citadel.gui.orbit_dist").append(new TextComponent( ": ")), new TextComponent( ""), 0.125F, 5F, rotateDist, true, true, (p_214132_1_) -> {
        }, distSliderResponder){
        });
        this.addRenderableWidget(new Button(i - 150 / 2 + 135, j+ 30, 40, 20,  new TranslatableComponent("citadel.gui.reset"), (p_213079_1_) -> {
            this.setSliderValue(0, 0.4F);
            distSlider.sliderValue = 0.4F;
            distSlider.updateSlider();
        }));
        this.addRenderableWidget(speedSlider = new Slider(i - 150 / 2 - 25, j + 60, 150, 20, new TranslatableComponent("citadel.gui.orbit_speed").append(new TextComponent( ": ")), new TextComponent( ""), 0.0F, 5F, rotateSpeed, true, true, (p_214132_1_) -> {
        }, speedSliderResponder){
        });
        this.addRenderableWidget(new Button(i - 150 / 2 + 135, j+ 60, 40, 20,  new TranslatableComponent("citadel.gui.reset"), (p_213079_1_) -> {
            this.setSliderValue(1, 1F / 5F);
            speedSlider.sliderValue = 1F/5F;
            speedSlider.updateSlider();
        }));
        this.addRenderableWidget(heightSlider = new Slider(i - 150 / 2 - 25, j + 90, 150, 20, new TranslatableComponent("citadel.gui.orbit_height").append(new TextComponent( ": ")), new TextComponent( ""), 0.0F, 2F, rotateHeight, true, true, (p_214132_1_) -> {
        }, heightSliderResponder){
        });
        this.addRenderableWidget(new Button(i - 150 / 2 + 135, j+ 90, 40, 20,  new TranslatableComponent("citadel.gui.reset"), (p_213079_1_) -> {
            this.setSliderValue(2, 0.5F);
            heightSlider.sliderValue = 0.5F;
            heightSlider.updateSlider();
        }));
        distSlider.precision = 1;
        heightSlider.precision = 2;
        speedSlider.precision = 2;
        distSlider.updateSlider();
        heightSlider.updateSlider();
        speedSlider.updateSlider();
        this.addRenderableWidget(changeButton = new Button(i - 100, j, 200, 20, getTypeText(), (p_213079_1_) -> {
            this.followType = CitadelPatreonRenderer.getIdOfNext(followType);
            CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
            if(tag != null){
                tag.putString("CitadelFollowerType", followType);
                CitadelEntityData.setCitadelTag(Minecraft.getInstance().player, tag);
            }
            Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", tag, Minecraft.getInstance().player.getId()));
            changeButton.setMessage(getTypeText());

        }));

    }

    private  Component getTypeText(){
        return new TranslatableComponent("citadel.gui.follower_type").append(new TranslatableComponent("citadel.follower." + followType));
    }
}
