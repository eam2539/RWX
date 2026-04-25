package com.corrodinggames.rts.java.gui;

import com.corrodinggames.librocket.GameMainManager;
import com.corrodinggames.rts.java.Main;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.b.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/b/a.class */
public class CommonGuiEngine extends GameMainManager {
    public Main f;
    boolean g = false;

    public static synchronized CommonGuiEngine p() {
        if (instance != null) {
            throw new RuntimeException("CommonGuiEngine already exists");
        }
        CommonGuiEngine commonGuiEngine = new CommonGuiEngine();
        instance = commonGuiEngine;
        return commonGuiEngine;
    }

    @Override // com.corrodinggames.librocket.GameMainManager
    /* JADX INFO: renamed from: g */
    public void applyResolution() {
        this.f.i();
    }

    @Override // com.corrodinggames.librocket.GameMainManager
    /* JADX INFO: renamed from: h */
    public void postUpdate() {
        this.f.u = true;
    }

    @Override // com.corrodinggames.librocket.GameMainManager
    /* JADX INFO: renamed from: i */
    public int getModifiers() {
        return this.f.j.e();
    }

    @Override // com.corrodinggames.librocket.GameMainManager
    /* JADX INFO: renamed from: d */
    public void setMouseGrabbed(boolean z) {
        this.f.a(z);
    }
}
