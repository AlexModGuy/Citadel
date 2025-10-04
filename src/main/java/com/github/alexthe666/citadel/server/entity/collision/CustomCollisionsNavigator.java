package com.github.alexthe666.citadel.server.entity.collision;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CustomCollisionsNavigator extends GroundPathNavigation {

    public CustomCollisionsNavigator(Mob mob, Level world) {
        super(mob, world);
    }

    protected PathFinder createPathFinder(int i) {
        this.nodeEvaluator = new CustomCollisionsNodeProcessor();
        return new PathFinder(this.nodeEvaluator, i);
    }

    public static boolean isClearForMovementBetweenRespectCustomCollisions(Mob mob, Vec3 pos1, Vec3 pos2, boolean allowSwimming) {
        Vec3 vec3 = new Vec3(pos2.x, pos2.y + (double)mob.getBbHeight() * 0.5, pos2.z);
        BlockHitResult hitResult = mob.level()
                .clip(new ClipContext(pos1, vec3, ClipContext.Block.COLLIDER, allowSwimming ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, mob));
        if(hitResult.getType() == HitResult.Type.MISS){
            return true;
        }else{
            BlockPos hitPos = hitResult.getBlockPos();
            BlockState state = mob.level().getBlockState(hitPos);
            return mob instanceof ICustomCollisions customCollisions && customCollisions.canPassThrough(hitPos, state, state.getCollisionShape(mob.level(), hitPos));
        }
    }

    @Override
    protected boolean canMoveDirectly(Vec3 pos1, Vec3 pos2) {
        return isClearForMovementBetweenRespectCustomCollisions(this.mob, pos1, pos2, false);
    }

}
