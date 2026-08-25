package com.corrodinggames.rts.gameFramework.ui.widgets;

import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import io.github.rwx.render.canvas.KoolArgbColor;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/f.class */
public class MenuDialog extends PopupWindow {
    /* JADX INFO: renamed from: a */
    LayoutContainer layoutContainer;

    public static MenuDialog a(String str, boolean z) {
        MenuDialog menuDialog = new MenuDialog();
        menuDialog.b = UIStyle.n;
        menuDialog.i = 200.0f;
        menuDialog.j = 200.0f;
        TextLabel textLabel = new TextLabel();
        textLabel.a(str);
        textLabel.e(5.0f);
        textLabel.f(5.0f);
        textLabel.a(-1);
        menuDialog.a(textLabel);
        menuDialog.layoutContainer = new LayoutContainer(LayoutDirection.horizontal);
        menuDialog.a(menuDialog.layoutContainer);
        if (z) {
            menuDialog.b(Locale.get("menus.common.cancel")).a(new UIEventHandler() { // from class: com.corrodinggames.rts.gameFramework.f.a.f.1
                @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIEventHandler
                public boolean a(UIEvent uIEvent) {
                    menuDialog.i();
                    return true;
                }
            });
        }
        return menuDialog;
    }

    public MenuButton a(String str) {
        MenuButton menuButton = new MenuButton();
        menuButton.a(str);
        menuButton.e(5.0f);
        menuButton.f(5.0f);
        menuButton.a(KoolArgbColor.a(255, 30, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_SATELLITE_SERVICE, 30));
        return menuButton;
    }

    public MenuButton b(String str) {
        return a(str, (UIEventHandler) null);
    }

    public MenuButton a(String str, UIEventHandler uIEventHandler) {
        MenuButton menuButtonA = a(str);
        menuButtonA.a(uIEventHandler);
        this.layoutContainer.a(menuButtonA);
        return menuButtonA;
    }

    public void u_() {
        if (!this.s) {
            return;
        }
        b();
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIElement
    public void b() {
        super.b();
        d();
        this.i = this.layoutWidth;
        this.j = this.layoutHeight;
        this.i += this.m + this.n;
        this.j += this.k + this.l;
    }
}
