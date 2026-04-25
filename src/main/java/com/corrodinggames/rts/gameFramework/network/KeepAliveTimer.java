package com.corrodinggames.rts.gameFramework.network;

import java.io.IOException;
import java.util.TimerTask;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.av */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/av.class */
class KeepAliveTimer extends TimerTask {

    /* JADX INFO: renamed from: c */
    private final NetworkEngine networkEngine;

    /* JADX INFO: renamed from: a */
    public boolean isRunning = true;

    /* JADX INFO: renamed from: b */
    public long lastPingTime = 0;

    KeepAliveTimer(NetworkEngine networkEngine) {
        this.networkEngine = networkEngine;
    }

    @Override // java.util.TimerTask, java.lang.Runnable
    public void run() {
        try {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (this.networkEngine.au != 0 && (jCurrentTimeMillis > this.networkEngine.au + 5 || jCurrentTimeMillis < this.networkEngine.au)) {
                this.networkEngine.au = 0L;
                this.networkEngine.Q();
            }
            if (jCurrentTimeMillis > this.lastPingTime + 1000 || jCurrentTimeMillis < this.lastPingTime) {
                this.lastPingTime = jCurrentTimeMillis;
                if (this.isRunning) {
                    GameOutputStream gameOutputStream = new GameOutputStream();
                    gameOutputStream.writeLong(System.currentTimeMillis());
                    gameOutputStream.writeByte(0);
                    this.networkEngine.g(gameOutputStream.buildPacketData(108));
                } else {
                    this.networkEngine.P();
                }
                this.isRunning = !this.isRunning;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
