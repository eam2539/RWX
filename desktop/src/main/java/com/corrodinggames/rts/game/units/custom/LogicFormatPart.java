package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.aq */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/aq.class */
public abstract class LogicFormatPart extends LocalePart {

    /* JADX INFO: renamed from: a */
    LogicBoolean logicBoolean;

    LogicFormatPart(LogicBoolean logicBoolean) {
        this.logicBoolean = logicBoolean;
    }

    static LogicFormatPart a(LogicBoolean logicBoolean) {
        LogicBoolean.ReturnType returnType = logicBoolean.getReturnType();
        if (returnType == LogicBoolean.ReturnType.number) {
            return new NumberFormatPart(logicBoolean);
        }
        if (returnType == LogicBoolean.ReturnType.string) {
            return new StringFormatPart(logicBoolean);
        }
        if (returnType == LogicBoolean.ReturnType.unit) {
            return new UnitFormatPart(logicBoolean);
        }
        if (LogicBoolean.ReturnType.isArrayType(returnType)) {
            return new ArrayFormatPart(logicBoolean);
        }
        return new BooleanFormatPart(logicBoolean);
    }
}
