package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.al */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/al.class */
abstract class UnitSelectionFilter {
    static UnitSelectionFilter a = new UnitSelectionFilter() { // from class: com.corrodinggames.rts.gameFramework.f.al.1
        @Override // com.corrodinggames.rts.gameFramework.ui.UnitSelectionFilter
        public boolean a(OrderableUnit orderableUnit) {
            if (orderableUnit.canUnitAttack() && !orderableUnit.u() && orderableUnit.unitTransportTarget == null && orderableUnit.hasNoCurrentWaypoint()) {
                return true;
            }
            return false;
        }
    };
    static UnitSelectionFilter b = new UnitSelectionFilter() { // from class: com.corrodinggames.rts.gameFramework.f.al.2
        @Override // com.corrodinggames.rts.gameFramework.ui.UnitSelectionFilter
        public boolean a(OrderableUnit orderableUnit) {
            if (orderableUnit.canUnitAttack() && !orderableUnit.u() && orderableUnit.unitTransportTarget == null) {
                return true;
            }
            return false;
        }
    };
    static UnitSelectionFilter c = new UnitSelectionFilter() { // from class: com.corrodinggames.rts.gameFramework.f.al.3
        @Override // com.corrodinggames.rts.gameFramework.ui.UnitSelectionFilter
        public boolean a(OrderableUnit orderableUnit) {
            if (orderableUnit.r() != null && orderableUnit.r().p() && orderableUnit.unitTransportTarget == null) {
                return true;
            }
            return false;
        }
    };
    static UnitSelectionFilter d = new UnitSelectionFilter() { // from class: com.corrodinggames.rts.gameFramework.f.al.4
        @Override // com.corrodinggames.rts.gameFramework.ui.UnitSelectionFilter
        public boolean a(OrderableUnit orderableUnit) {
            if (orderableUnit.r() == UnitTypeEnum.fabricator && orderableUnit.getUpgradeLevel() < 3 && orderableUnit.unitTransportTarget == null) {
                return true;
            }
            return false;
        }
    };
    static UnitSelectionFilter e = new UnitSelectionFilter() { // from class: com.corrodinggames.rts.gameFramework.f.al.5
        @Override // com.corrodinggames.rts.gameFramework.ui.UnitSelectionFilter
        public boolean a(OrderableUnit orderableUnit) {
            if (orderableUnit.r() == UnitTypeEnum.landFactory && orderableUnit.unitTransportTarget == null) {
                return true;
            }
            return false;
        }
    };
    static UnitSelectionFilter f = new UnitSelectionFilter() { // from class: com.corrodinggames.rts.gameFramework.f.al.6
        @Override // com.corrodinggames.rts.gameFramework.ui.UnitSelectionFilter
        public boolean a(OrderableUnit orderableUnit) {
            if (orderableUnit.r() == UnitTypeEnum.airFactory && orderableUnit.unitTransportTarget == null) {
                return true;
            }
            return false;
        }
    };

    public abstract boolean a(OrderableUnit orderableUnit);

    UnitSelectionFilter() {
    }

    public static void a(ArrayList arrayList, UnitSelectionFilter panelsVar, UnitSelectionFilter panelsVar2) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.gameUI.getSelectedUnitCount() != 1) {
            arrayList.clear();
        }
        OrderableUnit firstControllableSelectedUnit = gameEngine.gameUI.getFirstControllableSelectedUnit();
        if (firstControllableSelectedUnit != null) {
            if (panelsVar.a(firstControllableSelectedUnit) || (panelsVar2 != null && panelsVar2.a(firstControllableSelectedUnit))) {
                if (!arrayList.contains(firstControllableSelectedUnit)) {
                    arrayList.add(firstControllableSelectedUnit);
                }
            } else {
                arrayList.clear();
            }
        }
        OrderableUnit orderableUnitA = a(arrayList, panelsVar);
        if (orderableUnitA == null && panelsVar2 != null) {
            orderableUnitA = a(arrayList, panelsVar2);
        }
        if (orderableUnitA == null) {
            arrayList.clear();
            if (firstControllableSelectedUnit != null) {
                arrayList.add(firstControllableSelectedUnit);
            }
            orderableUnitA = a(arrayList, panelsVar);
            if (orderableUnitA == null && panelsVar2 != null) {
                orderableUnitA = a(arrayList, panelsVar2);
            }
        }
        if (orderableUnitA != null) {
            gameEngine.gameUI.clearSelection();
            gameEngine.gameUI.selectUnit(orderableUnitA);
            gameEngine.centerViewpoint(orderableUnitA.posX, orderableUnitA.posY);
            arrayList.add(orderableUnitA);
        }
    }

    public static OrderableUnit a(ArrayList arrayList, UnitSelectionFilter panelsVar) {
        GameEngine gameEngine = GameEngine.getInstance();
        OrderableUnit orderableUnit = null;
        float f2 = -1.0f;
        for (BaseUnit baseUnit : BaseUnit.bE) {
            if (baseUnit instanceof OrderableUnit) {
                OrderableUnit orderableUnit2 = (OrderableUnit) baseUnit;
                if (gameEngine.gameUI.canControlUnit(orderableUnit2) && panelsVar.a(orderableUnit2) && !arrayList.contains(orderableUnit2)) {
                    float fDistanceSq = Utility.distanceSq(gameEngine.viewpointX + gameEngine.halfVisibleWorldWidth, gameEngine.viewpointY + gameEngine.halfVisibleWorldHeight, orderableUnit2.posX, orderableUnit2.posY);
                    if (orderableUnit == null || fDistanceSq < f2) {
                        f2 = fDistanceSq;
                        orderableUnit = orderableUnit2;
                    }
                }
            }
        }
        return orderableUnit;
    }
}
