package com.corrodinggames.rts.debug.test;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/j.class */
public class VirtualTestChild extends VirtualTest {

    /* JADX INFO: renamed from: c */
    int value;
    final /* synthetic */ PerformanceTests d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public VirtualTestChild(PerformanceTests performanceTests) {
        super(performanceTests);
        this.d = performanceTests;
    }

    @Override // com.corrodinggames.rts.debug.test.VirtualTest
    /* JADX INFO: renamed from: a */
    public int getValue() {
        return this.value;
    }
}
