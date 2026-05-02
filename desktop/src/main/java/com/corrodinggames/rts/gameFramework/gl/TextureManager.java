package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.Bitmap;
import android.graphics.Paint;

import java.util.Iterator;
import java.util.Map;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/f.class */
public class TextureManager {
    protected  IGraphicsEngine a;
    private Map<Bitmap,Texture> b;
    private ShaderProgram c;
    private CircleShader d;
    private ITextureFilter e;

    public void a(Texture texture) {
        this.a.c(texture);
    }

    public void a() {
        this.a.d();
    }

    public IGraphicsEngine b() {
        return this.a;
    }

    public Texture a(Bitmap bitmap, com.corrodinggames.rts.gameFramework.graphics.Texture texture, ITextureFilter iTextureFilter) {
        this.e = iTextureFilter;
        Texture textureA = a(bitmap, texture);
        if (iTextureFilter instanceof FilterGroup) {
            textureA = ((FilterGroup) iTextureFilter).a(textureA, this.a, new FilterCallback() { // from class: com.corrodinggames.rts.gameFramework.b.f.1
                @Override // com.corrodinggames.rts.gameFramework.gl.FilterCallback
                public void a(Texture texture2, ITextureFilter iTextureFilter2, boolean z) {
                    TextureManager.this.a.a(texture2, 0, 0, texture2.b(), texture2.c(), iTextureFilter2, null);
                }
            });
        }
        return textureA;
    }

    public void a(Bitmap bitmap) {
        Texture texture = (Texture) this.b.get(bitmap);
        if (texture != null && (texture instanceof DynamicTexture)) {
            ((DynamicTexture) texture).l();
        }
        b().a(bitmap);
    }

    public Texture a(Bitmap bitmap, com.corrodinggames.rts.gameFramework.graphics.Texture texture) {
        Texture bitmapTexture = this.b.get(bitmap);
        if (bitmapTexture == null) {
            this.a.e();
            c();
            bitmapTexture = new BitmapTexture(bitmap);
            bitmapTexture.c(b());
            bitmapTexture.j = texture.d();
            OpenGLRenderer.b(bitmapTexture.e, bitmapTexture.f);
            this.b.put(bitmap, bitmapTexture);
            d();
        }
        return bitmapTexture;
    }

    public void a(float f, float f2, float f3, PaintStyle paintStyle) {
        if (paintStyle.c() == Paint.Style.FILL) {
            this.d.a(0.5f);
        } else {
            float fB = paintStyle.b();
            if (fB == 0.0f) {
                fB = 1.0f;
            }
            this.d.a(fB / (2.0f * f3));
        }
        this.a.a(f - f3, f2 - f3, f3, paintStyle, this.d);
    }

    public void a(float f, float f2, float f3, float f4, PaintStyle paintStyle) {
        this.a.a(f, f2, f3, f4, paintStyle, this.c);
    }

    public void c() {
        this.a.b();
    }

    public void d() {
        this.a.c();
    }

    public void e() {
        Iterator it = this.b.values().iterator();
        while (it.hasNext()) {
            ((Texture) it.next()).j();
        }
        this.b.clear();
    }

    protected void finalize() throws Throwable {
        super.finalize();
        e();
    }

    public void a(int i, int i2, int i3, int i4) {
        this.a.a(i, i2, i3, i4);
    }

    public void a(String str, float f, float f2, Paint paint) {
        this.a.a(str, f, f2, paint);
    }

    public void a(float[] fArr, int i, int i2, PaintStyle paintStyle) {
        this.a.a(fArr, i, i2, paintStyle, this.c);
    }
}
