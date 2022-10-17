package com.github.alexthe666.citadel.server.entity.collision;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public interface ICustomCollisions {
    /*
        Override Entity#getAllowedMovement with entity method
     */
    static Vec3 getAllowedMovementForEntity(Entity entity, Vec3 vecIN) {
        AABB aabb = entity.getBoundingBox();
        List<VoxelShape> list = entity.level.getEntityCollisions(entity, aabb.expandTowards(vecIN));
        Vec3 vec3 = vecIN.lengthSqr() == 0.0D ? vecIN : collideBoundingBox2(entity, vecIN, aabb, entity.level, list);
        boolean flag = vecIN.x != vec3.x;
        boolean flag1 = vecIN.y != vec3.y;
        boolean flag2 = vecIN.z != vec3.z;
        boolean flag3 = entity.isOnGround() || flag1 && vecIN.y < 0.0D;
        if (entity.maxUpStep > 0.0F && flag3 && (flag || flag2)) {
            Vec3 vec31 = collideBoundingBox2(entity, new Vec3(vecIN.x, (double)entity.maxUpStep, vecIN.z), aabb, entity.level, list);
            Vec3 vec32 = collideBoundingBox2(entity, new Vec3(0.0D, (double)entity.maxUpStep, 0.0D), aabb.expandTowards(vecIN.x, 0.0D, vecIN.z), entity.level, list);
            if (vec32.y < (double)entity.maxUpStep) {
                Vec3 vec33 = collideBoundingBox2(entity, new Vec3(vecIN.x, 0.0D, vecIN.z), aabb.move(vec32), entity.level, list).add(vec32);
                if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
                    vec31 = vec33;
                }
            }

            if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
                return vec31.add(collideBoundingBox2(entity, new Vec3(0.0D, -vec31.y + vecIN.y, 0.0D), aabb.move(vec31), entity.level, list));
            }
        }

        return vec3;
    }

    boolean canPassThrough(BlockPos mutablePos, BlockState blockstate, VoxelShape voxelshape);

    //1.18 logic
    private static Vec3 collideBoundingBox2(@Nullable Entity p_198895_, Vec3 p_198896_, AABB p_198897_, Level p_198898_, List<VoxelShape> p_198899_) {
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(p_198899_.size() + 1);
        if (!p_198899_.isEmpty()) {
            builder.addAll(p_198899_);
        }

        WorldBorder worldborder = p_198898_.getWorldBorder();
        boolean flag = p_198895_ != null && worldborder.isInsideCloseToBorder(p_198895_, p_198897_.expandTowards(p_198896_));
        if (flag) {
            builder.add(worldborder.getCollisionShape());
        }

        builder.addAll(new CustomCollisionsBlockCollisions(p_198898_, p_198895_, p_198897_.expandTowards(p_198896_)));
        return collideWithShapes2(p_198896_, p_198897_, builder.build());
    }

    private static Vec3 collideWithShapes2(Vec3 p_198901_, AABB p_198902_, List<VoxelShape> p_198903_) {
        if (p_198903_.isEmpty()) {
            return p_198901_;
        } else {
            double d0 = p_198901_.x;
            double d1 = p_198901_.y;
            double d2 = p_198901_.z;
            if (d1 != 0.0D) {
                d1 = Shapes.collide(Direction.Axis.Y, p_198902_, p_198903_, d1);
                if (d1 != 0.0D) {
                    p_198902_ = p_198902_.move(0.0D, d1, 0.0D);
                }
            }

            boolean flag = Math.abs(d0) < Math.abs(d2);
            if (flag && d2 != 0.0D) {
                d2 = Shapes.collide(Direction.Axis.Z, p_198902_, p_198903_, d2);
                if (d2 != 0.0D) {
                    p_198902_ = p_198902_.move(0.0D, 0.0D, d2);
                }
            }

            if (d0 != 0.0D) {
                d0 = Shapes.collide(Direction.Axis.X, p_198902_, p_198903_, d0);
                if (!flag && d0 != 0.0D) {
                    p_198902_ = p_198902_.move(d0, 0.0D, 0.0D);
                }
            }

            if (!flag && d2 != 0.0D) {
                d2 = Shapes.collide(Direction.Axis.Z, p_198902_, p_198903_, d2);
            }

            return new Vec3(d0, d1, d2);
        }
    }

}
