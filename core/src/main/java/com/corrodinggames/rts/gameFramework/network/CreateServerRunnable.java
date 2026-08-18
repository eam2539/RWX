package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.y */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/y.class */
class CreateServerRunnable implements Runnable {
    CreateServerRunnable() {
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        long currentTimeMillis = GameEngine.getCurrentTimeMillis();
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("StartCreateOnMasterServer", "Starting create");
        try {
            try {
                ArrayList arrayList = new ArrayList(2);
                MasterServerClient.addParam(arrayList, "action", "add");
                String str = "u_" + Utility.randomUUID();
                MasterServerClient.addParam(arrayList, "user_id", str);
                MasterServerAuth.instance.addAuthParams(str, arrayList);
                MasterServerClient.addParam(arrayList, "game_name", "Unnamed");
                MasterServerClient.addParam(arrayList, "game_version", Integer.toString(gameEngine.getVersionCode(true)));
                if (gameEngine.networkEngine.chatOnlyMode) {
                    MasterServerClient.addParam(arrayList, "game_version_string", "ANY");
                } else {
                    MasterServerClient.addParam(arrayList, "game_version_string", gameEngine.getVersionString());
                }
                MasterServerClient.addParam(arrayList, "game_version_beta", Utility.booleanToString(gameEngine.isBetaOrPreview()));
                String requiredModsSummary = gameEngine.networkEngine.getRequiredModsSummary();
                if (requiredModsSummary != null) {
                    MasterServerClient.addParam(arrayList, "game_mods", requiredModsSummary);
                }
                MasterServerClient.addParam(arrayList, "private_token", gameEngine.networkEngine.sessionToken);
                MasterServerClient.addParam(arrayList, "private_token_2", Utility.md5Hex(Utility.md5Hex(gameEngine.networkEngine.sessionToken)));
                MasterServerClient.addParam(arrayList, "confirm", Utility.md5Hex("a" + Utility.md5Hex(gameEngine.networkEngine.sessionToken)));
                MasterServerClient.addServerStatusParams(arrayList);
                BufferedReader bufferedReaderRequestMasterServerResponseWithTimeout = MasterServerClient.requestMasterServerResponseWithTimeout(arrayList, 15);
                String line = bufferedReaderRequestMasterServerResponseWithTimeout.readLine();
                if (line == null || !line.contains("CORRODINGGAMES")) {
                    GameEngine.log("StartCreateOnMasterServer", "Error bad header returned from the master server: " + line);
                    GameEngine.log("StartCreateOnMasterServer", "create took: " + ((GameEngine.getCurrentTimeMillis() - currentTimeMillis) / 1000000.0f) + " seconds");
                    return;
                }
                String[] strArrSplit = bufferedReaderRequestMasterServerResponseWithTimeout.readLine().split(",");
                if (strArrSplit.length < 1) {
                    GameEngine.log("StartCreateOnMasterServer", "columns.length too short at:" + strArrSplit.length);
                }
                String str2 = strArrSplit[0];
                try {
                    GameEngine.log("StartCreateOnMasterServer", "Created server is:" + str2);
                    gameEngine.networkEngine.masterServerGameId = str2;
                } catch (NumberFormatException e) {
                    GameEngine.log("StartCreateOnMasterServer", "failed to load server");
                    e.printStackTrace();
                }
                if (strArrSplit.length >= 2) {
                    try {
                        MasterServerAuth.authTokenLength = Integer.parseInt(strArrSplit[1]);
                    } catch (NumberFormatException e2) {
                        MasterServerAuth.authTokenLength = -1;
                    }
                }
                GameEngine.log("StartCreateOnMasterServer", "Completed create from master server without error");
                GameEngine.log("StartCreateOnMasterServer", "create took: " + ((GameEngine.getCurrentTimeMillis() - currentTimeMillis) / 1000000.0f) + " seconds");
            } catch (IOException e3) {
                e3.printStackTrace();
                GameEngine.log("StartCreateOnMasterServer", "create took: " + ((GameEngine.getCurrentTimeMillis() - currentTimeMillis) / 1000000.0f) + " seconds");
            }
        } catch (Throwable th) {
            GameEngine.log("StartCreateOnMasterServer", "create took: " + ((GameEngine.getCurrentTimeMillis() - currentTimeMillis) / 1000000.0f) + " seconds");
            try {
                throw th;
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
