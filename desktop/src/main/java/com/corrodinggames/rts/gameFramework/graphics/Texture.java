package com.corrodinggames.rts.gameFramework.graphics;

import android.graphics.Bitmap;
import com.corrodinggames.rts.game.ColorMode;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/e.class */
public class Texture {
    public Texture[] a;
    public Texture[] b;
    public Texture[] c;
    private static int x;
    public int d;
    public int e;
    public int f;
    public String g;
    public Integer h;
    public ShaderProgram i;
    public int[] j;
    public Bitmap k;
    public boolean m;
    public boolean n;
    public int p;
    public int q;
    public int r;
    public int s;
    public float t;
    public float u;
    public boolean v;
    public boolean l = true;
    public boolean o = false;
    boolean w = false;

    public Texture() {
        int i = x;
        x = i + 1;
        this.d = i;
        this.e = 1;
    }

    public Texture[] a(ColorMode colorMode) {
        if (colorMode == ColorMode.pureGreen) {
            return this.a;
        }
        if (colorMode == ColorMode.hueAdd) {
            return this.b;
        }
        if (colorMode == ColorMode.hueShift) {
            return this.c;
        }
        GameEngine.logColored("getTeamImageCache coloringMode:" + colorMode);
        return this.a;
    }

    public void a(ColorMode colorMode, Texture[] textureArr) {
        if (colorMode == ColorMode.pureGreen) {
            this.a = textureArr;
            return;
        }
        if (colorMode == ColorMode.hueAdd) {
            this.b = textureArr;
        } else if (colorMode == ColorMode.hueShift) {
            this.c = textureArr;
        } else {
            GameEngine.logColored("setTeamImageCache coloringMode:" + colorMode);
            this.a = textureArr;
        }
    }

    public void a(String str) {
        this.g = str;
    }

    public String a() {
        return this.g;
    }

    public Bitmap b() {
        return this.k;
    }

    public Texture c() {
        return this;
    }

    public void a(boolean z) {
        this.o = z;
        e();
    }

    public void b(boolean z) {
        this.w = z;
    }

    public boolean d() {
        return this.w;
    }

    protected void e() {
    }

    public boolean f() {
        return this.m;
    }

    public void a(Bitmap bitmap) {
        this.k = bitmap;
        this.p = this.k.b();
        this.q = this.k.c();
        g();
    }

    public void g() {
        this.r = this.p / 2;
        this.s = this.q / 2;
        this.t = this.p / 2.0f;
        this.u = this.q / 2.0f;
    }

    public void a(Texture texture) {
        texture.o = this.o;
        texture.p = this.p;
        texture.q = this.q;
        texture.r = this.r;
        texture.s = this.s;
        texture.t = this.t;
        texture.u = this.u;
    }

    @Override //
    /* JADX INFO: renamed from: h, reason: merged with bridge method [inline-methods] */
    public Texture clone() {
        Texture texture = new Texture();
        texture.o = this.o;
        if (this.k != null) {
            Bitmap bitmapA = this.k.a(this.k.d(), true);
            if (bitmapA == null) {
                throw new OutOfMemoryError("Failed to copy bitmap: " + this.k.d());
            }
            texture.a(bitmapA);
        }
        return texture;
    }

    public Texture a(int i, int i2, boolean z) {
        Texture texture = new Texture();
        texture.o = this.o;
        if (this.k != null) {
            Bitmap bitmapA = Bitmap.a(i, i2, this.k.d());
            texture.a(bitmapA);
            if (z) {
                for (int i3 = 0; i3 < bitmapA.b(); i3++) {
                    for (int i4 = 0; i4 < bitmapA.c(); i4++) {
                        bitmapA.a(i3, i4, this.k.a(i3, i4));
                    }
                }
            }
        }
        return texture;
    }

    public void i() {
        if (this.j == null) {
            j();
        }
    }

    public void j() {
        if (this.k == null && GameEngine.isNonAndroidVersion && !GameEngine.isJavaDesktopVersion) {
            return;
        }
        if (this.j == null) {
            this.j = new int[this.p * this.q];
        }
        this.k.a(this.j, 0, this.p, 0, 0, this.p, this.q);
    }

    public boolean k() {
        return true;
    }

    public int a(int i, int i2) {
        if (this.j != null) {
            return this.j[i + (i2 * this.p)];
        }
        return this.k.a(i, i2);
    }

    public void a(int i, int i2, int i3) {
        if (this.j != null) {
            this.j[i + (i2 * this.p)] = i3;
        } else {
            this.k.a(i, i2, i3);
        }
    }

    public int l() {
        return this.q;
    }

    public int m() {
        return this.p;
    }

    public void n() {
    }

    public void o() {
        if (this.k != null) {
            this.k = null;
        }
        if (this.w) {
            GameEngine.logColored("remove with keepInGPUMemory=true");
        }
    }

    public void p() {
        if (this.k == null && GameEngine.isNonAndroidVersion && !GameEngine.isPCOrIOSVersion) {
            return;
        }
        if (this.j != null) {
            this.k.b(this.j, 0, this.p, 0, 0, this.p, this.q);
            this.j = null;
        }
        this.e++;
    }

    public void q() {
    }

    public void r() {
        this.j = null;
    }

    public void s() {
        r();
    }

    public void t() {
    }

    public int u() {
        return this.p * this.q * 8;
    }

    public void v() {
        this.a = null;
        this.b = null;
        this.c = null;
        this.e++;
    }

    public void w() {
    }

    public void x() {
    }

    public void y() {
    }

    public void z() {
    }

    public boolean A() {
        return false;
    }

    public ShaderProgram B() {
        return this.i;
    }

    public void a(ShaderProgram shaderProgram) {
        this.i = shaderProgram;
    }
}
