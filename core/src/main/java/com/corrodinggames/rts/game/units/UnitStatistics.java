package com.corrodinggames.rts.game.units;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.aa */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/aa.class */
public class UnitStatistics {
    public float a;
    public float b;
    public float c;

    /* JADX INFO: renamed from: d */
    public int count = 1;

    public float a() {
        return (this.b + this.c) / 60.0f;
    }
}
