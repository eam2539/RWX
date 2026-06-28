package com.corrodinggames.rts.java.audio.util;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/o.class */
public class ObjectMap implements Iterable {
    public int a;
    Object[] b;
    Object[] c;
    int d;
    int e;
    private float f;
    private int g;
    private int h;
    private int i;
    private int j;
    private int k;
    private ObjectMapEntries l;
    private ObjectMapEntries m;

    public ObjectMap() {
        this(51, 0.8f);
    }

    public ObjectMap(int i, float f) {
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
        this.f = f;
        this.i = (int) (this.d * f);
        this.h = this.d - 1;
        this.g = 31 - Integer.numberOfTrailingZeros(this.d);
        this.j = Math.max(3, ((int) Math.ceil(Math.log(this.d))) * 2);
        this.k = Math.max(Math.min(this.d, 8), ((int) Math.sqrt(this.d)) / 8);
        this.b = new Object[this.d + this.j];
        this.c = new Object[this.b.length];
    }

    public Object a(Object obj, Object obj2) {
        if (obj == null) {
            throw new IllegalArgumentException("key cannot FastArrayList null.");
        }
        return b(obj, obj2);
    }

    private Object b(Object obj, Object obj2) {
        Object[] objArr = this.b;
        int iHashCode = obj.hashCode();
        int i = iHashCode & this.h;
        Object obj3 = objArr[i];
        if (obj.equals(obj3)) {
            Object obj4 = this.c[i];
            this.c[i] = obj2;
            return obj4;
        }
        int iC = c(iHashCode);
        Object obj5 = objArr[iC];
        if (obj.equals(obj5)) {
            Object obj6 = this.c[iC];
            this.c[iC] = obj2;
            return obj6;
        }
        int iD = d(iHashCode);
        Object obj7 = objArr[iD];
        if (obj.equals(obj7)) {
            Object obj8 = this.c[iD];
            this.c[iD] = obj2;
            return obj8;
        }
        int i2 = this.d;
        int i3 = i2 + this.e;
        while (i2 < i3) {
            if (!obj.equals(objArr[i2])) {
                i2++;
            } else {
                Object obj9 = this.c[i2];
                this.c[i2] = obj2;
                return obj9;
            }
        }
        if (obj3 == null) {
            objArr[i] = obj;
            this.c[i] = obj2;
            int i4 = this.a;
            this.a = i4 + 1;
            if (i4 >= this.i) {
                b(this.d << 1);
                return null;
            }
            return null;
        }
        if (obj5 == null) {
            objArr[iC] = obj;
            this.c[iC] = obj2;
            int i5 = this.a;
            this.a = i5 + 1;
            if (i5 >= this.i) {
                b(this.d << 1);
                return null;
            }
            return null;
        }
        if (obj7 == null) {
            objArr[iD] = obj;
            this.c[iD] = obj2;
            int i6 = this.a;
            this.a = i6 + 1;
            if (i6 >= this.i) {
                b(this.d << 1);
                return null;
            }
            return null;
        }
        a(obj, obj2, i, obj3, iC, obj5, iD, obj7);
        return null;
    }

    private void c(Object obj, Object obj2) {
        int iHashCode = obj.hashCode();
        int i = iHashCode & this.h;
        Object obj3 = this.b[i];
        if (obj3 == null) {
            this.b[i] = obj;
            this.c[i] = obj2;
            int i2 = this.a;
            this.a = i2 + 1;
            if (i2 >= this.i) {
                b(this.d << 1);
                return;
            }
            return;
        }
        int iC = c(iHashCode);
        Object obj4 = this.b[iC];
        if (obj4 == null) {
            this.b[iC] = obj;
            this.c[iC] = obj2;
            int i3 = this.a;
            this.a = i3 + 1;
            if (i3 >= this.i) {
                b(this.d << 1);
                return;
            }
            return;
        }
        int iD = d(iHashCode);
        Object obj5 = this.b[iD];
        if (obj5 == null) {
            this.b[iD] = obj;
            this.c[iD] = obj2;
            int i4 = this.a;
            this.a = i4 + 1;
            if (i4 >= this.i) {
                b(this.d << 1);
                return;
            }
            return;
        }
        a(obj, obj2, i, obj3, iC, obj4, iD, obj5);
    }

    private void a(Object obj, Object obj2, int i, Object obj3, int i2, Object obj4, int i3, Object obj5) {
        Object obj6;
        Object obj7;
        Object[] objArr = this.b;
        Object[] objArr2 = this.c;
        int i4 = this.h;
        int i5 = 0;
        int i6 = this.k;
        while (true) {
            switch (MathUtils.a(2)) {
                case 0:
                    obj6 = obj3;
                    obj7 = objArr2[i];
                    objArr[i] = obj;
                    objArr2[i] = obj2;
                    break;
                case 1:
                    obj6 = obj4;
                    obj7 = objArr2[i2];
                    objArr[i2] = obj;
                    objArr2[i2] = obj2;
                    break;
                default:
                    obj6 = obj5;
                    obj7 = objArr2[i3];
                    objArr[i3] = obj;
                    objArr2[i3] = obj2;
                    break;
            }
            int iHashCode = obj6.hashCode();
            i = iHashCode & i4;
            obj3 = objArr[i];
            if (obj3 == null) {
                objArr[i] = obj6;
                objArr2[i] = obj7;
                int i7 = this.a;
                this.a = i7 + 1;
                if (i7 >= this.i) {
                    b(this.d << 1);
                    return;
                }
                return;
            }
            i2 = c(iHashCode);
            obj4 = objArr[i2];
            if (obj4 == null) {
                objArr[i2] = obj6;
                objArr2[i2] = obj7;
                int i8 = this.a;
                this.a = i8 + 1;
                if (i8 >= this.i) {
                    b(this.d << 1);
                    return;
                }
                return;
            }
            i3 = d(iHashCode);
            obj5 = objArr[i3];
            if (obj5 == null) {
                objArr[i3] = obj6;
                objArr2[i3] = obj7;
                int i9 = this.a;
                this.a = i9 + 1;
                if (i9 >= this.i) {
                    b(this.d << 1);
                    return;
                }
                return;
            }
            i5++;
            if (i5 != i6) {
                obj = obj6;
                obj2 = obj7;
            } else {
                d(obj6, obj7);
                return;
            }
        }
    }

    private void d(Object obj, Object obj2) {
        if (this.e == this.j) {
            b(this.d << 1);
            b(obj, obj2);
            return;
        }
        int i = this.d + this.e;
        this.b[i] = obj;
        this.c[i] = obj2;
        this.e++;
        this.a++;
    }

    public Object a(Object obj) {
        int iHashCode = obj.hashCode();
        int iC = iHashCode & this.h;
        if (!obj.equals(this.b[iC])) {
            iC = c(iHashCode);
            if (!obj.equals(this.b[iC])) {
                iC = d(iHashCode);
                if (!obj.equals(this.b[iC])) {
                    return e(obj, null);
                }
            }
        }
        return this.c[iC];
    }

    private Object e(Object obj, Object obj2) {
        Object[] objArr = this.b;
        int i = this.d;
        int i2 = i + this.e;
        while (i < i2) {
            if (obj.equals(objArr[i])) {
                return this.c[i];
            }
            i++;
        }
        return obj2;
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

    public boolean b(Object obj) {
        int iHashCode = obj.hashCode();
        if (!obj.equals(this.b[iHashCode & this.h])) {
            if (!obj.equals(this.b[c(iHashCode)])) {
                if (obj.equals(this.b[d(iHashCode)])) {
                    return true;
                }
                return c(obj);
            }
            return true;
        }
        return true;
    }

    private boolean c(Object obj) {
        Object[] objArr = this.b;
        int i = this.d;
        int i2 = i + this.e;
        while (i < i2) {
            if (obj.equals(objArr[i])) {
                return true;
            }
            i++;
        }
        return false;
    }

    private void b(int i) {
        int i2 = this.d + this.e;
        this.d = i;
        this.i = (int) (i * this.f);
        this.h = i - 1;
        this.g = 31 - Integer.numberOfTrailingZeros(i);
        this.j = Math.max(3, ((int) Math.ceil(Math.log(i))) * 2);
        this.k = Math.max(Math.min(i, 8), ((int) Math.sqrt(i)) / 8);
        Object[] objArr = this.b;
        Object[] objArr2 = this.c;
        this.b = new Object[i + this.j];
        this.c = new Object[i + this.j];
        int i3 = this.a;
        this.a = 0;
        this.e = 0;
        if (i3 > 0) {
            for (int i4 = 0; i4 < i2; i4++) {
                Object obj = objArr[i4];
                if (obj != null) {
                    c(obj, objArr2[i4]);
                }
            }
        }
    }

    private int c(int i) {
        int i2 = i * (-1262997959);
        return (i2 ^ (i2 >>> this.g)) & this.h;
    }

    private int d(int i) {
        int i2 = i * (-825114047);
        return (i2 ^ (i2 >>> this.g)) & this.h;
    }

    public int hashCode() {
        int iHashCode = 0;
        Object[] objArr = this.b;
        Object[] objArr2 = this.c;
        int i = this.d + this.e;
        for (int i2 = 0; i2 < i; i2++) {
            Object obj = objArr[i2];
            if (obj != null) {
                iHashCode += obj.hashCode() * 31;
                Object obj2 = objArr2[i2];
                if (obj2 != null) {
                    iHashCode += obj2.hashCode();
                }
            }
        }
        return iHashCode;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ObjectMap)) {
            return false;
        }
        ObjectMap objectMap = (ObjectMap) obj;
        if (objectMap.a != this.a) {
            return false;
        }
        Object[] objArr = this.b;
        Object[] objArr2 = this.c;
        int i = this.d + this.e;
        for (int i2 = 0; i2 < i; i2++) {
            Object obj2 = objArr[i2];
            if (obj2 != null) {
                Object obj3 = objArr2[i2];
                if (obj3 == null) {
                    if (!objectMap.b(obj2) || objectMap.a(obj2) != null) {
                        return false;
                    }
                } else if (!obj3.equals(objectMap.a(obj2))) {
                    return false;
                }
            }
        }
        return true;
    }

    public String toString() {
        return a(", ", true);
    }

    private String a(String str, boolean z) {
        if (this.a == 0) {
            return z ? "{}" : VariableScope.nullOrMissingString;
        }
        StringBuilder sb = new StringBuilder(32);
        if (z) {
            sb.append('{');
        }
        Object[] objArr = this.b;
        Object[] objArr2 = this.c;
        int length = objArr.length;
        while (true) {
            int i = length;
            length--;
            if (i > 0) {
                Object obj = objArr[length];
                if (obj != null) {
                    sb.append(obj);
                    sb.append('=');
                    sb.append(objArr2[length]);
                    break;
                }
            } else {
                break;
            }
        }
        while (true) {
            int i2 = length;
            length--;
            if (i2 <= 0) {
                break;
            }
            Object obj2 = objArr[length];
            if (obj2 != null) {
                sb.append(str);
                sb.append(obj2);
                sb.append('=');
                sb.append(objArr2[length]);
            }
        }
        if (z) {
            sb.append('}');
        }
        return sb.toString();
    }

    @Override // java.lang.Iterable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public ObjectMapEntries iterator() {
        return b();
    }

    public ObjectMapEntries b() {
        if (this.l == null) {
            this.l = new ObjectMapEntries(this);
            this.m = new ObjectMapEntries(this);
        }
        if (!this.l.f) {
            this.l.c();
            this.l.f = true;
            this.m.f = false;
            return this.l;
        }
        this.m.c();
        this.m.f = true;
        this.l.f = false;
        return this.m;
    }
}
