package com.github.alexthe666.citadel.server.entity.collision;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class CustomCollisionsNodeProcessor extends WalkNodeEvaluator {

    public CustomCollisionsNodeProcessor() {
    }


    @Override
    public PathType getPathType(PathfindingContext context, int x, int y, int z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        BlockState state = context.getBlockState(blockpos$mutableblockpos.set(x, y, z));
        if (((ICustomCollisions) this.mob).canPassThrough(blockpos$mutableblockpos, state, state.getBlockSupportShape(context.level(), blockpos$mutableblockpos))) {
            return PathType.OPEN;
        }
        return super.getPathType(context, x, y, z);
    }
}