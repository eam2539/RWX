package com.corrodinggames.rts.java.audio.util;

import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/e.class */
public class IntObjectMap implements Iterable {
    public int a;
    int[] b;
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
    private IntObjectMapEntries n;
    private IntObjectMapEntries o;

    public IntObjectMap() {
        this(51, 0.8f);
    }

    public IntObjectMap(int i, float f) {
        if (i < 0) {
            throw new IllegalArgumentException("initialCapacity must FastArrayList >= 0: " + i);
        }
        int iB = MathUtils.b((int) Math.ceil(i / f));
        if (iB > 1073741824) {
            throw new IllegalArgumentException("initialCapacity is too large: " + iB);
        }
        this.d = iB;
        if (f <= 0.0f) {
            throw new IllegalArgumentException("loadFactor must FastArrayList > 0: " + f);
        }
        this.h = f;
        this.k = (int) (this.d * f);
        this.j = this.d - 1;
        this.i = 31 - Integer.numberOfTrailingZeros(this.d);
        this.l = Math.max(3, ((int) Math.ceil(Math.log(this.d))) * 2);
        this.m = Math.max(Math.min(this.d, 8), ((int) Math.sqrt(this.d)) / 8);
        this.b = new int[this.d + this.l];
        this.c = new Object[this.b.length];
    }

    public Object a(int i, Object obj) {
        if (i == 0) {
            Object obj2 = this.f;
            this.f = obj;
            if (!this.g) {
                this.g = true;
                this.a++;
            }
            return obj2;
        }
        int[] iArr = this.b;
        int i2 = i & this.j;
        int i3 = iArr[i2];
        if (i3 == i) {
            Object obj3 = this.c[i2];
            this.c[i2] = obj;
            return obj3;
        }
        int iH = h(i);
        int i4 = iArr[iH];
        if (i4 == i) {
            Object obj4 = this.c[iH];
            this.c[iH] = obj;
            return obj4;
        }
        int i5 = i(i);
        int i6 = iArr[i5];
        if (i6 == i) {
            Object obj5 = this.c[i5];
            this.c[i5] = obj;
            return obj5;
        }
        int i7 = this.d;
        int i8 = i7 + this.e;
        while (i7 < i8) {
            if (iArr[i7] != i) {
                i7++;
            } else {
                Object obj6 = this.c[i7];
                this.c[i7] = obj;
                return obj6;
            }
        }
        if (i3 == 0) {
            iArr[i2] = i;
            this.c[i2] = obj;
            int i9 = this.a;
            this.a = i9 + 1;
            if (i9 >= this.k) {
                g(this.d << 1);
                return null;
            }
            return null;
        }
        if (i4 == 0) {
            iArr[iH] = i;
            this.c[iH] = obj;
            int i10 = this.a;
            this.a = i10 + 1;
            if (i10 >= this.k) {
                g(this.d << 1);
                return null;
            }
            return null;
        }
        if (i6 == 0) {
            iArr[i5] = i;
            this.c[i5] = obj;
            int i11 = this.a;
            this.a = i11 + 1;
            if (i11 >= this.k) {
                g(this.d << 1);
                return null;
            }
            return null;
        }
        a(i, obj, i2, i3, iH, i4, i5, i6);
        return null;
    }

    private void b(int i, Object obj) {
        if (i == 0) {
            this.f = obj;
            this.g = true;
            return;
        }
        int i2 = i & this.j;
        int i3 = this.b[i2];
        if (i3 == 0) {
            this.b[i2] = i;
            this.c[i2] = obj;
            int i4 = this.a;
            this.a = i4 + 1;
            if (i4 >= this.k) {
                g(this.d << 1);
                return;
            }
            return;
        }
        int iH = h(i);
        int i5 = this.b[iH];
        if (i5 == 0) {
            this.b[iH] = i;
            this.c[iH] = obj;
            int i6 = this.a;
            this.a = i6 + 1;
            if (i6 >= this.k) {
                g(this.d << 1);
                return;
            }
            return;
        }
        int i7 = i(i);
        int i8 = this.b[i7];
        if (i8 == 0) {
            this.b[i7] = i;
            this.c[i7] = obj;
            int i9 = this.a;
            this.a = i9 + 1;
            if (i9 >= this.k) {
                g(this.d << 1);
                return;
            }
            return;
        }
        a(i, obj, i2, i3, iH, i5, i7, i8);
    }

    private void a(int i, Object obj, int i2, int i3, int i4, int i5, int i6, int i7) {
        int i8;
        Object obj2;
        int[] iArr = this.b;
        Object[] objArr = this.c;
        int i9 = this.j;
        int i10 = 0;
        int i11 = this.m;
        while (true) {
            switch (MathUtils.a(2)) {
                case 0:
                    i8 = i3;
                    obj2 = objArr[i2];
                    iArr[i2] = i;
                    objArr[i2] = obj;
                    break;
                case 1:
                    i8 = i5;
                    obj2 = objArr[i4];
                    iArr[i4] = i;
                    objArr[i4] = obj;
                    break;
                default:
                    i8 = i7;
                    obj2 = objArr[i6];
                    iArr[i6] = i;
                    objArr[i6] = obj;
                    break;
            }
            i2 = i8 & i9;
            i3 = iArr[i2];
            if (i3 == 0) {
                iArr[i2] = i8;
                objArr[i2] = obj2;
                int i12 = this.a;
                this.a = i12 + 1;
                if (i12 >= this.k) {
                    g(this.d << 1);
                    return;
                }
                return;
            }
            i4 = h(i8);
            i5 = iArr[i4];
            if (i5 == 0) {
                iArr[i4] = i8;
                objArr[i4] = obj2;
                int i13 = this.a;
                this.a = i13 + 1;
                if (i13 >= this.k) {
                    g(this.d << 1);
                    return;
                }
                return;
            }
            i6 = i(i8);
            i7 = iArr[i6];
            if (i7 == 0) {
                iArr[i6] = i8;
                objArr[i6] = obj2;
                int i14 = this.a;
                this.a = i14 + 1;
                if (i14 >= this.k) {
                    g(this.d << 1);
                    return;
                }
                return;
            }
            i10++;
            if (i10 != i11) {
                i = i8;
                obj = obj2;
            } else {
                c(i8, obj2);
                return;
            }
        }
    }

    private void c(int i, Object obj) {
        if (this.e == this.l) {
            g(this.d << 1);
            a(i, obj);
            return;
        }
        int i2 = this.d + this.e;
        this.b[i2] = i;
        this.c[i2] = obj;
        this.e++;
        this.a++;
    }

    public Object a(int i) {
        if (i == 0) {
            if (this.g) {
                return this.f;
            }
            return null;
        }
        int iH = i & this.j;
        if (this.b[iH] != i) {
            iH = h(i);
            if (this.b[iH] != i) {
                iH = i(i);
                if (this.b[iH] != i) {
                    return d(i, null);
                }
            }
        }
        return this.c[iH];
    }

    private Object d(int i, Object obj) {
        int[] iArr = this.b;
        int i2 = this.d;
        int i3 = i2 + this.e;
        while (i2 < i3) {
            if (iArr[i2] == i) {
                return this.c[i2];
            }
            i2++;
        }
        return obj;
    }

    public Object b(int i) {
        if (i == 0) {
            if (!this.g) {
                return null;
            }
            Object obj = this.f;
            this.f = null;
            this.g = false;
            this.a--;
            return obj;
        }
        int i2 = i & this.j;
        if (this.b[i2] == i) {
            this.b[i2] = 0;
            Object obj2 = this.c[i2];
            this.c[i2] = null;
            this.a--;
            return obj2;
        }
        int iH = h(i);
        if (this.b[iH] == i) {
            this.b[iH] = 0;
            Object obj3 = this.c[iH];
            this.c[iH] = null;
            this.a--;
            return obj3;
        }
        int i3 = i(i);
        if (this.b[i3] == i) {
            this.b[i3] = 0;
            Object obj4 = this.c[i3];
            this.c[i3] = null;
            this.a--;
            return obj4;
        }
        return c(i);
    }

    Object c(int i) {
        int[] iArr = this.b;
        int i2 = this.d;
        int i3 = i2 + this.e;
        while (i2 < i3) {
            if (iArr[i2] != i) {
                i2++;
            } else {
                Object obj = this.c[i2];
                d(i2);
                this.a--;
                return obj;
            }
        }
        return null;
    }

    void d(int i) {
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
        int[] iArr = this.b;
        Object[] objArr = this.c;
        int i = this.d + this.e;
        while (true) {
            int i2 = i;
            i--;
            if (i2 > 0) {
                iArr[i] = 0;
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

    public boolean e(int i) {
        if (i == 0) {
            return this.g;
        }
        if (this.b[i & this.j] != i) {
            if (this.b[h(i)] != i) {
                if (this.b[i(i)] != i) {
                    return f(i);
                }
                return true;
            }
            return true;
        }
        return true;
    }

    private boolean f(int i) {
        int[] iArr = this.b;
        int i2 = this.d;
        int i3 = i2 + this.e;
        while (i2 < i3) {
            if (iArr[i2] == i) {
                return true;
            }
            i2++;
        }
        return false;
    }

    private void g(int i) {
        int i2 = this.d + this.e;
        this.d = i;
        this.k = (int) (i * this.h);
        this.j = i - 1;
        this.i = 31 - Integer.numberOfTrailingZeros(i);
        this.l = Math.max(3, ((int) Math.ceil(Math.log(i))) * 2);
        this.m = Math.max(Math.min(i, 8), ((int) Math.sqrt(i)) / 8);
        int[] iArr = this.b;
        Object[] objArr = this.c;
        this.b = new int[i + this.l];
        this.c = new Object[i + this.l];
        int i3 = this.a;
        this.a = this.g ? 1 : 0;
        this.e = 0;
        if (i3 > 0) {
            for (int i4 = 0; i4 < i2; i4++) {
                int i5 = iArr[i4];
                if (i5 != 0) {
                    b(i5, objArr[i4]);
                }
            }
        }
    }

    private int h(int i) {
        int i2 = i * (-1262997959);
        return (i2 ^ (i2 >>> this.i)) & this.j;
    }

    private int i(int i) {
        int i2 = i * (-825114047);
        return (i2 ^ (i2 >>> this.i)) & this.j;
    }

    public int hashCode() {
        int iHashCode = 0;
        if (this.g && this.f != null) {
            iHashCode = 0 + this.f.hashCode();
        }
        int[] iArr = this.b;
        Object[] objArr = this.c;
        int i = this.d + this.e;
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = iArr[i2];
            if (i3 != 0) {
                iHashCode += i3 * 31;
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
        if (!(obj instanceof IntObjectMap)) {
            return false;
        }
        IntObjectMap intObjectMap = (IntObjectMap) obj;
        if (intObjectMap.a != this.a || intObjectMap.g != this.g) {
            return false;
        }
        if (this.g) {
            if (intObjectMap.f == null) {
                if (this.f != null) {
                    return false;
                }
            } else if (!intObjectMap.f.equals(this.f)) {
                return false;
            }
        }
        int[] iArr = this.b;
        Object[] objArr = this.c;
        int i = this.d + this.e;
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = iArr[i2];
            if (i3 != 0) {
                Object obj2 = objArr[i2];
                if (obj2 == null) {
                    if (!intObjectMap.e(i3) || intObjectMap.a(i3) != null) {
                        return false;
                    }
                } else if (!obj2.equals(intObjectMap.a(i3))) {
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
        int[] iArr = this.b;
        Object[] objArr = this.c;
        int length = iArr.length;
        if (this.g) {
            sb.append("0=");
            sb.append(this.f);
        } else {
            while (true) {
                int i = length;
                length--;
                if (i > 0) {
                    int i2 = iArr[length];
                    if (i2 != 0) {
                        sb.append(i2);
                        sb.append('=');
                        sb.append(objArr[length]);
                        break;
                    }
                }
            }
        }
        while (true) {
            int i3 = length;
            length--;
            if (i3 > 0) {
                int i4 = iArr[length];
                if (i4 != 0) {
                    sb.append(", ");
                    sb.append(i4);
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

    public IntObjectMapEntries b() {
        if (this.n == null) {
            this.n = new IntObjectMapEntries(this);
            this.o = new IntObjectMapEntries(this);
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
