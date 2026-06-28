package com.corrodinggames.rts.gameFramework.gl.font;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.e */
/* JADX INFO: loaded from: classes.dex */
public final class FontPage {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    int f552a;
    Canvas c;
    public Bitmap d;
    int e;
    int f;
    int j;
    int k;
    int l;
    int m;
    int n;
    int g = 0;
    int h = 0;
    int i = 0;
    int b = 512;

    public FontPage(int i, int i2, int i3, int i4, int i5) {
        this.l = i;
        this.e = 512 / i2;
        this.f = 512 / i3;
        this.j = i2;
        this.k = i3;
        this.m = i4;
        this.n = i5;
    }
}
