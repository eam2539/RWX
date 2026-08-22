package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.af */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/af.class */
public abstract class InputBinding {
    public int a = -1;
    /* JADX INFO: renamed from: b */
    public int modifierFlags = 0;
    /* JADX INFO: renamed from: c */
    protected boolean isPressed;
    /* JADX INFO: renamed from: d */
    public boolean isUserDefined;

    public abstract boolean a();

    public abstract boolean b();

    public abstract String c();

    public abstract boolean d();

    public boolean a(InputBinding inputBinding) {
        if (this.modifierFlags != inputBinding.modifierFlags || this.a != inputBinding.a) {
            return false;
        }
        return true;
    }
}
