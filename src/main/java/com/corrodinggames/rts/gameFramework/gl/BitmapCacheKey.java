package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.Bitmap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ai */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/ai.class */
class BitmapCacheKey implements Cloneable {
    public boolean a;
    public Bitmap.Config b;
    public int c;

    BitmapCacheKey() {
    }

    public int hashCode() {
        int iHashCode = this.b.hashCode() ^ this.c;
        return this.a ? iHashCode : -iHashCode;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BitmapCacheKey)) {
            return false;
        }
        BitmapCacheKey bitmapCacheKey = (BitmapCacheKey) obj;
        return this.a == bitmapCacheKey.a && this.b == bitmapCacheKey.b && this.c == bitmapCacheKey.c;
    }

    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public BitmapCacheKey clone() {
        try {
            return (BitmapCacheKey) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
