package org.lwjglx.input;

public final class Keyboard {
    private static boolean repeatEvents;

    private Keyboard() {
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

    public static boolean isKeyDown(int key) {
        return false;
    }

    public static String getKeyName(int key) {
        return "KEY" + key;
    }

    public static int getEventKey() {
        return 0;
    }

    public static char getEventCharacter() {
        return 0;
    }

    public static boolean getEventKeyState() {
        return false;
    }

    public static void enableRepeatEvents(boolean enable) {
        repeatEvents = enable;
    }

    public static boolean areRepeatEventsEnabled() {
        return repeatEvents;
    }
}
