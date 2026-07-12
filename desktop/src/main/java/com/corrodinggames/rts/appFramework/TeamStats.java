package com.corrodinggames.rts.appFramework;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/o.class */
public class TeamStats {

    /* JADX INFO: renamed from: a */
    String statName;

    /* JADX INFO: renamed from: b */
    int minValue = 0;

    /* JADX INFO: renamed from: c */
    int maxValue = 0;

    /* JADX INFO: renamed from: d */
    PlayerTeam minTeam = null;

    /* JADX INFO: renamed from: e */
    PlayerTeam maxTeam = null;

    /* JADX INFO: renamed from: f */
    int minTeamCount;

    /* JADX INFO: renamed from: g */
    int maxTeamCount;

    TeamStats(String str) {
        this.statName = str;
    }

    /* JADX INFO: renamed from: a */
    public void addValue(PlayerTeam playerTeam, int i) {
        if (i < this.minValue || this.minTeam == null) {
            this.minValue = i;
            this.minTeam = playerTeam;
            this.minTeamCount = 1;
        } else if (i == this.minValue) {
            this.minTeamCount++;
        }
        if (i > this.maxValue || this.maxTeam == null) {
            this.maxValue = i;
            this.maxTeam = playerTeam;
            this.maxTeamCount = 1;
        } else if (i == this.maxValue) {
            this.maxTeamCount++;
        }
    }

    /* JADX INFO: renamed from: a */
    public boolean checkForImbalance() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.minValue == this.maxValue) {
            return false;
        }
        if (this.minTeam == null && this.maxTeam == null) {
            return false;
        }
        if (this.maxTeamCount == 1) {
            gameEngine.gameUI.messageManager.addMessage((String) null, "Warning: Uneven map - Player " + (this.maxTeam.teamId + 1) + " on team " + this.maxTeam.getTeamSlotLabel() + ": " + this.statName + " is " + this.maxValue + " vs " + this.minValue);
            return true;
        }
        gameEngine.gameUI.messageManager.addMessage((String) null, "Warning: Uneven map - " + this.maxTeamCount + " players including player " + (this.maxTeam.teamId + 1) + " on team " + (this.maxTeam.teamColorId + 1) + ": " + this.statName + " is " + this.maxValue + " vs " + this.minValue);
        return true;
    }
}
