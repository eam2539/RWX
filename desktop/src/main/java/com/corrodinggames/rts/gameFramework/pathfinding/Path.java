package com.corrodinggames.rts.gameFramework.pathfinding;

import android.graphics.Paint;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.path.PathEngine;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

import java.io.IOException;
import java.util.LinkedList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/k.class */
public class Path {
    private PathEngine a;
    /* JADX INFO: renamed from: f */
    protected static int nextPathId;
    /* JADX INFO: renamed from: e */
    public int pathId;
    /* JADX INFO: renamed from: g */
    public int creationTick;
    /* JADX INFO: renamed from: h */
    public short startTileX;
    /* JADX INFO: renamed from: i */
    public short startTileY;
    protected Float j;
    protected boolean k;
    /* JADX INFO: renamed from: l */
    public short endTileX;
    /* JADX INFO: renamed from: m */
    public short endTileY;
    protected short n;
    /* JADX INFO: renamed from: o */
    public UnitMovementType movementType;
    public boolean p;
    public int q;
    /* JADX INFO: renamed from: r */
    public boolean isLowPriority;
    /* JADX INFO: renamed from: s */
    public float allowedDelayMs;
    /* JADX INFO: renamed from: t */
    public float remainingTimeMs;
    /* JADX INFO: renamed from: u */
    public boolean isTimedOut;
    /* JADX INFO: renamed from: v */
    public boolean isSolved;
    public boolean w;
    /* JADX INFO: renamed from: x */
    protected LinkedList pathPoints;
    public byte[] y;
    public byte[] z;
    public byte[] A;
    public short[] B;
    public byte[] C;

    public Path(PathEngine pathEngine, boolean z) {
        this.a = pathEngine;
        if (z) {
            int i = nextPathId;
            nextPathId = i + 1;
            this.pathId = i;
        }
        this.creationTick = GameEngine.getInstance().currentTick;
    }

    public void a(GameOutputStream gameOutputStream) throws IOException {
        int i;
        if (this.pathPoints == null) {
            gameOutputStream.writeBoolean(false);
            return;
        }
        gameOutputStream.writeBoolean(true);
        gameOutputStream.beginBlockInternal("p", true);
        gameOutputStream.writeInt(this.pathPoints.size());
        if (this.pathPoints.size() != 0) {
            PathPoint pathPoint = (PathPoint) this.pathPoints.get(0);
            gameOutputStream.writeShort(pathPoint.tileX);
            gameOutputStream.writeShort(pathPoint.tileY);
            for (int i2 = 1; i2 < this.pathPoints.size(); i2++) {
                PathPoint pathPoint2 = (PathPoint) this.pathPoints.get(i2);
                int i3 = pathPoint2.tileX - pathPoint.tileX;
                int i4 = pathPoint2.tileY - pathPoint.tileY;
                boolean z = Utility.abs(i3) > 1 || Utility.abs(i4) > 1;
                if (z) {
                    GameEngine.log("writeOutCompressedPath: out of range:" + i3 + "," + i4);
                    i = 128;
                } else {
                    i = i3 + 1 + ((i4 + 1) << 2);
                }
                gameOutputStream.writeByte(i);
                if (z) {
                    gameOutputStream.writeShort(pathPoint2.tileX);
                    gameOutputStream.writeShort(pathPoint2.tileY);
                }
                pathPoint = pathPoint2;
            }
        }
        gameOutputStream.endBlock("p");
    }

    public void a(GameInputStream gameInputStream) throws IOException {
        if (!gameInputStream.readBoolean()) {
            this.pathPoints = null;
            return;
        }
        gameInputStream.a("p", true);
        int i = gameInputStream.readInt();
        if (i > 160000 || i < 0) {
            GameEngine.log("readInCompressedPath: Path too big at:" + i);
            i = -1;
        }
        if (i != -1) {
            this.isTimedOut = true;
            if (this.pathPoints == null) {
                this.pathPoints = new LinkedList();
            }
            this.pathPoints.clear();
        }
        if (i > 0) {
            short shortValue = gameInputStream.readShortValue();
            short shortValue2 = gameInputStream.readShortValue();
            this.pathPoints.add(new PathPoint(shortValue, shortValue2));
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
                    this.pathPoints.add(new PathPoint(shortValue, shortValue2));
                } else {
                    GameEngine.log("readInCompressedPath: out of range unpack:" + ((int) shortValue) + "," + ((int) shortValue2));
                    shortValue = gameInputStream.readShortValue();
                    shortValue2 = gameInputStream.readShortValue();
                    this.pathPoints.add(new PathPoint(shortValue, shortValue2));
                }
            }
        }
        gameInputStream.d("p");
    }

    public void e() {
        PathCostMap pathCostMapA = this.a.a(this.movementType);
        if (pathCostMapA == null) {
            throw new RuntimeException("Could not get costs for:" + this.movementType.toString());
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
        this.movementType = unitMovementType;
        this.startTileX = s;
        this.startTileY = s2;
        this.j = f2;
        this.k = z;
        if (this.startTileX < 0) {
            this.startTileX = (short) 0;
        }
        if (this.startTileY < 0) {
            this.startTileY = (short) 0;
        }
        if (this.startTileX > this.a.widthTiles - 1) {
            this.startTileX = (short) (this.a.widthTiles - 1);
        }
        if (this.startTileY > this.a.heightTiles - 1) {
            this.startTileY = (short) (this.a.heightTiles - 1);
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
        if (s > this.a.widthTiles - 1) {
            s = (short) (this.a.widthTiles - 1);
        }
        if (s2 > this.a.heightTiles - 1) {
            s2 = (short) (this.a.heightTiles - 1);
        }
        this.endTileX = s;
        this.endTileY = s2;
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
            if (this.isTimedOut) {
                return this.pathPoints;
            }
            return null;
        }
        return this.pathPoints;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean c() {
        return this.pathPoints != null;
    }

    protected void a(LinkedList linkedList) {
        this.pathPoints = linkedList;
    }

    public void g() {
        GameEngine gameEngine = GameEngine.getInstance();
        TileMap tileMap = gameEngine.tileMap;
        Paint paint = new Paint();
        paint.a(2.0f);
        paint.a(100, 0, 100, 0);
        gameEngine.renderGraphicsEngine.a(((this.endTileX * tileMap.tileWorldSizeX) + tileMap.halfTileWorldSizeX) - GameEngine.getInstance().viewpointXInt, ((this.endTileY * tileMap.tileWorldSizeY) + tileMap.halfTileWorldSizeY) - GameEngine.getInstance().viewpointYInt, this.n * tileMap.tileWorldSizeX, paint);
        paint.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_PAIRING, 0, 0, 255);
        gameEngine.renderGraphicsEngine.a(((this.startTileX * tileMap.tileWorldSizeX) + tileMap.halfTileWorldSizeX) - GameEngine.getInstance().viewpointXInt, ((this.startTileY * tileMap.tileWorldSizeY) + tileMap.halfTileWorldSizeY) - GameEngine.getInstance().viewpointYInt, ((this.endTileX * tileMap.tileWorldSizeX) + tileMap.halfTileWorldSizeX) - GameEngine.getInstance().viewpointXInt, ((this.endTileY * tileMap.tileWorldSizeY) + tileMap.halfTileWorldSizeY) - GameEngine.getInstance().viewpointYInt, paint);
    }

    public void h() {
        if (this.pathPoints != null) {
            GameEngine gameEngine = GameEngine.getInstance();
            TileMap tileMap = gameEngine.tileMap;
            if (this.pathPoints.size() >= 1) {
                for (int i = 1; i < this.pathPoints.size(); i++) {
                    PathPoint pathPoint = (PathPoint) this.pathPoints.get(i);
                    PathPoint pathPoint2 = (PathPoint) this.pathPoints.get(i - 1);
                    Paint paint = new Paint();
                    paint.a(255, 0, 255, 0);
                    paint.a(2.0f);
                    gameEngine.renderGraphicsEngine.a(((pathPoint.tileX * tileMap.tileWorldSizeX) + tileMap.halfTileWorldSizeX) - GameEngine.getInstance().viewpointXInt, ((pathPoint.tileY * tileMap.tileWorldSizeY) + tileMap.halfTileWorldSizeY) - GameEngine.getInstance().viewpointYInt, ((pathPoint2.tileX * tileMap.tileWorldSizeX) + tileMap.halfTileWorldSizeX) - GameEngine.getInstance().viewpointXInt, ((pathPoint2.tileY * tileMap.tileWorldSizeY) + tileMap.halfTileWorldSizeY) - GameEngine.getInstance().viewpointYInt, paint);
                }
            }
        }
    }
}
