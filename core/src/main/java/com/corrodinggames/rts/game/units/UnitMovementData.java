package com.corrodinggames.rts.game.units;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.ap */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/ap.class */
public final class UnitMovementData {

    /* JADX INFO: renamed from: a */
    public float targetX;

    /* JADX INFO: renamed from: b */
    public float targetY;

    /* JADX INFO: renamed from: c */
    public float velocityX;

    /* JADX INFO: renamed from: d */
    public float velocityY;

    /* JADX INFO: renamed from: e */
    public float rotation;

    /* JADX INFO: renamed from: f */
    public float speed;

    /* JADX INFO: renamed from: g */
    public boolean isActive;
    public float h;
    public float i;

    /* JADX INFO: renamed from: j */
    public BaseUnit targetUnit;
    public float k;
    public float l;
    public boolean m;

    public void a(float f) {
        this.targetX = f;
        this.targetY = this.targetX;
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.rotation = 0.0f;
        this.speed = 0.0f;
        this.isActive = false;
        this.h = 0.0f;
        this.i = 0.0f;
        this.targetUnit = null;
        this.k = 0.0f;
        this.l = 0.0f;
        this.m = false;
    }

    public final void a(int i) {
        if (this.velocityY < i && this.velocityY >= 0.0f) {
            this.velocityY = i;
        }
    }

    public final void b(int i) {
        if (this.velocityY > (-i)) {
            this.velocityY = -i;
        }
    }

    public final boolean a() {
        return this.velocityY == 0.0f;
    }

    public final boolean b() {
        return this.velocityY < 0.0f;
    }
}
