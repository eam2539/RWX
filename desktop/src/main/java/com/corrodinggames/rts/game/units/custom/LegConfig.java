package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.ba */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/ba.class */
public class LegConfig {
    int a;
    String b;
    boolean c;
    public float d;
    public float e;
    public float i;
    public float j;
    public float k;
    public boolean l;
    public boolean p;
    public LogicBoolean q;
    public float w;
    public Texture x;
    public Texture[] y;
    public boolean z;
    public float A;
    public Texture B;
    public Texture[] C;
    public Texture D;
    public boolean E;
    public boolean H;
    public int[] S;
    public float T;
    public float f = 2.0f;
    public float g = 0.0f;
    public boolean h = true;
    public float m = 1.0f;
    public boolean n = true;
    public boolean o = false;
    public float r = 1.0f;
    public float s = 1.0f;
    public float t = 0.0f;
    public float u = 3.0f;
    public float v = 0.3f;
    public boolean F = true;
    public boolean G = true;
    public boolean I = true;
    public boolean J = true;
    public float K = 7.0f;
    public int L = 3;
    public boolean M = true;
    public float N = 16.0f;
    public float O = 50.0f;
    public boolean P = false;
    public boolean Q = false;
    public float R = 0.0f;

    public void a(LegConfig legConfig) {
        this.d = legConfig.d;
        this.e = legConfig.e;
        this.i = legConfig.i;
        this.j = legConfig.j;
        this.k = legConfig.k;
        this.f = legConfig.f;
        this.g = legConfig.g;
        this.h = legConfig.h;
        this.l = legConfig.l;
        this.m = legConfig.m;
        this.n = legConfig.n;
        this.o = legConfig.o;
        this.t = legConfig.t;
        this.p = legConfig.p;
        this.r = legConfig.r;
        this.s = legConfig.s;
        this.u = legConfig.u;
        this.w = legConfig.w;
        this.x = legConfig.x;
        this.y = legConfig.y;
        this.A = legConfig.A;
        this.z = legConfig.z;
        this.B = legConfig.B;
        this.C = legConfig.C;
        this.D = legConfig.D;
        this.E = legConfig.E;
        this.F = legConfig.F;
        this.G = legConfig.G;
        this.v = legConfig.v;
        this.H = legConfig.H;
        this.I = legConfig.I;
        this.J = legConfig.J;
        this.K = legConfig.K;
        this.L = legConfig.L;
        this.M = legConfig.M;
        this.N = legConfig.N;
        this.O = legConfig.O;
        this.P = legConfig.P;
        this.Q = legConfig.Q;
        this.R = legConfig.R;
        this.T = legConfig.T;
    }

    public static void a(LegConfig legConfig, CustomUnitConfig customUnitConfig, IniFile iniFile, String str, boolean z, ArrayList arrayList) {
        if (!z) {
            legConfig.l = true;
            legConfig.J = false;
        }
        int iIntValue = iniFile.getInt(str, "copyFrom", (Integer) 0).intValue();
        if (iIntValue != 0) {
            if (iIntValue - 1 >= arrayList.size()) {
                throw new RuntimeException("copyFrom: Leg/Arm copy target not loaded yet: " + iIntValue);
            }
            legConfig.a((LegConfig) arrayList.get(iIntValue - 1));
        }
        legConfig.d = iniFile.getFloatStrictRaw(str, "x");
        legConfig.e = iniFile.getFloatStrictRaw(str, "y");
        legConfig.b = str.replace("_", VariableScope.nullOrMissingString);
        legConfig.c = z;
        Float f = iniFile.getFloat(str, "attach_x", (Float) null);
        if (f != null) {
            legConfig.j = f.floatValue();
        }
        Float f2 = iniFile.getFloat(str, "attach_y", (Float) null);
        if (f2 != null) {
            legConfig.k = f2.floatValue();
        }
        legConfig.f = iniFile.getFloat(str, "liftingHeightOffset", Float.valueOf(legConfig.f)).floatValue();
        legConfig.g = iniFile.getFloat(str, "targetHeight", Float.valueOf(legConfig.g)).floatValue();
        legConfig.h = iniFile.getBoolean(str, "targetHeightRelative", Boolean.valueOf(legConfig.h)).booleanValue();
        legConfig.i = iniFile.getFloat(str, "endDirOffset", Float.valueOf(0.0f)).floatValue();
        legConfig.l = iniFile.getBoolean(str, "lockMovement", Boolean.valueOf(legConfig.l)).booleanValue();
        legConfig.m = iniFile.getFloat(str, "estimatingPositionMultiplier", Float.valueOf(legConfig.m)).floatValue();
        legConfig.q = iniFile.getLogicBoolean(customUnitConfig, str, "hidden", legConfig.q);
        legConfig.p = legConfig.q == LogicBoolean.trueBoolean;
        legConfig.r = iniFile.getFloat(str, "alpha", Float.valueOf(legConfig.r)).floatValue();
        Float f3 = iniFile.getFloat(str, "moveSpeed", (Float) null);
        if (f3 != null) {
            legConfig.s = f3.floatValue();
        }
        legConfig.t = iniFile.getTime(str, "moveWarmUp", Float.valueOf(legConfig.t)).floatValue();
        legConfig.u = iniFile.getFloat(str, "rotateSpeed", Float.valueOf(legConfig.u)).floatValue();
        Float f4 = iniFile.getFloat(str, "resetAngle", (Float) null);
        if (f4 != null) {
            legConfig.w = f4.floatValue();
        }
        boolean zBooleanValue = iniFile.getBoolean(str, "image_end_teamColors", (Boolean) false).booleanValue();
        Texture textureA = customUnitConfig.a(iniFile, str, "image_foot");
        if (textureA != null) {
            legConfig.B = textureA;
            if (zBooleanValue) {
                legConfig.C = customUnitConfig.a(legConfig.B, customUnitConfig.teamColoringMode);
            } else {
                legConfig.C = null;
            }
        }
        Texture textureA2 = customUnitConfig.a(iniFile, str, "image_end");
        if (textureA2 != null) {
            legConfig.B = textureA2;
            if (zBooleanValue) {
                legConfig.C = customUnitConfig.a(legConfig.B, customUnitConfig.teamColoringMode);
            } else {
                legConfig.C = null;
            }
        }
        Texture textureA3 = customUnitConfig.a(iniFile, str, "image_foot_shadow");
        if (textureA3 != null) {
            legConfig.D = textureA3;
        }
        Texture textureA4 = customUnitConfig.a(iniFile, str, "image_end_shadow");
        if (textureA4 != null) {
            legConfig.D = textureA4;
        }
        Texture textureA5 = customUnitConfig.a(iniFile, str, "image_leg");
        if (textureA5 != null) {
            legConfig.x = textureA5;
        }
        Texture textureA6 = customUnitConfig.a(iniFile, str, "image_middle");
        if (textureA6 != null) {
            legConfig.x = textureA6;
        }
        boolean zBooleanValue2 = iniFile.getBoolean(str, "image_middle_teamColors", (Boolean) false).booleanValue();
        if (legConfig.x != null && zBooleanValue2) {
            legConfig.y = customUnitConfig.a(legConfig.x, customUnitConfig.teamColoringMode);
        } else {
            legConfig.y = null;
        }
        Float f5 = iniFile.getFloat(str, "heightSpeed", (Float) null);
        if (f5 != null) {
            legConfig.v = f5.floatValue();
        }
        Boolean bool = iniFile.getBoolean(str, "draw_foot_on_top", (Boolean) null);
        if (bool != null) {
            legConfig.H = bool.booleanValue();
        }
        Boolean bool2 = iniFile.getBoolean(str, "dust_effect", (Boolean) null);
        if (bool2 != null) {
            legConfig.I = bool2.booleanValue();
        }
        Boolean bool3 = iniFile.getBoolean(str, "drawLegWhenZoomedOut", (Boolean) null);
        if (bool3 != null) {
            legConfig.F = bool3.booleanValue();
            legConfig.E = true;
        }
        Boolean bool4 = iniFile.getBoolean(str, "drawFootWhenZoomedOut", (Boolean) null);
        if (bool4 != null) {
            legConfig.G = bool4.booleanValue();
            legConfig.E = true;
        }
        if (!legConfig.E && !legConfig.l && !legConfig.P) {
            if (customUnitConfig.radius < 30) {
                legConfig.F = false;
            }
            if (customUnitConfig.radius < 20) {
                legConfig.G = false;
            }
        }
        Boolean bool5 = iniFile.getBoolean(str, "explodeOnDeath", (Boolean) null);
        if (bool5 != null) {
            legConfig.J = bool5.booleanValue();
        }
        Float f6 = iniFile.getFloat(str, "holdDisMin", (Float) null);
        if (f6 != null) {
            legConfig.K = f6.floatValue();
        }
        legConfig.L = iniFile.getInt(str, "holdDisMin_maxMovingLegs", Integer.valueOf(legConfig.L)).intValue();
        legConfig.M = iniFile.getBoolean(str, "hold_moveOnlyIfFurthest", Boolean.valueOf(legConfig.M)).booleanValue();
        legConfig.n = iniFile.getBoolean(str, "holdDisMin_checkNeighbours", Boolean.valueOf(legConfig.n)).booleanValue();
        legConfig.o = iniFile.getBoolean(str, "favourOppositeSideNeighbours", Boolean.valueOf(legConfig.o)).booleanValue();
        Float f7 = iniFile.getFloat(str, "holdDisMax", (Float) null);
        if (f7 != null) {
            legConfig.N = f7.floatValue();
        }
        Float f8 = iniFile.getFloat(str, "hardLimit", (Float) null);
        if (f8 != null) {
            legConfig.O = f8.floatValue();
        }
        legConfig.P = iniFile.getBoolean(str, "drawOverBody", Boolean.valueOf(legConfig.P)).booleanValue();
        if (legConfig.P) {
            customUnitConfig.drawLegsOverBody = true;
        }
        legConfig.Q = iniFile.getBoolean(str, "drawUnderAllUnits", Boolean.valueOf(legConfig.Q)).booleanValue();
        if (legConfig.Q) {
            customUnitConfig.drawLegsUnderAllUnits = true;
        }
        if (legConfig.Q && legConfig.P) {
            throw new RuntimeException("Both drawUnderAllUnits and drawOverBody can not be set true at the same time in leg/arm");
        }
        legConfig.R = iniFile.getFloat(str, "drawDirOffset", Float.valueOf(legConfig.R)).floatValue();
        legConfig.T = iniFile.getFloat(str, "spinRate", Float.valueOf(legConfig.T)).floatValue();
    }
}
