package com.corrodinggames.rts.game.units.actions;

import com.corrodinggames.rts.game.units.AttackMode;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.Command;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/d.class */
public class AttackModeAction extends AbstractUnitAction {
    int a;
    AttackMode b;

    public AttackModeAction() {
        super("c_7");
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public int isActive(BaseUnit baseUnit, boolean z) {
        return -1;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int isConfirmed() {
        return 0;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum getUnitType() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType e() {
        return ActionType.directToAction;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType isAlsoSelected() {
        return ActionDisplayType.none;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String isLocked() {
        return "Attack Mode";
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getCostForUnit() {
        AttackMode attackModeQ = q();
        if (attackModeQ != null) {
            return attackModeQ.name();
        }
        return "NA";
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: h_ */
    public boolean shouldShowDisplayText() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public void c(BaseUnit baseUnit) {
        GameEngine gameEngine = GameEngine.getInstance();
        AttackMode attackModeA = a(r());
        Command commandCreateCommandForTeam = gameEngine.commandController.createCommandForTeam(baseUnit.team);
        for (BaseUnit baseUnit2 : BaseUnit.bE) {
            if (baseUnit2 instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit2;
                if (orderableUnit.isSelected) {
                    commandCreateCommandForTeam.setTargetUnit(orderableUnit);
                }
            }
        }
        commandCreateCommandForTeam.setAttackMode(attackModeA);
        this.a = gameEngine.gameUI.selectionChangeCounter;
        this.b = attackModeA;
    }

    public AttackMode a(AttackMode attackMode) {
        if (attackMode == AttackMode.onlyInRange) {
            return AttackMode.guardArea;
        }
        if (attackMode == AttackMode.onlyInRange) {
            return AttackMode.aggressive;
        }
        return AttackMode.onlyInRange;
    }

    public AttackMode q() {
        GameEngine gameEngine = GameEngine.getInstance();
        AttackMode attackModeR = r();
        this.a = gameEngine.gameUI.selectionChangeCounter;
        this.b = attackModeR;
        return attackModeR;
    }

    public AttackMode r() {
        if (this.a == GameEngine.getInstance().gameUI.selectionChangeCounter && this.b != null) {
            return this.b;
        }
        AttackMode attackMode = null;
        for (BaseUnit baseUnit : BaseUnit.bE) {
            if (baseUnit instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                if (orderableUnit.isSelected) {
                    if (attackMode == null || attackMode == orderableUnit.attackMode) {
                        attackMode = orderableUnit.attackMode;
                    } else {
                        attackMode = AttackMode.mixed;
                    }
                }
            }
        }
        return attackMode;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean b(BaseUnit baseUnit) {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public String d() {
        return getCostForUnit();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: s */
    public boolean isWaitingForTarget() {
        return true;
    }
}
