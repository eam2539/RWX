package com.corrodinggames.rts.gameFramework.utility;

public final class Log {
    private Log() {
    }

    public static int a(String tag, String message) {
        print(tag, message, null, false);
        return 0;
    }

    public static int b(String tag, String message) {
        print(tag, message, null, true);
        return 0;
    }

    public static int b(String tag, String message, Throwable throwable) {
        print(tag, message, throwable, true);
        return 0;
    }

    public static int c(String tag, String message) {
        print(tag, message, null, true);
        return 0;
    }

    public static int d(String tag, String message) {
        print(tag, message, null, false);
        return 0;
    }

    private static void print(String tag, String message, Throwable throwable, boolean error) {
        String line = tag + ": " + message;
        if (error) {
            System.err.println(line);
        } else {
            System.out.println(line);
        }
        if (throwable != null) {
            throwable.printStackTrace(error ? System.err : System.out);
        }
    }
}
