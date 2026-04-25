package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: game-lib.jar:android/graphics/RectF.class */
public class RectF implements Parcelable {
    public float a;
    public float b;
    public float c;
    public float d;
    public static final Parcelable.Creator e = new Parcelable.Creator() { // from class: android.graphics.RectF.1
        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public RectF createFromParcel(Parcel parcel) {
            RectF rectF = new RectF();
            rectF.a(parcel);
            return rectF;
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public RectF[] newArray(int i) {
            return new RectF[i];
        }
    };

    public RectF() {
    }

    public RectF(float f, float f2, float f3, float f4) {
        this.a = f;
        this.b = f2;
        this.c = f3;
        this.d = f4;
    }

    public RectF(RectF rectF) {
        this.a = rectF.a;
        this.b = rectF.b;
        this.c = rectF.c;
        this.d = rectF.d;
    }

    public RectF(Rect rect) {
        this.a = rect.a;
        this.b = rect.b;
        this.c = rect.c;
        this.d = rect.d;
    }

    public String toString() {
        return "RectF(" + this.a + ", " + this.b + ", " + this.c + ", " + this.d + ")";
    }

    public final boolean a() {
        return this.a >= this.c || this.b >= this.d;
    }

    public final float b() {
        return this.c - this.a;
    }

    public final float c() {
        return this.d - this.b;
    }

    public final float d() {
        return (this.a + this.c) * 0.5f;
    }

    public final float e() {
        return (this.b + this.d) * 0.5f;
    }

    public void f() {
        this.d = 0.0f;
        this.b = 0.0f;
        this.c = 0.0f;
        this.a = 0.0f;
    }

    public void a(float f, float f2, float f3, float f4) {
        this.a = f;
        this.b = f2;
        this.c = f3;
        this.d = f4;
    }

    public void a(RectF rectF) {
        this.a = rectF.a;
        this.b = rectF.b;
        this.c = rectF.c;
        this.d = rectF.d;
    }

    public void a(Rect rect) {
        this.a = rect.a;
        this.b = rect.b;
        this.c = rect.c;
        this.d = rect.d;
    }

    public void a(float f, float f2) {
        this.a += f;
        this.b += f2;
        this.c += f;
        this.d += f2;
    }

    public boolean b(float f, float f2) {
        return this.a < this.c && this.b < this.d && f >= this.a && f < this.c && f2 >= this.b && f2 < this.d;
    }

    public boolean b(float f, float f2, float f3, float f4) {
        if (this.a >= f3 || f >= this.c || this.b >= f4 || f2 >= this.d) {
            return false;
        }
        if (this.a < f) {
            this.a = f;
        }
        if (this.b < f2) {
            this.b = f2;
        }
        if (this.c > f3) {
            this.c = f3;
        }
        if (this.d > f4) {
            this.d = f4;
            return true;
        }
        return true;
    }

    public boolean b(RectF rectF) {
        return b(rectF.a, rectF.b, rectF.c, rectF.d);
    }

    public static boolean a(RectF rectF, RectF rectF2) {
        return rectF.a < rectF2.c && rectF2.a < rectF.c && rectF.b < rectF2.d && rectF2.b < rectF.d;
    }

    public void c(float f, float f2) {
        if (f < this.a) {
            this.a = f;
        } else if (f > this.c) {
            this.c = f;
        }
        if (f2 < this.b) {
            this.b = f2;
        } else if (f2 > this.d) {
            this.d = f2;
        }
    }

    public void g() {
        if (this.a > this.c) {
            float f = this.a;
            this.a = this.c;
            this.c = f;
        }
        if (this.b > this.d) {
            float f2 = this.b;
            this.b = this.d;
            this.d = f2;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.a);
        parcel.writeFloat(this.b);
        parcel.writeFloat(this.c);
        parcel.writeFloat(this.d);
    }

    public void a(Parcel parcel) {
        this.a = parcel.readFloat();
        this.b = parcel.readFloat();
        this.c = parcel.readFloat();
        this.d = parcel.readFloat();
    }
}
