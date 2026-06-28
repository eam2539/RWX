package com.corrodinggames.rts.gameFramework.android.graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ai */
/* JADX INFO: loaded from: classes.dex */
public final class VertexBuffer {

    /* JADX INFO: renamed from: a */
    public final FloatBuffer f559a;
    public final ShortBuffer b;
    public int c;
    public int d;
    public int e;
    int[] f;
    int g;
    final /* synthetic */ BatchRenderer h;

    public VertexBuffer(BatchRenderer batchRenderer, int i, int i2) {
        this.h = batchRenderer;
        ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect(i * 32);
        byteBufferAllocateDirect.order(ByteOrder.nativeOrder());
        this.f559a = byteBufferAllocateDirect.asFloatBuffer();
        if (i2 > 0) {
            ByteBuffer byteBufferAllocateDirect2 = ByteBuffer.allocateDirect(i2 * 2);
            byteBufferAllocateDirect2.order(ByteOrder.nativeOrder());
            this.b = byteBufferAllocateDirect2.asShortBuffer();
        } else {
            this.b = null;
        }
        this.d = 0;
    }

    public final void a(float[] fArr, int i) {
        this.f559a.clear();
        this.f559a.put(fArr, 0, i);
        this.f559a.flip();
        this.e = i;
    }

    private void d() {
        GLES20.glEnableVertexAttribArray(this.h.j.f558a.f574a);
        GLES20.glEnableVertexAttribArray(this.h.j.b.f574a);
        GLES20.glEnableVertexAttribArray(this.h.j.c.f574a);
    }

    public final void a() {
        OpenGLRenderer.j();
        if (this.f == null) {
            this.f = new int[1];
            GLES20.glGenBuffers(1, this.f, 0);
            OpenGLRenderer.k();
        }
        this.g++;
        if (this.g > 0) {
            this.g = 0;
        }
        GLES20.glBindBuffer(34962, this.f[this.g]);
        GLES20.glBufferData(34962, this.e * 4, this.f559a, 35040);
        GLES20.glVertexAttribPointer(this.h.j.f558a.f574a, 2, 5126, false, 32, 0);
        OpenGLRenderer.j();
        GLES20.glVertexAttribPointer(this.h.j.b.f574a, 2, 5126, false, 32, 8);
        OpenGLRenderer.j();
        GLES20.glVertexAttribPointer(this.h.j.c.f574a, 4, 5126, false, 32, 16);
        OpenGLRenderer.j();
    }

    public final void a(int i) {
        if (this.b != null) {
            GLES20.glDrawElements(4, i, 5123, 0);
        } else {
            GLES20.glDrawArrays(4, 0, i);
        }
    }

    public final void b() {
        GLES20.glBindBuffer(34963, this.c);
        d();
    }

    public final void c() {
        GLES20.glDisableVertexAttribArray(this.h.j.b.f574a);
        GLES20.glDisableVertexAttribArray(this.h.j.c.f574a);
        GLES20.glBindBuffer(34963, 0);
    }
}
