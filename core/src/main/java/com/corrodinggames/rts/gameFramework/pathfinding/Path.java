package com.corrodinggames.rts.gameFramework.pathfinding;

import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.path.PathEngine;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import io.github.rwx.render.canvas.KoolPaint;

import java.io.IOException;
import java.util.LinkedList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/k.class */
public class Path {
    private PathEngine a;
    public int e;
    protected static int f;
    public int g;
    public short h;
    public short i;
    protected Float j;
    protected boolean k;
    public short l;
    public short m;
    protected short n;
    public UnitMovementType o;
    public boolean p;
    public int q;
    /* JADX INFO: renamed from: r */
    public boolean isLowPriority;
    public float s;
    public float t;
    public boolean u;
    public boolean v;
    public boolean w;
    protected LinkedList x;
    public byte[] y;
    public byte[] z;
    public byte[] A;
    public short[] B;
    public byte[] C;

    public Path(PathEngine pathEngine, boolean z) {
        this.a = pathEngine;
        if (z) {
            int i = f;
            f = i + 1;
            this.e = i;
        }
        this.g = GameEngine.getInstance().currentTick;
    }

    public void a(GameOutputStream gameOutputStream) throws IOException {
        int i;
        if (this.x == null) {
            gameOutputStream.writeBoolean(false);
            return;
        }
        gameOutputStream.writeBoolean(true);
        gameOutputStream.beginBlockInternal("p", true);
        gameOutputStream.writeInt(this.x.size());
        if (this.x.size() != 0) {
            PathPoint pathPoint = (PathPoint) this.x.get(0);
            gameOutputStream.writeShort(pathPoint.a);
            gameOutputStream.writeShort(pathPoint.b);
            for (int i2 = 1; i2 < this.x.size(); i2++) {
                PathPoint pathPoint2 = (PathPoint) this.x.get(i2);
                int i3 = pathPoint2.a - pathPoint.a;
                int i4 = pathPoint2.b - pathPoint.b;
                boolean z = Utility.abs(i3) > 1 || Utility.abs(i4) > 1;
                if (z) {
                    GameEngine.log("writeOutCompressedPath: out of range:" + i3 + "," + i4);
                    i = 128;
                } else {
                    i = i3 + 1 + ((i4 + 1) << 2);
                }
                gameOutputStream.writeByte(i);
                if (z) {
                    gameOutputStream.writeShort(pathPoint2.a);
                    gameOutputStream.writeShort(pathPoint2.b);
                }
                pathPoint = pathPoint2;
            }
        }
        gameOutputStream.endBlock("p");
    }

    public void a(GameInputStream gameInputStream) throws IOException {
        if (!gameInputStream.readBoolean()) {
            this.x = null;
            return;
        }
        gameInputStream.a("p", true);
        int i = gameInputStream.readInt();
        if (i > 160000 || i < 0) {
            GameEngine.log("readInCompressedPath: Path too big at:" + i);
            i = -1;
        }
        if (i != -1) {
            this.u = true;
            if (this.x == null) {
                this.x = new LinkedList();
            }
            this.x.clear();
        }
        if (i > 0) {
            short shortValue = gameInputStream.readShortValue();
            short shortValue2 = gameInputStream.readShortValue();
            this.x.add(new PathPoint(shortValue, shortValue2));
            for (int i2 = 1; i2 < i; i2++) {
                byte b = gameInputStream.readByte();
                if (b < 128) {
                    int i3 = (b & 3) - 1;
                    int i4 = ((b & 12) >> 2) - 1;
                    if (Utility.abs(i3) > 1 || Utility.abs(i4) > 1) {
                        GameEngine.log("readInCompressedPath: out of range but shouldn't be:" + i3 + "," + i4 + " for: " + ((int) b));
                    }
                    shortValue = (short) (shortValue + i3);
                    shortValue2 = (short) (shortValue2 + i4);
                    this.x.add(new PathPoint(shortValue, shortValue2));
                } else {
                    GameEngine.log("readInCompressedPath: out of range unpack:" + ((int) shortValue) + "," + ((int) shortValue2));
                    shortValue = gameInputStream.readShortValue();
                    shortValue2 = gameInputStream.readShortValue();
                    this.x.add(new PathPoint(shortValue, shortValue2));
                }
            }
        }
        gameInputStream.d("p");
    }

    public void e() {
        PathCostMap pathCostMapA = this.a.a(this.o);
        if (pathCostMapA == null) {
            throw new RuntimeException("Could not get costs for:" + this.o.toString());
        }
        this.y = pathCostMapA.d;
        this.z = pathCostMapA.e;
        this.A = pathCostMapA.f;
        this.B = pathCostMapA.g;
        this.C = pathCostMapA.j;
    }

    public void f() {
        this.y = null;
        this.z = null;
        this.A = null;
        this.B = null;
        this.C = null;
    }

    public void a(UnitMovementType unitMovementType, short s, short s2, Float f2, boolean z) {
        if (unitMovementType == null) {
            throw new RuntimeException("MovementType is null");
        }
        this.o = unitMovementType;
        this.h = s;
        this.i = s2;
        this.j = f2;
        this.k = z;
        if (this.h < 0) {
            this.h = (short) 0;
        }
        if (this.i < 0) {
            this.i = (short) 0;
        }
        if (this.h > this.a.s - 1) {
            this.h = (short) (this.a.s - 1);
        }
        if (this.i > this.a.t - 1) {
            this.i = (short) (this.a.t - 1);
        }
        if (this.a.a(unitMovementType) == null) {
            throw new RuntimeException("Could not get costs for:" + unitMovementType.toString());
        }
    }

    public void a(short s, short s2, short s3) {
        if (s < 0) {
            s = 0;
        }
        if (s2 < 0) {
            s2 = 0;
        }
        if (s > this.a.s - 1) {
            s = (short) (this.a.s - 1);
        }
        if (s2 > this.a.t - 1) {
            s2 = (short) (this.a.t - 1);
        }
        this.l = s;
        this.m = s2;
        this.n = s3;
    }

    public boolean b() {
        return false;
    }

    public boolean a(Path path) {
        return this == path;
    }

    public PathPositionProvider a(BaseUnit baseUnit) {
        return null;
    }

    public LinkedList a() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.networkEngine.networkGameActive || gameEngine.replayEngine.i()) {
            if (this.u) {
                return this.x;
            }
            return null;
        }
        return this.x;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean c() {
        return this.x != null;
    }

    protected void a(LinkedList linkedList) {
        this.x = linkedList;
    }

    public void g() {
        GameEngine gameEngine = GameEngine.getInstance();
        TileMap tileMap = gameEngine.tileMap;
        KoolPaint paint = new KoolPaint();
        paint.a(2.0f);
        paint.a(100, 0, 100, 0);
        gameEngine.renderGraphicsEngine.a(((this.l * tileMap.tileWorldSizeX) + tileMap.halfTileWorldSizeX) - GameEngine.getInstance().viewpointXInt, ((this.m * tileMap.tileWorldSizeY) + tileMap.halfTileWorldSizeY) - GameEngine.getInstance().viewpointYInt, this.n * tileMap.tileWorldSizeX, paint);
        paint.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_PAIRING, 0, 0, 255);
        gameEngine.renderGraphicsEngine.a(((this.h * tileMap.tileWorldSizeX) + tileMap.halfTileWorldSizeX) - GameEngine.getInstance().viewpointXInt, ((this.i * tileMap.tileWorldSizeY) + tileMap.halfTileWorldSizeY) - GameEngine.getInstance().viewpointYInt, ((this.l * tileMap.tileWorldSizeX) + tileMap.halfTileWorldSizeX) - GameEngine.getInstance().viewpointXInt, ((this.m * tileMap.tileWorldSizeY) + tileMap.halfTileWorldSizeY) - GameEngine.getInstance().viewpointYInt, paint);
    }

    public void h() {
        if (this.x != null) {
            GameEngine gameEngine = GameEngine.getInstance();
            TileMap tileMap = gameEngine.tileMap;
            if (this.x.size() >= 1) {
                for (int i = 1; i < this.x.size(); i++) {
                    PathPoint pathPoint = (PathPoint) this.x.get(i);
                    PathPoint pathPoint2 = (PathPoint) this.x.get(i - 1);
                    KoolPaint paint = new KoolPaint();
                    paint.a(255, 0, 255, 0);
                    paint.a(2.0f);
                    gameEngine.renderGraphicsEngine.a(((pathPoint.a * tileMap.tileWorldSizeX) + tileMap.halfTileWorldSizeX) - GameEngine.getInstance().viewpointXInt, ((pathPoint.b * tileMap.tileWorldSizeY) + tileMap.halfTileWorldSizeY) - GameEngine.getInstance().viewpointYInt, ((pathPoint2.a * tileMap.tileWorldSizeX) + tileMap.halfTileWorldSizeX) - GameEngine.getInstance().viewpointXInt, ((pathPoint2.b * tileMap.tileWorldSizeY) + tileMap.halfTileWorldSizeY) - GameEngine.getInstance().viewpointYInt, paint);
                }
            }
        }
    }
}
