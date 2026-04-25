package com.corrodinggames.rts.java.steam;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUtils;
import com.corrodinggames.librocket.scripts.Root;
import com.corrodinggames.librocket.scripts.ScriptEngine;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.mod.ModInfo;
import com.corrodinggames.rts.gameFramework.network.NetworkConnection;
import com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.c.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/c/b.class */
public class JavaSteamEngine extends DisabledSteamEngine {
    SteamFriendsCallbackHandler b;
    SteamFriends c;
    SteamMatchmaking d;
    SteamMatchmakingCallbackHandler e;
    SteamNetworkingCallbackHandler f;
    SteamWorkshopManager g;
    SteamNetworking h;
    SteamUtilsCallbackHandler i;
    SteamUtils j;
    boolean k = false;
    HashMap l = new HashMap();
    ByteBuffer m;
    SteamID n;
    boolean o;
    SteamID p;

    public SteamWorkshopManager n() {
        return this.g;
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void b() {
        if (this.k) {
            GameEngine.isInSpace("SteamEngine - init already called");
            return;
        }
        this.k = true;
        GameEngine.isInSpace("SteamEngine - java steamEngine init()");
        try {
            if (!SteamAPI.init()) {
                GameEngine.updatePaintTextSizeIfNeeded("steamAPI init failed");
                d();
                return;
            }
            this.m = ByteBuffer.allocateDirect(100000);
            this.b = new SteamFriendsCallbackHandler(this);
            this.c = new SteamFriends(this.b);
            this.e = new SteamMatchmakingCallbackHandler(this);
            this.d = new SteamMatchmaking(this.e);
            this.f = new SteamNetworkingCallbackHandler(this);
            this.h = new SteamNetworking(this.f, SteamNetworking.API.Client);
            this.g = new SteamWorkshopManager(this);
            try {
                this.g.a(new SteamUGC(this.g.a()));
                this.i = new SteamUtilsCallbackHandler(this);
                this.j = new SteamUtils(this.i);
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw new SteamException("Failed to create workshop");
            }
        } catch (SteamException e2) {
            e2.printStackTrace();
            d();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void a(float f) {
        SteamAPI.runCallbacks();
        if (this.h != null) {
            if (GameEngine.buildVersion != null) {
                GameEngine.isInSpace("Joining game from commandline invite:" + GameEngine.buildVersion);
                long j = Long.parseLong(GameEngine.buildVersion);
                GameEngine.buildVersion = null;
                this.d.joinLobby(SteamID.createFromNativeHandle(j));
            }
            while (true) {
                int iIsP2PPacketAvailable = this.h.isP2PPacketAvailable(0);
                if (iIsP2PPacketAvailable != 0) {
                    if (iIsP2PPacketAvailable > this.m.capacity()) {
                        GameEngine.updatePaintTextSizeIfNeeded("nextPacketSize:" + iIsP2PPacketAvailable + " larger then byteBuffer:" + this.m.capacity() + " resizing");
                        this.m = ByteBuffer.allocateDirect(iIsP2PPacketAvailable);
                    }
                    SteamID steamID = new SteamID();
                    try {
                        this.m.clear();
                        int p2PPacket = this.h.readP2PPacket(steamID, this.m, 0);
                        if (p2PPacket == 0) {
                            GameEngine.updatePaintTextSizeIfNeeded("readP2PPacket with rtn==" + p2PPacket);
                        }
                        SteamSocket steamSocket = (SteamSocket) this.l.get(steamID);
                        if (steamSocket != null && steamSocket.isClosed()) {
                            GameEngine.updatePaintTextSizeIfNeeded("Removing stale steam socket");
                            this.l.remove(steamID);
                            steamSocket = null;
                        }
                        if (steamSocket == null) {
                            b(steamID);
                            steamSocket = (SteamSocket) this.l.get(steamID);
                        }
                        if (steamSocket == null) {
                            GameEngine.isInSpace("Could not find remote ID steamSocket: " + steamID);
                        } else {
                            byte[] bArr = new byte[this.m.limit()];
                            this.m.get(bArr);
                            steamSocket.c.a(bArr);
                        }
                    } catch (Exception/*SteamException*/ e) {
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void d() {
        GameEngine.updatePaintTextSizeIfNeeded("JavaSteamEngine: disableSteam");
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine != null) {
            gameEngine.alert("Steam connection failed.");
        } else {
            GameEngine.isInSpace("cannot show alert game has not been created");
        }
        DisabledSteamEngine.a = new DisabledSteamEngine();
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public String c() {
        return this.c.getPersonaName();
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public boolean f() {
        return false;
    }

    public void a(String str) {
        GameEngine.isInSpace("Steam: " + str);
    }

    public void b(String str) {
        GameEngine.updatePaintTextSizeIfNeeded("Steam: " + str);
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void i() {
        a("createLobby");
        if (this.n != null) {
            b("createLobby: activeLobby!=null");
        }
        this.d.createLobby(SteamMatchmaking.LobbyType.FriendsOnly, 10);
    }

    public synchronized void a(SteamID steamID) {
        GameEngine.getInstance();
        this.n = steamID;
    }

    public NetworkConnection b(SteamID steamID) {
        GameEngine.isInSpace("addPeer: " + steamID);
        GameEngine gameEngine = GameEngine.getInstance();
        SteamSocket steamSocket = (SteamSocket) this.l.get(steamID);
        if (steamSocket != null) {
            if (steamSocket.isClosed()) {
                this.l.remove(steamID);
            } else {
                b("addPeer, user already exists");
                steamSocket.close();
            }
        }
        SteamSocket steamSocket2 = new SteamSocket(this, steamID);
        NetworkConnection networkConnection = new NetworkConnection(gameEngine.networkEngine, steamSocket2);
        try {
            networkConnection.i = true;
            networkConnection.startWorkers();
            gameEngine.networkEngine.sendQueue.add(networkConnection);
            this.l.put(steamID, steamSocket2);
            gameEngine.networkEngine.Q();
            return networkConnection;
        } catch (IOException e2) {
            e2.printStackTrace();
            networkConnection.handleRemoteDisconnect("crash");
            return null;
        }
    }

    public void c(final SteamID steamID) {
        GameEngine.isInSpace("connectTo: " + steamID);
        SteamSocket steamSocket = (SteamSocket) this.l.get(steamID);
        if (steamSocket != null) {
            if (steamSocket.isClosed()) {
                this.l.remove(steamID);
            } else {
                b("connectTo, user already exists");
                steamSocket.close();
            }
        }
        GameEngine.getInstance();
        if (!this.o) {
            ScriptEngine.getInstance().addRunnableToQueue(new Runnable() { // from class: com.corrodinggames.rts.java.c.b.1
                @Override // java.lang.Runnable
                public void run() {
                    GameEngine gameEngine = GameEngine.getInstance();
                    try {
                        JavaSteamEngine.this.a("connectTo runnable start");
                        Root root = ScriptEngine.getInstance().getRoot();
                        gameEngine.networkEngine.disconnectNetworking("starting new");
                        JavaSteamEngine.this.n = steamID;
                        JavaSteamEngine.this.p = JavaSteamEngine.this.d.getLobbyOwner(JavaSteamEngine.this.n);
                        String strPadLeft = gameEngine.settingsEngine.lastNetworkPlayerName;
                        String strC = DisabledSteamEngine.a().c();
                        if (strC != null && strPadLeft == null) {
                            strPadLeft = Utility.padLeft(strC.replace(" ", "_"), 20);
                        }
                        gameEngine.networkEngine.playerName = strPadLeft;
                        SteamSocket steamSocket2 = new SteamSocket(JavaSteamEngine.this, JavaSteamEngine.this.p);
                        JavaSteamEngine.this.l.put(JavaSteamEngine.this.p, steamSocket2);
                        gameEngine.networkEngine.a(steamSocket2);
                        Iterator it = gameEngine.networkEngine.sendQueue.iterator();
                        while (it.hasNext()) {
                            ((NetworkConnection) it.next()).i = true;
                        }
                        JavaSteamEngine.this.a("connected");
                        root.showBattleroom();
                        JavaSteamEngine.this.a("connectTo runnable end");
                    } catch (IOException e2) {
                        gameEngine.showMessageBox(e2.getMessage(), "Connection failed");
                        e2.printStackTrace();
                    }
                }
            });
        } else {
            a("connectTo as server?");
            b(steamID);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void j() {
        a("stopLobby");
        if (this.n == null) {
            b("stopLobby: activeLobby==null");
        } else {
            this.d.leaveLobby(this.n);
        }
        a("stopLobby: activeSteamSockets:" + this.l.size());
        Iterator it = this.l.values().iterator();
        while (it.hasNext()) {
            ((SteamSocket) it.next()).close();
        }
        this.l.clear();
        this.n = null;
        this.p = null;
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void g() {
        if (this.n == null) {
        }
        if (this.n == null) {
            GameEngine.getInstance().alert("Error: No steam lobby has been started");
        } else {
            this.c.activateGameOverlayInviteDialog(this.n);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void k() {
        try {
            this.g.c();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void l() {
        try {
            this.g.d();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void m() {
        n().b();
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void a(ModInfo modInfo) {
        n().c(modInfo);
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void b(ModInfo modInfo) {
        n().b(modInfo);
    }

    @Override // com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine
    public void a(ModInfo modInfo, boolean z, String str) {
        n().a(modInfo, z, str);
    }
}
