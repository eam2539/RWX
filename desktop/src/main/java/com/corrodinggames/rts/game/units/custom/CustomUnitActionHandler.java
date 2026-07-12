package com.corrodinggames.rts.game.units.custom;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.PopupQueueAction;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.u */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/u.class */
public class CustomUnitActionHandler {

    /* JADX INFO: renamed from: a */
    FastArrayList<String> actionNames = new FastArrayList();

    /* JADX INFO: renamed from: b */
    FastArrayList actions;

    /* JADX INFO: renamed from: c */
    String actionName;

    /* JADX INFO: renamed from: d */
    String actionDescription;

    public void a(CustomUnitConfig customUnitConfig) {
    }

    public void b(CustomUnitConfig customUnitConfig) throws ConfigParseException {
        FastArrayList fastArrayList = new FastArrayList();
        for (String str : this.actionNames) {
            AbstractUnitAction abstractUnitActionFindCustomActionByDisplayName = customUnitConfig.findCustomActionByDisplayName(str);
            if (abstractUnitActionFindCustomActionByDisplayName == null) {
                throw new ConfigParseException("[" + this.actionDescription + "]" + this.actionName + " Could not find action:" + str + " on unit: " + customUnitConfig.getConfigDisplayPath());
            }
            if (abstractUnitActionFindCustomActionByDisplayName instanceof PopupQueueAction) {
                fastArrayList.add((PopupQueueAction) abstractUnitActionFindCustomActionByDisplayName);
            } else {
                throw new ConfigParseException("[" + this.actionDescription + "]" + this.actionName + " Action:" + str + " on unit: " + customUnitConfig.getConfigDisplayPath() + " doesn't have the right type");
            }
        }
        this.actions = fastArrayList;
    }

    public void a(CustomUnit customUnit, PointF pointF, BaseUnit baseUnit, int i, int i2) {
        if (this.actions == null) {
            NetworkEngine.reportDesync("Action on " + customUnit.r().getUnitTypeDescriptionShort() + " has not been linked");
            return;
        }
        Iterator it = this.actions.iterator();
        while (it.hasNext()) {
            customUnit.a((AbstractUnitAction) it.next(), pointF, baseUnit, i, i2);
        }
    }

    public FastArrayList a() {
        if (this.actions == null) {
            NetworkEngine.reportDesync("Action on [" + this.actionDescription + "]" + this.actionName + " has not been linked");
            return new FastArrayList();
        }
        return this.actions;
    }

    public void a(CustomUnit customUnit, PointF pointF, BaseUnit baseUnit) {
        if (this.actions == null) {
            NetworkEngine.reportDesync("Action on " + customUnit.r().getUnitTypeDescriptionShort() + " has not been linked");
            return;
        }
        Iterator it = this.actions.iterator();
        while (it.hasNext()) {
            customUnit.unitEffectManager.a((PopupQueueAction) ((AbstractUnitAction) it.next()), false, pointF, baseUnit);
        }
    }
}
