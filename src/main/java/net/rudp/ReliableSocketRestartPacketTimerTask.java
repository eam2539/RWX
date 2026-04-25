package net.rudp;

import java.io.IOException;
import java.util.Iterator;
import net.rudp.impl.Segment;

/* JADX INFO: renamed from: a.a.n */
/* JADX INFO: loaded from: game-lib.jar:a/a/n.class */
class ReliableSocketRestartPacketTimerTask implements Runnable {
    final /* synthetic */ ReliableSocket a;

    ReliableSocketRestartPacketTimerTask(ReliableSocket reliableSocket) {
        this.a = reliableSocket;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this.a.unacknowledgedPackets) {
            Iterator it = this.a.unacknowledgedPackets.iterator();
            while (it.hasNext()) {
                this.a.startPacketTimers((Segment) it.next());
            }
        }
    }
}
