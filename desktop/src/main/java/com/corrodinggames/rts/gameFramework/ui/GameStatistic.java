package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.StatisticsData;
import com.corrodinggames.rts.gameFramework.Utility;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/e.class */
public class GameStatistic {

    /* JADX INFO: renamed from: a */
    public String name;

    /* JADX INFO: renamed from: b */
    public String stringValue;

    /* JADX INFO: renamed from: c */
    public float floatValue;

    /* JADX INFO: renamed from: d */
    public float field_d;

    public GameStatistic(String str, String str2) {
        this.name = str;
        this.stringValue = str2;
    }

    public GameStatistic(String str, float f) {
        this.name = str;
        this.floatValue = f;
        this.stringValue = null;
    }

    /* JADX INFO: renamed from: a */
    public static ArrayList getGameStatistics() {
        GameEngine gameEngine = GameEngine.getInstance();
        ArrayList arrayList = new ArrayList();
        StatisticsData statisticsDataA = null;
        if (gameEngine.playerTeam != null) {
            statisticsDataA = gameEngine.gameStatistics.a(gameEngine.playerTeam);
        }
        if (statisticsDataA != null) {
            if (gameEngine.missionEngine != null && gameEngine.missionEngine.k) {
                arrayList.add(new GameStatistic("Lasted till wave: " + gameEngine.missionEngine.r, VariableScope.nullOrMissingString));
                if (!gameEngine.missionEngine.l) {
                    arrayList.add(new GameStatistic("Wave difficulty: " + gameEngine.networkEngine.c(gameEngine.missionEngine.y), VariableScope.nullOrMissingString));
                }
            }
            arrayList.add(new GameStatistic("Game Time", Utility.copyStream(gameEngine.lastTick / 1000)));
            arrayList.add(new GameStatistic("=============================", VariableScope.nullOrMissingString));
            arrayList.add(new GameStatistic("Units Killed", statisticsDataA.c));
            arrayList.add(new GameStatistic("Buildings Killed", statisticsDataA.d));
            arrayList.add(new GameStatistic("Experimentals Killed", statisticsDataA.e));
            arrayList.add(new GameStatistic("=============================", VariableScope.nullOrMissingString));
            arrayList.add(new GameStatistic("Units Lost", statisticsDataA.f));
            arrayList.add(new GameStatistic("Buildings Lost", statisticsDataA.g));
            arrayList.add(new GameStatistic("Experimentals Lost", statisticsDataA.h));
            arrayList.add(new GameStatistic("=============================", VariableScope.nullOrMissingString));
        }
        return arrayList;
    }
}
