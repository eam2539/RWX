package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.*;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.m.*;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

import java.util.Iterator;
import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.co */
/* JADX INFO: loaded from: classes.dex */
public final class DeferredGraphicsRenderer extends AbstractGraphicsRenderer {
    public com.corrodinggames.rts.gameFramework.m.GraphicsContext b;

    /* JADX INFO: renamed from: a */
    GraphicsInterface f755a = new CanvasGraphicsRenderer(null);
    final FastArrayList c = new FastArrayList();
    final ObjectPool d = new ObjectPool(Paint.class);
    final ObjectPool e = new ObjectPool(Rect.class);
    final ObjectPool f = new ObjectPool(RectF.class);
    final ObjectPool g = new ObjectPool(Matrix.class);
    final ObjectPool h = new ObjectPool(FloatHolder.class);
    public FastArrayList i = new FastArrayList();
    final FastArrayList j = new FastArrayList(200);
    int k = 0;
    public volatile boolean l = false;

    public DeferredGraphicsRenderer() {
        this.c.add(this.d);
        this.c.add(this.e);
        this.c.add(this.f);
        this.c.add(this.g);
        this.c.add(this.h);
        for (int i = 0; i < 1000; i++) {
            this.j.add(new CanvasDrawCommand());
        }
    }

    private void d() {
        FastArrayList fastArrayList = this.i;
        if (fastArrayList.size > 0) {
            this.i = new FastArrayList();
            Iterator it = fastArrayList.iterator();
            while (it.hasNext()) {
                ((Lock) it.next()).unlock();
                GameEngine.log("removeAllLocksStillHeld: found lock");
            }
            fastArrayList.clear();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsRenderer
    public final void a(Canvas canvas) {
        Object[] objArr = this.j.a();
        int i = this.k;
        try {
            for (int i2 = 0; i2 < i; i2++) {
                CanvasDrawCommand canvasDrawCommand = (CanvasDrawCommand) objArr[i2];
                canvasDrawCommand.f761a.a(canvas, canvasDrawCommand);
            }
        } finally {
            d();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsRenderer
    public final void a() {
        Iterator it = this.c.iterator();
        while (it.hasNext()) {
            ((ObjectPool) it.next()).b = 0;
        }
        this.k = 0;
    }

    private void a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation graphicsOperation, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        if (this.k >= this.j.size) {
            this.j.add(new CanvasDrawCommand());
        }
        CanvasDrawCommand canvasDrawCommand = (CanvasDrawCommand) this.j.a()[this.k];
        canvasDrawCommand.f761a = graphicsOperation;
        Object[] objArr = canvasDrawCommand.b;
        objArr[0] = obj;
        objArr[1] = obj2;
        objArr[2] = obj3;
        objArr[3] = obj4;
        objArr[4] = obj5;
        objArr[5] = obj6;
        this.k++;
    }

    private void a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation graphicsOperation, Object obj, Object obj2, Object obj3, Object obj4) {
        if (this.k >= this.j.size) {
            this.j.add(new CanvasDrawCommand());
        }
        CanvasDrawCommand canvasDrawCommand = (CanvasDrawCommand) this.j.a()[this.k];
        canvasDrawCommand.f761a = graphicsOperation;
        Object[] objArr = canvasDrawCommand.b;
        objArr[0] = obj;
        objArr[1] = obj2;
        objArr[2] = obj3;
        objArr[3] = obj4;
        this.k++;
    }

    private void a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation graphicsOperation, Object obj, Object obj2) {
        if (this.k >= this.j.size) {
            this.j.add(new CanvasDrawCommand());
        }
        CanvasDrawCommand canvasDrawCommand = (CanvasDrawCommand) this.j.a()[this.k];
        canvasDrawCommand.f761a = graphicsOperation;
        Object[] objArr = canvasDrawCommand.b;
        objArr[0] = obj;
        objArr[1] = obj2;
        this.k++;
    }

    private CanvasDrawCommand a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation graphicsOperation) {
        FastArrayList fastArrayList = this.j;
        if (this.k >= fastArrayList.size) {
            fastArrayList.add(new CanvasDrawCommand());
        }
        CanvasDrawCommand canvasDrawCommand = (CanvasDrawCommand) fastArrayList.a()[this.k];
        canvasDrawCommand.f761a = graphicsOperation;
        this.k++;
        return canvasDrawCommand;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(boolean z) {
        this.l = z;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final boolean c() {
        return this.l;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, float f4, Region.Op op) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.c, Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4), op, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, float f4) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.d, Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4), (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, int i2, int i3, int i4) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.e, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect, Region.Op op) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.f, this.e.a(rect), op, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.g, this.e.a(rect), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, Region.Op op) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.h, this.f.a(rectF), op, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.i, this.f.a(rectF), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Matrix matrix) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.j, this.g.a(matrix), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(int i, int i2, int i3, int i4) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.k, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, float f, float f2, boolean z, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.l, this.f.a(rectF), Float.valueOf(f), Float.valueOf(f2), Boolean.valueOf(z), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, float f, float f2, Paint paint) {
        if (!(paint instanceof UniquePaint)) {
            paint = this.d.a(paint);
        }
        CanvasDrawCommand canvasDrawCommandA = a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.m);
        canvasDrawCommandA.b[0] = GraphicsUtils.a(unitTexture);
        canvasDrawCommandA.c = f;
        canvasDrawCommandA.d = f2;
        canvasDrawCommandA.b[1] = paint;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Matrix matrix, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.n, GraphicsUtils.a(unitTexture), this.g.a(matrix), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.o, GraphicsUtils.a(unitTexture), this.e.a(rect), this.e.a(rect2), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Rect rect, RectF rectF, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.p, GraphicsUtils.a(unitTexture), this.e.a(rect), this.f.a(rectF), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int[] iArr, int i, int i2, float f, float f2, int i3, int i4, boolean z, Paint paint) {
        throw new RuntimeException("Not supported");
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int[] iArr, int i, int i2, int i3, int i4, int i5, int i6, boolean z, Paint paint) {
        throw new RuntimeException("Not supported");
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, int i, int i2, float[] fArr, int i3, int[] iArr, int i4, Paint paint) {
        throw new RuntimeException("Not supported");
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.q, Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, PorterDuff.Mode mode) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.r, Integer.valueOf(i), mode);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.s, Integer.valueOf(i), (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, float f4, Paint paint) {
        if (!(paint instanceof UniquePaint)) {
            paint = this.d.a(paint);
        }
        CanvasDrawCommand canvasDrawCommandA = a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.t);
        canvasDrawCommandA.c = f;
        canvasDrawCommandA.d = f2;
        canvasDrawCommandA.e = f3;
        canvasDrawCommandA.f = f4;
        canvasDrawCommandA.b[0] = paint;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float[] fArr, int i, int i2, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.u, fArr, Integer.valueOf(i), Integer.valueOf(i2), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float[] fArr, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.v, fArr, !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.w, this.f.a(rectF), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.x, !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Path path, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.y, path, !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture, Rect rect) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.z, picture, this.e.a(rect), (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture, RectF rectF) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.A, picture, this.f.a(rectF), (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.B, picture, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.C, Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float[] fArr, int i, int i2, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.D, fArr, Integer.valueOf(i), Integer.valueOf(i2), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float[] fArr, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.E, fArr, !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(char[] cArr, int i, int i2, float[] fArr, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.F, cArr, Integer.valueOf(i), Integer.valueOf(i2), fArr, !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, float[] fArr, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.G, str, fArr, !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, int i2, int i3) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.H, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f, float f2, float f3, float f4, Paint paint) {
        if (!(paint instanceof UniquePaint)) {
            paint = this.d.a(paint);
        }
        com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation graphicsOperation = com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.I;
        Float fValueOf = Float.valueOf(f);
        Float fValueOf2 = Float.valueOf(f2);
        Float fValueOf3 = Float.valueOf(f3);
        Float fValueOf4 = Float.valueOf(f4);
        if (this.k >= this.j.size) {
            this.j.add(new CanvasDrawCommand());
        }
        CanvasDrawCommand canvasDrawCommand = (CanvasDrawCommand) this.j.a()[this.k];
        canvasDrawCommand.f761a = graphicsOperation;
        Object[] objArr = canvasDrawCommand.b;
        objArr[0] = fValueOf;
        objArr[1] = fValueOf2;
        objArr[2] = fValueOf3;
        objArr[3] = fValueOf4;
        objArr[4] = paint;
        this.k++;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect, Paint paint) {
        Rect rectA = this.e.a(rect);
        if (!(paint instanceof UniquePaint)) {
            paint = this.d.a(paint);
        }
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.J, rectA, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(RectF rectF, Paint paint) {
        RectF rectFA = this.f.a(rectF);
        if (!(paint instanceof UniquePaint)) {
            paint = this.d.a(paint);
        }
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.K, rectFA, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, float f, float f2, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.L, this.f.a(rectF), Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(char[] cArr, int i, int i2, float f, float f2, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.M, cArr, Integer.valueOf(i), Integer.valueOf(i2), Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(CharSequence charSequence, int i, int i2, float f, float f2, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.N, charSequence, Integer.valueOf(i), Integer.valueOf(i2), Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, float f, float f2, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.O, str, Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, int i, int i2, float f, float f2, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.P, str, Integer.valueOf(i), Integer.valueOf(i2), Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(char[] cArr, int i, int i2, Path path, float f, float f2, Paint paint) {
        throw new RuntimeException("Not supported");
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, Path path, float f, float f2, Paint paint) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.Q, str, path, Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.d.a(paint) : paint, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Canvas.VertexMode vertexMode, int i, float[] fArr, int i2, float[] fArr2, int i3, int[] iArr, int i4, short[] sArr, int i5, int i6, Paint paint) {
        throw new RuntimeException("Not used");
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a_() {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.R);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(int i) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.S, Integer.valueOf(i), (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.T).c = f;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3) {
        CanvasDrawCommand canvasDrawCommandA = a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.U);
        canvasDrawCommandA.c = f;
        canvasDrawCommandA.d = f2;
        canvasDrawCommandA.e = f3;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b() {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.V);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2) {
        CanvasDrawCommand canvasDrawCommandA = a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.aa);
        canvasDrawCommandA.c = f;
        canvasDrawCommandA.d = f2;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f, float f2, float f3, float f4) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.ab, this.h.a(f), this.h.a(f2), this.h.a(f3), this.h.a(f4));
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.ac, unitTexture.b(), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void c(int i) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.ad, Integer.valueOf(i), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(DrawFilter drawFilter) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.ae, drawFilter, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(Matrix matrix) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.af, this.g.a(matrix), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f, float f2) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.ag, Float.valueOf(f), Float.valueOf(f2), (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void c(float f, float f2) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.ah, Float.valueOf(f), Float.valueOf(f2));
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(GraphicsOperation graphicsOperation) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.ai, this, graphicsOperation);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Bitmap bitmap) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Lock lock) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.ak, this, lock);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(Lock lock) {
        a(com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation.al, this, lock);
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
