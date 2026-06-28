package com.corrodinggames.rts.appFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.eq */
/* JADX INFO: loaded from: classes.dex */
public final class MultiTouchController$PositionAndScale {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    float f149a;
    float b;
    float c;
    float d;
    float e;
    float f;
    boolean g;
    boolean h;
    boolean i;

    protected final void a(float f, float f2, float f3, float f4, float f5, float f6) {
        this.f149a = f;
        this.b = f2;
        if (f3 == 0.0f) {
            f3 = 1.0f;
        }
        this.c = f3;
        if (f4 == 0.0f) {
            f4 = 1.0f;
        }
        this.d = f4;
        this.e = f5 != 0.0f ? f5 : 1.0f;
        this.f = f6;
    }
}
