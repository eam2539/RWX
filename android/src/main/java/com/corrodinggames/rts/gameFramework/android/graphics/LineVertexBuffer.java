package com.corrodinggames.rts.gameFramework.android.graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.as */
/* JADX INFO: loaded from: classes.dex */
public final class LineVertexBuffer {

    /* JADX INFO: renamed from: a */
    public final FloatBuffer f564a;
    public final ShortBuffer b;
    public int c;
    public int d;
    int[] e;
    int f;
    final /* synthetic */ LineBatchRenderer g;

    public LineVertexBuffer(LineBatchRenderer lineBatchRenderer, int i) {
        this.g = lineBatchRenderer;
        ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect(i * 32);
        byteBufferAllocateDirect.order(ByteOrder.nativeOrder());
        this.f564a = byteBufferAllocateDirect.asFloatBuffer();
        this.b = null;
        this.c = 0;
    }

    public final void a(float[] fArr, int i) {
        this.f564a.clear();
        this.f564a.put(fArr, 0, i);
        this.f564a.flip();
        this.d = i;
    }

    public final void a() {
        OpenGLRenderer.j();
        if (this.e == null) {
            this.e = new int[1];
            GLES20.glGenBuffers(1, this.e, 0);
            OpenGLRenderer.k();
        }
        this.f++;
        if (this.f > 0) {
            this.f = 0;
        }
        GLES20.glBindBuffer(34962, this.e[this.f]);
        GLES20.glBufferData(34962, this.d * 4, this.f564a, 35040);
        GLES20.glVertexAttribPointer(this.g.k.f563a.f574a, 2, 5126, false, 32, 0);
        OpenGLRenderer.j();
        OpenGLRenderer.j();
        GLES20.glVertexAttribPointer(this.g.k.b.f574a, 4, 5126, false, 32, 16);
        OpenGLRenderer.j();
    }

    public final void a(int i, int i2) {
        if (this.b != null) {
            this.b.position(0);
            GLES20.glDrawElements(i, i2, 5123, this.b);
        } else {
            GLES20.glDrawArrays(i, 0, i2);
        }
    }

    public final void b() {
        GLES20.glDisableVertexAttribArray(this.g.k.b.f574a);
    }
}
