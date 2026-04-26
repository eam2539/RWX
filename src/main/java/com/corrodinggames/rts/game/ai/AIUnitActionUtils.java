package com.corrodinggames.rts.game.ai;

import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitCommand;
import com.corrodinggames.rts.game.units.UnitCommandType;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logic.ActionType;
import com.corrodinggames.rts.gameFramework.Utility;
import java.util.AbstractList;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.a.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/a/f.class */
public class AIUnitActionUtils {
    static boolean a(OrderableUnit orderableUnit) {
        UnitCommand currentWaypoint;
        boolean z = false;
        if (orderableUnit.hasNoCurrentWaypoint()) {
            z = true;
        }
        if (!z && (currentWaypoint = orderableUnit.getCurrentWaypoint()) != null && currentWaypoint.getCommandType() == UnitCommandType.reclaim) {
            z = true;
        }
        return z;
    }

    static boolean b(OrderableUnit orderableUnit) {
        boolean z = false;
        if (orderableUnit.hasNoCurrentWaypoint()) {
            z = true;
        }
        return z;
    }

    public static Object a(AbstractList abstractList) {
        int size = abstractList.size();
        if (size == 0) {
            return null;
        }
        return abstractList.get(Utility.getRandomIntInRange(0, size - 1));
    }

    public static boolean a(OrderableUnit orderableUnit, AnimationTag animationTag) {
        UnitType unitTypeR = orderableUnit.r();
        if ((unitTypeR instanceof CustomUnitConfig) && AnimationTag.a(animationTag, ((CustomUnitConfig) unitTypeR).aiTags)) {
            return true;
        }
        return false;
    }

    public static AbstractUnitAction a(AIController aIController, OrderableUnit orderableUnit, ActionType actionType) {
        ArrayList<AbstractUnitAction> arrayListN = orderableUnit.getAvailableActions();
        ArrayList reusableList = aIController.getReusableList();
        for (AbstractUnitAction abstractUnitAction : arrayListN) {
            if (abstractUnitAction.getActionTypeForUnit(orderableUnit) == actionType) {
                reusableList.add(abstractUnitAction);
            }
        }
        if (reusableList.size() > 0) {
            return (AbstractUnitAction) a(reusableList);
        }
        return null;
    }
}
