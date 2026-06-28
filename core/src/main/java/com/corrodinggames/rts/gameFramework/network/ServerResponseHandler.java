package com.corrodinggames.rts.gameFramework.network;

import java.io.BufferedReader;
import java.io.IOException;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.s */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/s.class */
public abstract class ServerResponseHandler {

    /* JADX INFO: renamed from: d */
    String errorOrResponseText;

    /* JADX INFO: renamed from: e */
    boolean parsedSuccessfully;

    /* JADX INFO: renamed from: f */
    int pendingRequests;

    /* JADX INFO: renamed from: a */
    abstract void handleServerListResponse(BufferedReader bufferedReader, int i, String str) throws IOException;

    /* JADX INFO: renamed from: a */
    abstract void onComplete();

    /* JADX INFO: Access modifiers changed from: package-private */
    protected ServerResponseHandler() {
    }
}
