package com.corrodinggames.rts.game.ai;

import com.corrodinggames.rts.game.units.UnitType;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.a.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/a/e.class */
public class UnitBuildPriority {

    /* JADX INFO: renamed from: a */
    public UnitType unitType;

    /* JADX INFO: renamed from: b */
    public float priority;

    /* JADX INFO: renamed from: c */
    final /* synthetic */ UnitBuildStrategy strategy;

    public UnitBuildPriority(UnitBuildStrategy unitBuildStrategy, UnitType unitType, float f) {
        this.strategy = unitBuildStrategy;
        this.unitType = unitType;
        this.priority = f;
    }
}
