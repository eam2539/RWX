package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.GameTeam;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import io.github.rwx.ui.BattleRoomUiBridge;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/c.class */
public class NetworkConnection {

    /* JADX INFO: renamed from: W */
    final NetworkEngine networkEngine;

    /* JADX INFO: renamed from: c */
    public int connectionId;

    /* JADX INFO: renamed from: d */
    public Socket socket;

    /* JADX INFO: renamed from: e */
    InetAddress inetAddress;

    /* JADX INFO: renamed from: g */
    public long lastActivityTime;
    public boolean h;
    public boolean i;

    /* JADX INFO: renamed from: j */
    public NetworkConnection relayConnection;

    /* JADX INFO: renamed from: l */
    PacketData lastPacket;

    /* JADX INFO: renamed from: m */
    public String remoteId;

    /* JADX INFO: renamed from: n */
    public String forwardedIpAddress;
    public String o;

    /* JADX INFO: renamed from: p */
    public boolean allowLargeIncomingPackets;

    /* JADX INFO: renamed from: q */
    public boolean trackLastPacket;

    /* JADX INFO: renamed from: r */
    public boolean optimizeSplitContinuation;

    /* JADX INFO: renamed from: s */
    public boolean isRelayServer;

    /* JADX INFO: renamed from: t */
    public boolean isRelayLinked;

    /* JADX INFO: renamed from: u */
    public boolean isDirectClient;

    /* JADX INFO: renamed from: v */
    public boolean isDirectServer;

    /* JADX INFO: renamed from: w */
    public boolean isForwarded;
    public int x;
    public int y;

    /* JADX INFO: renamed from: z */
    public GameTeam player;

    /* JADX INFO: renamed from: F */
    ReceiveWorker receiveWorker;

    /* JADX INFO: renamed from: G */
    SendWorker sendWorker;

    /* JADX INFO: renamed from: H */
    Thread receiveThread;

    /* JADX INFO: renamed from: I */
    Thread sendThread;

    /* JADX INFO: renamed from: L */
    public String connectionLabel;

    /* JADX INFO: renamed from: M */
    public int sessionRandomId;
    public boolean N;
    public boolean O;

    /* JADX INFO: renamed from: P */
    public int bytesReadIterations;
    public boolean Q;

    /* JADX INFO: renamed from: R */
    public int commandCounter;

    /* JADX INFO: renamed from: S */
    public long commandWindowStartMs;

    /* JADX INFO: renamed from: T */
    public boolean commandLimitReached;

    /* JADX INFO: renamed from: U */
    volatile int bytesReadTotalCurrentPacket;

    /* JADX INFO: renamed from: V */
    volatile int bytesReadSoFar;

    /* JADX INFO: renamed from: a */
    volatile boolean isDisconnecting = false;

    /* JADX INFO: renamed from: b */
    volatile boolean disconnectRequested = false;

    /* JADX INFO: renamed from: f */
    ConcurrentLinkedQueue outboundQueue = new ConcurrentLinkedQueue();

    /* JADX INFO: renamed from: k */
    public int relayChannelId = -1;
    int A = -1;
    long B = -1;
    boolean C = false;
    boolean D = false;
    public int E = 999999;

    /* JADX INFO: renamed from: J */
    boolean isInputClosed = false;

    /* JADX INFO: renamed from: K */
    boolean isOutputClosed = false;

    public NetworkConnection(NetworkEngine networkEngine, Socket socket) {
        this.networkEngine = networkEngine;
        this.socket = socket;
        synchronized (this.networkEngine.sessionLock) {
            this.connectionId = this.networkEngine.nextConnectionId;
            this.networkEngine.nextConnectionId++;
        }
        this.sessionRandomId = Utility.getRandomIntInRange(1, 1000000);
    }

    /* JADX INFO: renamed from: a */
    public boolean isCommandRateLimitExceeded() {
        if (this.commandWindowStartMs < System.currentTimeMillis() - 10000) {
            this.commandWindowStartMs = System.currentTimeMillis();
            this.commandCounter = 0;
        }
        if (this.commandCounter > 100) {
            if (!this.commandLimitReached) {
                this.commandLimitReached = true;
                logInfo("Command limit was reached");
                return true;
            }
            return true;
        }
        this.commandCounter++;
        return false;
    }

    /* JADX INFO: renamed from: b */
    public int getRecentPingMs() {
        if (this.B == -1) {
            return -2;
        }
        if (this.B < System.currentTimeMillis() - 5000) {
            return -1;
        }
        return this.A;
    }

    /* JADX INFO: renamed from: c */
    int getPlayerTeamId() {
        GameTeam gameTeam = this.player;
        if (gameTeam != null) {
            return gameTeam.teamId;
        }
        return -1;
    }

    /* JADX INFO: renamed from: d */
    public synchronized void startWorkers() throws IOException {
        this.sendWorker = new SendWorker(this);
        this.sendThread = new Thread(this.sendWorker);
        this.sendThread.setDaemon(true);
        this.sendThread.start();
        this.receiveWorker = new ReceiveWorker(this);
        this.receiveThread = new Thread(this.receiveWorker);
        this.receiveThread.setDaemon(true);
        this.receiveThread.start();
    }

    /* JADX INFO: renamed from: i */
    private void performDisconnectCleanup() {
        GameTeam gameTeam;
        this.isDisconnecting = true;
        if (this.networkEngine.isServer && !this.networkEngine.n() && (gameTeam = this.player) != null) {
            this.player = null;
            if (this.networkEngine.d(gameTeam) == null) {
                gameTeam.removeFromTeamRegistry();
                this.networkEngine.markPlayerUpdatePending();
                BattleRoomUiBridge.updateUI();
            }
        }
        if (this.receiveThread != null) {
            this.receiveThread.interrupt();
        }
        this.networkEngine.b(this);
        this.allowLargeIncomingPackets = false;
        if (this.trackLastPacket) {
            this.networkEngine.c(this, "Closing");
        }
    }

    /* JADX INFO: renamed from: j */
    private synchronized void requestLocalDisconnect() {
        if (this.isDisconnecting) {
            return;
        }
        this.disconnectRequested = true;
        if (this.sendWorker != null) {
            this.sendWorker.notifySendLoop();
        }
        if (this.receiveThread != null) {
            this.receiveThread.interrupt();
        }
        this.networkEngine.b(this);
    }

    /* JADX INFO: renamed from: a */
    public void handleRemoteDisconnect(String str) {
        GameOutputStream gameOutputStream = new GameOutputStream();
        if (str == null) {
            str = "NULL";
        }
        try {
            gameOutputStream.writeStringUTF(str);
            enqueuePacket(gameOutputStream.buildPacketData(111));
            requestLocalDisconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: a */
    public synchronized void handleTimeoutDisconnect(boolean z, boolean z2) {
        sendPacket(z, z2, "Time out");
    }

    /* JADX INFO: renamed from: e */
    public String getPlayerDisplayName() {
        String str = "<null>";
        if (this.player != null) {
            str = this.player.teamName;
        }
        return str;
    }

    /* JADX INFO: renamed from: f */
    public String getIpAddress() {
        InetAddress inetAddress;
        if (this.relayConnection != null) {
            return this.forwardedIpAddress;
        }
        try {
            Socket socket = this.socket;
            if (socket != null && (inetAddress = socket.getInetAddress()) != null) {
                return inetAddress.getHostAddress();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* JADX INFO: renamed from: g */
    public String getDisplayIpAddress() {
        if (this.relayConnection != null) {
            return this.forwardedIpAddress == null ? "<forwarded unknown>" : this.forwardedIpAddress;
        }
        String hostAddress = "<no socket>";
        try {
            Socket socket = this.socket;
            if (socket != null) {
                hostAddress = "<no bond socket>";
                InetAddress inetAddress = socket.getInetAddress();
                if (inetAddress != null) {
                    hostAddress = inetAddress.getHostAddress();
                }
            }
            return hostAddress;
        } catch (Exception e) {
            e.printStackTrace();
            return "<socket error>";
        }
    }

    /* JADX INFO: renamed from: a */
    public synchronized void sendPacket(boolean z, boolean z2, String str) {
        if (!this.isDisconnecting) {
            logInfo("handleRemoteDisconnect");
            String str2 = null;
            if (this.player != null) {
                str2 = this.player.teamName;
            }
            String str3 = null;
            if (this.player != null) {
                String str4 = "player";
                String str5 = VariableScope.nullOrMissingString;
                if (this.player.isSpectatorTeamColor()) {
                    str4 = "spectator";
                } else if (this.networkEngine.gameHasBeenStarted) {
                    str5 = this.player.getUnitCount(false, false) == 0 ? " (Had no units)" : " (Team " + this.player.getTeamSlotLabel() + ")";
                }
                str3 = str4 + " '" + this.player.teamName + "' disconnected" + str5;
            } else if (this.allowLargeIncomingPackets) {
                if (this.isRelayServer && this.trackLastPacket) {
                    str3 = "relay server disconnected";
                } else {
                    str3 = "a player disconnected";
                }
            }
            if (!this.networkEngine.isServer) {
                str3 = "The server disconnected";
            }
            if (str3 != null && str != null) {
                str3 = str3 + "  (" + NetworkEngine.i(str) + ")";
            }
            performDisconnectCleanup();
            if (str3 != null) {
                boolean z3 = false;
                if (this.player != null && this.networkEngine.isServer && this.networkEngine.d(this.player) != null) {
                    z3 = true;
                }
                if (!z3) {
                    if (!this.networkEngine.isServer) {
                        this.networkEngine.f(str3);
                    } else {
                        this.networkEngine.j(str3);
                    }
                } else {
                    logInfo("Not sending: '" + str3 + "' still another active connection");
                }
            }
            this.networkEngine.callbacks.b(this, str2);
        } else {
            logInfo("handleRemoteDisconnect: connection is already disconnecting");
        }
        if (!z2 && this.sendWorker != null) {
            this.sendWorker.notifySendLoop();
        }
        if (z2) {
            this.isInputClosed = true;
        }
        if (z) {
            this.isOutputClosed = true;
        }
        if (this.isInputClosed && this.isOutputClosed) {
            try {
                this.socket.close();
            } catch (IOException e) {
                GameEngine.log("Error while closing network socket", (Throwable) e);
            }
            this.sendThread = null;
            this.receiveThread = null;
            this.sendWorker = null;
            this.receiveWorker = null;
            if (this.outboundQueue != null) {
                this.outboundQueue.clear();
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public void logErrorWithException(String str, Throwable th) {
        GameEngine.log(formatLogPrefix(str), th);
    }

    /* JADX INFO: renamed from: b */
    public void logDebug(String str) {
        GameEngine.logColored(formatLogPrefix(str));
    }

    /* JADX INFO: renamed from: c */
    public void logInfo(String str) {
        GameEngine.log(formatLogPrefix(str));
    }

    /* JADX INFO: renamed from: d */
    public String formatLogPrefix(String str) {
        String str2 = "id:" + this.connectionId + ": " + str;
        GameTeam gameTeam = this.player;
        if (gameTeam != null) {
            str2 = str2 + " (Player:" + gameTeam.teamName + ")";
        }
        return str2;
    }

    /* JADX INFO: renamed from: a */
    public void enqueuePacket(PacketData packetData) {
        if (this.sendWorker == null && this.isDisconnecting) {
            return;
        }
        this.sendWorker.enqueueOutgoingPacket(packetData);
    }

    /* JADX INFO: renamed from: h */
    public boolean isConnected() {
        return !this.isDisconnecting;
    }

    public void finalize() {
        try {
            if (this.socket == null || this.socket.isClosed()) {
                return;
            }
            GameEngine.log("Connection::finalize called on unclosed socket (index:" + this.connectionId + ")");
            if (this.socket.getInetAddress() == null) {
                GameEngine.log("Skipping possible steam socket");
            }
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (RuntimeException e2) {
            e2.printStackTrace();
        }
    }
}
