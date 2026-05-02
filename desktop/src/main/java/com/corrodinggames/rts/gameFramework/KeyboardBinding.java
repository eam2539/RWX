package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ag */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ag.class */
public class KeyboardBinding extends InputBinding {
    int e;

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public boolean a(InputBinding inputBinding) {
        if (!(inputBinding instanceof KeyboardBinding) || this.e != ((KeyboardBinding) inputBinding).e) {
            return false;
        }
        return super.a(inputBinding);
    }

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public boolean a() {
        if (InputController.b.a(this.e, this.b, false)) {
            if (!this.c) {
                this.c = true;
                return true;
            }
            return false;
        }
        if (InputController.b.a(this.e, this.b, true)) {
            this.c = true;
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
        return InputController.b.a(this.e, this.b, false);
    }

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public String c() {
        if (this.e == 0) {
            return VariableScope.nullOrMissingString;
        }
        return InputController.b.c(this.e, this.b);
    }

    @Override // com.corrodinggames.rts.gameFramework.InputBinding
    public boolean d() {
        if (this.e == 0) {
            return true;
        }
        return false;
    }
}
