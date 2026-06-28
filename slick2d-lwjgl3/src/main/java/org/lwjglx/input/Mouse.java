package org.lwjglx.input;

public final class Mouse {
    private static boolean grabbed;

    private Mouse() {
    }

    public static void create() {
    }

    public static boolean isCreated() {
        return true;
    }

    public static void destroy() {
    }

    public static void poll() {
    }

    public static boolean next() {
        return false;
    }

    public static int getX() {
        return 0;
    }

    public static int getY() {
        return 0;
    }

    public static boolean isButtonDown(int button) {
        return false;
    }

    public static int getButtonCount() {
        return 3;
    }

    public static int getEventButton() {
        return -1;
    }

    public static boolean getEventButtonState() {
        return false;
    }

    public static int getEventX() {
        return 0;
    }

    public static int getEventY() {
        return 0;
    }

    public static int getEventDX() {
        return 0;
    }

    public static int getEventDY() {
        return 0;
    }

    public static int getEventDWheel() {
        return 0;
    }

    public static void setGrabbed(boolean grab) {
        grabbed = grab;
    }

    public static boolean isGrabbed() {
        return grabbed;
    }

    public static void setNativeCursor(Cursor cursor) {
    }
}
