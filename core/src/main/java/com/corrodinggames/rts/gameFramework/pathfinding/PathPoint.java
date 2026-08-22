package com.corrodinggames.rts.gameFramework.pathfinding;

import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/p.class */
public final class PathPoint {

    /* JADX INFO: renamed from: a */
    public short tileX;

    /* JADX INFO: renamed from: b */
    public short tileY;

    public PathPoint() {
    }

    public PathPoint(short s, short s2) {
        a(s, s2);
    }

    public final PathPoint a(short s, short s2) {
        this.tileX = s;
        this.tileY = s2;
        return this;
    }

    public final PathPoint a(PathPoint pathPoint) {
        this.tileX = pathPoint.tileX;
        this.tileY = pathPoint.tileY;
        return this;
    }

    public final PathPoint a(PathOpenListNode pathOpenListNode) {
        this.tileX = pathOpenListNode.x;
        this.tileY = pathOpenListNode.y;
        return this;
    }

    public final int a(PathSolver pathSolver) {
        short s = this.tileX;
        short s2 = this.tileY;
        if (pathSolver.b[(s * pathSolver.h) + s2] == -1 || pathSolver.c[(s * pathSolver.h) + s2] == -1 || pathSolver.d[(s * pathSolver.h) + s2] == -1) {
            return -1;
        }
        return pathSolver.b[(s * pathSolver.h) + s2] + pathSolver.c[(s * pathSolver.h) + s2] + (pathSolver.d[(s * pathSolver.h) + s2] * 10);
    }

    public final int a(PathSolver pathSolver, byte b) {
        return pathSolver.l[b][(this.tileX * pathSolver.h) + this.tileY];
    }

    public final void a(PathSolver pathSolver, byte b, int i) {
        pathSolver.l[b][(this.tileX * pathSolver.h) + this.tileY] = i;
    }

    public final void a(PathSolver pathSolver, byte b, boolean z) {
        if (z) {
            byte[] bArr = pathSolver.m[b];
            int i = (this.tileX * pathSolver.h) + this.tileY;
            bArr[i] = (byte) (bArr[i] | 16);
        } else {
            byte[] bArr2 = pathSolver.m[b];
            int i2 = (this.tileX * pathSolver.h) + this.tileY;
            bArr2[i2] = (byte) (bArr2[i2] & (-17));
        }
    }

    public final boolean b(PathSolver pathSolver, byte b) {
        return pathSolver.l[b][(this.tileX * pathSolver.h) + this.tileY] >= pathSolver.i && (pathSolver.m[b][(this.tileX * pathSolver.h) + this.tileY] & 16) != 0;
    }

    public final byte c(PathSolver pathSolver, byte b) {
        return (byte) (pathSolver.m[b][(this.tileX * pathSolver.h) + this.tileY] & 7);
    }

    public final boolean d(PathSolver pathSolver, byte b) {
        return (pathSolver.m[b][(this.tileX * pathSolver.h) + this.tileY] & 8) != 0;
    }

    public final void b(PathSolver pathSolver, byte b, boolean z) {
        if (z) {
            byte[] bArr = pathSolver.m[b];
            int i = (this.tileX * pathSolver.h) + this.tileY;
            bArr[i] = (byte) (bArr[i] | 8);
        } else {
            byte[] bArr2 = pathSolver.m[b];
            int i2 = (this.tileX * pathSolver.h) + this.tileY;
            bArr2[i2] = (byte) (bArr2[i2] & (-9));
        }
    }

    public final void a(PathSolver pathSolver, byte b, byte b2) {
        byte[] bArr = pathSolver.m[b];
        int i = (this.tileX * pathSolver.h) + this.tileY;
        bArr[i] = (byte) (bArr[i] & (-16));
        byte[] bArr2 = pathSolver.m[b];
        int i2 = (this.tileX * pathSolver.h) + this.tileY;
        bArr2[i2] = (byte) (bArr2[i2] | (b2 & 15));
    }

    public final void a(PathSolver pathSolver, byte b, float f) {
        int i = (int) (((f / 360.0f) * 8.0f) + 0.5f);
        if (i < 0) {
            i += 8;
        }
        if (i > 7) {
            i -= 8;
        }
        if (i < 0) {
            i += 8;
        }
        if (i > 7) {
            i -= 8;
        }
        if (i < 0 || i > 7) {
            GameEngine.log("setCurrentDirectionFromAngle: dir:" + i + " direction:" + f);
            i = 0;
        }
        a(pathSolver, b, (byte) i);
    }

    public final boolean e(PathSolver pathSolver, byte b) {
        if (pathSolver.l[b][(this.tileX * pathSolver.h) + this.tileY] >= pathSolver.i) {
            return true;
        }
        return false;
    }

    public final PathPoint f(PathSolver pathSolver, byte b) {
        PathPoint pathPoint = new PathPoint();
        if (a(pathSolver, b, pathPoint)) {
            return pathPoint;
        }
        return null;
    }

    public final boolean a(PathSolver pathSolver, byte b, PathPoint pathPoint) {
        if (!e(pathSolver, b)) {
            pathPoint.a((short) -1, (short) -1);
            return false;
        }
        byte bC = c(pathSolver, b);
        if (d(pathSolver, b)) {
            pathPoint.a((short) -1, (short) -1);
            return false;
        }
        int i = 0;
        int i2 = 0;
        if (bC == 0) {
            i = 0 + 1;
        }
        if (bC == 1) {
            i++;
            i2 = 0 + 1;
        }
        if (bC == 2) {
            i2++;
        }
        if (bC == 3) {
            i2++;
            i--;
        }
        if (bC == 4) {
            i--;
        }
        if (bC == 5) {
            i--;
            i2--;
        }
        if (bC == 6) {
            i2--;
        }
        if (bC == 7) {
            i2--;
            i++;
        }
        pathPoint.a((short) (this.tileX - i), (short) (this.tileY - i2));
        return true;
    }
}
