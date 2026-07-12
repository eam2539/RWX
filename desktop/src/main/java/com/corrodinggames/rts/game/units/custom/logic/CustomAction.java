package com.corrodinggames.rts.game.units.custom.logic;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.PopupQueueAction;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.UnitTypeReference;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.ui.GameInterfaceRenderer;
import com.corrodinggames.rts.gameFramework.ui.LagHidingManager;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/g.class */
public class CustomAction extends PopupQueueAction {
    public CustomActionDef a;
    public UnitTypeReference b;
    public ActionType c;

    public CustomAction(CustomActionDef customActionDef, UnitTypeReference unitTypeReference) {
        super((String) null);
        this.c = ActionType.disabled;
        String str = VariableScope.nullOrMissingString;
        canAfford(customActionDef.name != null ? customActionDef.name : (customActionDef.stringId != null ? str + customActionDef.stringId : str) + "_" + customActionDef.id);
        this.a = customActionDef;
        this.b = unitTypeReference;
        if (customActionDef.iconUnitType3 != null) {
            this.b = customActionDef.iconUnitType3;
        }
        this.c = customActionDef.aN;
        if (this.c == ActionType.auto) {
            boolean z = false;
            boolean z2 = false;
            if (customActionDef.energyCost3 != null && customActionDef.offset == null) {
                z2 = true;
            }
            if (customActionDef.buildCost.d()) {
                z = true;
                this.c = ActionType.upgrade;
            }
            if (z && !z2) {
                this.c = ActionType.upgrade;
            } else {
                this.c = ActionType.movementChange;
            }
            if (customActionDef.iconUnitType2 != null) {
                this.c = ActionType.sameAsBuilding;
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: P */
    public AnimationSet getAnimationSet() {
        return this.a.animationSet;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: F */
    public boolean getDisplayType() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: d */
    public boolean getDisplayTextWithQueueCount(BaseUnit baseUnit, boolean z) {
        return this.a.isDefaultBuildCommand2;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: k */
    public boolean isSingleUse(BaseUnit baseUnit) {
        return this.a.isDefaultBuildCommand3;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: l */
    public boolean isTargetingAction(BaseUnit baseUnit) {
        return this.a.isDefaultBuildCommand4;
    }

    @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction, com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: u */
    public boolean isGuiBlinking() {
        return super.isGuiBlinking();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
        CustomUnit customUnit = (CustomUnit) baseUnit;
        if (!this.a.isQueueUnitCommand && customUnit.a(getActionId(), z) > 0) {
            return false;
        }
        if (this.a.availableCondition != null) {
            if (z && getOptions()) {
                if (!LagHidingManager.a(this.a.availableCondition, customUnit)) {
                    return false;
                }
            } else if (!this.a.availableCondition.read(customUnit)) {
                return false;
            }
        }
        return super.drawTooltip(baseUnit, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isNotAvailable(BaseUnit baseUnit) {
        if (a(baseUnit, -1)) {
            return true;
        }
        return super.isNotAvailable(baseUnit);
    }

    public boolean a(BaseUnit baseUnit, int i) {
        if (this.a.highlightCondition != null && (i == -1 || i == 1)) {
            if (!(baseUnit instanceof CustomUnit)) {
                GameEngine.reportProblem("CustomActionConfig lockedInGame:" + baseUnit.r().getUnitTypeDescriptionShort() + " is not a custom unit");
                return false;
            }
            if (this.a.highlightCondition.read((CustomUnit) baseUnit)) {
                return true;
            }
        }
        if (this.a.highlightColorCondition != null && (i == -1 || i == 2)) {
            if (!(baseUnit instanceof CustomUnit)) {
                GameEngine.reportProblem("CustomActionConfig lockedInGame:" + baseUnit.r().getUnitTypeDescriptionShort() + " is not a custom unit");
                return false;
            }
            if (this.a.highlightColorCondition.read((CustomUnit) baseUnit)) {
                return true;
            }
        }
        if (this.a.iconCondition == null) {
            return false;
        }
        if (i == -1 || i == 3) {
            if (!(baseUnit instanceof CustomUnit)) {
                GameEngine.reportProblem("CustomActionConfig lockedInGame:" + baseUnit.r().getUnitTypeDescriptionShort() + " is not a custom unit");
                return false;
            }
            if (this.a.iconCondition.read((CustomUnit) baseUnit)) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: j */
    public String getIcon(BaseUnit baseUnit) {
        if (a(baseUnit, 1) && this.a.highlightText != null) {
            return this.a.highlightText.b(baseUnit);
        }
        if (a(baseUnit, 2) && this.a.highlightColor != null) {
            return this.a.highlightColor.b(baseUnit);
        }
        if (a(baseUnit, 3) && this.a.icon != null) {
            return this.a.icon.b(baseUnit);
        }
        return super.getIcon(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: r */
    public boolean isAvailable(BaseUnit baseUnit) {
        CustomUnit customUnit = (CustomUnit) baseUnit;
        if (this.a.enabledCondition != null) {
            if (getOptions()) {
                return LagHidingManager.a(this.a.enabledCondition, customUnit);
            }
            return this.a.enabledCondition.read(customUnit);
        }
        return super.b(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean b(BaseUnit baseUnit) {
        CustomUnit customUnit = (CustomUnit) baseUnit;
        if (this.a.enabledCondition != null) {
            return this.a.enabledCondition.read(customUnit);
        }
        return super.b(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean appendTooltip(BaseUnit baseUnit, PlayerTeam playerTeam) {
        if (!this.a.showInBuildMenu && !this.a.disableInBuildMenu) {
            return false;
        }
        if (baseUnit.team.d(playerTeam)) {
            return this.a.showInBuildMenu;
        }
        return this.a.disableInBuildMenu;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: r_ */
    public UnitPrice getAdditionalCost() {
        UnitPrice unitPriceB = this.unitAction.b();
        if (unitPriceB != null) {
            return unitPriceB;
        }
        return this.a.resourceCost;
    }

    @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction, com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public int isActive(BaseUnit baseUnit, boolean z) {
        if (this.a.isCondition) {
            return this.a.buildCost.a(baseUnit, true);
        }
        return super.isActive(baseUnit, z);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public String d() {
        return super.d();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getCostForUnit() {
        String strB = null;
        if (this.a.description != null) {
            strB = this.a.description.b();
        }
        return strB;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: d */
    public String isVisible(BaseUnit baseUnit) {
        UnitType typeOrNull;
        String strB = null;
        if (this.a.description != null) {
            strB = this.a.description.b(baseUnit);
        }
        if (this.a.targetUnitType != null && (typeOrNull = this.a.targetUnitType.getTypeOrNull(baseUnit)) != null) {
            if (strB == null) {
                strB = VariableScope.nullOrMissingString;
            } else if (!strB.equals(VariableScope.nullOrMissingString)) {
                strB = strB + " ";
            }
            strB = strB + typeOrNull.getUnitName();
        }
        if (this.a.message != null) {
            if (strB == null) {
                strB = VariableScope.nullOrMissingString;
            } else if (!strB.equals(VariableScope.nullOrMissingString)) {
                strB = strB + " ";
            }
            strB = strB + this.a.message.resolveText();
        }
        return strB;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String isLocked() {
        String strB = null;
        if (this.a.requiredUnitType != null) {
            strB = this.a.requiredUnitType.b();
        }
        return strB;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: e */
    public String getProducedUnitType(BaseUnit baseUnit) {
        UnitType typeOrNull;
        String strB = null;
        if (this.a.requiredUnitType != null) {
            strB = this.a.requiredUnitType.b(baseUnit);
        }
        if (this.a.sourceUnitType != null && (typeOrNull = this.a.sourceUnitType.getTypeOrNull(baseUnit)) != null) {
            if (strB == null) {
                strB = VariableScope.nullOrMissingString;
            } else if (!strB.equals(VariableScope.nullOrMissingString)) {
                strB = strB + " ";
            }
            strB = strB + typeOrNull.f();
        }
        if (this.a.relatedUnitType != null) {
            BaseUnit unitReferenceOrNull = this.a.relatedUnitType.getUnitReferenceOrNull(baseUnit);
            if (unitReferenceOrNull != null) {
                if (strB == null) {
                    strB = VariableScope.nullOrMissingString;
                } else if (!strB.equals(VariableScope.nullOrMissingString)) {
                    strB = strB + "\n\n";
                }
                strB = strB + GameInterfaceRenderer.a(unitReferenceOrNull, false, false, false);
            } else {
                BaseUnit unitOrSharedUnit = this.a.relatedUnitType.getUnitOrSharedUnit(baseUnit);
                if (unitOrSharedUnit != null) {
                    if (strB == null) {
                        strB = VariableScope.nullOrMissingString;
                    } else if (!strB.equals(VariableScope.nullOrMissingString)) {
                        strB = strB + "\n\n";
                    }
                    strB = strB + GameInterfaceRenderer.a(unitOrSharedUnit, false, false, true);
                }
            }
        }
        return strB;
    }

    public boolean L() {
        return this.a.autoRepeat2;
    }

    @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction
    public float K() {
        if (this.a.cooldownTime >= 1.0f) {
            return 1000.0f;
        }
        return this.a.cooldownTime;
    }

    @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction, com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public com.corrodinggames.rts.game.units.actions.ActionType e() {
        return this.a.queueType;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: B */
    public UnitPrice getDisplayText() {
        UnitPrice unitPriceA = this.unitAction.a();
        if (unitPriceA != null) {
            return unitPriceA;
        }
        return this.a.buildCost;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int isConfirmed() {
        return getDisplayText().a();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: i */
    public UnitType getUnitType() {
        if (this.b == null) {
            return null;
        }
        return this.b.c();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: y */
    public UnitType getAttachedUnitType() {
        if (this.a.iconUnitType3 != null) {
            return this.a.iconUnitType3.c();
        }
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: E */
    public UnitType getActionType() {
        if (this.a.iconUnitType2 != null) {
            return this.a.iconUnitType2.c();
        }
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: A */
    public boolean getDescription() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        if (this.a.iconUnitType3 != null) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType isAlsoSelected() {
        return this.a.aG;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: m */
    public boolean getEnergyCost(BaseUnit baseUnit) {
        return this.a.iconColor.read((CustomUnit) baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n */
    public boolean isSecondary(BaseUnit baseUnit) {
        if (this.a.iconColorCondition == null) {
            return false;
        }
        if (!(baseUnit instanceof CustomUnit)) {
            GameEngine.logColored("ai_isHighPriority non customUnit:" + baseUnit.r().getUnitTypeDescriptionShort());
            return false;
        }
        return this.a.iconColorCondition.read((CustomUnit) baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: v */
    public ActionType getActionTypeForUnit(BaseUnit baseUnit) {
        return this.c;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: H */
    public boolean isAttack() {
        return this.a.isDefaultAction;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: I */
    public boolean getTargetUnit() {
        return this.a.isDefaultBuildCommand;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: j */
    public Texture getIconColor() {
        return this.a.texture;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h */
    public Texture isShowingNotEnoughEnergy(BaseUnit baseUnit) {
        if (this.a.condition9 != null && (baseUnit instanceof CustomUnit) && !LagHidingManager.a(this.a.condition9, (CustomUnit) baseUnit)) {
            return null;
        }
        return this.a.texture2;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: J */
    public int getNotAvailableReason() {
        return this.a.texture3;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: i */
    public BaseUnit isShowingNotEnoughResources(BaseUnit baseUnit) {
        if (this.a.unitType != null) {
            return this.a.unitType.getUnitOrSharedUnit(baseUnit);
        }
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: s */
    public boolean shouldShowProgress(BaseUnit baseUnit) {
        return this.a.isUnitType;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: t */
    public boolean shouldShowCount(BaseUnit baseUnit) {
        return this.a.isUnitType2;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public boolean isTargetingGround(BaseUnit baseUnit) {
        if (this.a.condition10 != null) {
            return LagHidingManager.a(this.a.condition10, (CustomUnit) baseUnit);
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: Q */
    public boolean getOptions() {
        return this.a.isBuildAction;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public void a(OrderableUnit orderableUnit) {
        if (this.a.energyCost != null) {
            LagHidingManager.b(orderableUnit, this.a.energyCost);
        }
    }
}
