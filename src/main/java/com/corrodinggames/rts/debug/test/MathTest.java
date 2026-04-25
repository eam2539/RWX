package com.corrodinggames.rts.debug.test;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/d.class */
public class MathTest extends Test {

    /* JADX INFO: renamed from: a */
    int dummyValue;

    /* JADX INFO: renamed from: a */
    public void runTests() {
        GameEngine.isInSpace("Running unit tests - maths (v3)");
        Utility.fastAtan2(100.0f, 100.0f);
        Utility.fastAtan2(0.0f, 100.0f);
        Utility.fastAtan2(100.0f, 0.0f);
        Utility.fastAtan2(0.0f, -100.0f);
        Utility.fastAtan2(-100.0f, 0.0f);
        Utility.fastAtan2(0.0f, 0.0f);
        GameEngine.isInSpace("fast_atan2 - NaN");
        Utility.fastAtan2(Float.NaN, 0.0f);
        Utility.fastAtan2(0.0f, Float.NaN);
        Utility.fastAtan2(Float.NaN, Float.NaN);
        GameEngine.isInSpace("fast_atan2 - Max");
        Utility.fastAtan2(Float.MAX_VALUE, 0.0f);
        Utility.fastAtan2(Float.MIN_VALUE, 0.0f);
        Utility.fastAtan2(0.0f, Float.MAX_VALUE);
        Utility.fastAtan2(0.0f, Float.MIN_VALUE);
        GameEngine.isInSpace("fast_atan2 - NaN+Max");
        Utility.fastAtan2(Float.MAX_VALUE, Float.NaN);
        Utility.fastAtan2(Float.MIN_VALUE, Float.MAX_VALUE);
        Utility.fastAtan2(Float.MAX_VALUE, Float.MIN_VALUE);
        Utility.fastAtan2(900000.0f, 900000.0f);
        Utility.fastAtan2(3.4028236E33f, 3.4028236E33f);
        Utility.fastAtan2(3.4028236E34f, 3.4028236E34f);
        Utility.fastAtan2(3.4028234E35f, 3.4028234E35f);
        Utility.fastAtan2(3.4028236E36f, 3.4028236E36f);
        Utility.fastAtan2(3.4028235E37f, 3.4028235E37f);
        Utility.fastAtan2(Float.MAX_VALUE, Float.MAX_VALUE);
        GameEngine.isInSpace("fast_atan2 - max,max");
        Utility.fastAtan2(Float.MAX_VALUE, Float.MAX_VALUE);
        Utility.fastAtan2(Float.MIN_VALUE, Float.MIN_VALUE);
        GameEngine.isInSpace("cos/sin");
        Assert.assertEquals(Utility.fastCos(0.0f), 1.0f);
        Assert.assertEquals(Utility.fastCos(360.0f), 1.0f);
        Assert.assertEquals(Utility.fastCos(10800.0f), 1.0f);
        Assert.assertEquals(Utility.fastCos(45.0f), 0.70710677f);
        Assert.assertEquals(Utility.fastCos(90.0f), 0.0f);
        Assert.assertEquals(Utility.fastCos(450.0f), 0.0f);
        Assert.assertEquals(Utility.fastCos(10890.0f), 0.0f);
        Assert.assertEquals(Utility.fastSin(0.0f), 0.0f);
        Assert.assertEquals(Utility.fastSin(90.0f), 1.0f);
        Utility.fastCos(-999999.0f);
        Utility.fastCos(999999.0f);
        Utility.fastCos(Float.MAX_VALUE);
        Utility.fastCos(Float.MIN_VALUE);
        Utility.fastSin(Float.MAX_VALUE);
        Utility.fastSin(Float.MIN_VALUE);
        GameEngine.isInSpace("diff sin(0):  " + String.format("%.12f", Float.valueOf(Utility.fastSin(0.0f) - ((float) StrictMath.sin(0.0d)))));
        GameEngine.isInSpace("diff sin(45): " + String.format("%.12f", Float.valueOf(Utility.fastSin(45.0f) - ((float) StrictMath.sin(0.7853981633974483d)))));
        GameEngine.isInSpace("diff sin(90): " + String.format("%.12f", Float.valueOf(Utility.fastSin(90.0f) - ((float) StrictMath.sin(1.5707963267948966d)))));
        GameEngine.isInSpace("diff sin(180):" + String.format("%.12f", Float.valueOf(Utility.fastSin(180.0f) - ((float) StrictMath.sin(3.141592653589793d)))));
        GameEngine.isInSpace("diff sin(360):" + String.format("%.12f", Float.valueOf(Utility.fastSin(360.0f) - ((float) StrictMath.sin(6.283185307179586d)))));
        GameEngine.isInSpace("Testing squareroot");
        for (int i = 0; i < 1005; i++) {
            Assert.assertEquals(Utility.sqrt(i), Utility.round(Utility.sortRect(i)));
        }
        int i2 = 0;
        GameEngine.isInSpace("=== cos/sin tests (runs:5)");
        Long lValueOf = Long.valueOf(PerformanceProfiler.a());
        for (int i3 = 0; i3 < 5; i3++) {
            for (int i4 = 0; i4 < 2000; i4++) {
                if (Utility.fastCos(i4) == 0.0f) {
                    i2++;
                }
                if (Utility.fastSin(i4) == 0.0f) {
                    i2++;
                }
            }
        }
        double dA = PerformanceProfiler.a(lValueOf.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
        this.dummyValue += i2;
        GameEngine.isInSpace("Took: " + dA);
    }
}
