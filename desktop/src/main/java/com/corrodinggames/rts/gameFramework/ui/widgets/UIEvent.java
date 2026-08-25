package com.corrodinggames.rts.gameFramework.ui.widgets;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/c.class */
public class UIEvent {
    /* JADX INFO: renamed from: a */
    public int x;

    /* JADX INFO: renamed from: b */
    public int y;

    /* JADX INFO: renamed from: c */
    public UIEventType type;

    /* JADX INFO: renamed from: d */
    public int button = -1;

    public static UIEvent a(int i, int i2) {
        UIEvent uIEvent = new UIEvent();
        uIEvent.x = i;
        uIEvent.y = i2;
        uIEvent.type = UIEventType.mouseClick;
        uIEvent.button = 1;
        return uIEvent;
    }

    public static UIEvent b(int i, int i2) {
        UIEvent uIEvent = new UIEvent();
        uIEvent.x = i;
        uIEvent.y = i2;
        uIEvent.type = UIEventType.mouseMove;
        uIEvent.button = 1;
        return uIEvent;
    }

    public boolean a() {
        return this.type == UIEventType.mouseClick;
    }

    public boolean b() {
        return this.type == UIEventType.mouseMove;
    }
}
