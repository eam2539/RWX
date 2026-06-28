package org.lwjglx.opengl;

public class DisplayMode {
    private final int width;
    private final int height;

    public DisplayMode(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
