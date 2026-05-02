package com.corrodinggames.rts.gameFramework.path;

import android.graphics.Paint;
import android.graphics.Rect;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.map.MapLayer;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.pathfinding.DynamicUnitPath;
import com.corrodinggames.rts.gameFramework.pathfinding.Path;
import com.corrodinggames.rts.gameFramework.pathfinding.PathCostMap;
import com.corrodinggames.rts.gameFramework.pathfinding.PathSolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/l.class */
public final class PathEngine {
    public static final boolean a = false;
    static boolean b;
    static boolean c;
    static boolean d;
    public static DynamicUnitPath e;
    static boolean f;
    static boolean g;
    static boolean h;
    static int i;
    static boolean j;
    static ArrayList<Path> k;
    public static boolean l;
    public static final boolean m;
    public TileMap q;
    int r;
    public short s;
    public short t;
    public PathCostMap x;
    public PathCostMap y;
    public PathCostMap z;
    public PathCostMap A;
    public PathCostMap B;
    public PathCostMap C;
    public PathCostMap D;
    public PathCostMap E;
    public boolean n = true;
    PathSolver o = new PathSolver(this);
    boolean p = true;
    public ArrayList u = new ArrayList();
    public PathCostMap[] v = new PathCostMap[0];
    public Paint w = new Paint();
    Paint F = new Paint();
    public Object G = new Object();
    LinkedList<Path> I = new LinkedList();
    LinkedList<Path> J = new LinkedList();
    Object K = new Object();
    ArrayList<PathSolver> H = new ArrayList();

    static {
        b = !GameEngine.isGameBetaStatic;
        c = false;
        d = false;
        f = false;
        g = false;
        h = false;
        i = 20;
        j = false;
        k = new ArrayList();
        l = false;
        m = false;
    }

    public PathCostMap a(UnitMovementType unitMovementType) {
        for (PathCostMap pathCostMap : this.v) {
            if (pathCostMap.a == unitMovementType) {
                return pathCostMap;
            }
        }
        return null;
    }

    public boolean a(UnitMovementType unitMovementType, int i2, int i3) {
        return a(a(unitMovementType), i2, i3);
    }

    public boolean b(UnitMovementType unitMovementType, int i2, int i3) {
        return a(a(unitMovementType), i2, i3, true);
    }

    public boolean a(PathCostMap pathCostMap, int i2, int i3) {
        return a(pathCostMap, i2, i3, false);
    }

    public boolean a(PathCostMap pathCostMap, int i2, int i3, boolean z) {
        if (!this.q.isInBounds(i2, i3)) {
            return true;
        }
        if (pathCostMap.a == UnitMovementType.AIR) {
            return false;
        }
        int i4 = (i2 * this.t) + i3;
        if ((!z && pathCostMap.e[i4] == -1) || pathCostMap.d[i4] == -1 || pathCostMap.f[i4] == -1) {
            return true;
        }
        return false;
    }

    public final int b(PathCostMap pathCostMap, int i2, int i3) {
        if (!this.q.isInBounds(i2, i3)) {
            return -1;
        }
        if (pathCostMap.a == UnitMovementType.AIR) {
            return 0;
        }
        int i4 = (i2 * this.t) + i3;
        if (pathCostMap.d[i4] == -1 || pathCostMap.e[i4] == -1 || pathCostMap.f[i4] == -1) {
            return -1;
        }
        return pathCostMap.d[i4] + pathCostMap.e[i4] + (pathCostMap.f[i4] * 10);
    }

    public final int c(PathCostMap pathCostMap, int i2, int i3) {
        if (!this.q.isInBounds(i2, i3)) {
            return -1;
        }
        if (pathCostMap.a == UnitMovementType.AIR) {
            return 4;
        }
        if (pathCostMap.j == null) {
            return -1;
        }
        return pathCostMap.j[(i2 * this.t) + i3];
    }

    public boolean a(int i2, int i3) {
        if (!this.q.isInBounds(i2, i3)) {
            return true;
        }
        int i4 = (i2 * this.t) + i3;
        if (this.D.d[i4] != -1 || this.A.d[i4] == -1) {
            return false;
        }
        return true;
    }

    public boolean b(int i2, int i3) {
        if (!this.q.isInBounds(i2, i3)) {
            return true;
        }
        int i4 = (i2 * this.t) + i3;
        if (this.C.d[i4] != -1 || this.E.d[i4] == -1) {
            return false;
        }
        return true;
    }

    public synchronized void a(TileMap tileMap, boolean z) {
        d();
        GameEngine.log("PathEngine: Setting up map costs");
        boolean z2 = false;
        if (z && this.q != null && this.q == tileMap && this.s == tileMap.groundLayer.widthTiles && this.t == tileMap.groundLayer.heightTiles) {
            if (this.r == PathCostMap.a(tileMap)) {
                GameEngine.log("PathEngine: Keeping existing map costs");
                z2 = true;
            } else {
                GameEngine.log("PathEngine: Error: Map checksum does not match!!!");
            }
        }
        if (z2) {
        }
        this.q = tileMap;
        this.r = PathCostMap.a(tileMap);
        this.s = (short) tileMap.groundLayer.widthTiles;
        this.t = (short) tileMap.groundLayer.heightTiles;
        e = null;
        this.u.clear();
        this.v = new PathCostMap[0];
        this.x = new PathCostMap(this, UnitMovementType.NONE, this.s, this.t);
        this.y = new PathCostMap(this, UnitMovementType.LAND, this.s, this.t);
        this.y.b();
        this.y.a((OrderableUnit) null);
        this.z = new PathCostMap(this, UnitMovementType.BUILDING, this.s, this.t);
        this.A = new PathCostMap(this, UnitMovementType.WATER, this.s, this.t);
        this.A.b();
        this.A.a((OrderableUnit) null);
        this.B = new PathCostMap(this, UnitMovementType.AIR, this.s, this.t);
        this.C = new PathCostMap(this, UnitMovementType.HOVER, this.s, this.t);
        this.C.b();
        this.C.a((OrderableUnit) null);
        this.D = new PathCostMap(this, UnitMovementType.OVER_CLIFF, this.s, this.t);
        this.D.b();
        this.D.a((OrderableUnit) null);
        this.E = new PathCostMap(this, UnitMovementType.OVER_CLIFF_WATER, this.s, this.t);
        this.E.b();
        this.E.a((OrderableUnit) null);
        Iterator it = this.H.iterator();
        while (it.hasNext()) {
            ((PathSolver) it.next()).a(tileMap);
        }
        this.o.a(tileMap);
        GameEngine.log("PathEngine: Ready");
    }

    public void a() {
        int i2;
        int i3;
        int i4;
        GameEngine gameEngine = GameEngine.getInstance();
        PathCostMap pathCostMap = this.y;
        Rect rect = new Rect();
        float f2 = gameEngine.viewpointXSnapped;
        float f3 = gameEngine.viewpointYSnapped;
        float f4 = gameEngine.screenHeight;
        float f5 = gameEngine.viewpointHeight;
        MapLayer mapLayer = gameEngine.tileMap.groundLayer;
        int i5 = (int) ((f2 * this.q.tileScaleX) - 1.0f);
        if (i5 < 0) {
            i5 = 0;
        }
        int i6 = (int) ((f3 * this.q.tileScaleY) - 1.0f);
        if (i6 < 0) {
            i6 = 0;
        }
        int i7 = (int) (((f2 + f4) * this.q.tileScaleX) + 1.0f);
        if (i7 > this.s - 1) {
            i7 = this.s - 1;
        }
        int i8 = (int) (((f3 + f5) * this.q.tileScaleY) + 1.0f);
        if (i8 > this.t - 1) {
            i8 = this.t - 1;
        }
        for (int i9 = i5; i9 < i7 + 1; i9++) {
            for (int i10 = i6; i10 < i8 + 1; i10++) {
                if (mapLayer.getTileAt(i9, i10) != null) {
                    int i11 = i9 * this.q.tileWorldSizeX;
                    int i12 = i10 * this.q.tileWorldSizeY;
                    rect.a(i11, i12, i11 + this.q.tileWorldSizeX, i12 + this.q.tileWorldSizeY);
                    rect.a((int) (-f2), (int) (-f3));
                    boolean zB = rect.b((int) (gameEngine.gameUI.selectionBoxStartX / gameEngine.zoom), (int) (gameEngine.gameUI.selectionBoxStartY / gameEngine.zoom));
                    if (!g || zB) {
                        byte b2 = pathCostMap.d[(i9 * this.t) + i10];
                        byte b3 = pathCostMap.e[(i9 * this.t) + i10];
                        int i13 = pathCostMap.f[(i9 * this.t) + i10];
                        if (b2 == -1) {
                            i2 = 255;
                        } else {
                            i2 = b2 * 2;
                        }
                        if (b3 == -1) {
                            i3 = 255;
                        } else {
                            i3 = b3 * 2;
                        }
                        if (i13 == -1) {
                            i4 = 255;
                        } else {
                            if (i13 != 0) {
                                i13 += 30;
                            }
                            i4 = i13 * 2;
                        }
                        this.F.a(128, i2, i3, i4);
                        gameEngine.graphicsEngine2.b(rect, this.F);
                        if (zB && pathCostMap.f != null) {
                            gameEngine.graphicsEngine2.a("o:" + ((int) pathCostMap.f[(i9 * this.t) + i10]), rect.d(), rect.e(), gameEngine.loadingPaint);
                        }
                    }
                }
            }
        }
    }

    public void a(OrderableUnit orderableUnit) {
        if (orderableUnit != null) {
            PlayerTeam.b(orderableUnit);
        }
        for (PathCostMap pathCostMap : this.v) {
            pathCostMap.c(orderableUnit);
        }
        this.y.a(orderableUnit);
        this.C.a(orderableUnit);
        this.D.a(orderableUnit);
        this.E.a(orderableUnit);
    }

    public void b() {
        for (PathCostMap pathCostMap : this.v) {
            pathCostMap.e();
        }
    }

    public PathEngine() {
        this.H.add(new PathSolver(this));
        int iDoubleToString = Utility.doubleToString();
        if (iDoubleToString > 1) {
            GameEngine.log("PathEngine", "We have " + iDoubleToString + " cores, creating extra solvers");
            this.H.add(new PathSolver(this));
        } else {
            GameEngine.log("PathEngine", "We only have one core, using single solver");
        }
        Iterator it = this.H.iterator();
        while (it.hasNext()) {
            ((PathSolver) it.next()).c();
        }
    }

    public void c() {
        Iterator it = this.I.iterator();
        while (it.hasNext()) {
            ((Path) it.next()).w = true;
        }
        this.I.clear();
        h();
    }

    public void d() {
        Iterator it = this.I.iterator();
        while (it.hasNext()) {
            a((Path) it.next());
        }
        this.I.clear();
        h();
    }

    public void a(PathCostMap pathCostMap, boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (!z) {
            if (pathCostMap.k + 50 < gameEngine.currentTick) {
                pathCostMap.k = gameEngine.currentTick - 40;
                pathCostMap.e();
            }
            pathCostMap.a(z);
            return;
        }
        if (pathCostMap.k + 30 < gameEngine.currentTick) {
            pathCostMap.k = gameEngine.currentTick;
            pathCostMap.e();
        }
        pathCostMap.a(z);
    }

    public Path a(boolean z) {
        Path path;
        if (OrderableUnit.L) {
            path = new DynamicUnitPath(this, z);
        } else {
            path = new Path(this, z);
        }
        return path;
    }

    public void a(Path path, boolean z) {
        a(path, z, false);
    }

    public void a(Path path, boolean z, boolean z2) {
        if (!this.p) {
            GameEngine.log("PathEngine", "Cannot start new path, not running");
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        a(a(path.o), z);
        path.e();
        path.t = 300.0f;
        int iAbs = Utility.abs(path.h - path.l);
        int iAbs2 = Utility.abs(path.i - path.m);
        if (iAbs < 15 && iAbs2 < 15) {
            path.t = 12.0f;
        } else if (iAbs < 50 && iAbs2 < 50) {
            path.t = 16.0f;
        } else if (iAbs < 200 && iAbs2 < 200) {
            path.t = 24.0f;
        } else if (iAbs < 400 && iAbs2 < 400) {
            path.t = 50.0f;
        } else if (iAbs < 1000 && iAbs2 < 1000) {
            path.t = 100.0f;
        } else if (iAbs < 2000 && iAbs2 < 2000) {
            path.t = 200.0f;
        }
        if (!gameEngine.networkEngine.B && !gameEngine.replayEngine.i()) {
            if (iAbs < 1000 && iAbs2 < 1000) {
                path.t = 180.0f;
            } else {
                path.t = 360.0f;
            }
        }
        if (path.r) {
            path.t *= 2.0f;
            path.t += 50.0f;
        }
        path.s = path.t;
        if (!this.n || z2) {
            this.o.a(path);
            this.o.b();
            this.I.add(path);
        } else {
            b(path);
            this.I.add(path);
        }
    }

    public void a(float f2) {
        i();
    }

    public void b(float f2) {
        for (PathCostMap pathCostMap : this.v) {
            pathCostMap.p = 0;
            if (pathCostMap.o) {
                pathCostMap.o = false;
                pathCostMap.c(null);
            }
        }
        i();
        d(f2);
    }

    public void c(float f2) {
        if (j) {
            for (Path path : k) {
                path.h();
                path.g();
            }
        }
        if (d) {
            boolean z = true;
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine.gameUI.selectedUnitsList.b > 0) {
                BaseUnit baseUnitA = gameEngine.gameUI.selectedUnitsList.get(0);
                if (baseUnitA instanceof OrderableUnit) {
                    OrderableUnit orderableUnit = (OrderableUnit) baseUnitA;
                    if (orderableUnit.au != null) {
                        orderableUnit.au.d(orderableUnit);
                        z = false;
                    }
                }
            }
            if (z) {
            }
        }
        if (f) {
            a();
        }
        if (h) {
        }
    }

    public boolean e() {
        for (Path path : this.I) {
            if (path.t <= 0.0f && !path.c()) {
                return true;
            }
        }
        return false;
    }

    public String f() {
        String str = null;
        int i2 = 0;
        for (Path path : this.I) {
            if (path.t <= 0.0f && !path.c()) {
                if (str == null) {
                    str = "[distance:" + Utility.distance(path.h, path.i, path.l, path.m) + ", allowedDelay:" + path.s + " lowPriority:" + path.r + "]";
                }
                i2++;
            }
        }
        String str2 = "(total:" + i2 + ") ";
        if (str != null) {
            str2 = str2 + str;
        }
        return str2;
    }

    private void d(float f2) {
        Iterator it = this.I.iterator();
        while (it.hasNext()) {
            Path path = (Path) it.next();
            if (path.t <= 0.0f) {
                path.t = 0.0f;
                path.u = true;
                if (j) {
                    k.add(path);
                    if (k.size() > 10) {
                        k.remove(0);
                    }
                }
                if (!path.c()) {
                    if (GameEngine.getInstance().isInNetworkOrReplay()) {
                        GameEngine.log("PathEngine", "updateUnfinishedPaths: path wasn't solved, isGoingToBlockThisFrame did not protect");
                    }
                    a(path);
                }
                if (path.c()) {
                    it.remove();
                }
            } else {
                path.t -= f2;
            }
        }
    }

    private Path g() {
        Path path = null;
        for (Path path2 : this.J) {
            if (path == null || path.t > path2.t) {
                path = path2;
            }
        }
        if (path == null) {
            throw new RuntimeException("Failed to find any paths");
        }
        if (!this.J.remove(path)) {
            throw new RuntimeException("Failed remove found path");
        }
        return path;
    }

    private void b(Path path) {
        synchronized (this.K) {
            this.J.add(path);
        }
    }

    private void h() {
        synchronized (this.K) {
            this.J.clear();
        }
    }

    private void i() {
        PathSolver pathSolverJ;
        LinkedList linkedList = this.J;
        if (linkedList.size() > 0) {
            synchronized (this.K) {
                while (linkedList.size() > 0 && (pathSolverJ = j()) != null) {
                    Path pathG = g();
                    if (!pathG.v) {
                        a(pathSolverJ, pathG);
                    }
                }
            }
        }
    }

    private PathSolver j() {
        for (PathSolver pathSolver : this.H) {
            if (pathSolver.s) {
                return pathSolver;
            }
        }
        return null;
    }

    public void a(Path path) {
        PathSolver pathSolverJ;
        if (!path.v) {
            while (true) {
                synchronized (this.G) {
                    pathSolverJ = j();
                    if (pathSolverJ != null) {
                        break;
                    } else {
                        try {
                            this.G.wait(2000L);
                        } catch (InterruptedException e2) {
                        }
                    }
                }
            }
            a(pathSolverJ, path);
        }
        boolean z = false;
        long currentTimeMillis = GameEngine.getCurrentTimeMillis();
        while (true) {
            synchronized (this.G) {
                if (path.c()) {
                    break;
                }
                z = true;
                i();
                try {
                    this.G.wait(2000L);
                } catch (InterruptedException e3) {
                }
            }
            if (!z && b) {
                GameEngine.log("PathEngine", "We were blocked path(" + path.e + ") for:" + (GameEngine.getCurrentTimeMillis() - currentTimeMillis));
                return;
            }
        }
        if (!z) {
        }
    }

    private void a(PathSolver pathSolver, Path path) {
        synchronized (path) {
            if (!path.v) {
                pathSolver.a(path);
                pathSolver.a();
            }
        }
    }
}
