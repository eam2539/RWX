package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.gameFramework.StatisticType;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ac */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ac.class */
public enum StatsTab {
    overallStats("A", null),
    incomeChart("B", StatisticType.income),
    armyValueChart("C", StatisticType.armyValue),
    buildingValueChart("D", StatisticType.buildingValue),
    totalValueChart("E", StatisticType.totalValue);

    private final String f;
    private final StatisticType g;

    StatsTab(String str, StatisticType statisticType) {
        this.f = str;
        this.g = statisticType;
    }

    public StatisticType a() {
        return this.g;
    }
}
