package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.al */
/* JADX INFO: loaded from: classes.dex */
public final class BackingTexture extends ImageBase {
    int m;

    public BackingTexture(GraphicsRenderer graphicsRenderer) {
        super((byte) 0);
        this.m = 9729;
        a(1024, 1024);
        this.f565a = graphicsRenderer.a().a();
        graphicsRenderer.d(this);
        graphicsRenderer.a(this, 6408, 5121, 6408);
    }

    public final void a(GraphicsRenderer graphicsRenderer, Bitmap bitmap, int i, int i2) {
        graphicsRenderer.g();
        graphicsRenderer.b(this);
        OpenGLRenderer.j();
        GLUtils.texSubImage2D(3553, 0, i, i2, bitmap, 6408, 5121);
    }

    public final void d(GraphicsRenderer graphicsRenderer) {
        graphicsRenderer.g();
        GLES20.glBindTexture(3553, a());
        OpenGLRenderer.j();
        int width = TextureManager.c.getWidth();
        int height = TextureManager.c.getHeight();
        for (int i = 0; i < this.c; i += width) {
            for (int i2 = 0; i2 < this.d; i2 += height) {
                GLUtils.texSubImage2D(3553, 0, i, i2, TextureManager.c, 6408, 5121);
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    protected final boolean c(GraphicsRenderer graphicsRenderer) {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final void b(GraphicsRenderer graphicsRenderer) {
        GameEngine.log("BackingTexture prepare TODO");
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    protected final int f() {
        return 3553;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final void a(int i) {
        if (this.m != i) {
            GLES20.glTexParameterf(3553, 10241, i);
            GLES20.glTexParameterf(3553, 10240, i);
            this.m = i;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final int g() {
        return this.m;
    }
}
