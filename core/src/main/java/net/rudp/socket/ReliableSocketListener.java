package net.rudp.socket;

import net.rudp.ReliableSocket;

/* JADX INFO: renamed from: a.a.s */
/* JADX INFO: loaded from: game-lib.jar:a/a/s.class */
public interface ReliableSocketListener {
    /* JADX INFO: renamed from: a */
    void onConnected(ReliableSocket reliableSocket);

    /* JADX INFO: renamed from: b */
    void onConnectFailed(ReliableSocket reliableSocket);

    /* JADX INFO: renamed from: c */
    void onClosed(ReliableSocket reliableSocket);

    /* JADX INFO: renamed from: d */
    void onConnectionLost(ReliableSocket reliableSocket);

    /* JADX INFO: renamed from: e */
    void onRemoteReset(ReliableSocket reliableSocket);
}
