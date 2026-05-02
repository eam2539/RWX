package com.corrodinggames.rts.game.units;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.ak */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/ak.class */
public interface TransportUnitInterface {
    /* JADX INFO: renamed from: bB */
    int getTransportedUnitCount();

    /* JADX INFO: renamed from: bA */
    boolean isTransportUnloadingActive();

    boolean f();

    boolean j();

    void e(BaseUnit baseUnit);
}
