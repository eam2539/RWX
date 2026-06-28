package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.Bitmap;
import android.graphics.Paint;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.m.UnitTexture;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.h */
/* JADX INFO: loaded from: classes.dex */
public final class GraphicsEngine implements GraphicsContext {
    protected final DefaultGraphicsOption b;
    private float[] d;
    private int f;
    private int g;
    private ColorShader h;
    private GraphicsOption j;
    private Map c = new WeakHashMap();
    private float[] e = new float[16];
    private CircleShader i = new CircleShader();

    /* JADX INFO: renamed from: a */
    protected final GraphicsRenderer f566a = new OpenGLRenderer(this);

    public GraphicsEngine() {
        this.f566a.a(new ShaderLoadCallback(this));
        this.f566a.a(new ShaderCompileCallback(this));
        this.b = new DefaultGraphicsOption();
        this.h = new ColorShader();
        this.d = new float[4];
    }

    public final void a(ImageBase imageBase) {
        this.f566a.c(imageBase);
    }

    public final void a() {
        this.f566a.e();
    }

    public final GraphicsRenderer b() {
        return this.f566a;
    }

    public final ImageBase a(Bitmap bitmap, UnitTexture unitTexture, GraphicsOption graphicsOption) {
        this.j = graphicsOption;
        ImageBase imageBaseA = a(bitmap, unitTexture);
        if (graphicsOption instanceof FilterGroup) {
            return ((FilterGroup) graphicsOption).a(imageBaseA, this.f566a, new ImageLoadCallback(this));
        }
        return imageBaseA;
    }

    public final void a(Bitmap bitmap) {
        ImageBase imageBase = (ImageBase) this.c.get(bitmap);
        if (imageBase != null && (imageBase instanceof AbstractImage)) {
            ((AbstractImage) imageBase).k();
        }
        this.f566a.a(bitmap);
    }

    public final ImageBase a(Bitmap bitmap, UnitTexture unitTexture) {
        ImageBase imageBase = (ImageBase) this.c.get(bitmap);
        if (imageBase == null) {
            this.f566a.f();
            g();
            BitmapImage bitmapImage = new BitmapImage(bitmap);
            bitmapImage.c(this.f566a);
            bitmapImage.k = unitTexture.w;
            OpenGLRenderer.b(bitmapImage.e, bitmapImage.f);
            this.c.put(bitmap, bitmapImage);
            h();
            return bitmapImage;
        }
        return imageBase;
    }

    public final void a(float f, float f2, float f3, float f4, DrawStyle drawStyle) {
        this.f566a.a(f, f2, f3, f4, drawStyle);
    }

    private void g() {
        this.f566a.c();
    }

    private void h() {
        this.f566a.d();
    }

    public final void c() {
        this.f566a.b();
    }

    public final void a(int i, int i2) {
        this.f = i;
        this.g = i2;
        this.f566a.a(i, i2);
    }

    public final void d() {
        Iterator it = this.c.values().iterator();
        while (it.hasNext()) {
            ((ImageBase) it.next()).i();
        }
        this.c.clear();
    }

    public final void e() {
        Iterator it = this.c.values().iterator();
        while (it.hasNext()) {
            ((ImageBase) it.next()).j = 0;
        }
    }

    public final void f() {
        Iterator it = this.c.entrySet().iterator();
        while (it.hasNext()) {
            ImageBase imageBase = (ImageBase) ((Map.Entry) it.next()).getValue();
            if (imageBase.j == 0 && !imageBase.k) {
                GameEngine.log("Removing unused opengl texture");
                imageBase.i();
                it.remove();
            }
        }
    }

    protected final void finalize() throws Throwable {
        super.finalize();
        d();
    }

    public final void a(int i, int i2, int i3, int i4) {
        this.f566a.a(i, i2, i3, i4);
    }

    public final void a(String str, float f, float f2, Paint paint) {
        this.f566a.a(str, f, f2, paint);
    }

    public final void a(float[] fArr, int i, int i2, DrawStyle drawStyle) {
        this.f566a.a(fArr, i, i2, drawStyle, this.h);
    }

    public final void a(float f, float f2, float f3, DrawStyle drawStyle) {
        if (drawStyle.c == Paint.Style.FILL) {
            this.i.f570a = 0.5f;
        } else {
            float f4 = drawStyle.f555a;
            if (f4 == 0.0f) {
                f4 = 1.0f;
            }
            this.i.f570a = f4 / (2.0f * f3);
        }
        this.f566a.a(f - f3, f2 - f3, f3, drawStyle, this.i);
    }
}
