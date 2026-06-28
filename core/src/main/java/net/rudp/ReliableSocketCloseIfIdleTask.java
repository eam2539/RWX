package net.rudp;

import net.rudp.impl.NULSegment;

import java.io.IOException;

/* JADX INFO: renamed from: a.a.l */
/* JADX INFO: loaded from: game-lib.jar:a/a/l.class */
class ReliableSocketCloseIfIdleTask implements Runnable {

    /* JADX INFO: renamed from: a */
    final /* synthetic */ ReliableSocket socket;

    ReliableSocketCloseIfIdleTask(ReliableSocket reliableSocket) {
        this.socket = reliableSocket;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this.socket.unacknowledgedPackets) {
            if (this.socket.unacknowledgedPackets.isEmpty()) {
                try {
                    this.socket.closeImpl(new NULSegment(this.socket.connectionStats.nextSequenceToSendAndIncrement()));
                } catch (IOException e) {
                    if (ReliableSocket.DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
