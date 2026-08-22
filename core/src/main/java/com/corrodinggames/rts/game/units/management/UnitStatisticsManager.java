package com.corrodinggames.rts.game.units.management;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.f.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/f/a.class */
public class UnitStatisticsManager {
    public final UnitList a = new UnitList();
    public final UnitList[] b = new UnitList[PlayerTeam.TEAM_ALLIES];
    public final UnitList c = new UnitList();
    public final UnitList d = new UnitList();
    /* JADX INFO: renamed from: e */
    float maxUnitRadius;

    public UnitStatisticsManager() {
        for (int i = 0; i < this.b.length; i++) {
            this.b[i] = new UnitList();
        }
    }

    public void a(BaseUnit baseUnit) {
        this.a.a(baseUnit);
        int i = baseUnit.spatialIndexTeamId;
        if (i >= 0) {
            this.b[i].a(baseUnit);
        } else if (i == -1) {
            this.d.a(baseUnit);
        } else if (i == -2) {
            this.c.a(baseUnit);
        }
        if (baseUnit.radius > this.maxUnitRadius) {
            this.maxUnitRadius = baseUnit.radius;
        }
    }

    public void b(BaseUnit baseUnit) {
        this.a.b(baseUnit);
        int i = baseUnit.spatialIndexTeamId;
        if (i >= 0) {
            this.b[i].b(baseUnit);
        } else if (i == -1) {
            this.d.b(baseUnit);
        } else if (i == -2) {
            this.c.b(baseUnit);
        }
        if (this.a.size == 0) {
            this.maxUnitRadius = 0.0f;
        }
    }
}
