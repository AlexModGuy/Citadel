package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.item.component.CustomRenderDisplay;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CitadelItemstackRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation DEFAULT_ICON_TEXTURE = ResourceLocation.parse("citadel:textures/gui/book/icon_default.png");

    private static List<Holder.Reference<MobEffect>> mobEffectList = null;

    public CitadelItemstackRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
        float ticksExisted = Util.getMillis() / 50F + partialTicks;
        int id = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.getId();
        if (stack.getItem() == Citadel.FANCY_ITEM.get()) {
            Random random = new Random();
            boolean animateAnyways = false;

            CustomRenderDisplay display = stack.getOrDefault(Citadel.CUSTOM_RENDER_DISPLAY, CustomRenderDisplay.DEFAULT);

            if (!stack.has(Citadel.CUSTOM_RENDER_DISPLAY)) {
                animateAnyways = true;
            }

            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5f, 0.5f);
            if(display.shake()) {
                poseStack.translate((random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F);
            }
            if(animateAnyways || display.bob()){
                poseStack.translate(0, 0.05F + 0.1F * Mth.sin(0.3F * ticksExisted), 0);
            }
            if(display.spin()){
                poseStack.mulPose(Axis.YP.rotationDegrees(6 * ticksExisted));
            }
            if(animateAnyways || display.zoom()) {
                float scale = (float) (1F + 0.15F * (Math.sin(ticksExisted * 0.3F) + 1F));
                poseStack.scale(scale, scale, scale);
            }
            if(display.scale() != 1.0F){
                float scale = display.scale();
                poseStack.scale(scale, scale, scale);
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(display.item(), displayContext, packedLight, packedOverlay, poseStack, buffer, null, id);
            poseStack.popPose();
        }
        if (stack.getItem() == Citadel.EFFECT_ITEM.get()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
           // RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();
            Holder<MobEffect> effect;

            if (stack.has(Citadel.DISPLAY_EFFECT)) {
                effect = BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(stack.get(Citadel.DISPLAY_EFFECT));
            } else {
                if (mobEffectList == null) {
                    mobEffectList = BuiltInRegistries.MOB_EFFECT.holders().toList();
                }
                int size = mobEffectList.size();
                int time = (int) (Util.getMillis() / 500);
                effect = mobEffectList.get(time % size);
                if (effect == null) {
                    effect = MobEffects.MOVEMENT_SPEED.getDelegate();
                }
            }

            MobEffectTextureManager potionspriteuploader = Minecraft.getInstance().getMobEffectTextures();
            poseStack.pushPose();
            poseStack.translate(0, 0, 0.5F);
            TextureAtlasSprite sprite = potionspriteuploader.get(effect);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, sprite.atlasLocation());
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            Matrix4f mx = poseStack.last().pose();
            int br = 255;
            bufferbuilder.addVertex(mx, (float) 1, (float) 1, (float) 0).setUv(sprite.getU1(), sprite.getV0()).setColor(br, br, br, 255).setLight(packedLight);
            bufferbuilder.addVertex(mx, (float) 0, (float) 1, (float) 0).setUv(sprite.getU0(), sprite.getV0()).setColor(br, br, br, 255).setLight(packedLight);
            bufferbuilder.addVertex(mx, (float) 0, (float) 0, (float) 0).setUv(sprite.getU0(), sprite.getV1()).setColor(br, br, br, 255).setLight(packedLight);
            bufferbuilder.addVertex(mx, (float) 1, (float) 0, (float) 0).setUv(sprite.getU1(), sprite.getV1()).setColor(br, br, br, 255).setLight(packedLight);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            poseStack.popPose();
        }
        if (stack.getItem() == Citadel.ICON_ITEM.get()) {
            ResourceLocation texture = stack.getOrDefault(Citadel.ICON_LOCATION, DEFAULT_ICON_TEXTURE);
            poseStack.pushPose();
            poseStack.translate(0, 0, 0.5F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, texture);
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            Matrix4f mx = poseStack.last().pose();
            int br = 255;
            bufferbuilder.addVertex(mx, (float) 1, (float) 1, (float) 0).setUv(1, 0).setColor(br, br, br, 255).setLight(packedLight);
            bufferbuilder.addVertex(mx, (float) 0, (float) 1, (float) 0).setUv(0, 0).setColor(br, br, br, 255).setLight(packedLight);
            bufferbuilder.addVertex(mx, (float) 0, (float) 0, (float) 0).setUv(0, 1).setColor(br, br, br, 255).setLight(packedLight);
            bufferbuilder.addVertex(mx, (float) 1, (float) 0, (float) 0).setUv(1, 1).setColor(br, br, br, 255).setLight(packedLight);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            poseStack.popPose();
        }
    }


}
