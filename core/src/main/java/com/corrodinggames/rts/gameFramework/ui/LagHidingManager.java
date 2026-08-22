package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.condition.StoredResources;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.statistics.PlaceholderUnit;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.an */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/an.class */
public class LagHidingManager {
    static FastArrayList a = new FastArrayList();
    static final PlaceholderUnit b = new PlaceholderUnit();

    public static UnitSnapshot a(long j) {
        Object[] objArrA = a.a();
        for (int i = a.size - 1; i >= 0; i--) {
            UnitSnapshot unitSnapshot = (UnitSnapshot) objArrA[i];
            if (unitSnapshot.unitId == j) {
                return unitSnapshot;
            }
        }
        return null;
    }

    public static UnitSnapshot a(BaseUnit baseUnit) {
        UnitSnapshot unitSnapshotA = a(baseUnit.objectId);
        if (unitSnapshotA == null) {
            unitSnapshotA = new UnitSnapshot();
            unitSnapshotA.unitId = baseUnit.objectId;
            unitSnapshotA.ammo = baseUnit.ammo;
            unitSnapshotA.unitFlags = baseUnit.unitFlags;
            unitSnapshotA.blockingFrame = GameEngine.getInstance().networkEngine.nextBlockingFrame;
            a.add(unitSnapshotA);
        }
        return unitSnapshotA;
    }

    public static void a(BaseUnit baseUnit, UnitPrice unitPrice) {
        if (!GameEngine.getInstance().networkEngine.networkGameActive) {
            return;
        }
        UnitSnapshot unitSnapshotA = a(baseUnit);
        unitSnapshotA.ammo += unitPrice.f;
        unitSnapshotA.unitFlags = unitPrice.c(unitSnapshotA.unitFlags);
        if (!unitPrice.k.c()) {
            unitSnapshotA.resources = StoredResources.b(unitSnapshotA.resources, unitPrice.k);
        }
    }

    public static void b(BaseUnit baseUnit, UnitPrice unitPrice) {
        if (!GameEngine.getInstance().networkEngine.networkGameActive) {
            return;
        }
        UnitSnapshot unitSnapshotA = a(baseUnit);
        unitSnapshotA.ammo -= unitPrice.f;
        unitSnapshotA.unitFlags = unitPrice.c(unitSnapshotA.unitFlags);
        if (!unitPrice.k.c()) {
            unitSnapshotA.resources = StoredResources.a(unitSnapshotA.resources, unitPrice.k);
        }
        if (a.size > 0) {
        }
    }

    public static boolean c(BaseUnit baseUnit, UnitPrice unitPrice) {
        UnitSnapshot unitSnapshotA = a(baseUnit.objectId);
        if (unitSnapshotA != null) {
            b.team = baseUnit.team;
            b.ammo = unitSnapshotA.ammo;
            b.unitFlags = unitSnapshotA.unitFlags;
            StoredResources unitAICombatRange = b.getCustomResources();
            b.a(unitSnapshotA.resources);
            boolean zB = unitPrice.b(b);
            b.a(unitAICombatRange);
            return zB;
        }
        return unitPrice.b(baseUnit);
    }

    public static boolean a(LogicBoolean logicBoolean, OrderableUnit orderableUnit) {
        UnitSnapshot unitSnapshotA = a(orderableUnit.objectId);
        if (unitSnapshotA != null) {
            int i = orderableUnit.ammo;
            int i2 = orderableUnit.unitFlags;
            orderableUnit.ammo = unitSnapshotA.ammo;
            orderableUnit.unitFlags = unitSnapshotA.unitFlags;
            boolean z = logicBoolean.read(orderableUnit);
            orderableUnit.ammo = i;
            orderableUnit.unitFlags = i2;
            return z;
        }
        return logicBoolean.read(orderableUnit);
    }

    public static void a() {
        if (a.size > 0) {
            GameEngine.log("LagHiding: clearing: " + a.size);
        }
        a.clear();
    }

    public static void a(OrderableUnit orderableUnit, AbstractUnitAction abstractUnitAction) {
        if (a.size() == 0) {
            return;
        }
        int i = GameEngine.getInstance().networkEngine.nextBlockingFrame;
        for (int size = a.size() - 1; size >= 0; size--) {
            UnitSnapshot unitSnapshot = (UnitSnapshot) a.get(size);
            if (unitSnapshot.unitId == orderableUnit.objectId) {
                a.remove(size);
                return;
            } else {
                if (unitSnapshot.blockingFrame < i + 80) {
                    a.remove(size);
                    return;
                }
            }
        }
    }
}
