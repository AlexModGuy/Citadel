package com.github.alexthe666.citadel.client.gui.data;

public class EntityRenderData {
    private String entity;
    private int x;
    private int y;
    private double scale;
    private int page;
    private double rot_x;
    private double rot_y;
    private double rot_z;
    private boolean follow_cursor;
    private String entity_data;

    public EntityRenderData(String entity, int x, int y, double scale, int page, double rot_x, double rot_y, double rot_z, boolean follow_cursor, String entity_data) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.page = page;
        this.rot_x = rot_x;
        this.rot_y = rot_y;
        this.rot_z = rot_z;
        this.follow_cursor = follow_cursor;
        this.entity_data = entity_data;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String model) {
        this.entity = model;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public double getRot_x() {
        return rot_x;
    }

    public void setRot_x(double rot_x) {
        this.rot_x = rot_x;
    }

    public double getRot_y() {
        return rot_y;
    }

    public void setRot_y(double rot_y) {
        this.rot_y = rot_y;
    }

    public double getRot_z() {
        return rot_z;
    }

    public void setRot_z(double rot_z) {
        this.rot_z = rot_z;
    }

    public boolean isFollow_cursor() {
        return follow_cursor;
    }

    public void setFollow_cursor(boolean follow_cursor) {
        this.follow_cursor = follow_cursor;
    }

    public String getEntityData() {
        return entity_data;
    }

}
