package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ak */
/* JADX INFO: loaded from: classes.dex */
public final class TextureManager {
    public static Bitmap c = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);

    /* JADX INFO: renamed from: a */
    public GraphicsRenderer f560a;
    public BackingTexture b;
    public boolean g;
    public int h;
    HashMap d = new HashMap();
    public ArrayList e = new ArrayList();
    int f = 0;
    public boolean i = false;
    int j = 0;
    int k = 0;
    int l = 0;
    int m = 1;

    public TextureManager(GraphicsRenderer graphicsRenderer) {
        this.f560a = graphicsRenderer;
        this.b = new BackingTexture(graphicsRenderer);
    }

    public final void a() {
        if (this.i) {
            this.i = false;
            this.f = 0;
            this.g = false;
            this.h = 0;
            this.j = 0;
            this.k = 0;
            this.l = 0;
            this.d.clear();
            this.f560a.g();
            this.b.d(this.f560a);
            Iterator it = this.e.iterator();
            while (it.hasNext()) {
                a((Bitmap) it.next());
            }
            this.e.clear();
        }
    }

    public final ImageBase a(Bitmap bitmap) {
        SubTexture subTexture = (SubTexture) this.d.get(bitmap);
        if (subTexture != null) {
            if (this.i) {
                this.e.add(bitmap);
                return subTexture;
            }
            return subTexture;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int iB = this.b.b();
        int iC = this.b.c();
        if (this.j + width > iB) {
            this.j = 0;
            this.k += this.l + this.m;
            this.l = 0;
        }
        if (this.k + height > iC) {
            if (!this.g) {
                this.g = true;
            }
            return null;
        }
        SubTexture subTexture2 = new SubTexture();
        subTexture2.f565a = this.b.f565a;
        subTexture2.m = this.b;
        int i = this.j;
        int i2 = this.k;
        this.j += this.m + width;
        if (this.l < height) {
            this.l = height;
        }
        this.b.a(this.f560a, bitmap, i, i2);
        subTexture2.p = i;
        subTexture2.q = i2;
        subTexture2.n = ((float) i) / this.b.e;
        subTexture2.o = ((float) i2) / this.b.f;
        subTexture2.e = this.b.e;
        subTexture2.f = this.b.f;
        subTexture2.g = this.b.g;
        subTexture2.h = this.b.h;
        subTexture2.c = width;
        subTexture2.d = height;
        this.f++;
        this.d.put(bitmap, subTexture2);
        return subTexture2;
    }
}
