package com.corrodinggames.rts.gameFramework.utility;

/* JADX INFO: renamed from: com.a.a.a.a */
/* JADX INFO: loaded from: game-lib.jar:com/a/a/a/a.class */
public class OptimizedSizes {

    /* JADX INFO: renamed from: a */
    private static Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /* JADX INFO: renamed from: b */
    private static Object[] S_OBJECT_ARRAY = new Object[73];

    /* JADX INFO: renamed from: a */
    public static int growSize(int i) {
        for (int i2 = 4; i2 < 32; i2++) {
            if (i <= (1 << i2) - 12) {
                return (1 << i2) - 12;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: b */
    public static int growSizeByTwo(int i) {
        return growSize(i * 2) / 2;
    }

    /* JADX INFO: renamed from: c */
    public static int growSizeByFour(int i) {
        return growSize(i * 4) / 4;
    }
}
