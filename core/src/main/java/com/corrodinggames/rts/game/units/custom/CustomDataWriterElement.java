package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.av */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/av.class */
public class CustomDataWriterElement extends VariableScope.CachedWriter.WriterElement {
    public CustomUnitDataField a;
    public LogicBoolean b;
    public VariableScope.CachedWriter.Operator c;

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.CachedWriter.WriterElement
    public void writeToUnit(OrderableUnit orderableUnit) {
        if (!(orderableUnit instanceof CustomUnit)) {
            GameEngine.reportProblem("Cannot change data on non custom unit:" + BaseUnit.serialize(orderableUnit));
        } else {
            this.a.a((CustomUnit) orderableUnit, this.b, this.c);
        }
    }
}
