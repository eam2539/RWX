package com.corrodinggames.rts.gameFramework.graphics;

import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.Log;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.opengl.DrawBatch;
import com.corrodinggames.rts.gameFramework.graphics.opengl.GLMeshBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/a.class */
public class AudioRenderer implements GLSurfaceView.Renderer {
    private static BitmapFactory.Options q = new BitmapFactory.Options();
    public boolean a;
    GL10 b;
    public float c;
    float d;
    int e;
    int f;
    DrawBatch[] g;
    public GLMeshBuffer h;
    public int i;
    public int j;
    public int k;
    int l;
    int m;
    int n;
    String o;
    long p;

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 gl10) {
        if (this.e == -1) {
            Log.d("RustedWarfare", "---- render: no buffer is ready!");
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
            Log.d("RustedWarfare", "render:" + this.o + ", this renders has " + this.g[this.e].b + " draws");
        }
        this.f = this.e;
        gl10.glClear(16640);
        gl10.glMatrixMode(5888);
        GLMeshBuffer.a(gl10, true, false);
        DrawBatch drawBatch = this.g[this.f];
        this.i = -1;
        this.j = -1;
        this.k = -1;
        for (int i2 = 0; i2 < drawBatch.b; i2++) {
            drawBatch.a[i2].a(gl10);
        }
        GLMeshBuffer.a(gl10);
        this.f = -1;
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        Log.d("RustedWarfare", "2d gl onSurfaceChanged:" + i + "," + i2);
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
    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        Log.d("RustedWarfare", "2d gl onSurfaceCreated");
        this.b = gl10;
        gl10.glHint(3152, 4353);
        gl10.glClearColor(0.3f, 0.3f, 0.5f, 1.0f);
        gl10.glShadeModel(7424);
        gl10.glDisable(2929);
        gl10.glEnable(3553);
        gl10.glDisable(3024);
        gl10.glDisable(2896);
        gl10.glClear(16640);
        if (GameEngine.getInstance() != null) {
        }
        this.a = true;
    }
}
