package com.corrodinggames.rts.gameFramework;

import android.graphics.Color;
import android.graphics.RectF;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class LogicNumberFuntion {
    private LogicNumberFuntion() {
    }

    public static String a(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        return output.toString("UTF-8");
    }

    public static void a(RectF rect, float amount) {
        rect.inset(-amount, -amount);
    }

    public static String d(long bytes) {
        if (bytes < 1024L) {
            return bytes + "b";
        }
        if (bytes < 1024L * 1024L) {
            return (bytes / 1024L) + "kb";
        }
        return (bytes / (1024L * 1024L)) + "mb";
    }

    public static String i(String value) {
        int slash = Math.max(value.lastIndexOf('/'), value.lastIndexOf('\\'));
        String name = slash >= 0 ? value.substring(slash + 1) : value;
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    public static boolean r(String value) {
        int length = value.length();
        int index = 0;
        while (index < length) {
            int codePoint = value.codePointAt(index);
            if (codePoint > 128) {
                return true;
            }
            index += Character.charCount(codePoint);
        }
        return false;
    }

    public static String[] b(String value, char delimiter) {
        if (value == null) {
            return new String[]{""};
        }
        return value.split(java.util.regex.Pattern.quote(String.valueOf(delimiter)), -1);
    }

    public static float e(float from, float to, float amount) {
        return from + ((to - from) * amount);
    }

    public static int b(int alpha, int red, int green, int blue) {
        return Color.argb(alpha, red, green, blue);
    }

    public static float sin(float degrees) {
        return (float) StrictMath.sin(Math.toRadians(degrees));
    }

    public static float cos(float degrees) {
        return (float) StrictMath.cos(Math.toRadians(degrees));
    }
}
