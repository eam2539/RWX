package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/b.class */
class CustomUnitAnimation extends CustomUnitData {

    /* JADX INFO: renamed from: e */
    public CustomUnitSpawnList effect;

    /* JADX INFO: renamed from: f */
    public float offsetX;

    /* JADX INFO: renamed from: g */
    public float offsetY;

    /* JADX INFO: renamed from: h */
    public boolean finalized;

    public CustomUnitAnimation(float f, float f2) {
        super(f, f2);
    }

    public void a(CustomUnitConfig customUnitConfig, String str, String str2) throws ConfigParseException {
        try {
            if (str.equalsIgnoreCase("x")) {
                this.offsetX = Float.parseFloat(str2);
            } else if (str.equalsIgnoreCase("y")) {
                this.offsetY = Float.parseFloat(str2);
            } else {
                if (str.equalsIgnoreCase("name")) {
                    this.effect = customUnitConfig.addConfigExtension(str2, (CustomUnitSpawnList) null);
                    return;
                }
                throw new ConfigParseException("Unknown event key:" + str + " on animation");
            }
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Failed to parse float:" + str2);
        }
    }

    public void finalize() throws ConfigParseException {
        this.finalized = true;
        if (this.effect == null) {
            throw new ConfigParseException("Animation effect missing key 'name'");
        }
    }

    public void a(CustomUnit customUnit) {
        if (this.effect != null) {
            float f = customUnit.posX;
            float f2 = customUnit.posY;
            this.effect.a(f + this.offsetX, f2 + this.offsetY, customUnit.posZ, customUnit.rotationSpeed, customUnit);
        }
    }
}
