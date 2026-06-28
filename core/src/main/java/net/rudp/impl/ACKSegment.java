package net.rudp.impl;

/* JADX INFO: renamed from: a.a.a.a */
/* JADX INFO: loaded from: game-lib.jar:a/a/a/a.class */
public class ACKSegment extends Segment {
    protected ACKSegment() {
    }

    public ACKSegment(int seq, int i) {
        setHeaderFields(64, seq, 6);
        setAcknowledgmentNumber(i);
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: a */
    public String getSegmentName() {
        return "ACK";
    }
}
