package com.corrodinggames.rts.gameFramework.graphics;

import android.graphics.*;

import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/l.class */
public interface GraphicsInterface {
    void a(boolean z);

    boolean c();

    void a(Rect rect);

    void a(RectF rectF);

    void a(Texture texture, float f, float f2, Paint paint);

    void a(Texture texture, Rect rect, Rect rect2, Paint paint);

    void a(Texture texture, Rect rect, RectF rectF, Paint paint);

    void a(float f, float f2, float f3, Paint paint);

    void a(int i, PorterDuff.Mode mode);

    void a(int i);

    void a(float f, float f2, float f3, float f4, Paint paint);

    void a(float[] fArr, int i, int i2, Paint paint);

    void a(Rect rect, Paint paint);

    void a(RectF rectF, Paint paint);

    void a(String str, float f, float f2, Paint paint);

    void a();

    void a(float f, float f2, float f3);

    void b();

    void a(float f, float f2);

    void a(float f, float f2, float f3, float f4);

    void a(Texture texture);

    void b(float f, float f2);

    void a(GraphicsOperation graphicsOperation);

    void a(Bitmap bitmap);

    void a(Lock lock);

    void b(Lock lock);

    boolean a(ShaderProgram shaderProgram);
}
