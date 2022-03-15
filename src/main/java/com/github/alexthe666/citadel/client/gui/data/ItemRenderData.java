package com.github.alexthe666.citadel.client.gui.data;

public class ItemRenderData {
    private String item;
    private String item_tag = "";
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

    public String getItemTag() {
        return item_tag;
    }

    public void setItemTag(String item) {
        this.item_tag = item_tag;
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
}
