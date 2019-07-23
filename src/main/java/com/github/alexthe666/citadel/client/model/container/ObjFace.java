package com.github.alexthe666.citadel.client.model.container;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ObjFace {
    public ObjVertex[] vertices;
    public ObjVertex[] objVertexNormals;
    public ObjVertex faceNormal;
    public ObjTextureCoordinate[] objTextureCoordinates;

    @OnlyIn(Dist.CLIENT)
    public void addFaceForRender(Tessellator tessellator) {
        addFaceForRender(tessellator, 0.0005F);
    }

    @OnlyIn(Dist.CLIENT)
    public void addFaceForRender(Tessellator tessellator, float textureOffset) {
        if (faceNormal == null) {
            faceNormal = this.calculateFaceNormal();
        }


        float averageU = 0F;
        float averageV = 0F;

        if ((objTextureCoordinates != null) && (objTextureCoordinates.length > 0)) {
            for (int i = 0; i < objTextureCoordinates.length; ++i) {
                averageU += objTextureCoordinates[i].u;
                averageV += objTextureCoordinates[i].v;
            }

            averageU = averageU / objTextureCoordinates.length;
            averageV = averageV / objTextureCoordinates.length;
        }

        float offsetU, offsetV;

        for (int i = 0; i < vertices.length; ++i) {

            if ((objTextureCoordinates != null) && (objTextureCoordinates.length > 0)) {
                offsetU = textureOffset;
                offsetV = textureOffset;

                if (objTextureCoordinates[i].u > averageU) {
                    offsetU = -offsetU;
                }
                if (objTextureCoordinates[i].v > averageV) {
                    offsetV = -offsetV;
                }

                tessellator.getBuffer().pos(vertices[i].x, vertices[i].y, vertices[i].z).tex(objTextureCoordinates[i].u + offsetU, objTextureCoordinates[i].v + offsetV).endVertex();

            } else {
                tessellator.getBuffer().pos(vertices[i].x, vertices[i].y, vertices[i].z).endVertex();
            }
        }
    }

    public ObjVertex calculateFaceNormal() {
        Vec3d v1 = new Vec3d(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
        Vec3d v2 = new Vec3d(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);
        Vec3d normalVector = null;

        normalVector = v1.crossProduct(v2).normalize();

        return new ObjVertex((float) normalVector.x, (float) normalVector.x, (float) normalVector.x);
    }
}