package com.corrodinggames.rts.gameFramework.gl.font;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import com.corrodinggames.rts.gameFramework.gl.font.shaders.FontShader;
import com.corrodinggames.rts.gameFramework.gl.font.shaders.ShaderProgramBase;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.b */
/* JADX INFO: loaded from: classes.dex */
public final class FontRenderer {
    public static boolean z = true;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    AssetManager f549a;
    public TextRenderer b;
    int c;
    int d;
    float e;
    float f;
    float g;
    float h;
    float i;
    int j;
    int k;
    int l;
    int m;
    public float n;
    public float o;
    public float p;
    boolean q;
    public ShaderProgramBase r;
    public int s;
    public int t;
    public Paint u;
    public ArrayList v;
    GlyphInfo[][] w;
    public boolean x;
    public int y;

    public final GlyphInfo a(char c) {
        GlyphInfo[] glyphInfoArr;
        if (c <= 65535 && (glyphInfoArr = this.w[c / 256]) != null) {
            return glyphInfoArr[c & 255];
        }
        return null;
    }

    private void b(char c) {
        if (c <= 65535) {
            if (this.v.size() == 0) {
                c();
            }
            FontPage fontPage = (FontPage) this.v.get(this.v.size() - 1);
            if (!(fontPage.g < fontPage.f * fontPage.e)) {
                if (this.v.size() < this.y) {
                    c();
                } else {
                    return;
                }
            }
            FontPage fontPage2 = (FontPage) this.v.get(this.v.size() - 1);
            Paint paint = this.u;
            if (fontPage2.c == null) {
                fontPage2.d = Bitmap.createBitmap(fontPage2.b, fontPage2.b, Bitmap.Config.ALPHA_8);
                fontPage2.c = new Canvas();
                fontPage2.c.setBitmap(fontPage2.d);
                fontPage2.d.eraseColor(0);
            }
            int i = fontPage2.g / fontPage2.e;
            int i2 = (fontPage2.g % fontPage2.e) * fontPage2.j;
            int i3 = i * fontPage2.k;
            float fCeil = (float) Math.ceil(Math.abs(paint.getFontMetrics().descent));
            float[] fArr = new float[2];
            char[] cArr = {c, 0};
            paint.getTextWidths(cArr, 0, 1, fArr);
            float f = (int) fArr[0];
            if (f > fontPage2.j) {
                a("Warning chr is larger then cellWidth: ".concat(String.valueOf(c)));
            }
            fontPage2.c.drawText(cArr, 0, 1, fontPage2.m + i2, (int) (((fontPage2.k + i3) - fCeil) - fontPage2.n), paint);
            GlyphInfo glyphInfo = new GlyphInfo();
            glyphInfo.f550a = c;
            glyphInfo.b = fontPage2.l;
            float f2 = fontPage2.j;
            float f3 = fontPage2.k;
            glyphInfo.d = ((float) i2) / fontPage2.b;
            glyphInfo.e = ((float) i3) / fontPage2.b;
            glyphInfo.f = glyphInfo.d + (f2 / fontPage2.b);
            glyphInfo.g = glyphInfo.e + (f3 / fontPage2.b);
            glyphInfo.c = f;
            fontPage2.g++;
            GlyphInfo[] glyphInfoArr = this.w[c / 256];
            if (glyphInfoArr == null) {
                glyphInfoArr = new GlyphInfo[SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_MEDIA_CONTEXT_MENU];
                this.w[c / 256] = glyphInfoArr;
            }
            glyphInfoArr[c & 255] = glyphInfo;
        }
    }

    private void b() {
        if (this.v.size() > 0) {
            FontPage fontPage = (FontPage) this.v.get(this.v.size() - 1);
            if (fontPage.d != null) {
                if (fontPage.f552a == 0) {
                    int[] iArr = new int[1];
                    GLES20.glGenTextures(1, iArr, 0);
                    fontPage.f552a = iArr[0];
                    if (fontPage.f552a == 0) {
                        a("Failed to gen texture page");
                        return;
                    }
                }
                GLES20.glBindTexture(3553, fontPage.f552a);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameterf(3553, 10242, 33071.0f);
                GLES20.glTexParameterf(3553, 10243, 33071.0f);
                GLUtils.texImage2D(3553, 0, fontPage.d, 0);
            }
        }
    }

    private void c() {
        b();
        this.v.add(new FontPage(this.v.size(), this.j, this.k, this.c, this.d));
    }

    private FontRenderer() {
        this.q = true;
        this.v = new ArrayList();
        this.w = new GlyphInfo[SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_CONTENTS_MENU][];
        this.y = Integer.MAX_VALUE;
        FontShader fontShader = new FontShader();
        fontShader.a();
        this.f549a = null;
        this.b = new TextRenderer(fontShader, this);
        this.c = 0;
        this.d = 0;
        this.e = 0.0f;
        this.f = 0.0f;
        this.g = 0.0f;
        this.h = 0.0f;
        this.i = 0.0f;
        this.j = 0;
        this.k = 0;
        this.l = 0;
        this.m = 0;
        this.n = 1.0f;
        this.o = 1.0f;
        this.p = 0.0f;
        this.r = fontShader;
        this.s = GLES20.glGetUniformLocation(this.r.f548a, "u_Color");
        this.t = GLES20.glGetUniformLocation(this.r.f548a, "u_Texture");
    }

    public FontRenderer(byte b) {
        this();
    }

    public final boolean a(Paint paint, int i) {
        if (this.u != null) {
            throw new RuntimeException("Already loaded");
        }
        this.c = 3;
        this.d = 2;
        this.u = paint;
        this.u.setAntiAlias(true);
        this.u.setTextSize(i);
        this.u.setColor(-1);
        Paint.FontMetrics fontMetrics = this.u.getFontMetrics();
        this.e = (float) Math.ceil(Math.abs(fontMetrics.bottom) + Math.abs(fontMetrics.top));
        this.f = (float) Math.ceil(Math.abs(fontMetrics.ascent));
        this.g = (float) Math.ceil(Math.abs(fontMetrics.descent));
        char[] cArr = new char[2];
        this.i = 0.0f;
        this.h = 0.0f;
        float[] fArr = new float[2];
        for (char c = ' '; c <= '~'; c = (char) (c + 1)) {
            cArr[0] = c;
            paint.getTextWidths(cArr, 0, 1, fArr);
            float f = fArr[0];
            if (f > this.h) {
                this.h = f;
            }
        }
        this.i = this.e;
        this.j = ((int) this.h) + (this.c * 2);
        this.k = ((int) this.i) + (this.d * 2);
        for (char c2 = ' '; c2 <= '~'; c2 = (char) (c2 + 1)) {
            b(c2);
        }
        b();
        return true;
    }

    public static void a() {
        int iGlGetError;
        if (z && (iGlGetError = GLES20.glGetError()) != 0) {
            Log.e("GLTEXT", "GL error: ".concat(String.valueOf(iGlGetError)), new Throwable());
        }
    }

    public final void a(String str, float f, float f2) {
        float f3;
        float f4 = this.k * this.o;
        float f5 = this.j * this.n;
        int length = str.length();
        float f6 = (f5 / 2.0f) - (this.c * this.n);
        float f7 = ((f4 / 2.0f) - (this.d * this.o)) - (this.g * this.o);
        if (this.q) {
            f6 = (int) f6;
            f7 = (int) f7;
        }
        float f8 = f + f6;
        float f9 = f2 + f7;
        float f10 = 0.0f;
        int i = 0;
        while (i < length) {
            char cCharAt = str.charAt(i);
            float f11 = f10 + f8;
            float f12 = 0.0f + f9;
            GlyphInfo glyphInfoA = a(cCharAt);
            if (glyphInfoA == null && this.x) {
                a("Loading glyph:".concat(String.valueOf(cCharAt)));
                b(cCharAt);
                b();
            }
            if (glyphInfoA != null) {
                TextRenderer textRenderer = this.b;
                if (textRenderer.f == textRenderer.e) {
                    textRenderer.a();
                }
                int i2 = glyphInfoA.b;
                if (textRenderer.h != i2) {
                    textRenderer.a();
                    GLES20.glBindTexture(3553, ((FontPage) textRenderer.f551a.v.get(i2)).f552a);
                    textRenderer.h = i2;
                }
                float f13 = f5 / 2.0f;
                float f14 = f4 / 2.0f;
                float f15 = f11 - f13;
                float f16 = f12 - f14;
                float f17 = f13 + f11;
                float f18 = f12 + f14;
                float[] fArr = textRenderer.c;
                int i3 = textRenderer.d;
                textRenderer.d = i3 + 1;
                fArr[i3] = f15;
                float[] fArr2 = textRenderer.c;
                int i4 = textRenderer.d;
                textRenderer.d = i4 + 1;
                fArr2[i4] = f16;
                float[] fArr3 = textRenderer.c;
                int i5 = textRenderer.d;
                textRenderer.d = i5 + 1;
                fArr3[i5] = glyphInfoA.d;
                float[] fArr4 = textRenderer.c;
                int i6 = textRenderer.d;
                textRenderer.d = i6 + 1;
                fArr4[i6] = glyphInfoA.g;
                float[] fArr5 = textRenderer.c;
                int i7 = textRenderer.d;
                textRenderer.d = i7 + 1;
                fArr5[i7] = f17;
                float[] fArr6 = textRenderer.c;
                int i8 = textRenderer.d;
                textRenderer.d = i8 + 1;
                fArr6[i8] = f16;
                float[] fArr7 = textRenderer.c;
                int i9 = textRenderer.d;
                textRenderer.d = i9 + 1;
                fArr7[i9] = glyphInfoA.f;
                float[] fArr8 = textRenderer.c;
                int i10 = textRenderer.d;
                textRenderer.d = i10 + 1;
                fArr8[i10] = glyphInfoA.g;
                float[] fArr9 = textRenderer.c;
                int i11 = textRenderer.d;
                textRenderer.d = i11 + 1;
                fArr9[i11] = f17;
                float[] fArr10 = textRenderer.c;
                int i12 = textRenderer.d;
                textRenderer.d = i12 + 1;
                fArr10[i12] = f18;
                float[] fArr11 = textRenderer.c;
                int i13 = textRenderer.d;
                textRenderer.d = i13 + 1;
                fArr11[i13] = glyphInfoA.f;
                float[] fArr12 = textRenderer.c;
                int i14 = textRenderer.d;
                textRenderer.d = i14 + 1;
                fArr12[i14] = glyphInfoA.e;
                float[] fArr13 = textRenderer.c;
                int i15 = textRenderer.d;
                textRenderer.d = i15 + 1;
                fArr13[i15] = f15;
                float[] fArr14 = textRenderer.c;
                int i16 = textRenderer.d;
                textRenderer.d = i16 + 1;
                fArr14[i16] = f18;
                float[] fArr15 = textRenderer.c;
                int i17 = textRenderer.d;
                textRenderer.d = i17 + 1;
                fArr15[i17] = glyphInfoA.d;
                float[] fArr16 = textRenderer.c;
                int i18 = textRenderer.d;
                textRenderer.d = i18 + 1;
                fArr16[i18] = glyphInfoA.e;
                textRenderer.f++;
                float f19 = (glyphInfoA.c + this.p) * this.n;
                if (this.q) {
                    f19 = (int) (f19 + 0.95f);
                }
                f3 = f19 + f10;
            } else {
                f3 = f10;
            }
            i++;
            f10 = f3;
        }
    }

    private static void a(String str) {
        Log.d("GLTEXT", "debug:".concat(String.valueOf(str)));
    }
}
