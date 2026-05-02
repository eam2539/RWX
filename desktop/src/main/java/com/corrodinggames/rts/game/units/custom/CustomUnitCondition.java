package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logic.CustomAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.r */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/r.class */
public class CustomUnitCondition {

    /* JADX INFO: renamed from: a */
    LogicBoolean logicBoolean;

    /* JADX INFO: renamed from: b */
    String conditionName;

    /* JADX INFO: renamed from: c */
    UpdateFrequency triggerType = UpdateFrequency.everyFrame;

    /* JADX INFO: renamed from: d */
    CustomAction action;
}
