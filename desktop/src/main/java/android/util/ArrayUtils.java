package android.util;

/* JADX INFO: renamed from: android.util.a */
/* JADX INFO: loaded from: game-lib.jar:android/util/a.class */
class ArrayUtils {

    /* JADX INFO: renamed from: a */
    static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];

    /* JADX INFO: renamed from: b */
    static final int[] EMPTY_INT_ARRAY = new int[0];

    /* JADX INFO: renamed from: c */
    static final long[] EMPTY_LONG_ARRAY = new long[0];

    /* JADX INFO: renamed from: d */
    static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /* JADX INFO: renamed from: a */
    static int binarySearch(int[] iArr, int i, int i2) {
        int i3 = 0;
        int i4 = i - 1;
        while (i3 <= i4) {
            int i5 = (i3 + i4) >>> 1;
            int i6 = iArr[i5];
            if (i6 < i2) {
                i3 = i5 + 1;
            } else if (i6 > i2) {
                i4 = i5 - 1;
            } else {
                return i5;
            }
        }
        return i3 ^ (-1);
    }
}
