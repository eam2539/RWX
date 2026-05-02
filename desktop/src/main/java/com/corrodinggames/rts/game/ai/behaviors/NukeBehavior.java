package com.corrodinggames.rts.game.ai.behaviors;

import android.graphics.PointF;
import com.corrodinggames.rts.game.ai.AIController;
import com.corrodinggames.rts.game.ai.AIUnitActionUtils;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.logic.ActionType;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.a.a.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/a/a/d.class */
public class NukeBehavior extends UnitAIBehavior {
    public final boolean b = true;
    static final AnimationTag c = AnimationTag.c("nukeLauncher");

    @Override // com.corrodinggames.rts.game.ai.behaviors.AIBehavior
    public AIBehaviorType a() {
        return AIBehaviorType.nuking;
    }

    @Override // com.corrodinggames.rts.game.ai.behaviors.UnitAIBehavior
    /* JADX INFO: renamed from: c */
    public boolean isApplicableToUnit(AIController aIController, OrderableUnit orderableUnit) {
        return a(orderableUnit);
    }

    public PointF d(AIController aIController, OrderableUnit orderableUnit) {
        return aIController.getRandomEligibleUnitPosition();
    }

    public void e(AIController aIController, OrderableUnit orderableUnit) {
        AbstractUnitAction abstractUnitActionA = AIUnitActionUtils.a(aIController, orderableUnit, ActionType.launch);
        if (abstractUnitActionA != null) {
            if (abstractUnitActionA.b(orderableUnit) && abstractUnitActionA.drawTooltip((BaseUnit) orderableUnit, false)) {
                PointF pointFD = d(aIController, orderableUnit);
                if (pointFD != null) {
                    aIController.c("nuke: launching at:" + pointFD.x + ", " + pointFD.y);
                    aIController.pathCheck(orderableUnit, abstractUnitActionA, pointFD, (BaseUnit) null);
                    return;
                } else {
                    aIController.c("nuke: no target");
                    return;
                }
            }
            aIController.c("nuke: not ready");
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void f(AIController aIController, OrderableUnit orderableUnit) {
        AbstractUnitAction abstractUnitActionA;
        if ((orderableUnit instanceof FactoryQueueInterface) && ((FactoryQueueInterface) orderableUnit).dy() && (abstractUnitActionA = AIUnitActionUtils.a(aIController, orderableUnit, ActionType.launchAmmo)) != null && aIController.isPathPossibleBetweenPoints(abstractUnitActionA.getDisplayText(), orderableUnit)) {
            aIController.c("ai nuke building");
            aIController.issueUnitAction(orderableUnit, abstractUnitActionA);
        }
    }

    public boolean a(OrderableUnit orderableUnit) {
        if (AIUnitActionUtils.a(orderableUnit, c)) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.ai.behaviors.AIBehavior
    public void b(float f, AIController aIController) {
        BaseUnit[] baseUnitArrA = this.managedUnits.a();
        int size = this.managedUnits.size();
        for (int i = 0; i < size; i++) {
            OrderableUnit orderableUnit = (OrderableUnit) baseUnitArrA[i];
            f(aIController, orderableUnit);
            e(aIController, orderableUnit);
        }
    }
}
