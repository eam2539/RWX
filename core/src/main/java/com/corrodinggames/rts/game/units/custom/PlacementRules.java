package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.be */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/be.class */
public final class PlacementRules {
    FastArrayList<AnimationTag> a = new FastArrayList();
    FastArrayList<UnitSearchRule> b = new FastArrayList();
    boolean c;
    boolean d;
    public static final UnitCountCallback e = new UnitCountCallback();

    public static PlacementRules a(CustomUnitConfig customUnitConfig, IniFile iniFile) throws ConfigParseException {
        PlacementRules placementRules = new PlacementRules();
        placementRules.b(customUnitConfig, iniFile);
        if (placementRules.b.size() == 0) {
            return null;
        }
        for (AnimationTag animationTag : placementRules.a) {
            if (animationTag != null) {
                int i = 0;
                UnitSearchRule unitSearchRule = null;
                for (UnitSearchRule unitSearchRule2 : placementRules.b) {
                    if (unitSearchRule2.b == animationTag) {
                        i++;
                        unitSearchRule = unitSearchRule2;
                    }
                }
                if (i == 1) {
                    customUnitConfig.logWarningToMod("[placementRule_" + unitSearchRule.a + "]anyRuleInGroup: No other rule with this same group name found");
                }
            }
        }
        return placementRules;
    }

    public void b(CustomUnitConfig customUnitConfig, IniFile iniFile) throws ConfigParseException {
        for (String str : iniFile.getSectionsStartingWith("placementRule_")) {
            String strSubstring = str.substring("placementRule_".length());
            UnitSearchRule unitSearchRule = new UnitSearchRule();
            unitSearchRule.a = strSubstring;
            unitSearchRule.a(customUnitConfig, iniFile, str);
            if (unitSearchRule.a()) {
                if (!this.a.contains(unitSearchRule.b)) {
                    this.a.add(unitSearchRule.b);
                }
                if (unitSearchRule.n) {
                    if (!unitSearchRule.p) {
                        this.c = true;
                    } else {
                        this.d = true;
                    }
                }
                this.b.add(unitSearchRule);
            }
        }
    }

    public String a(OrderableUnit orderableUnit, float f, float f2) {
        if (!this.c) {
            return null;
        }
        return b(orderableUnit, f, f2);
    }

    public String a(final OrderableUnit y, final int integer2, final int integer3) {
        if (!this.d) {
            return null;
        }
        final TileMap tileMap = GameEngine.getInstance().tileMap;
        tileMap.exportTmxWithUnits(integer2, integer3);
        return this.b(y, (float) tileMap.cursorTileX, (float) tileMap.cursorTileY);
    }

    public String b(OrderableUnit orderableUnit, float f, float f2) {
        boolean z;
        for (AnimationTag animationTag : this.a) {
            boolean z2 = false;
            boolean z3 = false;
            UnitSearchRule unitSearchRule = null;
            for (UnitSearchRule unitSearchRule2 : this.b) {
                if (unitSearchRule2.b == animationTag && unitSearchRule2.n) {
                    if (!a(orderableUnit, unitSearchRule2, f, f2)) {
                        if (unitSearchRule == null) {
                            unitSearchRule = unitSearchRule2;
                        }
                        z3 = true;
                    } else {
                        z2 = true;
                    }
                }
            }
            if (animationTag == null) {
                z = !z3;
            } else {
                z = z2;
            }
            if (!z && unitSearchRule != null) {
                if (unitSearchRule.o != null) {
                    return unitSearchRule.o.resolveText();
                }
                return "{0}";
            }
        }
        return null;
    }

    private static boolean a(OrderableUnit orderableUnit, UnitSearchRule unitSearchRule, float f, float f2) {
        e.a = f + unitSearchRule.g;
        e.b = f2 + unitSearchRule.h;
        e.c = unitSearchRule;
        e.d = 0;
        GameEngine.getInstance().unitSpatialIndex.a(e.a, e.b, unitSearchRule.e, orderableUnit, 0.0f, e);
        if (e.d >= unitSearchRule.k && e.d <= unitSearchRule.l) {
            return true;
        }
        return false;
    }
}
