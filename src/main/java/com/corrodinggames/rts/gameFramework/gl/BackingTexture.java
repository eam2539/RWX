package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ad */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/ad.class */
public class BackingTexture extends Texture {
    int l = 9729;

    public BackingTexture(IGraphicsEngine iGraphicsEngine, int i, int i2) {
        a(i, i2);
        this.a = iGraphicsEngine.a().a();
        iGraphicsEngine.d(this);
        iGraphicsEngine.a(this, 6408, 5121, 6408);
    }

    public void a(IGraphicsEngine iGraphicsEngine, Bitmap bitmap, int i, int i2) {
        iGraphicsEngine.f();
        int iG = g();
        iGraphicsEngine.b(this);
        OpenGLRenderer.q();
        GLUtils.texSubImage2D(iG, 0, i, i2, bitmap, 6408, 5121);
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    protected boolean c(IGraphicsEngine iGraphicsEngine) {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public void b(IGraphicsEngine iGraphicsEngine) {
        GameEngine.isInSpace("BackingTexture prepare TODO");
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    protected int g() {
        return 3553;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public void b(int i) {
        if (this.l != i) {
            int iG = g();
            GLES20.glTexParameterf(iG, 10241, i);
            GLES20.glTexParameterf(iG, 10240, i);
            this.l = i;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public int h() {
        return this.l;
    }
}
