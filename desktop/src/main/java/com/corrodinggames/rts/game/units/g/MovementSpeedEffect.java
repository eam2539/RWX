package com.corrodinggames.rts.game.units.g;

import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.g.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/g/d.class */
public class MovementSpeedEffect extends AirUnitEffect {

    /* JADX INFO: renamed from: b */
    float speedMultiplier;

    @Override // com.corrodinggames.rts.game.units.g.AirUnitEffect
    public AirUnitEffectType b() {
        return AirUnitEffectType.movementSpeed;
    }

    @Override // com.corrodinggames.rts.game.units.g.AirUnitEffect
    public void a(OrderableUnit orderableUnit, GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeFloat(this.speedMultiplier);
        super.a(orderableUnit, gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.g.AirUnitEffect
    public void a(OrderableUnit orderableUnit, GameInputStream gameInputStream) throws IOException {
        this.speedMultiplier = gameInputStream.readFloat();
        super.a(orderableUnit, gameInputStream);
    }
}
