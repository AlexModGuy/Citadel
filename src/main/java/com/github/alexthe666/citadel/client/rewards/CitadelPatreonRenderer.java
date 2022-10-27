package com.github.alexthe666.citadel.client.rewards;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CitadelPatreonRenderer {
    private static final Map<String, CitadelPatreonRenderer> PATREON_RENDERER_MAP = new LinkedHashMap<String, CitadelPatreonRenderer>();

    public static CitadelPatreonRenderer get(String identifier) {
        return PATREON_RENDERER_MAP.get(identifier);
    }

    public static void register(String identifier, CitadelPatreonRenderer renderer) {
        PATREON_RENDERER_MAP.put(identifier, renderer);
    }

    public abstract void render(PoseStack matrixStackIn, MultiBufferSource buffer, int light, float partialTick, LivingEntity entity, float distanceIn, float rotateSpeed, float rotateHeight);

    public static String getIdOfNext(String identifier) {
        Object[] ids = PATREON_RENDERER_MAP.keySet().toArray();
        if(identifier.equals("none") && ids.length > 0){
            return (String)ids[0];
        }
        for (int i = 0; i < ids.length - 1; i++) {
           if(ids[i].equals(identifier)){
               return (String)ids[i+1];
           }
        }
        return "none";
    }

    public static String getDefault(){
        return "citadel";
    }
}
