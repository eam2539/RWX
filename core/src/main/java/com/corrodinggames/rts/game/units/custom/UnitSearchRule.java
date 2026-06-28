package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.TeamRelation;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bg */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bg.class */
public final class UnitSearchRule {
    String a;
    AnimationTag b;
    AnimationSet c;
    TeamRelation d;
    float e;
    float f;
    float g;
    float h;
    boolean i;
    boolean j;
    int k;
    int l;
    boolean m;
    boolean n;
    LocaleString o;
    boolean p;

    public boolean a() {
        return this.n || this.m;
    }

    public void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str) throws ConfigParseException {
        this.b = iniFile.getAnimationTag(str, "anyRuleInGroup", (AnimationTag) null);
        this.c = iniFile.getAnimationSet(customUnitConfig, str, "searchTags", (AnimationSet) null);
        this.d = (TeamRelation) iniFile.getEnum(str, "searchTeam", TeamRelation.own, TeamRelation.class);
        this.e = iniFile.getFloatStrictRaw(str, "searchDistance");
        this.f = this.e * this.e;
        this.g = iniFile.getFloat(str, "searchOffsetX", Float.valueOf(0.0f)).floatValue();
        this.h = iniFile.getFloat(str, "searchOffsetY", Float.valueOf(0.0f)).floatValue();
        this.i = iniFile.getBoolean(str, "excludeIncompleteBuildings", (Boolean) false).booleanValue();
        this.j = iniFile.getBoolean(str, "excludeNonBuildings", (Boolean) false).booleanValue();
        this.k = iniFile.getLogicBooleanUnit(str, "minCount", (Integer) Integer.MIN_VALUE).intValue();
        this.l = iniFile.getLogicBooleanUnit(str, "maxCount", (Integer) Integer.MAX_VALUE).intValue();
        this.p = iniFile.getBoolean(str, "checkEachTile", (Boolean) true).booleanValue();
        this.m = iniFile.getBoolean(str, "aiSuggestionOnly", (Boolean) false).booleanValue();
        this.n = iniFile.getBoolean(str, "blocksPlacement", Boolean.valueOf(!this.m)).booleanValue();
        if (this.m && this.n) {
            throw new ConfigParseException("[" + str + "]: Cannot use aiSuggestionOnly and blocksPlacement at the same time");
        }
        this.o = CustomUnitConfigParser.handleUnitLoadError(iniFile, str, "cannotPlaceMessage", (String) null);
    }
}
