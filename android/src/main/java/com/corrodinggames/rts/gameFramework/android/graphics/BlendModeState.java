package com.corrodinggames.rts.gameFramework.android.graphics;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ae */
/* JADX INFO: loaded from: classes.dex */
public final class BlendModeState {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int[] f556a = new int[8];
    public int b = 0;

    public final void a() {
        this.b = 0;
        if (this.f556a.length != 8) {
            this.f556a = new int[8];
        }
    }
}
