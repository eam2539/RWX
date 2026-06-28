package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bq */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bq.class */
public abstract class Serializable {
    public abstract void a(GameOutputStream gameOutputStream) throws IOException;
}
