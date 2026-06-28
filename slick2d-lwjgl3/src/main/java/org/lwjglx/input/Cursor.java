package org.lwjglx.input;

import org.lwjglx.LWJGLException;

import java.nio.IntBuffer;

public class Cursor {
    public static final int CURSOR_8_BIT_ALPHA = 1;
    public static final int CURSOR_ONE_BIT_TRANSPARENCY = 2;

    public Cursor(int width, int height, int xHotspot, int yHotspot, int numImages, IntBuffer images, IntBuffer delays)
            throws LWJGLException {
    }

    public void destroy() {
    }
}
