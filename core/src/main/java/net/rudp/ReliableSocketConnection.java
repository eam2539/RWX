package net.rudp;

import net.rudp.impl.Segment;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: renamed from: a.a.e */
/* JADX INFO: loaded from: game-lib.jar:a/a/e.class */
public class ReliableSocketConnection extends ReliableSocket {

    /* JADX INFO: renamed from: a */
    boolean isInitialized;

    /* JADX INFO: renamed from: i */
    private ArrayList incomingPacketQueue;

    /* JADX INFO: renamed from: b */
    final /* synthetic */ ReliableServerSocket serverSocket;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ReliableSocketConnection(ReliableServerSocket reliableServerSocket, DatagramSocket datagramSocket, SocketAddress socketAddress) {
        super(datagramSocket);
        this.serverSocket = reliableServerSocket;
        this.peerAddress = socketAddress;
    }

    @Override // net.rudp.ReliableSocket
    /* JADX INFO: renamed from: a */
    protected void init(DatagramSocket datagramSocket, ReliableSocketProfile reliableSocketProfile) {
        this.incomingPacketQueue = new ArrayList();
        this.datagramSocket = datagramSocket;
        this.socketProfile = reliableSocketProfile;
    }

    @Override // net.rudp.ReliableSocket
    /* JADX INFO: renamed from: a */
    protected Segment receiveSegment() {
        Segment segment;
        synchronized (this.incomingPacketQueue) {
            while (this.incomingPacketQueue.isEmpty()) {
                try {
                    this.incomingPacketQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            segment = (Segment) this.incomingPacketQueue.remove(0);
        }
        return segment;
    }

    /* JADX INFO: renamed from: a */
    protected void enqueueIncomingPacket(Segment segment) {
        synchronized (this.incomingPacketQueue) {
            if (!this.isInitialized) {
                this.isInitialized = true;
                super.init(this.datagramSocket, this.socketProfile);
            }
            this.incomingPacketQueue.add(segment);
            this.incomingPacketQueue.notify();
        }
    }

    @Override // net.rudp.ReliableSocket
    /* JADX INFO: renamed from: b */
    protected void mo1b() {
        synchronized (this.incomingPacketQueue) {
            this.incomingPacketQueue.clear();
            this.incomingPacketQueue.add(null);
            this.incomingPacketQueue.notify();
        }
    }

    @Override // net.rudp.ReliableSocket
    protected void a(String str) {
        System.out.println(getPort() + ": " + str);
    }
}
