package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.client.shader.CitadelInternalShaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerShaders(final RegisterShadersEvent e) {
        try {
            e.registerShader(new ShaderInstance(e.getResourceProvider(), ResourceLocation.parse("citadel:rendertype_rainbow_aura"), DefaultVertexFormat.POSITION_TEX_COLOR), CitadelInternalShaders::setRenderTypeRainbowAura);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
