package com.corrodinggames.rts.gameFramework.gl.font;

import android.opengl.GLES20;

import java.nio.*;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.g */
/* JADX INFO: loaded from: classes.dex */
public final class TextMesh {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f553a = 2;
    public final int b = this.f553a + 2;
    public final int c = this.b * 4;
    public final IntBuffer d;
    public final ShortBuffer e;
    public int f;
    public int g;
    final int[] h;
    int i;
    private int j;

    public TextMesh() {
        ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect(this.c * 96);
        byteBufferAllocateDirect.order(ByteOrder.nativeOrder());
        this.d = byteBufferAllocateDirect.asIntBuffer();
        ByteBuffer byteBufferAllocateDirect2 = ByteBuffer.allocateDirect(288);
        byteBufferAllocateDirect2.order(ByteOrder.nativeOrder());
        this.e = byteBufferAllocateDirect2.asShortBuffer();
        this.f = 0;
        this.g = 0;
        this.h = new int[(this.c * 96) / 4];
        this.i = ShaderAttributeType.A_TexCoordinate.c;
        this.j = ShaderAttributeType.A_Position.c;
    }

    public final void a(float[] fArr, int i) {
        this.d.clear();
        int i2 = i + 0;
        int i3 = 0;
        int i4 = 0;
        while (i4 < i2) {
            this.h[i3] = Float.floatToRawIntBits(fArr[i4]);
            i4++;
            i3++;
        }
        this.d.put(this.h, 0, i);
        this.d.flip();
        this.f = i / this.b;
    }

    public final void a() {
        this.d.position(0);
        GLES20.glVertexAttribPointer(this.j, this.f553a, 5126, false, this.c, (Buffer) this.d);
        GLES20.glEnableVertexAttribArray(this.j);
        this.d.position(this.f553a);
        GLES20.glVertexAttribPointer(this.i, 2, 5126, false, this.c, (Buffer) this.d);
        GLES20.glEnableVertexAttribArray(this.i);
    }

    public final void a(int i) {
        if (this.e != null) {
            this.e.position(0);
            GLES20.glDrawElements(4, i, 5123, this.e);
        } else {
            GLES20.glDrawArrays(4, 0, i);
        }
    }
}
