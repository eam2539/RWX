package com.corrodinggames.rts.debug.test;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/h.class */
public class TestDataContainer {

    /* JADX INFO: renamed from: a */
    int intValue1;

    /* JADX INFO: renamed from: b */
    int intValue2;

    /* JADX INFO: renamed from: c */
    long longValue1;

    /* JADX INFO: renamed from: d */
    boolean booleanValue;

    /* JADX INFO: renamed from: e */
    long longValue2;

    /* JADX INFO: renamed from: f */
    long longValue3;

    /* JADX INFO: renamed from: g */
    long longValue4;

    /* JADX INFO: renamed from: h */
    long longValue5;

    /* JADX INFO: renamed from: i */
    long longValue6;

    /* JADX INFO: renamed from: j */
    long longValue7;

    public String toString() {
        return (this.intValue1 + this.intValue2) + VariableScope.nullOrMissingString + this.longValue1 + VariableScope.nullOrMissingString + this.booleanValue + this.longValue2 + this.longValue3 + this.longValue4 + this.longValue5 + this.longValue6 + this.longValue7;
    }
}
