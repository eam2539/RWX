package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/o.class */
public enum EditorUnitTypeFilter {
    land { // from class: com.corrodinggames.rts.game.units.o.1

        @Override // com.corrodinggames.rts.game.units.EditorUnitTypeFilter
        public boolean a(UnitType unitType) {
            if (unitType == null) {
                return false;
            }
            BaseUnit baseUnitCanAttack = BaseUnit.canAttack(unitType);
            return (baseUnitCanAttack.bO() || unitType.isBuildingUnit() || baseUnitCanAttack.getMovementType() == UnitMovementType.AIR || baseUnitCanAttack.getMovementType() == UnitMovementType.WATER) ? false : true;
        }
    },
    air { // from class: com.corrodinggames.rts.game.units.o.2

        @Override // com.corrodinggames.rts.game.units.EditorUnitTypeFilter
        public boolean a(UnitType unitType) {
            if (unitType == null) {
                return false;
            }
            BaseUnit baseUnitCanAttack = BaseUnit.canAttack(unitType);
            return (baseUnitCanAttack.bO() || unitType.isBuildingUnit() || baseUnitCanAttack.getMovementType() != UnitMovementType.AIR) ? false : true;
        }
    },
    sea { // from class: com.corrodinggames.rts.game.units.o.3

        @Override // com.corrodinggames.rts.game.units.EditorUnitTypeFilter
        public boolean a(UnitType unitType) {
            if (unitType == null) {
                return false;
            }
            BaseUnit baseUnitCanAttack = BaseUnit.canAttack(unitType);
            return (baseUnitCanAttack.bO() || unitType.isBuildingUnit() || baseUnitCanAttack.getMovementType() != UnitMovementType.WATER) ? false : true;
        }
    },
    buildings { // from class: com.corrodinggames.rts.game.units.o.4

        @Override // com.corrodinggames.rts.game.units.EditorUnitTypeFilter
        public boolean a(UnitType unitType) {
            return (unitType == null || BaseUnit.canAttack(unitType).bO() || !unitType.isBuildingUnit()) ? false : true;
        }
    },
    bio { // from class: com.corrodinggames.rts.game.units.o.5

        @Override // com.corrodinggames.rts.game.units.EditorUnitTypeFilter
        public boolean a(UnitType unitType) {
            if (unitType == null) {
                return false;
            }
            return BaseUnit.canAttack(unitType).bO();
        }
    };

    public abstract boolean a(UnitType unitType);

    public String a() {
        return name();
    }

    public EditorUnitTypeFilter a(boolean z) {
        if (!z) {
            return a(1, 0);
        }
        return a(-1, 0);
    }

    public EditorUnitTypeFilter a(int i, int i2) {
        int iOrdinal = (ordinal() + i) % values().length;
        if (iOrdinal < 0) {
            iOrdinal += values().length;
        }
        EditorUnitTypeFilter editorUnitTypeFilterA = values()[iOrdinal];
        if (!editorUnitTypeFilterA.b()) {
            if (i2 > 30) {
                GameEngine.log("jumpBy recursion limit hit");
                return editorUnitTypeFilterA;
            }
            editorUnitTypeFilterA = editorUnitTypeFilterA.a(i, i2 + 1);
        }
        return editorUnitTypeFilterA;
    }

    public boolean b() {
        return true;
    }
}
