package com.corrodinggames.rts.game.units.buildings;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.PopupQueueAction;
import com.corrodinggames.rts.gameFramework.local.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.u */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/u.class */
class SeaFactoryT2Upgrade extends PopupQueueAction {
    public SeaFactoryT2Upgrade() {
        super(SeaFactory.upgradeActionId.getId());
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String isLocked() {
        return "-Allows factory to build Tech 2 units";
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getCostForUnit() {
        return Locale.get("gui.actions.upgradeT2", new Object[0]);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int isConfirmed() {
        return UnitTypeEnum.seaFactory.getUpgradeCost(2);
    }

    @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction
    public float K() {
        return 4.0E-4f;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
        SeaFactory seaFactory = (SeaFactory) baseUnit;
        if (seaFactory.buildingAnimationState != 1 || seaFactory.a(getActionId(), z) > 0) {
            return false;
        }
        return super.drawTooltip(baseUnit, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean b(BaseUnit baseUnit) {
        if (((SeaFactory) baseUnit).buildingAnimationState != 1) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: L, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum getUnitType() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType isAlsoSelected() {
        return ActionDisplayType.upgrade;
    }
}
