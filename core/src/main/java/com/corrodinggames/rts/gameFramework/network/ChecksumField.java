package com.corrodinggames.rts.gameFramework.network;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.al */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/al.class */
public class ChecksumField {

    /* JADX INFO: renamed from: a */
    public String label;

    /* JADX INFO: renamed from: b */
    public long value;

    /* JADX INFO: renamed from: c */
    boolean includeInTotalChecksum;
    final /* synthetic */ GameStateChecksum d;

    public ChecksumField(GameStateChecksum gameStateChecksum, String str) {
        this(gameStateChecksum, str, true);
    }

    public ChecksumField(GameStateChecksum gameStateChecksum, String str, boolean z) {
        this.d = gameStateChecksum;
        this.label = str;
        this.includeInTotalChecksum = z;
        gameStateChecksum.fields.add(this);
    }
}
