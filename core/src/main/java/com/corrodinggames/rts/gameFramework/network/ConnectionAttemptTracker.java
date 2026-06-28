package com.corrodinggames.rts.gameFramework.network;

import java.net.InetAddress;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.ap */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/ap.class */
class ConnectionAttemptTracker {
    InetAddress a;
    int b = 1;
    boolean c;
    boolean d;
    final /* synthetic */ ConnectionAcceptor e;

    ConnectionAttemptTracker(ConnectionAcceptor connectionAcceptor) {
        this.e = connectionAcceptor;
    }
}
