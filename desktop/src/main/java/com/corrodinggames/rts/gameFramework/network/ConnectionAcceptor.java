package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;
import net.rudp.ReliableServerSocket;
import net.rudp.socket.AcceptFilter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.ao */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/ao.class */
public class ConnectionAcceptor implements Runnable {
    public static boolean b = true;

    /* JADX INFO: renamed from: r */
    private final NetworkEngine networkEngine;

    /* JADX INFO: renamed from: d */
    ServerSocket serverSocket;

    /* JADX INFO: renamed from: e */
    int port;

    /* JADX INFO: renamed from: f */
    boolean isUdp;
    boolean o;
    boolean p;
    boolean q;
    public final boolean a = false;

    /* JADX INFO: renamed from: c */
    volatile boolean isRunning = true;

    /* JADX INFO: renamed from: g */
    long lastAcceptTime = -1;
    final boolean h = false;
    final boolean i = true;

    /* JADX INFO: renamed from: j */
    final Object acceptLock = new Object();

    /* JADX INFO: renamed from: k */
    ArrayList<ConnectionAttemptTracker> pendingConnections = new ArrayList();
    final Object l = new Object();

    /* JADX INFO: renamed from: m */
    int acceptedCount = 0;

    /* JADX INFO: renamed from: n */
    int pendingLimit = 0;

    ConnectionAcceptor(NetworkEngine networkEngine) {
        this.networkEngine = networkEngine;
    }

    /* JADX INFO: renamed from: a */
    public boolean isIpAllowed(InetAddress inetAddress, boolean z) {
        if (inetAddress == null) {
            GameEngine.log("isIpAllowed: inetAddress==null");
            return true;
        }
        if (!b) {
            return true;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis > this.lastAcceptTime + 60000) {
            this.lastAcceptTime = jCurrentTimeMillis;
            synchronized (this.l) {
                this.pendingConnections.clear();
            }
            this.acceptedCount = 0;
            this.pendingLimit = 0;
            this.o = false;
            this.p = false;
            this.q = false;
        }
        synchronized (this.l) {
            boolean z2 = false;
            Iterator it = this.pendingConnections.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                ConnectionAttemptTracker connectionAttemptTracker = (ConnectionAttemptTracker) it.next();
                if (inetAddress.equals(connectionAttemptTracker.address)) {
                    connectionAttemptTracker.attemptCount++;
                    int i = 30;
                    if (this.pendingLimit > 100) {
                        i = 10;
                    }
                    if (this.pendingLimit > 250) {
                        i = 5;
                    }
                    if (connectionAttemptTracker.attemptCount > i) {
                        if (!connectionAttemptTracker.dosWarningLogged) {
                            connectionAttemptTracker.dosWarningLogged = true;
                            GameEngine.log("DOS: Too many attempts:" + connectionAttemptTracker.attemptCount + " ip:" + inetAddress.toString());
                        }
                        if (connectionAttemptTracker.attemptCount > 300 && !connectionAttemptTracker.dosExcessiveLogged) {
                            connectionAttemptTracker.dosExcessiveLogged = true;
                            GameEngine.log("DOS: Excessive attempts:" + connectionAttemptTracker.attemptCount + " ip:" + inetAddress.toString());
                        }
                        return false;
                    }
                    z2 = true;
                }
            }
            if (!z2) {
                if (z) {
                    this.acceptedCount++;
                }
                if (this.pendingConnections.size() > 200) {
                    ConnectionAttemptTracker connectionAttemptTracker2 = null;
                    for (ConnectionAttemptTracker connectionAttemptTracker3 : this.pendingConnections) {
                        if (connectionAttemptTracker2 == null || connectionAttemptTracker2.attemptCount > connectionAttemptTracker3.attemptCount) {
                            connectionAttemptTracker2 = connectionAttemptTracker3;
                        }
                    }
                    if (connectionAttemptTracker2 != null) {
                        this.pendingConnections.remove(connectionAttemptTracker2);
                    }
                }
                ConnectionAttemptTracker connectionAttemptTracker4 = new ConnectionAttemptTracker(this);
                connectionAttemptTracker4.address = inetAddress;
                this.pendingConnections.add(connectionAttemptTracker4);
            }
            if (this.acceptedCount > 500) {
                if (!this.p) {
                    this.p = true;
                    GameEngine.log("DOS: Too many unique attempts: " + this.acceptedCount + ". udp:" + this.isUdp);
                    return false;
                }
                return false;
            }
            int i2 = 0;
            int i3 = 0;
            for (NetworkConnection networkConnection : this.networkEngine.sendQueue) {
                i3++;
                if (networkConnection.inetAddress != null && inetAddress.equals(networkConnection.inetAddress)) {
                    i2++;
                }
            }
            int i4 = 20;
            if (i3 > 150) {
                i4 = 10;
            }
            if (i3 > 200) {
                i4 = 5;
            }
            if (i2 > i4) {
                if (!this.q) {
                    this.q = true;
                    GameEngine.log("DOS: Too open connections from same ip:" + inetAddress.toString() + " (count:" + i2 + ") max:" + i4);
                    return false;
                }
                return false;
            }
            if (i3 > 300) {
                if (!this.o) {
                    this.o = true;
                    GameEngine.log("DOS: Too open connections locking down:" + inetAddress.toString() + " (count:" + i3 + ")");
                    return false;
                }
                return false;
            }
            this.pendingLimit++;
            return true;
        }
    }

    /* JADX INFO: renamed from: a */
    public void recreateServerSocket() throws IOException {
        this.networkEngine.d("Recreating server socket " + (this.isUdp ? "udp" : "tcp"));
        synchronized (this.acceptLock) {
            if (this.serverSocket != null) {
                try {
                    this.serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.serverSocket = null;
            }
            if (!this.isRunning) {
                throw new IOException("recreate on non-active socket");
            }
            startSocket(this.isUdp);
        }
    }

    /* JADX INFO: renamed from: a */
    public void startSocket(boolean z) throws IOException {
        this.port = this.networkEngine.m;
        this.networkEngine.d("starting socket.. " + (z ? "udp" : "tcp") + " port: " + this.port);
        this.isUdp = z;
        if (!z) {
            this.serverSocket = new ServerSocket(this.port);
            return;
        }
        ReliableServerSocket reliableServerSocket = new ReliableServerSocket(this.networkEngine.m, 0, null, true);
        reliableServerSocket.setAcceptancePolicy(new AcceptFilter() { // from class: com.corrodinggames.rts.gameFramework.j.ao.1
            @Override // net.rudp.socket.AcceptFilter
            /* JADX INFO: renamed from: a */
            public boolean shouldAcceptPeer(SocketAddress socketAddress) {
                if (socketAddress instanceof InetSocketAddress) {
                    return ConnectionAcceptor.this.isIpAllowed(((InetSocketAddress) socketAddress).getAddress(), false);
                }
                GameEngine.log("AcceptFilter: Unhandled SocketAddress type:" + socketAddress.getClass().getName());
                return true;
            }
        });
        this.serverSocket = reliableServerSocket;
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        Thread.currentThread().setName("NewConnectionWorker-" + (this.isUdp ? "udp" : "tcp") + " - " + this.port);
        int i = 0;
        int i2 = 0;
        this.networkEngine.d("reading..");
        while (this.isRunning) {
            try {
                Socket socketAccept = this.serverSocket.accept();
                try {
                    socketAccept.setTcpNoDelay(true);
                    socketAccept.setSoTimeout(15000);
                    String hostAddress = "<unknown>";
                    InetAddress inetAddress = socketAccept.getInetAddress();
                    if (inetAddress != null) {
                        hostAddress = inetAddress.getHostAddress();
                    }
                    if (!isIpAllowed(inetAddress, true)) {
                        socketAccept.close();
                    } else {
                        NetworkConnection networkConnection = new NetworkConnection(this.networkEngine, socketAccept);
                        String str = "Accepted new connection id:" + networkConnection.connectionId + ".. (ip:" + hostAddress + ")";
                        if (this.isUdp) {
                            str = str + " (udp)";
                        }
                        this.networkEngine.d(str);
                        networkConnection.isUdp = this.isUdp;
                        networkConnection.inetAddress = inetAddress;
                        networkConnection.startWorkers();
                        this.networkEngine.sendQueue.add(networkConnection);
                    }
                } catch (IOException e) {
                    GameEngine.log("Got IOException on new player connection");
                    e.printStackTrace();
                }
            } catch (IOException e2) {
                if (this.isRunning) {
                    GameEngine gameEngine = GameEngine.getInstance();
                    i++;
                    GameEngine.log("ServerSocket-accept(" + (this.isUdp ? "udp" : "tcp") + ") failed: " + e2.getMessage() + " (closed:" + this.serverSocket.isClosed() + ")");
                    if (i > 100) {
                        GameEngine.log("Too many server socket fails");
                        stop();
                        return;
                    }
                    try {
                        recreateServerSocket();
                        if (i2 < 3 && gameEngine.networkEngine.getRegisteredNonRelayConnectionCount() > 0) {
                            String str2 = "Warning: server socket got closed and needed to be recreated, players were likely disconnected (but can rejoin).";
                            if (GameEngine.isIOSVersion) {
                                str2 = str2 + "\n This likely due to iOS removing sockets of background apps. Avoid minimising the game when hosting.";
                            }
                            GameEngine.getInstance().alert(str2);
                            i2++;
                        }
                    } catch (IOException e3) {
                        e3.printStackTrace();
                        GameEngine.getInstance().alert("Warning server socket got closed and could not be recreated");
                        stop();
                        return;
                    }
                } else {
                    GameEngine.log("ServerSocket-accept(" + (this.isUdp ? "udp" : "tcp") + "): Got expected IOException after closed socket");
                    return;
                }
            }
        }
    }

    /* JADX INFO: renamed from: b */
    public void stop() {
        synchronized (this.acceptLock) {
            this.isRunning = false;
            if (this.serverSocket != null) {
                try {
                    this.serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.serverSocket = null;
            }
        }
    }
}
