package com.corrodinggames.rts.java.audio.util;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/b.class */
public class FloatArray {
    public float[] a;
    public int b;
    public boolean c;

    public FloatArray() {
        this(true, 16);
    }

    public FloatArray(int i) {
        this(true, i);
    }

    public FloatArray(boolean z, int i) {
        this.c = z;
        this.a = new float[i];
    }

    public void a(float f) {
        float[] fArrA = this.a;
        if (this.b == fArrA.length) {
            fArrA = a(Math.max(8, (int) (this.b * 1.75f)));
        }
        int i = this.b;
        this.b = i + 1;
        fArrA[i] = f;
    }

    public void a(int i, float f) {
        if (i >= this.b) {
            throw new IndexOutOfBoundsException("index can't FastArrayList >= size: " + i + " >= " + this.b);
        }
        this.a[i] = f;
    }

    public void b(int i, float f) {
        if (i > this.b) {
            throw new IndexOutOfBoundsException("index can't FastArrayList > size: " + i + " > " + this.b);
        }
        float[] fArrA = this.a;
        if (this.b == fArrA.length) {
            fArrA = a(Math.max(8, (int) (this.b * 1.75f)));
        }
        if (this.c) {
            System.arraycopy(fArrA, i, fArrA, i + 1, this.b - i);
        } else {
            fArrA[this.b] = fArrA[i];
        }
        this.b++;
        fArrA[i] = f;
    }

    public float a() {
        float[] fArr = this.a;
        int i = this.b - 1;
        this.b = i;
        return fArr[i];
    }

    public float b() {
        if (this.b == 0) {
            throw new IllegalStateException("Array is empty.");
        }
        return this.a[0];
    }

    public void c() {
        this.b = 0;
    }

    protected float[] a(int i) {
        float[] fArr = new float[i];
        System.arraycopy(this.a, 0, fArr, 0, Math.min(this.b, fArr.length));
        this.a = fArr;
        return fArr;
    }

    public int hashCode() {
        if (!this.c) {
            return super.hashCode();
        }
        float[] fArr = this.a;
        int iFloatToIntBits = 1;
        int i = this.b;
        for (int i2 = 0; i2 < i; i2++) {
            iFloatToIntBits = (iFloatToIntBits * 31) + Float.floatToIntBits(fArr[i2]);
        }
        return iFloatToIntBits;
    }

    public boolean equals(Object obj) {
        int i;
        if (obj == this) {
            return true;
        }
        if (!this.c || !(obj instanceof FloatArray)) {
            return false;
        }
        FloatArray floatArray = (FloatArray) obj;
        if (!floatArray.c || (i = this.b) != floatArray.b) {
            return false;
        }
        float[] fArr = this.a;
        float[] fArr2 = floatArray.a;
        for (int i2 = 0; i2 < i; i2++) {
            if (fArr[i2] != fArr2[i2]) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        if (this.b == 0) {
            return "[]";
        }
        float[] fArr = this.a;
        StringBuilder sb = new StringBuilder(32);
        sb.append('[');
        sb.append(fArr[0]);
        for (int i = 1; i < this.b; i++) {
            sb.append(", ");
            sb.append(fArr[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}
