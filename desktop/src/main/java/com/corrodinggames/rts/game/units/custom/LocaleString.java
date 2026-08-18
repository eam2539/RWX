package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.local.Locale;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bb */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bb.class */
public class LocaleString {

    /* JADX INFO: renamed from: a */
    public static final LocaleString EMPTY = fromRawText(VariableScope.nullOrMissingString);

    /* JADX INFO: renamed from: b */
    public LanguagePart[] localizedItems;

    /* JADX INFO: renamed from: c */
    public String cachedText;

    /* JADX INFO: renamed from: d */
    public int cachedReloadCount = -1;

    /* JADX INFO: renamed from: e */
    public String text;

    /* JADX INFO: renamed from: a */
    public static LocaleString fromRawText(String str) {
        LocaleString localeString = new LocaleString();
        ArrayList arrayList = new ArrayList();
        LanguagePart languagePart = new LanguagePart();
        languagePart.locale = null;
        languagePart.text = str;
        arrayList.add(languagePart);
        localeString.localizedItems = (LanguagePart[]) arrayList.toArray(new LanguagePart[0]);
        localeString.resolveText();
        return localeString;
    }

    /* JADX INFO: renamed from: b */
    public static LocaleString wrapLocaleKey(String str) {
        LocaleString localeString = new LocaleString();
        localeString.text = str;
        localeString.resolveText();
        return localeString;
    }

    public LocaleString() {
    }

    public LocaleString(LanguagePart[] languagePartArr) {
        this.localizedItems = languagePartArr;
    }

    /* JADX INFO: renamed from: a */
    public boolean isEmpty() {
        if (this.localizedItems != null) {
            for (LanguagePart languagePart : this.localizedItems) {
                if (languagePart.text != null && !VariableScope.nullOrMissingString.equals(languagePart.text)) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    /* JADX INFO: renamed from: a */
    public void replaceAll(String str, String str2) {
        if (this.localizedItems != null) {
            for (LanguagePart languagePart : this.localizedItems) {
                languagePart.a(str, str2);
            }
        } else {
            GameEngine.logColored("LocaleString: replaceAll with null strings");
        }
        this.cachedReloadCount = -1;
    }

    /* JADX INFO: renamed from: b */
    public String resolveText() {
        if (this.cachedReloadCount == Locale.reloadCount) {
            return this.cachedText;
        }
        if (this.text != null) {
            this.cachedReloadCount = Locale.reloadCount;
            this.cachedText = Locale.get(this.text, new Object[0]);
            return this.cachedText;
        }
        String language = Locale.getLanguage();
        for (LanguagePart languagePart : this.localizedItems) {
            if (language.equals(languagePart.locale)) {
                this.cachedReloadCount = Locale.reloadCount;
                this.cachedText = languagePart.text;
                return this.cachedText;
            }
        }
        for (LanguagePart languagePart2 : this.localizedItems) {
            if (languagePart2.locale == null) {
                this.cachedReloadCount = Locale.reloadCount;
                this.cachedText = languagePart2.text;
                return this.cachedText;
            }
        }
        this.cachedReloadCount = Locale.reloadCount;
        this.cachedText = "<NO DEFAULT TEXT FOUND>";
        return this.cachedText;
    }
}
