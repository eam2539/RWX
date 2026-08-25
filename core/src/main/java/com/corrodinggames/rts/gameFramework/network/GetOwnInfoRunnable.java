package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;
import io.github.rwx.ui.ServerListUiBridge;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/p.class */
class GetOwnInfoRunnable implements Runnable {
    GetOwnInfoRunnable() {
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("GetOwnInfoRunnable", "Starting getOwnInfoFromMasterServer");
        try {
            ArrayList arrayList = new ArrayList(2);
            arrayList.add(new BasicNameValuePair("action", "self_info"));
            MasterServerClient.addParam(arrayList, "port", Integer.toString(gameEngine.networkEngine.networkPort));
            MasterServerClient.addParam(arrayList, "id", gameEngine.networkEngine.masterServerGameId);
            MasterServerAuth.instance.addTokenHashParam(gameEngine.networkEngine.masterServerGameId, arrayList);
            MasterServerAuth.instance.addOptionalTokenHashParam(gameEngine.networkEngine.masterServerGameId, arrayList);
            BufferedReader bufferedReaderRequestMasterServerResponse = MasterServerClient.requestMasterServerResponse(arrayList);
            String line = bufferedReaderRequestMasterServerResponse.readLine();
            if (line == null || !line.contains("CORRODINGGAMES")) {
                GameEngine.log("GetOwnInfoRunnable", "Error bad header returned from the master server: " + line);
                return;
            }
            while (true) {
                String line2 = bufferedReaderRequestMasterServerResponse.readLine();
                if (line2 != null) {
                    String[] strArrSplit = line2.split(",");
                    if (strArrSplit.length <= 1) {
                        GameEngine.log("GetOwnInfoRunnable", "columns.length too short at:" + strArrSplit.length);
                    } else {
                        String str = strArrSplit[0];
                        String str2 = strArrSplit[1];
                        try {
                            GameEngine.log("GetOwnInfoRunnable", "got info");
                            gameEngine.networkEngine.setPublicIpInfoResult(true, str, Boolean.valueOf(Boolean.parseBoolean(str2)));
                        } catch (NumberFormatException e) {
                            GameEngine.log("GetOwnInfoRunnable", "failed to load server");
                            e.printStackTrace();
                        }
                    }
                } else {
                    ServerListUiBridge.refreshUI();
                    GameEngine.log("GetOwnInfoRunnable", "Completed load from master server without error");
                    return;
                }
            }
        } catch (ClientProtocolException e2) {
            gameEngine.networkEngine.setPublicIpInfoResult(false, (String) null, (Boolean) null);
            e2.printStackTrace();
        } catch (IOException e3) {
            gameEngine.networkEngine.setPublicIpInfoResult(false, (String) null, (Boolean) null);
            e3.printStackTrace();
        } catch (Exception e4) {
            gameEngine.networkEngine.setPublicIpInfoResult(false, (String) null, (Boolean) null);
            GameEngine.log("GetOwnInfoRunnable Failed", e4);
        }
    }
}
