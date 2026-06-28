package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.pathfinding.Path;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/d.class */
public class CommandTarget {

    /* JADX INFO: renamed from: a */
    public Path path;

    /* JADX INFO: renamed from: b */
    public long unitId;

    /* JADX INFO: renamed from: c */
    public float startX;

    /* JADX INFO: renamed from: d */
    public float startY;

    /* JADX INFO: renamed from: e */
    public float targetX;

    /* JADX INFO: renamed from: f */
    public float targetY;

    /* JADX INFO: renamed from: g */
    public int createdTick;

    /* JADX INFO: renamed from: h */
    public UnitMovementType movementType;

    /* JADX INFO: renamed from: a */
    public void serialize(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeLong(this.unitId);
        gameOutputStream.writeFloat(this.startX);
        gameOutputStream.writeFloat(this.startY);
        gameOutputStream.writeFloat(this.targetX);
        gameOutputStream.writeFloat(this.targetY);
        gameOutputStream.writeInt(this.createdTick);
        gameOutputStream.writeEnumOrdinal(this.movementType);
        gameOutputStream.writeBoolean(this.path != null);
        if (this.path != null) {
            this.path.a(gameOutputStream);
        }
    }

    /* JADX INFO: renamed from: a */
    public void deserialize(GameInputStream gameInputStream) throws IOException {
        this.unitId = gameInputStream.readLong();
        this.startX = gameInputStream.readFloat();
        this.startY = gameInputStream.readFloat();
        this.targetX = gameInputStream.readFloat();
        this.targetY = gameInputStream.readFloat();
        this.createdTick = gameInputStream.readInt();
        this.movementType = (UnitMovementType) gameInputStream.readEnumOrdinalOrNull(UnitMovementType.class);
        if (gameInputStream.readBoolean()) {
            this.path = new Path(null, false);
            this.path.a(gameInputStream);
        }
    }
}
