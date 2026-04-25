package com.corrodinggames.rts.gameFramework.platform;

import com.corrodinggames.rts.gameFramework.GameEngine;
import java.io.File;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.l.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/l/c.class */
public class PlatformExtensionBackend {
    public String a() {
        return null;
    }

    public boolean b() {
        return true;
    }

    public void a(File file) {
        GameEngine.isInSpace("PlatformExtensionBackend:shareFile");
        GameEngine.isInSpace("abstract shareFile:" + file.getPath());
    }

    public void a(FileSelectionCallback fileSelectionCallback) {
    }

    public float c() {
        return 0.0f;
    }
}
