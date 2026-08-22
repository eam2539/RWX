package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bt */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bt.class */
public class ProfilerTimer {

    /* JADX INFO: renamed from: a */
    boolean enabled;

    /* JADX INFO: renamed from: b */
    int count;

    /* JADX INFO: renamed from: c */
    double totalTimeMs;

    /* JADX INFO: renamed from: d */
    double peakTimeMs;

    /* JADX INFO: renamed from: e */
    long startTimeNs;

    /* JADX INFO: renamed from: f */
    String name;

    public ProfilerTimer(String str) {
        this.enabled = true;
        this.name = str;
    }

    public ProfilerTimer(String str, boolean z) {
        this.enabled = true;
        this.name = str;
        this.enabled = z;
    }

    public void a() {
        if (this.enabled) {
            if (this.startTimeNs != 0) {
                this.startTimeNs = Long.MIN_VALUE;
            } else {
                this.startTimeNs = PerformanceProfiler.a();
            }
        }
    }

    public void b() {
        if (this.enabled) {
            double dA = PerformanceProfiler.a(this.startTimeNs, PerformanceProfiler.a());
            this.totalTimeMs += dA;
            this.count++;
            if (dA > this.peakTimeMs) {
                this.peakTimeMs = dA;
            }
            this.startTimeNs = 0L;
        }
    }

    public String c() {
        String str;
        if (!this.enabled) {
            return "{ Not enabled }";
        }
        if (this.count > 0) {
            str = ((("{ #" + this.count + " = ") + "peak:" + Utility.padString(this.peakTimeMs, 2) + "ms ") + "avg:" + Utility.padString(this.totalTimeMs / ((double) this.count), 2) + "ms ") + "total:" + Utility.padString(this.totalTimeMs, 2) + "ms ";
        } else {
            str = "{ #0 = NA";
        }
        return str + "}";
    }

    public void d() {
        if (this.enabled) {
            b();
            e();
        }
    }

    public void e() {
        if (this.enabled && this.count > 0) {
            GameEngine.log(GameEngine.addColorCodes(this.name + " - " + c(), "\u001b[36m"));
            f();
        }
    }

    public void f() {
        this.count = 0;
        this.totalTimeMs = 0.0d;
        this.peakTimeMs = 0.0d;
    }
}
