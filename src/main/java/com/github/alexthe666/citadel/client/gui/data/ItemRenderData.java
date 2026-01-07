package com.github.alexthe666.citadel.client.gui.data;

import java.util.Map;

public class ItemRenderData {
    private String item;
    private Map<String, Object> components;
    private int x;
    private int y;
    private double scale;
    private int page;

    public ItemRenderData(String item, int x, int y, double scale, int page) {
        this.item = item;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.page = page;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getPage() {
        return page;
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

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public Map<String, Object> getComponents() {
        return components;
    }

    public void setComponents(Map<String, Object> components) {
        this.components = components;
    }
}
