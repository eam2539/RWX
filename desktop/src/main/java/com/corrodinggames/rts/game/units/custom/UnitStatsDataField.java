package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.aw */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/aw.class */
public abstract class UnitStatsDataField extends CustomUnitDataField {
    public abstract double a(UnitStats unitStats);

    public abstract void a(UnitStats unitStats, double d);

    public UnitStatsDataField(int i, String str) {
        super(i, str);
    }

    @Override // com.corrodinggames.rts.game.units.custom.CustomUnitDataField
    public double a(CustomUnit customUnit, UnitStats unitStats) {
        return a(unitStats);
    }

    @Override // com.corrodinggames.rts.game.units.custom.CustomUnitDataField
    public void a(CustomUnit customUnit, double d) {
        customUnit.dJ();
        a(customUnit.y, d);
    }

    @Override // com.corrodinggames.rts.game.units.custom.CustomUnitDataField
    public boolean b() {
        return false;
    }
}
