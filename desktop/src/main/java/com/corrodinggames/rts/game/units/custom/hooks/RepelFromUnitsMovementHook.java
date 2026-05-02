package com.corrodinggames.rts.game.units.custom.hooks;

import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.b.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/b/j.class */
public class RepelFromUnitsMovementHook extends CustomUnitRenderHook {
    LogicBoolean a;
    float b;
    float c;
    AnimationSet d;
    boolean e;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile) {
        if (iniFile.isSectionNotEmpty("movement_repelFromUnits")) {
            RepelFromUnitsMovementHook repelFromUnitsMovementHook = new RepelFromUnitsMovementHook();
            repelFromUnitsMovementHook.a(customUnitConfig, iniFile, "movement_repelFromUnits", "movement_repelFromUnits");
            if (!LogicBoolean.isStaticFalse(repelFromUnitsMovementHook.a)) {
                customUnitConfig.a(repelFromUnitsMovementHook);
            }
        }
    }

    public void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2) {
        this.a = iniFile.getLogicBoolean(customUnitConfig, str, "enabled");
        this.b = iniFile.getFloatStrictRaw(str, "speed");
        this.c = iniFile.getFloat(str, "maxSpeed", Float.valueOf(5.0f)).floatValue();
        this.d = AnimationTag.a(iniFile.getString(str, "otherUnitHasTag", (String) null), (AnimationSet) null);
        this.e = iniFile.getBoolean(str, "onlySameTeam", (Boolean) false).booleanValue();
    }

    @Override // com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook
    public void b(CustomUnit customUnit, float f) {
        if (!this.a.read(customUnit)) {
        }
    }
}
