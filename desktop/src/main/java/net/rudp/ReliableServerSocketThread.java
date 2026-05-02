package net.rudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import net.rudp.impl.ACKSegment;
import net.rudp.impl.SYNSegment;
import net.rudp.impl.Segment;
import net.rudp.socket.AcceptFilter;
import net.rudp.socket.RawSocket;

/* JADX INFO: renamed from: a.a.d */
/* JADX INFO: loaded from: game-lib.jar:a/a/d.class */
class ReliableServerSocketThread extends Thread {

    /* JADX INFO: renamed from: a */
    final /* synthetic */ ReliableServerSocket serverSocket;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ReliableServerSocketThread(ReliableServerSocket reliableServerSocket) {
        super("ReliableServerSocket");
        this.serverSocket = reliableServerSocket;
        setDaemon(true);
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        ReliableSocketConnection reliableSocketConnection;
        PendingConnection pendingConnection;
        AcceptFilter acceptFilter;
        byte[] bArr = new byte[65535];
        while (true) {
            DatagramPacket datagramPacket = new DatagramPacket(bArr, bArr.length);
            SocketAddress socketAddress = null;
            try {
                try {
                    this.serverSocket.datagramSocket.receive(datagramPacket);
                    socketAddress = datagramPacket.getSocketAddress();
                    synchronized (this.serverSocket.activeConnectionsMap) {
                        RawSocket rawSocket = (RawSocket) this.serverSocket.datagramReceiverMap.get(socketAddress);
                        if (rawSocket == null) {
                            synchronized (this.serverSocket.activeConnectionsMap) {
                                reliableSocketConnection = (ReliableSocketConnection) this.serverSocket.activeConnectionsMap.get(socketAddress);
                            }
                            if (reliableSocketConnection != null || (acceptFilter = this.serverSocket.acceptancePolicy) == null || acceptFilter.shouldAcceptPeer(socketAddress)) {
                                Segment segmentDecodeHeader = Segment.decodeHeader(datagramPacket.getData(), 0, datagramPacket.getLength());
                                if (!this.serverSocket.isClosed() && reliableSocketConnection == null) {
                                    if (segmentDecodeHeader instanceof SYNSegment) {
                                        long jCurrentTimeMillis = System.currentTimeMillis();
                                        if (this.serverSocket.pendingConnectionsMap.size() > 0) {
                                            int i = 10000;
                                            if (this.serverSocket.pendingConnectionsMap.size() > 20) {
                                                i = 5000;
                                            }
                                            if (this.serverSocket.pendingConnectionsMap.size() > 200) {
                                                i = 3000;
                                            }
                                            Iterator it = this.serverSocket.pendingConnectionsMap.entrySet().iterator();
                                            while (it.hasNext()) {
                                                if (((PendingConnection) ((Map.Entry) it.next()).getValue()).createdAtMs + ((long) i) < jCurrentTimeMillis) {
                                                    it.remove();
                                                }
                                            }
                                        }
                                        PendingConnection pendingConnection2 = (PendingConnection) this.serverSocket.pendingConnectionsMap.get(socketAddress);
                                        if (pendingConnection2 != null) {
                                            pendingConnection2.reliableSocketConnection.handlePacket((SYNSegment) segmentDecodeHeader);
                                        } else {
                                            PendingConnection pendingConnection3 = new PendingConnection();
                                            pendingConnection3.createdAtMs = jCurrentTimeMillis;
                                            pendingConnection3.reliableSocketConnection = new ReliableSocketConnection(this.serverSocket, this.serverSocket.datagramSocket, socketAddress);
                                            pendingConnection3.reliableSocketConnection.handlePacket((SYNSegment) segmentDecodeHeader);
                                            this.serverSocket.pendingConnectionsMap.put(socketAddress, pendingConnection3);
                                        }
                                    }
                                    if ((segmentDecodeHeader instanceof ACKSegment) && (pendingConnection = (PendingConnection) this.serverSocket.pendingConnectionsMap.get(socketAddress)) != null) {
                                        ReliableSocketConnection reliableSocketConnection2 = pendingConnection.reliableSocketConnection;
                                        if (!reliableSocketConnection2.isValidAcknowledgment(segmentDecodeHeader)) {
                                            this.serverSocket.logServerMessage("lightweight ack failed ack:" + segmentDecodeHeader.getAcknowledgmentNumber());
                                        } else {
                                            this.serverSocket.registerActiveConnection(socketAddress, reliableSocketConnection2);
                                            reliableSocketConnection = reliableSocketConnection2;
                                            this.serverSocket.pendingConnectionsMap.remove(socketAddress);
                                        }
                                    }
                                }
                                if (reliableSocketConnection != null) {
                                    reliableSocketConnection.enqueueIncomingPacket(segmentDecodeHeader);
                                }
                            }
                        } else {
                            rawSocket.onBytesReceived(datagramPacket.getData(), datagramPacket.getLength());
                        }
                    }
                } catch (IOException e) {
                    this.serverSocket.logServerMessage("IOException receiving packet:" + e.getMessage() + " isConnected:" + this.serverSocket.datagramSocket.isConnected());
                    if (!this.serverSocket.datagramSocket.isConnected()) {
                        this.serverSocket.close();
                    }
                    throw new IOException(e);
                }
            } catch (IOException e2) {
                if (!this.serverSocket.isClosed()) {
                    this.serverSocket.logServerMessage("IOException client " + socketAddress + " - " + e2.getMessage());
                } else {
                    return;
                }
            } catch (IllegalArgumentException e3) {
                if (!this.serverSocket.isClosed()) {
                    this.serverSocket.logServerMessage("IllegalArgumentException " + socketAddress + " - " + e3.getMessage());
                } else {
                    return;
                }
            }
        }
    }
}
