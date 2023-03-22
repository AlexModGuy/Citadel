package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.Citadel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CitadelItemstackRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation DEFAULT_ICON_TEXTURE = new ResourceLocation("citadel:textures/gui/book/icon_default.png");
    private static final Map<String, ResourceLocation> LOADED_ICONS = new HashMap<>();

    private static List<MobEffect> mobEffectList = null;

    public CitadelItemstackRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        float partialTicks = Minecraft.getInstance().getFrameTime();
        float ticksExisted = Util.getMillis() / 50F + partialTicks;
        int id = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.getId();
        if (stack.getItem() == Citadel.FANCY_ITEM.get()) {
            Random random = new Random();
            boolean animateAnyways = false;
            ItemStack toRender = null;
            if (stack.getTag() != null && stack.getTag().contains("DisplayItem")) {
                String displayID = stack.getTag().getString("DisplayItem");
                toRender = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(displayID)));
                if (stack.getTag().contains("DisplayItemNBT")) {
                    try {
                        toRender.setTag(stack.getTag().getCompound("DisplayItemNBT"));
                    } catch (Exception e) {
                        toRender = new ItemStack(Items.BARRIER);
                    }
                }
            }
            if (toRender == null) {
                animateAnyways = true;
                toRender = new ItemStack(Items.BARRIER);
            }
            matrixStack.pushPose();
            matrixStack.translate(0.5F, 0.5f, 0.5f);
            if(stack.getTag() != null && stack.getTag().contains("DisplayShake") && stack.getTag().getBoolean("DisplayShake")) {
                matrixStack.translate((random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F);
            }
            if(animateAnyways || stack.getTag() != null && stack.getTag().contains("DisplayBob") && stack.getTag().getBoolean("DisplayBob")){
                matrixStack.translate(0, 0.05F + 0.1F * Mth.sin(0.3F * ticksExisted), 0);
            }
            if(stack.getTag() != null && stack.getTag().contains("DisplaySpin") && stack.getTag().getBoolean("DisplaySpin")){
                matrixStack.mulPose(Axis.YP.rotationDegrees(6 * ticksExisted));
            }
            if(animateAnyways || stack.getTag() != null && stack.getTag().contains("DisplayZoom") && stack.getTag().getBoolean("DisplayZoom")) {
                float scale = (float) (1F + 0.15F * (Math.sin(ticksExisted * 0.3F) + 1F));
                matrixStack.scale(scale, scale, scale);
            }
            if(stack.getTag() != null && stack.getTag().contains("DisplayScale") && stack.getTag().getFloat("DisplayScale") != 1.0F){
                float scale = stack.getTag().getFloat("DisplayScale");
                matrixStack.scale(scale, scale, scale);
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(toRender, transformType, combinedLight, combinedOverlay, matrixStack, buffer, null, id);
            matrixStack.popPose();
        }
        if (stack.getItem() == Citadel.EFFECT_ITEM.get()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
           // RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();
            MobEffect effect;
            if (stack.getTag() != null && stack.getTag().contains("DisplayEffect")) {
                String displayID = stack.getTag().getString("DisplayEffect");
                effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(displayID));
            } else {
                if(mobEffectList == null){
                    mobEffectList = ForgeRegistries.MOB_EFFECTS.getValues().stream().toList();
                }
                int size = mobEffectList.size();
                int time = (int) (Util.getMillis() / 500);
                effect = mobEffectList.get(time % size);
                if (effect == null) {
                    effect = MobEffects.MOVEMENT_SPEED;
                }
            }
            if (effect == null) {
                effect = MobEffects.MOVEMENT_SPEED;
            }
            MobEffectTextureManager potionspriteuploader = Minecraft.getInstance().getMobEffectTextures();
            matrixStack.pushPose();
            matrixStack.translate(0, 0, 0.5F);
            TextureAtlasSprite sprite = potionspriteuploader.get(effect);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, sprite.atlasLocation());
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            Matrix4f mx = matrixStack.last().pose();
            int br = 255;
            bufferbuilder.vertex(mx, (float) 1, (float) 1, (float) 0).uv(sprite.getU1(), sprite.getV0()).color(br, br, br, 255).uv2(combinedLight).endVertex();
            bufferbuilder.vertex(mx, (float) 0, (float) 1, (float) 0).uv(sprite.getU0(), sprite.getV0()).color(br, br, br, 255).uv2(combinedLight).endVertex();
            bufferbuilder.vertex(mx, (float) 0, (float) 0, (float) 0).uv(sprite.getU0(), sprite.getV1()).color(br, br, br, 255).uv2(combinedLight).endVertex();
            bufferbuilder.vertex(mx, (float) 1, (float) 0, (float) 0).uv(sprite.getU1(), sprite.getV1()).color(br, br, br, 255).uv2(combinedLight).endVertex();
            tessellator.end();
            matrixStack.popPose();
        }
        if (stack.getItem() == Citadel.ICON_ITEM.get()) {
            ResourceLocation texture = DEFAULT_ICON_TEXTURE;
            if (stack.getTag() != null && stack.getTag().contains("IconLocation")) {
                String iconLocationStr = stack.getTag().getString("IconLocation");
                if(LOADED_ICONS.containsKey(iconLocationStr)){
                    texture = LOADED_ICONS.get(iconLocationStr);
                }else{
                    texture = new ResourceLocation(iconLocationStr);
                    LOADED_ICONS.put(iconLocationStr, texture);
                }
            }
            matrixStack.pushPose();
            matrixStack.translate(0, 0, 0.5F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, texture);
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            Matrix4f mx = matrixStack.last().pose();
            int br = 255;
            bufferbuilder.vertex(mx, (float) 1, (float) 1, (float) 0).uv(1, 0).color(br, br, br, 255).uv2(combinedLight).endVertex();
            bufferbuilder.vertex(mx, (float) 0, (float) 1, (float) 0).uv(0, 0).color(br, br, br, 255).uv2(combinedLight).endVertex();
            bufferbuilder.vertex(mx, (float) 0, (float) 0, (float) 0).uv(0, 1).color(br, br, br, 255).uv2(combinedLight).endVertex();
            bufferbuilder.vertex(mx, (float) 1, (float) 0, (float) 0).uv(1, 1).color(br, br, br, 255).uv2(combinedLight).endVertex();
            tessellator.end();
            matrixStack.popPose();
        }
    }


}
