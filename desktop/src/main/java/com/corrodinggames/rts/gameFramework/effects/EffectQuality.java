package com.corrodinggames.rts.gameFramework.effects;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.d.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/d/h.class */
public enum EffectQuality {
    verylow,
    low,
    high,
    veryhigh,
    critical;

    /* JADX INFO: renamed from: a */
    public boolean isLowerThan(EffectQuality effectQuality) {
        return effectQuality == null || ordinal() < effectQuality.ordinal();
    }
}
