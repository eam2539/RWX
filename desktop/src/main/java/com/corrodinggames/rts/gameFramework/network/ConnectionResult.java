package com.corrodinggames.rts.gameFramework.network;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/w.class */
public class ConnectionResult {

    /* JADX INFO: renamed from: a */
    public String resolvedAddress;

    /* JADX INFO: renamed from: b */
    public String errorMessage;

    /* JADX INFO: renamed from: c */
    public ConnectionErrorType errorType;

    /* JADX INFO: renamed from: a */
    public void setResolvedAddress(String str) {
        this.resolvedAddress = str;
    }

    /* JADX INFO: renamed from: a */
    public void setError(String str, ConnectionErrorType connectionErrorType, Exception exc) {
        this.errorMessage = str;
        this.errorType = connectionErrorType;
    }
}
