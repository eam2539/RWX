package android.graphics;

/* JADX INFO: loaded from: game-lib.jar:android/graphics/Rect.class */
public final class Rect {
    public int a;
    public int b;
    public int c;
    public int d;

    public Rect() {
    }

    public Rect(int i, int i2, int i3, int i4) {
        this.a = i;
        this.b = i2;
        this.c = i3;
        this.d = i4;
    }

    public Rect(Rect rect) {
        this.a = rect.a;
        this.b = rect.b;
        this.c = rect.c;
        this.d = rect.d;
    }

    public boolean equals(Object obj) {
        Rect rect = (Rect) obj;
        return rect != null && this.a == rect.a && this.b == rect.b && this.c == rect.c && this.d == rect.d;
    }

    public String toString() {
        return "Rect(" + this.a + ", " + this.b + ", " + this.c + ", " + this.d + ")";
    }

    public final boolean a() {
        return this.a >= this.c || this.b >= this.d;
    }

    public final int b() {
        return this.c - this.a;
    }

    public final int c() {
        return this.d - this.b;
    }

    public final int d() {
        return (this.a + this.c) >> 1;
    }

    public final int e() {
        return (this.b + this.d) >> 1;
    }

    public final float f() {
        return (this.a + this.c) * 0.5f;
    }

    public final float g() {
        return (this.b + this.d) * 0.5f;
    }

    public void h() {
        this.d = 0;
        this.b = 0;
        this.c = 0;
        this.a = 0;
    }

    public void a(int i, int i2, int i3, int i4) {
        this.a = i;
        this.b = i2;
        this.c = i3;
        this.d = i4;
    }

    public void a(Rect rect) {
        this.a = rect.a;
        this.b = rect.b;
        this.c = rect.c;
        this.d = rect.d;
    }

    public void a(int i, int i2) {
        this.a += i;
        this.b += i2;
        this.c += i;
        this.d += i2;
    }

    public boolean b(int i, int i2) {
        return this.a < this.c && this.b < this.d && i >= this.a && i < this.c && i2 >= this.b && i2 < this.d;
    }

    public boolean b(Rect rect) {
        return this.a < this.c && this.b < this.d && this.a <= rect.a && this.b <= rect.b && this.c >= rect.c && this.d >= rect.d;
    }

    public boolean b(int i, int i2, int i3, int i4) {
        return this.a < i3 && i < this.c && this.b < i4 && i2 < this.d;
    }
}
