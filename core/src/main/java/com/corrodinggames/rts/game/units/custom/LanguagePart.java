package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bc */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bc.class */
public class LanguagePart {

    /* JADX INFO: renamed from: a */
    public String locale;

    /* JADX INFO: renamed from: b */
    public String text;

    public LanguagePart() {
    }

    public LanguagePart(String str, String str2) {
        this.locale = str;
        this.text = str2;
    }

    public void a(String str, String str2) {
        if (this.text != null) {
            this.text = this.text.replaceAll(str, str2);
        }
    }
}
