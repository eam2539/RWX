package com.corrodinggames.rts.java;

import android.graphics.Bitmap;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.ImageData;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/a.class */
public class SlickTextureWrapper extends SlickTexture {
    SlickTexture x;

    public SlickTextureWrapper(SlickTexture slickTexture) {
        this.x = slickTexture;
    }

    @Override // com.corrodinggames.rts.java.SlickTexture
    public Image C() {
        return this.x.C();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public Bitmap b() {
        return this.x.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public Texture c() {
        return this.x.c();
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    public String a() {
        return this.x.a();
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
    public int u() {
        return this.x.u();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void w() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    public Texture a(int i, int i2, boolean z) {
        return this;
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    public int a(int i, int i2) {
        return this.x.a(i, i2);
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    public void p() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    public void r() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    public void n() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    /* JADX INFO: renamed from: h */
    public Texture clone() {
        return this;
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    public void t() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture
    public long c(boolean z) {
        return this.x.c(z);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void a(boolean z) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void a(Bitmap bitmap) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void g() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void a(Texture texture) {
        this.x.a(texture);
    }

    @Override // com.corrodinggames.rts.java.SlickTexture
    public void D() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture
    public void a(Image image, String str) {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture
    public void a(ImageData imageData, String str, boolean z) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void v() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    public void i() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    public void j() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    public void a(int i, int i2, int i3) {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture, com.corrodinggames.rts.gameFramework.graphics.Texture
    public void o() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture
    public void E() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture
    public void F() {
    }

    @Override // com.corrodinggames.rts.java.SlickTexture
    public void G() {
        this.x.G();
    }

    public String toString() {
        return this.x.toString();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public boolean A() {
        return true;
    }
}
