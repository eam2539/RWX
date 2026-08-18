package net.rudp;

import net.rudp.socket.AcceptFilter;
import net.rudp.socket.ReliableSocketListener;
import net.rudp.socket.ReliableSocketStateListener;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: renamed from: a.a.b */
/* JADX INFO: loaded from: game-lib.jar:a/a/b.class */
public class ReliableServerSocket extends ServerSocket {

    /* JADX INFO: renamed from: a */
    AcceptFilter acceptancePolicy;

    /* JADX INFO: renamed from: d */
    DatagramSocket datagramSocket;

    /* JADX INFO: renamed from: e */
    private int acceptTimeoutMs;

    /* JADX INFO: renamed from: f */
    private int acceptQueueSize;

    /* JADX INFO: renamed from: g */
    private boolean closed;

    /* JADX INFO: renamed from: h */
    public ArrayList acceptedConnections;

    /* JADX INFO: renamed from: i */
    HashMap activeConnectionsMap;

    /* JADX INFO: renamed from: j */
    HashMap pendingConnectionsMap;

    /* JADX INFO: renamed from: k */
    HashMap datagramReceiverMap;

    /* JADX INFO: renamed from: b */
    long lastLogTimeMs;

    /* JADX INFO: renamed from: c */
    int logCounter;

    /* JADX INFO: renamed from: l */
    private ReliableSocketListener eventListener;

    public ReliableServerSocket() throws IOException {
        this(new DatagramSocket((SocketAddress) null), 0);
    }

    public ReliableServerSocket(int i, int i2, InetAddress inetAddress, boolean z) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket((SocketAddress) null);
        datagramSocket.setReuseAddress(z);
        datagramSocket.bind(new InetSocketAddress(inetAddress, i));
        initializeServer(datagramSocket, i2);
    }

    public ReliableServerSocket(DatagramSocket datagramSocket, int i) throws IOException {
        initializeServer(datagramSocket, i);
    }

    /* JADX INFO: renamed from: a */
    public void initializeServer(DatagramSocket datagramSocket, int i) {
        if (datagramSocket == null) {
            throw new NullPointerException("sock");
        }
        this.datagramSocket = datagramSocket;
        this.acceptQueueSize = i <= 0 ? 50 : i;
        this.acceptedConnections = new ArrayList(this.acceptQueueSize);
        this.activeConnectionsMap = new HashMap();
        this.pendingConnectionsMap = new HashMap();
        this.datagramReceiverMap = new HashMap();
        this.eventListener = new ReliableSocketStateListener(this);
        this.acceptTimeoutMs = 0;
        this.closed = false;
        new ReliableServerSocketThread(this).start();
    }

    /* JADX INFO: renamed from: a */
    public void setAcceptancePolicy(AcceptFilter acceptFilter) {
        this.acceptancePolicy = acceptFilter;
    }

    @Override // java.net.ServerSocket
    public Socket accept() throws SocketException {
        if (this.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        synchronized (this.acceptedConnections) {
            while (this.acceptedConnections.isEmpty()) {
                try {
                    if (this.acceptTimeoutMs == 0) {
                        this.acceptedConnections.wait();
                    }
                    else {
                        final long currentTimeMillis = System.currentTimeMillis();
                        this.acceptedConnections.wait(this.acceptTimeoutMs);
                        if (System.currentTimeMillis() - currentTimeMillis >= this.acceptTimeoutMs) {
                            throw new SocketTimeoutException();
                        }
                    }
                }
                catch (final InterruptedException | SocketTimeoutException ex) {
                    ex.printStackTrace();
                }
                if (this.isClosed()) {
                    throw new SocketException("Socket is closed");
                }
            }
            return (Socket)this.acceptedConnections.remove(0);
        }
    }

    @Override // java.net.ServerSocket
    public synchronized void bind(SocketAddress socketAddress) throws SocketException {
        bind(socketAddress, 0);
    }

    @Override // java.net.ServerSocket
    public synchronized void bind(SocketAddress socketAddress, int i) throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        this.datagramSocket.setReuseAddress(true);
        this.datagramSocket.bind(socketAddress);
    }

    @Override // java.net.ServerSocket, java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() {
        if (isClosed()) {
            return;
        }
        this.closed = true;
        synchronized (this.acceptedConnections) {
            this.acceptedConnections.clear();
            this.acceptedConnections.notify();
        }
        synchronized (this.activeConnectionsMap) {
            if (this.activeConnectionsMap.isEmpty()) {
                this.datagramSocket.close();
            }
        }
    }

    @Override // java.net.ServerSocket
    public InetAddress getInetAddress() {
        return this.datagramSocket.getInetAddress();
    }

    @Override // java.net.ServerSocket
    public int getLocalPort() {
        return this.datagramSocket.getLocalPort();
    }

    @Override // java.net.ServerSocket
    public SocketAddress getLocalSocketAddress() {
        return this.datagramSocket.getLocalSocketAddress();
    }

    @Override // java.net.ServerSocket
    public boolean isBound() {
        return this.datagramSocket.isBound();
    }

    @Override // java.net.ServerSocket
    public boolean isClosed() {
        return this.closed;
    }

    @Override // java.net.ServerSocket
    public void setSoTimeout(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("timeout < 0");
        }
        this.acceptTimeoutMs = i;
    }

    @Override // java.net.ServerSocket
    public int getSoTimeout() {
        return this.acceptTimeoutMs;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: a */
    public void registerActiveConnection(SocketAddress socketAddress, ReliableSocketConnection reliableSocketConnection) {
        synchronized (this.activeConnectionsMap) {
            reliableSocketConnection.addStateListener(this.eventListener);
            this.activeConnectionsMap.put(socketAddress, reliableSocketConnection);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: a */
    public ReliableSocketConnection unregisterActiveConnection(SocketAddress socketAddress) {
        ReliableSocketConnection reliableSocketConnection;
        synchronized (this.activeConnectionsMap) {
            reliableSocketConnection = (ReliableSocketConnection) this.activeConnectionsMap.remove(socketAddress);
            if (this.activeConnectionsMap.isEmpty() && isClosed()) {
                this.datagramSocket.close();
            }
        }
        return reliableSocketConnection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: a */
    public void logServerMessage(String str) {
        if (this.lastLogTimeMs + 5000 < System.currentTimeMillis()) {
            this.lastLogTimeMs = System.currentTimeMillis();
            this.logCounter = 0;
        }
        if (this.logCounter > 20) {
            return;
        }
        this.logCounter++;
        System.out.println(str);
    }
}
