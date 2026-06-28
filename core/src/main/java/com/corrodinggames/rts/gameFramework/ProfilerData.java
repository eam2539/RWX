package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bu */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bu.class */
public class ProfilerData {
    public long[] a = new long[PerformanceProfiler.c];
    public long[] b = new long[PerformanceProfiler.c];
    public float[] c = new float[PerformanceProfiler.c];
    public long[] d = new long[PerformanceProfiler.c];
    public long[] e = new long[PerformanceProfiler.c];
    final /* synthetic */ PerformanceProfiler f;

    public ProfilerData(PerformanceProfiler performanceProfiler) {
        this.f = performanceProfiler;
    }
}
