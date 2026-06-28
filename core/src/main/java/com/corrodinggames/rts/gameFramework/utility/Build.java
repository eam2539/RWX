package com.corrodinggames.rts.gameFramework.utility;

public final class Build {
    public static final String MODEL = readStringField(
            "android.os.Build",
            "MODEL",
            System.getProperty("os.name", "unknown")
    );

    private Build() {
    }

    public static final class VERSION {
        public static final int SDK_INT = readIntField("android.os.Build$VERSION", "SDK_INT", 0);
        public static final String RELEASE = readStringField(
                "android.os.Build$VERSION",
                "RELEASE",
                System.getProperty("os.version", "unknown")
        );

        private VERSION() {
        }
    }

    private static int readIntField(String className, String fieldName, int fallback) {
        try {
            return Class.forName(className).getField(fieldName).getInt(null);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return fallback;
        }
    }

    private static String readStringField(String className, String fieldName, String fallback) {
        try {
            Object value = Class.forName(className).getField(fieldName).get(null);
            return value instanceof String ? (String) value : fallback;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return fallback;
        }
    }
}
