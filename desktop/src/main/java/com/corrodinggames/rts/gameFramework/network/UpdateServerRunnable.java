package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.aa */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/aa.class */
class UpdateServerRunnable implements Runnable {
    UpdateServerRunnable() {
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        GameEngine gameEngine = GameEngine.getInstance();
        try {
            ArrayList arrayList = new ArrayList(2);
            MasterServerClient.addParam(arrayList, "action", "update");
            String str = gameEngine.networkEngine.masterServerGameId;
            if (str == null) {
                GameEngine.log("startUpdateOnMasterServer", "No game id");
                return;
            }
            MasterServerClient.addParam(arrayList, "id", str);
            MasterServerClient.addParam(arrayList, "private_token", gameEngine.networkEngine.sessionToken);
            if (GameEngine.isDedicatedServer()) {
                MasterServerClient.addParam(arrayList, "check_port", "false");
            }
            MasterServerClient.addServerStatusParams(arrayList);
            BufferedReader bufferedReaderRequestMasterServerResponse = MasterServerClient.requestMasterServerResponse(arrayList);
            String line = bufferedReaderRequestMasterServerResponse.readLine();
            if (line == null || !line.contains("CORRODINGGAMES")) {
                GameEngine.log("startUpdateOnMasterServer", "Error bad header returned from the master server: " + line);
                return;
            }
            String line2 = bufferedReaderRequestMasterServerResponse.readLine();
            if (!"GAME UPDATED".equals(line2)) {
                GameEngine.log("startUpdateOnMasterServer", "Update server response was:" + line2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
