package com.corrodinggames.rts.gameFramework.pathfinding;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import com.corrodinggames.rts.game.map.MapLayer;
import com.corrodinggames.rts.game.map.MapTile;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.Tree;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.path.PathEngine;
import com.corrodinggames.rts.gameFramework.utility.CircularDeque;
import com.corrodinggames.rts.gameFramework.utility.ShortPair;
import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/i.class */
public final class PathCostMap {
    private final PathEngine q;
    public UnitMovementType a;
    public final int b;
    public final int c;
    public byte[] d;
    public byte[] e;
    public byte[] f;
    public short[] g;
    public HashMap h;
    public int i;
    public byte[] j;
    public boolean m;
    public boolean o;
    public int p;
    public int k = -99;
    public int l = 0;
    Point n = new Point();

    /* JADX INFO: Access modifiers changed from: package-private */
    public PathCostMap(PathEngine pathEngine, UnitMovementType unitMovementType, int i, int i2) {
        this.b = i;
        this.c = i2;
        this.q = pathEngine;
        this.a = unitMovementType;
        this.d = new byte[i * i2];
        this.q.u.add(this);
        this.q.v = (PathCostMap[]) this.q.u.toArray(new PathCostMap[0]);
        a();
    }

    void a() {
        d();
        c(null);
        e();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void a(OrderableUnit var_1_54) {
        if (var_1_54 != null) {
            ++this.l;
            if (this.l > 50) {
                if (!this.m) {
                    GameEngine.log("buildAndReplaceClearanceCost being skipped");
                }
                this.m = true;
                return;
            }
        }
        if (var_1_54 != null) {
            this.b(var_1_54);
            return;
        }
        long a = 0L;
        a = PerformanceProfiler.a();
        final TileMap q = this.q.q;
        this.j = new byte[this.b * this.c];
        short n = 0;
        short n2 = 0;
        short n3 = this.q.s;
        short n4 = this.q.t;
        for (short short4 = n; short4 < n3; ++short4) {
            for (short short3 = n2; short3 < n4; ++short3) {
                boolean b = false;
                if (this.d[short4 * this.c + short3] == -1) {
                    b = true;
                }
                if (this.e[short4 * this.c + short3] == -1) {
                    b = true;
                }
                if (b) {
                    this.j[short4 * this.c + short3] = 0;
                }
                else {
                    this.j[short4 * this.c + short3] = 4;
                }
            }
        }
        for (short short4 = n; short4 < n3; ++short4) {
            for (short short3 = n2; short3 < n4; ++short3) {
                if (this.j[short4 * this.c + short3] == 0) {
                    this.a(q, short4, short3, this.j);
                }
            }
        }
        for (short short4 = n; short4 < n3; ++short4) {
            this.a(q, short4, (short)(-1), this.j);
            this.a(q, short4, (short)(this.q.t + 1), this.j);
        }
        for (short short4 = n2; short4 < n4; ++short4) {
            this.a(q, (short)(-1), short4, this.j);
            this.a(q, (short)(this.q.s + 1), short4, this.j);
        }
        if (var_1_54 == null) {
            final double double1 = PerformanceProfiler.a(a);
            if (double1 > 30.0) {
                GameEngine.log("buildAndReplaceClearanceCostNew took:" + PerformanceProfiler.a(double1) + " for:" + this.a);
            }
        }
    }

    final void a(TileMap tileMap, short s, short s2, byte[] bArr) {
        int iCountChars;
        int i = s - 3;
        int i2 = s2 - 3;
        int i3 = s + 3;
        int i4 = s2 + 3;
        if (i < 0) {
            i = 0;
        }
        if (i2 < 0) {
            i2 = 0;
        }
        if (i3 > tileMap.tileCountX - 1) {
            i3 = tileMap.tileCountX - 1;
        }
        if (i4 > tileMap.tileCountY - 1) {
            i4 = tileMap.tileCountY - 1;
        }
        for (int i5 = i; i5 <= i3; i5++) {
            for (int i6 = i2; i6 <= i4; i6++) {
                byte b = bArr[(i5 * this.c) + i6];
                if (b != 0 && (iCountChars = Utility.chebyshevDistance((int) s, (int) s2, i5, i6)) < b) {
                    bArr[(i5 * this.c) + i6] = (byte) iCountChars;
                }
            }
        }
    }

    void b(OrderableUnit var_1_21) {
        long a = 0L;
        if (var_1_21 == null) {
            a = PerformanceProfiler.a();
        }
        final TileMap q = this.q.q;
        final byte[] j = this.j;
        if (this.j == null) {
            var_1_21 = null;
        }
        this.j = new byte[this.b * this.c];
        short n = 0;
        short n2 = 0;
        short n3 = this.q.s;
        short n4 = this.q.t;
        if (var_1_21 != null) {
            Utility.copyByteArray(j, this.j);
            q.setCursorTileIndexFromWorldPoint(var_1_21.posX, var_1_21.posY);
            final Rect cc = var_1_21.cc();
            final short short3 = (short)q.cursorTileX;
            final short n5 = (short)q.cursorTileY;
            n = (short)(short3 - 5 + cc.a);
            n2 = (short)(n5 - 5 + cc.b);
            n3 = (short)(short3 + 5 + cc.c);
            n4 = (short)(n5 + 5 + cc.d);
        }
        if (n < 0) {
            n = 0;
        }
        if (n2 < 0) {
            n2 = 0;
        }
        if (n3 > this.q.s) {
            n3 = this.q.s;
        }
        if (n4 > this.q.t) {
            n4 = this.q.t;
        }
        for (short short4 = n; short4 < n3; ++short4) {
            for (short short3 = n2; short3 < n4; ++short3) {
                this.j[short4 * this.c + short3] = this.a(q, short4, short3);
            }
        }
        if (var_1_21 == null) {
            GameEngine.log("buildAndReplaceClearanceCost took:" + PerformanceProfiler.a(PerformanceProfiler.a(a)) + " for:" + this.a);
        }
    }

    final byte a(TileMap tileMap, short s, short s2) {
        int iCountChars;
        if (this.d[(s * this.c) + s2] == -1) {
            return (byte) 0;
        }
        int i = s2 - 3;
        int i2 = s + 3;
        int i3 = s2 + 3;
        int i4 = 4;
        for (int i5 = s - 3; i5 <= i2; i5++) {
            for (int i6 = i; i6 <= i3; i6++) {
                boolean z = false;
                if (tileMap.isInBounds(i5, i6)) {
                    if (this.d[(i5 * this.c) + i6] == -1) {
                        z = true;
                    }
                    if (this.e[(i5 * this.c) + i6] == -1) {
                        z = true;
                    }
                } else {
                    z = true;
                }
                if (z && (iCountChars = Utility.chebyshevDistance((int) s, (int) s2, i5, i6)) < i4) {
                    i4 = iCountChars;
                }
            }
        }
        return (byte) i4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void b() {
        final int b = this.b;
        final int c = this.c;
        this.g = new short[b * c];
        this.h = new HashMap();
        short short3 = 1;
        for (short short4 = 0; short4 < b; ++short4) {
            for (short short5 = 0; short5 < c; ++short5) {
                if (this.g[short4 * c + short5] == 0) {
                    if (short3 <= 0) {
                        Log.d("RustedWarfare", "warning buildIsolatedGroups looped, ending");
                        return;
                    }
                    final int a = this.a(short4, short5, short3);
                    if (a > 0) {
                        this.h.put(short3, a);
                        if (this.i < a) {
                            this.i = a;
                        }
                        ++short3;
                    }
                }
            }
        }
    }

    int a(short s, short s2, short s3) {
        int i = this.c;
        TileMap tileMap = this.q.q;
        short[] sArr = this.g;
        byte[] bArr = this.d;
        if (bArr[(s * i) + s2] == -1) {
            sArr[(s * i) + s2] = -1;
            return 0;
        }
        if (s3 == 0) {
            throw new RuntimeException("id cannot be 0 is will cause can endless loop");
        }
        int i2 = 0;
        CircularDeque circularDeque = new CircularDeque();
        circularDeque.add(new ShortPair(s, s2));
        while (!circularDeque.isEmpty()) {
            ShortPair shortPair = (ShortPair) circularDeque.a();
            short s4 = shortPair.a;
            short s5 = shortPair.b;
            if (tileMap.isInBounds((int) s4, (int) s5)) {
                int i3 = (s4 * i) + s5;
                if (sArr[i3] == 0 && bArr[i3] != -1) {
                    sArr[i3] = s3;
                    i2++;
                    circularDeque.add(new ShortPair((short) (s4 - 1), s5));
                    circularDeque.add(new ShortPair((short) (s4 + 1), s5));
                    circularDeque.add(new ShortPair(s4, (short) (s5 - 1)));
                    circularDeque.add(new ShortPair(s4, (short) (s5 + 1)));
                }
            }
        }
        return i2;
    }

    boolean c() {
        return (this.a.equals(UnitMovementType.AIR) || this.a.equals(UnitMovementType.NONE)) ? false : true;
    }

    public static int a(TileMap tileMap) {
        MapLayer mapLayer = tileMap.groundLayer;
        int i = 0;
        for (int i2 = 0; i2 < mapLayer.widthTiles; i2++) {
            for (int i3 = 0; i3 < mapLayer.heightTiles; i3++) {
                MapTile tileAt = mapLayer.getTileAt(i2, i3);
                if (tileAt != null) {
                    i += (0 + (tileAt.isWater ? 1 : 0) + (tileAt.isCliff ? 2 : 0) + (tileAt.hasLargeObject ? 4 : 0) + (tileAt.isLava ? 8 : 0) + (tileAt.isWaterBridge ? 16 : 0)) * (i2 + i3);
                }
            }
        }
        return i;
    }

    void d() {
        TileMap tileMap = this.q.q;
        byte[] bArr = this.d;
        short[] tileIds = tileMap.groundLayer.getTileIds();
        MapTile[] mapTileArr = tileMap.uniqueTiles;
        UnitMovementType unitMovementType = this.a;
        int i = this.b;
        int i2 = this.c;
        if (!c()) {
            return;
        }
        boolean z = unitMovementType.equals(UnitMovementType.WATER) || unitMovementType.equals(UnitMovementType.HOVER) || unitMovementType.equals(UnitMovementType.OVER_CLIFF_WATER);
        boolean z2 = unitMovementType.equals(UnitMovementType.HOVER) || unitMovementType.equals(UnitMovementType.OVER_CLIFF) || unitMovementType.equals(UnitMovementType.OVER_CLIFF_WATER);
        boolean z3 = unitMovementType.equals(UnitMovementType.OVER_CLIFF) || unitMovementType.equals(UnitMovementType.OVER_CLIFF_WATER);
        for (int i3 = 0; i3 < i; i3++) {
            for (int i4 = 0; i4 < i2; i4++) {
                int i5 = (i3 * i2) + i4;
                bArr[i5] = 0;
                MapTile mapTile = mapTileArr[tileIds[i5]];
                if (mapTile != null) {
                    if (mapTile.isWater && !z) {
                        bArr[i5] = -1;
                    }
                    if (mapTile.isCliff && !z2) {
                        bArr[i5] = -1;
                    }
                    if (mapTile.hasLargeObject && !z3) {
                        bArr[i5] = -1;
                    }
                    if (mapTile.isLava) {
                        bArr[i5] = -1;
                    }
                    if (unitMovementType == UnitMovementType.WATER && !mapTile.isWater && !mapTile.isWaterBridge) {
                        bArr[i5] = -1;
                    }
                }
                MapTile pathingOverrideTileAt = tileMap.getPathingOverrideTileAt(i3, i4);
                if (pathingOverrideTileAt != null) {
                    if (unitMovementType == UnitMovementType.LAND && pathingOverrideTileAt.isResourcePool) {
                        bArr[i5] = -1;
                    }
                    if (pathingOverrideTileAt.hasLargeObject && !z3) {
                        bArr[i5] = -1;
                    }
                    if (bArr[i5] == 0) {
                        bArr[i5] = pathingOverrideTileAt.movementBlockLevel;
                    }
                }
                if (mapTile != null && bArr[i5] == 0) {
                    bArr[i5] = mapTile.movementBlockLevel;
                }
            }
        }
        if (tileMap.groundOverlayLayer != null) {
            for (int i6 = 0; i6 < i; i6++) {
                for (int i7 = 0; i7 < i2; i7++) {
                    MapTile tileAt = tileMap.groundOverlayLayer.getTileAt(i6, i7);
                    if (tileAt != null) {
                        bArr[(i6 * i2) + i7] = 0;
                        if (tileAt.isWater && !z) {
                            bArr[(i6 * i2) + i7] = -1;
                        }
                        if (tileAt.isCliff && !z2) {
                            bArr[(i6 * i2) + i7] = -1;
                        }
                        if (tileAt.hasLargeObject && !z3) {
                            bArr[(i6 * i2) + i7] = -1;
                        }
                        if (tileAt.isLava && 0 == 0) {
                            bArr[(i6 * i2) + i7] = -1;
                        }
                        if (bArr[(i6 * i2) + i7] == 0) {
                            bArr[(i6 * i2) + i7] = tileAt.movementBlockLevel;
                        }
                        if (unitMovementType == UnitMovementType.WATER && !tileAt.isWater && !tileAt.isWaterBridge) {
                            bArr[(i6 * i2) + i7] = -1;
                        }
                    }
                }
            }
        }
    }

    public void c(OrderableUnit orderableUnit) {
        Rect rectCc;
        if (orderableUnit != null) {
            this.p++;
            if (this.p > 50) {
                this.o = true;
                return;
            }
        }
        GameEngine.getCurrentTimeMillis();
        this.e = new byte[this.b * this.c];
        byte[] bArr = this.e;
        if (this.a.equals(UnitMovementType.AIR)) {
            return;
        }
        TileMap tileMap = this.q.q;
        BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
        int size = BaseUnit.bE.size();
        for (int i = 0; i < size; i++) {
            BaseUnit baseUnit = baseUnitArrA[i];
            if (baseUnit.bI() && !baseUnit.isDead) {
                Point pointA = baseUnit.a(tileMap, this.n);
                int i2 = pointA.worldX;
                int i3 = pointA.worldY;
                if (this.a.equals(UnitMovementType.BUILDING)) {
                    rectCc = baseUnit.cd();
                } else {
                    rectCc = baseUnit.cc();
                }
                for (int i4 = i2 + rectCc.a; i4 <= i2 + rectCc.c; i4++) {
                    for (int i5 = i3 + rectCc.b; i5 <= i3 + rectCc.d; i5++) {
                        if (tileMap.isInBounds(i4, i5)) {
                            bArr[(i4 * this.c) + i5] = -1;
                        }
                    }
                }
            }
        }
    }

    public void e() {
        GameEngine.getCurrentTimeMillis();
        final int c = this.c;
        this.f = new byte[this.b * c];
        if (this.a.equals(UnitMovementType.AIR)) {
            return;
        }
        final TileMap q = this.q.q;
        final int halfTileWorldSizeX = q.halfTileWorldSizeX;
        final int halfTileWorldSizeY = q.halfTileWorldSizeY;
        final BaseUnit[] a = BaseUnit.bE.a();
        for (int i = 0; i < BaseUnit.bE.size(); ++i) {
            final BaseUnit baseUnit = a[i];
            if (baseUnit.isAlive && !baseUnit.bI() && !baseUnit.isMoving && !(baseUnit instanceof Tree) && !baseUnit.isDead && !baseUnit.i() && baseUnit.unitTransportTarget == null && !baseUnit.Q()) {
                int n = 2;
                q.setCursorTileIndexFromWorldPoint(baseUnit.posX, baseUnit.posY);
                final int cursorTileX = q.cursorTileX;
                final int cursorTileY = q.cursorTileY;
                final float n2 = baseUnit.radius + 5.0f;
                final float n3 = baseUnit.radius + 10.0f;
                if (n3 < 10.0f) {
                    n = 0;
                }
                else if (n3 < 20.0f) {
                    n = 1;
                }
                for (int j = cursorTileX - n; j <= cursorTileX + n; ++j) {
                    for (int k = cursorTileY - n; k <= cursorTileY + n; ++k) {
                        if (q.isInBounds(j, k)) {
                            q.setCursorTileIndexFromTileIndex(j, k);
                            final float distanceSq = Utility.distanceSq((float)(q.cursorTileX + halfTileWorldSizeX), (float)(q.cursorTileY + halfTileWorldSizeY), baseUnit.posX, baseUnit.posY);
                            final byte b = 6;
                            final int n4 = j * c + k;
                            if (distanceSq < n2 * n2) {
                                final byte[] f = this.f;
                                final int n5 = n4;
                                f[n5] += b;
                            }
                            else if (distanceSq < n3 * n3) {
                                final byte[] f2 = this.f;
                                final int n6 = n4;
                                f2[n6] += (byte)(b * 0.333);
                            }
                            if (this.f[n4] < -1) {
                                this.f[n4] = 127;
                            }
                        }
                    }
                }
            }
        }
    }

    public void a(boolean z) {
        if (!z) {
            return;
        }
        if (this.m) {
            this.l = 0;
            this.m = false;
            c(null);
            if (this.j != null) {
                a((OrderableUnit) null);
            }
        }
        this.l = 0;
    }
}
