package com.corrodinggames.rts.gameFramework.m;

import android.graphics.Bitmap;
import com.corrodinggames.rts.game.teamColorsHueType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.android.graphics.C0009fo;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.e */
/* JADX INFO: loaded from: classes.dex */
public class UnitTexture {
    private static int x;

    /* JADX INFO: renamed from: a */
    public UnitTexture[] f758a;
    public UnitTexture[] b;
    public UnitTexture[] c;
    public int d;
    public int e;
    public int f;
    public String g;
    public Integer h;
    C0009fo i;
    public int[] j;
    protected Bitmap k;
    public boolean m;
    public boolean n;

    /* JADX INFO: renamed from: p */
    public int width;

    /* JADX INFO: renamed from: q */
    public int height;
    public int r;
    public int s;
    public float t;
    public float u;
    public boolean v;
    public boolean l = true;
    public boolean o = false;
    public boolean w = false;

    public UnitTexture() {
        int i = x;
        x = i + 1;
        this.d = i;
        this.e = 1;
    }

    public final UnitTexture[] a(teamColorsHueType teamcolorshuetype) {
        if (teamcolorshuetype == teamColorsHueType.pureGreen) {
            return this.f758a;
        }
        if (teamcolorshuetype == teamColorsHueType.hueAdd) {
            return this.b;
        }
        if (teamcolorshuetype == teamColorsHueType.hueShift) {
            return this.c;
        }
        GameEngine.logColored("getTeamImageCache coloringMode:".concat(String.valueOf(teamcolorshuetype)));
        return this.f758a;
    }

    public final void a(teamColorsHueType teamcolorshuetype, UnitTexture[] unitTextureArr) {
        if (teamcolorshuetype == teamColorsHueType.pureGreen) {
            this.f758a = unitTextureArr;
            return;
        }
        if (teamcolorshuetype == teamColorsHueType.hueAdd) {
            this.b = unitTextureArr;
        } else if (teamcolorshuetype == teamColorsHueType.hueShift) {
            this.c = unitTextureArr;
        } else {
            GameEngine.logColored("setTeamImageCache coloringMode:".concat(String.valueOf(teamcolorshuetype)));
            this.f758a = unitTextureArr;
        }
    }

    public String a() {
        return this.g;
    }

    public Bitmap b() {
        return this.k;
    }

    public void a(boolean z) {
        this.o = z;
    }

    public void a(Bitmap bitmap) {
        this.k = bitmap;
        this.width = this.k.getWidth();
        this.height = this.k.getHeight();
        c();
    }

    public void c() {
        this.r = this.width / 2;
        this.s = this.height / 2;
        this.t = this.width / 2.0f;
        this.u = this.height / 2.0f;
    }

    public void a(UnitTexture unitTexture) {
        unitTexture.o = this.o;
        unitTexture.width = this.width;
        unitTexture.height = this.height;
        unitTexture.r = this.r;
        unitTexture.s = this.s;
        unitTexture.t = this.t;
        unitTexture.u = this.u;
    }

    @Override // 
    /* JADX INFO: renamed from: d */
    public UnitTexture clone() {
        UnitTexture unitTexture = new UnitTexture();
        unitTexture.o = this.o;
        if (this.k != null) {
            Bitmap bitmapCopy = this.k.copy(this.k.getConfig(), true);
            if (bitmapCopy == null) {
                throw new OutOfMemoryError("Failed to copy bitmap: " + this.k.getConfig());
            }
            unitTexture.a(bitmapCopy);
        }
        return unitTexture;
    }

    public UnitTexture a(int i, int i2) {
        UnitTexture unitTexture = new UnitTexture();
        unitTexture.o = this.o;
        if (this.k != null) {
            unitTexture.a(Bitmap.createBitmap(i, i2, this.k.getConfig()));
        }
        return unitTexture;
    }

    public void e() {
        if (this.j == null) {
            f();
        }
    }

    public void f() {
        if (this.k != null) {
            if (this.j == null) {
                this.j = new int[this.width * this.height];
            }
            this.k.getPixels(this.j, 0, this.width, 0, 0, this.width, this.height);
        }
    }

    public int b(int i, int i2) {
        return this.j != null ? this.j[(this.width * i2) + i] : this.k.getPixel(i, i2);
    }

    public void a(int i, int i2, int i3) {
        if (this.j != null) {
            this.j[(this.width * i2) + i] = i3;
        } else {
            this.k.setPixel(i, i2, i3);
        }
    }

    /* JADX INFO: renamed from: g */
    public int height() {
        return this.height;
    }

    /* JADX INFO: renamed from: h */
    public int width() {
        return this.width;
    }

    public void i() {
        if (this.k != null) {
            this.k = null;
        }
        if (this.w) {
            GameEngine.logColored("remove with keepInGPUMemory=true");
        }
    }

    public void j() {
        if (this.k != null) {
            if (this.j != null) {
                this.k.setPixels(this.j, 0, this.width, 0, 0, this.width, this.height);
                this.j = null;
            }
            this.e++;
        }
    }

    public void k() {
        this.j = null;
    }

    public int l() {
        return this.width * this.height * 8;
    }

    public void m() {
    }

    public C0009fo n() {
        return this.i;
    }

    public void a(C0009fo c0009fo) {
        this.i = c0009fo;
    }
}
