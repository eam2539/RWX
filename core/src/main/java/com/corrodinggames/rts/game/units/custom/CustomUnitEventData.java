package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/k.class */
public class CustomUnitEventData {

    /* JADX INFO: renamed from: a */
    public CustomEventBinding eventInfo;

    /* JADX INFO: renamed from: b */
    public CustomUnit customUnit;

    /* JADX INFO: renamed from: c */
    public BaseUnit unit;

    /* JADX INFO: renamed from: d */
    public AnimationSet animationSet;

    /* JADX INFO: renamed from: e */
    public VariableScope variableScope;

    public void a() {
        this.eventInfo = null;
        this.customUnit = null;
        this.unit = null;
        this.animationSet = null;
        this.variableScope = null;
    }
}
