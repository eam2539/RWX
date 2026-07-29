package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bi */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bi.class */
public class UnitSpawnList {
    FastArrayList<UnitSpawnEntry> a;
    public static final ProjectileSpawnCallback b = new ProjectileSpawnCallback();

    public static UnitSpawnList a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, UnitSpawnList unitSpawnList) throws ConfigParseException {
        String string = iniFile.getString(str, str2, null);
        if (string == null) {
            return unitSpawnList;
        }
        return a(customUnitConfig, string, str, str2, false);
    }

    public static UnitSpawnList a(CustomUnitConfig customUnitConfig, String str, String str2, String str3, boolean z) throws ConfigParseException {
        if (customUnitConfig == null) {
            throw new RuntimeException("meta==null");
        }
        return b(customUnitConfig, str, str2, str3, z);
    }

    public static UnitSpawnList b(CustomUnitConfig customUnitConfig, String str, String str2, String str3, boolean z) throws ConfigParseException {
        int iA;
        UnitSpawnList unitSpawnList = new UnitSpawnList();
        if (str == null || VariableScope.nullOrMissingString.equals(str) || "NONE".equalsIgnoreCase(str)) {
            return unitSpawnList;
        }
        if (customUnitConfig == null) {
            throw new ConfigParseException("meta required");
        }
        Iterator it = StringUtils.a(str, ",", false).iterator();
        while (it.hasNext()) {
            String strTrim = ((String) it.next()).trim();
            if (!VariableScope.nullOrMissingString.equals(strTrim)) {
                String strTrim2 = null;
                if (strTrim.contains("(") && strTrim.contains(")")) {
                    String[] strArrSplit = strTrim.split("\\(");
                    if (strArrSplit.length != 2) {
                        throw new ConfigParseException("[" + str2 + "]" + str3 + " UnitList: Unexpected format for '" + strTrim + "' of " + str);
                    }
                    strTrim = strArrSplit[0];
                    strTrim2 = strArrSplit[1].trim();
                }
                String[] strArrSplit2 = strTrim.split("\\*");
                String str4 = strArrSplit2[0];
                int i = 1;
                if (strArrSplit2.length >= 2) {
                    i = Integer.parseInt(strArrSplit2[1]);
                }
                UnitSpawnEntry unitSpawnEntry = new UnitSpawnEntry(customUnitConfig.createProjectileReference(str4, str3, str2));
                if (unitSpawnList.a == null) {
                    unitSpawnList.a = new FastArrayList();
                }
                unitSpawnEntry.b = i;
                if (strTrim2 != null) {
                    if (!strTrim2.endsWith(")")) {
                        throw new ConfigParseException("[" + str2 + "]" + str3 + " UnitList: Expected ')' in '" + strTrim + "' of " + str);
                    }
                    for (String str5 : strTrim2.substring(0, strTrim2.length() - 1).split("\\,")) {
                        if (!str5.trim().equals(VariableScope.nullOrMissingString)) {
                            String[] strArrSplit3 = str5.split("\\=");
                            if (strArrSplit3.length != 2) {
                                throw new RuntimeException("[" + str2 + "]" + str3 + " UnitList: Unexpected key format for '" + strTrim + "' of " + str);
                            }
                            String strTrim3 = strArrSplit3[0].trim();
                            String strTrim4 = strArrSplit3[1].trim();
                            if (strTrim3.equalsIgnoreCase("spawnChance")) {
                                unitSpawnEntry.c = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("maxSpawnLimit")) {
                                unitSpawnEntry.d = IniFile.parseInt(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("recursionLimit")) {
                                unitSpawnEntry.n = IniFile.parseInt(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetX") || strTrim3.equalsIgnoreCase("xOffsetAbsolute")) {
                                unitSpawnEntry.e = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetY") || strTrim3.equalsIgnoreCase("yOffsetAbsolute")) {
                                unitSpawnEntry.f = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("xOffsetRelative")) {
                                unitSpawnEntry.i = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("yOffsetRelative")) {
                                unitSpawnEntry.j = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetRandomXY")) {
                                float f = IniFile.parseFloat(str2, str3, strTrim4);
                                unitSpawnEntry.k = f;
                                unitSpawnEntry.l = f;
                            } else if (strTrim3.equalsIgnoreCase("offsetRandomX")) {
                                unitSpawnEntry.k = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetRandomY")) {
                                unitSpawnEntry.l = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetHeight")) {
                                unitSpawnEntry.g = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetRandomDir")) {
                                unitSpawnEntry.m = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetDir")) {
                                unitSpawnEntry.h = IniFile.parseFloat(str2, str3, strTrim4);
                            } else {
                                throw new ConfigParseException("[" + str2 + "]" + str3 + " ProjectileList: Unknown parameter '" + strTrim3 + "' for '" + strTrim + "' of " + str);
                            }
                        }
                    }
                }
                unitSpawnList.a.add(unitSpawnEntry);
            }
        }
        if (z && (iA = unitSpawnList.a()) > 1) {
            throw new ConfigParseException("[" + str2 + "]" + str3 + " Too many units: " + iA + ", only single unit is allowed here");
        }
        return unitSpawnList;
    }

    public int a() {
        if (this.a == null || this.a.size() == 0) {
            return 0;
        }
        int i = 0;
        Iterator it = this.a.iterator();
        while (it.hasNext()) {
            i += ((UnitSpawnEntry) it.next()).b;
        }
        return i;
    }

    public void a(float f, float f2, float f3, float f4, BaseUnit baseUnit, FastArrayList fastArrayList, boolean z, int i, Projectile projectile, BaseUnit baseUnit2) {
        if (this.a == null || this.a.size() == 0) {
            return;
        }
        int i2 = 0;
        int i3 = 0;
        if (baseUnit == null) {
            GameEngine.log("projectile spawn At: Skipping, source unit required");
            return;
        }
        for (UnitSpawnEntry unitSpawnEntry : this.a) {
            CustomProjectileTemplate customProjectileTemplateF = unitSpawnEntry.a.f();
            if (customProjectileTemplateF == null) {
                GameEngine.log("projectile spawn At: Skipping, projectileType==null");
            } else {
                for (int i4 = 0; i4 < unitSpawnEntry.b; i4++) {
                    i3++;
                    if ((unitSpawnEntry.c >= 1.0f || Utility.getDeterministicRandomFloat(baseUnit, 0.0f, 1.0f, i3) <= unitSpawnEntry.c) && i2 < unitSpawnEntry.d && i <= unitSpawnEntry.n) {
                        float fCopyStream = f + unitSpawnEntry.e;
                        float fCopyStream2 = f2 + unitSpawnEntry.f;
                        float f5 = f3 + unitSpawnEntry.g;
                        float fCopyStream3 = f4 + unitSpawnEntry.h;
                        if (unitSpawnEntry.m != 0.0f) {
                            fCopyStream3 += Utility.getDeterministicRandomFloat(baseUnit, -unitSpawnEntry.m, unitSpawnEntry.m, (i3 * 4) + 3);
                        }
                        if (unitSpawnEntry.k != 0.0f) {
                            fCopyStream += Utility.getDeterministicRandomFloat(baseUnit, -unitSpawnEntry.k, unitSpawnEntry.k, (i3 * 2) + 1);
                        }
                        if (unitSpawnEntry.l != 0.0f) {
                            fCopyStream2 += Utility.getDeterministicRandomFloat(baseUnit, -unitSpawnEntry.l, unitSpawnEntry.l, (i3 * 3) + 2);
                        }
                        if (unitSpawnEntry.i != 0.0f || unitSpawnEntry.j != 0.0f) {
                            float fFastCos = Utility.fastCos(f4);
                            float fFastSin = Utility.fastSin(f4);
                            float f6 = unitSpawnEntry.i;
                            float f7 = unitSpawnEntry.j;
                            fCopyStream += (fFastCos * f7) - (fFastSin * f6);
                            fCopyStream2 += (fFastSin * f7) + (fFastCos * f6);
                        }
                        Projectile projectileA = CustomUnit.a(baseUnit, -1, customProjectileTemplateF, fCopyStream, fCopyStream2, f5, fCopyStream3);
                        projectileA.aD = i;
                        if (projectile != null && baseUnit != null) {
                            customProjectileTemplateF.a(baseUnit, projectileA, projectile.l, projectile.n, projectile.o, -1.0f);
                        }
                        a(projectileA, unitSpawnEntry, baseUnit, projectile, baseUnit2);
                        i2++;
                        if (fastArrayList != null) {
                            fastArrayList.add(projectileA);
                        }
                    }
                }
            }
        }
    }

    public void a(Projectile projectile, UnitSpawnEntry unitSpawnEntry, BaseUnit baseUnit, Projectile projectile2, BaseUnit baseUnit2) {
        GameEngine gameEngine = GameEngine.getInstance();
        b.a = projectile;
        b.b = unitSpawnEntry;
        b.c = baseUnit;
        b.d = projectile2;
        b.e = baseUnit2;
        gameEngine.unitSpatialIndex.a(projectile.posX, projectile.posY, 100.0f, null, 0.0f, b);
    }
}
