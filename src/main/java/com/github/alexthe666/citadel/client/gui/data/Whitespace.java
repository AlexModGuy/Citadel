package com.github.alexthe666.citadel.client.gui.data;

public record Whitespace(
    int page, int x, int y, int width, int height, boolean down
) {
    public Whitespace(int page, int x, int y, int width, int height) {
        this(page, x, y, width, height, false);
    }
}
