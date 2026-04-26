package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.as */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/as.class */
public class UnitStats implements Cloneable {
    public boolean a;

    /* JADX INFO: renamed from: b */
    public float mass;

    /* JADX INFO: renamed from: c */
    public int maxHp;

    /* JADX INFO: renamed from: d */
    public float maxEnergy;

    /* JADX INFO: renamed from: e */
    public float shootDelayMultiplier = 1.0f;

    /* JADX INFO: renamed from: f */
    public float shootDamageMultiplier = 1.0f;

    /* JADX INFO: renamed from: g */
    public int maxShield;

    /* JADX INFO: renamed from: h */
    public float shieldRegen;

    /* JADX INFO: renamed from: i */
    public float maxAttackRange;

    /* JADX INFO: renamed from: j */
    public float moveSpeed;

    /* JADX INFO: renamed from: k */
    public float maxTurnSpeed;

    /* JADX INFO: renamed from: l */
    public float armour;
    public boolean isVisibleToEnemies;

    /* JADX INFO: renamed from: n */
    public int fogOfWarSightRange;

    /* JADX INFO: renamed from: o */
    public int nanoRange;

    /* JADX INFO: renamed from: p */
    public float selfRegenRate;

    /* JADX INFO: renamed from: q */
    public float targetHeight;

    /* JADX INFO: renamed from: r */
    public float nanoFactorySpeed;
    static LinkedHashMap<String,CustomUnitDataField> s = new LinkedHashMap();
    static LinkedHashMap<String,CustomUnitDataField> t;

    public UnitStats(boolean z) {
        this.a = z;
    }

    public static VariableScope.CachedWriter a(String str, CustomUnitConfig customUnitConfig, String str2, String str3) {
        try {
            return VariableScope.CachedWriter.create(str, new CustomDataWriterFactory(customUnitConfig));
        } catch (ConfigParseException e) {
            throw new RuntimeException("[" + str2 + "]" + str3 + ": " + e.getMessage(), e);
        }
    }

    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public UnitStats clone() {
        try {
            UnitStats unitStats = (UnitStats) super.clone();
            unitStats.a = false;
            return unitStats;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    static void a(LinkedHashMap linkedHashMap, CustomUnitDataField customUnitDataField) {
        linkedHashMap.put(customUnitDataField.b, customUnitDataField);
    }

    static {
        a(s, new UnitStatsDataField(s.size(), "mass") { // from class: com.corrodinggames.rts.game.units.custom.as.1
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.mass;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.mass = (float) d;
            }
        });
        a(s, new UnitStatsDataField(s.size(), "maxenergy") { // from class: com.corrodinggames.rts.game.units.custom.as.12
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.maxEnergy;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.maxEnergy = (float) d;
            }
        });
        a(s, new UnitDataField(s.size(), "energy") { // from class: com.corrodinggames.rts.game.units.custom.as.13
            @Override // com.corrodinggames.rts.game.units.custom.UnitDataField
            public double a(CustomUnit customUnit) {
                return customUnit.f0cB;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitDataField
            public void b(CustomUnit customUnit, double d) {
                customUnit.f0cB = (float) d;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitDataField, com.corrodinggames.rts.game.units.custom.CustomUnitDataField
            public void a(CustomUnit customUnit, double d) {
                super.a(customUnit, d);
                customUnit.f0cB = (float) d;
            }
        });
        a(s, new UnitStatsDataField(s.size(), "maxhp") { // from class: com.corrodinggames.rts.game.units.custom.as.14
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.maxHp;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.maxHp = (int) d;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField, com.corrodinggames.rts.game.units.custom.CustomUnitDataField
            public void a(CustomUnit customUnit, double d) {
                super.a(customUnit, d);
                customUnit.maxHealth = (float) d;
            }
        });
        a(s, new UnitDataField(s.size(), "hp") { // from class: com.corrodinggames.rts.game.units.custom.as.15
            @Override // com.corrodinggames.rts.game.units.custom.UnitDataField
            public double a(CustomUnit customUnit) {
                return customUnit.currentHealth;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitDataField
            public void b(CustomUnit customUnit, double d) {
                customUnit.currentHealth = (float) d;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitDataField, com.corrodinggames.rts.game.units.custom.CustomUnitDataField
            public void a(CustomUnit customUnit, double d) {
                super.a(customUnit, d);
                customUnit.o((float) d);
            }
        });
        a(s, new UnitStatsDataField(s.size(), "maxshield") { // from class: com.corrodinggames.rts.game.units.custom.as.16
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.maxShield;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.maxShield = (int) d;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField, com.corrodinggames.rts.game.units.custom.CustomUnitDataField
            public void a(CustomUnit customUnit, double d) {
                super.a(customUnit, d);
                customUnit.unitEnergyMax = (float) d;
            }
        });
        a(s, new UnitDataField(s.size(), "shield") { // from class: com.corrodinggames.rts.game.units.custom.as.17
            @Override // com.corrodinggames.rts.game.units.custom.UnitDataField
            public double a(CustomUnit customUnit) {
                return customUnit.shield;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitDataField
            public void b(CustomUnit customUnit, double d) {
                customUnit.shield = (float) d;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitDataField, com.corrodinggames.rts.game.units.custom.CustomUnitDataField
            public void a(CustomUnit customUnit, double d) {
                super.a(customUnit, d);
                customUnit.shield = (float) d;
            }
        });
        a(s, new UnitStatsDataField(s.size(), "shieldregen") { // from class: com.corrodinggames.rts.game.units.custom.as.18
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.shieldRegen;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.shieldRegen = (float) d;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField, com.corrodinggames.rts.game.units.custom.CustomUnitDataField
            public void a(CustomUnit customUnit, double d) {
                super.a(customUnit, d);
            }
        });
        a(s, new UnitStatsDataField(s.size(), "armour") { // from class: com.corrodinggames.rts.game.units.custom.as.19
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.armour;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.armour = (float) d;
            }
        });
        a(s, new UnitStatsDataField(s.size(), "maxattackrange") { // from class: com.corrodinggames.rts.game.units.custom.as.2
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.maxAttackRange;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.maxAttackRange = (float) d;
            }
        });
        a(s, new UnitStatsDataField(s.size(), "shootdelaymultiplier") { // from class: com.corrodinggames.rts.game.units.custom.as.3
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.shootDelayMultiplier;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.shootDelayMultiplier = (float) d;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField, com.corrodinggames.rts.game.units.custom.CustomUnitDataField
            public void a(CustomUnit customUnit, double d) {
                super.a(customUnit, d);
                customUnit.clampMovementLevelRotations();
            }
        });
        a(s, new UnitStatsDataField(s.size(), "shootdamagemultiplier") { // from class: com.corrodinggames.rts.game.units.custom.as.4
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.shootDamageMultiplier;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.shootDamageMultiplier = (float) d;
            }
        });
        a(s, new UnitStatsDataField(s.size(), "movespeed") { // from class: com.corrodinggames.rts.game.units.custom.as.5
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.moveSpeed;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.moveSpeed = (float) d;
            }
        });
        a(s, new UnitStatsDataField(s.size(), "maxturnspeed") { // from class: com.corrodinggames.rts.game.units.custom.as.6
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.maxTurnSpeed;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.maxTurnSpeed = (float) d;
            }
        });
        a(s, new UnitStatsDataField(s.size(), "fogofwarsightrange") { // from class: com.corrodinggames.rts.game.units.custom.as.7
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.fogOfWarSightRange;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.fogOfWarSightRange = (int) d;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField, com.corrodinggames.rts.game.units.custom.CustomUnitDataField
            public void a(CustomUnit customUnit, double d) {
                int iS = customUnit.s();
                super.a(customUnit, d);
                if (customUnit.s() > iS && !customUnit.ax) {
                    customUnit.c(false);
                }
            }
        });
        a(s, new UnitStatsDataField(s.size(), "nanorange") { // from class: com.corrodinggames.rts.game.units.custom.as.8
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.nanoRange;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.nanoRange = (int) d;
            }
        });
        a(s, new UnitStatsDataField(s.size(), "selfregenrate") { // from class: com.corrodinggames.rts.game.units.custom.as.9
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.selfRegenRate;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.selfRegenRate = (float) d;
            }
        });
        a(s, new UnitStatsDataField(s.size(), "targetHeight") { // from class: com.corrodinggames.rts.game.units.custom.as.10
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.targetHeight;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.targetHeight = (float) d;
            }
        });
        a(s, new UnitStatsDataField(s.size(), "nanoFactorySpeed") { // from class: com.corrodinggames.rts.game.units.custom.as.11
            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public double a(UnitStats unitStats) {
                return unitStats.nanoFactorySpeed;
            }

            @Override // com.corrodinggames.rts.game.units.custom.UnitStatsDataField
            public void a(UnitStats unitStats, double d) {
                unitStats.nanoFactorySpeed = (float) d;
            }
        });
        t = new LinkedHashMap();
        for (String str : s.keySet()) {
            if (!str.equals(str.toLowerCase(Locale.ROOT))) {
                throw new RuntimeException(str);
            }
            CustomUnitDataField customUnitDataField = (CustomUnitDataField) s.get(str);
            if (!customUnitDataField.b()) {
                t.put(str, customUnitDataField);
            }
        }
    }

    public UnitStats b() {
        UnitStats unitStatsClone = clone();
        unitStatsClone.a = false;
        return unitStatsClone;
    }

    public static CustomUnitDataField a(int i) {
        for (CustomUnitDataField customUnitDataField : s.values()) {
            if (i == customUnitDataField.a) {
                return customUnitDataField;
            }
        }
        return null;
    }

    public static void a(CustomUnit customUnit, UnitStats unitStats, CustomUnitDataField[] customUnitDataFieldArr) {
        for (CustomUnitDataField customUnitDataField : customUnitDataFieldArr) {
            double dA = customUnitDataField.a(customUnit, customUnit.y);
            double dA2 = customUnitDataField.a(customUnit, unitStats);
            if (dA != dA2) {
                customUnit.dJ();
                customUnitDataField.a(customUnit, dA2);
            }
        }
    }

    public static void a(CustomUnit customUnit, UnitStats unitStats, CustomUnitConfig customUnitConfig) {
        if (!(unitStats != customUnitConfig.unitStats)) {
            return;
        }
        Iterator it = t.keySet().iterator();
        while (it.hasNext()) {
            CustomUnitDataField customUnitDataField = (CustomUnitDataField) t.get((String) it.next());
            double dA = customUnitDataField.a(customUnit, customUnitConfig.unitStats);
            double dA2 = customUnitDataField.a(customUnit, unitStats);
            if (dA != dA2) {
                customUnit.dJ();
                customUnitDataField.a(customUnit, dA2);
            }
        }
    }

    public static void a(UnitStats unitStats, CustomUnit customUnit, GameOutputStream gameOutputStream) throws IOException {
        CustomUnitConfig customUnitConfig = customUnit.unitConfig;
        if (!(unitStats != customUnitConfig.unitStats)) {
            gameOutputStream.writeBoolean(true);
            return;
        }
        gameOutputStream.writeBoolean(false);
        short s2 = 0;
        Iterator it = t.keySet().iterator();
        while (it.hasNext()) {
            CustomUnitDataField customUnitDataField = (CustomUnitDataField) t.get((String) it.next());
            if (customUnitDataField.a(customUnit, customUnitConfig.unitStats) != customUnitDataField.a(customUnit, unitStats)) {
                s2 = (short) (s2 + 1);
            }
        }
        gameOutputStream.writeShort(s2);
        int i = 0;
        Iterator it2 = t.keySet().iterator();
        while (it2.hasNext()) {
            CustomUnitDataField customUnitDataField2 = (CustomUnitDataField) t.get((String) it2.next());
            double dA = customUnitDataField2.a(customUnit, customUnitConfig.unitStats);
            double dA2 = customUnitDataField2.a(customUnit, unitStats);
            if (dA != dA2) {
                i++;
                if (s2 < i) {
                    throw new IOException("numberOfChangedFields>fieldsWritten: " + ((int) s2) + ">" + i);
                }
                gameOutputStream.writeShort((short) customUnitDataField2.a);
                gameOutputStream.writeDouble(dA2);
                gameOutputStream.writeDouble(dA);
            }
        }
    }

    public static void a(CustomUnit customUnit, GameInputStream gameInputStream, int i) throws IOException {
        CustomUnitConfig customUnitConfig = customUnit.unitConfig;
        if (gameInputStream.readBoolean()) {
            return;
        }
        short shortValue = gameInputStream.readShortValue();
        for (int i2 = 0; i2 < shortValue; i2++) {
            short shortValue2 = gameInputStream.readShortValue();
            double d = gameInputStream.readDouble();
            gameInputStream.readDouble();
            CustomUnitDataField customUnitDataFieldA = a(shortValue2);
            if (customUnitDataFieldA == null) {
                throw new IOException("Field " + ((int) shortValue2) + " doesn't exist");
            }
            customUnit.dJ();
            customUnitDataFieldA.a(customUnit, d);
        }
    }

    public static CustomUnitDataField[] a(IniFile iniFile, String str, String str2, CustomUnitDataField[] customUnitDataFieldArr) {
        try {
            return a(iniFile.getString(str, str2, (String) null), customUnitDataFieldArr);
        } catch (RuntimeException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": " + e.getMessage(), e);
        }
    }

    public static CustomUnitDataField[] a(String str, CustomUnitDataField[] customUnitDataFieldArr) {
        if (str == null) {
            return customUnitDataFieldArr;
        }
        ArrayList arrayList = new ArrayList();
        for (String str2 : Utility.splitByChar(str, ',')) {
            String lowerCase = str2.trim().toLowerCase(Locale.ROOT);
            CustomUnitDataField customUnitDataField = (CustomUnitDataField) t.get(lowerCase);
            if (arrayList.contains(customUnitDataField)) {
                throw new RuntimeException("Value: " + lowerCase + " is repeated");
            }
            if (customUnitDataField == null) {
                String str3 = VariableScope.nullOrMissingString;
                for (String str4 : t.keySet()) {
                    if (!str3.equals(VariableScope.nullOrMissingString)) {
                        str3 = str3 + ", ";
                    }
                    str3 = str3 + str4;
                }
                throw new RuntimeException("Unknown value: " + lowerCase + " (Expected: " + Utility.truncateWithEllipsis(str3, 100) + ")");
            }
            arrayList.add(customUnitDataField);
        }
        return (CustomUnitDataField[]) arrayList.toArray(new CustomUnitDataField[0]);
    }
}
