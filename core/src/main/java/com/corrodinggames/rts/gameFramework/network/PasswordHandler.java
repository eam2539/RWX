package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.ae */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/ae.class */
public class PasswordHandler {

    /* JADX INFO: renamed from: b */
    public String promptMessage;
    public int c;
    public boolean d;

    /* JADX INFO: renamed from: e */
    public String dialogTitle;

    /* JADX INFO: renamed from: f */
    public String confirmButtonLabel;

    /* JADX INFO: renamed from: g */
    public String cancelButtonLabel;

    /* JADX INFO: renamed from: a */
    public void submitPassword(String str) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.d) {
            try {
                GameOutputStream gameOutputStream = new GameOutputStream();
                gameOutputStream.writeByte(1);
                gameOutputStream.writeInt(this.c);
                gameOutputStream.writeStringUTF(str);
                gameEngine.networkEngine.d(gameOutputStream.buildPacketData(118));
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (gameEngine.networkEngine.isServer) {
            GameEngine.logErrorColored("Cannot enter a password when we are a server");
        } else {
            gameEngine.networkEngine.roomPassword = str;
            gameEngine.networkEngine.sendRegisterConnectionsToAll();
        }
    }

    /* JADX INFO: renamed from: a */
    public void cancelPasswordEntry() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.networkEngine.disconnectNetworking("exited password");
        gameEngine.networkEngine.closeBattleroom();
    }
}
