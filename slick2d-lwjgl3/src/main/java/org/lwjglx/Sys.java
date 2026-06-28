package org.lwjglx;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.net.URI;

public final class Sys {
    private static final long START_NANOS = System.nanoTime();

    private Sys() {
    }

    public static void initialize() {
    }

    public static String getVersion() {
        return org.lwjgl.Version.getVersion();
    }

    public static long getTimerResolution() {
        return 1_000L;
    }

    public static long getTime() {
        return (System.nanoTime() - START_NANOS) / 1_000_000L;
    }

    public static long getNanoTime() {
        return System.nanoTime() - START_NANOS;
    }

    public static boolean openURL(String url) {
        try {
            if (!Desktop.isDesktopSupported()) {
                return false;
            }
            Desktop.getDesktop().browse(URI.create(url));
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void alert(String title, String message) {
        System.err.println(title + ": " + message);
    }

    public static boolean is64Bit() {
        return org.lwjgl.system.Pointer.BITS64;
    }

    public static String getClipboard() {
        try {
            Object data = Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);
            return data instanceof String ? (String) data : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
