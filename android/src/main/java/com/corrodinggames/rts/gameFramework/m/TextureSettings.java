package com.corrodinggames.rts.gameFramework.m;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.fp */
/* JADX INFO: loaded from: classes.dex */
public final class TextureSettings {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public String f767a;
    public boolean c;
    public boolean d;
    public UnitTexture f;
    public boolean g;
    public int b = -1;
    public float[] e = new float[1];

    public final void a(float f) {
        if (this.e.length != 1) {
            this.e = new float[1];
        }
        if (this.e[0] != f) {
            this.e[0] = f;
            this.c = true;
        }
    }

    public final void a(float f, float f2) {
        if (this.e.length != 2) {
            this.e = new float[2];
        }
        if (this.e[0] != f || this.e[1] != f2) {
            this.e[0] = f;
            this.e[1] = f2;
            this.c = true;
        }
    }

    public final void a(float f, float f2, float f3, float f4) {
        if (this.e.length != 4) {
            this.e = new float[4];
        }
        if (this.e[0] != f || this.e[1] != f2 || this.e[2] != f3 || this.e[3] != f4) {
            this.e[0] = f;
            this.e[1] = f2;
            this.e[2] = f3;
            this.e[3] = f4;
            this.c = true;
        }
    }

    public final void a(UnitTexture unitTexture) {
        if (this.f != unitTexture) {
            this.f = unitTexture;
            this.c = true;
        }
    }

    public final void b(UnitTexture unitTexture) {
        this.g = true;
        if (this.f != unitTexture) {
            this.f = unitTexture;
            this.c = true;
        }
    }
}
