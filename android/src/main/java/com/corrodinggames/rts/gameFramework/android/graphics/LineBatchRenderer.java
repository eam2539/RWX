package com.corrodinggames.rts.gameFramework.android.graphics;

import android.opengl.GLES20;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.aq */
/* JADX INFO: loaded from: classes.dex */
public final class LineBatchRenderer {
    OpenGLRenderer g;
    int h;
    ImageBase l;
    C0009fo m;
    int o;
    public final boolean f = false;
    GraphicsOption1 i = new GraphicsOption1();
    final LineShaderAttributes j = new LineShaderAttributes();
    public LineShaderAttributes k = this.j;
    boolean n = true;
    int p = -1;
    float q = 1.0f;
    float r = 1.0f;
    float s = 1.0f;
    float t = 1.0f;
    float u = 0.0f;
    float v = 0.0f;
    float w = 1.0f;
    int e = 1280;
    float[] b = new float[this.e + 20];

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    LineVertexBuffer f562a = new LineVertexBuffer(this, this.e + 20);
    int c = 0;
    int d = 0;

    public LineBatchRenderer(OpenGLRenderer openGLRenderer) {
        this.g = openGLRenderer;
        this.h = openGLRenderer.a(this.k.e, "uniform mat4 uProjection;\nattribute vec2 aPosition;\nattribute vec4 aColor;\nvarying vec4 v_color;\nvoid main() {\n  vec4 pos = vec4(aPosition, 0.0, 1.0);\n  gl_Position = uProjection * pos;\n  v_color = aColor;\n}\n", "precision mediump float;\nvarying vec4 v_color;\nuniform sampler2D u_texture;\nvoid main() {\n  gl_FragColor = v_color ;\n}\n");
        OpenGLRenderer.k();
    }

    public final void a() {
        if (this.c > 0) {
            OpenGLRenderer.j();
            this.f562a.a(this.b, this.c);
            this.f562a.a();
            OpenGLRenderer.j();
            this.f562a.a(this.o, this.c);
            GLES20.glBindBuffer(34962, 0);
            OpenGLRenderer.j();
            this.c = 0;
            this.d = 0;
        }
    }

    public final void b() {
        a();
        this.l = null;
        this.f562a.b();
        OpenGLRenderer.j();
    }

    public final void a(int i) {
        if (this.p != i) {
            this.p = i;
            float f = ((i >>> 24) & 255) * 0.003921569f * 1.0f;
            this.q = ((i >>> 16) & 255) * 0.003921569f * f;
            this.r = ((i >>> 8) & 255) * 0.003921569f * f;
            this.s = (i & 255) * 0.003921569f * f;
            this.t = f;
        }
    }

    public final void a(float f) {
        if (this.w != f) {
            a();
            GLES20.glLineWidth(f);
            this.w = f;
        }
    }

    private void a(float f, float f2) {
        if (this.c + 8 + 24 >= this.e && this.o == 1 && this.d % 2 == 0) {
            a();
        }
        if (this.c + 8 >= this.e) {
            a();
        }
        OpenGLRenderer.j();
        if (this.n && this.m != null) {
            this.g.c(this.m);
            this.n = false;
        }
        OpenGLRenderer.j();
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
        fArr3[i3] = this.u;
        float[] fArr4 = this.b;
        int i4 = this.c;
        this.c = i4 + 1;
        fArr4[i4] = this.v;
        float[] fArr5 = this.b;
        int i5 = this.c;
        this.c = i5 + 1;
        fArr5[i5] = this.q;
        float[] fArr6 = this.b;
        int i6 = this.c;
        this.c = i6 + 1;
        fArr6[i6] = this.r;
        float[] fArr7 = this.b;
        int i7 = this.c;
        this.c = i7 + 1;
        fArr7[i7] = this.s;
        float[] fArr8 = this.b;
        int i8 = this.c;
        this.c = i8 + 1;
        fArr8[i8] = this.t;
        this.d++;
        OpenGLRenderer.j();
    }

    public final void a(float f, float f2, float[] fArr) {
        float f3 = fArr[0];
        float f4 = fArr[4];
        float f5 = fArr[1];
        float f6 = fArr[5];
        a((f3 * f) + (f4 * f2) + fArr[12], (f * f5) + (f2 * f6) + fArr[13]);
    }
}
