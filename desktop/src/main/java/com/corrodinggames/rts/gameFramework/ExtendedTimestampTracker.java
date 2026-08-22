package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.r */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/r.class */
class QueuedKeyStateEvent extends QueuedInputEvent {

    /* JADX INFO: renamed from: c */
    public int keyCode;

    /* JADX INFO: renamed from: d */
    public boolean isPressed;

    public QueuedKeyStateEvent(GameEngine gameEngine) {
        super(gameEngine);
    }
}
