package com.github.alexthe666.citadel.client.patreon;

import com.github.alexthe666.citadel.ClientProxy;
import com.github.alexthe666.citadel.client.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.client.render.LightningBoltData;
import com.github.alexthe666.citadel.client.render.LightningRender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;

public class SpaceStationPatreonRenderer extends CitadelPatreonRenderer {


    private final ResourceLocation texture;

    public SpaceStationPatreonRenderer(ResourceLocation texture) {
        this.texture = texture;
    }


    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource buffer, int light, float partialTick, LivingEntity entity, float distanceIn, float rotateSpeed, float rotateHeight) {
        VertexConsumer textureBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        float tick = entity.tickCount + partialTick;
        float bob = (float) (Math.sin(tick * 0.1F) * 1 * 0.05F - 1 * 0.05F);
        float scale = 0.4F;

        float rotation = Mth.wrapDegrees((tick * rotateSpeed) % 360);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rotation));
        matrixStackIn.translate(0, entity.getBbHeight() + bob + (rotateHeight - 1F), entity.getBbWidth() * distanceIn);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(75));
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rotation * 10));
        ClientProxy.CITADEL_MODEL.resetToDefaultPose();
        ClientProxy.CITADEL_MODEL.renderToBuffer(matrixStackIn, textureBuilder, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }
}
