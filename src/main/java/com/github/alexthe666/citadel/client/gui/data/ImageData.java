package com.github.alexthe666.citadel.client.gui.data;

public class ImageData {
    private String texture;
    private int x;
    private int y;
    private int page;
    private double scale;
    private int u;
    private int v;
    private int width;
    private int height;

    public ImageData(String texture, int x, int y, int page, double scale, int u, int v, int width, int height) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.page = page;
        this.scale = scale;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
