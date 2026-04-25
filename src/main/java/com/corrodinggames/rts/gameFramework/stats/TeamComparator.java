package com.corrodinggames.rts.gameFramework.stats;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.g.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/g/e.class */
public class TeamComparator extends GameObjectComparator {

    /* JADX INFO: renamed from: a */
    private final PlayerTeam team;

    public TeamComparator(PlayerTeam playerTeam) {
        this.team = playerTeam;
    }

    @Override // com.corrodinggames.rts.gameFramework.stats.GameObjectComparator
    public boolean a() {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.stats.GameObjectComparator
    public String b() {
        if (this.team.teamName == null) {
            return VariableScope.nullOrMissingString;
        }
        return this.team.teamName;
    }

    @Override // com.corrodinggames.rts.gameFramework.stats.GameObjectComparator
    public int c() {
        return this.team.getTeamUnitCount();
    }

    @Override // com.corrodinggames.rts.gameFramework.stats.GameObjectComparator
    public int d() {
        return -1;
    }

    @Override // com.corrodinggames.rts.gameFramework.stats.GameObjectComparator
    public int a(StatType statType) {
        return statType.calculate(this.team);
    }
}
