package com.corrodinggames.rts.gameFramework.graphics.opengl;

import android.graphics.Bitmap;
import com.corrodinggames.rts.gameFramework.graphics.ShaderProgram;
import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.ad */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/ad.class */
public class TextureWrapper extends Texture {
    Texture x;

    public TextureWrapper(Texture texture) {
        this.x = texture;
        this.k = texture.k;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public String a() {
        return this.x.a();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public Bitmap b() {
        return this.x.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public Texture c() {
        return this.x.c();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void a(boolean z) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void a(Bitmap bitmap) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void g() {
        this.x.g();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void a(Texture texture) {
        this.x.a(texture);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    /* JADX INFO: renamed from: h */
    public Texture clone() {
        return this;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public Texture a(int i, int i2, boolean z) {
        return this;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void i() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void j() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public int a(int i, int i2) {
        return this.x.a(i, i2);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void a(int i, int i2, int i3) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public int l() {
        return this.x.l();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public int m() {
        return this.x.m();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void n() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void o() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void p() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void r() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void t() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public int u() {
        return this.x.u();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void v() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void w() {
    }

    public String toString() {
        return "MutableBitmapOrTexture(" + this.x.toString() + ")";
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public ShaderProgram B() {
        return this.x.i;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void a(ShaderProgram shaderProgram) {
    }
}
