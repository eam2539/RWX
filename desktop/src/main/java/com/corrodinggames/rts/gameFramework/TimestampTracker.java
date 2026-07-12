package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/p.class */
class QueuedInputEvent {
    long a = System.currentTimeMillis();
    final /* synthetic */ GameEngine b;

    public QueuedInputEvent(GameEngine gameEngine) {
        this.b = gameEngine;
    }
}
