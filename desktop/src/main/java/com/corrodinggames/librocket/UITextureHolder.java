package com.corrodinggames.librocket;

import com.LibRocket;
import com.corrodinggames.rts.game.units.UnitType;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.librocket.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/c.class */
public abstract class UITextureHolder extends LibRocket.TextureHolder {

    /* JADX INFO: renamed from: a */
    public String texturePath;

    /* JADX INFO: renamed from: b */
    public boolean lazyLoad;

    /* JADX INFO: renamed from: c */
    public boolean isThumbnail;

    /* JADX INFO: renamed from: d */
    public boolean noColor;

    /* JADX INFO: renamed from: e */
    public float alpha;

    /* JADX INFO: renamed from: f */
    public UnitType unitType;

    /* JADX INFO: renamed from: g */
    final /* synthetic */ LibRocketManager libRocketManager;

    /* JADX INFO: renamed from: a */
    public abstract boolean loadTexture() throws IOException;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public UITextureHolder(LibRocketManager libRocketManager) {
        super();
        this.libRocketManager = libRocketManager;
        this.alpha = 1.0f;
    }
}
