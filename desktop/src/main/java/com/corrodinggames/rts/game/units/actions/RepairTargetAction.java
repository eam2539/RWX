package com.corrodinggames.rts.game.units.actions;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.local.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/n.class */
public class RepairTargetAction extends AbstractUnitAction {
    public RepairTargetAction() {
        super("c_3");
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String isLocked() {
        return Locale.get("gui.actions.repairTarget", new Object[0]);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getCostForUnit() {
        return Locale.get("gui.actions.repairTarget", new Object[0]);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int isConfirmed() {
        return 0;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public int isActive(BaseUnit baseUnit, boolean z) {
        return -1;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: K, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum getUnitType() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType e() {
        return ActionType.repairTarget;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h_ */
    public boolean shouldShowDisplayText() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType isAlsoSelected() {
        return ActionDisplayType.action;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h */
    public boolean getIconForUnit() {
        return true;
    }
}
