package net.rudp.impl;

import java.io.IOException;

/* JADX INFO: renamed from: a.a.a.c */
/* JADX INFO: loaded from: game-lib.jar:a/a/a/c.class */
public class EAKSegment extends ACKSegment {

    /* JADX INFO: renamed from: a */
    private int[] selectiveAckSequenceList;

    protected EAKSegment() {
    }

    public EAKSegment(int i, int i2, int[] iArr) {
        setHeaderFields(32, i, 6 + iArr.length);
        setAcknowledgmentNumber(i2);
        this.selectiveAckSequenceList = iArr;
    }

    @Override // net.rudp.impl.ACKSegment, net.rudp.impl.Segment
    /* JADX INFO: renamed from: a */
    public String getSegmentName() {
        return "EAK";
    }

    /* JADX INFO: renamed from: c */
    public int[] getSelectiveAckList() {
        return this.selectiveAckSequenceList;
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: d */
    public byte[] encodePayload() {
        byte[] bArrD = super.encodePayload();
        for (int i = 0; i < this.selectiveAckSequenceList.length; i++) {
            bArrD[4 + i] = (byte) (this.selectiveAckSequenceList[i] & 255);
        }
        return bArrD;
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: a */
    protected void deserializeFrom(byte[] bArr, int i, int i2) throws IOException {
        super.deserializeFrom(bArr, i, i2);
        this.selectiveAckSequenceList = new int[i2 - 6];
        for (int i3 = 0; i3 < this.selectiveAckSequenceList.length; i3++) {
            this.selectiveAckSequenceList[i3] = bArr[i + 4 + i3] & 255;
        }
    }
}
