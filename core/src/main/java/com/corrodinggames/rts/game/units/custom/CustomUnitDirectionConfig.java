package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/m.class */
public class CustomUnitDirectionConfig {

    /* JADX INFO: renamed from: a */
    public boolean useMainTurret;

    /* JADX INFO: renamed from: b */
    public float directionUnits;

    /* JADX INFO: renamed from: c */
    public int strideX;

    /* JADX INFO: renamed from: d */
    public int strideY;

    /* JADX INFO: renamed from: e */
    public float startingDirection;

    public static CustomUnitDirectionConfig a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, boolean z) {
        CustomUnitDirectionConfig customUnitDirectionConfig = new CustomUnitDirectionConfig();
        customUnitDirectionConfig.useMainTurret = iniFile.getBoolean(str, str2 + "direction_useMainTurret", (Boolean) false).booleanValue();
        customUnitDirectionConfig.directionUnits = iniFile.getFloat(str, str2 + "direction_units", Float.valueOf(0.0f)).floatValue();
        customUnitDirectionConfig.strideX = iniFile.getInt(str, str2 + "direction_strideX", (Integer) (-1)).intValue();
        customUnitDirectionConfig.strideY = iniFile.getInt(str, str2 + "direction_strideY", (Integer) (-1)).intValue();
        customUnitDirectionConfig.startingDirection = iniFile.getFloat(str, str2 + "direction_starting", Float.valueOf(0.0f)).floatValue();
        if (customUnitDirectionConfig.directionUnits == 0.0f) {
            return null;
        }
        return customUnitDirectionConfig;
    }
}
