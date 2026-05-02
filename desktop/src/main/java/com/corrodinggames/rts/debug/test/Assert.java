package com.corrodinggames.rts.debug.test;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/n.class */
public class Assert {
    /* JADX INFO: renamed from: b */
    public static void assertEqualsDebug(String str, String str2) {
        GameEngine.log("assertEqualDebug:'" + str + "' vs '" + str2 + "'");
        assertEquals(str, str2);
    }

    /* JADX INFO: renamed from: a */
    public static void assertTrue(boolean z) {
        if (!z) {
            throw new RuntimeException("Asset failed");
        }
    }

    /* JADX INFO: renamed from: b */
    public static void assertFalse(boolean z) {
        if (z) {
            throw new RuntimeException("Asset failed");
        }
    }

    /* JADX INFO: renamed from: a */
    public static void assertEquals(int i, int i2) {
        if (i != i2) {
            throw new RuntimeException("Asset failed (int):" + i + "!=" + i2);
        }
    }

    /* JADX INFO: renamed from: a */
    public static void assertEquals(float f, float f2) {
        if (Utility.abs(f - f2) > 0.001f) {
            throw new RuntimeException("Asset failed (float):" + f + "!=" + f2);
        }
    }

    /* JADX INFO: renamed from: a */
    public static void assertEquals(String str, String str2) {
        if (!str.equals(str2)) {
            throw new RuntimeException("Asset failed:" + str + "!=" + str2);
        }
    }

    /* JADX INFO: renamed from: a */
    public void runUnitTests() {
        GameEngine.log("Running unit tests");
        new LogicBooleanTest().runTests();
        new MathTest().runTests();
        new FastNodeQueueTest().runAllTests();
        new UtilityTests().runAllTests();
        new GameLogicTest().testTiming();
        new PerformanceTests().runPerformanceTests();
        new UnitReferenceTest().runUnitReferenceTests();
    }

    /* JADX INFO: renamed from: c */
    public static void assertEqualsFloat(String str, String str2) {
        assertEquals(Float.valueOf(Float.parseFloat(str)).floatValue(), Float.valueOf(Float.parseFloat(str2)).floatValue());
    }

    /* JADX INFO: renamed from: a */
    public static void assertEquals(Object obj, Object obj2) {
        if (obj != obj2) {
            throw new RuntimeException("Asset failed:" + obj + "!=" + obj2);
        }
    }
}
