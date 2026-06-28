package net.rudp.impl;

import java.io.IOException;

/* JADX INFO: renamed from: a.a.a.b */
/* JADX INFO: loaded from: game-lib.jar:a/a/a/b.class */
public class DATSegment extends Segment {

    /* JADX INFO: renamed from: a */
    private byte[] payload;

    protected DATSegment() {
    }

    public DATSegment(int i, int i2, byte[] bArr, int i3, int i4) {
        setHeaderFields(64, i, 6);
        setAcknowledgmentNumber(i2);
        this.payload = new byte[i4];
        System.arraycopy(bArr, i3, this.payload, 0, i4);
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: b */
    public int length() {
        return this.payload.length + super.length();
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: a */
    public String getSegmentName() {
        return "DAT";
    }

    /* JADX INFO: renamed from: c */
    public byte[] getPayloadBytes() {
        return this.payload;
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: d */
    public byte[] encodePayload() {
        byte[] bArrEncodePayload = super.encodePayload();
        System.arraycopy(this.payload, 0, bArrEncodePayload, 6, this.payload.length);
        return bArrEncodePayload;
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: a */
    public void deserializeFrom(byte[] bArr, int i, int i2) throws IOException {
        super.deserializeFrom(bArr, i, i2);
        this.payload = new byte[i2 - 6];
        System.arraycopy(bArr, i + 6, this.payload, 0, this.payload.length);
    }
}
