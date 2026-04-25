package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ac */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/ac.class */
public class TextureAtlas {
    public IGraphicsEngine a;
    public BackingTexture b;
    public static Bitmap c = Bitmap.a(64, 64, Bitmap.Config.ARGB_8888);
    boolean g;
    HashMap d = new HashMap();
    ArrayList e = new ArrayList();
    int f = 0;
    boolean h = false;
    int i = 0;
    int j = 0;
    int k = 0;
    int l = 1;

    public TextureAtlas(IGraphicsEngine iGraphicsEngine, int i, int i2) {
        this.a = iGraphicsEngine;
        this.b = new BackingTexture(iGraphicsEngine, i, i2);
    }

    public Texture a(Bitmap bitmap) {
        SubTexture subTexture = (SubTexture) this.d.get(bitmap);
        if (subTexture != null) {
            if (this.h) {
                this.e.add(bitmap);
            }
            return subTexture;
        }
        int iB = bitmap.b();
        int iC = bitmap.c();
        int iB2 = this.b.b();
        int iC2 = this.b.c();
        if (this.i + iB > iB2) {
            this.i = 0;
            this.j += this.k + this.l;
            this.k = 0;
        }
        if (this.j + iC > iC2) {
            if (!this.g) {
                this.g = true;
                return null;
            }
            return null;
        }
        SubTexture subTexture2 = new SubTexture();
        subTexture2.a = this.b.a;
        subTexture2.l = this.b;
        int i = this.i;
        int i2 = this.j;
        this.i += iB + this.l;
        if (this.k < iC) {
            this.k = iC;
        }
        this.b.a(this.a, bitmap, i, i2);
        subTexture2.o = i;
        subTexture2.p = i2;
        subTexture2.m = i / this.b.e;
        subTexture2.n = i2 / this.b.f;
        subTexture2.e = this.b.e;
        subTexture2.f = this.b.f;
        subTexture2.g = this.b.g;
        subTexture2.h = this.b.h;
        subTexture2.c = iB;
        subTexture2.d = iC;
        this.f++;
        this.d.put(bitmap, subTexture2);
        return subTexture2;
    }

    public void b(Bitmap bitmap) {
        if (((SubTexture) this.d.get(bitmap)) != null) {
            this.d.remove(bitmap);
        }
    }
}
