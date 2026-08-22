package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ad */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ad.class */
public class KeyBinding {

    /* JADX INFO: renamed from: a */
    public String name;

    /* JADX INFO: renamed from: b */
    public boolean isDefault = false;

    /* JADX INFO: renamed from: c */
    public ArrayList<InputBinding> bindings = new ArrayList();

    public ArrayList d = new ArrayList();

    public boolean a() {
        for (InputBinding inputBinding : this.bindings) {
            if (inputBinding.a == -1 && inputBinding.a()) {
                return true;
            }
        }
        return false;
    }

    public boolean b() {
        for (InputBinding inputBinding : this.bindings) {
            if (inputBinding != null && inputBinding.a == -1 && inputBinding.b()) {
                return true;
            }
        }
        return false;
    }

    public String c() {
        for (InputBinding inputBinding : this.bindings) {
            if (inputBinding != null) {
                return inputBinding.c().toUpperCase();
            }
        }
        return VariableScope.nullOrMissingString;
    }

    public InputBinding a(int i) {
        if (this.bindings.size() > i) {
            return (InputBinding) this.bindings.get(i);
        }
        return null;
    }

    public String b(int i) {
        if (this.bindings.size() > i) {
            InputBinding inputBinding = (InputBinding) this.bindings.get(i);
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
        keyboardBinding.keyCode = i;
        keyboardBinding.a = -1;
        keyboardBinding.modifierFlags = i3;
        if (z) {
            keyboardBinding.isUserDefined = true;
        }
        if (this.bindings.size() <= i2) {
            this.bindings.add(new NullInputBinding());
        }
        if (this.bindings.size() <= i2) {
            this.bindings.add(new NullInputBinding());
        }
        this.bindings.set(i2, keyboardBinding);
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
        keyboardBinding.modifierFlags = 0;
        String lowerCase = str.toLowerCase(Locale.ENGLISH);
        if (lowerCase.contains("alt+")) {
            lowerCase = lowerCase.replace("alt+", VariableScope.nullOrMissingString);
            keyboardBinding.modifierFlags += 4;
        }
        if (lowerCase.contains("ctrl+")) {
            lowerCase = lowerCase.replace("ctrl+", VariableScope.nullOrMissingString);
            keyboardBinding.modifierFlags++;
        }
        if (lowerCase.contains("shift+")) {
            lowerCase = lowerCase.replace("shift+", VariableScope.nullOrMissingString);
            keyboardBinding.modifierFlags += 2;
        }
        try {
            keyboardBinding.keyCode = InputController.d(lowerCase);
            if (i2 == -1) {
                this.bindings.add(keyboardBinding);
            } else {
                if (this.bindings.size() <= i2) {
                    this.bindings.add(new NullInputBinding());
                }
                if (this.bindings.size() <= i2) {
                    this.bindings.add(new NullInputBinding());
                }
                this.bindings.set(i2, keyboardBinding);
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
        controllerBinding.controllerId = i2;
        controllerBinding.axisId = i3;
        controllerBinding.invertAxis = z;
        try {
            controllerBinding.lastAxisValue = controllerBinding.a(true);
            this.bindings.add(controllerBinding);
            return this;
        } catch (IndexOutOfBoundsException e) {
            GameEngine.logColored("Failed to bind Axis:" + i3 + " on joystick:" + i2);
            return this;
        }
    }

    public boolean d() {
        return false;
    }

    public String e() {
        return this.name.replace("-", VariableScope.nullOrMissingString).replace("  ", " ").replace("  ", " ").replace(" ", "_").toLowerCase(Locale.ENGLISH);
    }
}
