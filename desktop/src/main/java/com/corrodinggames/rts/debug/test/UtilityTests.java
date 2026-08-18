package com.corrodinggames.rts.debug.test;

import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.game.units.custom.condition.DigitGroupingStyle;
import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;
import com.corrodinggames.rts.game.units.custom.variables.ExpressionEvaluator;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.CustomPathFileLoader;
import com.corrodinggames.rts.gameFramework.file.MergedFileLoader;
import com.corrodinggames.rts.gameFramework.mod.ModManager;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import com.corrodinggames.rts.gameFramework.utility.RwmodFileLoader;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/e.class */
public class UtilityTests extends Test {

    /* JADX INFO: renamed from: a */
    int dummyValue = 1;

    /* JADX INFO: renamed from: a */
    public void runAllTests() {
        GameEngine.log("separator regex test");
        "first".split(Pattern.quote(File.separator));
        Assert.assertEquals("first\\second".split(Pattern.quote("\\"))[0], "first");
        Assert.assertEquals("first/second".split(Pattern.quote("/"))[0], "first");
        GameEngine.log("Test for dis sq ranges");
        Assert.assertTrue(50 < 850000 * 850000);
        Assert.assertTrue(50.0f < ((float) (850000 * 850000)));
        Assert.assertTrue(50 < 8500000 * 8500000);
        Assert.assertTrue(50.0f < ((float) (8500000 * 8500000)));
        Random random = new Random();
        float[] fArr = new float[10000];
        for (int i = 1; i < 5; i++) {
            int iSqrt = 0;
            for (int i2 = 0; i2 < fArr.length; i2++) {
                fArr[i2] = random.nextFloat() * 50.0f;
            }
            long jA = PerformanceProfiler.a();
            for (int i3 = 0; i3 < 1000; i3++) {
                for (float f : fArr) {
                    iSqrt += Utility.fastSquareRootInt((int) f);
                }
            }
            GameEngine.log("sum:" + iSqrt);
            GameEngine.log("fastSquareRootInt took:" + PerformanceProfiler.a(PerformanceProfiler.a(jA)));
            for (int i4 = 0; i4 < fArr.length; i4++) {
                fArr[i4] = random.nextFloat() * 50.0f;
            }
            long jA2 = PerformanceProfiler.a();
            int iSortRect = 0;
            for (int i5 = 0; i5 < 1000; i5++) {
                for (float f2 : fArr) {
                    iSortRect = (int) (iSortRect + Utility.squareRoot((int) f2));
                }
            }
            GameEngine.log("sum:" + iSortRect);
            GameEngine.log("squareRoot took:" + PerformanceProfiler.a(PerformanceProfiler.a(jA2)));
        }
        GameEngine.log("CommonUtils.fastSplit");
        String[] strArrSplitByChar = Utility.splitByChar("testA|testB", '|');
        Assert.assertEquals(strArrSplitByChar.length, 2);
        Assert.assertEquals(strArrSplitByChar[0], "testA");
        Assert.assertEquals(strArrSplitByChar[1], "testB");
        String[] strArrSplitByChar2 = Utility.splitByChar("test|", '|');
        Assert.assertEquals(strArrSplitByChar2.length, "test|".split("\\|").length);
        Assert.assertEquals(strArrSplitByChar2[0], "test");
        String[] strArrSplitByChar3 = Utility.splitByChar("|test", '|');
        Assert.assertEquals(strArrSplitByChar3.length, 2);
        Assert.assertEquals(strArrSplitByChar3[0], VariableScope.nullOrMissingString);
        Assert.assertEquals(strArrSplitByChar3[1], "test");
        Assert.assertEquals(Utility.splitByChar("|", '|').length, 0);
        GameEngine.log("VariableReplacement");
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();
        expressionEvaluator.b.a("a", "5");
        expressionEvaluator.b.a("b", "10");
        expressionEvaluator.b.a("abc_foo", "7");
        CustomUnitConfig customUnitConfig = CustomUnitConfig.instance;
        try {
            IniFile iniFile = new IniFile("assets/" + customUnitConfig.configPath);
            try {
                Assert.assertEquals(expressionEvaluator.a(customUnitConfig, iniFile, "core", "123"), "123");
                Assert.assertEquals(expressionEvaluator.a(customUnitConfig, iniFile, "core", "1+1"), "2");
                Assert.assertEquals(expressionEvaluator.a(customUnitConfig, iniFile, "core", "(1+1)*2"), "4");
                Assert.assertEquals(expressionEvaluator.a(customUnitConfig, iniFile, "core", "a+b"), "15");
                Assert.assertEquals(expressionEvaluator.a(customUnitConfig, iniFile, "core", "a+a+abc_foo"), "17");
                Assert.assertEquals(expressionEvaluator.a(customUnitConfig, iniFile, "core", "(2+2)*(2+2)"), "16");
                Assert.assertEquals(expressionEvaluator.a(customUnitConfig, iniFile, "core", "10/5"), "2");
                Assert.assertEquals(expressionEvaluator.a(customUnitConfig, iniFile, "core", "10-5"), "5");
                Assert.assertEqualsFloat(expressionEvaluator.a(customUnitConfig, iniFile, "core", "cos(60)"), "0.5");
                Assert.assertEqualsFloat(expressionEvaluator.a(customUnitConfig, iniFile, "core", "sin(b+20+(2-2)+(5*0))"), "0.5");
                GameEngine.log("PassthroughZipReader");
                Assert.assertEquals(RwmodFileLoader.getPathInZip("/first/second/zip.rwmod/test1/test2"), "test1/test2");
                Assert.assertEquals(RwmodFileLoader.getPathInZip("\\first\\second\\zip.rwmod\\test1\\test2"), "test1/test2");
                assertKeyValueEquals("A", "B");
                assertKeyValueEquals("AA=11", "BB=22");
                assertKeyValueEquals("AA='11'", "BB='22'");
                assertKeyValueEquals("AA=(11)", "BB=22");
                assertKeyValueEquals("(AA)=(11)", "BB=22");
                assertKeyValueEquals("(AA)=('11')", "BB=22");
                assertKeyValueEquals("(AA)=('11')", "BB=((22))");
                assertKeyValueEquals("(A,A)=('1,1')", "BB=((2,2))");
                assertKeyValueEquals("(A,A)=('1,,1')", "BB=((2,2))");
                GameEngine.log("splitWithEscaping");
                assertStringArrayEquals(StringUtils.b("hello world", ' '), "hello", "world");
                assertStringArrayEquals(StringUtils.b("hello world", 'X'), "hello world");
                assertStringArrayEquals(StringUtils.b("hello,world", ','), "hello", "world");
                assertStringArrayEquals(StringUtils.b("he\\,llo,world", ','), "he,llo", "world");
                assertStringArrayEquals(VariableScope.nullOrMissingString.split(" "), VariableScope.nullOrMissingString);
                assertStringArrayEquals(StringUtils.b(VariableScope.nullOrMissingString, ' '), VariableScope.nullOrMissingString);
                assertStringArrayEquals(StringUtils.b("hello\\\\,World", ','), "hello\\", "World");
                assertStringArrayEquals(StringUtils.b("Hello\\A,world", ','), "HelloA", "world");
                assertStringArrayEquals(StringUtils.b("h\\ello\\,world", ','), "hello,world");
                assertStringArrayEquals(StringUtils.b("h\\ello\\,w,orld", ','), "hello,w", "orld");
                assertStringArrayEquals(StringUtils.b("h\\ello\\,w,orld", ','), "hello,w", "orld");
                Assert.assertEquals(StringUtils.a(new String[]{"Hello"}), "Hello");
                Assert.assertEquals(StringUtils.a(new String[]{"Hello", "World"}), "Hello,World");
                Assert.assertEquals(StringUtils.a(new String[]{"Hel,lo", "World"}), "Hel\\,lo,World");
                Assert.assertEquals(StringUtils.a(new String[]{"Hel,lo,", "Wor,ld"}), "Hel\\,lo\\,,Wor\\,ld");
                Assert.assertEquals(StringUtils.a(new String[]{"Hel\\,lo,", "Wor,ld"}), "Hel\\\\\\,lo\\,,Wor\\,ld");
                Assert.assertEquals(StringUtils.a(new String[]{"H\\el\\,lo,", "Wor,ld"}), "H\\\\el\\\\\\,lo\\,,Wor\\,ld");
                GameEngine.log("FileLoaderBackend");
                CustomPathFileLoader customPathFileLoader = new CustomPathFileLoader("/tmp/rustedWarfareTests/", "test1");
                customPathFileLoader.TAG = "fileLoader1: ";
                customPathFileLoader.debug = true;
                CustomPathFileLoader customPathFileLoader2 = new CustomPathFileLoader("/tmp/rustedWarfareTestsSec2/", "test2");
                customPathFileLoader2.TAG = "fileLoader2: ";
                customPathFileLoader2.debug = true;
                MergedFileLoader mergedFileLoader = new MergedFileLoader(customPathFileLoader, "primary-PATH/", customPathFileLoader2, "[ALT-PATH]/");
                mergedFileLoader.TAG = "mergedFileLoader: ";
                mergedFileLoader.debug = true;
                Assert.assertEqualsDebug(customPathFileLoader.convertAbstractPath("/SD/rustedWarfare/"), "/tmp/rustedWarfareTests/");
                Assert.assertEqualsDebug(customPathFileLoader.convertAbstractPath("/SD/rustedWarfare/maps/coolMap.tmx"), "/tmp/rustedWarfareTests/mods/maps/coolMap.tmx");
                Assert.assertEqualsDebug(customPathFileLoader.convertAbstractPath("/SD/rustedWarfare/maps/coolMap.tmx"), "/tmp/rustedWarfareTests/mods/maps/coolMap.tmx");
                Assert.assertEqualsDebug(customPathFileLoader.convertAbstractPath("units/test.ini"), "assets/units/test.ini");
                GameEngine.log("FileLoaderBackend - merged");
                Assert.assertEqualsDebug(mergedFileLoader.convertAbstractPath("/SD/rustedWarfare/"), "/tmp/rustedWarfareTests/");
                Assert.assertEqualsDebug(mergedFileLoader.convertAbstractPath("/SD/rustedWarfare/maps/coolMap.tmx"), "/tmp/rustedWarfareTests/mods/maps/coolMap.tmx");
                GameEngine.log("FileLoaderBackend - android fake");
                boolean z = GameEngine.isNonAndroidVersion;
                GameEngine.isNonAndroidVersion = false;
                try {
                    Assert.assertEqualsDebug(customPathFileLoader.convertAbstractPath("/SD/rustedWarfare/"), "/tmp/rustedWarfareTests/");
                    Assert.assertEqualsDebug(customPathFileLoader.convertAbstractPath("/SD/rustedWarfare/maps/coolMap.tmx"), "/tmp/rustedWarfareTests/maps/coolMap.tmx");
                    Assert.assertEqualsDebug(customPathFileLoader.convertAbstractPath("/SD/rustedWarfare/maps/coolMap.tmx"), "/tmp/rustedWarfareTests/maps/coolMap.tmx");
                    Assert.assertEqualsDebug(customPathFileLoader.convertAbstractPath("units/test.ini"), "units/test.ini");
                    GameEngine.isNonAndroidVersion = z;
                    if (1 != 0) {
                        GameEngine.log("FileLoaderBackend - real file tests");
                        File file = new File(customPathFileLoader.convertAbstractPath("/SD/rustedWarfare/testDir"));
                        file.mkdirs();
                        new File(customPathFileLoader2.convertAbstractPath("/SD/rustedWarfare/testDir")).mkdirs();
                        try {
                            try {
                                File file2 = new File(customPathFileLoader.convertAbstractPath("/SD/rustedWarfare/testDir/map1.tmx"));
                                file2.createNewFile();
                                FileWriter fileWriter = new FileWriter(file2);
                                fileWriter.write("map1");
                                fileWriter.close();
                                new File(customPathFileLoader.convertAbstractPath("/SD/rustedWarfare/testDir/map2.tmx")).createNewFile();
                                File file3 = new File(customPathFileLoader2.convertAbstractPath("/SD/rustedWarfare/testDir/map3.tmx"));
                                file3.createNewFile();
                                FileWriter fileWriter2 = new FileWriter(file3);
                                fileWriter2.write("map3");
                                fileWriter2.close();
                                String[] strArrB = customPathFileLoader.listDir("/SD/rustedWarfare/testDir", false);
                                Assert.assertEquals(strArrB.length, 2);
                                Assert.assertEqualsDebug(strArrB[0], "map1.tmx");
                                Assert.assertEqualsDebug(strArrB[1], "map2.tmx");
                                String[] strArrB2 = mergedFileLoader.listDir("/SD/rustedWarfare/testDir", false);
                                Assert.assertEquals(strArrB2.length, 3);
                                Assert.assertEqualsDebug(strArrB2[0], "primary-PATH/map1.tmx");
                                Assert.assertEqualsDebug(strArrB2[1], "primary-PATH/map2.tmx");
                                Assert.assertEqualsDebug(strArrB2[2], "[ALT-PATH]/map3.tmx");
                                String str = strArrB2[2];
                                AssetInputStream assetInputStreamOpenAssetSteam = mergedFileLoader.openAssetSteam("/SD/rustedWarfare/testDir/" + str);
                                if (assetInputStreamOpenAssetSteam == null) {
                                    throw new RuntimeException("Null for: /SD/rustedWarfare/testDir/" + str);
                                }
                                Assert.assertEqualsDebug(Utility.readStreamToStringUtf8(assetInputStreamOpenAssetSteam), "map3");
                                GameEngine.log("FileLoaderBackend - clean up");
                                for (String str2 : file.list()) {
                                    new File(file.getPath(), str2).delete();
                                }
                                file.delete();
                                GameEngine.log("isSameOrHigherVersion..");
                                assertVersionComparison("v1.13", "v1.14", true);
                                assertVersionComparison("v1.13", "v2.14", true);
                                assertVersionComparison("v1.13", "v2.11", true);
                                assertVersionComparison("v1.13", "v1.13p5", false);
                                assertVersionComparison("v1.13", "v1.13.2", true);
                                assertVersionComparison("v1.13.2", "v1.13", false);
                                assertVersionComparison("v1.13", "v1.13b", true);
                                assertVersionComparison("v1.13", "v1.13.2p6", true);
                                assertVersionComparison("v1.13", "v1.14.2p6", true);
                                assertVersionComparison("v1.13p9", "v1.14.2p6", true);
                                assertVersionComparison("v1.13p9", "v1.14p6", true);
                                assertVersionComparison("v1.14p3", "v1.14p6", true);
                                assertVersionComparison("v1.14p3", "v1.14p6b", true);
                                assertVersionComparison("v1.14p8", "v1.14p3", false);
                                assertVersionComparison("v1", "v2", true);
                                assertVersionComparison("v1.5", "v2", true);
                                assertVersionComparison("v2", "v1.15", false);
                                assertVersionComparison("v2.0.5", "v1.15", false);
                                assertVersionComparison("v1.15", "v2.0.5", true);
                                assertVersionComparison("v1.15.6", "v2.0.5", true);
                                assertVersionComparison("v1.15.6", "v1.16.5", true);
                                assertVersionComparison("v1.13", "v1.13.2p5", true);
                                assertVersionComparison("v1.14", "v1.14p3", false);
                                assertVersionComparison("v1.14b", "v1.14p3", false);
                                assertVersionComparison("v1.14.2", "v1.14p3", false);
                                assertVersionComparison("v1.14.2b", "v1.14p3", false);
                                try {
                                    ModManager.checkMinVersion("v1.11p1");
                                    Assert.assertEquals(Resource.a("10000", DigitGroupingStyle.none), "10000");
                                    Assert.assertEquals(Resource.a(VariableScope.nullOrMissingString, DigitGroupingStyle.space), VariableScope.nullOrMissingString);
                                    Assert.assertEquals(Resource.a("1", DigitGroupingStyle.comma), "1");
                                    Assert.assertEquals(Resource.a("10", DigitGroupingStyle.comma), "10");
                                    Assert.assertEquals(Resource.a("100", DigitGroupingStyle.comma), "100");
                                    Assert.assertEquals(Resource.a("1000", DigitGroupingStyle.comma), "1,000");
                                    Assert.assertEquals(Resource.a(".", DigitGroupingStyle.comma), ".");
                                    Assert.assertEquals(Resource.a(".2", DigitGroupingStyle.comma), ".2");
                                    Assert.assertEquals(Resource.a(".22", DigitGroupingStyle.comma), ".22");
                                    Assert.assertEquals(Resource.a(".223", DigitGroupingStyle.comma), ".223");
                                    Assert.assertEquals(Resource.a(".2234", DigitGroupingStyle.comma), ".2234");
                                    Assert.assertEquals(Resource.a("100.2234", DigitGroupingStyle.comma), "100.2234");
                                    Assert.assertEquals(Resource.a("1000.2234", DigitGroupingStyle.comma), "1,000.2234");
                                    Assert.assertEquals(Resource.a("10000", DigitGroupingStyle.comma), "10,000");
                                    Assert.assertEquals(Resource.a("9800000", DigitGroupingStyle.comma), "9,800,000");
                                    Assert.assertEquals(Resource.a("9800000.67", DigitGroupingStyle.comma), "9,800,000.67");
                                    Assert.assertEquals(Resource.a("98000000.67", DigitGroupingStyle.comma), "98,000,000.67");
                                    Assert.assertEquals(Resource.a("980000000.67", DigitGroupingStyle.comma), "980,000,000.67");
                                    Assert.assertEquals(Resource.a("9800000001.67", DigitGroupingStyle.comma), "9,800,000,001.67");
                                    Assert.assertEquals(Resource.a("9800000001.6", DigitGroupingStyle.comma), "9,800,000,001.6");
                                    Assert.assertEquals(Resource.a("9800000001.", DigitGroupingStyle.comma), "9,800,000,001.");
                                    Assert.assertEquals(Resource.a("9800000001", DigitGroupingStyle.comma), "9,800,000,001");
                                    Assert.assertEquals(Resource.a(9800000L, DigitGroupingStyle.comma), "9,800,000");
                                } catch (ConfigParseException e) {
                                    throw new RuntimeException(e);
                                }
                            } catch (IOException e2) {
                                throw new RuntimeException(e2);
                            }
                        } catch (Throwable th) {
                            GameEngine.log("FileLoaderBackend - clean up");
                            for (String str3 : file.list()) {
                                new File(file.getPath(), str3).delete();
                            }
                            file.delete();
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    GameEngine.isNonAndroidVersion = z;
                    throw th2;
                }
            } catch (ConfigParseException e3) {
                throw new RuntimeException(e3);
            }
        } catch (IOException e4) {
            throw new RuntimeException(e4);
        }
    }

    /* JADX INFO: renamed from: a */
    public void assertKeyValueEquals(String str, String str2) {
        ArrayList arrayListA = StringUtils.a(str + "," + str2, ",", false, false);
        Assert.assertEquals((String) arrayListA.get(0), str);
        Assert.assertEquals((String) arrayListA.get(1), str2);
    }

    /* JADX INFO: renamed from: a */
    public void assertStringArrayEquals(String[] strArr, String str) {
        Assert.assertEquals(strArr.length, 1);
        Assert.assertEquals(strArr[0], str);
    }

    /* JADX INFO: renamed from: a */
    public void assertStringArrayEquals(String[] strArr, String str, String str2) {
        Assert.assertEquals(strArr.length, 2);
        Assert.assertEquals(strArr[0], str);
        Assert.assertEquals(strArr[1], str2);
    }

    /* JADX INFO: renamed from: a */
    public void assertVersionComparison(String str, String str2, boolean z) {
        boolean z2;
        try {
            ModManager.checkVersion(str, str2);
            z2 = true;
        } catch (ConfigParseException e) {
            if (z) {
                GameEngine.logColored(e.getMessage());
            }
            z2 = false;
        }
        if (z2 != z) {
            throw new RuntimeException("isSameOrHigherVersion(" + str + "," + str2 + "): Asset failed got: " + z2);
        }
    }
}
