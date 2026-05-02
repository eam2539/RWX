package com.corrodinggames.rts.java;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.InputHandler;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.v */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/v.class */
public class JavaInputHandler extends InputHandler {
    @Override // com.corrodinggames.rts.gameFramework.InputHandler
    public boolean a(int i, int i2, boolean z) {
        return super.a(i, i2, z);
    }

    @Override // com.corrodinggames.rts.gameFramework.InputHandler
    public boolean a(int i, int i2) {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.InputHandler
    public int a() {
        return 0;
    }

    @Override // com.corrodinggames.rts.gameFramework.InputHandler
    public float b(int i, int i2) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.gameFramework.InputHandler
    public String c(int i, int i2) {
        return GameEngine.getModifierString(i2) + SlickToAndroidKeycodes.a(i);
    }
}
