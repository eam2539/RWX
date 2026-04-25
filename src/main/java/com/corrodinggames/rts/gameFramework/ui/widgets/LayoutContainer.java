package com.corrodinggames.rts.gameFramework.ui.widgets;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/g.class */
public class LayoutContainer extends UIElement {
    public LayoutContainer() {
    }

    public LayoutContainer(LayoutDirection layoutDirection) {
        this.x = layoutDirection;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIElement
    public void a(float f, float f2) {
        super.a(f, f2);
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIElement
    public void b() {
        super.b();
        d();
        this.i = this.z;
        this.j = this.y;
        this.i += this.m + this.n;
        this.j += this.k + this.l;
    }
}
