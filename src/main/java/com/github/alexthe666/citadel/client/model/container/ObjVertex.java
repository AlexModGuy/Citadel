package com.github.alexthe666.citadel.client.model.container;

public class ObjVertex {
    public float x, y, z;

    public ObjVertex(float x, float y) {
        this(x, y, 0F);
    }

    public ObjVertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}