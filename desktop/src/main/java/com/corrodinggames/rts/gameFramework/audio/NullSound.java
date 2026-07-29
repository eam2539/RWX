package com.corrodinggames.rts.gameFramework.audio;

import android.content.Context;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.a.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/a/f.class */
public class NullSound extends SoundFactory {
    @Override // com.corrodinggames.rts.gameFramework.audio.SoundFactory
    public void a(Context context) {
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.SoundFactory
    public Sound a(int i) {
        return new com.corrodinggames.rts.gameFramework.sound.NullSound(Utility.getFieldNameByValue(R.raw.class, i), this);
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.SoundFactory
    public Sound a(String str, AssetInputStream assetInputStream, boolean z) {
        return new com.corrodinggames.rts.gameFramework.sound.NullSound(str, this);
    }

    public static Sound b() {
        return new com.corrodinggames.rts.gameFramework.sound.NullSound("Null (from out of memory)", null);
    }

    public static Sound a(String str) {
        return new com.corrodinggames.rts.gameFramework.sound.NullSound("Null sound - " + str, null);
    }
}
