package net.rudp.impl;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import java.io.IOException;

/* JADX INFO: renamed from: a.a.a.h */
/* JADX INFO: loaded from: game-lib.jar:a/a/a/h.class */
public abstract class Segment {

    /* JADX INFO: renamed from: a */
    private int controlFlags;

    /* JADX INFO: renamed from: b */
    private int length;

    /* JADX INFO: renamed from: c */
    private int sequenceNumber;

    /* JADX INFO: renamed from: e */
    private int channel = 0;

    /* JADX INFO: renamed from: d */
    private int acknowledgmentNumber = -1;

    /* JADX INFO: renamed from: a */
    public abstract String getSegmentName();

    protected Segment() {
    }

    /* JADX INFO: renamed from: m */
    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    /* JADX INFO: renamed from: b */
    public int length() {
        return this.length;
    }

    /* JADX INFO: renamed from: a */
    public void setAcknowledgmentNumber(int i) {
        this.controlFlags |= 64;
        this.acknowledgmentNumber = i;
    }

    /* JADX INFO: renamed from: n */
    public int getAcknowledgmentNumber() {
        if ((this.controlFlags & 64) == 64) {
            return this.acknowledgmentNumber;
        }
        return -1;
    }

    /* JADX INFO: renamed from: o */
    public int getChannel() {
        return this.channel;
    }

    /* JADX INFO: renamed from: b */
    public void setChannel(int i) {
        this.channel = i;
    }

    /* JADX INFO: renamed from: d */
    public byte[] encodePayload() {
        byte[] bArr = new byte[length()];
        bArr[0] = (byte) (this.controlFlags & 255);
        bArr[1] = (byte) (this.length & 255);
        bArr[2] = (byte) (this.sequenceNumber & 255);
        bArr[3] = (byte) (this.acknowledgmentNumber & 255);
        return bArr;
    }

    public String toString() {
        return getSegmentName() + " [ SEQ = " + getSequenceNumber() + ", ACK = " + (getAcknowledgmentNumber() >= 0 ? VariableScope.nullOrMissingString + getAcknowledgmentNumber() : "N/A") + ", LEN = " + length() + " ]";
    }

    /* JADX INFO: renamed from: b */
    public static Segment decodeHeader(byte[] bArr, int off, int len) throws IOException {
        Segment dATSegment = null;
        if (len < 6) {
            throw new IOException("Invalid segment:" + len);
        }
        byte b = bArr[off];
        if ((b & (-128)) != 0) {
            dATSegment = new SYNSegment();
        } else if ((b & 8) != 0) {
            dATSegment = new NULSegment();
        } else if ((b & 32) != 0) {
            dATSegment = new EAKSegment();
        } else if ((b & 16) != 0) {
            dATSegment = new RSTSegment();
        } else if ((b & 2) != 0) {
            dATSegment = new FINSegment();
        } else if ((b & 64) != 0) {
            if (len == 6) {
                dATSegment = new ACKSegment();
            } else {
                dATSegment = new DATSegment();
            }
        }
        if (dATSegment == null) {
            throw new IOException("Invalid segment");
        }
        dATSegment.deserializeFrom(bArr, off, len);
        return dATSegment;
    }

    /* JADX INFO: renamed from: a */
    protected void setHeaderFields(int flags, int seq, int len) {
        this.controlFlags = flags;
        this.sequenceNumber = seq;
        this.length = len;
    }

    /* JADX INFO: renamed from: a */
    protected void deserializeFrom(byte[] bArr, int off, int len) throws IOException {
        this.controlFlags = bArr[off] & 255;
        this.length = bArr[off + 1] & 255;
        this.sequenceNumber = bArr[off + 2] & 255;
        this.acknowledgmentNumber = bArr[off + 3] & 255;
    }
}
