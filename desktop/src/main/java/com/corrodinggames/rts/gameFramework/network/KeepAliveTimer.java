package com.corrodinggames.rts.gameFramework.network;

import java.io.IOException;
import java.util.TimerTask;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.av */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/av.class */
class KeepAliveTimer extends TimerTask {

    /* JADX INFO: renamed from: c */
    private final NetworkEngine networkEngine;

    /* JADX INFO: renamed from: a */
    public boolean sendPingNext = true;

    /* JADX INFO: renamed from: b */
    public long lastKeepAliveTime = 0;

    KeepAliveTimer(NetworkEngine networkEngine) {
        this.networkEngine = networkEngine;
    }

    @Override // java.util.TimerTask, java.lang.Runnable
    public void run() {
        try {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (this.networkEngine.playerUpdatePendingTimestamp != 0 && (jCurrentTimeMillis > this.networkEngine.playerUpdatePendingTimestamp + 5 || jCurrentTimeMillis < this.networkEngine.playerUpdatePendingTimestamp)) {
                this.networkEngine.playerUpdatePendingTimestamp = 0L;
                this.networkEngine.sendPlayerUpdateNow();
            }
            if (jCurrentTimeMillis > this.lastKeepAliveTime + 1000 || jCurrentTimeMillis < this.lastKeepAliveTime) {
                this.lastKeepAliveTime = jCurrentTimeMillis;
                if (this.sendPingNext) {
                    GameOutputStream gameOutputStream = new GameOutputStream();
                    gameOutputStream.writeLong(System.currentTimeMillis());
                    gameOutputStream.writeByte(0);
                    this.networkEngine.g(gameOutputStream.buildPacketData(108));
                } else {
                    this.networkEngine.markPlayerUpdatePending();
                }
                this.sendPingNext = !this.sendPingNext;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
