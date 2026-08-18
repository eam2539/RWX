package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bt */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bt.class */
public class ProfilerTimer {
    boolean a;
    int b;
    double c;
    double d;
    long e;
    String f;

    public ProfilerTimer(String str) {
        this.a = true;
        this.f = str;
    }

    public ProfilerTimer(String str, boolean z) {
        this.a = true;
        this.f = str;
        this.a = z;
    }

    public void a() {
        if (this.a) {
            if (this.e != 0) {
                this.e = Long.MIN_VALUE;
            } else {
                this.e = PerformanceProfiler.a();
            }
        }
    }

    public void b() {
        if (this.a) {
            double dA = PerformanceProfiler.a(this.e, PerformanceProfiler.a());
            this.c += dA;
            this.b++;
            if (dA > this.d) {
                this.d = dA;
            }
            this.e = 0L;
        }
    }

    public String c() {
        String str;
        if (!this.a) {
            return "{ Not enabled }";
        }
        if (this.b > 0) {
            str = ((("{ #" + this.b + " = ") + "peak:" + Utility.padString(this.d, 2) + "ms ") + "avg:" + Utility.padString(this.c / ((double) this.b), 2) + "ms ") + "total:" + Utility.padString(this.c, 2) + "ms ";
        } else {
            str = "{ #0 = NA";
        }
        return str + "}";
    }

    public void d() {
        if (this.a) {
            b();
            e();
        }
    }

    public void e() {
        if (this.a && this.b > 0) {
            GameEngine.log(GameEngine.addColorCodes(this.f + " - " + c(), "\u001b[36m"));
            f();
        }
    }

    public void f() {
        this.b = 0;
        this.c = 0.0d;
        this.d = 0.0d;
    }
}
