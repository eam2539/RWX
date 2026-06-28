package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.Bitmap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ap */
/* JADX INFO: loaded from: classes.dex */
final class BitmapConfigKey implements Cloneable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public boolean f561a;
    public Bitmap.Config b;
    public int c;

    private BitmapConfigKey() {
    }

    /* synthetic */ BitmapConfigKey(byte b) {
        this();
    }

    public final int hashCode() {
        int iHashCode = this.b.hashCode() ^ this.c;
        return this.f561a ? iHashCode : -iHashCode;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof BitmapConfigKey)) {
            return false;
        }
        BitmapConfigKey bitmapConfigKey = (BitmapConfigKey) obj;
        return this.f561a == bitmapConfigKey.f561a && this.b == bitmapConfigKey.b && this.c == bitmapConfigKey.c;
    }

    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public final BitmapConfigKey clone() {
        try {
            return (BitmapConfigKey) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
