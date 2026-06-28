package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.RectF;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.am */
/* JADX INFO: loaded from: classes.dex */
public final class SubTexture extends ImageBase {
    BackingTexture m;
    public float n;
    public float o;
    public int p;
    public int q;

    public SubTexture() {
        super((byte) 0);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    protected final boolean c(GraphicsRenderer graphicsRenderer) {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    protected final int f() {
        return 3553;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final void a(int i) {
        this.m.a(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final int g() {
        return this.m.m;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final void a(RectF rectF) {
        float f = this.g;
        float f2 = this.h;
        rectF.left = (rectF.left * f) + this.n;
        rectF.right = (f * rectF.right) + this.n;
        rectF.top = (rectF.top * f2) + this.o;
        rectF.bottom = (rectF.bottom * f2) + this.o;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final void a(RectF rectF, RectF rectF2) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageBase
    public final void b(GraphicsRenderer graphicsRenderer) {
        GameEngine.log("SubTexture prepare TODO");
    }
}
