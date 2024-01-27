package com.github.alexthe666.citadel.server.entity.pathfinding.raycoms;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IPassabilityNavigator {

    int maxSearchNodes();

    boolean isBlockExplicitlyPassable(BlockState state, BlockPos pos, BlockPos entityPos);

    boolean isBlockExplicitlyNotPassable(BlockState state, BlockPos pos, BlockPos entityPos);
}
