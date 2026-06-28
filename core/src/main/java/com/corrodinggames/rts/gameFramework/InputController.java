package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ac */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ac.class */
public class InputController {
    public static AbstractC0003aj a = new DefaultInputHandler();
    public static InputHandler b = new InputHandler();
    public KeyBinding c;
    public KeyBinding d;
    public KeyBinding e;
    public KeyBinding f;
    public KeyBinding g;
    public KeyBinding h;
    public KeyBinding i;
    public KeyBinding j;
    public KeyBinding k;
    public KeyBinding l = a("Debug Left");
    public KeyBinding m = a("Debug Right");
    public KeyBinding n = b("Camera Up");
    public KeyBinding o = b("Camera Down");
    public KeyBinding p = b("Camera Left");
    public KeyBinding q = b("Camera Right");
    public KeyBinding r = b("Zoom In");
    public KeyBinding s = b("Zoom Out");
    public KeyBinding t = b("Send Chat");
    public KeyBinding u = b("Send Team Chat");
    public KeyBinding v = b("Ping Map");
    public KeyBinding w = b("Show Menu");
    public KeyBinding x = b("Save Game");
    public KeyBinding y = b("Deselect units");
    public KeyBinding z = b("Go to notification");
    public KeyBinding A = b("Select Whole Army");
    public KeyBinding B = b("Select Command Center");
    public KeyBinding C = b("Cycle Builders");
    public KeyBinding D = b("Cycle Extractors");
    public KeyBinding E = b("Cycle Upgradable Fabricators");
    public KeyBinding F = b("Cycle Land Factories");
    public KeyBinding G = b("Cycle Air Factories");
    public KeyBinding H = b("Next Music Track");
    public AlwaysActiveKeyBinding I = c("Game Speed (Single player)");
    public KeyBinding J = b("Slower");
    public KeyBinding K = b("Faster");
    public KeyBinding L = b("Pause Game");
    public AlwaysActiveKeyBinding M = c("Unit Actions");
    public KeyBinding N = b("Attack Move");
    public KeyBinding O = b("Stop");
    public KeyBinding P = b("Guard Unit");
    public KeyBinding Q = b("Patrol");
    public KeyBinding R = b("Reclaim");
    public KeyBinding S = b("Action - Upgrade");
    public KeyBinding T = b("Action - Set Rally");
    public KeyBinding U = a("Debug Editor");
    public KeyBinding V = a("Debug Pause");
    public KeyBinding W = a("Debug Slow");
    public KeyBinding X = a("Debug HideInterface");
    public KeyBinding Y = a("Debug HideInterface Temp");
    public KeyBinding Z = a("Debug InvincibleUnits");
    public KeyBinding aa = a("debugPrintSelectedUnit");
    public KeyBinding ab = a("debugDevModeSwitch");
    public KeyBinding ac = a("debugAIViewSwitch");
    public KeyBinding ad = a("debugMapSwitch");
    public KeyBinding ae = a("Debug Take Screenshot");
    public KeyBinding af = a("Debug Take Screenshot High");
    public KeyBinding[] ag;
    public AlwaysActiveKeyBinding ah;
    public KeyBinding[] ai;
    public KeyBinding[] aj;
    public KeyBinding[] ak;
    public ArrayList<KeyBinding> al;
    Properties am;
    int an;
    int ao;

    public InputController() {
        this.n.a("UP").a("NUMPAD8");
        this.o.a("DOWN").a("NUMPAD2");
        this.p.a("LEFT").a("NUMPAD4");
        this.q.a("RIGHT").a("NUMPAD6");
        this.l.a("F5");
        this.m.a("F6");
        this.x.a("CTRL+S");
        this.t.a("ENTER").a("T");
        this.u.a("SHIFT+ENTER").a("Y");
        this.v.a("CTRL+M").a("CTRL+P");
        this.w.a("ESCAPE").a("F10");
        this.y.a("SPACE");
        this.z.a("CTRL+SPACE");
        this.A.a("CTRL+A");
        this.C.a("CTRL+B");
        this.D.a("CTRL+E");
        this.E.a("CTRL+F");
        this.F.a("CTRL+L");
        this.G.a("CTRL+K");
        this.B.a("CTRL+C");
        this.H.a("CTRL+N");
        this.N.a("A");
        this.L.a("BREAK");
        this.O.a("S");
        this.P.a("G");
        this.Q.a("P");
        this.S.a("U");
        this.T.a("R");
        this.U.a("CTRL+SHIFT+E");
        this.V.a("CTRL+SHIFT+P");
        this.W.a("CTRL+SHIFT+S");
        this.X.a("CTRL+SHIFT+H");
        this.Y.a("CTRL+H");
        this.Z.a("CTRL+SHIFT+I");
        this.aa.a("CTRL+SHIFT+L");
        this.ab.a("CTRL+SHIFT+D");
        this.ac.a("SHIFT+F3");
        this.ad.a("SHIFT+F4");
        this.ae.a("CTRL+SHIFT+ALT+S");
        this.af.a("CTRL+SHIFT+ALT+D");
        this.J.a("minus").a("NUMPADSUBTRACT");
        this.K.a("equals").a("NUMPADADD");
        int[] iArr = {54, 52, 31, 50, 30, 42, 41, 38, 39, 40, 37, 43};
        this.ag = new KeyBinding[10];
        for (int i = 0; i < this.ag.length; i++) {
            this.ag[i] = b("unit action " + (i + 1));
            this.ag[i].c(iArr[i]);
        }
        this.ah = c("Unit Groups");
        this.ak = new KeyBinding[10];
        int i2 = 0;
        while (i2 < this.ak.length) {
            this.ak[i2] = b("create group " + (i2 + 1));
            this.ak[i2].a(a(i2 == 9 ? 0 : i2 + 1), 0, 1, false);
            i2++;
        }
        this.ai = new KeyBinding[10];
        int i3 = 0;
        while (i3 < this.ai.length) {
            this.ai[i3] = b("select group " + (i3 + 1));
            this.ai[i3].c(a(i3 == 9 ? 0 : i3 + 1));
            i3++;
        }
        this.aj = new KeyBinding[10];
        int i4 = 0;
        while (i4 < this.aj.length) {
            this.aj[i4] = b("Add group to selection " + (i4 + 1));
            this.aj[i4].a(a(i4 == 9 ? 0 : i4 + 1), 0, 2, false);
            i4++;
        }
        this.am = new Properties();
        this.an = 0;
        this.ao = 0;
    }

    public int a(int i) {
        if (i >= 10) {
            throw new RuntimeException("number:" + i + " too high");
        }
        if (i == 0) {
            return 7;
        }
        return 8 + (i - 1);
    }

    public KeyBinding a(String str) {
        if (this.al == null) {
            this.al = new ArrayList();
        }
        KeyBinding keyBinding = new KeyBinding();
        keyBinding.a = str;
        keyBinding.b = false;
        this.al.add(keyBinding);
        return keyBinding;
    }

    public KeyBinding b(String str) {
        if (this.al == null) {
            this.al = new ArrayList();
        }
        KeyBinding keyBinding = new KeyBinding();
        keyBinding.a = str;
        keyBinding.b = true;
        this.al.add(keyBinding);
        return keyBinding;
    }

    public AlwaysActiveKeyBinding c(String str) {
        if (this.al == null) {
            this.al = new ArrayList();
        }
        AlwaysActiveKeyBinding alwaysActiveKeyBinding = new AlwaysActiveKeyBinding();
        alwaysActiveKeyBinding.a = str;
        alwaysActiveKeyBinding.b = true;
        this.al.add(alwaysActiveKeyBinding);
        return alwaysActiveKeyBinding;
    }

    public void a(String str, String str2) {
        String strTrim = str.toLowerCase(Locale.ENGLISH).trim();
        KeyBinding keyBinding = null;
        for (KeyBinding keyBinding2 : this.al) {
            if (keyBinding2.a != null && keyBinding2.e().equals(strTrim)) {
                keyBinding = keyBinding2;
            }
        }
        if (keyBinding == null) {
            GameEngine.logColored("loadKey: could not find:" + strTrim);
            return;
        }
        String[] strArrSplit = str2.split(",");
        for (int i = 0; i <= 1 && i < strArrSplit.length; i++) {
            String str3 = strArrSplit[i];
            if (!str3.equalsIgnoreCase("DEFAULT")) {
                keyBinding.a(str3, i);
                if (keyBinding.c.size() > i && keyBinding.c.get(i) != null) {
                    ((InputBinding) keyBinding.c.get(i)).d = true;
                } else {
                    GameEngine.logWarningAndStack("out of range");
                }
            }
        }
    }

    public String a(KeyBinding keyBinding) {
        String str = VariableScope.nullOrMissingString;
        boolean z = true;
        for (InputBinding inputBinding : keyBinding.c) {
            if (z) {
                z = false;
            } else {
                str = str + ",";
            }
            if (inputBinding.d) {
                if (inputBinding.d()) {
                    str = str + "CLEARED";
                } else {
                    str = str + inputBinding.c();
                }
            } else {
                str = str + "DEFAULT";
            }
        }
        return str;
    }

    public boolean a(KeyBinding keyBinding, int i) {
        GameEngine gameEngine = GameEngine.getInstance();
        InputBinding inputBindingA = keyBinding.a(i);
        if (inputBindingA == null) {
            return false;
        }
        ArrayList arrayList = gameEngine.inputController.al;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            KeyBinding keyBinding2 = (KeyBinding) arrayList.get(i2);
            if (keyBinding2 != keyBinding) {
                Iterator it = keyBinding2.c.iterator();
                while (it.hasNext()) {
                    if (inputBindingA.a((InputBinding) it.next())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void a() {
        this.c = a("shoot");
        this.d = a("move up");
        this.e = a("move down");
        this.f = a("move left");
        this.g = a("move right");
        this.h = a("aim up");
        this.i = a("aim down");
        this.j = a("aim left");
        this.k = a("aim right");
        this.c.a(0, "enter", -1);
        this.c.a(0, "space", -1);
        this.d.a(0, "w", -1);
        this.e.a(0, "s", -1);
        this.f.a(0, "a", -1);
        this.g.a(0, "d", -1);
        this.h.a(0, "UP", -1);
        this.i.a(0, "DOWN", -1);
        this.j.a(0, "LEFT", -1);
        this.k.a(0, "RIGHT", -1);
        GameEngine.log("getControllerCount:" + b.a());
        this.d.b(0, 1, 0, true);
        this.e.b(0, 1, 0, false);
        this.f.b(0, 1, 1, true);
        this.g.b(0, 1, 1, false);
        this.h.b(0, 1, 2, true);
        this.i.b(0, 1, 2, false);
        this.j.b(0, 1, 3, true);
        this.k.b(0, 1, 3, false);
        this.c.b(0, 1, 4, true);
    }

    public void b() {
        if (b.a() != this.ao) {
            this.ao = b.a();
            GameEngine.log("Number of controllers changed, now:" + this.ao);
        }
    }

    public static int d(String str) throws SlickToAndroidKeycodes.MissingKey {
        if (str.equalsIgnoreCase("CLEARED")) {
            return 0;
        }
        return SlickToAndroidKeycodes.getAndroidKeyCode(str);
    }
}
