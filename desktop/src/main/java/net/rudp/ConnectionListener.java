package net.rudp;

/* JADX INFO: renamed from: a.a.p */
/* JADX INFO: loaded from: game-lib.jar:a/a/p.class */
public interface ConnectionListener {
    /* JADX INFO: renamed from: a */
    void onConnected();

    /* JADX INFO: renamed from: b */
    void onClosed();

    /* JADX INFO: renamed from: c */
    void onPacketReceived();

    /* JADX INFO: renamed from: d */
    void onPacketSent();
}
