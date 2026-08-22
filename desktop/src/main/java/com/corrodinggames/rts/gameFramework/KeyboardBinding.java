package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ag */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ag.class */
public class KeyboardBinding extends InputBinding {
    /* JADX INFO: renamed from: e */
    int keyCode;

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public boolean a(InputBinding inputBinding) {
        if (!(inputBinding instanceof KeyboardBinding) || this.keyCode != ((KeyboardBinding) inputBinding).keyCode) {
            return false;
        }
        return super.a(inputBinding);
    }

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public boolean a() {
        if (InputController.b.a(this.keyCode, this.modifierFlags, false)) {
            if (!this.isPressed) {
                this.isPressed = true;
                return true;
            }
            return false;
        }
        if (InputController.b.a(this.keyCode, this.modifierFlags, true)) {
            this.isPressed = true;
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
        return InputController.b.a(this.keyCode, this.modifierFlags, false);
    }

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public String c() {
        if (this.keyCode == 0) {
            return VariableScope.nullOrMissingString;
        }
        return InputController.b.c(this.keyCode, this.modifierFlags);
    }

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public boolean d() {
        if (this.keyCode == 0) {
            return true;
        }
        return false;
    }
}
