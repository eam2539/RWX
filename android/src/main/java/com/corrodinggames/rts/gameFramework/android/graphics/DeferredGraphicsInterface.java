package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.*;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation;
import com.corrodinggames.rts.gameFramework.m.*;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

import java.util.Iterator;
import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.o */
/* JADX INFO: loaded from: classes.dex */
public final class DeferredGraphicsInterface extends AbstractGraphicsRenderer {

    /* JADX INFO: renamed from: a */
    public GraphicsInterface f773a;
    public com.corrodinggames.rts.gameFramework.m.GraphicsContext c;
    int d;
    GraphicsInterface b = new CanvasGraphicsRenderer(null);
    final FastArrayList e = new FastArrayList();
    final DrawObjectPool f = new DrawObjectPool(Paint.class);
    final DrawObjectPool g = new DrawObjectPool(Rect.class);
    final DrawObjectPool h = new DrawObjectPool(RectF.class);
    final DrawObjectPool i = new DrawObjectPool(Matrix.class);
    final DrawObjectPool j = new DrawObjectPool(DrawMarker.class);
    public final DrawCommandList k = new DrawCommandList();
    public int l = 0;
    public volatile boolean m = false;

    public DeferredGraphicsInterface() {
        this.e.add(this.f);
        this.e.add(this.g);
        this.e.add(this.h);
        this.e.add(this.i);
        this.e.add(this.j);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsRenderer
    public final void a(Canvas canvas) {
        throw new RuntimeException("Not supported");
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsRenderer
    public final void a() {
        Iterator it = this.e.iterator();
        while (it.hasNext()) {
            ((DrawObjectPool) it.next()).b = 0;
        }
        this.l = 0;
        this.d = 0;
    }

    private GLDrawCommand a(CanvasDrawOperation canvasDrawOperation, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
        DrawCommandList drawCommandList = this.k;
        int i = this.l;
        if (i >= drawCommandList.f754a) {
            drawCommandList.a(new GLDrawCommand(this));
        }
        GLDrawCommand gLDrawCommand = drawCommandList.b[i];
        gLDrawCommand.f753a = canvasDrawOperation;
        Object[] objArr = gLDrawCommand.b;
        objArr[0] = obj;
        objArr[1] = obj2;
        objArr[2] = obj3;
        objArr[3] = obj4;
        objArr[4] = obj5;
        objArr[5] = obj6;
        objArr[6] = obj7;
        objArr[7] = obj8;
        this.l++;
        return gLDrawCommand;
    }

    private void a(CanvasDrawOperation canvasDrawOperation, Object obj, Object obj2, Object obj3, Object obj4) {
        DrawCommandList drawCommandList = this.k;
        int i = this.l;
        if (i >= drawCommandList.f754a) {
            drawCommandList.a(new GLDrawCommand(this));
        }
        GLDrawCommand gLDrawCommand = drawCommandList.b[i];
        gLDrawCommand.f753a = canvasDrawOperation;
        Object[] objArr = gLDrawCommand.b;
        objArr[0] = obj;
        objArr[1] = obj2;
        objArr[2] = obj3;
        objArr[3] = obj4;
        this.l++;
    }

    private void a(CanvasDrawOperation canvasDrawOperation, Object obj, Object obj2) {
        DrawCommandList drawCommandList = this.k;
        int i = this.l;
        if (i >= drawCommandList.f754a) {
            drawCommandList.a(new GLDrawCommand(this));
        }
        GLDrawCommand gLDrawCommand = drawCommandList.b[i];
        gLDrawCommand.f753a = canvasDrawOperation;
        Object[] objArr = gLDrawCommand.b;
        objArr[0] = obj;
        objArr[1] = obj2;
        this.l++;
    }

    private GLDrawCommand a(CanvasDrawOperation canvasDrawOperation) {
        DrawCommandList drawCommandList = this.k;
        int i = this.l;
        if (i >= drawCommandList.f754a) {
            drawCommandList.a(new GLDrawCommand(this));
        }
        GLDrawCommand gLDrawCommand = drawCommandList.b[i];
        gLDrawCommand.f753a = canvasDrawOperation;
        this.l++;
        return gLDrawCommand;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(boolean z) {
        this.m = z;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final boolean c() {
        return this.m;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, float f4, Region.Op op) {
        a(CanvasDrawOperation.c, Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4), op, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, float f4) {
        a(CanvasDrawOperation.d, Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4), (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, int i2, int i3, int i4) {
        a(CanvasDrawOperation.e, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect, Region.Op op) {
        a(CanvasDrawOperation.f, this.g.a(rect), op, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect) {
        a(CanvasDrawOperation.g, this.g.a(rect), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, Region.Op op) {
        a(CanvasDrawOperation.h, this.h.a(rectF), op, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF) {
        a(CanvasDrawOperation.i, this.h.a(rectF), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Matrix matrix) {
        a(CanvasDrawOperation.j, this.i.a(matrix), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(int i, int i2, int i3, int i4) {
        a(CanvasDrawOperation.k, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, float f, float f2, boolean z, Paint paint) {
        a(CanvasDrawOperation.l, this.h.a(rectF), Float.valueOf(f), Float.valueOf(f2), Boolean.valueOf(z), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, float f, float f2, Paint paint) {
        if (!(paint instanceof UniquePaint)) {
            paint = this.f.a(paint);
        }
        GLDrawCommand gLDrawCommandA = a(CanvasDrawOperation.m);
        gLDrawCommandA.b[0] = unitTexture;
        gLDrawCommandA.b[1] = paint;
        gLDrawCommandA.c = f;
        gLDrawCommandA.d = f2;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Matrix matrix, Paint paint) {
        a(CanvasDrawOperation.n, unitTexture, this.i.a(matrix), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint) {
        a(CanvasDrawOperation.o, unitTexture, this.g.a(rect), this.g.a(rect2), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Rect rect, RectF rectF, Paint paint) {
        a(CanvasDrawOperation.p, unitTexture, this.g.a(rect), this.h.a(rectF), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int[] iArr, int i, int i2, float f, float f2, int i3, int i4, boolean z, Paint paint) {
        GLDrawCommand gLDrawCommandA = a(CanvasDrawOperation.q, iArr, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Boolean.valueOf(z), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null);
        gLDrawCommandA.c = f;
        gLDrawCommandA.d = f2;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int[] iArr, int i, int i2, int i3, int i4, int i5, int i6, boolean z, Paint paint) {
        GLDrawCommand gLDrawCommandA = a(CanvasDrawOperation.r, iArr, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i5), Integer.valueOf(i6), Boolean.valueOf(z), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null);
        gLDrawCommandA.c = i3;
        gLDrawCommandA.d = i4;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, int i, int i2, float[] fArr, int i3, int[] iArr, int i4, Paint paint) {
        a(CanvasDrawOperation.s, unitTexture, Integer.valueOf(i), Integer.valueOf(i2), fArr, Integer.valueOf(i3), iArr, Integer.valueOf(i4), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, Paint paint) {
        a(CanvasDrawOperation.t, Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, PorterDuff.Mode mode) {
        a(CanvasDrawOperation.u, Integer.valueOf(i), mode);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i) {
        a(CanvasDrawOperation.v, Integer.valueOf(i), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3, float f4, Paint paint) {
        if (!(paint instanceof UniquePaint)) {
            paint = this.f.a(paint);
        }
        GLDrawCommand gLDrawCommandA = a(CanvasDrawOperation.w);
        gLDrawCommandA.c = f;
        gLDrawCommandA.d = f2;
        gLDrawCommandA.e = f3;
        gLDrawCommandA.f = f4;
        gLDrawCommandA.b[0] = paint;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float[] fArr, int i, int i2, Paint paint) {
        a(CanvasDrawOperation.x, fArr, Integer.valueOf(i), Integer.valueOf(i2), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float[] fArr, Paint paint) {
        a(CanvasDrawOperation.y, fArr, !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, Paint paint) {
        a(CanvasDrawOperation.z, this.h.a(rectF), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Paint paint) {
        a(CanvasDrawOperation.A, !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Path path, Paint paint) {
        a(CanvasDrawOperation.B, path, !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture, Rect rect) {
        a(CanvasDrawOperation.C, picture, this.g.a(rect), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture, RectF rectF) {
        a(CanvasDrawOperation.D, picture, this.h.a(rectF), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture) {
        a(CanvasDrawOperation.E, picture, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, Paint paint) {
        a(CanvasDrawOperation.F, Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float[] fArr, int i, int i2, Paint paint) {
        a(CanvasDrawOperation.G, fArr, Integer.valueOf(i), Integer.valueOf(i2), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float[] fArr, Paint paint) {
        a(CanvasDrawOperation.H, fArr, !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(char[] cArr, int i, int i2, float[] fArr, Paint paint) {
        a(CanvasDrawOperation.I, cArr, Integer.valueOf(i), Integer.valueOf(i2), fArr, !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, float[] fArr, Paint paint) {
        a(CanvasDrawOperation.J, str, fArr, !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, int i2, int i3) {
        a(CanvasDrawOperation.K, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f, float f2, float f3, float f4, Paint paint) {
        if (!(paint instanceof UniquePaint)) {
            paint = this.f.a(paint);
        }
        CanvasDrawOperation canvasDrawOperation = CanvasDrawOperation.L;
        Float fValueOf = Float.valueOf(f);
        Float fValueOf2 = Float.valueOf(f2);
        Float fValueOf3 = Float.valueOf(f3);
        Float fValueOf4 = Float.valueOf(f4);
        DrawCommandList drawCommandList = this.k;
        int i = this.l;
        if (i >= drawCommandList.f754a) {
            drawCommandList.a(new GLDrawCommand(this));
        }
        GLDrawCommand gLDrawCommand = drawCommandList.b[i];
        gLDrawCommand.f753a = canvasDrawOperation;
        Object[] objArr = gLDrawCommand.b;
        objArr[0] = fValueOf;
        objArr[1] = fValueOf2;
        objArr[2] = fValueOf3;
        objArr[3] = fValueOf4;
        objArr[4] = paint;
        this.l++;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect, Paint paint) {
        Rect rectA = this.g.a(rect);
        if (!(paint instanceof UniquePaint)) {
            paint = this.f.a(paint);
        }
        a(CanvasDrawOperation.M, rectA, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(RectF rectF, Paint paint) {
        RectF rectFA = this.h.a(rectF);
        if (!(paint instanceof UniquePaint)) {
            paint = this.f.a(paint);
        }
        a(CanvasDrawOperation.N, rectFA, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, float f, float f2, Paint paint) {
        a(CanvasDrawOperation.O, this.h.a(rectF), Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(char[] cArr, int i, int i2, float f, float f2, Paint paint) {
        a(CanvasDrawOperation.P, cArr, Integer.valueOf(i), Integer.valueOf(i2), Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(CharSequence charSequence, int i, int i2, float f, float f2, Paint paint) {
        a(CanvasDrawOperation.Q, charSequence, Integer.valueOf(i), Integer.valueOf(i2), Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, float f, float f2, Paint paint) {
        a(CanvasDrawOperation.R, str, Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, int i, int i2, float f, float f2, Paint paint) {
        a(CanvasDrawOperation.S, str, Integer.valueOf(i), Integer.valueOf(i2), Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(char[] cArr, int i, int i2, Path path, float f, float f2, Paint paint) {
        a(CanvasDrawOperation.T, cArr, Integer.valueOf(i), Integer.valueOf(i2), path, Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, Path path, float f, float f2, Paint paint) {
        a(CanvasDrawOperation.U, str, path, Float.valueOf(f), Float.valueOf(f2), !(paint instanceof UniquePaint) ? this.f.a(paint) : paint, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Canvas.VertexMode vertexMode, int i, float[] fArr, int i2, float[] fArr2, int i3, int[] iArr, int i4, short[] sArr, int i5, int i6, Paint paint) {
        throw new RuntimeException("Not used");
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a_() {
        a(CanvasDrawOperation.W);
        this.d--;
        if (this.d < 0) {
            GameEngine.logColored("saveStackSize: " + this.d);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(int i) {
        a(CanvasDrawOperation.X, Integer.valueOf(i), (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f) {
        a(CanvasDrawOperation.Y).c = f;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2, float f3) {
        GLDrawCommand gLDrawCommandA = a(CanvasDrawOperation.Z);
        gLDrawCommandA.c = f;
        gLDrawCommandA.d = f2;
        gLDrawCommandA.e = f3;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b() {
        a(CanvasDrawOperation.aa);
        this.d++;
        if (this.d <= 0) {
            GameEngine.logColored("saveStackSize (on save): " + this.d);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f, float f2) {
        GLDrawCommand gLDrawCommandA = a(CanvasDrawOperation.af);
        gLDrawCommandA.c = f;
        gLDrawCommandA.d = f2;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f, float f2, float f3, float f4) {
        GLDrawCommand gLDrawCommandA = a(CanvasDrawOperation.ag);
        gLDrawCommandA.c = f;
        gLDrawCommandA.d = f2;
        gLDrawCommandA.e = f3;
        gLDrawCommandA.f = f4;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture) {
        a(CanvasDrawOperation.ah, unitTexture, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void c(int i) {
        a(CanvasDrawOperation.ai, Integer.valueOf(i), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(DrawFilter drawFilter) {
        a(CanvasDrawOperation.aj, drawFilter, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(Matrix matrix) {
        a(CanvasDrawOperation.ak, this.i.a(matrix), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f, float f2) {
        a(CanvasDrawOperation.al, Float.valueOf(f), Float.valueOf(f2), (Object) null, (Object) null, (Object) null, (Object) null, (Object) null, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void c(float f, float f2) {
        GLDrawCommand gLDrawCommandA = a(CanvasDrawOperation.am);
        gLDrawCommandA.c = f;
        gLDrawCommandA.d = f2;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(GraphicsOperation graphicsOperation) {
        a(CanvasDrawOperation.an, this, graphicsOperation);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Bitmap bitmap) {
        a(CanvasDrawOperation.ap, bitmap, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Lock lock) {
        a(CanvasDrawOperation.aq, lock, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(Lock lock) {
        a(CanvasDrawOperation.ar, lock, (Object) null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final boolean a(C0009fo c0009fo) {
        a(CanvasDrawOperation.as, c0009fo, (Object) null);
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final boolean b(C0009fo c0009fo) {
        a(CanvasDrawOperation.at, c0009fo, (Object) null);
        return true;
    }
}
