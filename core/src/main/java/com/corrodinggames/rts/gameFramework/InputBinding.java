package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.af */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/af.class */
public abstract class InputBinding {
    public int a = -1;
    public int b = 0;
    protected boolean c;
    public boolean d;

    public abstract boolean a();

    public abstract boolean b();

    public abstract String c();

    public abstract boolean d();

    public boolean a(InputBinding inputBinding) {
        if (this.b != inputBinding.b || this.a != inputBinding.a) {
            return false;
        }
        return true;
    }
}
