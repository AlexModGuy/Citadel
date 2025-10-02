package com.github.alexthe666.citadel.client.shader;

import com.github.alexthe666.citadel.ClientProxy;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class CitadelShaderRenderTypes extends RenderType {

    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_RAINBOW_AURA_SHADER = new RenderStateShard.ShaderStateShard(CitadelInternalShaders::getRenderTypeRainbowAura);
    protected static final RenderStateShard.OutputStateShard RAINBOW_AURA_OUTPUT = new RenderStateShard.OutputStateShard("rainbow_aura_target", () -> {
        RenderTarget target = PostEffectRegistry.getRenderTargetFor(ClientProxy.RAINBOW_AURA_POST_SHADER);
        if (target != null) {
            target.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
            target.bindWrite(false);
        }
    }, () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false));

    private CitadelShaderRenderTypes(String s, VertexFormat format, VertexFormat.Mode mode, int i, boolean b1, boolean b2, Runnable runnable1, Runnable runnable2) {
        super(s, format, mode, i, b1, b2, runnable1, runnable2);
    }

    public static RenderType getRainbowAura(ResourceLocation locationIn) {
        return create("rainbow_aura", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_RAINBOW_AURA_SHADER)
                .setCullState(NO_CULL)
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setOutputState(RAINBOW_AURA_OUTPUT)
                .createCompositeState(true));
    }
}
