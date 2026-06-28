package com.corrodinggames.rts.debug.test;

import com.corrodinggames.rts.game.units.PathfindingUtils;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.CustomUnitAnimationTags;
import com.corrodinggames.rts.game.units.custom.condition.DigitGroupingStyle;
import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import io.github.rwx.geometry.Point;
import io.github.rwx.geometry.PointF;
import io.github.rwx.geometry.Rect;

import java.util.Iterator;
import java.util.Random;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/f.class */
public class PerformanceTests extends Test {

    /* JADX INFO: renamed from: b */
    int testCounter;

    /* JADX INFO: renamed from: e */
    static final Point testPoint = new Point();

    /* JADX INFO: renamed from: a */
    int assertionCounter = 1;

    /* JADX INFO: renamed from: c */
    final Rect testRect = new Rect();

    /* JADX INFO: renamed from: d */
    final PointF testPointF = new PointF();

    /* JADX INFO: renamed from: a */
    public void runPerformanceTests() {
        GameEngine.log("Misc Performance test");
        int i = 0;
        GameEngine.log("=== applyDigitGroupingStyle tests (runs:5)");
        Long lValueOf = Long.valueOf(PerformanceProfiler.a());
        for (int i2 = 0; i2 < 5; i2++) {
            for (int i3 = 0; i3 < 100; i3++) {
                if (!Resource.a(i3 + "9870000001.67", DigitGroupingStyle.comma).equals(VariableScope.nullOrMissingString)) {
                    i++;
                }
            }
        }
        double dA = PerformanceProfiler.a(lValueOf.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
        this.assertionCounter += i;
        GameEngine.log("Took: " + dA);
        int i4 = 0;
        GameEngine.log("=== applyDigitGroupingStyle_systemLibraryVersion tests (runs:5)");
        Long lValueOf2 = Long.valueOf(PerformanceProfiler.a());
        for (int i5 = 0; i5 < 5; i5++) {
            for (int i6 = 0; i6 < 100; i6++) {
                if (!Resource.a(((long) i6) + 9870000001L, DigitGroupingStyle.comma).equals(VariableScope.nullOrMissingString)) {
                    i4++;
                }
            }
        }
        double dA2 = PerformanceProfiler.a(lValueOf2.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
        this.assertionCounter += i4;
        GameEngine.log("Took: " + dA2);
        int i7 = 0;
        GameEngine.log("=== isLineClear tests (runs:3)");
        Long lValueOf3 = Long.valueOf(PerformanceProfiler.a());
        for (int i8 = 0; i8 < 3; i8++) {
            for (int i9 = 0; i9 < 100; i9++) {
                if (PathfindingUtils.canReachTargetPrepared(UnitMovementType.LAND, i9, 1000 - i9, 50, 50, 1000, 1, 1)) {
                    i7++;
                }
            }
        }
        double dA3 = PerformanceProfiler.a(lValueOf3.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
        this.assertionCounter += i7;
        GameEngine.log("Took: " + dA3);
        int i10 = 0;
        GameEngine.log("=== maths tests == (runs:3)");
        Long lValueOf4 = Long.valueOf(PerformanceProfiler.a());
        for (int i11 = 0; i11 < 3; i11++) {
            for (int i12 = 0; i12 < 1000; i12++) {
                Point point = testPoint;
                point.worldX += i12;
                point.worldX += i12;
                point.worldX += i12;
                point.worldX += i12;
                point.worldX += i12;
                point.worldX += i12;
                point.worldX += i12;
                point.worldX += i12;
                point.worldX += i12;
                this.testCounter++;
                i10 += 0;
            }
        }
        double dA4 = PerformanceProfiler.a(lValueOf4.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
        this.assertionCounter += i10;
        GameEngine.log("Took: " + dA4);
        FastArrayList fastArrayList = new FastArrayList();
        int i13 = 0;
        for (int i14 = 0; i14 < 20000; i14++) {
            CustomUnitAnimationTags customUnitAnimationTags = new CustomUnitAnimationTags();
            if (i14 % 10 != 0) {
                customUnitAnimationTags.a(AnimationTag.c("test"));
                customUnitAnimationTags.a(AnimationTag.c("test1"));
            }
            if (i14 % 2 == 0) {
                customUnitAnimationTags.a(AnimationTag.c("test2"));
                i13++;
            }
            if (i14 % 3 == 0) {
                customUnitAnimationTags.a(AnimationTag.c("test3"));
            }
            if (i14 % 4 == 0) {
                customUnitAnimationTags.a(AnimationTag.c("test4"));
            }
            if (i14 % 5 == 0) {
                fastArrayList.add(null);
            }
            fastArrayList.add(customUnitAnimationTags.a());
        }
        AnimationSet animationSetA = AnimationTag.a("test2");
        GameEngine.log("=== CustomTagList tests == (runs:5)");
        for (int i15 = 0; i15 < 14; i15++) {
            Long lValueOf5 = Long.valueOf(PerformanceProfiler.a());
            for (int i16 = 0; i16 < 5; i16++) {
                int i17 = 0;
                Iterator it = fastArrayList.iterator();
                while (it.hasNext()) {
                    if (AnimationTag.a(animationSetA, (AnimationSet) it.next())) {
                        i17++;
                    }
                }
                Assert.assertEquals(i13, i17);
            }
            GameEngine.log("test2Expected:" + i13);
            double dA5 = PerformanceProfiler.a(lValueOf5.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
            this.assertionCounter += 0;
            GameEngine.log("Took: " + dA5);
        }
        for (int i18 = 0; i18 < 2; i18++) {
            GameEngine.log("=== [Write]/comparison tests == (runs:5)");
            for (int i19 = 0; i19 < 5; i19++) {
                Random random = new Random();
                TestDataContainer[] testDataContainerArr = new TestDataContainer[5000000];
                for (int i20 = 0; i20 < testDataContainerArr.length; i20++) {
                    testDataContainerArr[i20] = new TestDataContainer();
                    testDataContainerArr[i20].booleanValue = random.nextFloat() < 0.5f;
                }
                Long lValueOf6 = Long.valueOf(PerformanceProfiler.a());
                for (int i21 = 0; i21 < 5; i21++) {
                    for (TestDataContainer testDataContainer : testDataContainerArr) {
                        testDataContainer.booleanValue = false;
                    }
                }
                double dA6 = PerformanceProfiler.a(lValueOf6.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
                this.assertionCounter += 0;
                GameEngine.log("Took: " + dA6);
            }
            GameEngine.log("=== Write/[comparison] tests == (runs:5)");
            for (int i22 = 0; i22 < 5; i22++) {
                Random random2 = new Random();
                TestDataContainer[] testDataContainerArr2 = new TestDataContainer[5000000];
                for (int i23 = 0; i23 < testDataContainerArr2.length; i23++) {
                    testDataContainerArr2[i23] = new TestDataContainer();
                    testDataContainerArr2[i23].booleanValue = random2.nextFloat() < 0.5f;
                }
                Long lValueOf7 = Long.valueOf(PerformanceProfiler.a());
                for (int i24 = 0; i24 < 5; i24++) {
                    for (TestDataContainer testDataContainer2 : testDataContainerArr2) {
                        if (testDataContainer2.booleanValue) {
                            testDataContainer2.booleanValue = false;
                        }
                    }
                }
                double dA7 = PerformanceProfiler.a(lValueOf7.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
                this.assertionCounter += 0;
                GameEngine.log("Took: " + dA7);
            }
        }
        int i25 = 0;
        GameEngine.log("=== [Virtual method]/if tests == (runs:5)");
        for (int i26 = 0; i26 < 7; i26++) {
            Random random3 = new Random();
            VirtualTest[] virtualTestArr = new VirtualTest[1000];
            for (int i27 = 0; i27 < virtualTestArr.length; i27++) {
                if (random3.nextFloat() < 0.3f) {
                    VirtualTestChild virtualTestChild = new VirtualTestChild(this);
                    virtualTestChild.value = random3.nextInt(1000);
                    virtualTestArr[i27] = virtualTestChild;
                } else {
                    virtualTestArr[i27] = new VirtualTest(this);
                    virtualTestArr[i27].value = random3.nextInt(1000);
                }
            }
            Long lValueOf8 = Long.valueOf(PerformanceProfiler.a());
            for (int i28 = 0; i28 < 5; i28++) {
                for (VirtualTest virtualTest : virtualTestArr) {
                    if (virtualTest.getValue() == 0) {
                        i25++;
                    }
                }
            }
            double dA8 = PerformanceProfiler.a(lValueOf8.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
            this.assertionCounter += i25;
            GameEngine.log("Took: " + dA8);
        }
        int i29 = 0;
        GameEngine.log("=== Virtual method/[if tests] == (runs:5)");
        for (int i30 = 0; i30 < 7; i30++) {
            Random random4 = new Random();
            TestData[] testDataArr = new TestData[1000];
            for (int i31 = 0; i31 < testDataArr.length; i31++) {
                boolean z = random4.nextFloat() < 0.3f;
                TestData testData = new TestData(this);
                testData.valueB = random4.nextInt(1000);
                testData.valueA = random4.nextInt(1000);
                testData.useValueB = z;
                testDataArr[i31] = testData;
            }
            Long lValueOf9 = Long.valueOf(PerformanceProfiler.a());
            for (int i32 = 0; i32 < 5; i32++) {
                for (TestData testData2 : testDataArr) {
                    if (testData2.getValue() == 0) {
                        i29++;
                    }
                }
            }
            double dA9 = PerformanceProfiler.a(lValueOf9.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
            this.assertionCounter += i29;
            GameEngine.log("Took: " + dA9);
        }
        int i33 = 0;
        GameEngine.log("=== comparison tests 1 == (runs:10)");
        for (int i34 = 0; i34 < 14; i34++) {
            Random random5 = new Random();
            float[] fArr = new float[600 * 600];
            for (int i35 = 0; i35 < 600; i35++) {
                for (int i36 = 0; i36 < 600; i36++) {
                    fArr[(i35 * 600) + i36] = random5.nextFloat();
                }
            }
            Long lValueOf10 = Long.valueOf(PerformanceProfiler.a());
            for (int i37 = 0; i37 < 10; i37++) {
                for (int i38 = 0; i38 < 600; i38++) {
                    for (int i39 = 0; i39 < 600; i39++) {
                        i33 = (int) (i33 + fArr[(i38 * 600) + i39]);
                    }
                }
            }
            double dA10 = PerformanceProfiler.a(lValueOf10.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
            this.assertionCounter += i33;
            GameEngine.log("Took: " + dA10);
        }
        int i40 = 0;
        GameEngine.log("=== comparison tests 2 == (runs:10)");
        for (int i41 = 0; i41 < 14; i41++) {
            Random random6 = new Random();
            float[][] fArr2 = new float[600][600];
            for (int i42 = 0; i42 < 600; i42++) {
                for (int i43 = 0; i43 < 600; i43++) {
                    fArr2[i42][i43] = random6.nextFloat();
                }
            }
            Long lValueOf11 = Long.valueOf(PerformanceProfiler.a());
            for (int i44 = 0; i44 < 10; i44++) {
                for (int i45 = 0; i45 < 600; i45++) {
                    for (int i46 = 0; i46 < 600; i46++) {
                        i40 = (int) (i40 + fArr2[i45][i46]);
                    }
                }
            }
            double dA11 = PerformanceProfiler.a(lValueOf11.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
            this.assertionCounter += i40;
            GameEngine.log("Took: " + dA11);
        }
        int i47 = 0;
        GameEngine.log("=== [divide]/multiply float tests == (runs:5)");
        for (int i48 = 0; i48 < 5; i48++) {
            Random random7 = new Random();
            float[] fArr3 = new float[5000000];
            float[] fArr4 = new float[5000000];
            for (int i49 = 0; i49 < fArr3.length; i49++) {
                fArr3[i49] = random7.nextFloat();
                fArr4[i49] = random7.nextFloat();
            }
            Long lValueOf12 = Long.valueOf(PerformanceProfiler.a());
            for (int i50 = 0; i50 < 5; i50++) {
                for (int i51 = 0; i51 < fArr3.length; i51++) {
                    if (fArr3[i51] / fArr4[i51] == 0.0f) {
                        i47++;
                    }
                }
            }
            double dA12 = PerformanceProfiler.a(lValueOf12.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
            this.assertionCounter += i47;
            GameEngine.log("Took: " + dA12);
        }
        int i52 = 0;
        GameEngine.log("=== divide/[multiply] float tests == (runs:5)");
        for (int i53 = 0; i53 < 5; i53++) {
            Random random8 = new Random();
            float[] fArr5 = new float[5000000];
            float[] fArr6 = new float[5000000];
            for (int i54 = 0; i54 < fArr5.length; i54++) {
                fArr5[i54] = random8.nextFloat();
                fArr6[i54] = random8.nextFloat();
            }
            Long lValueOf13 = Long.valueOf(PerformanceProfiler.a());
            for (int i55 = 0; i55 < 5; i55++) {
                for (int i56 = 0; i56 < fArr5.length; i56++) {
                    if (fArr5[i56] * fArr6[i56] == 0.0f) {
                        i52++;
                    }
                }
            }
            double dA13 = PerformanceProfiler.a(lValueOf13.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
            this.assertionCounter += i52;
            GameEngine.log("Took: " + dA13);
        }
        int i57 = 0;
        GameEngine.log("=== [divide]/multiply int tests == (runs:5)");
        for (int i58 = 0; i58 < 5; i58++) {
            Random random9 = new Random();
            int[] iArr = new int[5000000];
            int[] iArr2 = new int[5000000];
            for (int i59 = 0; i59 < iArr.length; i59++) {
                iArr[i59] = random9.nextInt();
                iArr2[i59] = random9.nextInt();
            }
            Long lValueOf14 = Long.valueOf(PerformanceProfiler.a());
            for (int i60 = 0; i60 < 5; i60++) {
                for (int i61 = 0; i61 < iArr.length; i61++) {
                    if (iArr[i61] / iArr2[i61] == 0) {
                        i57++;
                    }
                }
            }
            double dA14 = PerformanceProfiler.a(lValueOf14.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
            this.assertionCounter += i57;
            GameEngine.log("Took: " + dA14);
        }
        int i62 = 0;
        GameEngine.log("=== [float cast and divide]/multiply int tests == (runs:5)");
        for (int i63 = 0; i63 < 5; i63++) {
            Random random10 = new Random();
            int[] iArr3 = new int[5000000];
            int[] iArr4 = new int[5000000];
            for (int i64 = 0; i64 < iArr3.length; i64++) {
                iArr3[i64] = random10.nextInt();
                iArr4[i64] = random10.nextInt();
            }
            Long lValueOf15 = Long.valueOf(PerformanceProfiler.a());
            for (int i65 = 0; i65 < 5; i65++) {
                for (int i66 = 0; i66 < iArr3.length; i66++) {
                    if (iArr3[i66] / iArr4[i66] == 0.0f) {
                        i62++;
                    }
                }
            }
            double dA15 = PerformanceProfiler.a(lValueOf15.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
            this.assertionCounter += i62;
            GameEngine.log("Took: " + dA15);
        }
        int i67 = 0;
        GameEngine.log("=== [mixed float and divide]/multiply int tests == (runs:5)");
        for (int i68 = 0; i68 < 5; i68++) {
            Random random11 = new Random();
            int[] iArr5 = new int[5000000];
            float[] fArr7 = new float[5000000];
            for (int i69 = 0; i69 < iArr5.length; i69++) {
                iArr5[i69] = random11.nextInt();
                fArr7[i69] = random11.nextFloat();
            }
            Long lValueOf16 = Long.valueOf(PerformanceProfiler.a());
            for (int i70 = 0; i70 < 5; i70++) {
                for (int i71 = 0; i71 < iArr5.length; i71++) {
                    if (iArr5[i71] / fArr7[i71] == 0.0f) {
                        i67++;
                    }
                }
            }
            double dA16 = PerformanceProfiler.a(lValueOf16.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
            this.assertionCounter += i67;
            GameEngine.log("Took: " + dA16);
        }
        int i72 = 0;
        GameEngine.log("=== divide/[multiply] int tests == (runs:5)");
        for (int i73 = 0; i73 < 5; i73++) {
            Random random12 = new Random();
            int[] iArr6 = new int[5000000];
            int[] iArr7 = new int[5000000];
            for (int i74 = 0; i74 < iArr6.length; i74++) {
                iArr6[i74] = random12.nextInt();
                iArr7[i74] = random12.nextInt();
            }
            Long lValueOf17 = Long.valueOf(PerformanceProfiler.a());
            for (int i75 = 0; i75 < 5; i75++) {
                for (int i76 = 0; i76 < iArr6.length; i76++) {
                    if (iArr6[i76] * iArr7[i76] == 0) {
                        i72++;
                    }
                }
            }
            double dA17 = PerformanceProfiler.a(lValueOf17.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue());
            this.assertionCounter += i72;
            GameEngine.log("Took: " + dA17);
        }
    }
}
