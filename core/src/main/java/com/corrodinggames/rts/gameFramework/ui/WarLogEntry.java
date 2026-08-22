package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.au */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/au.class */
abstract class WarLogEntry implements Comparable<WarLogEntry> {

    /* JADX INFO: renamed from: c */
    long timestamp;

    /* JADX INFO: renamed from: d */
    long durationMs = 5000;

    /* JADX INFO: renamed from: e */
    float x;

    /* JADX INFO: renamed from: f */
    float y;

    /* JADX INFO: renamed from: g */
    String text;

    /* JADX INFO: renamed from: h */
    boolean hasBeenShown;

    /* JADX INFO: renamed from: i */
    boolean alwaysShow;

    public abstract void b(WarLogEntry warLogEntry);

    public abstract String a();

    public WarLogEntry(float f, float f2) {
        this.x = f;
        this.y = f2;
    }

    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: c, reason: merged with bridge method [inline-methods] */
    public int compareTo(WarLogEntry warLogEntry) {
        return (int) (warLogEntry.timestamp - this.timestamp);
    }

    public boolean a(WarLogEntry warLogEntry) {
        if (this.timestamp + b() < System.currentTimeMillis() || Utility.distanceSq(this.x, this.y, warLogEntry.x, warLogEntry.y) > 90000.0f) {
            return false;
        }
        return true;
    }

    protected long b() {
        return 5000L;
    }
}
