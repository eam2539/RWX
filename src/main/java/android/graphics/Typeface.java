package android.graphics;

import android.util.SparseArray;

/* JADX INFO: loaded from: game-lib.jar:android/graphics/Typeface.class */
public class Typeface {
    int g;
    private int j;
    String h;
    private static final SparseArray i = new SparseArray(3);
    public static final Typeface a = a((String) null, 0);
    public static final Typeface b = a((String) null, 1);
    public static final Typeface c = a("sans-serif", 0);
    public static final Typeface d = a("serif", 0);
    public static final Typeface e = a("monospace", 0);
    static Typeface[] f = {a, b, a((String) null, 2), a((String) null, 3)};

    public final boolean a() {
        return (this.j & 1) != 0;
    }

    public static Typeface a(String str, int i2) {
        Typeface typeface = new Typeface(0);
        typeface.j = i2;
        typeface.h = str;
        return typeface;
    }

    public static Typeface a(Typeface typeface, int i2) {
        Typeface typeface2;
        int i3 = 0;
        if (typeface != null) {
            if (typeface.j == i2) {
                return typeface;
            }
            i3 = typeface.g;
        }
        SparseArray sparseArray = (SparseArray) i.a(i3);
        if (sparseArray != null && (typeface2 = (Typeface) sparseArray.a(i2)) != null) {
            return typeface2;
        }
        Typeface typeface3 = new Typeface(0);
        typeface3.j = i2;
        typeface3.h = typeface.h;
        if (sparseArray == null) {
            sparseArray = new SparseArray(4);
            i.b(i3, sparseArray);
        }
        sparseArray.b(i2, typeface3);
        return typeface3;
    }

    private Typeface(int i2) {
        this.j = 0;
        this.g = i2;
        this.j = b(i2);
    }

    protected void finalize() throws Throwable {
        try {
            a(this.g);
        } finally {
            super.finalize();
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Typeface typeface = (Typeface) obj;
        return this.j == typeface.j && this.g == typeface.g;
    }

    public int hashCode() {
        return (31 * this.g) + this.j;
    }

    private static void a(int i2) {
    }

    private static int b(int i2) {
        return 0;
    }
}
