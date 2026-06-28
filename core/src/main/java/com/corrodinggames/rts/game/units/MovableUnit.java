package com.corrodinggames.rts.game.units;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/w.class */
public abstract class MovableUnit extends OrderableUnit {
    public MovableUnit(boolean z) {
        super(z);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean I() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean i() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean Q() {
        return false;
    }
}
