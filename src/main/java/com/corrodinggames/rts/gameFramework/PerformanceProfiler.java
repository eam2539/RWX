package com.corrodinggames.rts.gameFramework;

import android.graphics.Paint;
import android.graphics.Rect;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.br */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/br.class */
public final class PerformanceProfiler {
    GameEngine a;
    public static int c = 40;
    public int b = 0;
    public int d = 0;
    ProfilerData e = new ProfilerData(this);
    Paint f = new Paint();
    Rect g = new Rect();
    int h = -1;

    public PerformanceProfiler(GameEngine gameEngine) {
        this.a = gameEngine;
    }

    public static final long a() {
        return System.nanoTime();
    }

    public static final float a(long j) {
        return (System.nanoTime() - j) / 1000000.0f;
    }

    public static final double a(long j, long j2) {
        return (j2 - j) / 1000000.0d;
    }

    public static final void a(String str, long j) {
        GameEngine.isInSpace(str + VariableScope.nullOrMissingString + a(a(j)));
    }

    public final void a(ProfilerSection profilerSection) {
    }

    public final void b(ProfilerSection profilerSection) {
    }

    public static final String a(double d) {
        return VariableScope.nullOrMissingString + Utility.toHexString(d, 3) + "ms";
    }

    public static final String b(double d) {
        return VariableScope.nullOrMissingString + (d / 1000000.0d) + "ms";
    }

    public final void b() {
    }

    public final void c() {
    }

    public final void a(boolean z, boolean z2) {
    }
}
