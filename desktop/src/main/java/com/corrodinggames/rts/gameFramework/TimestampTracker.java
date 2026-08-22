package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/p.class */
class QueuedInputEvent {

    final /* synthetic */ GameEngine gameEngine;
    /* JADX INFO: renamed from: a */
    long timestamp = System.currentTimeMillis();

    public QueuedInputEvent(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
}
