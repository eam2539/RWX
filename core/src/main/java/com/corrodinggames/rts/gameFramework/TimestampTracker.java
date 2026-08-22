package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/p.class */
class QueuedInputEvent {

    /* JADX INFO: renamed from: a */
    long timestamp = System.currentTimeMillis();

    final /* synthetic */ GameEngine gameEngine;

    public QueuedInputEvent(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
}
