package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ad */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ad.class */
public class KeyBinding {
    public String a;
    public boolean b = false;
    public ArrayList<InputBinding> c = new ArrayList();
    public ArrayList d = new ArrayList();

    public boolean a() {
        for (InputBinding inputBinding : this.c) {
            if (inputBinding.a == -1 && inputBinding.a()) {
                return true;
            }
        }
        return false;
    }

    public boolean b() {
        for (InputBinding inputBinding : this.c) {
            if (inputBinding != null && inputBinding.a == -1 && inputBinding.b()) {
                return true;
            }
        }
        return false;
    }

    public String c() {
        for (InputBinding inputBinding : this.c) {
            if (inputBinding != null) {
                return inputBinding.c().toUpperCase();
            }
        }
        return VariableScope.nullOrMissingString;
    }

    public InputBinding a(int i) {
        if (this.c.size() > i) {
            return (InputBinding) this.c.get(i);
        }
        return null;
    }

    public String b(int i) {
        if (this.c.size() > i) {
            InputBinding inputBinding = (InputBinding) this.c.get(i);
            if (inputBinding == null) {
                return "<null>";
            }
            return inputBinding.c().toUpperCase();
        }
        return VariableScope.nullOrMissingString;
    }

    public KeyBinding c(int i) {
        return a(i, 0, 0, false);
    }

    public KeyBinding a(int i, int i2, int i3, boolean z) {
        KeyboardBinding keyboardBinding = new KeyboardBinding();
        keyboardBinding.e = i;
        keyboardBinding.a = -1;
        keyboardBinding.b = i3;
        if (z) {
            keyboardBinding.d = true;
        }
        if (this.c.size() <= i2) {
            this.c.add(new NullInputBinding());
        }
        if (this.c.size() <= i2) {
            this.c.add(new NullInputBinding());
        }
        this.c.set(i2, keyboardBinding);
        return this;
    }

    public KeyBinding a(String str) {
        return a(str, -1);
    }

    public KeyBinding a(String str, int i) {
        if (str == null) {
            throw new RuntimeException("key==null");
        }
        return a(-1, str, i);
    }

    public KeyBinding a(int i, String str, int i2) {
        if (str == null) {
            throw new RuntimeException("key==null");
        }
        KeyboardBinding keyboardBinding = new KeyboardBinding();
        keyboardBinding.a = i;
        keyboardBinding.b = 0;
        String lowerCase = str.toLowerCase(Locale.ENGLISH);
        if (lowerCase.contains("alt+")) {
            lowerCase = lowerCase.replace("alt+", VariableScope.nullOrMissingString);
            keyboardBinding.b += 4;
        }
        if (lowerCase.contains("ctrl+")) {
            lowerCase = lowerCase.replace("ctrl+", VariableScope.nullOrMissingString);
            keyboardBinding.b++;
        }
        if (lowerCase.contains("shift+")) {
            lowerCase = lowerCase.replace("shift+", VariableScope.nullOrMissingString);
            keyboardBinding.b += 2;
        }
        try {
            keyboardBinding.e = InputController.d(lowerCase);
            if (i2 == -1) {
                this.c.add(keyboardBinding);
            } else {
                if (this.c.size() <= i2) {
                    this.c.add(new NullInputBinding());
                }
                if (this.c.size() <= i2) {
                    this.c.add(new NullInputBinding());
                }
                this.c.set(i2, keyboardBinding);
            }
        } catch (SlickToAndroidKeycodes.MissingKey e) {
            e.printStackTrace();
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine != null) {
                gameEngine.alert(e.getMessage(), 1);
            }
        }
        return this;
    }

    public KeyBinding b(int i, int i2, int i3, boolean z) {
        ControllerBinding controllerBinding = new ControllerBinding();
        controllerBinding.a = i;
        controllerBinding.e = i2;
        controllerBinding.f = i3;
        controllerBinding.g = z;
        try {
            controllerBinding.i = controllerBinding.a(true);
            this.c.add(controllerBinding);
            return this;
        } catch (IndexOutOfBoundsException e) {
            GameEngine.updatePaintTextSizeIfNeeded("Failed to bind Axis:" + i3 + " on joystick:" + i2);
            return this;
        }
    }

    public boolean d() {
        return false;
    }

    public String e() {
        return this.a.replace("-", VariableScope.nullOrMissingString).replace("  ", " ").replace("  ", " ").replace(" ", "_").toLowerCase(Locale.ENGLISH);
    }
}
