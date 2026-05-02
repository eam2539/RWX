package com.corrodinggames.rts.gameFramework.graphics.opengl;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.ac */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/ac.class */
public class GLMeshBuffer {
    private FloatBuffer b;
    private FloatBuffer c;
    private FloatBuffer d;
    private IntBuffer e;
    private IntBuffer f;
    private IntBuffer g;
    private CharBuffer h;
    private Buffer i;
    private Buffer j;
    private Buffer k;
    private int l;
    private int m;
    private int n;
    private int o;
    private boolean p;
    private int q;
    private int r;
    private int s;
    private int t;
    static final /* synthetic */ boolean a;

    static {
        a = !GLMeshBuffer.class.desiredAssertionStatus();
    }

    void a(int i, int i2, float f, float f2) {
        if (i < 0 || i >= this.m) {
            throw new IllegalArgumentException("i");
        }
        if (i2 < 0 || i2 >= this.n) {
            throw new IllegalArgumentException("j");
        }
        int i3 = ((this.m * i2) + i) * 2;
        if (this.l == 5126) {
            this.c.put(i3, f);
            this.c.put(i3 + 1, f2);
        } else {
            this.f.put(i3, (int) (f * 65536.0f));
            this.f.put(i3 + 1, (int) (f2 * 65536.0f));
        }
    }

    void a(int i, int i2, float f, float f2, float f3, float f4, float f5, float[] fArr) {
        if (i < 0 || i >= this.m) {
            throw new IllegalArgumentException("i");
        }
        if (i2 < 0 || i2 >= this.n) {
            throw new IllegalArgumentException("j");
        }
        int i3 = (this.m * i2) + i;
        int i4 = i3 * 3;
        int i5 = i3 * 2;
        int i6 = i3 * 4;
        if (this.l == 5126) {
            this.b.put(i4, f);
            this.b.put(i4 + 1, f2);
            this.b.put(i4 + 2, f3);
            this.c.put(i5, f4);
            this.c.put(i5 + 1, f5);
            if (fArr != null) {
                this.d.put(i6, fArr[0]);
                this.d.put(i6 + 1, fArr[1]);
                this.d.put(i6 + 2, fArr[2]);
                this.d.put(i6 + 3, fArr[3]);
                return;
            }
            return;
        }
        this.e.put(i4, (int) (f * 65536.0f));
        this.e.put(i4 + 1, (int) (f2 * 65536.0f));
        this.e.put(i4 + 2, (int) (f3 * 65536.0f));
        this.f.put(i5, (int) (f4 * 65536.0f));
        this.f.put(i5 + 1, (int) (f5 * 65536.0f));
        if (fArr != null) {
            this.g.put(i6, (int) (fArr[0] * 65536.0f));
            this.g.put(i6 + 1, (int) (fArr[1] * 65536.0f));
            this.g.put(i6 + 2, (int) (fArr[2] * 65536.0f));
            this.g.put(i6 + 3, (int) (fArr[3] * 65536.0f));
        }
    }

    public static void a(GL10 gl10, boolean z, boolean z2) {
        gl10.glEnableClientState(32884);
        if (z) {
            gl10.glEnableClientState(32888);
            gl10.glEnable(3553);
        } else {
            gl10.glDisableClientState(32888);
            gl10.glDisable(3553);
        }
        if (z2) {
            gl10.glEnableClientState(32886);
        } else {
            gl10.glDisableClientState(32886);
        }
    }

    public void b(GL10 gl10, boolean z, boolean z2) {
        if (!this.p) {
            gl10.glVertexPointer(3, this.l, 0, this.i);
            if (z) {
                gl10.glTexCoordPointer(2, this.l, 0, this.j);
            }
            if (z2) {
                gl10.glColorPointer(4, this.l, 0, this.k);
            }
            gl10.glDrawElements(4, this.o, 5123, this.h);
            return;
        }
        GL11 gl11 = (GL11) gl10;
        gl11.glBindBuffer(34962, this.q);
        gl11.glVertexPointer(3, this.l, 0, 0);
        if (z) {
            gl11.glBindBuffer(34962, this.s);
            gl11.glTexCoordPointer(2, this.l, 0, 0);
        }
        if (z2) {
            gl11.glBindBuffer(34962, this.t);
            gl11.glColorPointer(4, this.l, 0, 0);
        }
        gl11.glBindBuffer(34963, this.r);
        gl11.glDrawElements(4, this.o, 5123, 0);
        gl11.glBindBuffer(34962, 0);
        gl11.glBindBuffer(34963, 0);
    }

    public static void a(GL10 gl10) {
        gl10.glDisableClientState(32884);
    }
}
