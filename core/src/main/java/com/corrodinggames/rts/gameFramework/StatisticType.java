package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.gameFramework.stats.StatType;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bj */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bj.class */
public enum StatisticType {
    income(StatType.income),
    armyValue(StatType.armyValue),
    buildingValue(StatType.buildingValue),
    totalValue(StatType.totalValue);

    /* JADX INFO: renamed from: e */
    final StatType statType;

    StatisticType(StatType statType) {
        this.statType = statType;
    }

    public StatType a() {
        return this.statType;
    }
}
