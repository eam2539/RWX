package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.ActionType;
import com.corrodinggames.rts.game.units.actions.NoneAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.ui.GameUI;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/k.class */
class TeamChangeAction extends NoneAction {
    boolean a;
    boolean b;

    public TeamChangeAction(boolean z, boolean z2) {
        super("changeTeam" + z + "d:" + z2);
        this.a = z;
        this.b = z2;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getDisplayName() {
        if (this.b) {
            return "Selected player";
        }
        if (this.a) {
            return "<- Set player";
        }
        return "Set player ->";
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public String d() {
        if (!this.b) {
            if (this.a) {
                return "<-";
            }
            return "->";
        }
        GameEngine gameEngine = GameEngine.getInstance();
        PlayerTeam playerTeam = null;
        for (BaseUnit baseUnit : gameEngine.gameUI.selectedUnitsList) {
            if (baseUnit instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                if (orderableUnit.isSelected && gameEngine.gameUI.canControlUnit(orderableUnit)) {
                    playerTeam = orderableUnit.team;
                }
            }
        }
        String str = VariableScope.nullOrMissingString;
        if (playerTeam != null) {
            str = str + "Team - " + (playerTeam.teamId + 1) + VariableScope.nullOrMissingString;
        }
        return str;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String getDescription() {
        return "Change targeted player for editor";
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: l */
    public float getBuildSpeed() {
        if (!GameUI.bP) {
            return 0.8f;
        }
        return 0.5f;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: m */
    public int getKeyBinding() {
        if (this.b) {
            return 2;
        }
        return 4;
    }

    @Override // com.corrodinggames.rts.game.units.actions.NoneAction, com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType getActionDisplayType() {
        if (this.b) {
            return ActionDisplayType.infoOnly;
        }
        return super.getActionDisplayType();
    }

    @Override // com.corrodinggames.rts.game.units.actions.NoneAction, com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType getActionType() {
        if (this.b) {
            return ActionType.infoOnly;
        }
        return super.getActionType();
    }
}
