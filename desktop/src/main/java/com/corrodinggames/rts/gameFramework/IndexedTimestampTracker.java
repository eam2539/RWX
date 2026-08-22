package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/t.class */
class QueuedMouseWheelEvent extends QueuedInputEvent {

    /* JADX INFO: renamed from: c */
    public int delta;
    final /* synthetic */ GameEngine d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public QueuedMouseWheelEvent(GameEngine gameEngine, int i) {
        super(gameEngine);
        this.d = gameEngine;
        this.delta = i;
    }
}
