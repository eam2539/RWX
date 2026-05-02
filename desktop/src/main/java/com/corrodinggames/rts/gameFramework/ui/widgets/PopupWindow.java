package com.corrodinggames.rts.gameFramework.ui.widgets;

import android.graphics.RectF;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/n.class */
public class PopupWindow extends UIElement {
    UIStyle b = UIStyle.j;

    @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIElement
    public void a(float f, float f2) {
        super.a(f, f2);
        this.b.a(d(), a(new RectF(), f, f2));
    }
}
