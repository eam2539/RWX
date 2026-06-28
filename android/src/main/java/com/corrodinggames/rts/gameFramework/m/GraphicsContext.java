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

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.fi */
/* JADX INFO: loaded from: classes.dex */
public interface GraphicsContext {
    int a(Paint paint);

    int a(String str, Paint paint);

    UnitTexture a(int i);

    UnitTexture a(int i, int i2, boolean z);

    UnitTexture a(int i, boolean z);

    UnitTexture a(InputStream inputStream, boolean z);

    GraphicsContext a(UnitTexture unitTexture);

    void a(float f, float f2);

    void a(float f, float f2, float f3);

    void a(float f, float f2, float f3, float f4);

    void a(float f, float f2, float f3, float f4, Paint paint);

    void a(float f, float f2, float f3, Paint paint);

    void a(int i, int i2);

    void a(Context context);

    void a(PorterDuff.Mode mode);

    void a(Rect rect);

    void a(Rect rect, Paint paint);

    void a(RectF rectF);

    void a(RectF rectF, Paint paint);

    void a(AndroidGLRenderer androidGLRenderer);

    void a(UnitTexture unitTexture, float f, float f2, float f3, Paint paint);

    void a(UnitTexture unitTexture, float f, float f2, Paint paint);

    void a(UnitTexture unitTexture, float f, float f2, Paint paint, float f3);

    void a(UnitTexture unitTexture, Rect rect);

    void a(UnitTexture unitTexture, Rect rect, float f, float f2, float f3, Paint paint);

    void a(UnitTexture unitTexture, Rect rect, Paint paint, int i, int i2, int i3, int i4);

    void a(UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint);

    void a(UnitTexture unitTexture, Rect rect, RectF rectF, Paint paint);

    void a(UnitTexture unitTexture, RectF rectF, Paint paint, float f, float f2);

    void a(C0009fo c0009fo);

    void a(GraphicsInterface graphicsInterface);

    void a(GraphicsOperation graphicsOperation);

    void a(String str, float f, float f2, Paint paint);

    void a(String str, float f, float f2, Paint paint, Paint paint2, float f3);

    void a(Lock lock);

    void a(boolean z);

    void a(float[] fArr, int i, Paint paint);

    UnitTexture b(int i, int i2, boolean z);

    GraphicsContext b(UnitTexture unitTexture);

    void b();

    void b(float f, float f2);

    void b(float f, float f2, float f3, Paint paint);

    void b(int i);

    void b(Rect rect, Paint paint);

    void b(UnitTexture unitTexture, float f, float f2, Paint paint);

    void b(UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint);

    void b(Lock lock);

    GraphicsInterface c();

    void c(Rect rect, Paint paint);

    void d();

    void e();

    void f();

    void g();

    void h();

    void i();

    void j();

    int k();

    int l();

    void m();

    void n();

    UnitTexture o();

    void p();
}
