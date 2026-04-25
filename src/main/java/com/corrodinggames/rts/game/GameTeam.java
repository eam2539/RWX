package com.corrodinggames.rts.game;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/e.class */
public class GameTeam extends PlayerTeam {
    public GameTeam(int i) {
        super(i);
    }

    public GameTeam(int i, boolean z) {
        super(i, z);
    }

    public GameTeam(int i, boolean z, String str) {
        super(i, z);
        this.teamName = str;
    }

    @Override // com.corrodinggames.rts.game.PlayerTeam
    /* JADX INFO: renamed from: a */
    public void updateTeam(float f) {
        super.updateTeam(f);
    }
}
