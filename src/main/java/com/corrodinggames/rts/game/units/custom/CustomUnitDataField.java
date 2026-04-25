package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.at */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/at.class */
public abstract class CustomUnitDataField {
    int a;
    String b;

    public abstract double a(CustomUnit customUnit, UnitStats unitStats);

    public abstract void a(CustomUnit customUnit, double d);

    public abstract boolean b();

    public CustomUnitDataField(int i, String str) {
        this.a = i;
        this.b = str.toLowerCase(Locale.ROOT);
    }

    public LogicBoolean.ReturnType a() {
        return LogicBoolean.ReturnType.number;
    }

    public final void a(CustomUnit customUnit, LogicBoolean logicBoolean, VariableScope.CachedWriter.Operator operator) {
        customUnit.dJ();
        double number = logicBoolean.readNumber(customUnit);
        if (operator == VariableScope.CachedWriter.Operator.set) {
            a(customUnit, number);
            return;
        }
        if (operator == VariableScope.CachedWriter.Operator.add) {
            a(customUnit, a(customUnit, customUnit.y) + number);
        } else if (operator == VariableScope.CachedWriter.Operator.subtract) {
            a(customUnit, a(customUnit, customUnit.y) - number);
        } else {
            GameEngine.reportProblem("setUnitDataFromLogic: unsupported operator");
        }
    }
}
