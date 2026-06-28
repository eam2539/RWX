package io.github.rwx.ui;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.gameFramework.GameEngine;

class TeamBalanceStats {
    String statName;
    int minValue = 0;
    int maxValue = 0;
    PlayerTeam minTeam = null;
    PlayerTeam maxTeam = null;
    int minTeamCount;
    int maxTeamCount;

    TeamBalanceStats(String str) {
        this.statName = str;
    }

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
