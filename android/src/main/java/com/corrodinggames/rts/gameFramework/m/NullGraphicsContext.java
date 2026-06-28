package com.corrodinggames.rts.gameFramework.m;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.gameFramework.android.graphics.AndroidGLRenderer;
import com.corrodinggames.rts.gameFramework.android.graphics.C0009fo;
import com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface;
import com.corrodinggames.rts.gameFramework.android.graphics.GraphicsOperation;

import java.io.InputStream;
import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.fj */
/* JADX INFO: loaded from: classes.dex */
public final class NullGraphicsContext implements GraphicsContext {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    UnitTexture f763a;
    UnitTexture b;

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final GraphicsContext b(UnitTexture unitTexture) {
        return new NullGraphicsContext();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(Context context) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b() {
        this.f763a = new UnitTexture();
        this.b = new UnitTexture();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final GraphicsInterface c() {
        return null;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(GraphicsInterface graphicsInterface) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(AndroidGLRenderer androidGLRenderer) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture a(int i, boolean z) {
        return new UnitTexture();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture a(InputStream inputStream, boolean z) {
        return new UnitTexture();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture a(int i, int i2, boolean z) {
        return b(i, i2, z);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture b(int i, int i2, boolean z) {
        return new UnitTexture();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, float f, float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, Rect rect, float f, float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(Rect rect, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(boolean z) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void d() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, Rect rect, RectF rectF, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, float f, float f2, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, float f, float f2, Paint paint, float f3) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(UnitTexture unitTexture, float f, float f2, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, Rect rect) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, Rect rect, Paint paint, int i, int i2, int i3, int i4) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, RectF rectF, Paint paint, float f, float f2) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(int i) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(PorterDuff.Mode mode) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(String str, float f, float f2, Paint paint, Paint paint2, float f3) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(String str, float f, float f2, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(Rect rect, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(RectF rectF, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void e() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void f() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void c(Rect rect, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(Rect rect) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(RectF rectF) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float f, float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(float f, float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float[] fArr, int i, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void g() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void h() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void i() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void j() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float f, float f2, float f3) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float f, float f2) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float f, float f2, float f3, float f4) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(float f, float f2) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(GraphicsOperation graphicsOperation) {
        graphicsOperation.a(this);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float f, float f2, float f3, float f4, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final int k() {
        return 0;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final int l() {
        return 0;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(int i, int i2) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void m() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(C0009fo c0009fo) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void n() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final int a(Paint paint) {
        return 1;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final int a(String str, Paint paint) {
        return 1;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture o() {
        return this.b;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void p() {
        throw new RuntimeException("writeImageToFile not yet supported");
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(Lock lock) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(Lock lock) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final GraphicsContext a(UnitTexture unitTexture) {
        return new NullGraphicsContext();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture a(int i) {
        return new UnitTexture();
    }
}
