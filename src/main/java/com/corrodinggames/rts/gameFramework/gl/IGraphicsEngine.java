package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.Bitmap;
import android.graphics.Paint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/k.class */
public interface IGraphicsEngine {
    ITextureResource a();

    void b();

    void c();

    void a(float f, float f2, float f3, PaintStyle paintStyle, IShaderProgram iShaderProgram);

    void a(float f, float f2, float f3, float f4, PaintStyle paintStyle, IShaderProgram iShaderProgram);

    void a(Texture texture, int i, int i2, int i3, int i4, ITextureFilter iTextureFilter, MatrixCalculator matrixCalculator);

    boolean a(Texture texture);

    void b(Texture texture);

    void c(Texture texture);

    void d();

    void d(Texture texture);

    void a(Texture texture, int i, int i2, int i3);

    void a(Texture texture, Bitmap bitmap, int i);

    void a(Texture texture, int i, int i2, Bitmap bitmap, int i3, int i4);

    void a(int i, int i2, int i3, int i4);

    void a(String str, float f, float f2, Paint paint);

    void a(float[] fArr, int i, int i2, PaintStyle paintStyle, IShaderProgram iShaderProgram);

    void e();

    void f();

    void a(Bitmap bitmap);
}
