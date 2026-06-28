package com.corrodinggames.rts.game.units.buildings;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.PopupQueueAction;
import com.corrodinggames.rts.gameFramework.local.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/n.class */
class LandFactoryUpgradeAction extends PopupQueueAction {
    public LandFactoryUpgradeAction() {
        super(LandFactory.upgradeActionId.getId());
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String getDescription() {
        return Locale.get("units.landFactory.upgrade.description", new Object[0]);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getDisplayName() {
        return Locale.get("units.landFactory.upgrade.name", new Object[0]);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int getCostAmount() {
        return UnitTypeEnum.landFactory.getUpgradeCost(2);
    }

    @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction
    public float K() {
        return 4.0E-4f;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean canAfford(BaseUnit baseUnit, boolean z) {
        LandFactory landFactory = (LandFactory) baseUnit;
        if (landFactory.isUpgraded || landFactory.a(getActionId(), z) > 0) {
            return false;
        }
        return super.canAfford(baseUnit, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: L, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum getUnitType() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType getActionDisplayType() {
        return ActionDisplayType.upgrade;
    }
}
