package org.lwjglx.input;

import org.lwjglx.LWJGLException;

public final class Controllers {
    private Controllers() {
    }

    public static void create() throws LWJGLException {
    }

    public static boolean isCreated() {
        return true;
    }

    public static int getControllerCount() {
        return 0;
    }

    public static Controller getController(int index) {
        throw new IndexOutOfBoundsException("No controllers are available");
    }

    public static void poll() {
    }
}
