package com.corrodinggames.rts.gameFramework.network;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.aj */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/aj.class */
public class BanEntry {

    /* JADX INFO: renamed from: a */
    String ipAddress;

    /* JADX INFO: renamed from: b */
    long expiryTimeMs;

    /* JADX INFO: renamed from: c */
    String reason;

    public String getReasonText() {
        return this.reason != null ? this.reason : "Active ban";
    }

    public float getRemainingSeconds() {
        return (this.expiryTimeMs - System.currentTimeMillis()) / 1000.0f;
    }
}
