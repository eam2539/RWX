package android.graphics;

import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: game-lib.jar:android/graphics/Color.class */
public class Color {
    private static final HashMap a = new HashMap();

    public static int a(int i) {
        return i >>> 24;
    }

    public static int b(int i) {
        return (i >> 16) & 255;
    }

    public static int c(int i) {
        return (i >> 8) & 255;
    }

    public static int d(int i) {
        return i & 255;
    }

    public static int a(int i, int i2, int i3) {
        return (-16777216) | (i << 16) | (i2 << 8) | i3;
    }

    public static int a(int i, int i2, int i3, int i4) {
        return (i << 24) | (i2 << 16) | (i3 << 8) | i4;
    }

    public static int a(String str) {
        if (str.charAt(0) == '#') {
            long j = Long.parseLong(str.substring(1), 16);
            if (str.length() == 7) {
                j |= -16777216;
            } else if (str.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) j;
        }
        Integer num = (Integer) a.get(str.toLowerCase(Locale.ROOT));
        if (num != null) {
            return num.intValue();
        }
        throw new IllegalArgumentException("Unknown color");
    }

    static {
        a.put("black", -16777216);
        a.put("darkgray", -12303292);
        a.put("gray", -7829368);
        a.put("lightgray", -3355444);
        a.put("white", -1);
        a.put("red", -65536);
        a.put("green", -16711936);
        a.put("blue", -16776961);
        a.put("yellow", -256);
        a.put("cyan", -16711681);
        a.put("magenta", -65281);
        a.put("aqua", -16711681);
        a.put("fuchsia", -65281);
        a.put("darkgrey", -12303292);
        a.put("grey", -7829368);
        a.put("lightgrey", -3355444);
        a.put("lime", -16711936);
        a.put("maroon", -8388608);
        a.put("navy", -16777088);
        a.put("olive", -8355840);
        a.put("purple", -8388480);
        a.put("silver", -4144960);
        a.put("teal", -16744320);
    }
}
