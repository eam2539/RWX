package com.corrodinggames.rts.debug.test;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/g.class */
public final class TestData {

    /* JADX INFO: renamed from: a */
    int valueA;

    /* JADX INFO: renamed from: b */
    int valueB;

    /* JADX INFO: renamed from: c */
    boolean useValueB;
    final /* synthetic */ PerformanceTests d;

    public TestData(PerformanceTests performanceTests) {
        this.d = performanceTests;
    }

    /* JADX INFO: renamed from: a */
    public final int getValue() {
        return this.useValueB ? this.valueB : this.valueA;
    }
}
