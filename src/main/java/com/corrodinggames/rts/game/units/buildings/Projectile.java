package com.corrodinggames.rts.game.units.buildings;

import android.graphics.PointF;
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
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/j.class */
public class Projectile extends Serializable {
    public int a;
    public float b;
    public AnimationSet e;
    public boolean f;
    public UnitType g;
    public PointF h;
    public BaseUnit i;
    public boolean k;
    public boolean l;
    public UnitPrice c = UnitPrice.a;
    public UnitPrice d = null;
    public ActionId j = AbstractUnitAction.NONE_ACTION_ID;
    public float m = -1.0f;
    public double n = 0.0d;

    @Override // com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(-1);
        gameOutputStream.writeInt(this.a);
        gameOutputStream.writeFloat(this.b);
        gameOutputStream.writeInt(-1);
        gameOutputStream.writeInt(this.c.a());
        gameOutputStream.writeBoolean(this.f);
        gameOutputStream.writeStringUTF(this.j.getId());
        gameOutputStream.writeStringUTF(this.j.getId());
        gameOutputStream.writeUnitIdIfAlive(this.i);
        gameOutputStream.writePointFNullable(this.h);
        gameOutputStream.writeBoolean(this.l);
        gameOutputStream.writeFloat(this.m);
        gameOutputStream.writeUnitTypeId(this.g);
        this.c.a(gameOutputStream);
        UnitPrice.a(gameOutputStream, this.d);
        AnimationTag.a(this.e, gameOutputStream);
    }

    /* JADX INFO: renamed from: a */
    public void readFromStream(GameInputStream gameInputStream) throws IOException {
        String.valueOf(gameInputStream.readInt());
        this.a = gameInputStream.readInt();
        this.b = gameInputStream.readFloat();
        int i = 0;
        if (gameInputStream.getProtocolVersion() >= 4) {
            this.j = ActionId.isSameInstance(String.valueOf(gameInputStream.readInt()));
        }
        if (gameInputStream.getProtocolVersion() >= 6) {
            i = gameInputStream.readInt();
        }
        if (gameInputStream.getProtocolVersion() >= 25) {
            this.f = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 33) {
            gameInputStream.readUTF();
            this.j = ActionId.isSameInstance(gameInputStream.readUTF());
        }
        if (gameInputStream.getProtocolVersion() >= 61) {
            this.i = gameInputStream.readBaseUnit();
            this.h = gameInputStream.y();
        }
        if (gameInputStream.getProtocolVersion() >= 64) {
            this.l = gameInputStream.readBoolean();
            this.m = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 71) {
            this.g = gameInputStream.q();
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
