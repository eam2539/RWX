package com.corrodinggames.rts.game.units.g;

import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.g.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/g/a.class */
public abstract class AirUnitEffect {

    /* JADX INFO: renamed from: a */
    int effectId;

    public abstract AirUnitEffectType b();

    public AirUnitEffect() {
    }

    public AirUnitEffect(int i) {
        this.effectId = i;
    }

    /* JADX INFO: renamed from: a */
    public int getEffectId() {
        return this.effectId;
    }

    public void a(OrderableUnit orderableUnit, float f) {
    }

    public void a(OrderableUnit orderableUnit, GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(this.effectId);
    }

    public void a(OrderableUnit orderableUnit, GameInputStream gameInputStream) throws IOException {
        this.effectId = gameInputStream.readInt();
    }
}
