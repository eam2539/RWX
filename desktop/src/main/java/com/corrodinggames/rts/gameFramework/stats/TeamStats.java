package com.corrodinggames.rts.gameFramework.stats;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.game.units.custom.condition.resources.CreditsResource;
import com.corrodinggames.rts.gameFramework.GameEngine;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.g.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/g/a.class */
public class TeamStats {

    /* JADX INFO: renamed from: a */
    private final StatType statType;

    /* JADX INFO: renamed from: b */
    private final StatGroup statGroup;

    /* JADX INFO: renamed from: c */
    private final ArrayList comparators;

    public TeamStats() {
        this(StatType.none, StatGroup.player);
    }

    public TeamStats(StatType statType, StatGroup statGroup) {
        this.comparators = new ArrayList();
        this.statType = statType;
        this.statGroup = statGroup;
    }

    /* JADX INFO: renamed from: a */
    public void rebuild() {
        if (this.statType == StatType.none) {
            return;
        }
        ArrayList<PlayerTeam> arrayListIsEnemyToTeam = PlayerTeam.isEnemyToTeam(false);
        if (this.statGroup == StatGroup.player) {
            Iterator it = arrayListIsEnemyToTeam.iterator();
            while (it.hasNext()) {
                this.comparators.add(new TeamComparator((PlayerTeam) it.next()));
            }
        } else if (this.statGroup == StatGroup.allyGroup) {
            for (Integer num : PlayerTeam.getAllTeams()) {
                ArrayList arrayList = new ArrayList();
                for (PlayerTeam playerTeam : arrayListIsEnemyToTeam) {
                    if (playerTeam.teamColorId == num.intValue()) {
                        arrayList.add(playerTeam);
                    }
                }
                this.comparators.add(new TeamObjectComparator(num.intValue(), arrayList));
            }
        } else if (this.statGroup == StatGroup.combinedPlayerAndGroup) {
            int size = 0;
            ArrayList<Integer> allTeams = PlayerTeam.getAllTeams();
            for (Integer num2 : allTeams) {
                ArrayList arrayList2 = new ArrayList();
                for (PlayerTeam playerTeam2 : arrayListIsEnemyToTeam) {
                    if (playerTeam2.teamColorId == num2.intValue()) {
                        arrayList2.add(playerTeam2);
                    }
                }
                if (size < arrayList2.size()) {
                    size = arrayList2.size();
                }
            }
            if (size <= 1) {
                Iterator it2 = arrayListIsEnemyToTeam.iterator();
                while (it2.hasNext()) {
                    this.comparators.add(new TeamComparator((PlayerTeam) it2.next()));
                }
            } else {
                for (Integer num3 : allTeams) {
                    ArrayList arrayList3 = new ArrayList();
                    for (PlayerTeam playerTeam3 : arrayListIsEnemyToTeam) {
                        if (playerTeam3.teamColorId == num3.intValue()) {
                            arrayList3.add(playerTeam3);
                        }
                    }
                    this.comparators.add(new TeamObjectComparator(num3.intValue(), arrayList3));
                    Iterator it3 = arrayList3.iterator();
                    while (it3.hasNext()) {
                        this.comparators.add(new TeamComparator((PlayerTeam) it3.next()));
                    }
                }
            }
        }
        update();
    }

    /* JADX INFO: renamed from: b */
    public void update() {
        Iterator it = this.comparators.iterator();
        while (it.hasNext()) {
            ((GameObjectComparator) it.next()).getName(this.statType);
        }
    }

    /* JADX INFO: renamed from: c */
    public void nextSort() {
        int iOrdinal = this.statType.ordinal() + 1;
        if (iOrdinal >= StatType.values().length) {
            iOrdinal = 0;
        }
        GameEngine.getInstance().setupTeamStats(StatType.values()[iOrdinal], StatGroup.combinedPlayerAndGroup);
    }

    /* JADX INFO: renamed from: a */
    public String getFormattedValue(GameObjectComparator gameObjectComparator) {
        return (this.statGroup == StatGroup.combinedPlayerAndGroup && (gameObjectComparator instanceof TeamComparator)) ? "   " + formatValue(this.statType, gameObjectComparator.cachedStat) : formatValue(this.statType, gameObjectComparator.cachedStat);
    }

    /* JADX INFO: renamed from: a */
    public static String formatValue(StatType statType, int i) {
        switch (statType) {
            case none:
                return VariableScope.nullOrMissingString + i;
            case income:
                return "+" + CreditsResource.D.a(i, true);
            default:
                return CreditsResource.D.a(i, true);
        }
    }

    /* JADX INFO: renamed from: d */
    public ArrayList getComparators() {
        return this.comparators;
    }

    /* JADX INFO: renamed from: e */
    public StatType getStatType() {
        return this.statType;
    }

    /* JADX INFO: renamed from: f */
    public StatGroup getStatGroup() {
        return this.statGroup;
    }
}
