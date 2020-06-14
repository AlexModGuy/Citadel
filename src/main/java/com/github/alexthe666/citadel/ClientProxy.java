package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.server.entity.EntityDataHandler;
import com.github.alexthe666.citadel.server.entity.EntityProperties;
import com.github.alexthe666.citadel.server.entity.IEntityData;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
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
    private static TabulaModel CITADEL_MODEL;

    public void onPreInit(){
        try {
            CITADEL_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/citadel/models/citadel_model"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void playerRender(RenderPlayerEvent.Post event) {
        MatrixStack matrixStackIn = event.getMatrixStack();
        String username = event.getPlayer().getName().getUnformattedComponentText();
        if(Citadel.PATREONS.contains(username)) {
            IVertexBuilder textureBuilder = event.getBuffers().getBuffer(RenderType.getEntityCutoutNoCull(CITADEL_TEXTURE));
            float tick = event.getEntity().ticksExisted - 1 + event.getPartialRenderTick();
            float bob = (float) (Math.sin(tick * 0.1F) * 1 * 0.05F - 1 * 0.05F);
            float scale = 0.4F;
            float rotation = MathHelper.wrapDegrees(tick % 360);
            matrixStackIn.push();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rotation));
            matrixStackIn.translate(0, event.getEntity().getHeight() + bob, event.getEntity().getWidth() + 1.75F + bob * 5);
            matrixStackIn.push();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rotation * 10));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(75));
            matrixStackIn.scale(scale, scale, scale);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
            CITADEL_MODEL.resetToDefaultPose();
            CITADEL_MODEL.render(matrixStackIn, textureBuilder, event.getLight(), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.pop();
            matrixStackIn.pop();
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


    @Override
    public void handlePropertiesPacket(String propertyID, CompoundNBT compound, int entityID) {
        PlayerEntity player = Minecraft.getInstance().player;
        Entity entity = player.world.getEntityByID(entityID);
        if (entity != null) {
            IEntityData<?> extendedProperties = EntityDataHandler.INSTANCE.getEntityData(entity, propertyID);
            if (extendedProperties instanceof EntityProperties) {
                EntityProperties<?> properties = (EntityProperties) extendedProperties;
                properties.loadTrackingSensitiveData(compound);
                properties.onSync();
            }
        }
    }

}
