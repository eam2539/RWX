package com.corrodinggames.rts.gameFramework.gl.renderer;

import android.opengl.GLES20;
import com.corrodinggames.rts.gameFramework.gl.OpenGLRenderer;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.al */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/al.class */
public class VertexBuffer {
    public  FloatBuffer a;
    public  ShortBuffer b;
    public int c;
    int[] d;
    int e;
     /* synthetic */ VertexBatchRenderer f;

    public void a(float[] fArr, int i, int i2) {
        this.a.clear();
        int i3 = i + i2;
        this.a.put(fArr, 0, i2);
        this.a.flip();
        this.c = i2;
    }

    public void a() {
        GLES20.glEnableVertexAttribArray(this.f.h.a.a);
        GLES20.glEnableVertexAttribArray(this.f.h.b.a);
    }

    public void b() {
        OpenGLRenderer.q();
        if (this.d == null) {
            this.d = new int[1];
            GLES20.glGenBuffers(1, this.d, 0);
            OpenGLRenderer.r();
        }
        this.e++;
        if (this.e >= 1) {
            this.e = 0;
        }
        GLES20.glBindBuffer(34962, this.d[this.e]);
        GLES20.glBufferData(34962, this.c * 4, this.a, 35040);
        GLES20.glVertexAttribPointer(this.f.h.a.a, 2, 5126, false, 32, 0);
        OpenGLRenderer.q();
        OpenGLRenderer.q();
        GLES20.glVertexAttribPointer(this.f.h.b.a, 4, 5126, false, 32, 16);
        OpenGLRenderer.q();
    }

    public void a(int i, int i2, int i3) {
        if (this.b != null) {
            this.b.position(i2);
            GLES20.glDrawElements(i, i3, 5123, this.b);
        } else {
            GLES20.glDrawArrays(i, i2, i3);
        }
    }

    public void c() {
        GLES20.glBindBuffer(34962, 0);
    }

    public void d() {
        a();
    }

    public void e() {
        GLES20.glDisableVertexAttribArray(this.f.h.b.a);
    }
}
