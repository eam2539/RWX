package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.Paint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.v */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/v.class */
public class PaintStyle {
    private float a;
    private int b;
    private Paint.Style c;

    public void a(int i) {
        this.b = i;
    }

    public int a() {
        return this.b;
    }

    public void a(float f) {
        this.a = f;
    }

    public float b() {
        return this.a;
    }

    public void a(Paint.Style style) {
        this.c = style;
    }

    public Paint.Style c() {
        return this.c;
    }
}
