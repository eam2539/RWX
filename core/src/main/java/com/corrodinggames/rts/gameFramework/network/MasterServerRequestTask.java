package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;

import java.io.BufferedReader;
import java.net.UnknownHostException;
import java.util.List;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.u */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/u.class */
class MasterServerRequestTask implements Runnable {

    /* JADX INFO: renamed from: a */
    int requestIndex;

    /* JADX INFO: renamed from: b */
    List params;

    /* JADX INFO: renamed from: c */
    ServerResponseHandler responseHandler;

    /* JADX INFO: renamed from: d */
    String baseUrl;

    /* JADX INFO: renamed from: e */
    boolean usePost;

    public MasterServerRequestTask(List list, ServerResponseHandler serverResponseHandler, String str, boolean z, int i) {
        this.requestIndex = i;
        this.params = list;
        this.responseHandler = serverResponseHandler;
        this.baseUrl = str;
        this.usePost = z;
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine gameEngine = GameEngine.getInstance();
        try {
            if (MasterServerClient.ENABLE_STATUS_LOGGING) {
                GameEngine.log("LoadFromMasterServer", this.requestIndex + ": Started doSingleRequest");
            }
            BufferedReader bufferedReader = MasterServerClient.doSingleRequest(this.params, this.baseUrl, this.usePost).bufferedReader;
            if (MasterServerClient.ENABLE_STATUS_LOGGING) {
                GameEngine.log("LoadFromMasterServer", this.requestIndex + ": Ended doSingleRequest");
            }
            this.responseHandler.handleServerListResponse(bufferedReader, this.requestIndex, this.baseUrl);
        } catch (Exception e) {
            e.printStackTrace();
            String strAreEqual = Utility.formatExceptionMessage(e, true);
            if (e instanceof UnknownHostException) {
                strAreEqual = "DNS lookup failed, check your internet connection";
            }
            if (strAreEqual != null && strAreEqual.contains("Cleartext HTTP traffic")) {
                strAreEqual = strAreEqual + " ( Broken apk file? - " + gameEngine.getPackageName() + ")";
            }
            this.responseHandler.errorOrResponseText = "#" + this.requestIndex + ": " + strAreEqual;
            GameEngine.log("Error getting game list from server #" + this.requestIndex);
            if (gameEngine.isModdingEnabled()) {
                gameEngine.alert("Error getting game list from server #" + this.requestIndex, 1);
            }
        }
        synchronized (this.responseHandler) {
            this.responseHandler.pendingRequests--;
            if (this.responseHandler.pendingRequests == 0) {
                this.responseHandler.onComplete();
            }
        }
    }
}
