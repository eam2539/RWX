package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bo */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bo.class */
public class StatisticsData {
    public int a;
    public int b;
    public int c;
    public int d;
    public int e;
    public int f;
    public int g;
    public int h;
    public int i;
    public int j;
    public long k;
    public TeamHistory l = new TeamHistory();
    private static final byte m = (byte) SaveGameVersion.v220911_added_history.ordinal();

    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeByte(m);
        gameOutputStream.writeMagicShort();
        gameOutputStream.writeInt(this.a);
        gameOutputStream.writeInt(this.b);
        gameOutputStream.writeInt(this.c);
        gameOutputStream.writeInt(this.d);
        gameOutputStream.writeInt(this.e);
        gameOutputStream.writeInt(this.f);
        gameOutputStream.writeInt(this.g);
        gameOutputStream.writeInt(this.h);
        gameOutputStream.writeInt(this.i);
        gameOutputStream.writeInt(this.j);
        gameOutputStream.writeLong(this.k);
        this.l.a(gameOutputStream);
    }

    public void a(GameInputStream gameInputStream) throws IOException {
        byte b = gameInputStream.readByte();
        gameInputStream.a("stats start");
        this.a = gameInputStream.readInt();
        this.b = gameInputStream.readInt();
        this.c = gameInputStream.readInt();
        this.d = gameInputStream.readInt();
        this.e = gameInputStream.readInt();
        this.f = gameInputStream.readInt();
        this.g = gameInputStream.readInt();
        this.h = gameInputStream.readInt();
        this.i = gameInputStream.readInt();
        this.j = gameInputStream.readInt();
        this.k = gameInputStream.readLong();
        if (b >= SaveGameVersion.v220911_added_history.ordinal()) {
            this.l.a(gameInputStream);
        }
    }
}
