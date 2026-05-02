package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/w.class */
public class CustomUnitTypeReference extends UnitTypeReference {
    @Override // com.corrodinggames.rts.game.units.custom.UnitTypeReference
    public void a() throws ConfigParseException {
        if (!this.e) {
            this.unitType = CustomUnitConfig.findConfigByName(this.unitTypeName);
            if (this.unitType == null) {
                throw new ConfigParseException("Could not find customUnit target:" + d() + " used on:" + this.configKey + " in section:" + this.sectionName);
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.UnitTypeReference
    /* JADX INFO: renamed from: e, reason: merged with bridge method [inline-methods] */
    public CustomUnitConfig c() {
        return (CustomUnitConfig) this.unitType;
    }
}
