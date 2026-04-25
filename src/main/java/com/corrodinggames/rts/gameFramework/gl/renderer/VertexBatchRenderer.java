package com.corrodinggames.rts.gameFramework.gl.renderer;

import android.opengl.GLES20;
import com.corrodinggames.rts.gameFramework.gl.OpenGLRenderer;
import com.corrodinggames.rts.gameFramework.gl.Texture;
import com.corrodinggames.rts.gameFramework.graphics.ShaderProgram;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.aj */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/aj.class */
public class VertexBatchRenderer {
    VertexBuffer a;
    float[] b;
    int c;
    int d;
    int e;
    OpenGLRenderer f;
    int g;
    public ShaderBinding h;
    Texture i;
    ShaderProgram j;
    boolean k;
    int l;
    int m;
    float n;
    float o;
    float p;
    float q;
    float r;
    float s;
    float t;

    public void a(ShaderProgram shaderProgram) {
        this.c = 0;
        this.d = 0;
        this.i = null;
        b(shaderProgram);
        a();
        OpenGLRenderer.q();
        this.a.d();
        OpenGLRenderer.q();
    }

    public void a() {
        this.h.c.a(this.f.d);
    }

    void b(ShaderProgram shaderProgram) {
        if (shaderProgram != null) {
            this.f.a(shaderProgram.n);
            this.f.c(shaderProgram);
            this.k = false;
        } else {
            this.f.a(this.g);
        }
        this.j = shaderProgram;
    }

    public void b() {
        if (this.c > 0) {
            OpenGLRenderer.q();
            this.a.a(this.b, 0, this.c);
            this.a.b();
            OpenGLRenderer.q();
            this.a.a(this.l, 0, this.c);
            this.a.c();
            OpenGLRenderer.q();
            this.c = 0;
            this.d = 0;
        }
    }

    public void c() {
        b();
        this.i = null;
        this.a.e();
        OpenGLRenderer.q();
    }

    public void a(int i) {
        if (this.m == i) {
            return;
        }
        this.m = i;
        float f = ((i >>> 24) & 255) * 0.003921569f * 1.0f;
        this.n = ((i >>> 16) & 255) * 0.003921569f * f;
        this.o = ((i >>> 8) & 255) * 0.003921569f * f;
        this.p = (i & 255) * 0.003921569f * f;
        this.q = f;
    }

    public void b(int i) {
        if (this.l != i) {
            b();
            this.l = i;
        }
    }

    public void a(float f) {
        if (this.t != f) {
            b();
            GLES20.glLineWidth(f);
            this.t = f;
        }
    }

    public void a(float f, float f2) {
        if (this.c + 8 + 24 >= this.e && this.l == 1 && this.d % 2 == 0) {
            b();
        }
        if (this.c + 8 >= this.e) {
            b();
        }
        OpenGLRenderer.q();
        if (this.k && this.j != null) {
            this.f.c(this.j);
            this.k = false;
        }
        OpenGLRenderer.q();
        float[] fArr = this.b;
        int i = this.c;
        this.c = i + 1;
        fArr[i] = f;
        float[] fArr2 = this.b;
        int i2 = this.c;
        this.c = i2 + 1;
        fArr2[i2] = f2;
        float[] fArr3 = this.b;
        int i3 = this.c;
        this.c = i3 + 1;
        fArr3[i3] = this.r;
        float[] fArr4 = this.b;
        int i4 = this.c;
        this.c = i4 + 1;
        fArr4[i4] = this.s;
        float[] fArr5 = this.b;
        int i5 = this.c;
        this.c = i5 + 1;
        fArr5[i5] = this.n;
        float[] fArr6 = this.b;
        int i6 = this.c;
        this.c = i6 + 1;
        fArr6[i6] = this.o;
        float[] fArr7 = this.b;
        int i7 = this.c;
        this.c = i7 + 1;
        fArr7[i7] = this.p;
        float[] fArr8 = this.b;
        int i8 = this.c;
        this.c = i8 + 1;
        fArr8[i8] = this.q;
        this.d++;
        OpenGLRenderer.q();
    }

    public void a(float f, float f2, float[] fArr) {
        float f3 = fArr[0];
        float f4 = fArr[4];
        float f5 = fArr[1];
        float f6 = fArr[5];
        a((f * f3) + (f2 * f4) + fArr[12], (f * f5) + (f2 * f6) + fArr[13]);
    }
}
