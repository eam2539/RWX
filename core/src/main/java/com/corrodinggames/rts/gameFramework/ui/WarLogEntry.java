package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.au */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/au.class */
abstract class WarLogEntry implements Comparable<WarLogEntry> {
    long c;
    long d = 5000;
    float e;
    float f;
    String g;
    boolean h;
    boolean i;

    public abstract void b(WarLogEntry warLogEntry);

    public abstract String a();

    public WarLogEntry(float f, float f2) {
        this.e = f;
        this.f = f2;
    }

    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: c, reason: merged with bridge method [inline-methods] */
    public int compareTo(WarLogEntry warLogEntry) {
        return (int) (warLogEntry.c - this.c);
    }

    public boolean a(WarLogEntry warLogEntry) {
        if (this.c + b() < System.currentTimeMillis() || Utility.distanceSq(this.e, this.f, warLogEntry.e, warLogEntry.f) > 90000.0f) {
            return false;
        }
        return true;
    }

    protected long b() {
        return 5000L;
    }
}
