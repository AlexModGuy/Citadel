package com.github.alexthe666.citadel.client.model.container;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ObjGroupObject {
    public String name;
    public ArrayList<ObjFace> faces = new ArrayList<ObjFace>();
    public int glDrawingMode;

    public ObjGroupObject() {
        this("");
    }

    public ObjGroupObject(String name) {
        this(name, -1);
    }

    public ObjGroupObject(String name, int glDrawingMode) {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    @OnlyIn(Dist.CLIENT)
    public void render() {
        if (faces.size() > 0) {
            Tessellator tessellator = Tessellator.getInstance();
            tessellator.getBuffer().begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            render(tessellator);
            tessellator.draw();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void render(Tessellator tessellator) {
        if (faces.size() > 0) {
            for (ObjFace face : faces) {
                face.addFaceForRender(tessellator);
            }
        }
    }
}