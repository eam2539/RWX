package com.corrodinggames.rts.game.units.buildings;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.PopupQueueAction;
import com.corrodinggames.rts.gameFramework.local.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/b.class */
class AirFactoryUpgradeT2 extends PopupQueueAction {
    public AirFactoryUpgradeT2() {
        super(AirFactory.upgradeActionId.getId());
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String getDescription() {
        return "-Allows factory to build Tech 2 units";
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getDisplayName() {
        return Locale.get("gui.actions.upgradeT2", new Object[0]);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int getCostAmount() {
        return UnitTypeEnum.airFactory.getUpgradeCost(2);
    }

    @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction
    public float K() {
        return 4.0E-4f;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean canAfford(BaseUnit baseUnit, boolean z) {
        AirFactory airFactory = (AirFactory) baseUnit;
        if (airFactory.factoryLevel != 1 || airFactory.a(getActionId(), z) > 0) {
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
