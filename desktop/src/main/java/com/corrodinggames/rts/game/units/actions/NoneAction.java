package com.corrodinggames.rts.game.units.actions;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.x */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/x.class */
public abstract class NoneAction extends AbstractUnitAction {
    public NoneAction(int i) {
        super(i);
    }

    public NoneAction(String str) {
        super(str);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public int getActiveCount(BaseUnit baseUnit, boolean z) {
        return -1;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int getCostAmount() {
        return 0;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: K, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum getUnitType() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType getActionType() {
        return ActionType.none;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType getActionDisplayType() {
        return ActionDisplayType.none;
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
