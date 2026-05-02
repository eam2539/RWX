package net.rudp.impl;

/* JADX INFO: renamed from: a.a.a.d */
/* JADX INFO: loaded from: game-lib.jar:a/a/a/d.class */
public class FINSegment extends Segment {
    protected FINSegment() {
    }

    public FINSegment(int i) {
        setHeaderFields(2, i, 6);
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: a */
    public String getSegmentName() {
        return "FIN";
    }
}
