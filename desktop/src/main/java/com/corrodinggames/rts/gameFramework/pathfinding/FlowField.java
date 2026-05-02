package com.corrodinggames.rts.gameFramework.pathfinding;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/g.class */
public class FlowField {
    int a;
    int b;
    int c;
    int d;
    byte[] e;
    byte[] f;

    public FlowField(int i, int i2) {
        this.a = i;
        this.b = i2;
        this.e = new byte[i * i2];
        this.f = new byte[i * i2];
    }

    public final byte a(int i, int i2) {
        return this.e[(i * this.b) + i2];
    }

    public final byte a(PathPoint pathPoint) {
        return this.e[(pathPoint.a * this.b) + pathPoint.b];
    }

    public boolean b(PathPoint pathPoint) {
        return a(pathPoint) <= 0;
    }

    public void a(PathPoint pathPoint, byte b) {
        this.e[(pathPoint.a * this.b) + pathPoint.b] = b;
    }

    public void a(PathPoint pathPoint, boolean z) {
        this.f[(pathPoint.a * this.b) + pathPoint.b] = (byte) (z ? 1 : 0);
    }

    public boolean c(PathPoint pathPoint) {
        return this.f[(pathPoint.a * this.b) + pathPoint.b] == 1;
    }
}
