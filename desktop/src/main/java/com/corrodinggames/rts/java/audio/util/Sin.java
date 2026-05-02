package com.corrodinggames.rts.java.audio.util;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/n.class */
class Sin {
    static final float[] a = new float[16384];

    static {
        for (int i = 0; i < 16384; i++) {
            a[i] = (float) Math.sin(((i + 0.5f) / 16384.0f) * 6.2831855f);
        }
        for (int i2 = 0; i2 < 360; i2 += 90) {
            a[((int) (i2 * 45.511112f)) & 16383] = (float) Math.sin(i2 * 0.017453292f);
        }
    }
}
