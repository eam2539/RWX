package net.rudp;

/* JADX INFO: renamed from: a.a.i */
/* JADX INFO: loaded from: game-lib.jar:a/a/i.class */
class ConnectionStats {

    /* JADX INFO: renamed from: a */
    private int nextSequenceToSend;

    /* JADX INFO: renamed from: b */
    private int lastSequenceReceived;

    /* JADX INFO: renamed from: c */
    private int sentPacketCount;

    /* JADX INFO: renamed from: d */
    private int receivedPacketCount;

    /* JADX INFO: renamed from: e */
    private int droppedPacketCount;

    /* JADX INFO: renamed from: a */
    public synchronized int nextSequenceToSendAndIncrement() {
        int iIncrementSequenceModulo255 = ReliableSocket.incrementSequenceModulo255(this.nextSequenceToSend);
        this.nextSequenceToSend = iIncrementSequenceModulo255;
        return iIncrementSequenceModulo255;
    }

    /* JADX INFO: renamed from: a */
    public synchronized int setNextSequenceToSend(int i) {
        this.nextSequenceToSend = i;
        return this.nextSequenceToSend;
    }

    /* JADX INFO: renamed from: b */
    public synchronized int setLastSequenceReceived(int i) {
        this.lastSequenceReceived = i;
        return this.lastSequenceReceived;
    }

    /* JADX INFO: renamed from: b */
    public synchronized int getLastSequenceReceived() {
        return this.lastSequenceReceived;
    }

    /* JADX INFO: renamed from: c */
    public synchronized void incrementSentPackets() {
        this.sentPacketCount++;
    }

    /* JADX INFO: renamed from: d */
    public synchronized int getSentPacketCount() {
        return this.sentPacketCount;
    }

    /* JADX INFO: renamed from: e */
    public synchronized int drainSentPacketCount() {
        int i = this.sentPacketCount;
        this.sentPacketCount = 0;
        return i;
    }

    /* JADX INFO: renamed from: f */
    public synchronized void incrementReceivedPackets() {
        this.receivedPacketCount++;
    }

    /* JADX INFO: renamed from: g */
    public synchronized int getReceivedPacketCount() {
        return this.receivedPacketCount;
    }

    /* JADX INFO: renamed from: h */
    public synchronized int drainReceivedPacketCount() {
        int i = this.receivedPacketCount;
        this.receivedPacketCount = 0;
        return i;
    }

    /* JADX INFO: renamed from: i */
    public synchronized void incrementDroppedPackets() {
        this.droppedPacketCount++;
    }

    /* JADX INFO: renamed from: j */
    public synchronized int getDroppedPacketCount() {
        return this.droppedPacketCount;
    }

    /* JADX INFO: renamed from: k */
    public synchronized int drainDroppedPacketCount() {
        int i = this.droppedPacketCount;
        this.droppedPacketCount = 0;
        return i;
    }

    /* JADX INFO: renamed from: l */
    public synchronized void reset() {
        this.receivedPacketCount = 0;
        this.droppedPacketCount = 0;
        this.sentPacketCount = 0;
    }
}
