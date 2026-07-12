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
    public String a;
    public String b;
    public String c;
    public TriggerType g;
    public boolean h;
    public boolean i;
    public boolean j;
    public int k;
    public int l;
    public boolean m;
    public int p;
    public MapObject t;
    public UnitSpawner v;
    public float w;
    public float x;
    public PlayerTeam y;
    public LocaleString z;
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
        this.t.getDescription(str);
    }

    public String b(String str) {
        return this.t.getDescription(str);
    }

    public String a(String str, String str2) {
        return this.t.getPropertyOrDefault(str, str2);
    }

    public boolean c(String str) {
        return this.t.getDescription(str) != null;
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
        return this.t.createLocaleStringFromProperty(str, localeString);
    }

    public boolean a(BaseUnit baseUnit) {
        return this.t.containsUnitPosition(baseUnit);
    }

    public MapLoadException f(String str) {
        return a(str, (Exception) null);
    }

    public MapLoadException a(String str, Exception exc) {
        String str2 = "MapTrigger-Error (" + this.a + " id:" + this.b + "): " + str;
        NetworkEngine.reportDesync(str2);
        if (exc == null) {
            return new MapLoadException(str2);
        }
        return new MapLoadException(str2, exc);
    }

    public void g(String str) {
        NetworkEngine.reportDesync("MapTrigger-Error (" + this.a + " id:" + this.b + " type:" + this.g + "): " + str);
    }

    public void h(String str) {
        GameEngine.log("MapTrigger-Debug (" + this.b + " type:" + this.g + "): " + str);
    }

    public PlayerTeam a() {
        return this.y;
    }

    public int b() {
        return (int) this.t.tileRect.d();
    }

    public int c() {
        return (int) this.t.tileRect.e();
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
        if (!this.m && this.r != -1) {
            if (this.r <= i) {
                z3 = true;
                this.m = true;
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
        if (this.h) {
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
