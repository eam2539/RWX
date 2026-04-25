package com.corrodinggames.rts.game.units.custom.hooks;

import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.b.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/b/e.class */
public class DecalImageSlice {
    public Texture a;
    public Texture[] b;
    public int c;
    public int d;
    public int e = 1;
    public int f = 1;

    public void a(DecalDefinition decalDefinition) {
        int i = this.a.p;
        int i2 = this.a.q;
        this.c = i;
        this.d = i2;
        if (decalDefinition.K > 0) {
            this.c = decalDefinition.K;
        } else if (decalDefinition.J > 0) {
            this.c = i / decalDefinition.J;
        }
        if (decalDefinition.L > 0) {
            this.d = decalDefinition.L;
        }
        if (this.c > 0) {
            this.f = i / this.c;
        }
        if (this.d > 0) {
            this.e = i2 / this.d;
        }
        if (this.f <= 0) {
            this.f = 1;
        }
        if (this.e <= 0) {
            this.e = 1;
        }
    }
}
