package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.*;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.io.IOException;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy extends ServerProxy {
    private static final ResourceLocation CITADEL_TEXTURE = new ResourceLocation("citadel", "textures/citadel_model.png");
    private static final ResourceLocation CITADEL_GLOW_TEXTURE = new ResourceLocation("citadel", "textures/citadel_model_glow.png");
    private static AdvancedEntityModel CITADEL_MODEL;

    public void onPreInit(){
        try {
            CITADEL_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/citadel/models/citadel_model"), new ITabulaModelAnimator() {
                @Override
                public void setRotationAngles(TabulaModel model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void playerRender(RenderPlayerEvent.Post event) {
        String username = event.getEntityPlayer().getName().getUnformattedComponentText();
        if(Citadel.PATREONS.contains(username)) {
            GlStateManager.disableCull();
            float tick = event.getEntity().ticksExisted - 1 + event.getPartialRenderTick();
            float bob = (float) (Math.sin(tick * 0.1F) * 1 * 0.05F - 1 * 0.05F);
            float scale = 0.4F;
            GlStateManager.pushMatrix();
            GlStateManager.translated(event.getX(), event.getY(), event.getZ());
            float rotation = MathHelper.wrapDegrees(tick % 360);
            GlStateManager.rotatef(rotation, 0, 1, 0);
            GlStateManager.translatef(0, event.getEntity().getHeight() + bob, event.getEntity().getWidth() + 1.75F + bob * 5);
            GlStateManager.pushMatrix();
            event.getRenderer().bindTexture(CITADEL_TEXTURE);
            GlStateManager.rotatef(rotation * 10, 0, 1, 0);
            GlStateManager.rotatef(75, 1, 0, 0);
            GlStateManager.scalef(scale, scale, scale);
            GlStateManager.rotatef(90, 1, 0, 0);
            CITADEL_MODEL.render(event.getEntityLiving(), 0, 0, event.getPartialRenderTick(), 0, 0, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            GlStateManager.disableLighting();
            GlStateManager.depthFunc(514);
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 0.0F);
            GlStateManager.enableLighting();

            event.getRenderer().bindTexture(CITADEL_GLOW_TEXTURE);
            GlStateManager.rotatef(rotation * 10, 0, 1, 0);
            GlStateManager.rotatef(75, 1, 0, 0);
            GlStateManager.scalef(scale, scale, scale);
            GlStateManager.rotatef(90, 1, 0, 0);
            CITADEL_MODEL.render(event.getEntityLiving(), 0, 0, event.getPartialRenderTick(), 0, 0, 0.0625F);

            GlStateManager.disableBlend();
            GlStateManager.depthFunc(515);

            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
            GlStateManager.enableCull();
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
