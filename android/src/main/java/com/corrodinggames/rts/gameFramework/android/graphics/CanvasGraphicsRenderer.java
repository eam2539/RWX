package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.*;
import com.corrodinggames.rts.gameFramework.m.GraphicsUtils;
import com.corrodinggames.rts.gameFramework.m.UnitTexture;

import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.h */
/* JADX INFO: loaded from: classes.dex */
public final class CanvasGraphicsRenderer implements GraphicsInterface {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public Canvas f770a;
    boolean b = false;

    public CanvasGraphicsRenderer(Canvas canvas) {
        this.f770a = canvas;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, float f4, Region.Op op) {
        this.f770a.clipRect(f, f2, f3, f4, op);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, float f4) {
        this.f770a.clipRect(f, f2, f3, f4);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, int i2, int i3, int i4) {
        this.f770a.clipRect(i, i2, i3, i4);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect, Region.Op op) {
        this.f770a.clipRect(rect, op);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect) {
        this.f770a.clipRect(rect);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, Region.Op op) {
        this.f770a.clipRect(rectF, op);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF) {
        this.f770a.clipRect(rectF);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Matrix matrix) {
        this.f770a.concat(matrix);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(int i, int i2, int i3, int i4) {
        this.f770a.drawARGB(i, i2, i3, i4);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, float f, float f2, boolean z, Paint paint) {
        this.f770a.drawArc(rectF, f, f2, z, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, float f, float f2, Paint paint) {
        this.f770a.drawBitmap(GraphicsUtils.a(unitTexture), f, f2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Matrix matrix, Paint paint) {
        this.f770a.drawBitmap(GraphicsUtils.a(unitTexture), matrix, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint) {
        this.f770a.drawBitmap(GraphicsUtils.a(unitTexture), rect, rect2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Rect rect, RectF rectF, Paint paint) {
        this.f770a.drawBitmap(GraphicsUtils.a(unitTexture), rect, rectF, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int[] iArr, int i, int i2, float f, float f2, int i3, int i4, boolean z, Paint paint) {
        this.f770a.drawBitmap(iArr, i, i2, f, f2, i3, i4, z, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int[] iArr, int i, int i2, int i3, int i4, int i5, int i6, boolean z, Paint paint) {
        this.f770a.drawBitmap(iArr, i, i2, i3, i4, i5, i6, z, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, int i, int i2, float[] fArr, int i3, int[] iArr, int i4, Paint paint) {
        this.f770a.drawBitmapMesh(GraphicsUtils.a(unitTexture), i, i2, fArr, i3, iArr, i4, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, Paint paint) {
        this.f770a.drawCircle(f, f2, f3, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, PorterDuff.Mode mode) {
        this.f770a.drawColor(i, mode);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i) {
        this.f770a.drawColor(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, float f4, Paint paint) {
        this.f770a.drawLine(f, f2, f3, f4, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float[] fArr, int i, int i2, Paint paint) {
        this.f770a.drawLines(fArr, i, i2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float[] fArr, Paint paint) {
        this.f770a.drawLines(fArr, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, Paint paint) {
        this.f770a.drawOval(rectF, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Paint paint) {
        this.f770a.drawPaint(paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Path path, Paint paint) {
        this.f770a.drawPath(path, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture, Rect rect) {
        this.f770a.drawPicture(picture, rect);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture, RectF rectF) {
        this.f770a.drawPicture(picture, rectF);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture) {
        this.f770a.drawPicture(picture);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, Paint paint) {
        this.f770a.drawPoint(f, f2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float[] fArr, int i, int i2, Paint paint) {
        this.f770a.drawPoints(fArr, i, i2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float[] fArr, Paint paint) {
        this.f770a.drawPoints(fArr, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    @Deprecated
    public final void a(char[] cArr, int i, int i2, float[] fArr, Paint paint) {
        this.f770a.drawPosText(cArr, i, i2, fArr, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    @Deprecated
    public final void a(String str, float[] fArr, Paint paint) {
        this.f770a.drawPosText(str, fArr, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, int i2, int i3) {
        this.f770a.drawRGB(i, i2, i3);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f, float f2, float f3, float f4, Paint paint) {
        this.f770a.drawRect(f, f2, f3, f4, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect, Paint paint) {
        this.f770a.drawRect(rect, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(RectF rectF, Paint paint) {
        this.f770a.drawRect(rectF, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, float f, float f2, Paint paint) {
        this.f770a.drawRoundRect(rectF, f, f2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(char[] cArr, int i, int i2, float f, float f2, Paint paint) {
        this.f770a.drawText(cArr, i, i2, f, f2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(CharSequence charSequence, int i, int i2, float f, float f2, Paint paint) {
        this.f770a.drawText(charSequence, i, i2, f, f2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, float f, float f2, Paint paint) {
        this.f770a.drawText(str, f, f2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, int i, int i2, float f, float f2, Paint paint) {
        this.f770a.drawText(str, i, i2, f, f2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(char[] cArr, int i, int i2, Path path, float f, float f2, Paint paint) {
        this.f770a.drawTextOnPath(cArr, i, i2, path, f, f2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, Path path, float f, float f2, Paint paint) {
        this.f770a.drawTextOnPath(str, path, f, f2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Canvas.VertexMode vertexMode, int i, float[] fArr, int i2, float[] fArr2, int i3, int[] iArr, int i4, short[] sArr, int i5, int i6, Paint paint) {
        this.f770a.drawVertices(vertexMode, i, fArr, i2, fArr2, i3, iArr, i4, sArr, i5, i6, paint);
    }

    public final boolean equals(Object obj) {
        return this.f770a.equals(obj);
    }

    public final int hashCode() {
        return this.f770a.hashCode();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a_() {
        this.f770a.restore();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(int i) {
        this.f770a.restoreToCount(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3) {
        this.f770a.rotate(f, f2, f3);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f) {
        this.f770a.rotate(f);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b() {
        this.f770a.save();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f, float f2, float f3, float f4) {
        this.f770a.scale(f, f2, f3, f4);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2) {
        this.f770a.scale(f, f2);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture) {
        this.f770a.setBitmap(unitTexture.b());
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void c(int i) {
        this.f770a.setDensity(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(DrawFilter drawFilter) {
        this.f770a.setDrawFilter(drawFilter);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(Matrix matrix) {
        this.f770a.setMatrix(matrix);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f, float f2) {
        this.f770a.skew(f, f2);
    }

    public final String toString() {
        return this.f770a.toString();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void c(float f, float f2) {
        this.f770a.translate(f, f2);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(boolean z) {
        this.b = z;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final boolean c() {
        return this.b;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(GraphicsOperation graphicsOperation) {
        com.corrodinggames.rts.gameFramework.m.GraphicsContext graphicsContext = new AndroidGraphicsContext().a();
        graphicsContext.a(this);
        graphicsOperation.a(graphicsContext);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Bitmap bitmap) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Lock lock) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(Lock lock) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final boolean a(C0009fo c0009fo) {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final boolean b(C0009fo c0009fo) {
        return false;
    }
}
