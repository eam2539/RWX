package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bg */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bg.class */
public class GameStatistics {
    StatisticsData b = new StatisticsData();
    StatisticsData[] c = new StatisticsData[PlayerTeam.TEAM_ALLIES];
    int d;
    boolean e;
    public static boolean a = true;
    public static UnitEventManager f = new UnitEventManager();

    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.startBlock("stats");
        gameOutputStream.writeByte(0);
        int i = PlayerTeam.TEAM_NEUTRAL;
        gameOutputStream.writeInt(i);
        for (int i2 = 0; i2 < i; i2++) {
            this.c[i2].a(gameOutputStream);
        }
        gameOutputStream.endBlock("stats");
    }

    public void a(GameInputStream gameInputStream, boolean z) throws IOException {
        gameInputStream.startBlockNamed("stats");
        gameInputStream.readByte();
        int i = gameInputStream.readInt();
        this.c = new StatisticsData[PlayerTeam.TEAM_ALLIES];
        for (int i2 = 0; i2 < i; i2++) {
            this.c[i2] = new StatisticsData();
            this.c[i2].a(gameInputStream);
        }
        gameInputStream.d("stats");
    }

    public void a() {
        this.b = new StatisticsData();
        this.c = new StatisticsData[PlayerTeam.TEAM_ALLIES];
        for (int i = 0; i < this.c.length; i++) {
            this.c[i] = new StatisticsData();
        }
        this.d = 0;
        this.e = a;
    }

    public void b() {
        int i = GameEngine.getInstance().gameTimeMillis;
        if (this.e && this.d <= i) {
            int i2 = 5000;
            if (i < 60000) {
                i2 = 1000;
            }
            if (i > 1800000) {
                i2 = 15000;
            }
            if (i > 3600000) {
                i2 = 30000;
            }
            int i3 = i2 + i2;
            a(i, false, false);
        }
    }

    private void a(int i, boolean z, boolean z2) {
        for (int i2 = 0; i2 < PlayerTeam.TEAM_NEUTRAL; i2++) {
            PlayerTeam playerTeamK = PlayerTeam.k(i2);
            if (playerTeamK != null) {
                TeamHistory teamHistory = this.c[i2].l;
                if (!z || teamHistory.c()) {
                    teamHistory.a(playerTeamK, i, z2);
                    teamHistory.a(i2);
                }
            }
        }
    }

    public void c() {
        this.e = false;
        a(GameEngine.getInstance().gameTimeMillis, true, true);
    }

    /* JADX INFO: renamed from: d */
    public ArrayList getActiveTeamStatistics() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < PlayerTeam.TEAM_NEUTRAL; i++) {
            if (this.c[i].l.c()) {
                arrayList.add(this.c[i]);
            }
        }
        return arrayList;
    }

    public StatisticsData a(BaseUnit baseUnit) {
        return a(baseUnit.team);
    }

    public StatisticsData a(PlayerTeam playerTeam) {
        int i = playerTeam.teamId;
        if (i < 0 || i >= this.c.length) {
            return this.b;
        }
        StatisticsData statisticsData = this.c[i];
        if (statisticsData == null) {
            return this.b;
        }
        return statisticsData;
    }

    public void a(BaseUnit baseUnit, BaseUnit baseUnit2, float f2) {
        if (baseUnit != null) {
            boolean z = baseUnit2.isDestroyed;
            StatisticsData statisticsDataA = a(baseUnit);
            StatisticsData statisticsDataA2 = a(baseUnit2);
            if (z) {
                f.a(baseUnit, baseUnit2);
                if (baseUnit2.bI()) {
                    statisticsDataA.d++;
                    statisticsDataA2.g++;
                } else if (baseUnit2.getUnitAICombatTarget()) {
                    statisticsDataA.e++;
                    statisticsDataA2.h++;
                } else {
                    statisticsDataA.c++;
                    statisticsDataA2.f++;
                }
            }
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (baseUnit2.team == gameEngine.playerTeam) {
            gameEngine.pingMinimap(baseUnit2, f2);
        }
    }
}
