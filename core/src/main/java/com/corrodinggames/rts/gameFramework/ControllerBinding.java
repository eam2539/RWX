package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ah */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ah.class */
public class ControllerBinding extends InputBinding {
    /* JADX INFO: renamed from: g */
    boolean invertAxis;
    /* JADX INFO: renamed from: i */
    float lastAxisValue;
    /* JADX INFO: renamed from: e */
    int controllerId = -1;
    /* JADX INFO: renamed from: f */
    int axisId = -1;
    /* JADX INFO: renamed from: h */
    int buttonId = -1;
    /* JADX INFO: renamed from: j */
    boolean hasBeenActivated = false;

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public boolean a() {
        if (b()) {
            if (!this.isPressed) {
                this.isPressed = true;
                return true;
            }
            return false;
        }
        if (this.isPressed) {
            this.isPressed = false;
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
        if (this.buttonId != -1) {
            f = InputController.b.a(this.buttonId, this.controllerId) ? 0.0f : 1.0f;
        } else {
            float fB = InputController.b.b(this.controllerId, this.axisId);
            f = this.invertAxis ? -fB : fB;
        }
        if (z) {
            return f;
        }
        if (!this.hasBeenActivated && Math.abs(f - this.lastAxisValue) > 0.001f) {
            this.hasBeenActivated = true;
        }
        if (!this.hasBeenActivated) {
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
