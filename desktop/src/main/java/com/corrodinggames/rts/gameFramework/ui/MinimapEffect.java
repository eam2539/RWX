package com.corrodinggames.rts.gameFramework.ui;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/p.class */
class MinimapEffect {

    /* JADX INFO: renamed from: a */
    public int x;

    /* JADX INFO: renamed from: b */
    public int y;

    /* JADX INFO: renamed from: c */
    public float intensity = 1.0f;

    /* JADX INFO: renamed from: d */
    public float duration = 1.0f;

    /* JADX INFO: renamed from: e */
    public MinimapEffectType type = MinimapEffectType.base;

    /* JADX INFO: renamed from: f */
    final /* synthetic */ Minimap minimap;

    public MinimapEffect(Minimap minimap) {
        this.minimap = minimap;
    }
}
