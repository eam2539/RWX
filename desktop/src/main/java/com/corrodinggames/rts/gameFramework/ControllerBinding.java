package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ah */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ah.class */
public class ControllerBinding extends InputBinding {
    boolean g;
    float i;
    int e = -1;
    int f = -1;
    int h = -1;
    boolean j = false;

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public boolean a() {
        if (b()) {
            if (!this.c) {
                this.c = true;
                return true;
            }
            return false;
        }
        if (this.c) {
            this.c = false;
            return false;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public boolean b() {
        return e() > 0.5f;
    }

    public float e() {
        return a(false);
    }

    public float a(boolean z) {
        float f;
        if (this.h != -1) {
            f = InputController.b.a(this.h, this.e) ? 0.0f : 1.0f;
        } else {
            float fB = InputController.b.b(this.e, this.f);
            f = this.g ? -fB : fB;
        }
        if (z) {
            return f;
        }
        if (!this.j && Math.abs(f - this.i) > 0.001f) {
            this.j = true;
        }
        if (!this.j) {
            return 0.0f;
        }
        return f;
    }

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public String c() {
        return "controller";
    }

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public boolean d() {
        return false;
    }
}
