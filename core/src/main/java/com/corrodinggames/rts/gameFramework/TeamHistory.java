package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bn */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bn.class */
public class TeamHistory {
    private int a = -1;
    private IntLookupTable[] b = new IntLookupTable[StatisticType.values().length];

    public TeamHistory() {
        a();
    }

    public void a() {
        for (int i = 0; i < this.b.length; i++) {
            this.b[i] = new IntLookupTable();
        }
    }

    public void a(GameInputStream gameInputStream) throws IOException {
        int i;
        int i2;
        if (gameInputStream.readBoolean()) {
            gameInputStream.a("History");
            gameInputStream.readByte();
            this.a = gameInputStream.readInt();
            boolean z = gameInputStream.readBoolean();
            byte b = gameInputStream.readByte();
            a();
            for (int i3 = 0; i3 < b; i3++) {
                int i4 = 0;
                int i5 = 0;
                short shortValue = gameInputStream.readShortValue();
                for (int i6 = 0; i6 < shortValue; i6++) {
                    if (z) {
                        i = gameInputStream.readInt() + i4;
                        i2 = gameInputStream.readInt() + i5;
                        i4 = i;
                        i5 = i2;
                    } else {
                        i = gameInputStream.readInt();
                        i2 = gameInputStream.readInt();
                    }
                    if (i3 < this.b.length) {
                        this.b[i3].add(new Point2i(i, i2));
                    }
                }
            }
        }
    }

    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeBoolean(true);
        if (1 != 0) {
            gameOutputStream.writeMagicShort();
            gameOutputStream.writeByte(0);
            gameOutputStream.writeInt(this.a);
            gameOutputStream.writeBoolean(true);
            gameOutputStream.writeByte(this.b.length);
            int i = 0;
            for (IntLookupTable intLookupTable : this.b) {
                short size = (short) intLookupTable.size();
                gameOutputStream.writeShort(size);
                int i2 = 0;
                int i3 = 0;
                for (int i4 = 0; i4 < size; i4++) {
                    i++;
                    Point2i point2i = (Point2i) intLookupTable.get(i4);
                    if (1 != 0) {
                        int i5 = point2i.x;
                        int i6 = point2i.y;
                        gameOutputStream.writeInt(i5 - i2);
                        gameOutputStream.writeInt(i6 - i3);
                        i2 = i5;
                        i3 = i6;
                    } else {
                        gameOutputStream.writeInt(point2i.x);
                        gameOutputStream.writeInt(point2i.y);
                    }
                }
            }
            GameEngine.log("TeamHistory(" + this.a + "): totalValues written:" + i);
        }
    }

    public void a(PlayerTeam playerTeam, int i, boolean z) {
        for (StatisticType statisticType : StatisticType.values()) {
            int iCalculate = statisticType.e.calculate(playerTeam);
            IntLookupTable intLookupTable = this.b[statisticType.ordinal()];
            if (intLookupTable.isEmpty() || z || ((Point2i) intLookupTable.get(intLookupTable.size() - 1)).y != iCalculate) {
                intLookupTable.add(new Point2i(i, iCalculate));
            }
        }
    }

    public void a(int i) {
        this.a = i;
    }

    public int b() {
        return this.a;
    }

    public IntLookupTable a(StatisticType statisticType) {
        return this.b[statisticType.ordinal()];
    }

    public boolean c() {
        if (this.a < 0) {
            return false;
        }
        for (IntLookupTable intLookupTable : this.b) {
            if (intLookupTable.size() > 1) {
                return true;
            }
        }
        return false;
    }

    public int a(StatisticType statisticType, int i) {
        return this.b[statisticType.ordinal()].a(i);
    }

    public void a(TeamHistory teamHistory) {
        for (int i = 0; i < this.b.length; i++) {
            this.b[i] = a(this.b[i], teamHistory.b[i]);
        }
    }

    private IntLookupTable a(IntLookupTable intLookupTable, IntLookupTable intLookupTable2) {
        if (intLookupTable.isEmpty()) {
            intLookupTable.addAll(intLookupTable2);
            return intLookupTable;
        }
        IntLookupTable intLookupTable3 = new IntLookupTable();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        Iterator it = intLookupTable.iterator();
        while (it.hasNext()) {
            Point2i point2i = (Point2i) it.next();
            int i4 = point2i.x;
            int i5 = point2i.y;
            if (i < intLookupTable2.size()) {
                Point2i point2i2 = (Point2i) intLookupTable2.get(i);
                while (point2i2.x < i4) {
                    i3 = point2i2.y;
                    intLookupTable3.add(new Point2i(point2i2.x, i2 + i3));
                    i++;
                    if (i < intLookupTable2.size()) {
                        point2i2 = (Point2i) intLookupTable2.get(i);
                    }
                }
                if (point2i2.x == i4) {
                    i3 = point2i2.y;
                    i2 = i5;
                    intLookupTable3.add(new Point2i(i4, i2 + i3));
                    i++;
                } else if (point2i2.x > i4) {
                    i2 = i5;
                    intLookupTable3.add(new Point2i(i4, i2 + i3));
                }
            } else {
                i2 = i5;
                intLookupTable3.add(new Point2i(i4, i2 + i3));
            }
        }
        while (i < intLookupTable2.size()) {
            Point2i point2i3 = (Point2i) intLookupTable2.get(i);
            intLookupTable3.add(new Point2i(point2i3.x, i2 + point2i3.y));
            i++;
        }
        return intLookupTable3;
    }
}
