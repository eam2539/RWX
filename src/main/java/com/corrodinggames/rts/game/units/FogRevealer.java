package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.u */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/u.class */
public class FogRevealer extends DummyUnit {

    /* JADX INFO: renamed from: a */
    public int sightRange;

    /* JADX INFO: renamed from: b */
    public float lifeTimer;

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeByte(0);
        gameOutputStream.writeInt(this.sightRange);
        gameOutputStream.writeFloat(this.lifeTimer);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        gameInputStream.readByte();
        this.sightRange = gameInputStream.readInt();
        this.lifeTimer = gameInputStream.readFloat();
        super.a(gameInputStream);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.fogRevealer;
    }

    public static void f() {
        GameEngine.getInstance();
    }

    public FogRevealer(boolean z) {
        super(z);
        this.sightRange = 14;
        this.lifeTimer = 60.0f;
    }

    @Override // com.corrodinggames.rts.game.units.DummyUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        this.lifeTimer -= f;
        if (this.lifeTimer < 0.0f) {
            getUnitAICondition();
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public int s() {
        return this.sightRange;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean t() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.DummyUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean u() {
        return true;
    }
}
