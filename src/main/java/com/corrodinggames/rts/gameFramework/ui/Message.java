package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/n.class */
public class Message {

    /* JADX INFO: renamed from: a */
    String author;

    /* JADX INFO: renamed from: b */
    String text;

    /* JADX INFO: renamed from: c */
    long timestamp;

    /* JADX INFO: renamed from: d */
    String formattedTimestamp;

    /* JADX INFO: renamed from: e */
    public int field_e = -1;

    /* JADX INFO: renamed from: f */
    public int field_f = -1;

    /* JADX INFO: renamed from: a */
    public int getTimeDifference() {
        return (int) (System.currentTimeMillis() - this.timestamp);
    }

    /* JADX INFO: renamed from: b */
    public boolean shouldDisplay() {
        if (GameEngine.getInstance().createInstance() || this.timestamp + ((long) 14000) > System.currentTimeMillis()) {
            return true;
        }
        return false;
    }
}
