package com.github.alexthe666.citadel.server.entity.collision;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.NotNull;

public class CustomCollisionsNodeProcessor extends WalkNodeEvaluator {

    public CustomCollisionsNodeProcessor() {
    }

    @Override
    public @NotNull PathType getPathType(@NotNull PathfindingContext context, int x, int y, int z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);
        BlockState state = context.getBlockState(blockpos$mutableblockpos);
        if (((ICustomCollisions) this.mob).canPassThrough(blockpos$mutableblockpos, state, state.getBlockSupportShape(context.level(), blockpos$mutableblockpos))) {
            return PathType.OPEN;
        }
        return super.getPathType(context, x, y, z);
    }
}