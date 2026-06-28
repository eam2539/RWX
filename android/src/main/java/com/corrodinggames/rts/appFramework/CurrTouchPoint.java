package com.corrodinggames.rts.appFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.ep */
/* JADX INFO: loaded from: classes.dex */
public final class CurrTouchPoint {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f148a;
    public float[] b = new float[10];
    public float[] c = new float[10];
    float[] d = new float[10];
    int[] e = new int[10];
    float f;
    float g;
    float h;
    float i;
    float j;
    float k;
    float l;
    float m;
    public boolean n;
    boolean o;
    public boolean p;
    public int q;
    boolean r;
    boolean s;
    boolean t;
    int u;
    long v;

    public CurrTouchPoint() {
        for (int i = 0; i < this.b.length; i++) {
            this.b[i] = 40.0f;
        }
        for (int i2 = 0; i2 < this.c.length; i2++) {
            this.c[i2] = 40.0f;
        }
    }

    public final void a(float f, float f2, boolean z) {
        this.u = 0;
        this.f148a = z ? 1 : 0;
        MotionEventCompat.M[0] = 2;
        this.b[0] = f;
        this.c[0] = f2;
        this.d[0] = 0.0f;
        this.e[0] = 0;
        this.n = z;
        this.o = false;
        if (this.n) {
            this.p = this.n;
        }
        if (this.f148a > 0) {
            this.q = this.f148a;
        }
        this.f = this.b[0];
        this.g = this.c[0];
        this.h = this.d[0];
        this.j = 0.0f;
        this.i = 0.0f;
        this.t = false;
        this.s = false;
        this.r = false;
    }

    public final void a(CurrTouchPoint currTouchPoint) {
        this.f148a = currTouchPoint.f148a;
        for (int i = 0; i < this.f148a; i++) {
            this.b[i] = currTouchPoint.b[i];
            this.c[i] = currTouchPoint.c[i];
            this.d[i] = currTouchPoint.d[i];
            this.e[i] = currTouchPoint.e[i];
        }
        this.f = currTouchPoint.f;
        this.g = currTouchPoint.g;
        this.h = currTouchPoint.h;
        this.i = currTouchPoint.i;
        this.j = currTouchPoint.j;
        this.k = currTouchPoint.k;
        this.l = currTouchPoint.l;
        this.m = currTouchPoint.m;
        this.n = currTouchPoint.n;
        this.u = currTouchPoint.u;
        this.o = currTouchPoint.o;
        this.s = currTouchPoint.s;
        this.r = currTouchPoint.r;
        this.t = currTouchPoint.t;
        this.v = currTouchPoint.v;
        if (this.n) {
            this.p = this.n;
        }
        if (this.f148a > 0) {
            this.q = this.f148a;
        }
    }

    /* JADX INFO: renamed from: a */
    public final float x() {
        if (this.o) {
            return this.i;
        }
        return 0.0f;
    }

    /* JADX INFO: renamed from: b */
    public final float y() {
        if (this.o) {
            return this.j;
        }
        return 0.0f;
    }
}
