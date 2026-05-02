package net.rudp;

/* JADX INFO: renamed from: a.a.j */
/* JADX INFO: loaded from: game-lib.jar:a/a/j.class */
class ReliableSocketKeepAliveTask implements Runnable {
    final /* synthetic */ ReliableSocket a;

    ReliableSocketKeepAliveTask(ReliableSocket reliableSocket) {
        this.a = reliableSocket;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.a.sendKeepAlivePacket();
    }
}
