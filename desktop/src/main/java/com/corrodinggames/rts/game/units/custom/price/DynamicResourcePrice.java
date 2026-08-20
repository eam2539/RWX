package com.corrodinggames.rts.game.units.custom.price;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.d.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/d/c.class */
public class DynamicResourcePrice extends PriceCondition {
    public final FastArrayList a = new FastArrayList();
    boolean b;
    public int c;
    public int d;
    public int e;
    public int f;

    public static DynamicResourcePrice a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, DynamicResourcePrice dynamicResourcePrice) throws ConfigParseException {
        String string = iniFile.getString(str, str2, (String) null);
        if (string == null) {
            return dynamicResourcePrice;
        }
        try {
            return a(customUnitConfig, string);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ConfigParseException("[" + str + "]" + str2 + ": " + e.getMessage());
        }
    }

    public static DynamicResourcePrice a(CustomUnitConfig customUnitConfig, String str) throws ConfigParseException {
        return a(customUnitConfig, str, false);
    }

    public static DynamicResourcePrice a(CustomUnitConfig customUnitConfig, String str, boolean z) throws ConfigParseException {
        String strTrim;
        String strSubstring;
        DynamicResourcePrice dynamicResourcePrice = new DynamicResourcePrice();
        for (String str2 : StringUtils.a(str, ",", "|", false)) {
            int iA = StringUtils.a(str2, "=", ":");
            if (iA == -1) {
                if (!z) {
                    throw new ConfigParseException("Unknown price format:" + str);
                }
                strTrim = "credits";
                strSubstring = str2;
            } else {
                strTrim = str2.substring(0, iA).trim();
                strSubstring = str2.substring(iA + 1);
            }
            if (strTrim.equals("hasFlag")) {
                dynamicResourcePrice.e = UnitPrice.a(dynamicResourcePrice.e, strSubstring);
            } else if (strTrim.equals("hasMissingFlag")) {
                dynamicResourcePrice.f = UnitPrice.a(dynamicResourcePrice.f, strSubstring);
            } else if (strTrim.equals("setFlag")) {
                dynamicResourcePrice.c = UnitPrice.a(dynamicResourcePrice.c, strSubstring);
            } else if (strTrim.equals("unsetFlag")) {
                dynamicResourcePrice.d = UnitPrice.a(dynamicResourcePrice.d, strSubstring);
            } else {
                Resource resourceFindOrCreateCustomResource = customUnitConfig.findOrCreateCustomResource(strTrim);
                if (resourceFindOrCreateCustomResource == null) {
                    throw new ConfigParseException("Could not find resource type:" + strTrim + " from [" + str + "]");
                }
                LogicBoolean numberBlock = LogicBooleanLoader.parseNumberBlock(customUnitConfig, strSubstring);
                if (numberBlock == null) {
                    throw new ConfigParseException("Value missing for:" + strTrim + " from [" + str + "]");
                }
                if (!(numberBlock instanceof LogicBoolean.StaticValueBoolean)) {
                    dynamicResourcePrice.b = true;
                }
                dynamicResourcePrice.a.add(new ResourceConditionEntry(resourceFindOrCreateCustomResource, numberBlock));
            }
        }
        return dynamicResourcePrice;
    }

    @Override // com.corrodinggames.rts.game.units.custom.price.PriceCondition
    public boolean b(BaseUnit baseUnit) {
        return b(baseUnit, 1.0d);
    }

    @Override // com.corrodinggames.rts.game.units.custom.price.PriceCondition
    public boolean b(BaseUnit baseUnit, double d) {
        double number;
        if (!(baseUnit instanceof OrderableUnit)) {
            return false;
        }
        OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
        int i = this.a.size;
        Object[] objArrA = this.a.a();
        for (int i2 = 0; i2 < i; i2++) {
            ResourceConditionEntry resourceConditionEntry = (ResourceConditionEntry) objArrA[i2];
            if (resourceConditionEntry.c != null) {
                number = ((double) resourceConditionEntry.c.readNumber(orderableUnit)) * d;
            } else {
                number = resourceConditionEntry.b * d;
            }
            if (number > 0.0d && resourceConditionEntry.a.a(orderableUnit) < number) {
                return false;
            }
        }
        if (!g(orderableUnit)) {
            return false;
        }
        return true;
    }

    public void d(BaseUnit baseUnit) {
        double number;
        if (!(baseUnit instanceof OrderableUnit)) {
            GameEngine.reportProblem("DynamicResourcePrice doesn't work on: " + baseUnit.getUnitShortName());
            return;
        }
        OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
        int i = this.a.size;
        Object[] objArrA = this.a.a();
        for (int i2 = 0; i2 < i; i2++) {
            ResourceConditionEntry resourceConditionEntry = (ResourceConditionEntry) objArrA[i2];
            if (resourceConditionEntry.c != null) {
                number = resourceConditionEntry.c.readNumber(orderableUnit);
            } else {
                number = resourceConditionEntry.b;
            }
            resourceConditionEntry.a.a(orderableUnit, number);
        }
        f(orderableUnit);
        UnitPrice.d(orderableUnit);
    }

    @Override // com.corrodinggames.rts.game.units.custom.price.PriceCondition
    public void a(BaseUnit baseUnit) {
        a(baseUnit, 1.0d);
    }

    @Override // com.corrodinggames.rts.game.units.custom.price.PriceCondition
    public void a(BaseUnit baseUnit, double d) {
        double number;
        if (!(baseUnit instanceof OrderableUnit)) {
            GameEngine.reportProblem("DynamicResourcePrice doesn't work on: " + baseUnit.getUnitShortName());
            return;
        }
        OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
        int i = this.a.size;
        Object[] objArrA = this.a.a();
        for (int i2 = 0; i2 < i; i2++) {
            ResourceConditionEntry resourceConditionEntry = (ResourceConditionEntry) objArrA[i2];
            if (resourceConditionEntry.c != null) {
                number = resourceConditionEntry.c.readNumber(orderableUnit);
            } else {
                number = resourceConditionEntry.b;
            }
            resourceConditionEntry.a.b(orderableUnit, (-number) * d);
        }
        f(orderableUnit);
        UnitPrice.d(orderableUnit);
    }

    public void e(BaseUnit baseUnit) {
        double number;
        if (!(baseUnit instanceof OrderableUnit)) {
            GameEngine.reportProblem("DynamicResourcePrice doesn't work on: " + baseUnit.getUnitShortName());
            return;
        }
        OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
        int i = this.a.size;
        Object[] objArrA = this.a.a();
        for (int i2 = 0; i2 < i; i2++) {
            ResourceConditionEntry resourceConditionEntry = (ResourceConditionEntry) objArrA[i2];
            if (resourceConditionEntry.c != null) {
                number = resourceConditionEntry.c.readNumber(orderableUnit);
            } else {
                number = resourceConditionEntry.b;
            }
            resourceConditionEntry.a.b(orderableUnit, number);
        }
        f(orderableUnit);
        UnitPrice.d(orderableUnit);
    }

    public void f(BaseUnit baseUnit) {
        if (this.d != 0) {
            baseUnit.unitFlags &= this.d ^ (-1);
        }
        if (this.c != 0) {
            baseUnit.unitFlags |= this.c;
        }
    }

    public boolean g(BaseUnit baseUnit) {
        if (this.e != 0 && !a(baseUnit.unitFlags, this.e)) {
            return false;
        }
        if (this.f != 0 && b(baseUnit.unitFlags, this.f)) {
            return false;
        }
        return true;
    }

    public static boolean a(int i, int i2) {
        return (i2 & i) == i2;
    }

    public static boolean b(int i, int i2) {
        return (i2 & i) != 0;
    }
}
