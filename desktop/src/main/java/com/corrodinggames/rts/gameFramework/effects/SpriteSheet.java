package com.corrodinggames.rts.gameFramework.effects;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.d.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/d/g.class */
public final class SpriteSheet {
    public String a;
    public int b = 25;
    public int c = 25;
    public int d = 1;
    public int e = 1;
    public int f = 26;
    public int g = 26;
    public int h = Integer.MAX_VALUE;
    public Texture i = null;
    public Texture j = null;
    public boolean k = false;
    static final Rect l = new Rect();
    static final RectF m = new RectF();

    /* JADX INFO: renamed from: a */
    public void createOutline() {
        this.j = this.i.clone();
        this.j.j();
        for (int i = 0; i < this.j.m(); i++) {
            for (int i2 = 0; i2 < this.j.l(); i2++) {
                this.j.a(i, i2, Color.a(Color.a(this.j.a(i, i2)), 0, 0, 0));
            }
        }
        this.j.p();
        this.j.s();
    }

    /* JADX INFO: renamed from: a */
    public void drawSprite(int i, float f, float f2, Paint paint) {
        Rect rect = l;
        RectF rectF = m;
        GameEngine gameEngine = GameEngine.getInstance();
        int i2 = i;
        int i3 = 0;
        if (i2 >= this.h) {
            i3 = 0 + (i2 / this.h);
            i2 %= this.h;
        }
        int i4 = this.d + (i2 * this.f);
        int i5 = this.e + (i3 * this.g);
        l.a(i4, i5, i4 + this.b, i5 + this.c);
        rectF.a(f, f2, f + rect.b(), f2 + rect.c());
        if (1 != 0) {
            rectF.a((-rectF.b()) / 2.0f, (-rectF.c()) / 2.0f);
        }
        gameEngine.graphicsEngine2.a(this.i, rect, rectF, paint);
    }
}
