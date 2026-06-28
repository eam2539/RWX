package com.corrodinggames.rts.game.units;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.ad */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/ad.class */
final class UnitStateTracker {

    /* JADX INFO: renamed from: a */
    public boolean stateFlag1;

    /* JADX INFO: renamed from: b */
    public boolean stateFlag2;

    /* JADX INFO: renamed from: c */
    public boolean stateFlag3;

    /* JADX INFO: renamed from: d */
    boolean isReset;

    /* JADX INFO: renamed from: e */
    public float stateValue1;

    /* JADX INFO: renamed from: f */
    public float stateValue2;

    UnitStateTracker() {
    }

    public void a() {
        this.stateFlag1 = false;
        this.stateFlag2 = false;
        this.stateFlag3 = false;
        this.stateValue1 = 0.0f;
        this.stateValue2 = 0.0f;
        this.isReset = true;
    }
}
