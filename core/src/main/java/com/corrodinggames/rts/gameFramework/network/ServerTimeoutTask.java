package com.corrodinggames.rts.gameFramework.network;

import java.util.TimerTask;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/o.class */
class ServerTimeoutTask extends TimerTask {

    /* JADX INFO: renamed from: a */
    int timeoutSeconds;

    ServerTimeoutTask(int i) {
        this.timeoutSeconds = i;
    }

    @Override // java.util.TimerTask, java.lang.Runnable
    public void run() {
        MasterServerClient.removeStaleServers(this.timeoutSeconds, -1);
    }
}
