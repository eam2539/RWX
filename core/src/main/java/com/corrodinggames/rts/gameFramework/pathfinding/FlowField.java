package com.corrodinggames.rts.gameFramework.pathfinding;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/g.class */
public class FlowField {

    /* JADX INFO: renamed from: a */
    int width;

    /* JADX INFO: renamed from: b */
    int height;

    int c;
    int d;

    /* JADX INFO: renamed from: e */
    byte[] flowDirections;

    /* JADX INFO: renamed from: f */
    byte[] flags;

    public FlowField(int i, int i2) {
        this.width = i;
        this.height = i2;
        this.flowDirections = new byte[i * i2];
        this.flags = new byte[i * i2];
    }

    public final byte a(int i, int i2) {
        return this.flowDirections[(i * this.height) + i2];
    }

    public final byte a(PathPoint pathPoint) {
        return this.flowDirections[(pathPoint.tileX * this.height) + pathPoint.tileY];
    }

    public boolean b(PathPoint pathPoint) {
        return a(pathPoint) <= 0;
    }

    public void a(PathPoint pathPoint, byte b) {
        this.flowDirections[(pathPoint.tileX * this.height) + pathPoint.tileY] = b;
    }

    public void a(PathPoint pathPoint, boolean z) {
        this.flags[(pathPoint.tileX * this.height) + pathPoint.tileY] = (byte) (z ? 1 : 0);
    }

    public boolean c(PathPoint pathPoint) {
        return this.flags[(pathPoint.tileX * this.height) + pathPoint.tileY] == 1;
    }
}
