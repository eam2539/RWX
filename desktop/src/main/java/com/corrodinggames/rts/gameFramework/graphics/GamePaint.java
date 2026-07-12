package com.corrodinggames.rts.gameFramework.graphics;

import android.graphics.Paint;
import android.graphics.Typeface;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.ag */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/ag.class */
public class GamePaint extends Paint {
    public static final GamePaint r = new GamePaint();
    ShaderProgram t;
    boolean s = false;
    boolean u = false;

    static {
        r.b(-1);
        r.o();
    }

    public void o() {
        this.u = true;
    }

    public void c(float f) {
        super.b(f);
    }

    @Override // android.graphics.Paint
    public void b(float f) {
        if (this.u) {
            GameEngine.logColored("UniquePaint changed when locked down:");
            GameEngine.logColored("from:" + k() + " to: " + f);
            GameEngine.printStackTrace();
        }
        super.b(f);
    }

    @Override // android.graphics.Paint
    public Typeface a(Typeface typeface) {
        if (this.u) {
            GameEngine.logColored("UniquePaint changed when locked down:");
            GameEngine.printStackTrace();
        }
        return super.a(typeface);
    }

    public static void b(Paint paint) {
        ((GamePaint) paint).o();
    }

    public boolean p() {
        return this.s;
    }

    @Override // android.graphics.Paint
    public void a(boolean z) {
        this.s = z;
        super.a(z);
    }

    public ShaderProgram q() {
        return this.t;
    }

    public void a(ShaderProgram shaderProgram) {
        this.t = shaderProgram;
    }
}
