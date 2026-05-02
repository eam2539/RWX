package com.corrodinggames.rts.game.units.custom.logic;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitAction;
import com.corrodinggames.rts.game.units.custom.CustomUnitTrigger;
import com.corrodinggames.rts.game.units.custom.LocalizedText;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.ui.LagHidingManager;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/c.class */
public class ConfigurableCustomAction extends UnitAction {

    /* JADX INFO: renamed from: b */
    public LogicBoolean availableCondition;

    /* JADX INFO: renamed from: c */
    public LogicBoolean condition1;

    /* JADX INFO: renamed from: d */
    public LocalizedText action1;

    /* JADX INFO: renamed from: e */
    public LogicBoolean condition2;

    /* JADX INFO: renamed from: f */
    public LocalizedText action2;

    /* JADX INFO: renamed from: g */
    public LogicBoolean condition3;

    /* JADX INFO: renamed from: h */
    public LocalizedText action3;

    /* JADX INFO: renamed from: i */
    public boolean alwaysShow;

    /* JADX INFO: renamed from: j */
    public LogicBoolean guiBlinkingCondition;

    /* JADX INFO: renamed from: k */
    public UnitPrice buildCost;

    /* JADX INFO: renamed from: l */
    public UnitPrice resourceCost;

    /* JADX INFO: renamed from: m */
    public UnitPrice upgradeCost;

    public static UnitAction a(CustomActionDef customActionDef) {
        boolean z = false;
        if (customActionDef.highlightCondition != null && customActionDef.highlightCondition != LogicBoolean.falseBoolean) {
            z = true;
        }
        if (customActionDef.highlightColorCondition != null && customActionDef.highlightColorCondition != LogicBoolean.falseBoolean) {
            z = true;
        }
        if (customActionDef.iconCondition != null && customActionDef.iconCondition != LogicBoolean.falseBoolean) {
            z = true;
        }
        if (customActionDef.enabledCondition != null && customActionDef.enabledCondition != LogicBoolean.trueBoolean) {
            z = true;
        }
        if (customActionDef.condition10 != null && customActionDef.condition10 != LogicBoolean.falseBoolean) {
            z = true;
        }
        if (customActionDef.energyCost != null) {
            z = true;
        }
        if (customActionDef.buildCost != null) {
            z = true;
        }
        if (!z) {
            return UnitAction.a;
        }
        ConfigurableCustomAction configurableCustomAction = new ConfigurableCustomAction();
        configurableCustomAction.condition1 = customActionDef.highlightCondition;
        configurableCustomAction.action1 = customActionDef.highlightText;
        configurableCustomAction.condition2 = customActionDef.highlightColorCondition;
        configurableCustomAction.action2 = customActionDef.highlightColor;
        configurableCustomAction.condition3 = customActionDef.iconCondition;
        configurableCustomAction.action3 = customActionDef.icon;
        configurableCustomAction.availableCondition = customActionDef.enabledCondition;
        configurableCustomAction.guiBlinkingCondition = customActionDef.condition10;
        configurableCustomAction.resourceCost = customActionDef.energyCost;
        configurableCustomAction.buildCost = customActionDef.buildCost;
        configurableCustomAction.upgradeCost = customActionDef.resourceCost;
        configurableCustomAction.alwaysShow = customActionDef.hideInBuildMenu;
        return configurableCustomAction;
    }

    public static UnitAction a(CustomUnitTrigger customUnitTrigger) {
        boolean z = false;
        if (customUnitTrigger.logicCondition != null && customUnitTrigger.logicCondition != LogicBoolean.falseBoolean) {
            z = true;
        }
        if (!z) {
            return UnitAction.a;
        }
        ConfigurableCustomAction configurableCustomAction = new ConfigurableCustomAction();
        configurableCustomAction.condition1 = customUnitTrigger.logicCondition;
        configurableCustomAction.action1 = LocalizedText.a(customUnitTrigger.action);
        return configurableCustomAction;
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public boolean a(BaseUnit baseUnit) {
        return this.alwaysShow;
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public boolean b(BaseUnit baseUnit) {
        return a(baseUnit, -1);
    }

    public boolean a(BaseUnit baseUnit, int i) {
        if (this.condition1 != null && (i == -1 || i == 1)) {
            if (!(baseUnit instanceof OrderableUnit)) {
                GameEngine.reportProblem("CustomActionConfig lockedInGame:" + baseUnit.r().getUnitTypeDescriptionShort() + " is not a OrderableUnit unit");
                return false;
            }
            if (this.condition1.read((OrderableUnit) baseUnit)) {
                return true;
            }
        }
        if (this.condition2 != null && (i == -1 || i == 2)) {
            if (!(baseUnit instanceof OrderableUnit)) {
                GameEngine.reportProblem("CustomActionConfig lockedInGame:" + baseUnit.r().getUnitTypeDescriptionShort() + " is not a OrderableUnit unit");
                return false;
            }
            if (this.condition2.read((OrderableUnit) baseUnit)) {
                return true;
            }
        }
        if (this.condition3 == null) {
            return false;
        }
        if (i == -1 || i == 3) {
            if (!(baseUnit instanceof OrderableUnit)) {
                GameEngine.reportProblem("CustomActionConfig lockedInGame:" + baseUnit.r().getUnitTypeDescriptionShort() + " is not a OrderableUnit unit");
                return false;
            }
            if (this.condition3.read((OrderableUnit) baseUnit)) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public String c(BaseUnit baseUnit) {
        if (a(baseUnit, 1) && this.action1 != null) {
            return this.action1.b(baseUnit);
        }
        if (a(baseUnit, 2) && this.action2 != null) {
            return this.action2.b(baseUnit);
        }
        if (a(baseUnit, 3) && this.action3 != null) {
            return this.action3.b(baseUnit);
        }
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public boolean a(BaseUnit baseUnit, boolean z) {
        if (this.availableCondition != null) {
            if (!(baseUnit instanceof OrderableUnit)) {
                GameEngine.reportProblem("CustomActionConfig isAvailable:" + baseUnit.r().getUnitTypeDescriptionShort() + " is not a OrderableUnit unit");
                return true;
            }
            if (z) {
                return LagHidingManager.a(this.availableCondition, (OrderableUnit) baseUnit);
            }
            return this.availableCondition.read((OrderableUnit) baseUnit);
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public boolean d(BaseUnit baseUnit) {
        if (this.guiBlinkingCondition != null) {
            if (!(baseUnit instanceof OrderableUnit)) {
                GameEngine.reportProblem("CustomActionConfig isGuiBlinking:" + baseUnit.r().getUnitTypeDescriptionShort() + " is not a OrderableUnit unit");
                return true;
            }
            return this.guiBlinkingCondition.read((OrderableUnit) baseUnit);
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public void a(BaseUnit baseUnit, BaseUnit baseUnit2) {
        if (this.resourceCost != null) {
            this.resourceCost.h(baseUnit);
        }
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public UnitPrice a() {
        return this.buildCost;
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public UnitPrice b() {
        return this.upgradeCost;
    }
}
