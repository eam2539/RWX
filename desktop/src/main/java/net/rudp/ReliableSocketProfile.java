package net.rudp;

import java.io.IOException;

/* JADX INFO: renamed from: a.a.r */
/* JADX INFO: loaded from: game-lib.jar:a/a/r.class */
public class ReliableSocketProfile {

    /* JADX INFO: renamed from: a */
    public static final ReliableSocketProfile DEFAULT_PROFILE = new ReliableSocketProfile();

    /* JADX INFO: renamed from: b */
    private int maxSendQueueSize;

    /* JADX INFO: renamed from: c */
    private int maxRecvQueueSize;

    /* JADX INFO: renamed from: d */
    private int maxSegmentSize;

    /* JADX INFO: renamed from: e */
    private int gameVersion;

    /* JADX INFO: renamed from: f */
    private int maxRetransmissions;

    /* JADX INFO: renamed from: g */
    private int maxCumulativeAcks;

    /* JADX INFO: renamed from: h */
    private int maxOutOfOrder;

    /* JADX INFO: renamed from: i */
    private int maxAutoResets;

    /* JADX INFO: renamed from: j */
    private int nullSegmentTimeoutMs;

    /* JADX INFO: renamed from: k */
    private int retransmissionTimeoutMs;

    /* JADX INFO: renamed from: l */
    private int cumulativeAckTimeoutMs;

    public ReliableSocketProfile() {
        try {
            initialize(32, 32, 300, 70, 0, 3, 3, 3, 2000, 600, 300);
        } catch (IOException e) {
            throw new RuntimeException("IOException on ReliableSocketProfile default:" + e);
        }
    }

    public ReliableSocketProfile(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11) throws IOException {
        initialize(i, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11);
    }

    /* JADX INFO: renamed from: a */
    private void initialize(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11) throws IOException {
        checkRange("maxSendQueueSize", i, 1, 255);
        checkRange("maxRecvQueueSize", i2, 1, 255);
        checkRange("maxSegmentSize", i3, 22, 6535);
        checkRange("maxOutstandingSegs", i4, 1, 255);
        checkRange("maxRetrans", i5, 0, 255);
        checkRange("maxCumulativeAcks", i6, 0, 255);
        checkRange("maxOutOfSequence", i7, 0, 255);
        checkRange("maxAutoReset", i8, 0, 255);
        checkRange("nullSegmentTimeout", i9, 0, 65535);
        checkRange("retransmissionTimeout", i10, 100, 65535);
        checkRange("cumulativeAckTimeout", i11, 100, 65535);
        this.maxSendQueueSize = i;
        this.maxRecvQueueSize = i2;
        this.maxSegmentSize = i3;
        this.gameVersion = i4;
        this.maxRetransmissions = i5;
        this.maxCumulativeAcks = i6;
        this.maxOutOfOrder = i7;
        this.maxAutoResets = i8;
        this.nullSegmentTimeoutMs = i9;
        this.retransmissionTimeoutMs = i10;
        this.cumulativeAckTimeoutMs = i11;
    }

    /* JADX INFO: renamed from: a */
    public int getMaxSegmentSize() {
        return this.maxSegmentSize;
    }

    /* JADX INFO: renamed from: b */
    public int getMaxOutstandingSegments() {
        return this.gameVersion;
    }

    /* JADX INFO: renamed from: c */
    public int getMaxRetransmissions() {
        return this.maxRetransmissions;
    }

    /* JADX INFO: renamed from: d */
    public int getMaxCumulativeAcks() {
        return this.maxCumulativeAcks;
    }

    /* JADX INFO: renamed from: e */
    public int getMaxOutOfOrder() {
        return this.maxOutOfOrder;
    }

    /* JADX INFO: renamed from: f */
    public int getMaxAutoResets() {
        return this.maxAutoResets;
    }

    /* JADX INFO: renamed from: g */
    public int getNullSegmentTimeoutMs() {
        return this.nullSegmentTimeoutMs;
    }

    /* JADX INFO: renamed from: h */
    public int getRetransmissionTimeoutMs() {
        return this.retransmissionTimeoutMs;
    }

    /* JADX INFO: renamed from: i */
    public int getCumulativeAckTimeoutMs() {
        return this.cumulativeAckTimeoutMs;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(this.maxSendQueueSize).append(", ");
        sb.append(this.maxRecvQueueSize).append(", ");
        sb.append(this.maxSegmentSize).append(", ");
        sb.append(this.gameVersion).append(", ");
        sb.append(this.maxRetransmissions).append(", ");
        sb.append(this.maxCumulativeAcks).append(", ");
        sb.append(this.maxOutOfOrder).append(", ");
        sb.append(this.maxAutoResets).append(", ");
        sb.append(this.nullSegmentTimeoutMs).append(", ");
        sb.append(this.retransmissionTimeoutMs).append(", ");
        sb.append(this.cumulativeAckTimeoutMs);
        sb.append("]");
        return sb.toString();
    }

    /* JADX INFO: renamed from: a */
    private void checkRange(String str, int i, int i2, int i3) throws IOException {
        if (i < i2 || i > i3) {
            throw new IOException(str + " out of range");
        }
    }
}
