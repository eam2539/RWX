package com.corrodinggames.rts.gameFramework.graphics;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/n.class */
public class NullGraphicsRenderer implements GraphicsInterface {
    boolean a = false;

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Rect rect) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(RectF rectF) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Texture texture, float f, float f2, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Texture texture, Rect rect, Rect rect2, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Texture texture, Rect rect, RectF rectF, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float f, float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(int i, PorterDuff.Mode mode) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(int i) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float f, float f2, float f3, float f4, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float[] fArr, int i, int i2, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Rect rect, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(RectF rectF, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(String str, float f, float f2, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float f, float f2, float f3) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void b() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float f, float f2) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float f, float f2, float f3, float f4) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Texture texture) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void b(float f, float f2) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(boolean z) {
        this.a = z;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public boolean c() {
        return this.a;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(GraphicsOperation graphicsOperation) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Bitmap bitmap) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Lock lock) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void b(Lock lock) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public boolean a(ShaderProgram shaderProgram) {
        return false;
    }
}
