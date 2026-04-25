package com.corrodinggames.rts.gameFramework.gl.font;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/a/e.class */
public class FontPage {
    int a;
    int b;
    Canvas c;
    public Bitmap d;
    int e;
    int f;
    int g = 0;
    int h = 0;
    int i = 0;
    int j;
    int k;
    int l;
    int m;
    int n;

    public boolean a() {
        return this.g < this.e * this.f;
    }

    public FontPage(int i, int i2, int i3, int i4, int i5, int i6) {
        this.l = i2;
        this.b = i;
        this.e = i / i3;
        this.f = i / i4;
        this.j = i3;
        this.k = i4;
        this.m = i5;
        this.n = i6;
    }

    public void b() {
        this.d = Bitmap.a(this.b, this.b, Bitmap.Config.ALPHA_8);
        this.c = new Canvas();
        this.c.a(this.d);
        this.d.a(0);
    }

    public void c() {
        if (this.d == null) {
            return;
        }
        if (this.a == 0) {
            int[] iArr = new int[1];
            GLES20.glGenTextures(1, iArr, 0);
            this.a = iArr[0];
            if (this.a == 0) {
                FontRenderer.b("Failed to gen texture page");
                return;
            }
        }
        GLES20.glBindTexture(3553, this.a);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameterf(3553, 10242, 33071.0f);
        GLES20.glTexParameterf(3553, 10243, 33071.0f);
        GLUtils.texImage2D(3553, 0, this.d, 0);
    }

    public GlyphInfo a(char c, Paint paint) {
        if (this.c == null) {
            b();
        }
        int i = this.g / this.e;
        int i2 = (this.g % this.e) * this.j;
        int i3 = i * this.k;
        float fCeil = (float) Math.ceil(Math.abs(paint.n().c));
        float[] fArr = new float[2];
        char[] cArr = {c, 0};
        paint.a(cArr, 0, 1, fArr);
        float f = (int) fArr[0];
        if (f > this.j) {
            FontRenderer.b("Warning chr is larger then cellWidth: " + c);
        }
        this.c.a(cArr, 0, 1, i2 + this.m, (int) (((i3 + this.k) - fCeil) - this.n), paint);
        GlyphInfo glyphInfo = new GlyphInfo();
        glyphInfo.a = c;
        glyphInfo.b = this.l;
        a(glyphInfo, i2, i3, this.j, this.k);
        glyphInfo.c = f;
        this.g++;
        return glyphInfo;
    }

    private void a(GlyphInfo glyphInfo, float f, float f2, float f3, float f4) {
        glyphInfo.d = f / this.b;
        glyphInfo.e = f2 / this.b;
        glyphInfo.f = glyphInfo.d + (f3 / this.b);
        glyphInfo.g = glyphInfo.e + (f4 / this.b);
    }
}
