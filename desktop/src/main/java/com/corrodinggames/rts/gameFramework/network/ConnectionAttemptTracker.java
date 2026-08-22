package com.corrodinggames.rts.gameFramework.network;

import java.net.InetAddress;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.ap */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/ap.class */
class ConnectionAttemptTracker {

    /* JADX INFO: renamed from: a */
    InetAddress address;

    /* JADX INFO: renamed from: b */
    int attemptCount = 1;

    /* JADX INFO: renamed from: c */
    boolean dosWarningLogged;

    /* JADX INFO: renamed from: d */
    boolean dosExcessiveLogged;
    final /* synthetic */ ConnectionAcceptor e;

    ConnectionAttemptTracker(ConnectionAcceptor connectionAcceptor) {
        this.e = connectionAcceptor;
    }
}
