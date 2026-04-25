package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.http.message.BasicNameValuePair;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.ab */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/ab.class */
class GetServerInfoRunnable implements Runnable {

    /* JADX INFO: renamed from: a */
    ConnectionResult result;

    /* JADX INFO: renamed from: b */
    String gameId;

    /* JADX INFO: renamed from: c */
    int serverCode;

    /* JADX INFO: renamed from: d */
    String password;

    GetServerInfoRunnable() {
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        GameEngine.getInstance();
        GameEngine.log("getGameServerInfoFromMasterServer", "Starting getGameServerInfoFromMasterServer");
        String serverCode = MasterServerClient.formatServerCode(this.serverCode);
        String strSplit = null;
        if (this.password != null) {
            strSplit = Utility.split(this.gameId + this.password, 3);
        }
        try {
            ArrayList arrayList = new ArrayList(2);
            arrayList.add(new BasicNameValuePair("action", "get"));
            arrayList.add(new BasicNameValuePair("game_id", this.gameId));
            arrayList.add(new BasicNameValuePair("c", serverCode));
            arrayList.add(new BasicNameValuePair("p_hash", strSplit));
            BufferedReader bufferedReaderRequestMasterServerResponse = MasterServerClient.requestMasterServerResponse(arrayList);
            String line = bufferedReaderRequestMasterServerResponse.readLine();
            if (line == null || !line.contains("CORRODINGGAMES")) {
                GameEngine.log("getGameServerInfoFromMasterServerRunnable", "Error bad header returned from the master server: " + line);
                this.result.setError("Unexpected header from master server", ConnectionErrorType.unknown, null);
                return;
            }
            String line2 = bufferedReaderRequestMasterServerResponse.readLine();
            if (line2 == null) {
                throw new IOException("Unexpected end of response");
            }
            if (line.contains("[FAILED]")) {
                GameEngine.updatePaintTextSizeIfNeeded("Got failed header with status:" + line2);
                String str = "Failed to get server connection data - unknown";
                ConnectionErrorType connectionErrorType = ConnectionErrorType.unknown;
                if (line2.startsWith("ERROR_OTHER")) {
                    String[] strArrSplit = line2.split(",");
                    if (strArrSplit.length >= 2) {
                        str = strArrSplit[1];
                    }
                    connectionErrorType = ConnectionErrorType.unknown;
                } else if (line2.startsWith("ERROR_MISSING")) {
                    str = "Request missing required fields";
                } else if (!line2.startsWith("ERROR_WRONG_C")) {
                    if (line2.startsWith("ERROR_MISSING_PASSWORD")) {
                        str = "Missing password";
                        connectionErrorType = ConnectionErrorType.unknown;
                    } else if (line2.startsWith("ERROR_WRONG_PASSWORD")) {
                        str = "Wrong password";
                        connectionErrorType = ConnectionErrorType.wrongPassword;
                    }
                }
                this.result.setError(str, connectionErrorType, null);
                return;
            }
            String line3 = bufferedReaderRequestMasterServerResponse.readLine();
            if (line3 == null) {
                throw new IOException("Unexpected end of response");
            }
            if (!line3.toLowerCase(Locale.ROOT).contains(Utility.md5("game_" + serverCode).toLowerCase(Locale.ROOT))) {
                GameEngine.log("getGameServerInfoFromMasterServerRunnable", "Error bad header returned from the master server: " + line3);
                this.result.setError("Unexpected return from master server", ConnectionErrorType.unknown, null);
                return;
            }
            bufferedReaderRequestMasterServerResponse.readLine();
            String line4 = bufferedReaderRequestMasterServerResponse.readLine();
            if (line4 == null) {
                throw new IOException("Unexpected end of response");
            }
            String[] strArrSplit2 = line4.split(",");
            if (strArrSplit2.length <= 18) {
                throw new RuntimeException("getGameServerInfoFromMasterServerRunnable: columns.length too short at:" + strArrSplit2.length);
            }
            String str2 = strArrSplit2[3];
            String str3 = strArrSplit2[4];
            String str4 = strArrSplit2[5];
            String str5 = strArrSplit2[6];
            String str6 = strArrSplit2[7];
            String str7 = strArrSplit2[8];
            String str8 = strArrSplit2[9];
            String str9 = strArrSplit2[10];
            String str10 = strArrSplit2[11];
            String str11 = strArrSplit2[12];
            String str12 = strArrSplit2[13];
            String str13 = strArrSplit2[15];
            String str14 = strArrSplit2[16];
            String str15 = strArrSplit2[17];
            String str16 = strArrSplit2[18];
            GameEngine.log("getGameServerInfoFromMasterServerRunnable", "got ");
            GameEngine.log("getGameServerInfoFromMasterServerRunnable", "Completed get from master server without error");
            this.result.setResolvedAddress(str2 + ":" + str4);
        } catch (IOException e) {
            GameEngine.log("getGameServerInfoFromMasterServerRunnable Failed", (Exception) e);
            this.result.setError(e.getMessage(), ConnectionErrorType.unknown, e);
        } catch (ExecutionException e) {


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
