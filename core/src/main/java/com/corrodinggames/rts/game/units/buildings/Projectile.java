package com.corrodinggames.rts.game.units.buildings;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.Serializable;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import io.github.rwx.geometry.PointF;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/j.class */
public class Projectile extends Serializable {
    /* JADX INFO: renamed from: a */
    public int launchDelay;
    public float b;
    public AnimationSet e;
    public boolean f;
    /* JADX INFO: renamed from: g */
    public UnitType unitType;
    /* JADX INFO: renamed from: h */
    public PointF targetPoint;
    /* JADX INFO: renamed from: i */
    public BaseUnit targetUnit;
    public boolean k;
    /* JADX INFO: renamed from: l */
    public boolean isHighPriority;
    public UnitPrice c = UnitPrice.a;
    public UnitPrice d = null;
    public ActionId j = AbstractUnitAction.NONE_ACTION_ID;
    public float m = -1.0f;
    public double n = 0.0d;

    @Override // com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(-1);
        gameOutputStream.writeInt(this.launchDelay);
        gameOutputStream.writeFloat(this.b);
        gameOutputStream.writeInt(-1);
        gameOutputStream.writeInt(this.c.a());
        gameOutputStream.writeBoolean(this.f);
        gameOutputStream.writeStringUTF(this.j.getId());
        gameOutputStream.writeStringUTF(this.j.getId());
        gameOutputStream.writeUnitIdIfAlive(this.targetUnit);
        gameOutputStream.writePointFNullable(this.targetPoint);
        gameOutputStream.writeBoolean(this.isHighPriority);
        gameOutputStream.writeFloat(this.m);
        gameOutputStream.writeUnitTypeId(this.unitType);
        this.c.a(gameOutputStream);
        UnitPrice.a(gameOutputStream, this.d);
        AnimationTag.a(this.e, gameOutputStream);
    }

    /* JADX INFO: renamed from: a */
    public void readFromStream(GameInputStream gameInputStream) throws IOException {
        String.valueOf(gameInputStream.readInt());
        this.launchDelay = gameInputStream.readInt();
        this.b = gameInputStream.readFloat();
        int i = 0;
        if (gameInputStream.getProtocolVersion() >= 4) {
            this.j = ActionId.intern(String.valueOf(gameInputStream.readInt()));
        }
        if (gameInputStream.getProtocolVersion() >= 6) {
            i = gameInputStream.readInt();
        }
        if (gameInputStream.getProtocolVersion() >= 25) {
            this.f = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 33) {
            gameInputStream.readUTF();
            this.j = ActionId.intern(gameInputStream.readUTF());
        }
        if (gameInputStream.getProtocolVersion() >= 61) {
            this.targetUnit = gameInputStream.readBaseUnit();
            this.targetPoint = gameInputStream.y();
        }
        if (gameInputStream.getProtocolVersion() >= 64) {
            this.isHighPriority = gameInputStream.readBoolean();
            this.m = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 71) {
            this.unitType = gameInputStream.q();
        }
        if (gameInputStream.getProtocolVersion() >= 73) {
            this.c = UnitPrice.b(gameInputStream);
        } else {
            this.c = UnitPrice.a(i);
        }
        if (gameInputStream.getProtocolVersion() >= 91) {
            this.d = UnitPrice.a(gameInputStream);
        }
        if (gameInputStream.getProtocolVersion() >= 95) {
            this.e = AnimationTag.a(gameInputStream);
        }
    }
}
