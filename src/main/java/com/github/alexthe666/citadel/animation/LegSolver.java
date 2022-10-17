package com.github.alexthe666.citadel.animation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * @author paul101
 * @since 1.0.0
 * Code used with permission from JurassiCraft 1
 */
public class LegSolver {
    public final Leg[] legs;

    public LegSolver(Leg... legs) {
        this.legs = legs;
    }

    public final void update(LivingEntity entity, float scale) {
        this.update(entity, entity.yBodyRot, scale);
    }

    public final void update(LivingEntity entity, float yaw, float scale) {
        double sideTheta = yaw / (180 / Math.PI);
        double sideX = Math.cos(sideTheta) * scale;
        double sideZ = Math.sin(sideTheta) * scale;
        double forwardTheta = sideTheta + Math.PI / 2;
        double forwardX = Math.cos(forwardTheta) * scale;
        double forwardZ = Math.sin(forwardTheta) * scale;
        for (Leg leg : this.legs) {
            leg.update(entity, sideX, sideZ, forwardX, forwardZ, scale);
        }
    }

    public static final class Leg {

        public final float forward;
        public final float side;
        private final float range;
        private float height;
        private float prevHeight;
        private boolean isWing;

        public Leg(float forward, float side, float range, boolean isWing) {
            this.forward = forward;
            this.side = side;
            this.range = range;
            this.isWing = isWing;
        }

        public final float getHeight(float delta) {
            return this.prevHeight + (this.height - this.prevHeight) * delta;
        }

        public void update(LivingEntity entity, double sideX, double sideZ, double forwardX, double forwardZ, float scale) {
            this.prevHeight = this.height;
            double posY = entity.getY();
            float settledHeight = this.settle(entity, entity.getX() + sideX * this.side + forwardX * this.forward, posY, entity.getZ() + sideZ * this.side + forwardZ * this.forward, this.height);
            this.height = Mth.clamp(settledHeight, -this.range * scale, this.range * scale);
        }


        private float settle(LivingEntity entity, double x, double y, double z, float height) {
            BlockPos pos = new BlockPos(x, y + 1e-3, z);
            float dist = this.getDistance(entity.level, pos);
            if (1 - dist < 1e-3) {
                dist = this.getDistance(entity.level, pos.below()) + (float) y % 1;
            } else {
                dist -= 1 - (y % 1);
            }
            if (entity.isOnGround() && height <= dist) {
                return height == dist ? height : Math.min(height + this.getFallSpeed(), dist);
            } else if (height > 0) {
                return Math.max(height - this.getRiseSpeed(), dist);
            }
            return height;
        }

        private float getDistance(Level world, BlockPos pos) {
            BlockState state = world.getBlockState(pos);
            AABB aabb = state.getBlockSupportShape(world, pos).getFaceShape(Direction.UP).bounds();
            return aabb == null ? 1 : 1 - Math.min((float) aabb.maxY, 1);
        }

        protected float getFallSpeed() {
            return 0.25F;
        }

        protected float getRiseSpeed() {
            return 0.25F;
        }
    }
}