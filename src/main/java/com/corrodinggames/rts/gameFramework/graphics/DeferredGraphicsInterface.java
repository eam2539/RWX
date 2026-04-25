package com.corrodinggames.rts.gameFramework.graphics;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.opengl.EmptyGraphicsClass;
import com.corrodinggames.rts.gameFramework.graphics.opengl.GraphicsCommand;
import com.corrodinggames.rts.gameFramework.graphics.opengl.GraphicsCommandBuffer;
import com.corrodinggames.rts.gameFramework.graphics.opengl.GraphicsObjectPool;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.util.concurrent.locks.Lock;
import com.corrodinggames.rts.gameFramework.graphics.opengl.GraphicsOperation;
/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/o.class */
public final class DeferredGraphicsInterface extends AbstractGraphicsRenderer {
    int b;
    GraphicsInterface a = new CanvasGraphicsRenderer(null);
    final FastArrayList c = new FastArrayList();
    final GraphicsObjectPool d = new GraphicsObjectPool(Paint.class);
    final GraphicsObjectPool e = new GraphicsObjectPool(Rect.class);
    final GraphicsObjectPool f = new GraphicsObjectPool(RectF.class);
    final GraphicsObjectPool g = new GraphicsObjectPool(Matrix.class);
    final GraphicsObjectPool h = new GraphicsObjectPool(EmptyGraphicsClass.class);
    final GraphicsCommandBuffer i = new GraphicsCommandBuffer(200);
    int j = 0;
    volatile boolean k = false;

    public DeferredGraphicsInterface() {
        this.c.add(this.d);
        this.c.add(this.e);
        this.c.add(this.f);
        this.c.add(this.g);
        this.c.add(this.h);
    }

    public GraphicsCommand a(GraphicsOperation graphicsOperation, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
        GraphicsCommandBuffer graphicsCommandBuffer = this.i;
        int i = this.j;
        if (i >= graphicsCommandBuffer.a) {
            graphicsCommandBuffer.a(new GraphicsCommand(this));
        }
        GraphicsCommand graphicsCommand = graphicsCommandBuffer.b[i];
        graphicsCommand.a = graphicsOperation;
        Object[] objArr = graphicsCommand.b;
        objArr[0] = obj;
        objArr[1] = obj2;
        objArr[2] = obj3;
        objArr[3] = obj4;
        objArr[4] = obj5;
        objArr[5] = obj6;
        objArr[6] = obj7;
        objArr[7] = obj8;
        this.j++;
        return graphicsCommand;
    }

    public void a(GraphicsOperation graphicsOperation, Object obj, Object obj2, Object obj3, Object obj4) {
        GraphicsCommandBuffer graphicsCommandBuffer = this.i;
        int i = this.j;
        if (i >= graphicsCommandBuffer.a) {
            graphicsCommandBuffer.a(new GraphicsCommand(this));
        }
        GraphicsCommand graphicsCommand = graphicsCommandBuffer.b[i];
        graphicsCommand.a = graphicsOperation;
        Object[] objArr = graphicsCommand.b;
        objArr[0] = obj;
        objArr[1] = obj2;
        objArr[2] = obj3;
        objArr[3] = obj4;
        this.j++;
    }

    public void a(GraphicsOperation graphicsOperation, Object obj, Object obj2) {
        GraphicsCommandBuffer graphicsCommandBuffer = this.i;
        int i = this.j;
        if (i >= graphicsCommandBuffer.a) {
            graphicsCommandBuffer.a(new GraphicsCommand(this));
        }
        GraphicsCommand graphicsCommand = graphicsCommandBuffer.b[i];
        graphicsCommand.a = graphicsOperation;
        Object[] objArr = graphicsCommand.b;
        objArr[0] = obj;
        objArr[1] = obj2;
        this.j++;
    }

    public GraphicsCommand a(GraphicsOperation graphicsOperation) {
        GraphicsCommandBuffer graphicsCommandBuffer = this.i;
        int i = this.j;
        if (i >= graphicsCommandBuffer.a) {
            graphicsCommandBuffer.a(new GraphicsCommand(this));
        }
        GraphicsCommand graphicsCommand = graphicsCommandBuffer.b[i];
        graphicsCommand.a = graphicsOperation;
        this.j++;
        return graphicsCommand;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(boolean z) {
        this.k = z;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public boolean c() {
        return this.k;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Rect rect) {
        a(GraphicsOperation.clipRect_Rect, this.e.a(rect), null, null, null, null, null, null, null);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(RectF rectF) {
        a(GraphicsOperation.clipRect_RectF, this.f.a(rectF), null, null, null, null, null, null, null);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Texture texture, float f, float f2, Paint paint) {
        if (!(paint instanceof GamePaint)) {
            paint = this.d.a(paint);
        }
        GraphicsCommand graphicsCommandA = a(GraphicsOperation.drawBitmap_Bitmap_float_float_Paint);
        graphicsCommandA.b[0] = texture;
        graphicsCommandA.b[1] = paint;
        graphicsCommandA.c = f;
        graphicsCommandA.d = f2;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Texture texture, Rect rect, Rect rect2, Paint paint) {
        Rect rectA = this.e.a(rect);
        Rect rectA2 = this.e.a(rect2);
        if (!(paint instanceof GamePaint)) {
            paint = this.d.a(paint);
        }
        a(GraphicsOperation.drawBitmap_Bitmap_Rect_Rect_Paint, texture, rectA, rectA2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Texture texture, Rect rect, RectF rectF, Paint paint) {
        Rect rectA = this.e.a(rect);
        RectF rectFA = this.f.a(rectF);
        if (!(paint instanceof GamePaint)) {
            paint = this.d.a(paint);
        }
        a(GraphicsOperation.drawBitmap_Bitmap_Rect_RectF_Paint, texture, rectA, rectFA, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float f, float f2, float f3, Paint paint) {
        if (!(paint instanceof GamePaint)) {
            paint = this.d.a(paint);
        }
        a(GraphicsOperation.drawCircle_float_float_float_Paint, Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(int i, PorterDuff.Mode mode) {
        a(GraphicsOperation.drawColor_int_Mode, Integer.valueOf(i), mode);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(int i) {
        a(GraphicsOperation.drawColor_int, Integer.valueOf(i), null, null, null, null, null, null, null);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float f, float f2, float f3, float f4, Paint paint) {
        if (!(paint instanceof GamePaint)) {
            paint = this.d.a(paint);
        }
        GraphicsCommand graphicsCommandA = a(GraphicsOperation.drawLine_float_float_float_float_Paint);
        graphicsCommandA.c = f;
        graphicsCommandA.d = f2;
        graphicsCommandA.e = f3;
        graphicsCommandA.f = f4;
        graphicsCommandA.b[0] = paint;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float[] fArr, int i, int i2, Paint paint) {
        if (!(paint instanceof GamePaint)) {
            paint = this.d.a(paint);
        }
        a(GraphicsOperation.drawPoints_floatarray_int_int_Paint, fArr, Integer.valueOf(i), Integer.valueOf(i2), paint, null, null, null, null);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Rect rect, Paint paint) {
        Rect rectA = this.e.a(rect);
        if (!(paint instanceof GamePaint)) {
            paint = this.d.a(paint);
        }
        a(GraphicsOperation.drawRect_Rect_Paint, rectA, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(RectF rectF, Paint paint) {
        RectF rectFA = this.f.a(rectF);
        if (!(paint instanceof GamePaint)) {
            paint = this.d.a(paint);
        }
        a(GraphicsOperation.drawRect_RectF_Paint, rectFA, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(String str, float f, float f2, Paint paint) {
        if (!(paint instanceof GamePaint)) {
            paint = this.d.a(paint);
        }
        a(GraphicsOperation.drawText_String_float_float_Paint, str, Float.valueOf(f), Float.valueOf(f2), paint, null, null, null, null);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a() {
        a(GraphicsOperation.restore);
        this.b--;
        if (this.b < 0) {
            GameEngine.logWarningAndStack("saveStackSize: " + this.b);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float f, float f2, float f3) {
        GraphicsCommand graphicsCommandA = a(GraphicsOperation.rotate_float_float_float);
        graphicsCommandA.c = f;
        graphicsCommandA.d = f2;
        graphicsCommandA.e = f3;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void b() {
        a(GraphicsOperation.save);
        this.b++;
        if (this.b <= 0) {
            GameEngine.logWarningAndStack("saveStackSize (on save): " + this.b);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float f, float f2) {
        GraphicsCommand graphicsCommandA = a(GraphicsOperation.scale_float_float);
        graphicsCommandA.c = f;
        graphicsCommandA.d = f2;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(float f, float f2, float f3, float f4) {
        GraphicsCommand graphicsCommandA = a(GraphicsOperation.scale_float_float_float_float);
        graphicsCommandA.c = f;
        graphicsCommandA.d = f2;
        graphicsCommandA.e = f3;
        graphicsCommandA.f = f4;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Texture texture) {
        a(GraphicsOperation.setBitmap_Bitmap, texture, null, null, null, null, null, null, null);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void b(float f, float f2) {
        GraphicsCommand graphicsCommandA = a(GraphicsOperation.translate_float_float);
        graphicsCommandA.c = f;
        graphicsCommandA.d = f2;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(com.corrodinggames.rts.gameFramework.graphics.GraphicsOperation graphicsOperation) {
        a(GraphicsOperation.runDrawTimeCallback_DrawTimeCallback, this, graphicsOperation);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Bitmap bitmap) {
        a(GraphicsOperation.flushBitmap, bitmap, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void a(Lock lock) {
        a(GraphicsOperation.enterLock_object, lock, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public void b(Lock lock) {
        a(GraphicsOperation.leaveLock_object, lock, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
    public boolean a(ShaderProgram shaderProgram) {
        a(GraphicsOperation.compileShader_object, shaderProgram, (Object) null);
        return true;
    }
}
