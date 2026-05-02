package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ai */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ai.class */
public class InputHandler {
    public boolean a(int i, int i2, boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (!gameEngine.checkModifierKeys(i2, z)) {
            return false;
        }
        return gameEngine.isKeyPressed(i);
    }

    public boolean a(int i, int i2) {
        return false;
    }

    public int a() {
        return 0;
    }

    public float b(int i, int i2) {
        return 0.0f;
    }

    public String c(int i, int i2) {
        return "<abstract>";
    }
}
