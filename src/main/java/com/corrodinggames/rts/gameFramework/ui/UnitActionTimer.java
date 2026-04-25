package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/c.class */
class UnitActionTimer {

    /* JADX INFO: renamed from: a */
    BaseUnit unit;

    /* JADX INFO: renamed from: b */
    AbstractUnitAction action;

    /* JADX INFO: renamed from: c */
    float timer;

    /* JADX INFO: renamed from: d */
    boolean isNegative;

    /* JADX INFO: renamed from: e */
    boolean field_e;

    /* JADX INFO: renamed from: f */
    static FastArrayList<UnitActionTimer> timers = new FastArrayList();

    UnitActionTimer() {
    }

    /* JADX INFO: renamed from: a */
    public static void startTimer(BaseUnit baseUnit, AbstractUnitAction abstractUnitAction, boolean z, boolean z2) {
        UnitActionTimer unitActionTimerFindTimer = findTimer(baseUnit, abstractUnitAction, z2);
        if (unitActionTimerFindTimer == null) {
            unitActionTimerFindTimer = new UnitActionTimer();
            timers.add(unitActionTimerFindTimer);
        }
        unitActionTimerFindTimer.unit = baseUnit;
        unitActionTimerFindTimer.action = abstractUnitAction;
        unitActionTimerFindTimer.timer = 10.0f;
        unitActionTimerFindTimer.isNegative = z;
        unitActionTimerFindTimer.field_e = z2;
    }

    /* JADX INFO: renamed from: a */
    public static UnitActionTimer findTimer(BaseUnit baseUnit, AbstractUnitAction abstractUnitAction, boolean z) {
        for (UnitActionTimer unitActionTimer : timers) {
            if (unitActionTimer.unit == baseUnit && unitActionTimer.action == abstractUnitAction && unitActionTimer.field_e == z) {
                return unitActionTimer;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: b */
    public static float getTimerValue(BaseUnit baseUnit, AbstractUnitAction abstractUnitAction, boolean z) {
        UnitActionTimer unitActionTimerFindTimer = findTimer(baseUnit, abstractUnitAction, z);
        if (unitActionTimerFindTimer != null) {
            float f = unitActionTimerFindTimer.timer / 10.0f;
            if (unitActionTimerFindTimer.isNegative) {
                f = -f;
            }
            return f;
        }
        return 0.0f;
    }

    /* JADX INFO: renamed from: a */
    public static void updateTimers(float f) {
        for (int size = timers.size() - 1; size >= 0; size--) {
            UnitActionTimer unitActionTimer = (UnitActionTimer) timers.get(size);
            unitActionTimer.timer -= f;
            if (unitActionTimer.timer <= 0.0f) {
                timers.remove(size);
            }
        }
    }
}
