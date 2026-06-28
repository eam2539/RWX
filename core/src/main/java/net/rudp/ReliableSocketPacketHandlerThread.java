package net.rudp;

import net.rudp.impl.ACKSegment;
import net.rudp.impl.EAKSegment;
import net.rudp.impl.SYNSegment;
import net.rudp.impl.Segment;

import java.io.IOException;

/* JADX INFO: renamed from: a.a.m */
/* JADX INFO: loaded from: game-lib.jar:a/a/m.class */
class ReliableSocketPacketHandlerThread extends Thread {
    final /* synthetic */ ReliableSocket a;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ReliableSocketPacketHandlerThread(ReliableSocket reliableSocket) {
        super("ReliableSocket");
        this.a = reliableSocket;
        setDaemon(true);
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (true) {
            try {
                Segment segmentDequeueIncomingPacketAndLog = this.a.dequeueIncomingPacketAndLog();
                if (segmentDequeueIncomingPacketAndLog != null) {
                    if (segmentDequeueIncomingPacketAndLog instanceof SYNSegment) {
                        this.a.handlePacket((SYNSegment) segmentDequeueIncomingPacketAndLog);
                    } else if (segmentDequeueIncomingPacketAndLog instanceof EAKSegment) {
                        this.a.log((EAKSegment) segmentDequeueIncomingPacketAndLog);
                    } else if (!(segmentDequeueIncomingPacketAndLog instanceof ACKSegment)) {
                        this.a.stopPacketTimers(segmentDequeueIncomingPacketAndLog);
                    }
                    this.a.processPacket(segmentDequeueIncomingPacketAndLog);
                } else {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
