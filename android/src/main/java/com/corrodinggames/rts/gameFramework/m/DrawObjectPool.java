package com.corrodinggames.rts.gameFramework.m;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.ck */
/* JADX INFO: loaded from: classes.dex */
public final class DrawObjectPool {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final FastArrayList f752a = new FastArrayList();
    public int b;
    public Class c;

    public DrawObjectPool(Class cls) {
        this.c = cls;
    }

    public final Rect a(Rect rect) {
        if (this.b >= this.f752a.size) {
            this.f752a.add(new Rect());
        }
        Rect rect2 = (Rect) this.f752a.a()[this.b];
        rect2.top = rect.top;
        rect2.bottom = rect.bottom;
        rect2.left = rect.left;
        rect2.right = rect.right;
        this.b++;
        return rect2;
    }

    public final RectF a(RectF rectF) {
        if (this.b >= this.f752a.size) {
            this.f752a.add(new RectF());
        }
        RectF rectF2 = (RectF) this.f752a.a()[this.b];
        rectF2.top = rectF.top;
        rectF2.bottom = rectF.bottom;
        rectF2.left = rectF.left;
        rectF2.right = rectF.right;
        this.b++;
        return rectF2;
    }

    public final Paint a(Paint paint) {
        if (paint == null) {
            return null;
        }
        if (this.b >= this.f752a.size) {
            this.f752a.add(new Paint());
        }
        Paint paint2 = (Paint) this.f752a.a()[this.b];
        paint2.set(paint);
        this.b++;
        return paint2;
    }

    public final Matrix a(Matrix matrix) {
        if (matrix == null) {
            return null;
        }
        if (this.b >= this.f752a.size) {
            this.f752a.add(new Matrix());
        }
        Matrix matrix2 = (Matrix) this.f752a.a()[this.b];
        matrix2.set(matrix);
        this.b++;
        return matrix2;
    }
}
