package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/p.class */
public class CustomUnitTrigger {

    /* JADX INFO: renamed from: a */
    String triggerName;

    /* JADX INFO: renamed from: b */
    float delay = -999.0f;

    /* JADX INFO: renamed from: c */
    boolean enabled;

    /* JADX INFO: renamed from: d */
    String condition;

    /* JADX INFO: renamed from: e */
    boolean runOnce;

    /* JADX INFO: renamed from: f */
    public LogicBoolean logicCondition;

    /* JADX INFO: renamed from: g */
    public LocaleString action;
}
