package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.corrodinggames.rts.gameFramework.GameEngine;

import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ao */
/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractImage extends ImageBase {
    private static HashMap m = new HashMap();
    private static BitmapConfigKey p = new BitmapConfigKey((byte) 0);
    private static int v;
    protected Bitmap n;
    int o;
    private boolean q;
    private boolean r;
    private boolean s;
    private boolean t;
    private boolean u;
    private int w;

    protected abstract Bitmap j();

    protected AbstractImage() {
        this((byte) 0);
    }

    protected AbstractImage(byte b) {
        this.q = true;
        this.r = false;
        this.s = false;
        this.t = false;
        this.u = false;
        this.o = 9729;
    }

    private static Bitmap a(boolean z, Bitmap.Config config, int i) {
        BitmapConfigKey bitmapConfigKey = p;
        bitmapConfigKey.f561a = z;
        bitmapConfigKey.b = config;
        bitmapConfigKey.c = i;
        Bitmap bitmapCreateBitmap = (Bitmap) m.get(bitmapConfigKey);
        if (bitmapCreateBitmap == null) {
            if (z) {
                bitmapCreateBitmap = Bitmap.createBitmap(1, i, config);
            } else {
                bitmapCreateBitmap = Bitmap.createBitmap(i, 1, config);
            }
            m.put(bitmapConfigKey.clone(), bitmapCreateBitmap);
        }
        return bitmapCreateBitmap;
    }

    private Bitmap l() {
        if (this.n == null) {
            this.n = j();
            int width = this.n.getWidth() + (this.w * 2);
            int height = this.n.getHeight() + (this.w * 2);
            if (this.c == -1) {
                a(width, height);
            }
        }
        return this.n;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final int b() {
        if (this.c == -1) {
            l();
        }
        return this.c;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final int c() {
        if (this.c == -1) {
            l();
        }
        return this.d;
    }

    public final void k() {
        if (this.n != null) {
            this.n = null;
        }
        this.q = false;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final void a(int i) {
        if (this.o != i) {
            if (this.q) {
                GLES20.glTexParameterf(3553, 10241, i);
                GLES20.glTexParameterf(3553, 10240, i);
            }
            this.o = i;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final int g() {
        return this.o;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final void b(GraphicsRenderer graphicsRenderer) {
        this.f565a = graphicsRenderer.a().a();
        graphicsRenderer.a(this, 6408, 5121, 6408);
        graphicsRenderer.d(this);
        this.b = 1;
        a(graphicsRenderer);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final int f() {
        return 3553;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final void i() {
        super.i();
        if (this.n == null) {
            return;
        }
        this.n = null;
    }

    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0014  */
    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean c(GraphicsRenderer graphicsRenderer) {
        int type;
        int type2;
        if (!h()) {
            // Original DEX jumps into the upload block when u is false; JADX nested it incorrectly.
            boolean shouldUpload = true;
            if (this.u) {
                int i = v + 1;
                v = i;
                shouldUpload = i <= 100;
            }
            if (shouldUpload) {
                Bitmap bitmapL = l();
                if (bitmapL != null) {
                    try {
                        int width = bitmapL.getWidth();
                        int height = bitmapL.getHeight();
                        int iD = d();
                        int iE = e();
                        this.f565a = graphicsRenderer.a().a();
                        this.q = true;
                        graphicsRenderer.d(this);
                        if (width == iD && height == iE) {
                            graphicsRenderer.a(this, bitmapL);
                            OpenGLRenderer.j();
                        } else {
                            int internalFormat = GLUtils.getInternalFormat(bitmapL);
                            try {
                                type2 = GLUtils.getType(bitmapL);
                            } catch (IllegalArgumentException e) {
                                GameEngine.log("uploadToCanvas: GLUtils.getType failed, defaulting to GL_UNSIGNED_BYTE", (Throwable) e);
                                type2 = 5121;
                            }
                            Bitmap.Config config = bitmapL.getConfig();
                            graphicsRenderer.a(this, internalFormat, type2, internalFormat);
                            OpenGLRenderer.j();
                            graphicsRenderer.a(this, this.w, this.w, bitmapL, internalFormat, type2);
                            OpenGLRenderer.j();
                            if (this.w > 0) {
                                graphicsRenderer.a(this, 0, 0, a(true, config, iE), internalFormat, type2);
                                graphicsRenderer.a(this, 0, 0, a(false, config, iD), internalFormat, type2);
                            }
                            if (this.w > 0) {
                                if (this.w + width < iD) {
                                    graphicsRenderer.a(this, this.w + width, 0, a(true, config, iE), internalFormat, type2);
                                }
                                if (this.w + height < iE) {
                                    graphicsRenderer.a(this, 0, this.w + height, a(false, config, iD), internalFormat, type2);
                                }
                            }
                        }
                        this.n = null;
                        a(graphicsRenderer);
                        this.b = 1;
                        this.q = true;
                    } catch (Throwable th) {
                        this.n = null;
                        throw th;
                    }
                } else {
                    this.b = -1;
                    throw new RuntimeException("Texture load fail, no bitmap");
                }
            }
        } else if (!this.q) {
            Bitmap bitmapL2 = l();
            int internalFormat2 = GLUtils.getInternalFormat(bitmapL2);
            try {
                type = GLUtils.getType(bitmapL2);
            } catch (IllegalArgumentException e2) {
                GameEngine.log("updateContent: GLUtils.getType failed, defaulting to GL_UNSIGNED_BYTE", (Throwable) e2);
                type = 5121;
            }
            graphicsRenderer.a(this, this.w, this.w, bitmapL2, internalFormat2, type);
            this.n = null;
            this.q = true;
        }
        this.j++;
        return h() && this.q;
    }
}
