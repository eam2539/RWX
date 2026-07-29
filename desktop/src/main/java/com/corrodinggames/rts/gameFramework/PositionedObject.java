package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.az */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/az.class */
public abstract class PositionedObject extends GameObject {
    public int ex;

    protected PositionedObject(boolean z) {
        super(z);
        this.ex = 0;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeDebugMessage("xy is:");
        gameOutputStream.writeFloat(this.posX);
        gameOutputStream.writeFloat(this.posY);
        gameOutputStream.writeFloat(this.posZ);
        gameOutputStream.writeInt(this.ex);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.posX = gameInputStream.readFloat();
        this.posY = gameInputStream.readFloat();
        this.posZ = gameInputStream.readFloat();
        this.ex = gameInputStream.readInt();
        super.a(gameInputStream);
    }
}
