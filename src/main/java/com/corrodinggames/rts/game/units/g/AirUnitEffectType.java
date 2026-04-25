package com.corrodinggames.rts.game.units.g;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.g.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/g/b.class */
enum AirUnitEffectType {
    movementSpeed { // from class: com.corrodinggames.rts.game.units.g.b.1
        @Override // com.corrodinggames.rts.game.units.g.AirUnitEffectType
        AirUnitEffect a() {
            return new MovementSpeedEffect();
        }
    },
    specialActionBlock { // from class: com.corrodinggames.rts.game.units.g.b.2
        @Override // com.corrodinggames.rts.game.units.g.AirUnitEffectType
        AirUnitEffect a() {
            return new SpecialActionBlockEffect();
        }
    };

    abstract AirUnitEffect a();
}
