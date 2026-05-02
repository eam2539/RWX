package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.RectF;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ae */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/ae.class */
public class SubTexture extends Texture {
    BackingTexture l;
    public float m;
    public float n;
    public int o;
    public int p;

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    protected boolean c(IGraphicsEngine iGraphicsEngine) {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    protected int g() {
        return 3553;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public void b(int i) {
        this.l.b(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public int h() {
        return this.l.h();
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public void a(RectF rectF) {
        float f = this.g;
        float f2 = this.h;
        rectF.a = (rectF.a * f) + this.m;
        rectF.c = (rectF.c * f) + this.m;
        rectF.b = (rectF.b * f2) + this.n;
        rectF.d = (rectF.d * f2) + this.n;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public void a(RectF rectF, RectF rectF2) {
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.Texture
    public void b(IGraphicsEngine iGraphicsEngine) {
        GameEngine.log("SubTexture prepare TODO");
    }
}
