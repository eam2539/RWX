package com.corrodinggames.rts.game.ai;

import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/a/c.class */
public class BuildPreferenceCache {
    HashMap a = new HashMap();
    HashMap b = new HashMap();
    HashMap c = new HashMap();

    public Integer a(boolean z, UnitType unitType, boolean z2) {
        if (z) {
            return (Integer) this.c.get(unitType);
        }
        if (!z2) {
            return (Integer) this.b.get(unitType);
        }
        return (Integer) this.a.get(unitType);
    }

    public void a(boolean z, UnitType unitType, boolean z2, Integer num) {
        if (z) {
            this.c.put(unitType, num);
        } else if (!z2) {
            this.b.put(unitType, num);
        } else {
            this.a.put(unitType, num);
        }
    }

    public void a() {
        this.a.clear();
        this.b.clear();
    }

    public void a(UnitType unitType) {
        this.a.put(unitType, null);
        this.b.put(unitType, null);
    }

    public void a(OrderableUnit orderableUnit) {
        this.c.put(orderableUnit.unitType, null);
    }

    public void b() {
        this.c.clear();
    }
}
