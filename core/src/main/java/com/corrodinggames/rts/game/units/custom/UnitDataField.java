package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.ax */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/ax.class */
public abstract class UnitDataField extends CustomUnitDataField {
    public abstract double a(CustomUnit customUnit);

    public abstract void b(CustomUnit customUnit, double d);

    public UnitDataField(int i, String str) {
        super(i, str);
    }

    @Override // com.corrodinggames.rts.game.units.custom.CustomUnitDataField
    public double a(CustomUnit customUnit, UnitStats unitStats) {
        return a(customUnit);
    }

    @Override // com.corrodinggames.rts.game.units.custom.CustomUnitDataField
    public void a(CustomUnit customUnit, double d) {
        customUnit.dJ();
        b(customUnit, d);
    }

    @Override // com.corrodinggames.rts.game.units.custom.CustomUnitDataField
    public boolean b() {
        return true;
    }
}
