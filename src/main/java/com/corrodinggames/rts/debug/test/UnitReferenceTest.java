package com.corrodinggames.rts.debug.test;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logicBooleans.BooleanParseException;
import com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/m.class */
public class UnitReferenceTest extends Test {
    /* JADX INFO: renamed from: a */
    public void runUnitReferenceTests() {
        GameEngine.isInSpace("Unit Reference tests");
        CustomUnitConfig customUnitConfig = CustomUnitConfig.instance;
        OrderableUnit orderableUnitA = CustomUnitConfig.a(false, customUnitConfig);
        orderableUnitA.setUnitTeam(PlayerTeam.TEAM_ALL);
        CustomUnit customUnitA = CustomUnitConfig.a(false, customUnitConfig);
        customUnitA.setUnitTeam(PlayerTeam.TEAM_ALL);
        customUnitA.posX = 2.0f;
        CustomUnit customUnitA2 = CustomUnitConfig.a(false, customUnitConfig);
        customUnitA2.setUnitTeam(PlayerTeam.TEAM_ALL);
        customUnitA2.posX = 3.0f;
        CustomUnit customUnitA3 = CustomUnitConfig.a(false, customUnitConfig);
        customUnitA3.setUnitTeam(PlayerTeam.TEAM_ALL);
        customUnitA3.posX = 3.0f;
        customUnitA.loadTransportedUnit(customUnitA2);
        customUnitA.loadTransportedUnit(customUnitA3);
        CustomUnit customUnitA4 = CustomUnitConfig.a(false, customUnitConfig);
        customUnitA4.setUnitTeam(PlayerTeam.TEAM_ALL);
        BaseUnit baseUnitA = CustomUnitConfig.a(false, customUnitConfig);
        baseUnitA.setUnitTeam(PlayerTeam.TEAM_ALL);
        BaseUnit baseUnitA2 = CustomUnitConfig.a(false, customUnitConfig);
        baseUnitA2.setUnitTeam(PlayerTeam.TEAM_ALL);
        orderableUnitA.unitTarget2 = customUnitA4;
        customUnitA4.unitTarget3 = baseUnitA;
        customUnitA2.unitTarget3 = baseUnitA;
        customUnitA.unitTarget2 = baseUnitA2;
        GameEngine.isInSpace("=== unit reference tests == (runs:2)");
        Long lValueOf = Long.valueOf(PerformanceProfiler.a());
        for (int i = 0; i < 2; i++) {
            assertReferenceResolvesTo(orderableUnitA, createReference("self"), orderableUnitA);
            assertReferenceResolvesTo(orderableUnitA, createReference("self.parent"), null);
            assertCreateError("self.unknown", true);
            assertReferenceResolvesTo(orderableUnitA, createReference("self.parent"), null);
            assertReferenceResolvesTo(orderableUnitA, createReference("nullUnit"), null);
            assertReferenceResolvesTo(orderableUnitA, createReference("self.customTarget1"), customUnitA4);
            assertReferenceResolvesTo(orderableUnitA, createReference("self.customTarget1.customTarget2"), baseUnitA);
            assertReferenceResolvesTo(orderableUnitA, createReference("self.customTarget2"), null);
            assertReferenceResolvesTo(orderableUnitA, createReference("self.nullUnit"), null);
            assertReferenceResolvesTo(orderableUnitA, createReference("nullUnit.nullUnit"), null);
            assertReferenceResolvesTo(customUnitA2, createReference("self.parent.customTarget1"), baseUnitA2);
            assertReferenceResolvesTo(customUnitA, createReference("self.transporting(slot=0)"), customUnitA2);
            assertReferenceResolvesTo(customUnitA, createReference("self.transporting(SLOT=0)"), customUnitA2);
            assertCreateError("self.transporting(MISS=0)", true);
            assertReferenceResolvesTo(customUnitA, createReference("self.transporting(slot=3)"), null);
            assertReferenceResolvesTo(customUnitA, createReference("self.transporting"), customUnitA2);
            assertReferenceResolvesTo(customUnitA, createReference("self.transporting(slot=0).customTarget2"), baseUnitA);
            assertReferenceResolvesTo(customUnitA, createReference("self.self.transporting(slot=0).customTarget2"), baseUnitA);
            assertReferenceResolvesTo(customUnitA, createReference("self.SELF.TRANsporting(slot=0).customTarget2"), baseUnitA);
            assertReferenceResolvesTo(customUnitA, createReference("self.SELF.transporting(slot=0).customTarget2"), baseUnitA);
            getUnitFromReference(orderableUnitA, createReference("self.nearestUnit(withinRange=500, withTag='test', relation='any')"));
            assertCreateError(VariableScope.nullOrMissingString, true);
        }
        GameEngine.isInSpace("Took: " + PerformanceProfiler.a(lValueOf.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue()));
    }

    /* JADX INFO: renamed from: a */
    public void assertCreateError(String str, boolean z) {
        try {
            UnitReference.parseSingleUnitReferenceBlock(CustomUnitConfig.instance, str);
            throw new RuntimeException("assertCreateError got no error for: " + str);
        } catch (RuntimeException e) {
            if (e.getClass() != RuntimeException.class && e.getClass() != BooleanParseException.class) {
                throw new RuntimeException(e);
            }
            if (z) {
                GameEngine.isInSpace("(debug)assertCreateError: " + str + " expected-error:" + e.getMessage());
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public UnitReference createReference(String str) {
        try {
            UnitReference singleUnitReferenceBlock = UnitReference.parseSingleUnitReferenceBlock(CustomUnitConfig.instance, str);
            if (singleUnitReferenceBlock == null) {
                throw new RuntimeException("Null when parsing [" + str + "]");
            }
            return singleUnitReferenceBlock;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error: " + e.getMessage() + " parsing [" + str + "]", e);
        }
    }

    /* JADX INFO: renamed from: a */
    public void assertReferenceResolvesTo(OrderableUnit orderableUnit, UnitReference unitReference, BaseUnit baseUnit) {
        BaseUnit baseUnit2 = unitReference.get(orderableUnit);
        if (baseUnit2 != baseUnit) {
            throw new RuntimeException("assertSame type expected:" + BaseUnit.serialize(baseUnit) + " got: " + BaseUnit.serialize(baseUnit2));
        }
    }

    /* JADX INFO: renamed from: a */
    public void getUnitFromReference(OrderableUnit orderableUnit, UnitReference unitReference) {
        unitReference.get(orderableUnit);
    }
}
