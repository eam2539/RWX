package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.gameFramework.local.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ar */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ar.class */
class UnitCreatedLogEntry extends WarLogEntry {
    UnitType a;
    int b;

    public UnitCreatedLogEntry(float f, float f2, UnitType unitType) {
        super(f, f2);
        this.a = unitType;
        this.b = 1;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.WarLogEntry
    public boolean a(WarLogEntry warLogEntry) {
        return super.a(warLogEntry) && (warLogEntry instanceof UnitCreatedLogEntry) && ((UnitCreatedLogEntry) warLogEntry).a == this.a;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.WarLogEntry
    public void b(WarLogEntry warLogEntry) {
        this.c = warLogEntry.c;
        this.b++;
        this.g = null;
        this.h = false;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.WarLogEntry
    public String a() {
        if (this.g == null) {
            String str = "gui.log.unitCreated";
            if (this.a.isBuildingUnit()) {
                str = "gui.log.buildingConstructed";
            }
            this.g = String.format(Locale.get(str, new Object[0]), this.a.getUnitName(), Integer.valueOf(this.b));
        }
        return this.g;
    }
}
