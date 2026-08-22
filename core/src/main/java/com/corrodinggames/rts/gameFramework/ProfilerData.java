package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bu */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bu.class */
public class ProfilerData {
    public long[] a = new long[PerformanceProfiler.dataCapacity];
    public long[] b = new long[PerformanceProfiler.dataCapacity];
    public float[] c = new float[PerformanceProfiler.dataCapacity];
    public long[] d = new long[PerformanceProfiler.dataCapacity];
    public long[] e = new long[PerformanceProfiler.dataCapacity];
    final /* synthetic */ PerformanceProfiler f;

    public ProfilerData(PerformanceProfiler performanceProfiler) {
        this.f = performanceProfiler;
    }
}
