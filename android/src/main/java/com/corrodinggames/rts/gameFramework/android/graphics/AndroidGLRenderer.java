package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.Log;
import com.corrodinggames.rts.appFramework.android.AndroidSAF;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.android.graphics.opengl.DrawBatch;
import com.corrodinggames.rts.gameFramework.android.graphics.opengl.DrawCommand;
import com.corrodinggames.rts.gameFramework.android.graphics.opengl.ShapeType;
import com.corrodinggames.rts.gameFramework.m.GLMesh;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.a */
/* JADX INFO: loaded from: classes.dex */
public final class AndroidGLRenderer implements GLSurfaceView.Renderer {
    private static BitmapFactory.Options q = new BitmapFactory.Options();

    /* JADX INFO: renamed from: a */
    public boolean f749a;
    GL10 b;
    float c;
    float d;
    int e;
    int f;
    DrawBatch[] g;
    GLMesh h;
    int i;
    int j;
    int k;
    int l;
    int m;
    int n;
    String o;
    long p;

    @Override // android.opengl.GLSurfaceView.Renderer
    public final void onDrawFrame(GL10 gl10) {
        if (this.e == -1) {
            Log.e(AndroidSAF.TAG, "---- render: no buffer is ready!");
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        int i = (int) (jCurrentTimeMillis - this.p);
        this.p = jCurrentTimeMillis;
        this.l += i;
        this.m++;
        if (this.m == 10) {
            this.n = 10000 / this.l;
            this.l = 0;
            this.m = 0;
            this.o = this.n + "fps";
            Log.e(AndroidSAF.TAG, "render:" + this.o + ", this renders has " + this.g[this.e].b + " draws");
        }
        this.f = this.e;
        gl10.glClear(16640);
        gl10.glMatrixMode(5888);
        GLMesh.a(gl10);
        DrawBatch drawBatch = this.g[this.f];
        this.i = -1;
        this.j = -1;
        this.k = -1;
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 < drawBatch.b) {
                DrawCommand drawCommand = drawBatch.f751a[i3];
                if (drawCommand.g.i != drawCommand.b.h.intValue()) {
                    gl10.glBindTexture(3553, drawCommand.b.h.intValue());
                    drawCommand.g.i = drawCommand.b.h.intValue();
                }
                gl10.glPushMatrix();
                gl10.glLoadIdentity();
                if (drawCommand.f750a == ShapeType.b) {
                    gl10.glTranslatef(drawCommand.f.left, (drawCommand.g.c - drawCommand.f.top) - drawCommand.e.height(), 0.0f);
                    GLMesh gLMesh = drawCommand.g.h;
                    float fWidth = ((float) drawCommand.e.left) / drawCommand.b.width();
                    float fWidth2 = ((float) drawCommand.e.right) / drawCommand.b.width();
                    float fHeight = ((float) drawCommand.e.top) / drawCommand.b.height();
                    float fHeight2 = ((float) drawCommand.e.bottom) / drawCommand.b.height();
                    if (drawCommand.g.j == drawCommand.e.height() && drawCommand.g.k == drawCommand.e.width()) {
                        gLMesh.a(0, 0, fWidth, fHeight2);
                        gLMesh.a(1, 0, fWidth2, fHeight2);
                        gLMesh.a(0, 1, fWidth, fHeight);
                        gLMesh.a(1, 1, fWidth2, fHeight);
                    } else {
                        drawCommand.g.j = drawCommand.e.height();
                        drawCommand.g.k = drawCommand.e.width();
                        gLMesh.a(0, 0, 0.0f, 0.0f, fWidth, fHeight2);
                        gLMesh.a(1, 0, drawCommand.e.width(), 0.0f, fWidth2, fHeight2);
                        gLMesh.a(0, 1, 0.0f, drawCommand.e.height(), fWidth, fHeight);
                        gLMesh.a(1, 1, drawCommand.e.width(), drawCommand.e.height(), fWidth2, fHeight);
                    }
                    if (!gLMesh.f) {
                        gl10.glVertexPointer(3, gLMesh.d, 0, gLMesh.b);
                        gl10.glTexCoordPointer(2, gLMesh.d, 0, gLMesh.c);
                        gl10.glDrawElements(4, gLMesh.e, 5123, gLMesh.f766a);
                    } else {
                        GL11 gl11 = (GL11) gl10;
                        gl11.glBindBuffer(34962, gLMesh.g);
                        gl11.glVertexPointer(3, gLMesh.d, 0, 0);
                        gl11.glBindBuffer(34962, gLMesh.i);
                        gl11.glTexCoordPointer(2, gLMesh.d, 0, 0);
                        gl11.glBindBuffer(34963, gLMesh.h);
                        gl11.glDrawElements(4, gLMesh.e, 5123, 0);
                        gl11.glBindBuffer(34962, 0);
                        gl11.glBindBuffer(34963, 0);
                    }
                    gl10.glPopMatrix();
                    i2 = i3 + 1;
                } else {
                    gl10.glTranslatef(drawCommand.c, (drawCommand.g.c - drawCommand.d) - drawCommand.b.height(), 0.0f);
                    throw new RuntimeException("Not supported");
                }
            } else {
                GLMesh.b(gl10);
                this.f = -1;
                return;
            }
        }
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public final void onSurfaceChanged(GL10 gl10, int i, int i2) {
        Log.e(AndroidSAF.TAG, "2d gl onSurfaceChanged:" + i + "," + i2);
        this.b = gl10;
        this.c = i2;
        this.d = i;
        gl10.glViewport(0, 0, i, i2);
        gl10.glMatrixMode(5889);
        gl10.glLoadIdentity();
        gl10.glOrthof(0.0f, i, 0.0f, i2, 0.0f, 1.0f);
        gl10.glShadeModel(7424);
        gl10.glEnable(3042);
        gl10.glBlendFunc(770, 771);
        gl10.glColor4x(65536, 65536, 65536, 65536);
        gl10.glEnable(3553);
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public final void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        Log.e(AndroidSAF.TAG, "2d gl onSurfaceCreated");
        this.b = gl10;
        gl10.glHint(3152, 4353);
        gl10.glClearColor(0.3f, 0.3f, 0.5f, 1.0f);
        gl10.glShadeModel(7424);
        gl10.glDisable(2929);
        gl10.glEnable(3553);
        gl10.glDisable(3024);
        gl10.glDisable(2896);
        gl10.glClear(16640);
        GameEngine.getInstance();
        this.f749a = true;
    }
}
