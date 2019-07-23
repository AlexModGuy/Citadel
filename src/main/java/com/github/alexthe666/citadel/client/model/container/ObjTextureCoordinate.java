package com.github.alexthe666.citadel.client.model.container;

public class ObjTextureCoordinate {
    public float u, v, w;

    public ObjTextureCoordinate(float u, float v) {
        this(u, v, 0F);
    }

    public ObjTextureCoordinate(float u, float v, float w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }
}