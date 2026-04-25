package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.corrodinggames.rts.gameFramework.GameEngine;
import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ah */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/ah.class */
public abstract class DynamicTexture extends Texture {
    private static HashMap l = new HashMap();
    private static BitmapCacheKey o = new BitmapCacheKey();
    private boolean p;
    private boolean q;
    private boolean r;
    private boolean s;
    private boolean t;
    private static int u;
    protected Bitmap m;
    private int v;
    int n;

    protected abstract Bitmap k();

    protected abstract void a(Bitmap bitmap);

    protected DynamicTexture() {
        this(false);
    }

    protected DynamicTexture(boolean z) {
        super(null, 0, 0);
        this.p = true;
        this.q = false;
        this.r = false;
        this.s = false;
        this.t = false;
        this.n = 9729;
        if (z) {
            a(true);
            this.v = 1;
        }
    }

    private static Bitmap a(boolean z, Bitmap.Config config, int i) {
        Bitmap bitmapA;
        BitmapCacheKey bitmapCacheKey = o;
        bitmapCacheKey.a = z;
        bitmapCacheKey.b = config;
        bitmapCacheKey.c = i;
        Bitmap bitmap = (Bitmap) l.get(bitmapCacheKey);
        if (bitmap == null) {
            if (z) {
                bitmapA = Bitmap.a(1, i, config);
            } else {
                bitmapA = Bitmap.a(i, 1, config);
            }
            bitmap = bitmapA;
            l.put(bitmapCacheKey.clone(), bitmap);
        }
        return bitmap;
    }

    private Bitmap n() {
        if (this.m == null) {
            this.m = k();
            int iB = this.m.b() + (this.v * 2);
            int iC = this.m.c() + (this.v * 2);
            if (this.c == -1) {
                a(iB, iC);
            }
        }
        return this.m;
    }

    private void o() {
        a(this.m);
        this.m = null;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public int b() {
        if (this.c == -1) {
            n();
        }
        return this.c;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public int c() {
        if (this.c == -1) {
            n();
        }
        return this.d;
    }

    public void l() {
        if (this.m != null) {
            o();
        }
        this.p = false;
    }

    public boolean m() {
        return i() && this.p;
    }

    public void d(IGraphicsEngine iGraphicsEngine) {
        int type;
        if (!i()) {
            if (this.t) {
                int i = u + 1;
                u = i;
                if (i > 100) {
                    return;
                }
            }
            e(iGraphicsEngine);
            return;
        }
        if (!this.p) {
            Bitmap bitmapN = n();
            int internalFormat = GLUtils.getInternalFormat(bitmapN);
            try {
                type = GLUtils.getType(bitmapN);
            } catch (IllegalArgumentException e) {
                GameEngine.log("updateContent: GLUtils.getType failed, defaulting to GL_UNSIGNED_BYTE", (Throwable) e);
                type = 5121;
            }
            iGraphicsEngine.a(this, this.v, this.v, bitmapN, internalFormat, type);
            o();
            this.p = true;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public void b(int i) {
        if (this.n != i) {
            if (this.p) {
                int iG = g();
                GLES20.glTexParameterf(iG, 10241, i);
                GLES20.glTexParameterf(iG, 10240, i);
            }
            this.n = i;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public int h() {
        return this.n;
    }

    private void e(IGraphicsEngine iGraphicsEngine) {
        int type;
        Bitmap bitmapN = n();
        if (bitmapN != null) {
            try {
                int iB = bitmapN.b();
                int iC = bitmapN.c();
                int i = iB + (this.v * 2);
                int i2 = iC + (this.v * 2);
                int iD = d();
                int iE = e();
                this.a = iGraphicsEngine.a().a();
                this.p = true;
                iGraphicsEngine.d(this);
                if (iB == iD && iC == iE) {
                    iGraphicsEngine.a(this, bitmapN, 0);
                    OpenGLRenderer.q();
                } else {
                    int internalFormat = GLUtils.getInternalFormat(bitmapN);
                    try {
                        type = GLUtils.getType(bitmapN);
                    } catch (IllegalArgumentException e) {
                        GameEngine.log("uploadToCanvas: GLUtils.getType failed, defaulting to GL_UNSIGNED_BYTE", (Throwable) e);
                        type = 5121;
                    }
                    Bitmap.Config configD = bitmapN.d();
                    iGraphicsEngine.a(this, internalFormat, type, internalFormat);
                    OpenGLRenderer.q();
                    iGraphicsEngine.a(this, this.v, this.v, bitmapN, internalFormat, type);
                    OpenGLRenderer.q();
                    if (this.v > 0) {
                        iGraphicsEngine.a(this, 0, 0, a(true, configD, iE), internalFormat, type);
                        iGraphicsEngine.a(this, 0, 0, a(false, configD, iD), internalFormat, type);
                    }
                    if (this.v > 0) {
                        if (this.v + iB < iD) {
                            iGraphicsEngine.a(this, this.v + iB, 0, a(true, configD, iE), internalFormat, type);
                        }
                        if (this.v + iC < iE) {
                            iGraphicsEngine.a(this, 0, this.v + iC, a(false, configD, iD), internalFormat, type);
                        }
                    }
                }
                a(iGraphicsEngine);
                this.b = 1;
                this.p = true;
                return;
            } finally {
                o();
            }
        }
        this.b = -1;
        throw new RuntimeException("Texture load fail, no bitmap");
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public boolean c(IGraphicsEngine iGraphicsEngine) {
        d(iGraphicsEngine);
        this.i++;
        return m();
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public void b(IGraphicsEngine iGraphicsEngine) {
        this.a = iGraphicsEngine.a().a();
        if (3553 == 3553) {
            iGraphicsEngine.a(this, 6408, 5121, 6408);
        }
        iGraphicsEngine.d(this);
        this.b = 1;
        a(iGraphicsEngine);
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public int g() {
        return 3553;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public void j() {
        super.j();
        if (this.m != null) {
            o();
        }
    }
}
