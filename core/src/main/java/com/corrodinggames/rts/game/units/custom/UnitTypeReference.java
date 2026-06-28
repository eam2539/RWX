package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.v */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/v.class */
public class UnitTypeReference {

    /* JADX INFO: renamed from: a */
    String configKey;

    /* JADX INFO: renamed from: b */
    String sectionName;

    /* JADX INFO: renamed from: c */
    String unitTypeName;

    /* JADX INFO: renamed from: d */
    UnitType unitType;
    boolean e;
    public boolean f;

    public void a() throws ConfigParseException {
        if (!this.e) {
            this.unitType = CustomUnitConfig.getUnitTypeByName(this.unitTypeName);
            if (this.unitType == null) {
                GameEngine.log("AllUnitTypes: " + CustomUnitConfig.getAllUnitAndTriggerNames());
                if (this.f) {
                    throw new ConfigParseException("Could not find unit type:" + this.unitTypeName + " used on:" + this.configKey + " in section:" + this.sectionName + " (Note: Prefix with 'unitref' if not using a unit type here)");
                }
                throw new ConfigParseException("Could not find unit type:" + this.unitTypeName + " used on:" + this.configKey + " in section:" + this.sectionName);
            }
        }
    }

    public void b() throws ConfigParseException {
    }

    public UnitType c() {
        return this.unitType;
    }

    public String d() {
        if (this.e) {
            if (this.unitType != null) {
                return this.unitType.getUnitTypeDescriptionShort();
            }
            return "(Error: known type is null)";
        }
        return this.unitTypeName;
    }
}
