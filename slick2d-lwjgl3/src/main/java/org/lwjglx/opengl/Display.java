package org.lwjglx.opengl;

public final class Display {
    private static DisplayMode displayMode = new DisplayMode(854, 480);
    private static boolean vsyncEnabled;

    private Display() {
    }

    public static boolean isCreated() {
        return true;
    }

    public static boolean isActive() {
        return true;
    }

    public static boolean isVisible() {
        return true;
    }

    public static boolean isCloseRequested() {
        return false;
    }

    public static boolean isDirty() {
        return false;
    }

    public static void makeCurrent() {
    }

    public static void update() {
    }

    public static void sync(int fps) {
    }

    public static void destroy() {
    }

    public static void setVSyncEnabled(boolean enabled) {
        vsyncEnabled = enabled;
    }

    public static boolean isVSyncEnabled() {
        return vsyncEnabled;
    }

    public static DisplayMode getDisplayMode() {
        return displayMode;
    }

    public static void setDisplayMode(DisplayMode mode) {
        displayMode = mode;
    }

    public static int getWidth() {
        return displayMode.getWidth();
    }

    public static int getHeight() {
        return displayMode.getHeight();
    }
}
