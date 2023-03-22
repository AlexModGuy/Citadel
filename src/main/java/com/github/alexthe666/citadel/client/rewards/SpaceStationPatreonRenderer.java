package com.github.alexthe666.citadel.client.rewards;

import com.github.alexthe666.citadel.ClientProxy;
import com.github.alexthe666.citadel.client.texture.CitadelTextureManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class SpaceStationPatreonRenderer extends CitadelPatreonRenderer {


    private static final ResourceLocation CITADEL_TEXTURE = new ResourceLocation("citadel", "textures/patreon/citadel_model.png");
    private static final ResourceLocation CITADEL_LIGHTS_TEXTURE = new ResourceLocation("citadel", "textures/patreon/citadel_model_glow.png");
    private final ResourceLocation resourceLocation;
    private int[] colors;

    public SpaceStationPatreonRenderer(ResourceLocation resourceLocation, int[] colors) {
        this.resourceLocation = resourceLocation;
        this.colors = colors;
    }


    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource buffer, int light, float partialTick, LivingEntity entity, float distanceIn, float rotateSpeed, float rotateHeight) {
        float tick = entity.tickCount + partialTick;
        float bob = (float) (Math.sin(tick * 0.1F) * 1 * 0.05F - 1 * 0.05F);
        float scale = 0.4F;
        float rotation = Mth.wrapDegrees((tick * rotateSpeed) % 360);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
        matrixStackIn.translate(0, entity.getBbHeight() + bob + (rotateHeight - 1F), entity.getBbWidth() * distanceIn);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(75));
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation * 10));
        ClientProxy.CITADEL_MODEL.resetToDefaultPose();
        ClientProxy.CITADEL_MODEL.renderToBuffer(matrixStackIn, buffer.getBuffer(RenderType.entityCutoutNoCull(CitadelTextureManager.getColorMappedTexture(resourceLocation, CITADEL_TEXTURE, colors))), light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        ClientProxy.CITADEL_MODEL.renderToBuffer(matrixStackIn, buffer.getBuffer(RenderType.eyes(CITADEL_LIGHTS_TEXTURE)), light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }
}
