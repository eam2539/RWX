package com.corrodinggames.rts.gameFramework.pathfinding;

import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.path.PathEngine;
import com.corrodinggames.rts.gameFramework.utility.Debug;
import com.corrodinggames.rts.gameFramework.utility.Log;
import io.github.rwx.render.canvas.KoolPaint;

import java.util.Iterator;
import java.util.LinkedList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/o.class */
public final class PathSolver implements Runnable {
    private final PathEngine C;
    public byte[] b;
    public byte[] c;
    public byte[] d;
    public short[] e;
    public byte[] f;
    private Path D;
    int g;
    int h;
    int[][] l;
    byte[][] m;
    PathCostMap n;
    static LinkedList t = new LinkedList();
    static int u;
    int v;
    Thread w;
    long y;
    long z;
    boolean a = true;
    int i = 5;
    int j = 0;
    int k = 0;
    final PathOpenListPool o = new PathOpenListPool();
    final PathOpenListPool p = new PathOpenListPool();
    final PathPoint q = new PathPoint();
    final PathPoint r = new PathPoint();
    public volatile boolean s = true;
    Object x = new Object();
    Object A = new Object();
    KoolPaint B = new KoolPaint();

    public void a(Path path) {
        if (!this.s) {
            throw new RuntimeException("setupNewPath: last path not yet finished");
        }
        this.s = false;
        a(path.movementType, path);
        path.isSolved = true;
        this.D = path;
    }

    public void a() {
        if (this.w == null) {
            throw new RuntimeException("thread==null");
        }
        synchronized (this.x) {
            this.x.notifyAll();
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        while (this.a) {
            synchronized (this.x) {
                if (this.D == null) {
                    try {
                        this.x.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (this.D != null) {
                b();
            }
        }
    }

    static {
        t.add(new PathPoint((short) -9, (short) -9));
        t.add(new PathPoint((short) -9, (short) -9));
        t.add(new PathPoint((short) -9, (short) -9));
        u = 0;
    }

    public void b() {
        LinkedList linkedListE;
        if (this.D instanceof DynamicUnitPath) {
            f();
            linkedListE = t;
        } else {
            linkedListE = e();
        }
        synchronized (this.C.G) {
            this.D.f();
            this.b = null;
            this.c = null;
            this.d = null;
            this.e = null;
            this.f = null;
            this.D.a(linkedListE);
            this.D = null;
            this.s = true;
            this.C.G.notifyAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PathSolver(PathEngine pathEngine) {
        this.C = pathEngine;
        int i = u;
        u = i + 1;
        this.v = i;
    }

    public synchronized void c() {
        if (this.w != null) {
            throw new RuntimeException("thread!=null");
        }
        this.w = new Thread(this);
        this.w.setName("PathSolver-" + this.v);
        this.w.setPriority(10);
        this.w.setDaemon(true);
        this.w.start();
    }

    public void a(TileMap tileMap) {
        this.g = tileMap.groundLayer.widthTiles;
        this.h = tileMap.groundLayer.heightTiles;
        this.l = new int[2][this.g * this.h];
        this.m = new byte[2][this.g * this.h];
        d();
    }

    public void d() {
        int i = (this.g * this.h * 42) + 1;
        this.i += i;
        if (this.i > Integer.MAX_VALUE - i || this.i < 0 || 0 != 0) {
            this.i = 5;
            for (int i2 = 0; i2 < 2; i2++) {
                for (int i3 = 0; i3 < this.g; i3++) {
                    for (int i4 = 0; i4 < this.h; i4++) {
                        this.l[i2][(i3 * this.h) + i4] = -1;
                    }
                }
            }
        }
    }

    public final int a(int i, int i2) {
        if (this.b[(i * this.h) + i2] == -1 || this.c[(i * this.h) + i2] == -1 || this.d[(i * this.h) + i2] == -1) {
            return -1;
        }
        return this.b[(i * this.h) + i2] + this.c[(i * this.h) + i2] + (this.d[(i * this.h) + i2] * 10);
    }

    public void a(UnitMovementType unitMovementType, Path path) {
        if (unitMovementType == null) {
            throw new RuntimeException("MovementType is null");
        }
        PathCostMap pathCostMapA = this.C.a(unitMovementType);
        if (pathCostMapA == null) {
            throw new RuntimeException("Could not get costs for:" + unitMovementType.toString());
        }
        this.n = pathCostMapA;
        this.b = path.y;
        this.c = path.z;
        this.d = path.A;
        this.e = path.B;
        this.f = path.C;
        if (this.b == null) {
            throw new RuntimeException("linkToPath failed mapCosts_mapCosts is null");
        }
        if (this.c == null) {
            throw new RuntimeException("linkToPath failed mapCosts_buildingCosts is null");
        }
        if (this.d == null) {
            throw new RuntimeException("linkToPath failed mapCosts_objectCosts is null");
        }
    }

    public void a(UnitMovementType unitMovementType, short s, short s2, Float f) {
        if (PathEngine.a) {
            Log.d("RustedWarfare", "path start is:" + ((int) s) + "," + ((int) s2));
        }
        PathPoint pathPoint = new PathPoint(s, s2);
        pathPoint.a(this, (byte) 0, this.i);
        if (f == null) {
            pathPoint.a(this, (byte) 0, (byte) 0);
            pathPoint.b(this, (byte) 0, true);
        } else {
            pathPoint.a(this, (byte) 0, f.floatValue());
            pathPoint.b(this, (byte) 0, true);
        }
        pathPoint.a(this, (byte) 0, false);
    }

    public void a(short s, short s2, short s3) {
        if (PathEngine.a) {
            Log.d("RustedWarfare", "path end is:" + ((int) s) + "," + ((int) s2) + " size:" + ((int) s3));
        }
        PathPoint pathPoint = new PathPoint(s, s2);
        pathPoint.a(this, (byte) 1, (byte) 0);
        pathPoint.b(this, (byte) 1, true);
        pathPoint.a(this, (byte) 1, this.i);
        pathPoint.a(this, (byte) 1, false);
    }

    static int b(int i, int i2) {
        if (i == i2) {
            return 0;
        }
        int i3 = i - i2;
        if (i3 < 0) {
            i3 = -i3;
        }
        if (i3 > 4) {
            i3 = 8 - i3;
        }
        if (i3 == 1) {
            return 4;
        }
        return i3 == 2 ? 21 : 25;
    }

    static int c(int i, int i2) {
        if (i == i2) {
            return 0;
        }
        int iAbs = Math.abs(i - i2);
        if (iAbs > 4) {
            iAbs = 8 - iAbs;
        }
        if (iAbs == 1) {
            return 4;
        }
        if (iAbs == 2) {
            return 21;
        }
        if (iAbs == 3) {
            return 4;
        }
        return (iAbs == 4 || iAbs == 5) ? 0 : 25;
    }

    public LinkedList e() {
        short s;
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        if (PathEngine.l && !GameEngine.getInstance().isMenuBackgroundMap) {
            Debug.startMethodTracing((String) "pathTrace", (int) 110000000);
        }
        Path path = this.D;
        int n6 = path.p ? 7 : 1;
        int n7 = path.q;
        if (PathEngine.a) {
            Log.d("RustedWarfare", "starting path for:" + this.n.a.toString());
        }
        PathPoint pathPoint = this.r;
        PathPoint pathPoint2 = this.q;
        short s2 = (short) this.h;
        int n8 = this.g;
        this.y = GameEngine.getCurrentTimeMillis();
        short s3 = path.startTileX;
        short s4 = path.startTileY;
        boolean bl = path.k;
        this.d();
        this.a(path.movementType, path.startTileX, path.startTileY, path.j);
        int n9 = path.endTileX;
        int n10 = path.endTileY;
        short s5 = path.n;
        LinkedList linkedList = new LinkedList();
        if (s3 == n9 && s4 == n10) {
            if (PathEngine.a) {
                Log.d("RustedWarfare", "no point pathing when start=end");
            }
            linkedList.clear();
            linkedList.add((Object) new PathPoint((short) n9, (short) n10));
            return linkedList;
        }
        if (this.n.a.equals((Object) UnitMovementType.NONE)) {
            if (PathEngine.a) {
                Log.d("RustedWarfare", "no point pathing for none");
            }
            linkedList.clear();
            return linkedList;
        }
        int n11 = 0;
        int n12 = n9;
        int n13 = n10;
        short s6 = s5;
        if (this.e != null) {
            int n14;
            short[] sArray = this.e;
            n5 = sArray[s3 * s2 + s4];
            n4 = 1;
            if (n5 == -1) {
                n4 = 0;
            } else {
                for (n14 = (int) (n9 - s5); n14 <= n9 + s5; n14 = (short) (n14 + 1)) {
                    for (n3 = (short) (n10 - s5); n3 <= n10 + s5; n3 = (short) (n3 + 1)) {
                        if (n14 < 0 || n14 >= n8 || n3 < 0 || n3 >= s2 || n5 != sArray[n14 * s2 + n3]) continue;
                        n4 = 0;
                    }
                }
            }
            if (n4 != 0) {
                float f;
                int n15;
                if (PathEngine.a) {
                    Log.d("RustedWarfare", "end is blocked on isolated groups");
                }
                n14 = n12;
                n3 = n13;
                float f2 = -1.0f;
                for (n15 = (int) (n9 - 25); n15 <= n9 + 25; n15 = (short) (n15 + 1)) {
                    for (n2 = (int) (n10 - 25); n2 <= n10 + 25; n2 = (short) (n2 + 1)) {
                        if (n15 < 0 || n15 >= n8 || n2 < 0 || n2 >= s2 || n5 != sArray[n15 * s2 + n2] && sArray[n15 * s2 + n2] != 0)
                            continue;
                        f = Utility.distanceSq(n15, n2, n9, n10);
                        if (f2 != -1.0f && !(f < f2)) continue;
                        f2 = f;
                        n14 = n15;
                        n3 = n2;
                        s6 = 0;
                    }
                }
                if (f2 == -1.0f) {
                    for (n15 = 0; n15 < n8; n15 = (short) (n15 + 1)) {
                        for (n2 = 0; n2 < s2; n2 = (int) ((short) (n2 + 1))) {
                            if (n5 != sArray[n15 * s2 + n2] && sArray[n15 * s2 + n2] != 0) continue;
                            f = Utility.distanceSq(n15, n2, n9, n10);
                            if (f2 != -1.0f && !(f < f2)) continue;
                            f2 = f;
                            n14 = n15;
                            n3 = n2;
                            s6 = 0;
                        }
                    }
                }
                n12 = n14;
                n13 = n3;
                if (PathEngine.a) {
                    long l = System.currentTimeMillis() - this.y;
                    Log.d("RustedWarfare", "fakeNode search was:" + l);
                }
            }
        }
        boolean bl2 = true;
        block6:
        for (n5 = (int) (n12 - s6); n5 <= n12 + s6; n5 = (short) (n5 + 1)) {
            for (n4 = (int) (n13 - s6); n4 <= n13 + s6; n4 = (short) (n4 + 1)) {
                if (n5 < 0 || n5 >= n8 || n4 < 0 || n4 >= s2 || this.a(n5, n4) == -1) continue;
                bl2 = false;
                break block6;
            }
        }
        if (bl2) {
            n5 = n12;
            n4 = n13;
            float f = -1.0f;
            if (PathEngine.a) {
                Log.d("RustedWarfare", "end is blocked on non isolated groups");
            }
            for (n3 = (short) (n12 - 9); n3 <= n12 + 9; n3 = (short) (n3 + 1)) {
                for (int n16 = (int) (n13 - 9); n16 <= n13 + 9; n16 = (short) (n16 + 1)) {
                    if (n3 < 0 || n3 >= n8 || n16 < 0 || n16 >= s2 || this.a(n3, n16) == -1) continue;
                    float f3 = Utility.distanceSq(n3, n16, n12, n13);
                    if (f != -1.0f && !(f3 < f)) continue;
                    f = f3;
                    n5 = n3;
                    n4 = n16;
                    s6 = 0;
                }
            }
            if (f == -1.0f) {
                for (n3 = 0; n3 < n8; n3 = (short) (n3 + 1)) {
                    for (short s7 = 0; s7 < s2; s7 = (short) ((short) (s7 + 1))) {
                        if (this.a(n3, s7) == -1) continue;
                        float f4 = Utility.distanceSq(n3, s7, n12, n13);
                        if (f != -1.0f && !(f4 < f)) continue;
                        f = f4;
                        n5 = n3;
                        n4 = s7;
                        s6 = 0;
                    }
                }
            }
            n12 = n5;
            n13 = n4;
            if (PathEngine.a) {
                long l = System.currentTimeMillis() - this.y;
                Log.d("RustedWarfare", "fakeNode search was:" + l);
            }
        }
        if (PathEngine.a && (n12 != n9 || n13 != n10)) {
            Log.d("RustedWarfare", "Moved end to fakeEndX:" + n12 + " fakeEndY:" + n13);
        }
        this.o.a(n12, n13);
        this.o.a(0, s3, s4);
        this.p.a(s3, s4);
        this.a((short) n12, (short) n13, s6);
        this.p.a(0, (short) n12, (short) n13);
        bl2 = false;
        n5 = 0;
        n4 = -1;
        short s8 = -1;
        short s9 = -1;
        short s10 = -1;
        int n17 = 400;
        n2 = 0;
        while (!bl2) {
            byte by;
            boolean bl3;
            PathOpenListPool pathOpenListPool;
            PathOpenListNode pathOpenListNode;
            ++n2;
            if (path.w) {
                linkedList.clear();
                return linkedList;
            }
            ++n11;
            if (n17 > 0) {
                --n17;
            } else {
                n5 = n5 == 0 ? 1 : 0;
            }
            byte by2 = 0;
            if (n5 != 0) {
                by2 = 1;
            }
            if ((pathOpenListNode = (pathOpenListPool = n5 == 0 ? this.o : this.p).c()) == null) {
                if (n5 != 0) continue;
                if (!PathEngine.a) break;
                Log.d("RustedWarfare", "listNode==null for normal side ending path");
                break;
            }
            PathPoint pathPoint3 = pathPoint2.a(pathOpenListNode);
            int n18 = pathPoint3.a(this, by2);
            byte by3 = pathPoint3.c(this, by2);
            n = pathPoint3.d(this, by2) ? 1 : 0;
            s = 0;
            if (n5 == 0) {
                if (pathPoint3.tileX == n12 && pathPoint3.tileY == n13) {
                    if (PathEngine.a) {
                        Log.d("RustedWarfare", "Over goal: fakeEnd");
                    }
                    s = 1;
                }
                if (Utility.abs(pathPoint3.tileX - n9) <= s5 && Utility.abs(pathPoint3.tileY - n10) <= s5) {
                    if (PathEngine.a) {
                        Log.d("RustedWarfare", "Over goal: real end");
                    }
                    s = 1;
                }
            }
            if ((bl3 = pathPoint3.b(this, (byte) (1 - by2))) || s != 0) {
                pathPoint.a(pathPoint3);
                if (!bl3) {
                    if (PathEngine.a) {
                        Log.d("RustedWarfare", "Not closedOnOtherSide");
                    }
                    n4 = pathPoint2.tileX;
                    s8 = pathPoint2.tileY;
                    bl2 = true;
                    break;
                }
                PathPoint pathPoint4 = pathPoint.f(this, by2);
                if (pathPoint4 == null) {
                    Log.d("RustedWarfare", "findPath: otherConnection==null");
                    Log.d("RustedWarfare", "currentNode:" + pathPoint2.tileX + "," + pathPoint2.tileY);
                    Log.d("RustedWarfare", "currentNode cost normal:" + pathPoint2.a(this, (byte) 0));
                    Log.d("RustedWarfare", "currentNode cost opposite:" + pathPoint2.a(this, (byte) 1));
                    linkedList.clear();
                    return linkedList;
                }
                if (n5 == 0) {
                    if (PathEngine.a) {
                        Log.d("RustedWarfare", "closing path runFromOppositeSide=false");
                    }
                    s9 = pathPoint2.tileX;
                    s10 = pathPoint2.tileY;
                    n4 = pathPoint4.tileX;
                    s8 = pathPoint4.tileY;
                } else {
                    if (PathEngine.a) {
                        Log.d("RustedWarfare", "closing path runFromOppositeSide=true");
                    }
                    s9 = pathPoint4.tileX;
                    s10 = pathPoint4.tileY;
                    n4 = pathPoint2.tileX;
                    s8 = pathPoint2.tileY;
                }
                bl2 = true;
                break;
            }
            pathPoint3.a(this, by2, true);
            byte by4 = 0;
            byte by5 = 7;
            if (n != 0) {
                by4 = 0;
                by5 = 7;
            } else {
                by = 2;
                if (this.f != null && this.f[pathPoint3.tileX * s2 + pathPoint3.tileY] > 1) {
                    by = 1;
                }
                by4 = (byte) (by3 - by);
                by5 = (byte) (by3 + by);
            }
            for (by = by4; by <= by5; by = (byte) ((byte) (by + 1))) {
                byte by6;
                int n19;
                int n20;
                pathPoint.a(pathPoint3);
                byte by7 = by;
                if (by7 > 7) {
                    by7 = (byte) (by7 - 8);
                }
                if (by7 < 0) {
                    by7 = (byte) (by7 + 8);
                }
                if (by7 == 0) {
                    pathPoint.tileX = (short) (pathPoint.tileX + 1);
                }
                if (by7 == 1) {
                    pathPoint.tileX = (short) (pathPoint.tileX + 1);
                    pathPoint.tileY = (short) (pathPoint.tileY + 1);
                }
                if (by7 == 2) {
                    pathPoint.tileY = (short) (pathPoint.tileY + 1);
                }
                if (by7 == 3) {
                    pathPoint.tileY = (short) (pathPoint.tileY + 1);
                    pathPoint.tileX = (short) (pathPoint.tileX - 1);
                }
                if (by7 == 4) {
                    pathPoint.tileX = (short) (pathPoint.tileX - 1);
                }
                if (by7 == 5) {
                    pathPoint.tileX = (short) (pathPoint.tileX - 1);
                    pathPoint.tileY = (short) (pathPoint.tileY - 1);
                }
                if (by7 == 6) {
                    pathPoint.tileY = (short) (pathPoint.tileY - 1);
                }
                if (by7 == 7) {
                    pathPoint.tileY = (short) (pathPoint.tileY - 1);
                    pathPoint.tileX = (short) (pathPoint.tileX + 1);
                }
                if (pathPoint.tileX < 0 || pathPoint.tileX >= n8 || pathPoint.tileY < 0 || pathPoint.tileY >= s2 || (n20 = pathPoint.a(this)) == -1)
                    continue;
                int n21 = pathPoint.a(this, by2);
                if (pathPoint.b(this, by2)) continue;
                if (pathPoint.tileX != pathPoint3.tileX && pathPoint.tileY != pathPoint3.tileY) {
                    if (this.a(pathPoint.tileX, pathPoint3.tileY) == -1 || this.a(pathPoint3.tileX, pathPoint.tileY) == -1)
                        continue;
                    n19 = n18 + (14 + n20) + 1;
                } else {
                    n19 = n18 + (10 + n20) + 1;
                }
                if (by3 != by7) {
                    if (n == 0) {
                        n19 += PathSolver.b(by3, by7);
                    } else if (n5 == 0) {
                        n19 = bl ? (n19 += PathSolver.c(by3, by7)) : (n19 += PathSolver.b(by3, by7));
                    }
                }
                if (this.f != null) {
                    n19 += (4 - this.f[pathPoint.tileX * s2 + pathPoint.tileY]) * n6;
                }
                if (n7 > 0 && this.f != null && (by6 = this.f[pathPoint.tileX * s2 + pathPoint.tileY]) <= n7) {
                    n19 += 100;
                }
                if (n21 >= this.i && n19 >= n21) continue;
                pathPoint.a(this, by2, by7);
                pathPoint.a(this, by2, false);
                pathPoint.a(this, by2, n19);
                pathOpenListPool.a(n19 - this.i, pathPoint.tileX, pathPoint.tileY);
            }
        }
        if (PathEngine.a) {
            Log.d("RustedWarfare", "grid path finshed in :" + n2 + " ticks");
            long l = System.currentTimeMillis() - this.y;
            Log.d("RustedWarfare", "grid path done in:" + l);
        }
        long l = System.currentTimeMillis();
        if (!bl2) {
            if (PathEngine.a) {
                Log.d("RustedWarfare", "could not find end node");
            }
            long l2 = System.currentTimeMillis();
            float f = -1.0f;
            PathPoint pathPoint5 = new PathPoint();
            for (n = 0; n < n8; n = (short) (n + 1)) {
                for (s = 0; s < s2; s = (short) (s + 1)) {
                    pathPoint5.a((short) n, s);
                    if (!pathPoint5.e(this, (byte) 0)) continue;
                    float f5 = Utility.distanceSq(n, s, n9, n10);
                    if (f != -1.0f && !(f5 < f)) continue;
                    f = f5;
                    n4 = n;
                    s8 = s;
                }
            }
            if (PathEngine.a) {
                long l3 = System.currentTimeMillis() - l2;
                Log.d("RustedWarfare", "got closest node in:" + l3);
            }
        }
        linkedList.clear();
        if (n4 != -1 && s8 != -1) {
            LinkedList linkedList2 = this.a((byte) 0, (short) n4, s8);
            linkedList.addAll(this.a(linkedList2));
        }
        if (s9 != -1 && s10 != -1) {
            LinkedList linkedList3 = this.a((byte) 1, s9, s10);
            linkedList.addAll(linkedList3);
        }
        if (linkedList.size() > 1) {
            linkedList.remove(0);
        }
        this.z = GameEngine.getCurrentTimeMillis();
        if (PathEngine.a) {
            long l4 = this.z - this.y;
            Log.d("RustedWarfare", "path(" + path.pathId + ") finished in:" + l4);
        }
        if (PathEngine.l && !GameEngine.getInstance().isMenuBackgroundMap) {
            Debug.stopMethodTracing();
            PathEngine.l = false;
        }
        return linkedList;
    }

    public LinkedList a(byte b, short s, short s2) {
        LinkedList linkedList = new LinkedList();
        PathPoint pathPoint = new PathPoint(s, s2);
        linkedList.add(pathPoint);
        while (true) {
            PathPoint pathPointF = pathPoint.f(this, b);
            if (pathPointF != null) {
                linkedList.add(pathPointF);
                pathPoint = pathPointF;
            } else {
                return linkedList;
            }
        }
    }

    public LinkedList a(LinkedList linkedList) {
        LinkedList linkedList2 = new LinkedList();
        Iterator it = linkedList.iterator();
        while (it.hasNext()) {
            linkedList2.addFirst((PathPoint) it.next());
        }
        return linkedList2;
    }

    public void f() {
        g();
    }

    public void g() {
        final DynamicUnitPath e = (DynamicUnitPath) this.D;
        final FlowField b = new FlowField(this.g, this.h);
        if (PathEngine.l && !GameEngine.getInstance().isMenuBackgroundMap) {
            Debug.startMethodTracing("pathTrace", 110000000);
        }
        final int n = 7;
        final byte b2 = 0;
        if (PathEngine.a) {
            Log.d("RustedWarfare", "starting path for:" + this.n.a.toString());
        }
        final PathPoint r = this.r;
        final PathPoint q = this.q;
        final int h = this.h;
        final int g = this.g;
        this.y = GameEngine.getCurrentTimeMillis();
        final short h2 = e.startTileX;
        final short i = e.startTileY;
        final boolean k = e.k;
        this.d();
        this.a(e.movementType, e.startTileX, e.startTileY, e.j);
        final short l = e.endTileX;
        final short m = e.endTileY;
        final short n2 = e.n;
        final LinkedList list = new LinkedList();
        if (this.n.a.equals(UnitMovementType.NONE)) {
            if (PathEngine.a) {
                Log.d("RustedWarfare", "no point pathing for none");
            }
            return;
        }
        int n3 = 0;
        short n4 = l;
        short n5 = m;
        short short3 = n2;
        if (this.e != null) {
            final short[] e2 = this.e;
            final short integer1 = e2[h2 * h + i];
            boolean b3 = true;
            if (integer1 == -1) {
                b3 = false;
            } else {
                for (short n6 = (short) (l - n2); n6 <= l + n2; ++n6) {
                    for (short n7 = (short) (m - n2); n7 <= m + n2; ++n7) {
                        if (n6 >= 0) {
                            if (n6 < g) {
                                if (n7 >= 0) {
                                    if (n7 < h) {
                                        if (integer1 == e2[n6 * h + n7]) {
                                            b3 = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (b3) {
                if (PathEngine.a) {
                    Log.d("RustedWarfare", "end is blocked on isolated groups");
                }
                short n6 = n4;
                short n7 = n5;
                float n8 = -1.0f;
                for (short n9 = (short) (l - 25); n9 <= l + 25; ++n9) {
                    for (short n10 = (short) (m - 25); n10 <= m + 25; ++n10) {
                        if (n9 >= 0) {
                            if (n9 < g) {
                                if (n10 >= 0) {
                                    if (n10 < h) {
                                        if (integer1 == e2[n9 * h + n10] || e2[n9 * h + n10] == 0) {
                                            final float n11 = Utility.distanceSq(n9, n10, l, m);
                                            if (n8 == -1.0f || n11 < n8) {
                                                n8 = n11;
                                                n6 = n9;
                                                n7 = n10;
                                                short3 = 0;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (n8 == -1.0f) {
                    for (short n9 = 0; n9 < g; ++n9) {
                        for (short n10 = 0; n10 < h; ++n10) {
                            if (integer1 == e2[n9 * h + n10] || e2[n9 * h + n10] == 0) {
                                final float n11 = Utility.distanceSq(n9, n10, l, m);
                                if (n8 == -1.0f || n11 < n8) {
                                    n8 = n11;
                                    n6 = n9;
                                    n7 = n10;
                                    short3 = 0;
                                }
                            }
                        }
                    }
                }
                n4 = n6;
                n5 = n7;
                if (PathEngine.a) {
                    Log.d("RustedWarfare", "fakeNode search was:" + (System.currentTimeMillis() - this.y));
                }
            }
        }
        int j = 1;
        short integer1;
        Label_0876:
        for (integer1 = (short) (n4 - short3); integer1 <= n4 + short3; ++integer1) {
            for (short integer2 = (short) (n5 - short3); integer2 <= n5 + short3; ++integer2) {
                if (integer1 >= 0) {
                    if (integer1 < g) {
                        if (integer2 >= 0) {
                            if (integer2 < h) {
                                if (this.a(integer1, integer2) != -1) {
                                    j = 0;
                                    break Label_0876;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (j != 0) {
            integer1 = n4;
            short integer2 = n5;
            float n12 = -1.0f;
            if (PathEngine.a) {
                Log.d("RustedWarfare", "end is blocked on non isolated groups");
            }
            for (short n7 = (short) (n4 - 9); n7 <= n4 + 9; ++n7) {
                for (short n13 = (short) (n5 - 9); n13 <= n5 + 9; ++n13) {
                    if (n7 >= 0) {
                        if (n7 < g) {
                            if (n13 >= 0) {
                                if (n13 < h) {
                                    if (this.a(n7, n13) != -1) {
                                        final float n14 = Utility.distanceSq(n7, n13, n4, n5);
                                        if (n12 == -1.0f || n14 < n12) {
                                            n12 = n14;
                                            integer1 = n7;
                                            integer2 = n13;
                                            short3 = 0;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (n12 == -1.0f) {
                for (short n7 = 0; n7 < g; ++n7) {
                    for (short n13 = 0; n13 < h; ++n13) {
                        if (this.a(n7, n13) != -1) {
                            final float n14 = Utility.distanceSq(n7, n13, n4, n5);
                            if (n12 == -1.0f || n14 < n12) {
                                n12 = n14;
                                integer1 = n7;
                                integer2 = n13;
                                short3 = 0;
                            }
                        }
                    }
                }
            }
            n4 = integer1;
            n5 = integer2;
            if (PathEngine.a) {
                Log.d("RustedWarfare", "fakeNode search was:" + (System.currentTimeMillis() - this.y));
            }
        }
        if (PathEngine.a && (n4 != l || n5 != m)) {
            Log.d("RustedWarfare", "Moved end to fakeEndX:" + (int) n4 + " fakeEndY:" + (int) n5);
        }
        this.o.a(n4, n5);
        this.o.a(0, h2, i);
        this.p.a(h2, i);
        this.a(n4, n5, short3);
        this.p.a(0, n4, n5);
        j = 0;
        short integer2 = -1;
        short n6 = -1;
        short n7 = -1;
        short n13 = -1;
        short n9 = 0;
        while (j == 0) {
            ++n9;
            if (e.w) {
                return;
            }
            ++n3;
            byte byte2 = 0;
            byte2 = 1;
            final PathOpenListPool p = this.p;
            final PathOpenListNode c = p.c();
            if (c == null) {
                break;
            }
            final PathPoint a = q.a(c);
            final int a2 = a.a(this, byte2);
            final byte integer3 = (byte) (b.a(a) - 1);
            final boolean b4 = b.b(a);
            b.a(a, true);
            byte b5 = 7;
            byte b6;
            if (b4) {
                b6 = 0;
                b5 = 7;
            } else {
                byte b7 = 2;
                if (this.f != null && this.f[a.tileX * h + a.tileY] > 1) {
                    b7 = 1;
                }
                b6 = (byte) (integer3 - b7);
                b5 = (byte) (integer3 + b7);
            }
            for (byte b7 = b6; b7 <= b5; ++b7) {
                r.a(a);
                byte integer4 = b7;
                if (integer4 > 7) {
                    integer4 -= 8;
                }
                if (integer4 < 0) {
                    integer4 += 8;
                }
                if (integer4 == 0) {
                    final PathPoint pathPoint = r;
                    ++pathPoint.tileX;
                }
                if (integer4 == 1) {
                    final PathPoint pathPoint2 = r;
                    ++pathPoint2.tileX;
                    final PathPoint pathPoint3 = r;
                    ++pathPoint3.tileY;
                }
                if (integer4 == 2) {
                    final PathPoint pathPoint4 = r;
                    ++pathPoint4.tileY;
                }
                if (integer4 == 3) {
                    final PathPoint pathPoint5 = r;
                    ++pathPoint5.tileY;
                    final PathPoint pathPoint6 = r;
                    --pathPoint6.tileX;
                }
                if (integer4 == 4) {
                    final PathPoint pathPoint7 = r;
                    --pathPoint7.tileX;
                }
                if (integer4 == 5) {
                    final PathPoint pathPoint8 = r;
                    --pathPoint8.tileX;
                    final PathPoint pathPoint9 = r;
                    --pathPoint9.tileY;
                }
                if (integer4 == 6) {
                    final PathPoint pathPoint10 = r;
                    --pathPoint10.tileY;
                }
                if (integer4 == 7) {
                    final PathPoint pathPoint11 = r;
                    --pathPoint11.tileY;
                    final PathPoint pathPoint12 = r;
                    ++pathPoint12.tileX;
                }
                if (r.tileX >= 0) {
                    if (r.tileX < g) {
                        if (r.tileY >= 0) {
                            if (r.tileY < h) {
                                final int a3 = r.a(this);
                                if (a3 != -1) {
                                    final int a4 = r.a(this, byte2);
                                    if (!b.c(r)) {
                                        int integer5;
                                        if (r.tileX != a.tileX && r.tileY != a.tileY) {
                                            if (this.a(r.tileX, a.tileY) == -1) {
                                                continue;
                                            }
                                            if (this.a(a.tileX, r.tileY) == -1) {
                                                continue;
                                            }
                                            integer5 = a2 + (14 + a3) + 1;
                                        } else {
                                            integer5 = a2 + (10 + a3) + 1;
                                        }
                                        if (integer3 != integer4 && !b4) {
                                            integer5 += b(integer3, integer4);
                                        }
                                        if (this.f != null) {
                                            integer5 += (4 - this.f[r.tileX * h + r.tileY]) * n;
                                        }
                                        if (b2 > 0 && this.f != null && this.f[r.tileX * h + r.tileY] <= b2) {
                                            integer5 += 100;
                                        }
                                        if (a4 < this.i || integer5 < a4) {
                                            b.a(r, (byte) (integer4 + 1));
                                            b.a(r, false);
                                            r.a(this, byte2, integer5);
                                            p.a(integer5 - this.i, r.tileX, r.tileY);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (PathEngine.a) {
            Log.d("RustedWarfare", "grid path finshed in :" + (int) n9 + " ticks");
            final long currentTimeMillis = System.currentTimeMillis() - this.y;
            Log.d("RustedWarfare", "grid path done in:" + currentTimeMillis);
        }
        final long currentTimeMillis = System.currentTimeMillis();
        b.c = n4;
        b.d = n5;
        e.flowField = b;
        PathEngine.e = e;
        this.z = GameEngine.getCurrentTimeMillis();
        if (PathEngine.a) {
            Log.d("RustedWarfare", "path(" + e.pathId + ") finished in:" + (this.z - this.y));
        }
        if (PathEngine.l && !GameEngine.getInstance().isMenuBackgroundMap) {
            Debug.stopMethodTracing();
            PathEngine.l = false;
        }
    }
}
