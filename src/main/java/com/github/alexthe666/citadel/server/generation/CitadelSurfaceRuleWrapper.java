package com.github.alexthe666.citadel.server.generation;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record CitadelSurfaceRuleWrapper(SurfaceRules.RuleSource wrappedRule) implements SurfaceRules.RuleSource {

    public static final KeyDispatchDataCodec<CitadelSurfaceRuleWrapper> CODEC = KeyDispatchDataCodec.of(SurfaceRules.RuleSource.CODEC.xmap(CitadelSurfaceRuleWrapper::new, CitadelSurfaceRuleWrapper::wrappedRule).fieldOf("wrapped_rule"));

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        return wrappedRule.apply(context);
    }
}
