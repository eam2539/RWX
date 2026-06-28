package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.af */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/af.class */
public class PositionData {

    /* JADX INFO: renamed from: a */
    public float posX;

    /* JADX INFO: renamed from: b */
    public float posY;

    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeFloat(this.posX);
        gameOutputStream.writeFloat(this.posY);
    }

    public void a(GameInputStream gameInputStream) throws IOException {
        this.posX = gameInputStream.readFloat();
        this.posY = gameInputStream.readFloat();
    }
}
