package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.CitadelItemRenderProperties;
import com.github.alexthe666.citadel.client.CitadelItemstackRenderer;
import com.github.alexthe666.citadel.client.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.client.event.EventGetOutlineColor;
import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import com.github.alexthe666.citadel.client.gui.GuiCitadelBook;
import com.github.alexthe666.citadel.client.gui.GuiCitadelPatreonConfig;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.client.patreon.SpaceStationPatreonRenderer;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SkinCustomizationScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.io.IOException;
import java.util.concurrent.Callable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy extends ServerProxy {
    public static TabulaModel CITADEL_MODEL;
    private static final ResourceLocation CITADEL_TEXTURE = new ResourceLocation("citadel", "textures/patreon/citadel_model.png");
    private static final ResourceLocation CITADEL_TEXTURE_RED = new ResourceLocation("citadel", "textures/patreon/citadel_model_red.png");
    private static final ResourceLocation CITADEL_TEXTURE_GRAY = new ResourceLocation("citadel", "textures/patreon/citadel_model_gray.png");
    private static final String RICKROLL_URL = "http://techslides.com/demos/sample-videos/small.mp4";//"https://ia801602.us.archive.org/11/items/Rick_Astley_Never_Gonna_Give_You_Up/Rick_Astley_Never_Gonna_Give_You_Up.mp4";
    private static final ResourceLocation RICKROLL_LOCATION = new ResourceLocation("citadel:rickroll");
    public ClientProxy() {
        super();
    }

    public void onPreInit() {
        try {
            CITADEL_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/citadel/models/citadel_model"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CitadelPatreonRenderer.register("citadel", new SpaceStationPatreonRenderer(CITADEL_TEXTURE));
        CitadelPatreonRenderer.register("citadel_red", new SpaceStationPatreonRenderer(CITADEL_TEXTURE_RED));
        CitadelPatreonRenderer.register("citadel_gray", new SpaceStationPatreonRenderer(CITADEL_TEXTURE_GRAY));
    }

    @SubscribeEvent
    public void openScreen(ScreenEvent.InitScreenEvent event) {
        if (event.getScreen() instanceof SkinCustomizationScreen && Minecraft.getInstance().player != null) {
           try{
               String username = Minecraft.getInstance().player.getName().getString();
               if (Citadel.PATREONS.contains(username)) {
                   event.addListener(new Button(event.getScreen().width / 2 - 100, event.getScreen().height / 6 + 150, 200, 20, Component.translatable("citadel.gui.patreon_rewards_option").withStyle(ChatFormatting.GREEN), (p_213080_2_) -> {
                       Minecraft.getInstance().setScreen(new GuiCitadelPatreonConfig(event.getScreen(), Minecraft.getInstance().options));
                   }));
               }
           }catch (Exception e){
               e.printStackTrace();
           }
        }
    }

    @SubscribeEvent
    public void drawScreen(ScreenEvent.DrawScreenEvent event) {

    }

    @SubscribeEvent
    public void playerRender(RenderPlayerEvent.Post event) {
        PoseStack matrixStackIn = event.getPoseStack();
        String username = event.getPlayer().getName().getString();
        if (!event.getPlayer().isModelPartShown(PlayerModelPart.CAPE)) {
            return;
        }
        if (Citadel.PATREONS.contains(username)) {
            CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
            String rendererName = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
            if (!rendererName.equals("none")) {
                CitadelPatreonRenderer renderer = CitadelPatreonRenderer.get(rendererName);
                if (renderer != null) {
                    float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2F;
                    float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1;
                    float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1F;
                    renderer.render(matrixStackIn, event.getMultiBufferSource(), event.getPackedLight(), event.getPartialTick(), event.getEntityLiving(), distance, speed, height);
                }
            }
        }
    }

    @Override
    public void handleAnimationPacket(int entityId, int index) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            IAnimatedEntity entity = (IAnimatedEntity) player.level.getEntity(entityId);
            if (entity != null) {
                if (index == -1) {
                    entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
                } else {
                    entity.setAnimation(entity.getAnimations()[index]);
                }
                entity.setAnimationTick(0);
            }
        }
    }

    @Override
    public void handlePropertiesPacket(String propertyID, CompoundTag compound, int entityID) {
        if(compound == null){
            return;
        }
        Player player = Minecraft.getInstance().player;
        Entity entity = player.level.getEntity(entityID);
        if ((propertyID.equals("CitadelPatreonConfig") || propertyID.equals("CitadelTagUpdate")) && entity instanceof LivingEntity) {
            CitadelEntityData.setCitadelTag((LivingEntity) entity, compound);
        }
    }
    @Override
    public Object getISTERProperties() {
        return new CitadelItemRenderProperties();
    }

    @Override
    public void openBookGUI(ItemStack book) {
        Minecraft.getInstance().setScreen(new GuiCitadelBook(book));
    }

    @SubscribeEvent
    public void outlineColorTest(EventGetOutlineColor event) {
    //    event.setColor(0XEB3FFF);
    //    event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public void animateHandTest(EventPosePlayerHand event) {
     //   event.getModel().rightArm.xRot = (float)Math.PI / 2F;
     //   event.setResult(Event.Result.ALLOW);
    }
}
