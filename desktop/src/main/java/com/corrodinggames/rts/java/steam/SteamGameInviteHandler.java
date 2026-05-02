package com.corrodinggames.rts.java.steam;

import com.codedisaster.steamworks.SteamID;
import com.corrodinggames.librocket.ButtonAction;
import com.corrodinggames.librocket.scripts.ScriptEngine;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.c.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/c/a.class */
public class SteamGameInviteHandler implements Runnable {
    JavaSteamEngine a;
    String b;
    SteamID c;
    SteamID d;
    long e;
    Thread f;

    public SteamGameInviteHandler(JavaSteamEngine javaSteamEngine, SteamID steamID, SteamID steamID2, long j) {
        this.a = javaSteamEngine;
        this.c = steamID;
        this.d = steamID2;
        this.e = j;
        this.b = javaSteamEngine.c.getFriendPersonaName(steamID);
    }

    public void a() {
        if (this.f != null) {
            throw new RuntimeException("already started");
        }
        ScriptEngine.getInstance().addRunnableToQueue(new Runnable() { // from class: com.corrodinggames.rts.java.c.a.1
            @Override // java.lang.Runnable
            public void run() {
                ScriptEngine.getInstance().getRoot().showPopupWithButtons("Invite", "'" + SteamGameInviteHandler.this.b + "' has invited you to join a game", true, new ButtonAction("Join", SteamGameInviteHandler.this), null);
            }
        });
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.log("Join clicked");
        ScriptEngine.getInstance().getRoot().closePopup();
        GameEngine.getInstance();
        this.a.d.joinLobby(this.d);
    }
}
