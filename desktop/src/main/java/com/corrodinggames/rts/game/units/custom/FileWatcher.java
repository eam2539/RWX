package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.gameFramework.FileChangeEngine;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.aa */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/aa.class */
class FileWatcher {
    public long a;
    public String b;

    public FileWatcher(String str) {
        this.b = str;
        this.a = a(true);
    }

    public long a(boolean z) {
        if (GameEngine.isAndroid()) {
            return 0L;
        }
        long jA = FileChangeEngine.a(this.b, z);
        if (z && jA == 0) {
            GameEngine.log("Failed to watch: " + this.b);
        }
        return jA;
    }
}
