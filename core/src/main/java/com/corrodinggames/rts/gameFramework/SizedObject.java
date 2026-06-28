package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ay */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ay.class */
public abstract class SizedObject extends PositionedObject {
    public int es;
    public int et;
    public float eu;
    public float ev;
    public boolean ew;

    public void b(Texture texture) {
        T(texture.p);
        U(texture.q);
        this.ew = true;
    }

    public void a(Texture texture, int i) {
        T(texture.p / i);
        U(texture.q);
        this.ew = false;
    }

    public void T(int i) {
        this.es = i;
        this.eu = i / 2;
    }

    public void U(int i) {
        this.et = i;
        this.ev = i / 2;
    }

    public void V(int i) {
        this.es = i;
        this.eu = i / 2.0f;
    }

    public void W(int i) {
        this.et = i;
        this.ev = i / 2.0f;
    }

    protected SizedObject(boolean z) {
        super(z);
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void remove() {
        super.remove();
    }
}
