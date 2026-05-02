package com.corrodinggames.rts.gameFramework.graphics;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.y */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/y.class */
public interface GraphicsEngine {
    GraphicsEngine b(Texture texture);

    GraphicsEngine a(Texture texture);

    boolean a();

    void a(Context context);

    void b();

    GraphicsInterface d();

    void a(GraphicsInterface graphicsInterface);

    void a(AudioRenderer audioRenderer);

    Texture a(int i);

    Texture a(int i, boolean z);

    Texture a(InputStream inputStream, boolean z);

    Texture a(int i, int i2, boolean z);

    Texture b(int i, int i2, boolean z);

    void e();

    void a(Texture texture, float f, float f2, float f3, Paint paint);

    void a(Texture texture, Rect rect, float f, float f2, float f3, Paint paint);

    void a(Texture texture, Rect rect, Rect rect2, Paint paint);

    void a(Texture texture, Rect rect, RectF rectF, Paint paint);

    void a(Texture texture, float f, float f2, Paint paint);

    void a(Texture texture, float f, float f2, Paint paint, float f3, float f4);

    void b(Texture texture, float f, float f2, Paint paint);

    void b(Texture texture, Rect rect, Rect rect2, Paint paint);

    void a(Rect rect, Paint paint);

    void a(boolean z);

    void f();

    void a(Texture texture, Rect rect, Paint paint);

    void a(Texture texture, Rect rect, Paint paint, int i, int i2, int i3, int i4);

    void a(Texture texture, RectF rectF, Paint paint, float f, float f2, int i, int i2);

    void b(int i);

    void a(int i, PorterDuff.Mode mode);

    void a(String str, float f, float f2, Paint paint, Paint paint2, float f3);

    void a(String str, float f, float f2, Paint paint);

    void b(Rect rect, Paint paint);

    void a(RectF rectF, Paint paint);

    void g();

    void h();

    void c(Rect rect, Paint paint);

    void a(Rect rect);

    void a(RectF rectF);

    void a(float f, float f2, float f3, Paint paint);

    void b(float f, float f2, float f3, Paint paint);

    void a(float[] fArr, int i, int i2, Paint paint);

    void i();

    void j();

    void k();

    void l();

    void a(float f, float f2, float f3);

    void a(float f, float f2);

    void a(float f, float f2, float f3, float f4);

    void b(float f, float f2);

    void a(GraphicsOperation graphicsOperation);

    void a(float f, float f2, float f3, float f4, Paint paint);

    int m();

    int n();

    void a(int i, int i2);

    void o();

    void a(Paint paint);

    void p();

    void q();

    int a(String str, Paint paint);

    int b(String str, Paint paint);

    Texture r();

    void a(Texture texture, File file);

    void a(Lock lock);

    void b(Lock lock);

    void a(ShaderProgram shaderProgram);

    float s();
}
