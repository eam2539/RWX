package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.local.Locale;

import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.aj */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/aj.class */
public class LocalizedText {

    /* JADX INFO: renamed from: a */
    public static final LocalizedText EMPTY_INSTANCE = a(VariableScope.nullOrMissingString);

    /* JADX INFO: renamed from: b */
    public LocalePart[] parsedParts;

    /* JADX INFO: renamed from: c */
    public LanguagePart[] localizedParts;

    /* JADX INFO: renamed from: d */
    public String cachedString;

    /* JADX INFO: renamed from: e */
    public int cacheState = -1;

    /* JADX INFO: renamed from: f */
    public String formatString;

    /* JADX INFO: renamed from: g */
    public String errorMessage;

    /* JADX INFO: renamed from: h */
    CustomUnitConfig customUnitConfig;

    public static LocalizedText a(String str) {
        LocalizedText localizedText = new LocalizedText();
        ArrayList arrayList = new ArrayList();
        LanguagePart languagePart = new LanguagePart();
        languagePart.locale = null;
        languagePart.text = str;
        arrayList.add(languagePart);
        localizedText.localizedParts = (LanguagePart[]) arrayList.toArray(new LanguagePart[0]);
        localizedText.a();
        return localizedText;
    }

    public static LocalizedText a(LocaleString localeString) {
        if (localeString == null) {
            return null;
        }
        LocalizedText localizedText = new LocalizedText();
        localizedText.customUnitConfig = CustomUnitConfig.instance;
        localizedText.localizedParts = localeString.localizedItems;
        localizedText.formatString = localeString.text;
        if (localizedText.localizedParts != null) {
            for (LanguagePart languagePart : localizedText.localizedParts) {
                if (languagePart.text == null || languagePart.text.contains("%{")) {
                }
            }
        }
        localizedText.c();
        return localizedText;
    }

    public LocalizedText() {
    }

    public LocalizedText(CustomUnitConfig customUnitConfig, LocaleString localeString) {
        this.customUnitConfig = customUnitConfig;
        this.localizedParts = localeString.localizedItems;
        this.formatString = localeString.text;
        if (this.localizedParts != null) {
            for (LanguagePart languagePart : this.localizedParts) {
                if (languagePart.text != null && languagePart.text.contains("%{")) {
                    a(languagePart.text, true);
                }
            }
        }
        a();
    }

    public void a() {
        a(true);
    }

    public void a(boolean z) {
        c();
        if (this.cachedString != null && this.cachedString.contains("%{")) {
            this.parsedParts = a(this.cachedString, z);
        } else {
            this.parsedParts = null;
        }
    }


    public LocalePart[] a(String string, boolean boolean2) {
        ArrayList<LocalePart> var3 = new ArrayList();
        int var4 = 0;
        boolean var5 = false;

        while (true) {
            if (!var5) {
                int var11 = string.indexOf("%{", var4);
                if (var11 == -1) {
                    String var13 = string.substring(var4, string.length());
                    if (!var13.equals("")) {
                        var3.add(new TextFormatPart(var13));
                    }
                    break;
                }

                String var12 = string.substring(var4, var11);
                if (!var12.equals("")) {
                    var3.add(new TextFormatPart(var12));
                }

                var5 = true;
                var4 = var11 + 2;
            } else {
                int var6 = string.indexOf("}", var4);
                if (var6 == -1) {
                    var3.add(new TextFormatPart("< %{ NOT CLOSED >"));
                    break;
                }

                String var7 = string.substring(var4, var6);

                try {
                    LogicBoolean var8 = LogicBooleanLoader.parseBooleanBlock(this.customUnitConfig, var7, false);
                    var3.add(LogicFormatPart.a(var8));
                } catch (RuntimeException var10) {
                    String var9 = "Error: " + var10.getMessage() + ", [parsing: '" + var7 + "']";
                    this.errorMessage = var9;
                    var3.add(new TextFormatPart("Error:< " + var9 + " >"));
                    if (boolean2) {
                        throw var10;
                    }
                }

                var4 = var6 + 1;
                var5 = false;
            }
        }

        return var3.toArray(new LocalePart[0]);
    }

    public String a(BaseUnit baseUnit) {
        if (!(baseUnit instanceof OrderableUnit)) {
            return "<No unit>:" + this.cachedString;
        }
        OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
        StringBuffer stringBuffer = new StringBuffer();
        for (LocalePart localePart : this.parsedParts) {
            stringBuffer.append(localePart.a(orderableUnit));
        }
        return stringBuffer.toString();
    }

    public String b(BaseUnit baseUnit) {
        if (this.cacheState == Locale.reloadCount) {
            if (this.parsedParts != null) {
                return a(baseUnit);
            }
            return this.cachedString;
        }
        a(false);
        if (this.parsedParts != null) {
            return a(baseUnit);
        }
        return this.cachedString;
    }

    public String b() {
        if (this.cacheState == Locale.reloadCount) {
            return this.cachedString;
        }
        a(false);
        return this.cachedString;
    }

    public void c() {
        if (this.formatString != null) {
            this.cacheState = Locale.reloadCount;
            this.cachedString = Locale.get(this.formatString, new Object[0]);
            return;
        }
        String language = Locale.getLanguage();
        for (LanguagePart languagePart : this.localizedParts) {
            if (language.equals(languagePart.locale)) {
                this.cacheState = Locale.reloadCount;
                this.cachedString = languagePart.text;
                return;
            }
        }
        for (LanguagePart languagePart2 : this.localizedParts) {
            if (languagePart2.locale == null) {
                this.cacheState = Locale.reloadCount;
                this.cachedString = languagePart2.text;
                return;
            }
        }
        this.cacheState = Locale.reloadCount;
        this.cachedString = "<NO DEFAULT TEXT FOUND>";
    }
}
