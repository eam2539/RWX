package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.*;
import android.util.Log;
import com.corrodinggames.rts.gameFramework.m.GraphicsUtils;
import com.corrodinggames.rts.gameFramework.m.NullGraphicsContext;
import com.corrodinggames.rts.gameFramework.m.UniquePaint;
import com.corrodinggames.rts.gameFramework.m.UnitTexture;

import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.i */
/* JADX INFO: loaded from: classes.dex */
public final class OpenGLGraphicsRenderer implements GraphicsInterface {
    private static int debugPointCallCount;
    public static boolean g;
    static UnitTexture p;
    public OpenGLRenderer b;
    UnitTexture u;
    public static boolean f = false;
    static Rect q = new Rect(0, 0, 1, 1);
    static Paint r = new Paint();
    static Rect t = new Rect();
    public Object c = new Object();
    DrawStyle d = new DrawStyle();
    public int e = 0;
    Rect h = new Rect();
    RectF i = new RectF();
    DefaultGraphicsOption j = new DefaultGraphicsOption();
    OpenGLGraphicsBase k = new OpenGLGraphicsBase(this);
    GraphicsTag l = new CanvasGraphicsTag(this);
    RectF m = new RectF();
    RectF n = new RectF();
    float[] o = new float[4];
    Paint s = new Paint();
    boolean v = false;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public GraphicsEngine f771a = null;

    public OpenGLGraphicsRenderer() {
        GraphicsEngine graphicsEngine = null;
        if (this.f771a != null) {
            this.b = (OpenGLRenderer) graphicsEngine.b();
        }
    }

    public static void d() {
        g = true;
    }

    private DrawStyle b(Paint paint) {
        this.b.a((C0009fo) null);
        applyRwxBlendMode(paint);
        if (paint == null) {
            return null;
        }
        this.d.c = paint.getStyle();
        this.d.b = paint.getColor();
        this.d.f555a = paint.getStrokeWidth();
        return this.d;
    }

    private void applyRwxBlendMode(Paint paint) {
        int blendMode = RwxBlendPaint.BLEND_NORMAL;
        if (paint instanceof RwxBlendPaint) {
            blendMode = ((RwxBlendPaint) paint).rwxBlendMode;
        }
        this.b.aRwxBlendMode(blendMode);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f2, float f3, float f4, float f5, Region.Op op) {
        this.f771a.a((int) f2, (int) f3, (int) f4, (int) f5);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f2, float f3, float f4, float f5) {
        this.f771a.a((int) f2, (int) f3, (int) f4, (int) f5);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, int i2, int i3, int i4) {
        this.f771a.a(i, i2, i3, i4);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect, Region.Op op) {
        this.f771a.a(rect.left, rect.top, rect.right, rect.bottom);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect) {
        this.f771a.a(rect.left, rect.top, rect.right, rect.bottom);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, Region.Op op) {
        this.f771a.a((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF) {
        this.f771a.a((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Matrix matrix) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(int i, int i2, int i3, int i4) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, float f2, float f3, boolean z, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, float f2, float f3, Paint paint) {
        this.h.set(0, 0, unitTexture.width(), unitTexture.height());
        this.i.set(f2, f3, unitTexture.width() + f2, unitTexture.height() + f3);
        b(unitTexture, this.h, this.i, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint) {
        this.i.set(rect2);
        b(unitTexture, rect, this.i, paint);
    }

    private ImageBase a(Bitmap bitmap, UnitTexture unitTexture) {
        ImageBase imageBaseA;
        OpenGLRenderer openGLRenderer = this.b;
        if (openGLRenderer.f572a == null) {
            openGLRenderer.f572a = new TextureManager(openGLRenderer);
        }
        if (bitmap.getWidth() >= 450 || bitmap.getHeight() >= 100 || (imageBaseA = openGLRenderer.f572a.a(bitmap)) == null) {
            return this.f771a.a(bitmap, unitTexture, this.j);
        }
        return imageBaseA;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Rect rect, RectF rectF, Paint paint) {
        b(unitTexture, rect, rectF, paint);
    }

    private void b(UnitTexture unitTexture, Rect rect, RectF rectF, Paint paint) {
        Bitmap bitmapA = GraphicsUtils.a(unitTexture);
        this.m.set(rect);
        if (bitmapA == null) {
            throw new RuntimeException("bitmap==null. sourceImage: " + unitTexture.a());
        }
        ImageBase imageBaseA = a(bitmapA, unitTexture);
        OpenGLRenderer openGLRenderer = this.b;
        boolean zIsAntiAlias = true;
        if (paint == null) {
            openGLRenderer.E = -1;
        } else {
            int color = paint.getColor();
            if (color != -1 && paint.getColorFilter() == null && !(paint instanceof RwxBlendPaint)) {
                color = Color.argb(Color.alpha(color), 255, 255, 255);
            }
            openGLRenderer.E = color;
            if (!(paint instanceof UniquePaint)) {
                zIsAntiAlias = paint.isAntiAlias();
            } else {
                zIsAntiAlias = ((UniquePaint) paint).b;
            }
        }
        openGLRenderer.a(imageBaseA, zIsAntiAlias ? 9729 : 9728);
        C0009fo c0009foN = paint instanceof UniquePaint ? ((UniquePaint) paint).c : null;
        if (paint instanceof RwxBlendPaint && ((RwxBlendPaint) paint).rwxShader != null) {
            c0009foN = ((RwxBlendPaint) paint).rwxShader;
        }
        if (unitTexture != null && c0009foN == null) {
            c0009foN = unitTexture.n();
        }
        if (c0009foN != null) {
            boolean zA = c0009foN.a(paint, unitTexture);
            this.b.a(c0009foN);
            if (zA) {
                this.b.f();
                this.b.i();
            }
        } else {
            this.b.a((C0009fo) null);
        }
        applyRwxBlendMode(paint);
        openGLRenderer.a(imageBaseA, this.m, rectF, this.j);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Bitmap bitmap) {
        this.f771a.a(bitmap);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, Matrix matrix, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int[] iArr, int i, int i2, float f2, float f3, int i3, int i4, boolean z, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int[] iArr, int i, int i2, int i3, int i4, int i5, int i6, boolean z, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture, int i, int i2, float[] fArr, int i3, int[] iArr, int i4, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f2, float f3, float f4, Paint paint) {
        this.f771a.a(f2, f3, f4, b(paint));
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, PorterDuff.Mode mode) {
        OpenGLRenderer.a(d(i));
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i) {
        OpenGLRenderer.a(d(i));
    }

    private float[] d(int i) {
        float f2 = ((i >>> 24) & 255) * 0.003921569f * 1.0f;
        this.o[0] = f2;
        this.o[1] = ((i >>> 16) & 255) * 0.003921569f * f2;
        this.o[2] = ((i >>> 8) & 255) * 0.003921569f * f2;
        this.o[3] = (i & 255) * 0.003921569f * f2;
        return this.o;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f2, float f3, float f4, float f5, Paint paint) {
        this.f771a.a(f2, f3, f4, f5, b(paint));
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float[] fArr, int i, int i2, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float[] fArr, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Path path, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture, Rect rect) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture, RectF rectF) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Picture picture) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float[] fArr, int i, int i2, Paint paint) {
        if (debugPointCallCount < 16) {
            Log.d("RWX_MINIMAP_GL", "drawPoints offset=" + i + " floats=" + i2
                    + " color=" + (paint == null ? "null" : paint.getColor())
                    + " strokeWidth=" + (paint == null ? "null" : paint.getStrokeWidth()));
            debugPointCallCount++;
        }
        this.f771a.a(fArr, i, i2, b(paint));
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float[] fArr, Paint paint) {
        this.f771a.a(fArr, 0, fArr.length, b(paint));
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    @Deprecated
    public final void a(char[] cArr, int i, int i2, float[] fArr, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    @Deprecated
    public final void a(String str, float[] fArr, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(int i, int i2, int i3) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f2, float f3, float f4, float f5, Paint paint) {
        if (p == null) {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            bitmapCreateBitmap.setPixel(0, 0, -1);
            UnitTexture unitTexture = new UnitTexture();
            unitTexture.a(bitmapCreateBitmap);
            p = unitTexture;
            r.setAntiAlias(false);
            r.setColorFilter(new LightingColorFilter(-1, -16777216));
        }
        r.setColor(paint.getColor());
        if (paint.getStyle() == Paint.Style.STROKE) {
            float strokeWidth = paint.getStrokeWidth();
            if (strokeWidth == 0.0f) {
                strokeWidth = 1.0f;
            }
            this.i.set(f2, f3, f4, f3 + strokeWidth);
            b(p, q, this.i, r);
            this.i.set(f2, f5, f4, f5 + strokeWidth);
            b(p, q, this.i, r);
            this.i.set(f2, f3, f2 + strokeWidth, f5);
            b(p, q, this.i, r);
            this.i.set(f4, f3, strokeWidth + f4, f5);
            b(p, q, this.i, r);
            return;
        }
        this.i.set(f2, f3, f4, f5);
        b(p, q, this.i, r);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Rect rect, Paint paint) {
        b(rect.left, rect.top, rect.right, rect.bottom, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(RectF rectF, Paint paint) {
        b(rectF.left, rectF.top, rectF.right, rectF.bottom, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(RectF rectF, float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(char[] cArr, int i, int i2, float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(CharSequence charSequence, int i, int i2, float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, float f2, float f3, Paint paint) {
        this.b.b((C0009fo) null);
        applyRwxBlendMode(paint);
        OpenGLRenderer.O = this;
        this.f771a.a(str, f2, f3, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, int i, int i2, float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(char[] cArr, int i, int i2, Path path, float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(String str, Path path, float f2, float f3, Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Canvas.VertexMode vertexMode, int i, float[] fArr, int i2, float[] fArr2, int i3, int[] iArr, int i4, short[] sArr, int i5, int i6, Paint paint) {
    }

    public final boolean equals(Object obj) {
        return this.f771a.equals(obj);
    }

    public final int hashCode() {
        return this.f771a.hashCode();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a_() {
        this.b.d();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(int i) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f2, float f3, float f4) {
        OpenGLRenderer openGLRenderer = this.b;
        openGLRenderer.a(f3, f4);
        openGLRenderer.a(f2);
        openGLRenderer.a(-f3, -f4);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f2) {
        this.b.a(f2);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b() {
        this.b.c();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f2, float f3, float f4, float f5) {
        OpenGLRenderer openGLRenderer = this.b;
        openGLRenderer.a(f4, f5);
        openGLRenderer.b(f2, f3);
        openGLRenderer.a(-f4, -f5);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(float f2, float f3) {
        this.b.b(f2, f3);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(UnitTexture unitTexture) {
        if (this.u != null) {
            this.f771a.a();
        }
        if (unitTexture != null) {
            this.f771a.a(this.f771a.a(unitTexture.b(), unitTexture, this.j));
        }
        this.u = unitTexture;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void c(int i) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(DrawFilter drawFilter) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(Matrix matrix) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(float f2, float f3) {
    }

    public final String toString() {
        return this.f771a.toString();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void c(float f2, float f3) {
        this.b.a(f2, f3);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(boolean z) {
        this.v = z;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final boolean c() {
        return this.v;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(GraphicsOperation graphicsOperation) {
        graphicsOperation.a(new NullGraphicsContext());
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void a(Lock lock) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final void b(Lock lock) {
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final boolean a(C0009fo c0009fo) {
        return this.b.d(c0009fo);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface
    public final boolean b(C0009fo c0009fo) {
        this.b.b(c0009fo);
        return true;
    }
}
