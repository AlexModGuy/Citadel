package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.animation.IScaleable;
import com.github.alexthe666.citadel.animation.LegSolverQuadruped;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author paul101
 * @since 1.0.0
 * Code used with permission from JurassiCraft 1
 */
public final class LegArticulator {

    public static void articulateQuadruped(LivingEntity entity, LegSolverQuadruped legs, AdvancedModelBox body, AdvancedModelBox lowerBody, AdvancedModelBox neck, AdvancedModelBox backLeftThigh, AdvancedModelBox backLeftCalf, AdvancedModelBox backLeftFoot, AdvancedModelBox backRightThigh, AdvancedModelBox backRightCalf, AdvancedModelBox backRightFoot, AdvancedModelBox frontLeftThigh, AdvancedModelBox frontLeftCalf, AdvancedModelBox frontLeftFoot, AdvancedModelBox frontRightThigh, AdvancedModelBox frontRightCalf, AdvancedModelBox frontRightFoot, float rotBackThigh, float rotBackCalf, float rotBackFoot, float rotFrontThigh, float rotFrontCalf, float rotFrontFoot, float delta) {
        float heightBackLeft = legs.backLeft.getHeight(delta);
        float heightBackRight = legs.backRight.getHeight(delta);
        float heightFrontLeft = legs.frontLeft.getHeight(delta);
        float heightFrontRight = legs.frontRight.getHeight(delta);
        if (heightBackLeft > 0.0F || heightBackRight > 0.0F || heightFrontLeft > 0.0F || heightFrontRight > 0.0F) {
            float sc = getScale(entity);
            float backAvg = avg(heightBackLeft, heightBackRight);
            float frontAvg = avg(heightFrontLeft, heightFrontRight);
            float bodyLength = Math.abs(avg(legs.backLeft.forward, legs.backRight.forward) - avg(legs.frontLeft.forward, legs.frontRight.forward));
            float tilt = (float)(Mth.atan2((double)(bodyLength * sc), (double)(backAvg - frontAvg)) - 1.5707963267948966);
            body.rotationPointY += 16.0F / sc * backAvg;
            body.rotateAngleX += tilt;
            lowerBody.rotateAngleX -= tilt;
            backLeftThigh.rotationPointY += 16.0F / sc * tilt;
            backRightThigh.rotationPointY += 16.0F / sc * tilt;
            frontLeftThigh.rotateAngleX -= tilt;
            frontRightThigh.rotateAngleX -= tilt;
            neck.rotateAngleX -= tilt;
            articulateLegPair(sc, heightBackLeft, heightBackRight, backAvg, -backAvg, backLeftThigh, backLeftCalf, backLeftFoot, backRightThigh, backRightCalf, backRightFoot, rotBackThigh, rotBackCalf, rotBackFoot);
            articulateLegPair(sc, heightFrontLeft, heightFrontRight, frontAvg, -frontAvg, frontLeftThigh, frontLeftCalf, frontLeftFoot, frontRightThigh, frontRightCalf, frontRightFoot, rotFrontThigh, rotFrontCalf, rotFrontFoot);
        }

    }

    public static void articulateLegPair(float sc, float heightLeft, float heightRight, float avg, float offsetY, AdvancedModelBox leftThigh, AdvancedModelBox leftCalf, AdvancedModelBox leftFoot, AdvancedModelBox rightThigh, AdvancedModelBox rightCalf, AdvancedModelBox rightFoot, float rotThigh, float rotCalf, float rotFoot) {
        float difLeft = Math.max(0.0F, heightRight - heightLeft);
        float difRight = Math.max(0.0F, heightLeft - heightRight);
        leftThigh.rotationPointY += 16.0F / sc * (Math.max(heightLeft, avg) + offsetY);
        rightThigh.rotationPointY += 16.0F / sc * (Math.max(heightRight, avg) + offsetY);
        leftThigh.rotateAngleX -= rotThigh * difLeft;
        leftCalf.rotateAngleX += rotCalf * difLeft;
        rightThigh.rotateAngleX -= rotThigh * difRight;
        rightCalf.rotateAngleX += rotCalf * difRight;
        rightFoot.rotateAngleX -= rotFoot * Math.min(0.0F, heightRight - heightLeft);
        leftFoot.rotateAngleX -= rotFoot * Math.min(0.0F, heightLeft - heightRight);
    }


    private static float avg(float a, float b) {
        return (a + b) / 2;
    }

    private static float getScale(LivingEntity entity) {
        if (entity instanceof IScaleable) {
            return ((IScaleable) entity).getScaleForLegSolver();
        }
        return entity.getBbWidth();
    }
}
