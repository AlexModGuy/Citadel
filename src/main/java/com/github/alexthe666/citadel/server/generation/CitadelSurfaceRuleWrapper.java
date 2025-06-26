package com.github.alexthe666.citadel.server.generation;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.storage.LevelStorageSource;

public record CitadelSurfaceRuleWrapper(SurfaceRules.RuleSource vanillaRules,
                                        SurfaceRules.RuleSource citadelRules) implements SurfaceRules.RuleSource {

    public static final KeyDispatchDataCodec<CitadelSurfaceRuleWrapper> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((builder) -> builder.group(SurfaceRules.RuleSource.CODEC.fieldOf("vanilla_rules").forGetter(CitadelSurfaceRuleWrapper::vanillaRules), SurfaceRules.RuleSource.CODEC.fieldOf("citadel_rules").forGetter(CitadelSurfaceRuleWrapper::citadelRules)).apply(builder, CitadelSurfaceRuleWrapper::new)));

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        return new CitadelSurfaceRule(context, this.vanillaRules.apply(context), this.citadelRules.apply(context));
    }

    record CitadelSurfaceRule(SurfaceRules.Context context, SurfaceRules.SurfaceRule vanillaRule,
                              SurfaceRules.SurfaceRule citadelRule) implements SurfaceRules.SurfaceRule {
        public BlockState tryApply(int x, int y, int z) {
            BlockState citadelState = this.citadelRule.tryApply(x, y, z);
            return citadelState == null ? this.vanillaRule.tryApply(x, y, z) : citadelState;
        }
    }
}
