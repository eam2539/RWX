package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.RectF;
import android.opengl.GLES20;
import com.corrodinggames.rts.gameFramework.graphics.ShaderProgram;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.y */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/y.class */
public class TextureLayer {
    VertexBuffer a;
    float[] b;
    int c;
    int d;
    int e;
    OpenGLRenderer f;
    int g;
    TextureShader h;
     ShaderLocations i;
    public ShaderLocations j;
    Texture k;
    ShaderProgram l;
    boolean m;
    int n;
    float o;
    float p;
    float q;
    float r;

    public void a(ShaderProgram shaderProgram) {
        this.e = 0;
        this.c = 0;
        this.k = null;
        b(shaderProgram);
        a();
        OpenGLRenderer.q();
        this.a.d();
    }

    public void a() {
        this.j.d.a(this.f.d);
    }

    public void b() {
        this.m = true;
    }

    void b(ShaderProgram shaderProgram) {
        if (shaderProgram != null) {
            this.f.a(shaderProgram.n);
            this.f.c(shaderProgram);
            this.m = false;
        } else {
            this.f.a(this.g);
        }
        this.l = shaderProgram;
    }

    void a(Texture texture) {
        int iA;
        if (this.k == texture) {
            return;
        }
        if (this.k != null && (iA = this.k.a()) == texture.a() && iA != -1) {
            return;
        }
        c();
        GLES20.glActiveTexture(33984);
        OpenGLRenderer.q();
        texture.c(this.f);
        GLES20.glBindTexture(texture.g(), texture.a());
        OpenGLRenderer.q();
        GLES20.glUniform1i(this.j.e.a, 0);
        OpenGLRenderer.q();
        this.k = texture;
    }

    public void c() {
        if (this.e > 0) {
            OpenGLRenderer.q();
            this.a.a(this.b, 0, this.c);
            this.a.b();
            OpenGLRenderer.q();
            this.a.a(4, 0, this.e * 6);
            this.a.c();
            OpenGLRenderer.q();
            this.e = 0;
            this.c = 0;
        }
    }

    public void d() {
        c();
        this.k = null;
        this.a.e();
    }

    public void a(int i) {
        if (this.n == i) {
            return;
        }
        this.n = i;
        float f = ((i >>> 24) & 255) * 0.003921569f * 1.0f;
        this.o = ((i >>> 16) & 255) * 0.003921569f * f;
        this.p = ((i >>> 8) & 255) * 0.003921569f * f;
        this.q = (i & 255) * 0.003921569f * f;
        this.r = f;
    }

    public void a(Texture texture, RectF rectF, RectF rectF2, float[] fArr) {
        if (this.e == this.d) {
            c();
        }
        a(texture);
        if (this.m && this.l != null) {
            this.f.c(this.l);
            this.m = false;
        }
        float f = rectF2.a;
        float f2 = rectF2.d;
        float f3 = rectF2.c;
        float f4 = rectF2.b;
        float f5 = rectF.a;
        float f6 = rectF.b;
        float f7 = rectF.c;
        float f8 = rectF.d;
        float f9 = this.o;
        float f10 = this.p;
        float f11 = this.q;
        float f12 = this.r;
        float f13 = fArr[0];
        float f14 = fArr[4];
        float f15 = fArr[1];
        float f16 = fArr[5];
        float f17 = fArr[12];
        float f18 = fArr[13];
        float f19 = f * f13;
        float f20 = f3 * f13;
        float f21 = f * f15;
        float f22 = f3 * f15;
        float f23 = f2 * f14;
        float f24 = f2 * f16;
        float f25 = f4 * f14;
        float f26 = f4 * f16;
        int i = this.c;
        int i2 = i + 1;
        this.b[i] = f19 + f23 + f17;
        int i3 = i2 + 1;
        this.b[i2] = f21 + f24 + f18;
        int i4 = i3 + 1;
        this.b[i3] = f5;
        int i5 = i4 + 1;
        this.b[i4] = f8;
        int i6 = i5 + 1;
        this.b[i5] = f9;
        int i7 = i6 + 1;
        this.b[i6] = f10;
        int i8 = i7 + 1;
        this.b[i7] = f11;
        int i9 = i8 + 1;
        this.b[i8] = f12;
        int i10 = i9 + 1;
        this.b[i9] = f20 + f23 + f17;
        int i11 = i10 + 1;
        this.b[i10] = f22 + f24 + f18;
        int i12 = i11 + 1;
        this.b[i11] = f7;
        int i13 = i12 + 1;
        this.b[i12] = f8;
        int i14 = i13 + 1;
        this.b[i13] = f9;
        int i15 = i14 + 1;
        this.b[i14] = f10;
        int i16 = i15 + 1;
        this.b[i15] = f11;
        int i17 = i16 + 1;
        this.b[i16] = f12;
        int i18 = i17 + 1;
        this.b[i17] = f20 + f25 + f17;
        int i19 = i18 + 1;
        this.b[i18] = f22 + f26 + f18;
        int i20 = i19 + 1;
        this.b[i19] = f7;
        int i21 = i20 + 1;
        this.b[i20] = f6;
        int i22 = i21 + 1;
        this.b[i21] = f9;
        int i23 = i22 + 1;
        this.b[i22] = f10;
        int i24 = i23 + 1;
        this.b[i23] = f11;
        int i25 = i24 + 1;
        this.b[i24] = f12;
        int i26 = i25 + 1;
        this.b[i25] = f19 + f25 + f17;
        int i27 = i26 + 1;
        this.b[i26] = f21 + f26 + f18;
        int i28 = i27 + 1;
        this.b[i27] = f5;
        int i29 = i28 + 1;
        this.b[i28] = f6;
        int i30 = i29 + 1;
        this.b[i29] = f9;
        int i31 = i30 + 1;
        this.b[i30] = f10;
        int i32 = i31 + 1;
        this.b[i31] = f11;
        this.b[i32] = f12;
        this.c = i32 + 1;
        this.e++;
    }
}
