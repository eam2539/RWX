package com.corrodinggames.rts.gameFramework.stats;

import com.corrodinggames.rts.game.PlayerTeam;

import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.g.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/g/b.class */
public class TeamObjectComparator extends GameObjectComparator {

    /* JADX INFO: renamed from: a */
    private final String name;

    /* JADX INFO: renamed from: b */
    private final int teamColorId;

    /* JADX INFO: renamed from: c */
    private final ArrayList teams;

    public TeamObjectComparator(int i, ArrayList arrayList) {
        this.teamColorId = i;
        this.teams = arrayList;
        this.name = "Team " + PlayerTeam.getTeamSlotLabel(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.stats.GameObjectComparator
    public boolean a() {
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.stats.GameObjectComparator
    public String b() {
        return this.name;
    }

    @Override // com.corrodinggames.rts.gameFramework.stats.GameObjectComparator
    public int c() {
        return PlayerTeam.i(this.teamColorId);
    }

    @Override // com.corrodinggames.rts.gameFramework.stats.GameObjectComparator
    public int d() {
        return PlayerTeam.i(this.teamColorId);
    }

    @Override // com.corrodinggames.rts.gameFramework.stats.GameObjectComparator
    public int a(StatType statType) {
        int iCalculate = 0;
        Iterator it = this.teams.iterator();
        while (it.hasNext()) {
            iCalculate += statType.calculate((PlayerTeam) it.next());
        }
        return iCalculate;
    }
}
