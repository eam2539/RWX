package com.corrodinggames.rts.game.units.custom.hooks;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.TeamRelation;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.*;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.graphics.opengl.GraphicsUtils;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;
import com.corrodinggames.rts.gameFramework.utility.Vector3D;

import java.util.ArrayList;
import java.util.Collections;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.b.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/b/c.class */
public class CustomUnitDecalRenderer extends CustomUnitRenderHook {
    FastArrayList c = new FastArrayList();
    FastArrayList d = new FastArrayList();
    FastArrayList e = new FastArrayList();
    FastArrayList f = new FastArrayList();
    FastArrayList g = new FastArrayList();
    boolean h;
    static Paint a = new Paint();
    static GamePaint b = new GamePaint();
    static final PointF i = new PointF();
    static final DecalListProcessor j = new DecalListProcessor(VariableScope.nullOrMissingString);
    static final Rect k = new Rect();
    static final RectF l = new RectF();

    /* JADX INFO: Access modifiers changed from: private */
    public static DecalDefinition c(CustomUnitConfig customUnitConfig, String str) {
        for (DecalDefinition decalDefinition : customUnitConfig.customAttachments) {
            if (str.equalsIgnoreCase(decalDefinition.a)) {
                return decalDefinition;
            }
        }
        return null;
    }

    public static DecalListProcessor a(CustomUnitConfig customUnitConfig, String str) {
        if (str == null || str.equals(VariableScope.nullOrMissingString) || str.equalsIgnoreCase("NONE")) {
            return null;
        }
        DecalListProcessor decalListProcessor = new DecalListProcessor(str);
        customUnitConfig.registerConfigProcessor(decalListProcessor);
        return decalListProcessor;
    }

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile) throws ConfigParseException {
        String str;
        FastArrayList fastArrayList;
        CustomUnitDecalRenderer customUnitDecalRenderer = null;
        for (String str2 : iniFile.getSectionsStartingWith("decal_")) {
            customUnitConfig.a("1.15p9", 115009, str2, "decals");
            String strSubstring = str2.substring("decal_".length());
            DecalDefinition decalDefinition = new DecalDefinition();
            decalDefinition.a = strSubstring;
            if (strSubstring.contains(",")) {
                throw new ConfigParseException("[" + str2 + "]Decal name: '" + strSubstring + "' cannot have ','");
            }
            if (strSubstring.contains(" ")) {
                throw new ConfigParseException("[" + str2 + "]Decal name: '" + strSubstring + "' cannot have spaces");
            }
            if (strSubstring.contains("(")) {
                throw new ConfigParseException("[" + str2 + "]Decal name: '" + strSubstring + "' cannot have '('");
            }
            decalDefinition.G = (DecalLayer) iniFile.getEnum(str2, "layer", DecalLayer.onTop, DecalLayer.class);
            decalDefinition.H = iniFile.getFloat(str2, "order", Float.valueOf(0.0f)).floatValue();
            decalDefinition.c = iniFile.getBoolean(str2, "onlyWhenSelectedByOwnPlayer", (Boolean) false).booleanValue();
            decalDefinition.d = iniFile.getBoolean(str2, "onlyWhenSelectedByEnemyPlayer", (Boolean) false).booleanValue();
            decalDefinition.e = iniFile.getBoolean(str2, "onlyWhenSelectedByAllyNotOwnPlayer", (Boolean) false).booleanValue();
            decalDefinition.f = iniFile.getBoolean(str2, "onlyWhenSelectedByAnyPlayer", (Boolean) false).booleanValue();
            int i2 = decalDefinition.c ? 0 + 1 : 0;
            if (decalDefinition.d) {
                i2++;
            }
            if (decalDefinition.e) {
                i2++;
            }
            if (decalDefinition.f) {
                i2++;
            }
            if (i2 >= 2) {
                throw new ConfigParseException("[" + str2 + "]Doesn't make sense to set more than one onlyWhenSelectedBy* to true at a time.");
            }
            if (i2 > 0) {
                decalDefinition.b = true;
            } else {
                decalDefinition.b = false;
            }
            decalDefinition.g = iniFile.getBoolean(str2, "includeParentsSelection", (Boolean) false).booleanValue();
            decalDefinition.h = (TeamRelation) iniFile.getEnum(str2, "onlyTeam", TeamRelation.any, TeamRelation.class);
            decalDefinition.i = iniFile.getBoolean(str2, "onlyPlayersWithUnitControl", (Boolean) false).booleanValue();
            decalDefinition.j = iniFile.getFloat(str2, "onlyWithZoomLevelOrMore", Float.valueOf(0.0f)).floatValue();
            boolean z = false;
            if (decalDefinition.G == DecalLayer.beforeUI) {
                z = true;
            }
            decalDefinition.k = iniFile.getBoolean(str2, "onlyWhileActive", (Boolean) false).booleanValue();
            decalDefinition.l = iniFile.getBoolean(str2, "onlyWhileAlive", Boolean.valueOf(z)).booleanValue();
            decalDefinition.m = iniFile.getBoolean(str2, "onlyInPreview", (Boolean) false).booleanValue();
            decalDefinition.n = iniFile.getBoolean(str2, "onlyOnNonPreview", (Boolean) false).booleanValue();
            if (decalDefinition.m && decalDefinition.n) {
                throw new ConfigParseException("[" + str2 + "]decal with both onlyInPreview and onlyOnNonPreview will never be visible");
            }
            if (decalDefinition.j > 5.0f) {
                throw new ConfigParseException("[" + str2 + "]decal with onlyWithZoomLevelOrMore:" + decalDefinition.j + " will never be visible");
            }
            if (decalDefinition.j < 0.0f) {
                throw new ConfigParseException("[" + str2 + "]onlyWithZoomLevelOrMore:" + decalDefinition.j + " cannot be less than zero");
            }
            Integer logicBooleanUnit = iniFile.getLogicBooleanUnit(str2, "onlyOnBodyFrameOf", (Integer) null);
            if (logicBooleanUnit != null) {
                decalDefinition.o = logicBooleanUnit.intValue();
                if (decalDefinition.o < 0) {
                    throw new ConfigParseException("[" + str2 + "]onlyOnBodyFrameOf cannot be: " + decalDefinition.o);
                }
            }
            LogicBoolean logicBooleanNumber = iniFile.getLogicBooleanNumber(customUnitConfig, str2, "imageScale", null);
            if (logicBooleanNumber != null) {
                if (LogicBoolean.isStaticNumber(logicBooleanNumber)) {
                    decalDefinition.p = LogicBoolean.getKnownStaticNumber(logicBooleanNumber);
                } else {
                    decalDefinition.q = true;
                    decalDefinition.r = logicBooleanNumber;
                }
            }
            LogicBoolean logicBooleanNumber2 = iniFile.getLogicBooleanNumber(customUnitConfig, str2, "imageScaleX", null);
            LogicBoolean logicBooleanNumber3 = iniFile.getLogicBooleanNumber(customUnitConfig, str2, "imageScaleY", null);
            if (logicBooleanNumber2 != null || logicBooleanNumber3 != null) {
                decalDefinition.q = true;
                decalDefinition.s = logicBooleanNumber2;
                decalDefinition.t = logicBooleanNumber3;
            }
            Integer logicBooleanUnit2 = iniFile.getLogicBooleanUnit(str2, "total_frames", (Integer) null);
            if (logicBooleanUnit2 != null) {
                decalDefinition.J = logicBooleanUnit2.intValue();
                if (decalDefinition.J < 1) {
                    throw new ConfigParseException("[" + str2 + "] TOTAL_FRAMES cannot be: " + decalDefinition.J + " (must be 1 or more)");
                }
            }
            decalDefinition.M = iniFile.getBoolean(str2, "frame_verticalOrdering", (Boolean) false).booleanValue();
            decalDefinition.K = iniFile.getLogicBooleanUnit(str2, "frame_width", (Integer) (-1)).intValue();
            decalDefinition.L = iniFile.getLogicBooleanUnit(str2, "frame_height", (Integer) (-1)).intValue();
            if (decalDefinition.K != -1 && decalDefinition.J != -1) {
                throw new ConfigParseException("[" + str2 + "]TOTAL_FRAMES and frame_width cannot be both set");
            }
            if (decalDefinition.L != -1 && decalDefinition.L <= 0) {
                throw new ConfigParseException("[" + str2 + "]frame_height cannot be: " + decalDefinition.L);
            }
            if (decalDefinition.K != -1 && decalDefinition.K <= 0) {
                throw new ConfigParseException("[" + str2 + "]frame_width cannot be: " + decalDefinition.K);
            }
            if (decalDefinition.J != -1 && decalDefinition.J <= 0) {
                throw new ConfigParseException("[" + str2 + "]TOTAL_FRAMES cannot be: " + decalDefinition.J);
            }
            if (decalDefinition.L != -1 || decalDefinition.K != -1 || decalDefinition.J != -1) {
                decalDefinition.I = true;
            }
            boolean zBooleanValue = iniFile.getBoolean(str2, "teamColors", (Boolean) false).booleanValue();
            Texture textureA = customUnitConfig.a(iniFile, str2, "image");
            if (textureA != null) {
                DecalImageSlice decalImageSlice = new DecalImageSlice();
                decalImageSlice.a = textureA;
                if (decalImageSlice.a != null && zBooleanValue) {
                    decalImageSlice.b = customUnitConfig.a(textureA, customUnitConfig.teamColoringMode);
                }
                decalImageSlice.a(decalDefinition);
                decalDefinition.v = decalImageSlice;
                decalDefinition.u = true;
            }
            String string = iniFile.getString(str2, "imageStack", (String) null);
            if (string != null && !string.equals(VariableScope.nullOrMissingString)) {
                decalDefinition.u = true;
                ArrayList arrayList = new ArrayList();
                for (String str3 : string.split(",")) {
                    String strTrim = str3.trim();
                    boolean z2 = zBooleanValue;
                    String strTrim2 = null;
                    if (strTrim.contains("(") && strTrim.contains(")")) {
                        String[] strArrB = StringUtils.b(strTrim, "(");
                        if (strArrB == null) {
                            throw new ConfigParseException("[" + str2 + "]imageStack: Unexpected format for: " + string);
                        }
                        strTrim = strArrB[0];
                        strTrim2 = strArrB[1].trim();
                    }
                    String[] strArrSplit = strTrim.split("\\*");
                    String str4 = strArrSplit[0];
                    int i3 = 1;
                    if (strArrSplit.length >= 2) {
                        i3 = Integer.parseInt(strArrSplit[1]);
                    }
                    if (strTrim2 != null) {
                        if (!strTrim2.endsWith(")")) {
                            throw new ConfigParseException("[" + str2 + "]imageStack: Missing ')' in: " + string);
                        }
                        for (String str5 : StringUtils.a(strTrim2.substring(0, strTrim2.length() - 1), ",", false, false)) {
                            if (!str5.trim().equals(VariableScope.nullOrMissingString)) {
                                String[] strArrB2 = StringUtils.b(str5, "=");
                                if (strArrB2 == null) {
                                    throw new RuntimeException("[" + str2 + "]imageStack: Unexpected key format for: " + string);
                                }
                                String strTrim3 = strArrB2[0].trim();
                                String strTrim4 = strArrB2[1].trim();
                                if (strTrim3.equalsIgnoreCase("teamColors")) {
                                    z2 = IniFile.parseBoolean(str2, "imageStack", strTrim4);
                                } else if (strTrim3.equalsIgnoreCase("teamColor")) {
                                    z2 = IniFile.parseBoolean(str2, "imageStack", strTrim4);
                                } else {
                                    throw new RuntimeException("[" + str2 + "]imageStack: Unknown parameter: " + strTrim3);
                                }
                            }
                        }
                    }
                    DecalImageSlice decalImageSlice2 = new DecalImageSlice();
                    decalImageSlice2.a = customUnitConfig.a(customUnitConfig.resourceLoadPath, str4, customUnitConfig.imageSmoothing, str2, "imageStack");
                    if (decalImageSlice2.a == null) {
                        throw new ConfigParseException("[" + str2 + "]failed to load image " + str4);
                    }
                    if (z2) {
                        decalImageSlice2.b = customUnitConfig.a(decalImageSlice2.a, customUnitConfig.teamColoringMode);
                    }
                    decalImageSlice2.a(decalDefinition);
                    for (int i4 = 0; i4 < i3; i4++) {
                        arrayList.add(decalImageSlice2);
                    }
                }
                if (arrayList.size() > 0) {
                    decalDefinition.w = (DecalImageSlice[]) arrayList.toArray(new DecalImageSlice[0]);
                }
            }
            decalDefinition.x = iniFile.getFloat(str2, "stack_hOffset", Float.valueOf(1.0f)).floatValue();
            decalDefinition.y = iniFile.getLogicBooleanUnit(str2, "stack_frameOffset", (Integer) 0).intValue();
            decalDefinition.A = iniFile.getLogicBooleanNumber(customUnitConfig, str2, "stack_indexStart", null);
            decalDefinition.B = iniFile.getLogicBooleanNumber(customUnitConfig, str2, "stack_indexCount", null);
            Boolean bool = iniFile.getBoolean(str2, "stack_drawInReverseOrder", (Boolean) null);
            if ((bool != null && bool.booleanValue()) || (bool == null && decalDefinition.x < 0.0f)) {
                decalDefinition.z = true;
            }
            decalDefinition.N = iniFile.getLogicBooleanWithReturnType(customUnitConfig, str2, "frame", (LogicBoolean) null, LogicBoolean.ReturnType.number);
            decalDefinition.O = iniFile.getLogicBooleanUnit(str2, "addBodyFrameMultipliedBy", (Integer) 0).intValue();
            decalDefinition.F = iniFile.getLogicBoolean(customUnitConfig, str2, "isVisible", (LogicBoolean) null);
            decalDefinition.R = iniFile.getFloat(str2, "xOffsetRelative", Float.valueOf(0.0f)).floatValue();
            decalDefinition.S = iniFile.getFloat(str2, "yOffsetRelative", Float.valueOf(0.0f)).floatValue();
            decalDefinition.W = iniFile.getLogicBooleanNumber(customUnitConfig, str2, "xOffsetAbsolute", null);
            decalDefinition.X = iniFile.getLogicBooleanNumber(customUnitConfig, str2, "yOffsetAbsolute", null);
            if (LogicBoolean.isStaticNumber(decalDefinition.W)) {
                decalDefinition.T = LogicBoolean.getKnownStaticNumber(decalDefinition.W);
                decalDefinition.W = null;
            }
            if (LogicBoolean.isStaticNumber(decalDefinition.X)) {
                decalDefinition.U = LogicBoolean.getKnownStaticNumber(decalDefinition.X);
                decalDefinition.X = null;
            }
            decalDefinition.V = iniFile.getFloat(str2, "hOffset", Float.valueOf(0.0f)).floatValue();
            decalDefinition.aa = iniFile.getFloat(str2, "dirOffset", Float.valueOf(0.0f)).floatValue();
            decalDefinition.ab = iniFile.getFloat(str2, "pivotOffset", Float.valueOf(0.0f)).floatValue();
            decalDefinition.Y = iniFile.getBooleanFromTwoKeys(str2, "alwaysStartDirAtZero", "alwayStartDirAtZero", (Boolean) false).booleanValue();
            decalDefinition.Z = iniFile.getBoolean(str2, "alwaysStartHeightAtZero", (Boolean) false).booleanValue();
            if (decalDefinition.R != 0.0f) {
            }
            decalDefinition.ac = iniFile.getInt(customUnitConfig, str2, "basePosition", null);
            decalDefinition.ad = iniFile.getInt(customUnitConfig, str2, "drawLineTo", null);
            String string2 = iniFile.getString(str2, "basePositionFromLegEnd", (String) null);
            if (string2 != null || 0 != 0) {
                if (string2 != null && 0 != 0) {
                    throw new ConfigParseException("[" + str2 + "]basePositionFromLegEnd and basePositionFromLegMiddle cannot be used at the same time");
                }
                if (0 != 0) {
                    str = null;
                    decalDefinition.af = true;
                } else {
                    str = string2;
                }
                decalDefinition.ae = customUnitConfig.findLegConfigIndex(str);
                if (decalDefinition.ae == -1) {
                    throw new ConfigParseException("[" + str2 + "]basePositionFromLeg* failed to find: " + str);
                }
            }
            String string3 = iniFile.getString(str2, "basePositionFromTurret", (String) null);
            if (string3 != null) {
                TurretConfig turretConfigFindProjectileConfigByName = customUnitConfig.findProjectileConfigByName(string3);
                if (turretConfigFindProjectileConfigByName == null) {
                    throw new ConfigParseException("[" + str2 + "]basePositionFromTurret failed to find: " + string3);
                }
                decalDefinition.ag = turretConfigFindProjectileConfigByName.turretIndex;
            }
            if (decalDefinition.ae != -1 && decalDefinition.ag != -1) {
                throw new ConfigParseException("[" + str2 + "]basePositionFromTurret and basePositionFromLeg cannot be used at the same time");
            }
            if ((decalDefinition.ae != -1 || decalDefinition.ag != -1) && decalDefinition.ac != null) {
                throw new ConfigParseException("[" + str2 + "]basePositionFromTurret/basePositionFromLeg cannot be used at the same time as basePosition");
            }
            decalDefinition.C = customUnitConfig.a(iniFile, str2, "image_shadow");
            decalDefinition.D = iniFile.getFloat(str2, "shadowOffsetX", Float.valueOf(1.0f)).floatValue();
            decalDefinition.E = iniFile.getFloat(str2, "shadowOffsetY", Float.valueOf(1.0f)).floatValue();
            decalDefinition.P = iniFile.getColorAsInt(str2, "color", (Integer) (-1)).intValue();
            decalDefinition.Q = iniFile.getFloat(str2, "lineWidth", Float.valueOf(1.0f)).floatValue();
            float f = 1.0f;
            LogicBoolean logicBooleanNumber4 = iniFile.getLogicBooleanNumber(customUnitConfig, str2, "alpha", null);
            if (logicBooleanNumber4 != null) {
                if (LogicBoolean.isStaticNumber(logicBooleanNumber4)) {
                    float knownStaticNumber = LogicBoolean.getKnownStaticNumber(logicBooleanNumber4);
                    if (knownStaticNumber < 0.0f || knownStaticNumber > 1.0f) {
                        throw new ConfigParseException("[" + str2 + "]alpha should be between 0-1");
                    }
                    f = knownStaticNumber;
                } else {
                    decalDefinition.ai = logicBooleanNumber4;
                }
            }
            if (decalDefinition.P != -1 || decalDefinition.Q != 1.0f || f != 1.0f) {
                decalDefinition.ah = new GamePaint();
                decalDefinition.ah.b(decalDefinition.P);
                if (decalDefinition.P != -1) {
                    GraphicsUtils.a(decalDefinition.ah);
                }
                int iF = (int) (decalDefinition.ah.f() * f);
                if (iF < 0) {
                    iF = 0;
                }
                if (iF > 255) {
                    iF = 255;
                }
                decalDefinition.ah.c(iF);
                decalDefinition.ah.a(decalDefinition.Q);
            }
            boolean z3 = true;
            if (LogicBoolean.isStaticFalse(decalDefinition.F)) {
                z3 = false;
            }
            if (decalDefinition.ad == null && !decalDefinition.u) {
                z3 = false;
            }
            if (f == 0.0f) {
                z3 = false;
            }
            customUnitConfig.customAttachments.add(decalDefinition);
            if (z3) {
                if (customUnitDecalRenderer == null) {
                    customUnitDecalRenderer = new CustomUnitDecalRenderer();
                    customUnitConfig.b(customUnitDecalRenderer);
                }
                if (!decalDefinition.b) {
                    customUnitDecalRenderer.h = true;
                }
                if (decalDefinition.C != null && decalDefinition.G != DecalLayer.shadow) {
                    customUnitDecalRenderer.c.add(decalDefinition);
                }
                if (decalDefinition.G == DecalLayer.afterBody) {
                    fastArrayList = customUnitDecalRenderer.f;
                } else if (decalDefinition.G == DecalLayer.beforeBody) {
                    fastArrayList = customUnitDecalRenderer.e;
                } else if (decalDefinition.G == DecalLayer.beforeUI) {
                    fastArrayList = customUnitDecalRenderer.g;
                } else if (decalDefinition.G == DecalLayer.shadow) {
                    fastArrayList = customUnitDecalRenderer.c;
                } else if (decalDefinition.G == DecalLayer.inactive) {
                    fastArrayList = null;
                } else {
                    fastArrayList = customUnitDecalRenderer.d;
                }
                if (fastArrayList != null) {
                    fastArrayList.add(decalDefinition);
                    Collections.sort(fastArrayList);
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook
    public void b(CustomUnit customUnit, float f) {
        GameEngine.getInstance();
    }

    @Override // com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook
    public void a(CustomUnit customUnit) {
    }

    @Override // com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook
    public void d(CustomUnit customUnit, float f) {
        a(customUnit, f, DecalLayer.shadow, this.c);
        a(customUnit, f, DecalLayer.beforeBody, this.e);
    }

    @Override // com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook
    public void e(CustomUnit customUnit, float f) {
        a(customUnit, f, DecalLayer.afterBody, this.f);
    }

    @Override // com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook
    public void c(CustomUnit customUnit, float f) {
        a(customUnit, f, DecalLayer.onTop, this.d);
    }

    @Override // com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook
    public void f(CustomUnit customUnit, float f) {
        a(customUnit, f, DecalLayer.beforeUI, this.g);
    }

    public static Rect a(DecalDefinition decalDefinition, DecalImageSlice decalImageSlice, Texture texture, int i2) {
        int i3 = decalImageSlice.c;
        int i4 = decalImageSlice.d;
        int i5 = 0;
        int i6 = 0;
        if (i2 > 0) {
            if (!decalDefinition.M) {
                int i7 = 0;
                int i8 = i2;
                int i9 = decalImageSlice.f;
                if (i2 >= i9) {
                    i7 = 0 + (i2 / i9);
                    i8 = i2 % i9;
                }
                i5 = i8 * i3;
                i6 = i7 * i4;
            } else {
                int i10 = i2;
                int i11 = 0;
                int i12 = decalImageSlice.e;
                if (i2 >= i12) {
                    i11 = 0 + (i2 / i12);
                    i10 = i2 % i12;
                }
                i5 = i11 * i3;
                i6 = i10 * i4;
            }
        }
        Rect rect = k;
        rect.a = i5;
        rect.b = i6;
        rect.c = i5 + i3;
        rect.d = i6 + i4;
        return rect;
    }

    public static RectF a(DecalDefinition decalDefinition, DecalImageSlice decalImageSlice, Texture texture, float f, float f2) {
        int i2 = decalImageSlice.c;
        int i3 = decalImageSlice.d;
        float f3 = f - (i2 / 2);
        float f4 = f2 - (i3 / 2);
        RectF rectF = l;
        rectF.a = f3;
        rectF.c = f3 + i2;
        rectF.b = f4;
        rectF.d = f4 + i3;
        return rectF;
    }

    public final void a(CustomUnit customUnit, float f, DecalLayer decalLayer, FastArrayList fastArrayList) {
        a(customUnit, f, decalLayer, fastArrayList, (PointF) null);
    }


    public static final void a(
            CustomUnit jx, float float2, DecalLayer fx, FastArrayList m, PointF pointF
    ) {
        int var5 = m.size;
        if (var5 != 0) {
            boolean var6 = jx.isSelected;
            boolean var7 = false;
            BaseUnit var8 = jx.dr();
            if (var8 != null) {
                if (var8.isSelected) {
                    var7 = true;
                }

                BaseUnit var9 = var8.dr();
                if (var9 != null && var9.isSelected) {
                    var7 = true;
                }
            }

            GameEngine var39 = GameEngine.getInstance();
            float var10 = var39.zoom;
            boolean var11 = fx == DecalLayer.shadow;
            Object[] var12 = m.a();

            for (int var13 = 0; var13 < var5; var13++) {
                DecalDefinition var14 = (DecalDefinition)var12[var13];
                if ((!var14.b || var6 || var7) && !(var14.j > var10) && (!var14.k || jx.isAlive()) && (!var14.l || !jx.isDead)) {
                    if (var14.b) {
                        boolean var15 = var6;
                        if (var14.g && var7) {
                            var15 = true;
                        }

                        if (!var15) {
                            continue;
                        }

                        PlayerTeam var16 = var39.playerTeam;
                        PlayerTeam var17 = jx.team;
                        if (var16 != null && (var14.c && var17 != var16 || var14.d && !var17.c(var39.playerTeam) || var14.e && (!var17.d(var39.playerTeam) || var17 == var16))) {
                            continue;
                        }
                    }

                    if ((var14.F == null || var14.F.read(jx))
                            && (var14.o < 0 || jx.animationFrameIndex == var14.o)
                            && (!var14.i || var39.gameUI.canControlUnit(jx))
                            && (var14.h == TeamRelation.any || var14.h == null || var39.playerTeam == null || var39.playerTeam.a(var14.h, jx.team))
                            && (!var14.m || jx.isUnitParalyzed)
                            && (!var14.n || !jx.isUnitParalyzed)) {
                        float var18;
                        float var19;
                        float var40;
                        float var42;
                        float var44;
                        if (var14.ae != -1) {
                            LegInstance[] var20 = jx.legInstances;
                            LegConfig[] var21 = jx.unitConfig.legConfig;
                            if (var20 == null || var20.length <= var14.ae || var21 == null || var21.length <= var14.ae) {
                                continue;
                            }

                            LegInstance var22 = var20[var14.ae];
                            LegConfig var23 = var21[var14.ae];
                            var40 = jx.posX + var22.b;
                            var42 = jx.posY + var22.c;
                            var44 = jx.posZ + var22.d;
                            var18 = var22.i + var22.r + var23.R + 90.0F;
                            var19 = var22.i + var22.r + var23.R;
                            if (var14.af) {
                                float var24 = jx.rotationSpeed;
                                if (jx.unitConfig.lockLegRotationWithMainTurret) {
                                    var24 = jx.movementLevels[jx.unitConfig.mainTurretIndex].targetX;
                                }

                                float var25 = Utility.fastCos(var24);
                                float var26 = Utility.fastSin(var24);
                                float var27 = var25 * var23.k - var26 * var23.j;
                                float var28 = var26 * var23.k + var25 * var23.j;
                                float var29 = Utility.getAngleBetweenPoints(var22.b, var22.c, var27, var28);
                                float var30 = Utility.distanceSq(var22.b, var22.c, var27, var28);
                                var18 = var29 + 90.0F;
                                var19 = var29 + 90.0F;
                            }
                        } else if (var14.ag != -1) {
                            if (var14.ag >= jx.movementLevels.length) {
                                continue;
                            }

                            int var49 = var14.ag;
                            Vector3D var55 = jx.F(var49);
                            var40 = var55.a;
                            var42 = var55.b;
                            var44 = jx.posZ + var55.c;
                            var18 = jx.movementLevels[var49].targetX + 90.0F;
                            var19 = jx.movementLevels[var49].targetX;
                        } else {
                            Object var50;
                            if (var14.ac == null) {
                                var50 = jx;
                            } else {
                                var50 = var14.ac.readUnit(jx);
                                if (var50 == null) {
                                    continue;
                                }
                            }

                            var40 = ((BaseUnit)var50).posX;
                            var42 = ((BaseUnit)var50).posY;
                            var44 = ((BaseUnit)var50).posZ;
                            if (pointF != null && var14.ac == null) {
                                var40 = pointF.x;
                                var42 = pointF.y;
                                var18 = 0.0F;
                            }

                            var18 = ((BaseUnit)var50).rotationSpeed + 90.0F;
                            var19 = ((BaseUnit)var50).rotationSpeed;
                            if (var14.ac == null && jx.unitConfig.lockBodyRotationWithMainTurret) {
                                float var56 = jx.movementLevels[jx.unitConfig.mainTurretIndex].targetX;
                                var18 = var56 + 90.0F;
                                var19 = var56;
                            }
                        }

                        if (var14.Y) {
                            var18 = 0.0F;
                        }

                        if (var14.Z) {
                            var44 = 0.0F;
                        }

                        var19 += var14.ab;
                        var18 += var14.aa;
                        var40 += var14.T;
                        var42 += var14.U;
                        if (var14.W != null) {
                            var40 += var14.W.readNumber(jx);
                        }

                        if (var14.X != null) {
                            var42 += var14.X.readNumber(jx);
                        }

                        if (var14.R != 0.0F || var14.S != 0.0F) {
                            float var51 = Utility.fastCos(var19);
                            float var57 = Utility.fastSin(var19);
                            float var61 = var14.R;
                            float var65 = var14.S;
                            var40 += var51 * var65 - var57 * var61;
                            var42 += var57 * var65 + var51 * var61;
                        }

                        var44 += var14.V;
                        if (var11 && var14.C != null) {
                            GraphicsEngine var54 = var39.renderGraphicsEngine;
                            float var60 = var40 - var39.viewpointXSnapped + var14.D;
                            float var64 = var42 - var39.viewpointYSnapped + var14.E;
                            Paint var70 = jx.getSelectionPaint();
                            Texture var75 = var14.C;
                            var54.k();
                            var54.a(var18, var60, var64);
                            var54.a(var75, var60, var64, var70);
                            var54.l();
                            return;
                        }

                        if (var14.u) {
                            GraphicsEngine var52 = var39.renderGraphicsEngine;
                            float var58 = var40 - var39.viewpointXSnapped;
                            float var62 = var42 - var39.viewpointYSnapped - var44;
                            float var66 = var18;
                            Paint var68 = var14.ah;
                            if (var68 == null) {
                                var68 = jx.getRenderPaint();
                            }

                            if (var14.ai != null) {
                                float var71 = var14.ai.readNumber(jx);
                                if (var71 != 1.0F) {
                                    Paint var76 = a;
                                    var76.b(var68.e());
                                    var76.a(var68.c());
                                    int var80 = (int)(var68.f() * var71);
                                    if (var80 < 0) {
                                        var80 = 0;
                                    }

                                    if (var80 > 255) {
                                        var80 = 255;
                                    }

                                    var76.c(var80);
                                    var68 = var76;
                                }
                            }

                            int var72;
                            if (var14.N != null) {
                                var72 = (int)var14.N.readNumber(jx);
                            } else {
                                var72 = 0;
                            }

                            var72 += jx.animationFrameIndex * var14.O;
                            if (var14.v != null) {
                                DecalImageSlice var77 = var14.v;
                                Texture var81;
                                if (var77.b != null) {
                                    var81 = var77.b[jx.team.getTeamColorIndex()];
                                } else {
                                    var81 = var77.a;
                                }

                                var52.k();
                                var52.a(var18, var58, var62);
                                float var84 = var14.p;
                                float var88 = var14.p;
                                if (var14.q) {
                                    if (var14.r != null) {
                                        float var90 = var14.r.readNumber(jx);
                                        var84 = var90;
                                        var88 = var90;
                                    }

                                    if (var14.s != null) {
                                        var84 *= var14.s.readNumber(jx);
                                    }

                                    if (var14.t != null) {
                                        var88 *= var14.t.readNumber(jx);
                                    }
                                }

                                if (var84 != 1.0F || var88 != 1.0F) {
                                    var52.a(var84, var88, var58, var62);
                                }

                                if (!var14.I) {
                                    var52.a(var81, var58, var62, (Paint)var68);
                                } else {
                                    Rect var91 = a(var14, var77, var81, var72);
                                    RectF var31 = a(var14, var77, var81, var58, var62);
                                    var52.a(var81, var91, var31, (Paint)var68);
                                }

                                var52.l();
                            }

                            if (var14.w != null) {
                                float var78 = var14.p;
                                float var82 = var14.p;
                                if (var14.q) {
                                    if (var14.r != null) {
                                        float var85 = var14.r.readNumber(jx);
                                        var78 = var85;
                                        var82 = var85;
                                    }

                                    if (var14.s != null) {
                                        var78 *= var14.s.readNumber(jx);
                                    }

                                    if (var14.t != null) {
                                        var82 *= var14.t.readNumber(jx);
                                    }
                                }

                                DecalImageSlice[] var86 = var14.w;
                                int var89 = 0;
                                int var92 = var86.length;
                                if (var14.A != null) {
                                    var89 = (int)var14.A.readNumber(jx);
                                    if (var89 < 0) {
                                        var89 = 0;
                                    }

                                    if (var89 >= var86.length) {
                                        var89 = var86.length;
                                    }
                                }

                                if (var14.B != null) {
                                    int var93 = (int)var14.B.readNumber(jx);
                                    var92 = var89 + var93;
                                    if (var92 < 0) {
                                        var92 = 0;
                                    }

                                    if (var92 >= var86.length) {
                                        var92 = var86.length;
                                    }
                                }

                                for (int var94 = var89; var94 < var92; var94++) {
                                    int var32 = var94;
                                    if (var14.z) {
                                        var32 = var14.w.length - 1 - var94;
                                    }

                                    DecalImageSlice var33 = var86[var32];
                                    Texture var34;
                                    if (var33.b != null) {
                                        var34 = var33.b[jx.team.getTeamColorIndex()];
                                    } else {
                                        var34 = var33.a;
                                    }

                                    float var35 = var32 * var14.x;
                                    var52.k();
                                    var52.a(var66, var58, var62 - var35);
                                    if (var78 != 1.0F || var82 != 1.0F) {
                                        var52.a(var78, var82, var58, var62 - var35);
                                    }

                                    int var36 = var72 + var32 * var14.y;
                                    Rect var37 = a(var14, var33, var34, var36);
                                    RectF var38 = a(var14, var33, var34, var58, var62 - var35);
                                    var52.a(var34, var37, var38, (Paint)var68);
                                    var52.l();
                                }
                            }
                        }

                        BaseUnit var53 = null;
                        if (var14.ad != null) {
                            var53 = var14.ad.readUnit(jx);
                        }

                        if (var53 != null) {
                            float var59 = var40 - var39.viewpointXSnapped;
                            float var63 = var42 - var39.viewpointYSnapped - var44;
                            float var67 = var53.posX - var39.viewpointXSnapped;
                            float var69 = var53.posY - var39.viewpointYSnapped - var53.posZ;
                            Paint var74 = var14.ah;
                            if (var74 == null) {
                                var74 = b;
                            }

                            if (var14.ai != null) {
                                float var79 = var14.ai.readNumber(jx);
                                if (var79 != 1.0F) {
                                    Paint var83 = a;
                                    var83.b(var74.e());
                                    int var87 = (int)(var74.f() * var79);
                                    if (var87 < 0) {
                                        var87 = 0;
                                    }

                                    if (var87 > 255) {
                                        var87 = 255;
                                    }

                                    var83.c(var87);
                                    var74 = var83;
                                }
                            }

                            var39.renderGraphicsEngine.a(var59, var63, var67, var69, (Paint)var74);
                        }
                    }
                }
            }
        }
    }
}
