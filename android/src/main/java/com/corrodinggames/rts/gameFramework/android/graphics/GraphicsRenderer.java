package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.Bitmap;
import android.graphics.Paint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.p */
/* JADX INFO: loaded from: classes.dex */
public interface GraphicsRenderer {
    ResourceManagerInterface a();

    void a(float f, float f2, float f3, float f4, DrawStyle drawStyle);

    void a(float f, float f2, float f3, DrawStyle drawStyle, ShaderInterface shaderInterface);

    void a(int i, int i2);

    void a(int i, int i2, int i3, int i4);

    void a(Bitmap bitmap);

    void a(ImageBase imageBase, int i, int i2, int i3);

    void a(ImageBase imageBase, int i, int i2, Bitmap bitmap, int i3, int i4);

    void a(ImageBase imageBase, int i, int i2, GraphicsOption graphicsOption);

    void a(ImageBase imageBase, Bitmap bitmap);

    void a(ShaderLoadInterface shaderLoadInterface);

    void a(ShaderCompileInterface shaderCompileInterface);

    void a(String str, float f, float f2, Paint paint);

    void a(float[] fArr, int i, int i2, DrawStyle drawStyle, ShaderInterface shaderInterface);

    boolean a(ImageBase imageBase);

    void b();

    void b(ImageBase imageBase);

    void c();

    void c(ImageBase imageBase);

    void d();

    void d(ImageBase imageBase);

    void e();

    void f();

    void g();
}
