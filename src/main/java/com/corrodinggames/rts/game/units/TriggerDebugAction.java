package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.z */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/z.class */
public class TriggerDebugAction {

    /* JADX INFO: renamed from: a */
    BaseUnit sourceUnit;

    /* JADX INFO: renamed from: b */
    BaseUnit targetUnit;

    /* JADX INFO: renamed from: c */
    boolean isActive;

    /* JADX INFO: renamed from: d */
    AbstractUnitAction action;

    public TriggerDebugAction a() {
        this.sourceUnit = null;
        this.targetUnit = null;
        this.isActive = false;
        this.action = null;
        return this;
    }
}
