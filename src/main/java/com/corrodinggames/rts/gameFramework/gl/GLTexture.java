package com.corrodinggames.rts.gameFramework.gl;

import android.util.Log;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.x */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/x.class */
public class GLTexture extends Texture {
    private final boolean m;
    private int n;
    protected boolean l;

    public GLTexture(int i, int i2, boolean z) {
        this(i, i2, z, 3553);
    }

    public GLTexture(int i, int i2, boolean z, int i3) {
        this.n = 3553;
        this.m = z;
        a(i, i2);
        this.n = i3;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public void b(IGraphicsEngine iGraphicsEngine) {
        this.a = iGraphicsEngine.a().a();
        if (this.n == 3553) {
            iGraphicsEngine.a(this, 6408, 5121, 6408);
        }
        iGraphicsEngine.d(this);
        this.b = 1;
        a(iGraphicsEngine);
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    protected boolean c(IGraphicsEngine iGraphicsEngine) {
        if (i()) {
            return true;
        }
        Log.c("RawTexture", "lost the content due to context change");
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public int g() {
        return this.n;
    }

    public boolean k() {
        return this.l;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public void b(int i) {
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public int h() {
        return 9729;
    }
}
