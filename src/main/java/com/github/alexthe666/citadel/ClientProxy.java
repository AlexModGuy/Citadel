package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.CitadelItemRenderProperties;
import com.github.alexthe666.citadel.client.event.*;
import com.github.alexthe666.citadel.client.gui.GuiCitadelCapesConfig;
import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import com.github.alexthe666.citadel.client.rewards.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.client.gui.GuiCitadelBook;
import com.github.alexthe666.citadel.client.gui.GuiCitadelPatreonConfig;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.client.rewards.SpaceStationPatreonRenderer;
import com.github.alexthe666.citadel.client.texture.CitadelTextureManager;
import com.github.alexthe666.citadel.client.texture.VideoFrameTexture;
import com.github.alexthe666.citadel.client.video.Video;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.io.IOException;

import static org.jcodec.containers.mkv.MKVType.Video;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy extends ServerProxy {
    public static TabulaModel CITADEL_MODEL;
    public static final String RICKROLL_URL = "https://ia801602.us.archive.org/11/items/Rick_Astley_Never_Gonna_Give_You_Up/Rick_Astley_Never_Gonna_Give_You_Up.mp4";
    private static final ResourceLocation RICKROLL_LOCATION = new ResourceLocation("citadel:rickroll.mp4");
    public static boolean hideFollower = false;
    private Video rickrollVideo = null;
    public ClientProxy() {
        super();
    }

    public void onPreInit() {
        try {
            CITADEL_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/citadel/models/citadel_model"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CitadelPatreonRenderer.register("citadel", new SpaceStationPatreonRenderer(new ResourceLocation("citadel:patreon_space_station"), new int[]{}));
        CitadelPatreonRenderer.register("citadel_red", new SpaceStationPatreonRenderer(new ResourceLocation("citadel:patreon_space_station_red"), new int[]{0XB25048, 0X9D4540, 0X7A3631, 0X71302A}));
        CitadelPatreonRenderer.register("citadel_gray", new SpaceStationPatreonRenderer(new ResourceLocation("citadel:patreon_space_station_gray"), new int[]{0XA0A0A0, 0X888888, 0X646464, 0X575757}));
    }

    @SubscribeEvent
    public void screenOpen(ScreenEvent.Init event) {
        if (event.getScreen() instanceof SkinCustomizationScreen && Minecraft.getInstance().player != null) {
           try{
               String username = Minecraft.getInstance().player.getName().getString();
               int height = -20;
               if (Citadel.PATREONS.contains(username)) {
                   event.addListener(new Button(event.getScreen().width / 2 - 100, event.getScreen().height / 6 + 150 + height, 200, 20, Component.translatable("citadel.gui.patreon_rewards_option").withStyle(ChatFormatting.GREEN), (p_213080_2_) -> {
                       Minecraft.getInstance().setScreen(new GuiCitadelPatreonConfig(event.getScreen(), Minecraft.getInstance().options));
                   }));
                   height += 25;
               }
               if (!CitadelCapes.getCapesFor(Minecraft.getInstance().player.getUUID()).isEmpty()) {
                   event.addListener(new Button(event.getScreen().width / 2 - 100, event.getScreen().height / 6 + 150 + height, 200, 20, Component.translatable("citadel.gui.capes_option").withStyle(ChatFormatting.GREEN), (p_213080_2_) -> {
                       Minecraft.getInstance().setScreen(new GuiCitadelCapesConfig(event.getScreen(), Minecraft.getInstance().options));
                   }));
                   height += 25;
               }
           }catch (Exception e){
               e.printStackTrace();
           }
        }
    }

    @SubscribeEvent
    public void screenRender(ScreenEvent.Render event) {
        if(event.getScreen() instanceof TitleScreen && Citadel.isAprilFools()){
            if(rickrollVideo == null){
                VideoFrameTexture videoFrameTexture = CitadelTextureManager.getVideoTexture(RICKROLL_LOCATION, 640, 480);
                rickrollVideo = new Video(RICKROLL_URL, RICKROLL_LOCATION, videoFrameTexture, 25, false);
                rickrollVideo.setRepeat(true);
            }else{
                rickrollVideo.setPaused(false);
                int screenHeight = event.getScreen().height;
                int screenWidth = event.getScreen().width;
                rickrollVideo.update();
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, RICKROLL_LOCATION);
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferbuilder.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
                bufferbuilder.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
                bufferbuilder.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
                bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
                tesselator.end();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }else if(rickrollVideo != null){
            rickrollVideo.setPaused(true);
        }
    }

    @SubscribeEvent
    public void playerRender(RenderPlayerEvent.Post event) {
        PoseStack matrixStackIn = event.getPoseStack();
        String username = event.getEntity().getName().getString();
        if (!event.getEntity().isModelPartShown(PlayerModelPart.CAPE)) {
            return;
        }
        if (Citadel.PATREONS.contains(username)) {
            CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
            String rendererName = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
            if (!rendererName.equals("none") && !hideFollower) {
                CitadelPatreonRenderer renderer = CitadelPatreonRenderer.get(rendererName);
                if (renderer != null) {
                    float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2F;
                    float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1;
                    float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1F;
                    renderer.render(matrixStackIn, event.getMultiBufferSource(), event.getPackedLight(), event.getPartialTick(), event.getEntity(), distance, speed, height);
                }
            }
        }
    }

    @SubscribeEvent
    public void onOpenGui(ScreenEvent.Opening event) {
        if (ServerConfig.skipWarnings) {
            try{
                if (event.getScreen() instanceof BackupConfirmScreen) {
                    BackupConfirmScreen confirmBackupScreen = (BackupConfirmScreen) event.getScreen();
                    String name = "";
                    MutableComponent title = Component.translatable("selectWorld.backupQuestion.experimental");

                    if (confirmBackupScreen.getTitle().equals(title)) {
                        confirmBackupScreen.listener.proceed(false, true);
                    }
                }
                if (event.getScreen() instanceof ConfirmScreen) {
                    ConfirmScreen confirmScreen = (ConfirmScreen) event.getScreen();
                    MutableComponent title = Component.translatable("selectWorld.backupQuestion.experimental");
                    String name = "";
                    if (confirmScreen.getTitle().equals(title)) {
                        confirmScreen.callback.accept(true);
                    }
                }
            }catch (Exception e){
                Citadel.LOGGER.warn("Citadel couldn't skip world loadings");
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onRenderSplashTextBefore(EventRenderSplashText.Pre event) {
        if(Citadel.isAprilFools() && rickrollVideo != null && rickrollVideo.getLastFrame() > 35){
            event.setResult(Event.Result.ALLOW);
            float hue = (System.currentTimeMillis() % 6000) / 6000f;
            event.getPoseStack().mulPose(Vector3f.ZP.rotationDegrees((float)Math.sin(hue * Math.PI) * 360));
            event.setSplashText("Never gonna give you up!");
            int rainbow = Color.HSBtoRGB(hue, 0.6f, 1);
            event.setSplashTextColor(rainbow);
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

}
