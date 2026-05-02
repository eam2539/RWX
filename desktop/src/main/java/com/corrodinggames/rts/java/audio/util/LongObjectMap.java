package com.corrodinggames.rts.java.audio.util;

import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/i.class */
public class LongObjectMap implements Iterable {
    public int a;
    long[] b;
    Object[] c;
    int d;
    int e;
    Object f;
    boolean g;
    private float h;
    private int i;
    private int j;
    private int k;
    private int l;
    private int m;
    private LongObjectMapEntries n;
    private LongObjectMapEntries o;

    public LongObjectMap() {
        this(51, 0.8f);
    }

    public LongObjectMap(int i, float f) {
        if (i < 0) {
            throw new IllegalArgumentException("initialCapacity must be >= 0: " + i);
        }
        int iB = MathUtils.b((int) Math.ceil(i / f));
        if (iB > 1073741824) {
            throw new IllegalArgumentException("initialCapacity is too large: " + iB);
        }
        this.d = iB;
        if (f <= 0.0f) {
            throw new IllegalArgumentException("loadFactor must be > 0: " + f);
        }
        this.h = f;
        this.k = (int) (this.d * f);
        this.j = this.d - 1;
        this.i = 63 - Long.numberOfTrailingZeros(this.d);
        this.l = Math.max(3, ((int) Math.ceil(Math.log(this.d))) * 2);
        this.m = Math.max(Math.min(this.d, 8), ((int) Math.sqrt(this.d)) / 8);
        this.b = new long[this.d + this.l];
        this.c = new Object[this.b.length];
    }

    public Object a(long j, Object obj) {
        if (j == 0) {
            Object obj2 = this.f;
            this.f = obj;
            if (!this.g) {
                this.g = true;
                this.a++;
            }
            return obj2;
        }
        long[] jArr = this.b;
        int i = (int) (j & ((long) this.j));
        long j2 = jArr[i];
        if (j2 == j) {
            Object obj3 = this.c[i];
            this.c[i] = obj;
            return obj3;
        }
        int iF = f(j);
        long j3 = jArr[iF];
        if (j3 == j) {
            Object obj4 = this.c[iF];
            this.c[iF] = obj;
            return obj4;
        }
        int iG = g(j);
        long j4 = jArr[iG];
        if (j4 == j) {
            Object obj5 = this.c[iG];
            this.c[iG] = obj;
            return obj5;
        }
        int i2 = this.d;
        int i3 = i2 + this.e;
        while (i2 < i3) {
            if (jArr[i2] != j) {
                i2++;
            } else {
                Object obj6 = this.c[i2];
                this.c[i2] = obj;
                return obj6;
            }
        }
        if (j2 == 0) {
            jArr[i] = j;
            this.c[i] = obj;
            int i4 = this.a;
            this.a = i4 + 1;
            if (i4 >= this.k) {
                b(this.d << 1);
                return null;
            }
            return null;
        }
        if (j3 == 0) {
            jArr[iF] = j;
            this.c[iF] = obj;
            int i5 = this.a;
            this.a = i5 + 1;
            if (i5 >= this.k) {
                b(this.d << 1);
                return null;
            }
            return null;
        }
        if (j4 == 0) {
            jArr[iG] = j;
            this.c[iG] = obj;
            int i6 = this.a;
            this.a = i6 + 1;
            if (i6 >= this.k) {
                b(this.d << 1);
                return null;
            }
            return null;
        }
        a(j, obj, i, j2, iF, j3, iG, j4);
        return null;
    }

    private void b(long j, Object obj) {
        if (j == 0) {
            this.f = obj;
            this.g = true;
            return;
        }
        int i = (int) (j & ((long) this.j));
        long j2 = this.b[i];
        if (j2 == 0) {
            this.b[i] = j;
            this.c[i] = obj;
            int i2 = this.a;
            this.a = i2 + 1;
            if (i2 >= this.k) {
                b(this.d << 1);
                return;
            }
            return;
        }
        int iF = f(j);
        long j3 = this.b[iF];
        if (j3 == 0) {
            this.b[iF] = j;
            this.c[iF] = obj;
            int i3 = this.a;
            this.a = i3 + 1;
            if (i3 >= this.k) {
                b(this.d << 1);
                return;
            }
            return;
        }
        int iG = g(j);
        long j4 = this.b[iG];
        if (j4 == 0) {
            this.b[iG] = j;
            this.c[iG] = obj;
            int i4 = this.a;
            this.a = i4 + 1;
            if (i4 >= this.k) {
                b(this.d << 1);
                return;
            }
            return;
        }
        a(j, obj, i, j2, iF, j3, iG, j4);
    }

    private void a(long j, Object obj, int i, long j2, int i2, long j3, int i3, long j4) {
        long j5;
        Object obj2;
        long[] jArr = this.b;
        Object[] objArr = this.c;
        int i4 = this.j;
        int i5 = 0;
        int i6 = this.m;
        while (true) {
            switch (MathUtils.a(2)) {
                case 0:
                    j5 = j2;
                    obj2 = objArr[i];
                    jArr[i] = j;
                    objArr[i] = obj;
                    break;
                case 1:
                    j5 = j3;
                    obj2 = objArr[i2];
                    jArr[i2] = j;
                    objArr[i2] = obj;
                    break;
                default:
                    j5 = j4;
                    obj2 = objArr[i3];
                    jArr[i3] = j;
                    objArr[i3] = obj;
                    break;
            }
            i = (int) (j5 & ((long) i4));
            j2 = jArr[i];
            if (j2 == 0) {
                jArr[i] = j5;
                objArr[i] = obj2;
                int i7 = this.a;
                this.a = i7 + 1;
                if (i7 >= this.k) {
                    b(this.d << 1);
                    return;
                }
                return;
            }
            i2 = f(j5);
            j3 = jArr[i2];
            if (j3 == 0) {
                jArr[i2] = j5;
                objArr[i2] = obj2;
                int i8 = this.a;
                this.a = i8 + 1;
                if (i8 >= this.k) {
                    b(this.d << 1);
                    return;
                }
                return;
            }
            i3 = g(j5);
            j4 = jArr[i3];
            if (j4 == 0) {
                jArr[i3] = j5;
                objArr[i3] = obj2;
                int i9 = this.a;
                this.a = i9 + 1;
                if (i9 >= this.k) {
                    b(this.d << 1);
                    return;
                }
                return;
            }
            i5++;
            if (i5 != i6) {
                j = j5;
                obj = obj2;
            } else {
                c(j5, obj2);
                return;
            }
        }
    }

    private void c(long j, Object obj) {
        if (this.e == this.l) {
            b(this.d << 1);
            a(j, obj);
            return;
        }
        int i = this.d + this.e;
        this.b[i] = j;
        this.c[i] = obj;
        this.e++;
        this.a++;
    }

    public Object a(long j) {
        if (j == 0) {
            if (this.g) {
                return this.f;
            }
            return null;
        }
        int iF = (int) (j & ((long) this.j));
        if (this.b[iF] != j) {
            iF = f(j);
            if (this.b[iF] != j) {
                iF = g(j);
                if (this.b[iF] != j) {
                    return d(j, null);
                }
            }
        }
        return this.c[iF];
    }

    private Object d(long j, Object obj) {
        long[] jArr = this.b;
        int i = this.d;
        int i2 = i + this.e;
        while (i < i2) {
            if (jArr[i] == j) {
                return this.c[i];
            }
            i++;
        }
        return obj;
    }

    public Object b(long j) {
        if (j == 0) {
            if (!this.g) {
                return null;
            }
            Object obj = this.f;
            this.f = null;
            this.g = false;
            this.a--;
            return obj;
        }
        int i = (int) (j & ((long) this.j));
        if (this.b[i] == j) {
            this.b[i] = 0;
            Object obj2 = this.c[i];
            this.c[i] = null;
            this.a--;
            return obj2;
        }
        int iF = f(j);
        if (this.b[iF] == j) {
            this.b[iF] = 0;
            Object obj3 = this.c[iF];
            this.c[iF] = null;
            this.a--;
            return obj3;
        }
        int iG = g(j);
        if (this.b[iG] == j) {
            this.b[iG] = 0;
            Object obj4 = this.c[iG];
            this.c[iG] = null;
            this.a--;
            return obj4;
        }
        return c(j);
    }

    Object c(long j) {
        long[] jArr = this.b;
        int i = this.d;
        int i2 = i + this.e;
        while (i < i2) {
            if (jArr[i] != j) {
                i++;
            } else {
                Object obj = this.c[i];
                a(i);
                this.a--;
                return obj;
            }
        }
        return null;
    }

    void a(int i) {
        this.e--;
        int i2 = this.d + this.e;
        if (i < i2) {
            this.b[i] = this.b[i2];
            this.c[i] = this.c[i2];
            this.c[i2] = null;
            return;
        }
        this.c[i] = null;
    }

    public void a() {
        if (this.a == 0) {
            return;
        }
        long[] jArr = this.b;
        Object[] objArr = this.c;
        int i = this.d + this.e;
        while (true) {
            int i2 = i;
            i--;
            if (i2 > 0) {
                jArr[i] = 0;
                objArr[i] = null;
            } else {
                this.a = 0;
                this.e = 0;
                this.f = null;
                this.g = false;
                return;
            }
        }
    }

    public boolean d(long j) {
        if (j == 0) {
            return this.g;
        }
        if (this.b[(int) (j & ((long) this.j))] != j) {
            if (this.b[f(j)] != j) {
                if (this.b[g(j)] != j) {
                    return e(j);
                }
                return true;
            }
            return true;
        }
        return true;
    }

    private boolean e(long j) {
        long[] jArr = this.b;
        int i = this.d;
        int i2 = i + this.e;
        while (i < i2) {
            if (jArr[i] == j) {
                return true;
            }
            i++;
        }
        return false;
    }

    private void b(int i) {
        int i2 = this.d + this.e;
        this.d = i;
        this.k = (int) (i * this.h);
        this.j = i - 1;
        this.i = 63 - Long.numberOfTrailingZeros(i);
        this.l = Math.max(3, ((int) Math.ceil(Math.log(i))) * 2);
        this.m = Math.max(Math.min(i, 8), ((int) Math.sqrt(i)) / 8);
        long[] jArr = this.b;
        Object[] objArr = this.c;
        this.b = new long[i + this.l];
        this.c = new Object[i + this.l];
        int i3 = this.a;
        this.a = this.g ? 1 : 0;
        this.e = 0;
        if (i3 > 0) {
            for (int i4 = 0; i4 < i2; i4++) {
                long j = jArr[i4];
                if (j != 0) {
                    b(j, objArr[i4]);
                }
            }
        }
    }

    private int f(long j) {
        long j2 = j * (-1262997959);
        return (int) ((j2 ^ (j2 >>> this.i)) & ((long) this.j));
    }

    private int g(long j) {
        long j2 = j * (-825114047);
        return (int) ((j2 ^ (j2 >>> this.i)) & ((long) this.j));
    }

    public int hashCode() {
        int iHashCode = 0;
        if (this.g && this.f != null) {
            iHashCode = 0 + this.f.hashCode();
        }
        long[] jArr = this.b;
        Object[] objArr = this.c;
        int i = this.d + this.e;
        for (int i2 = 0; i2 < i; i2++) {
            long j = jArr[i2];
            if (j != 0) {
                iHashCode += ((int) (j ^ (j >>> 32))) * 31;
                Object obj = objArr[i2];
                if (obj != null) {
                    iHashCode += obj.hashCode();
                }
            }
        }
        return iHashCode;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LongObjectMap)) {
            return false;
        }
        LongObjectMap longObjectMap = (LongObjectMap) obj;
        if (longObjectMap.a != this.a || longObjectMap.g != this.g) {
            return false;
        }
        if (this.g) {
            if (longObjectMap.f == null) {
                if (this.f != null) {
                    return false;
                }
            } else if (!longObjectMap.f.equals(this.f)) {
                return false;
            }
        }
        long[] jArr = this.b;
        Object[] objArr = this.c;
        int i = this.d + this.e;
        for (int i2 = 0; i2 < i; i2++) {
            long j = jArr[i2];
            if (j != 0) {
                Object obj2 = objArr[i2];
                if (obj2 == null) {
                    if (!longObjectMap.d(j) || longObjectMap.a(j) != null) {
                        return false;
                    }
                } else if (!obj2.equals(longObjectMap.a(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    public String toString() {
        if (this.a == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(32);
        sb.append('[');
        long[] jArr = this.b;
        Object[] objArr = this.c;
        int length = jArr.length;
        while (true) {
            int i = length;
            length--;
            if (i <= 0) {
                break;
            }
            long j = jArr[length];
            if (j != 0) {
                sb.append(j);
                sb.append('=');
                sb.append(objArr[length]);
                break;
            }
        }
        while (true) {
            int i2 = length;
            length--;
            if (i2 > 0) {
                long j2 = jArr[length];
                if (j2 != 0) {
                    sb.append(", ");
                    sb.append(j2);
                    sb.append('=');
                    sb.append(objArr[length]);
                }
            } else {
                sb.append(']');
                return sb.toString();
            }
        }
    }

    @Override // java.lang.Iterable
    public Iterator iterator() {
        return b();
    }

    public LongObjectMapEntries b() {
        if (this.n == null) {
            this.n = new LongObjectMapEntries(this);
            this.o = new LongObjectMapEntries(this);
        }
        if (!this.n.e) {
            this.n.b();
            this.n.e = true;
            this.o.e = false;
            return this.n;
        }
        this.o.b();
        this.o.e = true;
        this.n.e = false;
        return this.o;
    }
}
