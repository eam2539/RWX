package com.corrodinggames.rts.gameFramework.mission;

import android.graphics.Color;
import android.graphics.Paint;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.map.MapObject;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.TransportUnitInterface;
import com.corrodinggames.rts.game.units.custom.LocaleString;
import com.corrodinggames.rts.game.units.custom.UnitSpawner;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.mission.conditions.TriggerCondition;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/a.class */
public class MapTrigger {
        /* JADX INFO: renamed from: a */
    public String name;
        /* JADX INFO: renamed from: b */
    public String rawId;
        /* JADX INFO: renamed from: c */
    public String uniqueId;
        /* JADX INFO: renamed from: g */
    public TriggerType triggerType;
        /* JADX INFO: renamed from: h */
    public boolean allToActivate;
    public boolean i;
        /* JADX INFO: renamed from: j */
    public boolean isActive;
        /* JADX INFO: renamed from: k */
    public int activationTime;
    public int l;
        /* JADX INFO: renamed from: m */
    public boolean hasCompleted;
        /* JADX INFO: renamed from: p */
    public int repeatDelay;
        /* JADX INFO: renamed from: t */
    public MapObject mapObject;
    public UnitSpawner v;
        /* JADX INFO: renamed from: w */
    public float textOffsetX;
        /* JADX INFO: renamed from: x */
    public float textOffsetY;
        /* JADX INFO: renamed from: y */
    public PlayerTeam team;
        /* JADX INFO: renamed from: z */
    public LocaleString text;
    public LocaleString A;
    public Paint B;
    public boolean C;
    public TriggerGroup d = new TriggerGroup();
    public TriggerGroup e = new TriggerGroup();
    public FastArrayList<TriggerCondition> f = new FastArrayList();
    public int n = -1;
    public int o = Integer.MAX_VALUE;
    public int q = -1;
    public int r = -1;
    public int s = -1;
    public boolean u = false;

    public void a(TriggerCondition triggerCondition) {
        this.f.add(triggerCondition);
    }

    public void a(String str) {
        this.mapObject.getDescription(str);
    }

    public String b(String str) {
        return this.mapObject.getDescription(str);
    }

    public String a(String str, String str2) {
        return this.mapObject.getPropertyOrDefault(str, str2);
    }

    public boolean c(String str) {
        return this.mapObject.getDescription(str) != null;
    }

    public int a(String str, int i) throws MapLoadException {
        String strA = a(str, (String) null);
        if (strA == null) {
            return i;
        }
        try {
            return Integer.parseInt(strA);
        } catch (NumberFormatException e) {
            throw f(str + ": Unexpected integer value:'" + strA + "'");
        }
    }

    public int b(String str, int i) throws MapLoadException {
        double d;
        String strB = b(str);
        if (strB == null) {
            return i;
        }
        if (strB.endsWith("ms")) {
            strB = strB.substring(0, strB.length() - 2);
            d = 1.0d;
        } else if (strB.endsWith("s")) {
            strB = strB.substring(0, strB.length() - 1);
            d = 1000.0d;
        } else {
            d = 1.0d;
        }
        try {
            return (int) (Double.parseDouble(strB) * d);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw f(str + ": Unexpected time:'" + strB + "'");
        }
    }

    public float a(String str, float f) throws MapLoadException {
        String strA = a(str, (String) null);
        if (strA == null) {
            return f;
        }
        try {
            return Float.parseFloat(strA);
        } catch (NumberFormatException e) {
            throw f(str + ": Unexpected float value:'" + strA + "'");
        }
    }

    public Integer d(String str) throws MapLoadException {
        String strA = a(str, (String) null);
        if (strA == null) {
            return null;
        }
        try {
            return Integer.valueOf(Integer.parseInt(strA));
        } catch (NumberFormatException e) {
            throw f(str + ": Unexpected integer value:'" + strA + "'");
        }
    }

    public Boolean e(String str) throws MapLoadException {
        String strA = a(str, (String) null);
        if (strA == null) {
            return null;
        }
        if (strA.equalsIgnoreCase("true")) {
            return true;
        }
        if (strA.equalsIgnoreCase("false")) {
            return false;
        }
        throw f(str + ": Unexpected boolean value:'" + strA + "'");
    }

    public boolean a(String str, String str2, boolean z) throws MapLoadException {
        Boolean boolE = e(str);
        if (boolE != null) {
            return boolE.booleanValue();
        }
        Boolean boolE2 = e(str2);
        if (boolE2 != null) {
            return boolE2.booleanValue();
        }
        return z;
    }

    public boolean a(String str, boolean z) throws MapLoadException {
        String strA = a(str, (String) null);
        if (strA == null) {
            return z;
        }
        if (strA.equalsIgnoreCase("true")) {
            return true;
        }
        if (strA.equalsIgnoreCase("false")) {
            return false;
        }
        throw f(str + ": Unexpected boolean value:'" + strA + "'");
    }

    public int c(String str, int i) throws MapLoadException {
        String strB = b(str);
        if (strB == null) {
            return i;
        }
        if (strB.equals(VariableScope.nullOrMissingString)) {
            throw f(str + ": Unknown color:" + strB);
        }
        try {
            return Color.a(strB);
        } catch (IllegalArgumentException e) {
            throw f(str + ": Unknown color:" + strB);
        }
    }

    public LocaleString a(String str, LocaleString localeString) {
        return this.mapObject.createLocaleStringFromProperty(str, localeString);
    }

    public boolean a(BaseUnit baseUnit) {
        return this.mapObject.containsUnitPosition(baseUnit);
    }

    public MapLoadException f(String str) {
        return a(str, (Exception) null);
    }

    public MapLoadException a(String str, Exception exc) {
        String str2 = "MapTrigger-Error (" + this.name + " id:" + this.rawId + "): " + str;
        NetworkEngine.reportDesync(str2);
        if (exc == null) {
            return new MapLoadException(str2);
        }
        return new MapLoadException(str2, exc);
    }

    public void g(String str) {
        NetworkEngine.reportDesync("MapTrigger-Error (" + this.name + " id:" + this.rawId + " type:" + this.triggerType + "): " + str);
    }

    public void h(String str) {
        GameEngine.log("MapTrigger-Debug (" + this.rawId + " type:" + this.triggerType + "): " + str);
    }

    public PlayerTeam a() {
        return this.team;
    }

    public int b() {
        return (int) this.mapObject.tileRect.d();
    }

    public int c() {
        return (int) this.mapObject.tileRect.e();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean b(BaseUnit baseUnit) {
        PlayerTeam playerTeamA = a();
        if (playerTeamA != null && baseUnit.team != playerTeamA) {
            return false;
        }
        if (c("onlyIfEmpty") && baseUnit.canTransportUnits() && (baseUnit instanceof TransportUnitInterface) && ((TransportUnitInterface) baseUnit).getTransportedUnitCount() > 0) {
            return false;
        }
        return true;
    }

    public boolean d() {
        boolean z;
        int i = GameEngine.getInstance().gameTimeMillis;
        boolean z2 = true;
        boolean z3 = false;
        if (!this.hasCompleted && this.r != -1) {
            if (this.r <= i) {
                z3 = true;
                this.hasCompleted = true;
            } else {
                z2 = false;
            }
        }
        if (this.d.a()) {
            if (this.d.b()) {
                z3 = true;
            } else {
                z2 = false;
            }
        }
        if (this.f.size > 0) {
            for (TriggerCondition triggerCondition : this.f) {
                if (triggerCondition.a(this)) {
                    if (triggerCondition.b(this)) {
                        z3 = true;
                    } else {
                        z2 = false;
                    }
                }
            }
        }
        if (this.allToActivate) {
            z = z3 && z2;
        } else {
            z = z3;
            if (z2) {
                z = true;
            }
        }
        if (z) {
            if (this.n == -1) {
                this.n = i;
            }
            if (this.s <= 0 || i >= this.n + this.s) {
                return true;
            }
            return false;
        }
        this.n = -1;
        return false;
    }
}
