package net.rudp.impl;

/* JADX INFO: renamed from: a.a.a.e */
/* JADX INFO: loaded from: game-lib.jar:a/a/a/e.class */
public class NULSegment extends Segment {
    protected NULSegment() {
    }

    public NULSegment(int i) {
        setHeaderFields(8, i, 6);
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: a */
    public String getSegmentName() {
        return "NUL";
    }
}
