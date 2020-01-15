package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.obj.IModelObj;
import com.github.alexthe666.citadel.client.model.obj.ObjModelLoader;
import com.github.alexthe666.citadel.client.model.obj.WavefrontModelLoader;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends ServerProxy {
    private static final ResourceLocation CITADEL_TEXTURE = new ResourceLocation("citadel", "textures/citadel_model.png");
    private static IModelObj CITADEL_MODEL;

    public void onPreInit(){
       CITADEL_MODEL = WavefrontModelLoader.loadModel(new ResourceLocation("citadel", "models/citadel_model.obj"));
    }

    @SubscribeEvent
    public void playerRender(RenderPlayerEvent.Post event) {
        String username = event.getEntityPlayer().getName().getUnformattedComponentText();
        if(Citadel.PATREONS.contains(username)) {
            float tick = event.getEntity().ticksExisted - 1 + event.getPartialRenderTick();
            float bob = (float) (Math.sin(tick * 0.1F) * 1 * 0.05F - 1 * 0.05F);
            GlStateManager.pushMatrix();
            GlStateManager.translated(event.getX(), event.getY(), event.getZ());
            float rotation = MathHelper.wrapDegrees(tick % 360);
            GlStateManager.rotatef(rotation, 0, 1, 0);
            GlStateManager.translatef(0, event.getEntity().getHeight() + bob, event.getEntity().getWidth() + 1.75F + bob * 5);
            GlStateManager.pushMatrix();
            event.getRenderer().bindTexture(CITADEL_TEXTURE);
            GlStateManager.disableLighting();
            GlStateManager.rotatef(rotation * 10, 0, 1, 0);
            GlStateManager.rotatef(75, 1, 0, 0);
            GlStateManager.scalef(0.35F, 0.35F, 0.35F);
            CITADEL_MODEL.renderAll();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void handleAnimationPacket(int entityId, int index){
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            IAnimatedEntity entity = (IAnimatedEntity) player.world.getEntityByID(entityId);
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

}
