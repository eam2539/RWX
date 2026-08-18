package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/f.class */
public class AnimationConfig {

    /* JADX INFO: renamed from: a */
    public String animationName;
    public int b;
    public int c;
    public float d;
    public float e;
    public float f;
    public boolean g;
    public float h;
    public float i;
    public LogicBoolean j;
    public CustomUnitDirectionConfig k;
    public float n;
    public boolean o;
    public float q;
    public FastArrayList<CustomUnitAnimationSet> l = new FastArrayList();
    public boolean m = true;
    public ArrayList p = new ArrayList();

    public AnimationConfig(String str) {
        this.animationName = str;
    }

    public void a(CustomUnitConfig customUnitConfig) throws ConfigParseException {
        for (CustomUnitAnimationSet customUnitAnimationSet : this.l) {
            if (customUnitAnimationSet.animationType == CustomUnitAnimationType.legX || customUnitAnimationSet.animationType == CustomUnitAnimationType.legY || customUnitAnimationSet.animationType == CustomUnitAnimationType.legHeight || customUnitAnimationSet.animationType == CustomUnitAnimationType.legDir || customUnitAnimationSet.animationType == CustomUnitAnimationType.legAlpha) {
                boolean z = false;
                LegConfig[] legConfigArr = customUnitConfig.legConfig;
                int length = legConfigArr.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    LegConfig legConfig = legConfigArr[i];
                    if (!customUnitAnimationSet.animationName.equals(legConfig.b)) {
                        i++;
                    } else {
                        customUnitAnimationSet.animationId = legConfig.a;
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    throw new ConfigParseException("Cannot find leg:" + customUnitAnimationSet.animationName + " for animation:" + this.animationName);
                }
            }
            if (customUnitAnimationSet.animationId < 0) {
                throw new ConfigParseException("Cannot find target for:" + customUnitAnimationSet.animationName + " for animation:" + this.animationName);
            }
        }
    }

    public boolean a(CustomUnitAction customUnitAction) {
        Iterator it = this.p.iterator();
        while (it.hasNext()) {
            if (((CustomUnitAction) it.next()) == customUnitAction) {
                return true;
            }
        }
        return false;
    }

    public boolean a() {
        return this.o;
    }

    public void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2) throws ConfigParseException {
        boolean z = false;
        String str3 = null;
        String string = iniFile.getString(str, str2 + "onActions", (String) null);
        if (string != null) {
            for (String str4 : string.split(",")) {
                String strTrim = str4.trim();
                if (!strTrim.equals(VariableScope.nullOrMissingString)) {
                    CustomUnitAction customUnitActionA = CustomUnitAction.a(strTrim);
                    if (customUnitActionA == null) {
                        throw new ConfigParseException("Unknown action type: " + strTrim + " on animation:" + this.animationName);
                    }
                    AnimationConfig animationConfigFindAnimationForAction = customUnitConfig.findAnimationForAction(customUnitActionA);
                    if (animationConfigFindAnimationForAction != null) {
                        throw new ConfigParseException("Cannot add action: " + strTrim + " to:" + this.animationName + " it already exists on:" + animationConfigFindAnimationForAction.animationName);
                    }
                    this.p.add(customUnitActionA);
                }
            }
        }
        this.q = iniFile.getFloat(str, str2 + "onActionsQueuedUnitPlayAt", Float.valueOf(0.0f)).floatValue();
        this.b = iniFile.getInt(str, str2 + "start", (Integer) 0).intValue();
        this.c = iniFile.getInt(str, str2 + "end", (Integer) (-1)).intValue();
        if (this.c != -1 && this.c < this.b) {
            throw new RuntimeException("animationEnd cannot before animationStart on animation:" + this.animationName);
        }
        this.k = CustomUnitDirectionConfig.a(customUnitConfig, iniFile, str, VariableScope.nullOrMissingString, true);
        this.h = iniFile.getInvertedTime(str, str2 + "blendIn", Float.valueOf(-1.0f)).floatValue();
        this.i = iniFile.getInvertedTime(str, str2 + "blendOut", Float.valueOf(-1.0f)).floatValue();
        this.j = iniFile.getLogicBooleanWithReturnType(customUnitConfig, str, str2 + "playbackRate", (LogicBoolean) null, LogicBoolean.ReturnType.number);
        this.d = iniFile.getFloat(str, str2 + "scale_start", Float.valueOf(1.0f)).floatValue();
        this.e = iniFile.getFloat(str, str2 + "scale_end", Float.valueOf(1.0f)).floatValue();
        Float f = iniFile.getFloat(str, str2 + "speed", (Float) null);
        if (f != null) {
            this.f = f.floatValue();
            z = true;
            str3 = "speed";
        } else {
            this.f = 40.0f;
        }
        this.g = iniFile.getBoolean(str, str2 + "pingPong", (Boolean) false).booleanValue();
        float f2 = 1.0f * this.f;
        float fFloatValue = iniFile.getFloat(str, str2 + "KeyframeTimeScale", Float.valueOf(1.0f)).floatValue();
        if (this.c != -1) {
            z = true;
            str3 = "animationEnd";
            CustomUnitAnimationSet customUnitAnimationSet = new CustomUnitAnimationSet();
            customUnitAnimationSet.animationType = CustomUnitAnimationType.frame;
            this.l.add(customUnitAnimationSet);
            f2 *= (this.c - this.b) + 1;
            customUnitAnimationSet.a(0.0f, this.b);
            customUnitAnimationSet.a(f2, this.c + 0.99f);
        }
        if (this.d != 1.0f || this.e != 1.0f) {
            z = true;
            str3 = "animationScaleX";
            CustomUnitAnimationSet customUnitAnimationSet2 = new CustomUnitAnimationSet();
            customUnitAnimationSet2.animationType = CustomUnitAnimationType.scale;
            this.l.add(customUnitAnimationSet2);
            customUnitAnimationSet2.a(0.0f, this.d);
            customUnitAnimationSet2.a(f2, this.e);
        }
        if (z) {
            this.n = f2;
        }
        FastArrayList<String> keysStartingWithTwoPrefixes = iniFile.getKeysStartingWithTwoPrefixes(str, str2 + "leg", str2 + "arm");
        keysStartingWithTwoPrefixes.addAll(iniFile.getKeysStartingWith(str, str2 + "turret"));
        keysStartingWithTwoPrefixes.addAll(iniFile.getKeysStartingWith(str, str2 + "body"));
        keysStartingWithTwoPrefixes.addAll(iniFile.getKeysStartingWith(str, str2 + "effect"));
        for (String str5 : keysStartingWithTwoPrefixes) {
            if (0 == 0) {
                if (z) {
                    throw new ConfigParseException("Cannot mix new (" + str5 + ") and old style (" + str3 + ") animations on:" + this.animationName);
                }
                a(customUnitConfig, iniFile, str, str2, str5);
            }
        }
        FastArrayList fastArrayList = new FastArrayList();
        this.m = false;
        for (CustomUnitAnimationSet customUnitAnimationSet3 : this.l) {
            customUnitAnimationSet3.a(fFloatValue);
            customUnitAnimationSet3.c();
            if (this.n < customUnitAnimationSet3.maxTime) {
                this.n = customUnitAnimationSet3.maxTime;
            }
            if (customUnitAnimationSet3.e.length > 0) {
                this.o = true;
                if (customUnitAnimationSet3.animationType != CustomUnitAnimationType.frame && customUnitAnimationSet3.animationType != CustomUnitAnimationType.scale) {
                    this.m = true;
                }
                fastArrayList.add(customUnitAnimationSet3);
            }
        }
        this.l = fastArrayList;
    }

    public CustomUnitAnimationSet a(String str, String str2) throws ConfigParseException {
        int i;
        CustomUnitAnimationType customUnitAnimationType;
        if (str2.startsWith("leg") || str2.startsWith("arm")) {
            i = -1;
            if (str.equalsIgnoreCase("x")) {
                customUnitAnimationType = CustomUnitAnimationType.legX;
            } else if (str.equalsIgnoreCase("y")) {
                customUnitAnimationType = CustomUnitAnimationType.legY;
            } else if (str.equalsIgnoreCase("dir")) {
                customUnitAnimationType = CustomUnitAnimationType.legDir;
            } else if (str.equalsIgnoreCase("height")) {
                customUnitAnimationType = CustomUnitAnimationType.legHeight;
            } else if (str.equalsIgnoreCase("alpha")) {
                customUnitAnimationType = CustomUnitAnimationType.legAlpha;
            } else {
                throw new ConfigParseException("Unknown leg/arm animation type:" + str + " on animation:" + this.animationName);
            }
        } else if (str2.startsWith("turret")) {
            i = Integer.parseInt(str2.substring("turret".length())) - 1;
            if (str.equalsIgnoreCase("x")) {
                customUnitAnimationType = CustomUnitAnimationType.turretX;
            } else if (str.equalsIgnoreCase("y")) {
                customUnitAnimationType = CustomUnitAnimationType.turretY;
            } else {
                throw new ConfigParseException("Unknown turret animation type:" + str + " on animation:" + this.animationName);
            }
        } else if (str2.startsWith("body")) {
            i = 0;
            if (str.equalsIgnoreCase("scale")) {
                customUnitAnimationType = CustomUnitAnimationType.scale;
            } else if (str.equalsIgnoreCase("frame")) {
                customUnitAnimationType = CustomUnitAnimationType.frame;
            } else {
                throw new ConfigParseException("Unknown body animation type:" + str + " on animation:" + this.animationName);
            }
        } else if (str2.startsWith("effect")) {
            i = 0;
            customUnitAnimationType = CustomUnitAnimationType.event;
            str2 = "event";
        } else {
            throw new ConfigParseException("Unknown animation target:" + str2 + " on animation:" + this.animationName);
        }
        for (CustomUnitAnimationSet customUnitAnimationSet : this.l) {
            if (customUnitAnimationSet.animationType == customUnitAnimationType && str2.equals(customUnitAnimationSet.animationName)) {
                return customUnitAnimationSet;
            }
        }
        CustomUnitAnimationSet customUnitAnimationSet2 = new CustomUnitAnimationSet();
        customUnitAnimationSet2.animationType = customUnitAnimationType;
        customUnitAnimationSet2.animationId = i;
        customUnitAnimationSet2.animationName = str2;
        this.l.add(customUnitAnimationSet2);
        return customUnitAnimationSet2;
    }

    public void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, String str3) throws ConfigParseException {
        String str4 = str3.substring(str2.length()).split("_")[0];
        String strSubstring = str3.substring((str2 + str4 + "_").length());
        try {
            float unitValue = IniFile.parseUnitValue(strSubstring, false, str, str3);
            String valueStrict = iniFile.getValueStrict(str, str3);
            if (!valueStrict.startsWith("{") || !valueStrict.endsWith("}")) {
                throw new ConfigParseException("Unknown format:" + valueStrict, str, str3);
            }
            String strSubstring2 = valueStrict.substring(1, valueStrict.length() - 1);
            CustomUnitAnimationSet customUnitAnimationSet = null;
            for (String str5 : strSubstring2.split(",")) {
                String[] strArrSplit = str5.split(":");
                if (strArrSplit.length != 2) {
                    throw new ConfigParseException("Unknown format on part:" + str5 + " of: " + strSubstring2, str, str3);
                }
                String strTrim = strArrSplit[0].trim();
                String strTrim2 = strArrSplit[1].trim();
                CustomUnitAnimationSet customUnitAnimationSetA = a(strTrim, str4);
                if (customUnitAnimationSet != customUnitAnimationSetA) {
                    if (customUnitAnimationSet != null) {
                        customUnitAnimationSet.b();
                    }
                    customUnitAnimationSet = customUnitAnimationSetA;
                }
                try {
                    customUnitAnimationSetA.a(customUnitConfig, unitValue, strTrim, strTrim2);
                } catch (ConfigParseException e) {
                    throw new ConfigParseException(e.getMessage() + " (as part of key:" + str3 + " section:" + str + ")", e);
                }
            }
            if (customUnitAnimationSet != null) {
                customUnitAnimationSet.b();
            }
        } catch (NumberFormatException e2) {
            throw new ConfigParseException("Failed to read time:" + strSubstring + " in key:" + str3 + " section:" + str + " expected a float with optional 's' or 'ms' postfix");
        }
    }
}
