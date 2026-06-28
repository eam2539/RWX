package com.corrodinggames.rts.gameFramework.android.graphics;

import android.util.Log;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.af */
/* JADX INFO: loaded from: classes.dex */
public final class RawTexture extends ImageBase {
    protected boolean m;
    private final boolean n;
    private int o;

    public RawTexture(int i, int i2) {
        this(i, i2, (byte) 0);
    }

    private RawTexture(int i, int i2, byte b) {
        super((byte) 0);
        this.o = 3553;
        this.n = false;
        a(i, i2);
        this.o = 3553;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final void b(GraphicsRenderer graphicsRenderer) {
        this.f565a = graphicsRenderer.a().a();
        if (this.o == 3553) {
            graphicsRenderer.a(this, 6408, 5121, 6408);
        }
        graphicsRenderer.d(this);
        this.b = 1;
        a(graphicsRenderer);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    protected final boolean c(GraphicsRenderer graphicsRenderer) {
        if (h()) {
            return true;
        }
        Log.w("RawTexture", "lost the content due to context change");
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final int f() {
        return this.o;
    }

    public final boolean j() {
        return this.m;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final void a(int i) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final int g() {
        return 9729;
    }
}
