package com.github.alexthe666.citadel.client.gui.data;

public class Whitespace {
    private final int page;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private boolean down = false;

    public Whitespace(int page, int x, int y, int width, int height) {
        this.page = page;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Whitespace(int page, int x, int y, int width, int height, boolean down) {
        this.page = page;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.down = down;
    }

    public int getPage() {
        return page;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean downIn) {
        down = downIn;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Whitespace ws) {
            return ws.x == this.x && ws.y == this.y && ws.height == this.height && ws.width == this.width && ws.down == this.down;
        }
        return false;
    }
}
