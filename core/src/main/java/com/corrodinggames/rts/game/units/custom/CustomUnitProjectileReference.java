package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.x */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/x.class */
public class CustomUnitProjectileReference extends CustomUnitTypeReference {

    /* JADX INFO: renamed from: g */
    String projectileName;

    /* JADX INFO: renamed from: h */
    CustomProjectileTemplate projectile;

    @Override
    // com.corrodinggames.rts.game.units.custom.CustomUnitTypeReference, com.corrodinggames.rts.game.units.custom.UnitTypeReference
    public void a() {
    }

    @Override // com.corrodinggames.rts.game.units.custom.UnitTypeReference
    public void b() throws ConfigParseException {
        super.a();
        this.projectile = c().findProjectileTemplateByName(this.projectileName);
        if (this.projectile == null) {
            throw new ConfigParseException("Could not find projectile:" + this.projectileName + " on unit target:" + d() + " used on:" + this.configKey + " in section:" + this.sectionName);
        }
    }

    public CustomProjectileTemplate f() {
        return this.projectile;
    }
}
