package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.Bitmap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.g */
/* JADX INFO: loaded from: classes.dex */
public final class BitmapImage extends AbstractImage {
    protected Bitmap m;

    public BitmapImage(Bitmap bitmap) {
        this(bitmap, (byte) 0);
    }

    private BitmapImage(Bitmap bitmap, byte b) {
        super((byte) 0);
        this.m = bitmap;
        this.n = this.m;
        a(this.n.getWidth() + 0, this.n.getHeight() + 0);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.AbstractImage
    protected final Bitmap j() {
        return this.m;
    }
}
