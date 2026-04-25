package com.corrodinggames.rts.game.units.custom.hooks;

import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.buildings.RepairBay;
import com.corrodinggames.rts.game.units.custom.CustomUnit;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.b.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/b/b.class */
public class AutoRepairRenderHook extends CustomUnitRenderHook {
    public static final CustomUnitRenderHook a = new AutoRepairRenderHook();

    @Override // com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook
    public void b(CustomUnit customUnit, float f) {
        customUnit.u += f;
        if (customUnit.u > 40.0f && customUnit.hasNoCurrentWaypoint()) {
            customUnit.u = 0.0f;
            RepairBay.a((OrderableUnit) customUnit, f, 0.0f, false);
        }
    }
}
