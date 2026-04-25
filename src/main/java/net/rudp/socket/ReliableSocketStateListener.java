package net.rudp.socket;

import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketConnection;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: renamed from: a.a.f */
/* JADX INFO: loaded from: game-lib.jar:a/a/f.class */
public class ReliableSocketStateListener implements ReliableSocketListener {

    /* JADX INFO: renamed from: a */
    final /* synthetic */ ReliableServerSocket serverSocket;

    public ReliableSocketStateListener(ReliableServerSocket reliableServerSocket) {
        this.serverSocket = reliableServerSocket;
    }

    @Override // net.rudp.socket.ReliableSocketListener
    /* JADX INFO: renamed from: a */
    public void onConnected(ReliableSocket reliableSocket) {
        if (reliableSocket instanceof ReliableSocketConnection) {
            synchronized (this.serverSocket.acceptedConnections) {
                while (this.serverSocket.acceptedConnections.size() > 50) {
                    try {
                        this.serverSocket.acceptedConnections.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.serverSocket.acceptedConnections.add((ReliableSocketConnection) reliableSocket);
                this.serverSocket.acceptedConnections.notify();
            }
        }
    }

    @Override // net.rudp.socket.ReliableSocketListener
    /* JADX INFO: renamed from: b */
    public void onConnectFailed(ReliableSocket reliableSocket) {
    }

    @Override // net.rudp.socket.ReliableSocketListener
    /* JADX INFO: renamed from: c */
    public void onClosed(ReliableSocket reliableSocket) {
        if (reliableSocket instanceof ReliableSocketConnection) {
            this.serverSocket.unregisterActiveConnection(((ReliableSocketConnection) reliableSocket).getPeerAddress());
        }
    }

    @Override // net.rudp.socket.ReliableSocketListener
    /* JADX INFO: renamed from: d */
    public void onConnectionLost(ReliableSocket reliableSocket) {
        if (reliableSocket instanceof ReliableSocketConnection) {
            this.serverSocket.unregisterActiveConnection(((ReliableSocketConnection) reliableSocket).getPeerAddress());
        }
    }

    @Override // net.rudp.socket.ReliableSocketListener
    /* JADX INFO: renamed from: e */
    public void onRemoteReset(ReliableSocket reliableSocket) {
    }
}
