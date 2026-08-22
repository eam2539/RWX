package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bo */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bo.class */
public class StatisticsData {
    public int a;
    public int b;

    /* JADX INFO: renamed from: m */
    private static final byte saveVersion = (byte) SaveGameVersion.v220911_added_history.ordinal();
    /* JADX INFO: renamed from: c */
    public int killedUnits;
    /* JADX INFO: renamed from: d */
    public int killedBuildings;
    /* JADX INFO: renamed from: e */
    public int killedExperimental;
    /* JADX INFO: renamed from: f */
    public int lostUnits;
    /* JADX INFO: renamed from: g */
    public int lostBuildings;

    public int i;
    public int j;
    public long k;
    /* JADX INFO: renamed from: h */
    public int lostExperimental;
    /* JADX INFO: renamed from: l */
    public TeamHistory teamHistory = new TeamHistory();

    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeByte(saveVersion);
        gameOutputStream.writeMagicShort();
        gameOutputStream.writeInt(this.a);
        gameOutputStream.writeInt(this.b);
        gameOutputStream.writeInt(this.killedUnits);
        gameOutputStream.writeInt(this.killedBuildings);
        gameOutputStream.writeInt(this.killedExperimental);
        gameOutputStream.writeInt(this.lostUnits);
        gameOutputStream.writeInt(this.lostBuildings);
        gameOutputStream.writeInt(this.lostExperimental);
        gameOutputStream.writeInt(this.i);
        gameOutputStream.writeInt(this.j);
        gameOutputStream.writeLong(this.k);
        this.teamHistory.a(gameOutputStream);
    }

    public void a(GameInputStream gameInputStream) throws IOException {
        byte b = gameInputStream.readByte();
        gameInputStream.a("stats start");
        this.a = gameInputStream.readInt();
        this.b = gameInputStream.readInt();
        this.killedUnits = gameInputStream.readInt();
        this.killedBuildings = gameInputStream.readInt();
        this.killedExperimental = gameInputStream.readInt();
        this.lostUnits = gameInputStream.readInt();
        this.lostBuildings = gameInputStream.readInt();
        this.lostExperimental = gameInputStream.readInt();
        this.i = gameInputStream.readInt();
        this.j = gameInputStream.readInt();
        this.k = gameInputStream.readLong();
        if (b >= SaveGameVersion.v220911_added_history.ordinal()) {
            this.teamHistory.a(gameInputStream);
        }
    }
}
