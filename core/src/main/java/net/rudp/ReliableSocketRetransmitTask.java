package net.rudp;

/* JADX INFO: renamed from: a.a.k */
/* JADX INFO: loaded from: game-lib.jar:a/a/k.class */
class ReliableSocketRetransmitTask implements Runnable {
    final /* synthetic */ ReliableSocket a;

    ReliableSocketRetransmitTask(ReliableSocket reliableSocket) {
        this.a = reliableSocket;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.a.retransmitPending();
    }
}
