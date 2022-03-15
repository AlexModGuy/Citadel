package com.github.alexthe666.citadel.client.gui.data;

public class RecipeData {
    private String recipe;
    private boolean shapeless;
    private int x;
    private int y;
    private double scale;
    private int page;

    public RecipeData(String recipe, int x, int y, boolean shapeless, double scale, int page) {
        this.recipe = recipe;
        this.x = x;
        this.y = y;
        this.shapeless = shapeless;
        this.scale = scale;
        this.page = page;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
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
