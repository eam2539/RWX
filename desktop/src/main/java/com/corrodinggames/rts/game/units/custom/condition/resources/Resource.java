package com.corrodinggames.rts.game.units.custom.condition.resources;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.LocaleString;
import com.corrodinggames.rts.game.units.custom.condition.DigitGroupingStyle;
import com.corrodinggames.rts.game.units.custom.condition.ResourceDefinition;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.e.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/e/a.class */
public class Resource {
    public boolean a;
    public String b;
    protected LocaleString c;
    protected LocaleString d;
    protected boolean e;
    protected boolean f;
    protected LocaleString g;
    protected LocaleString h;
    public Resource i;
    public boolean j;
    public boolean k;
    public Integer m;
    public boolean n;
    public boolean o;
    public boolean p;
    boolean r;
    float s;
    public boolean t;
    public boolean u;
    public Resource v;
    public boolean w;
    public float x;
    public Texture y;
    public boolean z;
    static ArrayList<Resource> A = new ArrayList();
    static ArrayList B = new ArrayList();
    public static ArrayList<Resource> C = new ArrayList();
    public static final Resource D = a(new CreditsResource());
    public static final Resource E = a(new EnergyResource());
    public static final Resource F = a(new AmmoResource());
    public static final Resource H = a(new ShieldResource());
    public static final Resource G = a(new HpResource());
    public boolean l = true;
    public DigitGroupingStyle q = DigitGroupingStyle.none;

    public boolean a() {
        return this.r;
    }

    public float b() {
        return this.s;
    }

    public boolean c() {
        return this.u;
    }

    public boolean d() {
        return this.t;
    }

    public static void e() {
        Iterator it = A.iterator();
        while (it.hasNext()) {
            ((Resource) it.next()).g();
        }
        ArrayList arrayList = new ArrayList();
        for (Resource resource : A) {
            if (resource.a) {
                arrayList.add(resource);
            }
        }
        B = arrayList;
    }

    public static ArrayList<Resource> f() {
        return B;
    }

    public void g() {
        if (this.u) {
            this.a = true;
            return;
        }
        ResourceDefinition resourceDefinition = null;
        Iterator it = CustomUnitConfig.activeConfigs.iterator();
        while (it.hasNext()) {
            ResourceDefinition resourceDefinitionA = ((CustomUnitConfig) it.next()).a(this);
            if (resourceDefinitionA != null && (resourceDefinition == null || resourceDefinition.c < resourceDefinitionA.c)) {
                resourceDefinition = resourceDefinitionA;
            }
        }
        this.a = resourceDefinition != null;
        if (resourceDefinition != null) {
            this.c = resourceDefinition.g;
            this.d = resourceDefinition.h;
            this.e = resourceDefinition.i;
            this.f = resourceDefinition.j;
            this.m = resourceDefinition.d;
            this.n = resourceDefinition.e;
            this.o = resourceDefinition.o;
            this.p = resourceDefinition.p;
            this.q = resourceDefinition.r;
            this.g = resourceDefinition.t;
            this.h = resourceDefinition.u;
            this.i = resourceDefinition.w;
            this.j = resourceDefinition.y;
            this.l = resourceDefinition.q;
            this.k = resourceDefinition.x;
            this.r = resourceDefinition.l;
            this.s = resourceDefinition.m;
            this.v = resourceDefinition.A;
            this.w = resourceDefinition.k;
            this.x = resourceDefinition.s;
            this.y = resourceDefinition.B;
            this.z = resourceDefinition.C;
        }
    }

    public Integer h() {
        return this.m;
    }

    public String i() {
        if (this.c == null) {
            return this.b;
        }
        return this.c.resolveText();
    }

    public String j() {
        if (this.d != null) {
            return this.d.resolveText();
        }
        return i();
    }

    public String a(double d, boolean z) {
        String strMd5;
        if (this.o) {
            strMd5 = VariableScope.nullOrMissingString + ((int) d);
        } else {
            strMd5 = Utility.md5(d);
        }
        return a(z) + a(strMd5, this.q) + b(z);
    }

    public static String a(String str, DigitGroupingStyle digitGroupingStyle) {
        String str2;
        if (digitGroupingStyle == DigitGroupingStyle.none) {
            return str;
        }
        if (GameEngine.getInstance().settingsEngine.disableDigitGrouping) {
            return str;
        }
        String strSubstring = str;
        String strSubstring2 = VariableScope.nullOrMissingString;
        int iIndexOf = strSubstring.indexOf(".");
        if (iIndexOf != -1) {
            strSubstring2 = strSubstring.substring(iIndexOf);
            strSubstring = strSubstring.substring(0, iIndexOf);
        }
        if (strSubstring.length() <= 3) {
            return str;
        }
        if (digitGroupingStyle == DigitGroupingStyle.space) {
            str2 = " ";
        } else if (digitGroupingStyle == DigitGroupingStyle.comma) {
            str2 = ",";
        } else {
            throw new RuntimeException("Unhandled grouping style: " + digitGroupingStyle);
        }
        StringBuilder sb = new StringBuilder();
        int length = strSubstring.length() % 3;
        if (length != 0) {
            sb.append(strSubstring.substring(0, length));
        }
        for (int i = length; i < strSubstring.length(); i += 3) {
            if (i != 0) {
                sb.append(str2);
            }
            sb.append(strSubstring.substring(i, i + 3));
        }
        if (strSubstring2 == VariableScope.nullOrMissingString) {
            return sb.toString();
        }
        return sb.toString() + strSubstring2;
    }

    public static String a(long j, DigitGroupingStyle digitGroupingStyle) {
        if (digitGroupingStyle == DigitGroupingStyle.none) {
            return VariableScope.nullOrMissingString + j;
        }
        if (digitGroupingStyle == DigitGroupingStyle.space) {
            return String.format(Locale.US, "%,d", Long.valueOf(j)).replace(",", " ");
        }
        if (digitGroupingStyle == DigitGroupingStyle.comma) {
            return String.format(Locale.US, "%,d", Long.valueOf(j));
        }
        throw new RuntimeException("Unhandled grouping style: " + digitGroupingStyle);
    }

    public String a(boolean z) {
        if (this.g != null) {
            return this.g.resolveText();
        }
        if (z && this.e) {
            return VariableScope.nullOrMissingString;
        }
        return i() + ": ";
    }

    public String b(boolean z) {
        if (this.h != null) {
            return this.h.resolveText();
        }
        return VariableScope.nullOrMissingString;
    }

    public Texture k() {
        return this.y;
    }

    protected Resource() {
    }

    public static Resource a(String str) {
        String lowerCase = str.toLowerCase(Locale.ENGLISH);
        for (Resource resource : C) {
            if (resource.b.equalsIgnoreCase(lowerCase)) {
                return resource;
            }
        }
        return null;
    }

    public static Resource a(Resource resource) {
        Iterator it = A.iterator();
        while (it.hasNext()) {
            if (((Resource) it.next()).b.equals(resource.b)) {
                throw new RuntimeException("Built in resource already exists:" + resource.b);
            }
        }
        A.add(resource);
        C.add(resource);
        return resource;
    }

    public static Resource a(String str, boolean z, boolean z2) {
        for (Resource resource : A) {
            if (resource.b.equals(str)) {
                return resource;
            }
        }
        Resource resource2 = new Resource();
        resource2.b = str;
        resource2.u = z;
        resource2.t = z2;
        A.add(resource2);
        return resource2;
    }

    public static Resource b(String str) {
        for (Resource resource : A) {
            if (resource.b.equals(str)) {
                return resource;
            }
        }
        return null;
    }

    private String a(double d) {
        return a(Utility.toHexString(d, 1), this.q);
    }

    public String a(double d, boolean z, boolean z2) {
        String str;
        if (z2 && this.f) {
            str = VariableScope.nullOrMissingString;
        } else {
            str = j() + ": ";
        }
        if (this == D) {
            str = "$";
        }
        if (z) {
            if (d > 0.0d) {
                return "+" + str + a(d);
            }
            return "-" + str + a(-d);
        }
        if (d > 0.0d) {
            return str + a(d);
        }
        return str + a(d);
    }

    public String toString() {
        return "resource(" + this.b + ")";
    }

    public double a(BaseUnit baseUnit) {
        if (this.t) {
            return baseUnit.team.c(this);
        }
        return baseUnit.a(this);
    }

    public void a(BaseUnit baseUnit, double d) {
        if (this.t) {
            baseUnit.team.getCustomResources().a(this, d);
        } else {
            baseUnit.getUnitAICombatRange().a(this, d);
        }
    }

    public void b(BaseUnit baseUnit, double d) {
        if (this.t) {
            baseUnit.team.getCustomResources().b(this, d);
        } else {
            baseUnit.getUnitAICombatRange().b(this, d);
        }
    }
}
