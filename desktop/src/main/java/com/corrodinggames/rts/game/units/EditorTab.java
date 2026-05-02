package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.gameFramework.GameEngine;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/n.class */
public enum EditorTab {
    all { // from class: com.corrodinggames.rts.game.units.n.1
        @Override // com.corrodinggames.rts.game.units.EditorTab
        public boolean a(UnitType unitType) {
            return true;
        }
    },
    types { // from class: com.corrodinggames.rts.game.units.n.2
        @Override // com.corrodinggames.rts.game.units.EditorTab
        public boolean a(UnitType unitType) {
            EditorOrBuilder editorOrBuilderL = EditorOrBuilder.L();
            if (editorOrBuilderL != null && editorOrBuilderL.F != null) {
                return editorOrBuilderL.F.a(unitType);
            }
            return false;
        }
    },
    terrain { // from class: com.corrodinggames.rts.game.units.n.3
        @Override // com.corrodinggames.rts.game.units.EditorTab
        public boolean a(UnitType unitType) {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.EditorTab
        public boolean b() {
            return false;
        }
    },
    modded { // from class: com.corrodinggames.rts.game.units.n.4
        @Override // com.corrodinggames.rts.game.units.EditorTab
        public boolean a(UnitType unitType) {
            if (unitType != null && (unitType instanceof CustomUnitConfig)) {
                CustomUnitConfig customUnitConfig = (CustomUnitConfig) unitType;
                if (customUnitConfig.modInfo == null) {
                    return false;
                }
                EditorOrBuilder editorOrBuilderL = EditorOrBuilder.L();
                if (editorOrBuilderL != null && editorOrBuilderL.E != null && customUnitConfig.modInfo != editorOrBuilderL.E) {
                    return false;
                }
                return true;
            }
            return false;
        }
    },
    search { // from class: com.corrodinggames.rts.game.units.n.5
        @Override // com.corrodinggames.rts.game.units.EditorTab
        public boolean a(UnitType unitType) {
            EditorOrBuilder editorOrBuilderL = EditorOrBuilder.L();
            if (editorOrBuilderL == null || editorOrBuilderL.H == null) {
                return false;
            }
            if (editorOrBuilderL.I) {
                editorOrBuilderL.I = false;
                editorOrBuilderL.J = editorOrBuilderL.H.toLowerCase().trim();
            }
            if (unitType == null) {
                return false;
            }
            if (unitType.getUnitTypeDescriptionShort() != null && unitType.getUnitTypeDescriptionShort().toLowerCase(Locale.ROOT).contains(editorOrBuilderL.J)) {
                return true;
            }
            if (unitType.getUnitTypeDescriptionShort() != null && unitType.getUnitName().toLowerCase(Locale.ROOT).contains(editorOrBuilderL.J)) {
                return true;
            }
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.EditorTab
        public boolean b() {
            EditorOrBuilder editorOrBuilderL = EditorOrBuilder.L();
            return (editorOrBuilderL == null || editorOrBuilderL.H == null) ? false : true;
        }
    },
    actions { // from class: com.corrodinggames.rts.game.units.n.6
        @Override // com.corrodinggames.rts.game.units.EditorTab
        public boolean a(UnitType unitType) {
            return unitType == null;
        }
    };

    public abstract boolean a(UnitType unitType);

    public String a() {
        return name();
    }

    public boolean b() {
        return true;
    }

    public EditorTab a(boolean z) {
        if (!z) {
            return a(1, 0);
        }
        return a(-1, 0);
    }

    public EditorTab a(int i, int i2) {
        int iOrdinal = (ordinal() + i) % values().length;
        if (iOrdinal < 0) {
            iOrdinal += values().length;
        }
        EditorTab editorTabA = values()[iOrdinal];
        if (!editorTabA.b()) {
            if (i2 > 30) {
                GameEngine.log("jumpBy recursion limit hit");
                return editorTabA;
            }
            editorTabA = editorTabA.a(i, i2 + 1);
        }
        return editorTabA;
    }
}
