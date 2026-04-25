package com.corrodinggames.rts.gameFramework.ui;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/t.class */
class StrategicPoint {

    /* JADX INFO: renamed from: a */
    public int gridX;

    /* JADX INFO: renamed from: b */
    public int gridY;

    /* JADX INFO: renamed from: c */
    public int screenX;

    /* JADX INFO: renamed from: d */
    public int screenY;

    /* JADX INFO: renamed from: e */
    public boolean isVisible;

    /* JADX INFO: renamed from: f */
    final /* synthetic */ Minimap minimap;

    public StrategicPoint(Minimap minimap, int i, int i2) {
        this.minimap = minimap;
        this.gridX = i;
        this.gridY = i2;
    }
}
