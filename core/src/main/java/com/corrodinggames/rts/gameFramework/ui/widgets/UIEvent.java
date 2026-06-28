package com.corrodinggames.rts.gameFramework.ui.widgets;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/c.class */
public class UIEvent {
    public int a;
    public int b;
    public UIEventType c;
    public int d = -1;

    public static UIEvent a(int i, int i2) {
        UIEvent uIEvent = new UIEvent();
        uIEvent.a = i;
        uIEvent.b = i2;
        uIEvent.c = UIEventType.mouseClick;
        uIEvent.d = 1;
        return uIEvent;
    }

    public static UIEvent b(int i, int i2) {
        UIEvent uIEvent = new UIEvent();
        uIEvent.a = i;
        uIEvent.b = i2;
        uIEvent.c = UIEventType.mouseMove;
        uIEvent.d = 1;
        return uIEvent;
    }

    public boolean a() {
        return this.c == UIEventType.mouseClick;
    }

    public boolean b() {
        return this.c == UIEventType.mouseMove;
    }
}
