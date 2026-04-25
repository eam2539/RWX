package android.util;

import com.corrodinggames.rts.gameFramework.utility.OptimizedSizes;

/* JADX INFO: loaded from: game-lib.jar:android/util/SparseArray.class */
public class SparseArray implements Cloneable {
    private static final Object a = new Object();
    private boolean b;
    private int[] c;
    private Object[] d;
    private int e;

    public SparseArray() {
        this(10);
    }

    public SparseArray(int i) {
        this.b = false;
        if (i == 0) {
            this.c = ArrayUtils.EMPTY_INT_ARRAY;
            this.d = ArrayUtils.EMPTY_OBJECT_ARRAY;
        } else {
            int iGrowSizeByFour = OptimizedSizes.growSizeByFour(i);
            this.c = new int[iGrowSizeByFour];
            this.d = new Object[iGrowSizeByFour];
        }
        this.e = 0;
    }

    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public SparseArray clone() {
        SparseArray sparseArray = null;
        try {
            sparseArray = (SparseArray) super.clone();
            sparseArray.c = (int[]) this.c.clone();
            sparseArray.d = (Object[]) this.d.clone();
        } catch (CloneNotSupportedException e) {
        }
        return sparseArray;
    }

    public Object a(int i) {
        return a(i, null);
    }

    public Object a(int i, Object obj) {
        int iBinarySearch = ArrayUtils.binarySearch(this.c, this.e, i);
        if (iBinarySearch < 0 || this.d[iBinarySearch] == a) {
            return obj;
        }
        return this.d[iBinarySearch];
    }

    private void c() {
        int i = this.e;
        int i2 = 0;
        int[] iArr = this.c;
        Object[] objArr = this.d;
        for (int i3 = 0; i3 < i; i3++) {
            Object obj = objArr[i3];
            if (obj != a) {
                if (i3 != i2) {
                    iArr[i2] = iArr[i3];
                    objArr[i2] = obj;
                    objArr[i3] = null;
                }
                i2++;
            }
        }
        this.b = false;
        this.e = i2;
    }

    public void b(int i, Object obj) {
        int iBinarySearch = ArrayUtils.binarySearch(this.c, this.e, i);
        if (iBinarySearch >= 0) {
            this.d[iBinarySearch] = obj;
            return;
        }
        int iBinarySearch2 = iBinarySearch ^ (-1);
        if (iBinarySearch2 < this.e && this.d[iBinarySearch2] == a) {
            this.c[iBinarySearch2] = i;
            this.d[iBinarySearch2] = obj;
            return;
        }
        if (this.b && this.e >= this.c.length) {
            c();
            iBinarySearch2 = ArrayUtils.binarySearch(this.c, this.e, i) ^ (-1);
        }
        if (this.e >= this.c.length) {
            int iGrowSizeByFour = OptimizedSizes.growSizeByFour(this.e + 1);
            int[] iArr = new int[iGrowSizeByFour];
            Object[] objArr = new Object[iGrowSizeByFour];
            System.arraycopy(this.c, 0, iArr, 0, this.c.length);
            System.arraycopy(this.d, 0, objArr, 0, this.d.length);
            this.c = iArr;
            this.d = objArr;
        }
        if (this.e - iBinarySearch2 != 0) {
            System.arraycopy(this.c, iBinarySearch2, this.c, iBinarySearch2 + 1, this.e - iBinarySearch2);
            System.arraycopy(this.d, iBinarySearch2, this.d, iBinarySearch2 + 1, this.e - iBinarySearch2);
        }
        this.c[iBinarySearch2] = i;
        this.d[iBinarySearch2] = obj;
        this.e++;
    }

    public int b() {
        if (this.b) {
            c();
        }
        return this.e;
    }

    public int b(int i) {
        if (this.b) {
            c();
        }
        return this.c[i];
    }

    public Object c(int i) {
        if (this.b) {
            c();
        }
        return this.d[i];
    }

    public void c(int i, Object obj) {
        if (this.e != 0 && i <= this.c[this.e - 1]) {
            b(i, obj);
            return;
        }
        if (this.b && this.e >= this.c.length) {
            c();
        }
        int i2 = this.e;
        if (i2 >= this.c.length) {
            int iGrowSizeByFour = OptimizedSizes.growSizeByFour(i2 + 1);
            int[] iArr = new int[iGrowSizeByFour];
            Object[] objArr = new Object[iGrowSizeByFour];
            System.arraycopy(this.c, 0, iArr, 0, this.c.length);
            System.arraycopy(this.d, 0, objArr, 0, this.d.length);
            this.c = iArr;
            this.d = objArr;
        }
        this.c[i2] = i;
        this.d[i2] = obj;
        this.e = i2 + 1;
    }

    public String toString() {
        if (b() <= 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.e * 28);
        sb.append('{');
        for (int i = 0; i < this.e; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(b(i));
            sb.append('=');
            Object objC = c(i);
            if (objC != this) {
                sb.append(objC);
            } else {
                sb.append("(this Map)");
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
