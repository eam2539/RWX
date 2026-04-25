package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/t.class */
class IndexedTimestampTracker extends TimestampTracker {
    public int c;
    final /* synthetic */ GameEngine d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public IndexedTimestampTracker(GameEngine gameEngine, int i) {
        super(gameEngine);
        this.d = gameEngine;
        this.c = i;
    }
}
