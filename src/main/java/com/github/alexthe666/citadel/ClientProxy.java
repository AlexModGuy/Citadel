package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.CitadelItemRenderProperties;
import com.github.alexthe666.citadel.client.event.*;
import com.github.alexthe666.citadel.client.game.Tetris;
import com.github.alexthe666.citadel.client.gui.GuiCitadelCapesConfig;
import com.github.alexthe666.citadel.client.render.CitadelLecternRenderer;
import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import com.github.alexthe666.citadel.client.rewards.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.client.gui.GuiCitadelBook;
import com.github.alexthe666.citadel.client.gui.GuiCitadelPatreonConfig;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.client.rewards.SpaceStationPatreonRenderer;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.github.alexthe666.citadel.client.texture.CitadelTextureManager;
import com.github.alexthe666.citadel.client.texture.VideoFrameTexture;
import com.github.alexthe666.citadel.client.tick.ClientTickRateTracker;
import com.github.alexthe666.citadel.client.video.Video;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.item.ItemWithHoverAnimation;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.event.EventChangeEntityTickRate;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy extends ServerProxy {
    public static TabulaModel CITADEL_MODEL;
    public static boolean hideFollower = false;
    private Map<ItemStack, Float> prevMouseOverProgresses = new HashMap<>();

    private Map<ItemStack, Float> mouseOverProgresses = new HashMap<>();
    private ItemStack lastHoveredItem = null;
    private Tetris aprilFoolsTetrisGame = null;

    public ClientProxy() {
        super();
    }

    public void onClientInit() {
        try {
            CITADEL_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/citadel/models/citadel_model"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockEntityRenderers.register(Citadel.LECTERN_BE.get(), CitadelLecternRenderer::new);
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
                   Button button1 = Button.builder(Component.translatable("citadel.gui.patreon_rewards_option").withStyle(ChatFormatting.GREEN), (p_213080_2_) -> {
                       Minecraft.getInstance().setScreen(new GuiCitadelPatreonConfig(event.getScreen(), Minecraft.getInstance().options));
                   }).size(200, 20).pos(event.getScreen().width / 2 - 100, event.getScreen().height / 6 + 150 + height).build();
                   event.addListener(button1);
                   height += 25;
               }
               if (!CitadelCapes.getCapesFor(Minecraft.getInstance().player.getUUID()).isEmpty()) {
                   Button button2 = Button.builder(Component.translatable("citadel.gui.capes_option").withStyle(ChatFormatting.GREEN), (p_213080_2_) -> {
                       Minecraft.getInstance().setScreen(new GuiCitadelCapesConfig(event.getScreen(), Minecraft.getInstance().options));
                   }).size(200, 20).pos(event.getScreen().width / 2 - 100, event.getScreen().height / 6 + 150 + height).build();
                   event.addListener(button2);
                   height += 25;
               }
           }catch (Exception e){
               e.printStackTrace();
           }
        }
    }

    @SubscribeEvent
    public void screenRender(ScreenEvent.Render event) {
        if(event.getScreen() instanceof TitleScreen && CitadelConstants.isAprilFools()) {
            if(aprilFoolsTetrisGame == null){
                aprilFoolsTetrisGame = new Tetris();
            }else{
                aprilFoolsTetrisGame.render((TitleScreen) event.getScreen(), event.getPoseStack(), event.getPartialTick());
            }
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
    public void renderSplashTextBefore(EventRenderSplashText.Pre event) {
        if(CitadelConstants.isAprilFools() && aprilFoolsTetrisGame != null){
            event.setResult(Event.Result.ALLOW);
            float hue = (System.currentTimeMillis() % 6000) / 6000f;
            event.getPoseStack().mulPose(Axis.ZP.rotationDegrees((float)Math.sin(hue * Math.PI) * 360));
            if(!aprilFoolsTetrisGame.isStarted()){
                event.setSplashText("Psst... press 'T' ;)");
            }else{
                event.setSplashText("");
            }
            int rainbow = Color.HSBtoRGB(hue, 0.6f, 1);
            event.setSplashTextColor(rainbow);
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.START && !isGamePaused()){
            ClientTickRateTracker.getForClient(Minecraft.getInstance()).masterTick();
            tickMouseOverAnimations();
        }
        if(event.type == TickEvent.Type.CLIENT && event.phase == TickEvent.Phase.START && !isGamePaused() && CitadelConstants.isAprilFools()) {
            if(aprilFoolsTetrisGame != null){
                if(Minecraft.getInstance().screen instanceof TitleScreen){
                    aprilFoolsTetrisGame.tick();
                }else{
                    aprilFoolsTetrisGame.reset();
                }
            }
        }
    }

    private void tickMouseOverAnimations() {
        prevMouseOverProgresses.putAll(mouseOverProgresses);
        if (lastHoveredItem != null) {
            float prev = mouseOverProgresses.getOrDefault(lastHoveredItem, 0F);
            float maxTime = 5F;
            if(lastHoveredItem.getItem() instanceof ItemWithHoverAnimation hoverOver){
                maxTime = hoverOver.getMaxHoverOverTime(lastHoveredItem);
            }
            if (prev < maxTime) {
                mouseOverProgresses.put(lastHoveredItem, prev + 1);
            }
        }

        if (!mouseOverProgresses.isEmpty()) {
            Iterator<Map.Entry<ItemStack, Float>> it = mouseOverProgresses.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<ItemStack, Float> next = it.next();
                float progress = next.getValue();
                if (lastHoveredItem == null || next.getKey() != lastHoveredItem) {
                    if (progress == 0) {
                        it.remove();
                    } else {
                        next.setValue(progress - 1);
                    }
                }
            }
        }
        lastHoveredItem = null;
    }

    @SubscribeEvent
    public void renderTooltipColor(RenderTooltipEvent.Color event) {
        if (event.getItemStack().getItem() instanceof ItemWithHoverAnimation hoverOver && hoverOver.canHoverOver(event.getItemStack())) {
            lastHoveredItem = event.getItemStack();
        } else {
            lastHoveredItem = null;
        }
    }

    @Override
    public float getMouseOverProgress(ItemStack itemStack){
        float prev = prevMouseOverProgresses.getOrDefault(itemStack, 0F);
        float current = mouseOverProgresses.getOrDefault(itemStack, 0F);
        float lerped = prev + (current - prev) * Minecraft.getInstance().getFrameTime();
        float maxTime = 5F;
        if(itemStack.getItem() instanceof ItemWithHoverAnimation hoverOver){
            maxTime = hoverOver.getMaxHoverOverTime(itemStack);
        }
        return lerped / maxTime;
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
    public void handleClientTickRatePacket(CompoundTag compound) {
        ClientTickRateTracker.getForClient(Minecraft.getInstance()).syncFromServer(compound);
    }

    @Override
    public Object getISTERProperties() {
        return new CitadelItemRenderProperties();
    }

    @Override
    public void openBookGUI(ItemStack book) {
        Minecraft.getInstance().setScreen(new GuiCitadelBook(book));
    }

    public boolean isGamePaused() {
        return Minecraft.getInstance().isPaused();
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    public boolean canEntityTickClient(Level level, Entity entity) {
        ClientTickRateTracker tracker = ClientTickRateTracker.getForClient(Minecraft.getInstance());
        if(tracker.isTickingHandled(entity)){
            return false;
        }else if(!tracker.hasNormalTickRate(entity)){
            EventChangeEntityTickRate event = new EventChangeEntityTickRate(entity, tracker.getEntityTickLengthModifier(entity));
            MinecraftForge.EVENT_BUS.post(event);
            if(event.isCanceled()){
                return true;
            }else{
                tracker.addTickBlockedEntity(entity);
                return false;
            }
        }
        return true;
    }

    @SubscribeEvent
    public void postRenderStage(RenderLevelStageEvent event) {
    }
}
