package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.RectF;
import android.util.Log;

import java.util.WeakHashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.c */
/* JADX INFO: loaded from: classes.dex */
public abstract class ImageBase implements TextureInterface {
    private static WeakHashMap n = new WeakHashMap();
    private static ThreadLocal o = new ThreadLocal();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    protected int f565a;
    protected int b;
    protected int c;
    protected int d;
    protected int e;
    protected int f;
    protected float g;
    protected float h;
    boolean i;
    public int j;
    public boolean k;
    protected GraphicsRenderer l;
    private boolean m;

    public abstract void a(int i);

    public abstract void b(GraphicsRenderer graphicsRenderer);

    protected abstract boolean c(GraphicsRenderer graphicsRenderer);

    protected abstract int f();

    public abstract int g();

    protected ImageBase() {
        this.f565a = -1;
        this.c = -1;
        this.d = -1;
        this.l = null;
        this.l = null;
        this.f565a = 0;
        this.b = 0;
        synchronized (n) {
            n.put(this, null);
        }
    }

    protected ImageBase(byte b) {
        this();
    }

    protected final void a(GraphicsRenderer graphicsRenderer) {
        this.l = graphicsRenderer;
    }

    public final void a(int i, int i2) {
        this.c = i;
        this.d = i2;
        this.e = i > 0 ? b(i) : 0;
        this.f = i2 > 0 ? b(i2) : 0;
        if (this.e == 0) {
            this.g = 0.0f;
        } else {
            this.g = 1.0f / this.e;
        }
        if (this.f == 0) {
            this.h = 0.0f;
        } else {
            this.h = 1.0f / this.f;
        }
        if (this.e > 4096 || this.f > 4096) {
            Log.w("BasicTexture", String.format("secondBitmap is too large: %d x %d", Integer.valueOf(this.e), Integer.valueOf(this.f)), new Exception());
        }
    }

    private static int b(int i) {
        if (i <= 0 || i > 1073741824) {
            throw new IllegalArgumentException("n is invalid: ".concat(String.valueOf(i)));
        }
        int i2 = i - 1;
        int i3 = i2 | (i2 >> 16);
        int i4 = i3 | (i3 >> 8);
        int i5 = i4 | (i4 >> 4);
        int i6 = i5 | (i5 >> 2);
        return (i6 | (i6 >> 1)) + 1;
    }

    public final int a() {
        return this.f565a;
    }

    public int b() {
        return this.c;
    }

    public int c() {
        return this.d;
    }

    public final int d() {
        return this.e;
    }

    public final int e() {
        return this.f;
    }

    public final boolean h() {
        return this.b == 1;
    }

    public void i() {
        this.m = true;
        GraphicsRenderer graphicsRenderer = this.l;
        if (graphicsRenderer != null && this.f565a != -1) {
            graphicsRenderer.a(this);
            this.f565a = -1;
        }
        this.b = 0;
        this.l = null;
    }

    protected void finalize() {
        o.set(ImageBase.class);
        i();
        o.set(null);
    }

    public void a(RectF rectF) {
        int iB = b();
        int iC = c();
        int i = this.e;
        int i2 = this.f;
        rectF.left /= i;
        rectF.right /= i;
        rectF.top /= i2;
        rectF.bottom /= i2;
        float f = ((float) iB) / i;
        if (rectF.right > f) {
            rectF.right = f;
        }
        float f2 = ((float) iC) / i2;
        if (rectF.bottom > f2) {
            rectF.bottom = f2;
        }
    }

    public void a(RectF rectF, RectF rectF2) {
        float fC = ((float) c()) / this.f;
        float fB = ((float) b()) / this.e;
        if (rectF.right > fB) {
            rectF2.right = (((fB - rectF.left) * rectF2.width()) / rectF.width()) + rectF2.left;
        }
        if (rectF.bottom > fC) {
            rectF2.bottom = (((fC - rectF.top) * rectF2.height()) / rectF.height()) + rectF2.top;
        }
    }
}
