package com.corrodinggames.rts.gameFramework;

import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bm */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bm.class */
public class TeamStatistics extends StatisticsData {
    public TeamStatistics(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            StatisticsData statisticsData = (StatisticsData) it.next();
            this.a += statisticsData.a;
            this.b += statisticsData.b;
            this.killedUnits += statisticsData.killedUnits;
            this.killedBuildings += statisticsData.killedBuildings;
            this.killedExperimental += statisticsData.killedExperimental;
            this.lostUnits += statisticsData.lostUnits;
            this.lostBuildings += statisticsData.lostBuildings;
            this.lostExperimental += statisticsData.lostExperimental;
            this.i += statisticsData.i;
            this.j = Math.max(this.j, statisticsData.j);
            this.k += statisticsData.k;
            this.teamHistory.a(statisticsData.teamHistory);
        }
    }
}
