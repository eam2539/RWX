package com.corrodinggames.rts.gameFramework.gl.font;

import android.opengl.GLES20;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/a/g.class */
public class TextMesh {
    public final int a = 2;
    public final int b = this.a + 2;
    public final int c = this.b * 4;
    public final IntBuffer d;
    public final ShortBuffer e;
    public int f;
    public int g;
    final int[] h;
    private int i;
    private int j;

    public TextMesh(int i, int i2) {
        ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect(i * this.c);
        byteBufferAllocateDirect.order(ByteOrder.nativeOrder());
        this.d = byteBufferAllocateDirect.asIntBuffer();
        if (i2 > 0) {
            ByteBuffer byteBufferAllocateDirect2 = ByteBuffer.allocateDirect(i2 * 2);
            byteBufferAllocateDirect2.order(ByteOrder.nativeOrder());
            this.e = byteBufferAllocateDirect2.asShortBuffer();
        } else {
            this.e = null;
        }
        this.f = 0;
        this.g = 0;
        this.h = new int[(i * this.c) / 4];
        this.i = ShaderAttributeType.A_TexCoordinate.a();
        this.j = ShaderAttributeType.A_Position.a();
    }

    public void a(float[] fArr, int i, int i2) {
        this.d.clear();
        int i3 = i + i2;
        int i4 = i;
        int i5 = 0;
        while (i4 < i3) {
            this.h[i5] = Float.floatToRawIntBits(fArr[i4]);
            i4++;
            i5++;
        }
        this.d.put(this.h, 0, i2);
        this.d.flip();
        this.f = i2 / this.b;
    }

    public void a(short[] sArr, int i, int i2) {
        this.e.clear();
        this.e.put(sArr, i, i2);
        this.e.flip();
        this.g = i2;
    }

    public void a() {
        this.d.position(0);
        GLES20.glVertexAttribPointer(this.j, this.a, 5126, false, this.c, (Buffer) this.d);
        GLES20.glEnableVertexAttribArray(this.j);
        this.d.position(this.a);
        GLES20.glVertexAttribPointer(this.i, 2, 5126, false, this.c, (Buffer) this.d);
        GLES20.glEnableVertexAttribArray(this.i);
    }

    public void a(int i, int i2, int i3) {
        if (this.e != null) {
            this.e.position(i2);
            GLES20.glDrawElements(i, i3, 5123, this.e);
        } else {
            GLES20.glDrawArrays(i, i2, i3);
        }
    }

    public void b() {
        GLES20.glDisableVertexAttribArray(this.i);
    }
}
