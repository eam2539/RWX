package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.*;
import com.corrodinggames.rts.gameFramework.m.UnitTexture;

import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.l */
/* JADX INFO: loaded from: classes.dex */
public interface GraphicsInterface {
    void a(float f);

    void a(float f, float f2);

    void a(float f, float f2, float f3);

    void a(float f, float f2, float f3, float f4);

    void a(float f, float f2, float f3, float f4, Paint paint);

    void a(float f, float f2, float f3, float f4, Region.Op op);

    void a(float f, float f2, float f3, Paint paint);

    void a(float f, float f2, Paint paint);

    void a(int i);

    void a(int i, int i2, int i3);

    void a(int i, int i2, int i3, int i4);

    void a(int i, PorterDuff.Mode mode);

    void a(Bitmap bitmap);

    void a(Canvas.VertexMode vertexMode, int i, float[] fArr, int i2, float[] fArr2, int i3, int[] iArr, int i4, short[] sArr, int i5, int i6, Paint paint);

    void a(DrawFilter drawFilter);

    void a(Matrix matrix);

    void a(Paint paint);

    void a(Path path, Paint paint);

    void a(Picture picture);

    void a(Picture picture, Rect rect);

    void a(Picture picture, RectF rectF);

    void a(Rect rect);

    void a(Rect rect, Paint paint);

    void a(Rect rect, Region.Op op);

    void a(RectF rectF);

    void a(RectF rectF, float f, float f2, Paint paint);

    void a(RectF rectF, float f, float f2, boolean z, Paint paint);

    void a(RectF rectF, Paint paint);

    void a(RectF rectF, Region.Op op);

    void a(UnitTexture unitTexture);

    void a(UnitTexture unitTexture, float f, float f2, Paint paint);

    void a(UnitTexture unitTexture, int i, int i2, float[] fArr, int i3, int[] iArr, int i4, Paint paint);

    void a(UnitTexture unitTexture, Matrix matrix, Paint paint);

    void a(UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint);

    void a(UnitTexture unitTexture, Rect rect, RectF rectF, Paint paint);

    void a(GraphicsOperation graphicsOperation);

    void a(CharSequence charSequence, int i, int i2, float f, float f2, Paint paint);

    void a(String str, float f, float f2, Paint paint);

    void a(String str, int i, int i2, float f, float f2, Paint paint);

    void a(String str, Path path, float f, float f2, Paint paint);

    @Deprecated
    void a(String str, float[] fArr, Paint paint);

    void a(Lock lock);

    void a(boolean z);

    void a(char[] cArr, int i, int i2, float f, float f2, Paint paint);

    void a(char[] cArr, int i, int i2, Path path, float f, float f2, Paint paint);

    @Deprecated
    void a(char[] cArr, int i, int i2, float[] fArr, Paint paint);

    void a(float[] fArr, int i, int i2, Paint paint);

    void a(float[] fArr, Paint paint);

    void a(int[] iArr, int i, int i2, float f, float f2, int i3, int i4, boolean z, Paint paint);

    void a(int[] iArr, int i, int i2, int i3, int i4, int i5, int i6, boolean z, Paint paint);

    boolean a(C0009fo c0009fo);

    void a_();

    void b();

    void b(float f, float f2);

    void b(float f, float f2, float f3, float f4);

    void b(float f, float f2, float f3, float f4, Paint paint);

    void b(int i);

    void b(int i, int i2, int i3, int i4);

    void b(Matrix matrix);

    void b(RectF rectF, Paint paint);

    void b(Lock lock);

    void b(float[] fArr, int i, int i2, Paint paint);

    void b(float[] fArr, Paint paint);

    boolean b(C0009fo c0009fo);

    void c(float f, float f2);

    void c(int i);

    boolean c();
}
