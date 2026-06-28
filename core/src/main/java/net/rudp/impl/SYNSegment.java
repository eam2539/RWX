package net.rudp.impl;

import java.io.IOException;

/* JADX INFO: renamed from: a.a.a.g */
/* JADX INFO: loaded from: game-lib.jar:a/a/a/g.class */
public class SYNSegment extends Segment {

    /* JADX INFO: renamed from: a */
    private int rudpVersion;

    /* JADX INFO: renamed from: b */
    private int gameVersion;

    /* JADX INFO: renamed from: c */
    private int optionFlags;

    /* JADX INFO: renamed from: d */
    private int maxSegmentSize;

    /* JADX INFO: renamed from: e */
    private int retransmissionTimeoutMs;

    /* JADX INFO: renamed from: f */
    private int cumulativeAckTimeoutMs;

    /* JADX INFO: renamed from: g */
    private int nullSegmentTimeoutMs;

    /* JADX INFO: renamed from: h */
    private int maxRetransmissions;

    /* JADX INFO: renamed from: i */
    private int maxCumulativeAcks;

    /* JADX INFO: renamed from: j */
    private int maxOutOfOrderSegments;

    /* JADX INFO: renamed from: k */
    private int maxAutoResets;

    protected SYNSegment() {
    }

    public SYNSegment(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
        setHeaderFields(-128, i, 22);
        this.rudpVersion = 1;
        this.gameVersion = i2;
        this.optionFlags = 1;
        this.maxSegmentSize = i3;
        this.retransmissionTimeoutMs = i4;
        this.cumulativeAckTimeoutMs = i5;
        this.nullSegmentTimeoutMs = i6;
        this.maxRetransmissions = i7;
        this.maxCumulativeAcks = i8;
        this.maxOutOfOrderSegments = i9;
        this.maxAutoResets = i10;
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: a */
    public String getSegmentName() {
        return "SYN";
    }

    /* JADX INFO: renamed from: c */
    public int getGameVersion() {
        return this.gameVersion;
    }

    /* JADX INFO: renamed from: e */
    public int getMaxSegmentSize() {
        return this.maxSegmentSize;
    }

    /* JADX INFO: renamed from: f */
    public int getRetransmissionTimeoutMs() {
        return this.retransmissionTimeoutMs;
    }

    /* JADX INFO: renamed from: g */
    public int getCumulativeAckTimeoutMs() {
        return this.cumulativeAckTimeoutMs;
    }

    /* JADX INFO: renamed from: h */
    public int getNullPacketTimeoutMs() {
        return this.nullSegmentTimeoutMs;
    }

    /* JADX INFO: renamed from: i */
    public int getMaxRetransmissions() {
        return this.maxRetransmissions;
    }

    /* JADX INFO: renamed from: j */
    public int getMaxCumulativeAcks() {
        return this.maxCumulativeAcks;
    }

    /* JADX INFO: renamed from: k */
    public int getMaxOutOfOrderPackets() {
        return this.maxOutOfOrderSegments;
    }

    /* JADX INFO: renamed from: l */
    public int getMaxAutoResets() {
        return this.maxAutoResets;
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: d */
    public byte[] encodePayload() {
        byte[] bArrEncodePayload = super.encodePayload();
        bArrEncodePayload[4] = (byte) ((this.rudpVersion << 4) & 255);
        bArrEncodePayload[5] = (byte) (this.gameVersion & 255);
        bArrEncodePayload[6] = (byte) (this.optionFlags & 255);
        bArrEncodePayload[7] = 0;
        bArrEncodePayload[8] = (byte) ((this.maxSegmentSize >>> 8) & 255);
        bArrEncodePayload[9] = (byte) ((this.maxSegmentSize >>> 0) & 255);
        bArrEncodePayload[10] = (byte) ((this.retransmissionTimeoutMs >>> 8) & 255);
        bArrEncodePayload[11] = (byte) ((this.retransmissionTimeoutMs >>> 0) & 255);
        bArrEncodePayload[12] = (byte) ((this.cumulativeAckTimeoutMs >>> 8) & 255);
        bArrEncodePayload[13] = (byte) ((this.cumulativeAckTimeoutMs >>> 0) & 255);
        bArrEncodePayload[14] = (byte) ((this.nullSegmentTimeoutMs >>> 8) & 255);
        bArrEncodePayload[15] = (byte) ((this.nullSegmentTimeoutMs >>> 0) & 255);
        bArrEncodePayload[16] = (byte) (this.maxRetransmissions & 255);
        bArrEncodePayload[17] = (byte) (this.maxCumulativeAcks & 255);
        bArrEncodePayload[18] = (byte) (this.maxOutOfOrderSegments & 255);
        bArrEncodePayload[19] = (byte) (this.maxAutoResets & 255);
        return bArrEncodePayload;
    }

    @Override // net.rudp.impl.Segment
    /* JADX INFO: renamed from: a */
    protected void deserializeFrom(byte[] bArr, int i, int i2) throws IOException {
        super.deserializeFrom(bArr, i, i2);
        if (i2 < 22) {
            throw new IOException("Invalid SYN segment");
        }
        this.rudpVersion = (bArr[i + 4] & 255) >>> 4;
        if (this.rudpVersion != 1) {
            throw new IOException("Invalid RUDP version:" + this.rudpVersion);
        }
        this.gameVersion = bArr[i + 5] & 255;
        this.optionFlags = bArr[i + 6] & 255;
        this.maxSegmentSize = ((bArr[i + 8] & 255) << 8) | ((bArr[i + 9] & 255) << 0);
        this.retransmissionTimeoutMs = ((bArr[i + 10] & 255) << 8) | ((bArr[i + 11] & 255) << 0);
        this.cumulativeAckTimeoutMs = ((bArr[i + 12] & 255) << 8) | ((bArr[i + 13] & 255) << 0);
        this.nullSegmentTimeoutMs = ((bArr[i + 14] & 255) << 8) | ((bArr[i + 15] & 255) << 0);
        this.maxRetransmissions = bArr[i + 16] & 255;
        this.maxCumulativeAcks = bArr[i + 17] & 255;
        this.maxOutOfOrderSegments = bArr[i + 18] & 255;
        this.maxAutoResets = bArr[i + 19] & 255;
    }
}
