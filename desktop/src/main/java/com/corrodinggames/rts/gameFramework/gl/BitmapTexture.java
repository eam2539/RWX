package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.Bitmap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/e.class */
public class BitmapTexture extends DynamicTexture {
    protected Bitmap l;

    public BitmapTexture(Bitmap bitmap) {
        this(bitmap, false);
    }

    public BitmapTexture(Bitmap bitmap, boolean z) {
        super(z);
        this.l = bitmap;
        this.m = k();
        a(this.m.b() + 0, this.m.c() + 0);
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.DynamicTexture
    protected void a(Bitmap bitmap) {
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.DynamicTexture
    protected Bitmap k() {
        return this.l;
    }
}
