package com.corrodinggames.rts.gameFramework.m;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.fc */
/* JADX INFO: loaded from: classes.dex */
public final class ObjectPool {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final FastArrayList f759a = new FastArrayList();
    public int b;
    public Class c;

    public ObjectPool(Class cls) {
        this.c = cls;
    }

    public final FloatHolder a(float f) {
        if (this.b >= this.f759a.size) {
            this.f759a.add(new FloatHolder());
        }
        FloatHolder floatHolder = (FloatHolder) this.f759a.a()[this.b];
        floatHolder.f760a = f;
        this.b++;
        return floatHolder;
    }

    public final Rect a(Rect rect) {
        if (this.b >= this.f759a.size) {
            this.f759a.add(new Rect());
        }
        Rect rect2 = (Rect) this.f759a.a()[this.b];
        rect2.top = rect.top;
        rect2.bottom = rect.bottom;
        rect2.left = rect.left;
        rect2.right = rect.right;
        this.b++;
        return rect2;
    }

    public final RectF a(RectF rectF) {
        if (this.b >= this.f759a.size) {
            this.f759a.add(new RectF());
        }
        RectF rectF2 = (RectF) this.f759a.a()[this.b];
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
        if (this.b >= this.f759a.size) {
            this.f759a.add(new Paint());
        }
        Paint paint2 = (Paint) this.f759a.a()[this.b];
        paint2.set(paint);
        this.b++;
        return paint2;
    }

    public final Matrix a(Matrix matrix) {
        if (matrix == null) {
            return null;
        }
        if (this.b >= this.f759a.size) {
            this.f759a.add(new Matrix());
        }
        Matrix matrix2 = (Matrix) this.f759a.a()[this.b];
        matrix2.set(matrix);
        this.b++;
        return matrix2;
    }
}
