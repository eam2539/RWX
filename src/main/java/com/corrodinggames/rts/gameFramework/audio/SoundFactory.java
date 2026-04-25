package com.corrodinggames.rts.gameFramework.audio;

import android.content.Context;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.a.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/a/h.class */
public abstract class SoundFactory {
    HashMap h = new HashMap();

    public abstract Sound a(int i);

    public abstract Sound a(String str, AssetInputStream assetInputStream, boolean z);

    public abstract void a(Context context);

    public void a() {
    }
}
