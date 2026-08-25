package com.corrodinggames.rts.gameFramework.ui.widgets;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/b.class */
public class MenuButton extends TextLabel {
    public MenuButton() {
        this.c = UIStyle.j;
    }

    @Override
    // com.corrodinggames.rts.gameFramework.ui.widgets.TextLabel, com.corrodinggames.rts.gameFramework.ui.widgets.UIElement
    public void a(float f, float f2) {
        if (this.isHovered) {
            this.c = UIStyle.k;
        } else {
            this.c = UIStyle.j;
        }
        super.a(f, f2);
    }
}
