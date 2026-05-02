package com.corrodinggames.rts.game.map;

import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.b.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/b/k.class */
class TilesetImageDescriptor {

    /* JADX INFO: renamed from: a */
    static int nextEmbedId = 1;

    /* JADX INFO: renamed from: b */
    public boolean inUse;

    /* JADX INFO: renamed from: c */
    public String pathPrefix;

    /* JADX INFO: renamed from: d */
    public String imageKey;

    /* JADX INFO: renamed from: e */
    public Texture texture;

    /* JADX INFO: renamed from: f */
    public String embeddedBase64;

    /* JADX INFO: renamed from: g */
    public String originalImageName;

    TilesetImageDescriptor() {
    }
}
