package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.animation.IScaleable;
import com.github.alexthe666.citadel.animation.LegSolverQuadruped;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

/**
 * @author paul101
 * @since 1.0.0
 * Code used with permission from JurassiCraft 1
 */
public final class LegArticulator {

    public static void articulateQuadruped(
            LivingEntity entity, LegSolverQuadruped legs, AdvancedRendererModel body, AdvancedRendererModel lowerBody, AdvancedRendererModel neck,
            AdvancedRendererModel backLeftThigh, AdvancedRendererModel backLeftCalf, AdvancedRendererModel[] backLeftFoot,
            AdvancedRendererModel backRightThigh, AdvancedRendererModel backRightCalf, AdvancedRendererModel[] backRightFoot,

            AdvancedRendererModel frontLeftThigh, AdvancedRendererModel frontLeftCalf, AdvancedRendererModel[] frontLeftFoot,
            AdvancedRendererModel frontRightThigh, AdvancedRendererModel frontRightCalf, AdvancedRendererModel[] frontRightFoot,
            float rotBackThigh, float rotBackCalf, float rotBackFoot,
            float rotFrontThigh, float rotFrontCalf, float rotFrontFoot,
            float delta) {
        final float heightBackLeft = legs.backLeft.getHeight(delta);
        final float heightBackRight = legs.backRight.getHeight(delta);
        final float heightFrontLeft = legs.frontLeft.getHeight(delta);
        final float heightFrontRight = legs.frontRight.getHeight(delta);
        if (heightBackLeft > 0 || heightBackRight > 0 || heightFrontLeft > 0 || heightFrontRight > 0) {
            final float sc = LegArticulator.getScale(entity);
            final float backAvg = LegArticulator.avg(heightBackLeft, heightBackRight);
            final float frontAvg = LegArticulator.avg(heightFrontLeft, heightFrontRight);
            final float bodyLength = Math.abs(avg(legs.backLeft.forward, legs.backRight.forward) - avg(legs.frontLeft.forward, legs.frontRight.forward));
            final float tilt = (float) (MathHelper.atan2(bodyLength * sc, backAvg - frontAvg) - Math.PI / 2);
            body.rotationPointY += 16 / sc * backAvg;
            body.rotateAngleX += tilt;
            lowerBody.rotateAngleX -= tilt;
            backLeftThigh.rotationPointY += 16 / sc * tilt;
            backRightThigh.rotationPointY += 16 / sc * tilt;
            frontLeftThigh.rotateAngleX -= tilt;
            frontRightThigh.rotateAngleX -= tilt;
            neck.rotateAngleX -= tilt;
            LegArticulator.articulateLegPair(sc, heightBackLeft, heightBackRight, backAvg, -backAvg, backLeftThigh, backLeftCalf, backLeftFoot, backRightThigh, backRightCalf, backRightFoot, rotBackThigh, rotBackCalf, rotBackFoot);
            LegArticulator.articulateLegPair(sc, heightFrontLeft, heightFrontRight, frontAvg, -frontAvg, frontLeftThigh, frontLeftCalf, frontLeftFoot, frontRightThigh, frontRightCalf, frontRightFoot, rotFrontThigh, rotFrontCalf, rotFrontFoot);
        }
    }

    private static void articulateLegPair(float sc, float heightLeft, float heightRight, float avg, float offsetY, AdvancedRendererModel leftThigh, AdvancedRendererModel leftCalf, AdvancedRendererModel[] leftFoot, AdvancedRendererModel rightThigh, AdvancedRendererModel rightCalf, AdvancedRendererModel[] rightFoot, float rotThigh, float rotCalf, float rotFoot) {
        final float difLeft = Math.max(0, heightRight - heightLeft);
        final float difRight = Math.max(0, heightLeft - heightRight);
        leftThigh.rotationPointY += 16 / sc * (Math.max(heightLeft, avg) + offsetY);
        rightThigh.rotationPointY += 16 / sc * (Math.max(heightRight, avg) + offsetY);
        leftThigh.rotateAngleX -= rotThigh * difLeft;
        leftCalf.rotateAngleX += rotCalf * difLeft;
        rightThigh.rotateAngleX -= rotThigh * difRight;
        rightCalf.rotateAngleX += rotCalf * difRight;
        for (AdvancedRendererModel part : rightFoot) {
            part.rotateAngleX -= rotFoot * Math.min(0, heightRight - heightLeft);
        }
        for (AdvancedRendererModel part : leftFoot) {
            part.rotateAngleX -= rotFoot * Math.min(0, heightLeft - heightRight);

        }
    }

    private static float avg(float a, float b) {
        return (a + b) / 2;
    }

    private static float getScale(LivingEntity entity) {
        if (entity instanceof IScaleable) {
            return ((IScaleable) entity).getScaleForLegSolver();
        }
        return entity.getWidth();
    }
}
