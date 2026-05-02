package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.GameTeam;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.ac */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/ac.class */
public class NetworkCallbacks {
    public boolean a(NetworkConnection networkConnection, String str, String str2) {
        return true;
    }

    public boolean a(NetworkConnection networkConnection, PlayerTeam playerTeam, String str, boolean z) {
        return true;
    }

    public void b(NetworkConnection networkConnection, String str, String str2) {
    }

    public void a(int i, String str, String str2, NetworkConnection networkConnection) {
    }

    public String a(NetworkConnection networkConnection, String str) {
        return null;
    }

    public void c(NetworkConnection networkConnection, String str, String str2) {
    }

    public void b(NetworkConnection networkConnection, String str) {
    }

    public void a(PlayerTeam playerTeam) {
    }

    public String a(NetworkConnection networkConnection, String str, int i, int i2, String str2, GameTeam gameTeam) {
        GameEngine.log("new player Joining packageName:" + str2 + ", appVersion:" + i2 + ", playerName:" + str + " ip:" + networkConnection.getDisplayIpAddress() + " id:" + networkConnection.connectionId);
        return null;
    }

    /* JADX INFO: renamed from: a */
    public void onAllPlayersReady() {
    }

    public boolean a(NetworkConnection networkConnection) {
        return false;
    }

    public boolean b(NetworkConnection networkConnection) {
        return false;
    }

    /* JADX INFO: renamed from: b */
    public void onStartGameEvent() {
        GameEngine.log("NetworkCallbacks:startGameEvent()");
    }

    public void c() {
    }

    /* JADX INFO: renamed from: a */
    public void onPasswordPrompt(PasswordHandler passwordHandler) {
    }

    public void d() {
    }

    public boolean e() {
        return false;
    }
}
