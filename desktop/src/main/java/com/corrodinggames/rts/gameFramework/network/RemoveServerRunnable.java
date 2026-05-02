package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.z */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/z.class */
class RemoveServerRunnable implements Runnable {
    RemoveServerRunnable() {
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("startRemoveOnMasterServer", "Starting remove");
        try {
            ArrayList arrayList = new ArrayList(2);
            MasterServerClient.addParam(arrayList, "action", "remove");
            String str = gameEngine.networkEngine.masterServerGameId;
            if (str == null) {
                GameEngine.log("startRemoveOnMasterServer", "No game id");
                return;
            }
            MasterServerClient.addParam(arrayList, "id", str);
            MasterServerClient.addParam(arrayList, "private_token", gameEngine.networkEngine.sessionToken);
            BufferedReader bufferedReaderRequestMasterServerResponse = MasterServerClient.requestMasterServerResponse(arrayList);
            String line = bufferedReaderRequestMasterServerResponse.readLine();
            if (line == null || !line.contains("CORRODINGGAMES")) {
                GameEngine.log("startRemoveOnMasterServer", "Error bad header returned from the master server: " + line);
                return;
            }
            GameEngine.log("startRemoveOnMasterServer", "Remove server response was:" + bufferedReaderRequestMasterServerResponse.readLine());
            GameEngine.log("startRemoveOnMasterServer", "Completed load from master server without error");
        } catch (IOException e) {
            GameEngine.log("startRemoveOnMasterServer", "Remove failed");
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
