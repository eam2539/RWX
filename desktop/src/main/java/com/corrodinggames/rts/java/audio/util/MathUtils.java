package com.corrodinggames.rts.java.audio.util;

import java.util.Random;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/m.class */
public final class MathUtils {
    public static Random a = new Random();

    public static float a(float f) {
        return Sin.a[((int) (f * 2607.5945f)) & 16383];
    }

    public static float b(float f) {
        return Sin.a[((int) ((f + 1.5707964f) * 2607.5945f)) & 16383];
    }

    public static int a(int i) {
        return a.nextInt(i + 1);
    }

    public static int b(int i) {
        if (i == 0) {
            return 1;
        }
        int i2 = i - 1;
        int i3 = i2 | (i2 >> 1);
        int i4 = i3 | (i3 >> 2);
        int i5 = i4 | (i4 >> 4);
        int i6 = i5 | (i5 >> 8);
        return (i6 | (i6 >> 16)) + 1;
    }

    public static float a(float f, float f2, float f3) {
        return f < f2 ? f2 : f > f3 ? f3 : f;
    }
}
