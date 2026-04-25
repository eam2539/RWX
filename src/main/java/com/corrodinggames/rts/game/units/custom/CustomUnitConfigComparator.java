package com.corrodinggames.rts.game.units.custom;

import java.util.Comparator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.q */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/q.class */
class CustomUnitConfigComparator implements Comparator<CustomUnitConfig> {
    @Override // java.util.Comparator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compare(CustomUnitConfig customUnitConfig, CustomUnitConfig customUnitConfig2) {
        if (customUnitConfig.onNewMapSpawn == null || customUnitConfig2.onNewMapSpawn == null) {
            return 0;
        }
        return customUnitConfig.onNewMapSpawn.compareTo(customUnitConfig2.onNewMapSpawn);
    }
}
