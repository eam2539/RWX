package com.corrodinggames.rts.game.units.custom.condition;

import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfigParser;
import com.corrodinggames.rts.game.units.custom.LocaleString;
import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.e.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/e/d.class */
public class ResourceDefinition {
    public String a;
    public Resource b;
    public float c;
    public Integer d;
    public boolean e;
    public boolean f;
    public LocaleString g;
    public LocaleString h;
    public boolean i;
    public boolean j;
    public boolean k;
    public boolean l;
    public float m;
    public boolean n;
    public boolean o;
    public boolean p;
    public boolean q;
    public DigitGroupingStyle r = DigitGroupingStyle.none;
    public int s;
    public LocaleString t;
    public LocaleString u;
    public String v;
    public Resource w;
    public boolean x;
    public boolean y;
    public String z;
    public Resource A;
    public Texture B;
    public boolean C;

    public ResourceDefinition(boolean z) {
        this.f = z;
    }

    public void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2) throws ConfigParseException {
        this.a = str2;
        this.g = CustomUnitConfigParser.getLocaleString(iniFile, str, "displayName", (String) null);
        this.h = CustomUnitConfigParser.getLocaleString(iniFile, str, "displayNameShort", (String) null);
        if (this.h == null) {
            this.h = this.g;
        }
        this.i = iniFile.getBoolean(str, "displayNameHideWhenIconShownInHUD", (Boolean) false).booleanValue();
        this.j = iniFile.getBoolean(str, "displayNameHideWhenIconShownInText", (Boolean) false).booleanValue();
        this.l = iniFile.getBoolean(str, "hidden", (Boolean) false).booleanValue();
        float f = 1.0f;
        boolean zBooleanValue = iniFile.getBoolean(str, "includeInStats", (Boolean) true).booleanValue();
        if (!zBooleanValue) {
            f = 0.0f;
        }
        if (this.l || !this.f) {
            f = 0.0f;
        }
        this.m = iniFile.getFloat(str, "valueInStats", Float.valueOf(f)).floatValue();
        if (!zBooleanValue && this.m != 0.0f) {
            throw new ConfigParseException("[" + str + "]includeInStats==false expects valueInStats==0");
        }
        if (this.m < 0.0f) {
            throw new ConfigParseException("[" + str + "]valueInStats cannot be < 0 (is:" + this.m + ")");
        }
        this.k = iniFile.getBoolean(str, "stackHorizontal", (Boolean) false).booleanValue();
        this.c = iniFile.getFloat(str, "priority", Float.valueOf(0.0f)).floatValue();
        this.d = iniFile.getColorAsInt(str, "displayColor", (Integer) null);
        this.e = iniFile.getBoolean(str, "displayColorUseInText", (Boolean) true).booleanValue();
        this.n = iniFile.getBoolean(str, "displayWithRounding", (Boolean) true).booleanValue();
        this.o = iniFile.getBoolean(str, "displayRoundedDown", (Boolean) false).booleanValue();
        this.p = iniFile.getBoolean(str, "displayWhenZero", (Boolean) false).booleanValue();
        this.q = iniFile.getBoolean(str, "displayInHud", Boolean.valueOf(!this.l && this.f)).booleanValue();
        if (this.q && !this.f) {
            throw new ConfigParseException("[" + str + "]displayInHud:true currently only supported on global resources");
        }
        if (this.q && this.l) {
            throw new ConfigParseException("[" + str + "]displayInHud:true only supported non-hidden resources");
        }
        this.s = iniFile.getInt(str, "displayPos", (Integer) 0).intValue();
        this.r = (DigitGroupingStyle) iniFile.getEnum(str, "displayDigitGrouping", DigitGroupingStyle.none, DigitGroupingStyle.class);
        this.t = CustomUnitConfigParser.getLocaleString(iniFile, str, "displayTextPrefix", (String) null);
        this.u = CustomUnitConfigParser.getLocaleString(iniFile, str, "displayTextPostfix", (String) null);
        LocaleString localeStringHandleUnitLoadError = CustomUnitConfigParser.getLocaleString(iniFile, str, "displayPrefixInHUD", (String) null);
        if (localeStringHandleUnitLoadError != null) {
            if (this.t != null) {
                throw new ConfigParseException("[" + str + "]displayPrefixInHUD and displayTextPrefix are aliases, don't use both");
            }
            this.t = localeStringHandleUnitLoadError;
        }
        LocaleString localeStringHandleUnitLoadError2 = CustomUnitConfigParser.getLocaleString(iniFile, str, "displayPostfixInHUD", (String) null);
        if (localeStringHandleUnitLoadError2 != null) {
            if (this.u != null) {
                throw new ConfigParseException("[" + str + "]displayPostfixInHUD and displayTextPostfix are aliases, don't use both");
            }
            this.u = localeStringHandleUnitLoadError2;
        }
        this.v = iniFile.getString(str, "displayTextAppendResource", (String) null);
        String string = iniFile.getString(str, "appendResourceInHUD", (String) null);
        if (string != null) {
            if (this.v != null) {
                throw new ConfigParseException("[" + str + "]displayTextAppendResource and appendResourceInHUD are aliases, don't use both");
            }
            this.v = string;
        }
        this.x = iniFile.getBoolean(str, "displayTextAppendResourceWithGap", (Boolean) false).booleanValue();
        this.y = iniFile.getBoolean(str, "appendResourceInHUD_whenThisZero", (Boolean) true).booleanValue();
        this.B = customUnitConfig.a(iniFile, str, "iconImage", true);
        if (this.B != null && (this.B.m() > 100 || this.B.l() > 100)) {
            throw new ConfigParseException("[" + str + "]iconImage: Image is too big, keep under 100x100");
        }
        this.C = iniFile.getBoolean(str, "iconImageUseInText", (Boolean) true).booleanValue();
        if (this.i && this.B == null) {
            throw new ConfigParseException("[" + str + "]displayNameHideWhenIconShownInHUD: Cannot use without iconImage");
        }
        if (this.j && this.B == null) {
            throw new ConfigParseException("[" + str + "]displayNameHideWhenIconShownInText: Cannot use without iconImage");
        }
        String str3 = (this.f ? "g_" : "l_") + this.a;
        this.b = Resource.a(str3, false, this.f);
        if (this.b.u) {
            throw new RuntimeException("Cannot define resource with a built-in name: " + str3);
        }
        if (!this.f) {
            this.z = iniFile.getString(str, "equivalentGlobalResourceForAI", (String) null);
        }
    }

    public void a(CustomUnitConfig customUnitConfig) throws ConfigParseException {
        if (this.z != null) {
            this.A = customUnitConfig.findCustomResourceInList(this.z);
            if (this.A == null) {
                throw new ConfigParseException("[resource]equivalentGlobalResourceForAI: Failed to find resource: " + this.z);
            }
            if (!this.A.t) {
                throw new ConfigParseException("[resource]equivalentGlobalResourceForAI: Expected global resource for: " + this.z);
            }
        }
        if (this.v != null) {
            this.w = customUnitConfig.findCustomResourceInList(this.v);
            if (this.w == null) {
                throw new ConfigParseException("[resource]displayTextAppendResource: Failed to find resource: " + this.v);
            }
        }
    }
}
