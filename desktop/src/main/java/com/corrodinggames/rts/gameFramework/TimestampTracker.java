package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/p.class */
class TimestampTracker {
    long a = System.currentTimeMillis();
    final /* synthetic */ GameEngine b;

    public TimestampTracker(GameEngine gameEngine) {
        this.b = gameEngine;
    }
}
