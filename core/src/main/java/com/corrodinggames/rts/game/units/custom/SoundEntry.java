package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.gameFramework.audio.Sound;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bm */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bm.class */
class SoundEntry {

    /* JADX INFO: renamed from: a */
    Sound sound;

    /* JADX INFO: renamed from: b */
    float volume = 0.3f;

    /* JADX INFO: renamed from: c */
    final /* synthetic */ SoundList parent;

    SoundEntry(SoundList soundList) {
        this.parent = soundList;
    }
}
