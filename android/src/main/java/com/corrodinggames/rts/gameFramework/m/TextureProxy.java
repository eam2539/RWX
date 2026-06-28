package com.corrodinggames.rts.gameFramework.m;

import android.graphics.Bitmap;
import com.corrodinggames.rts.gameFramework.android.graphics.C0009fo;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.fn */
/* JADX INFO: loaded from: classes.dex */
public final class TextureProxy extends UnitTexture {
    UnitTexture x;

    public TextureProxy(UnitTexture unitTexture) {
        this.x = unitTexture;
        this.k = unitTexture.k;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final String a() {
        return this.x.a();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final Bitmap b() {
        return this.x.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void a(boolean z) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void a(Bitmap bitmap) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void c() {
        this.x.c();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void a(UnitTexture unitTexture) {
        this.x.a(unitTexture);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    /* JADX INFO: renamed from: d */
    public final UnitTexture clone() {
        return this;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final UnitTexture a(int i, int i2) {
        return this;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void e() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void f() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final int b(int i, int i2) {
        return this.x.b(i, i2);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void a(int i, int i2, int i3) {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    /* JADX INFO: renamed from: g */
    public final int height() {
        return this.x.height();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    /* JADX INFO: renamed from: h */
    public final int width() {
        return this.x.width();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void i() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void j() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void k() {
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final int l() {
        return this.x.l();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void m() {
    }

    public final String toString() {
        return "MutableBitmapOrTexture(" + this.x.toString() + ")";
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final C0009fo n() {
        return this.x.i;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void a(C0009fo c0009fo) {
    }
}
