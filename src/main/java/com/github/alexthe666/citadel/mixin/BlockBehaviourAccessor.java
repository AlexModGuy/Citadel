package com.github.alexthe666.citadel.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockBehaviour.class)
public interface BlockBehaviourAccessor {
    //AT cannot correctly widen this method
    @Invoker("canSurvive")
    boolean citadel_canSurvive(BlockState state, LevelReader level, BlockPos pos);
}
