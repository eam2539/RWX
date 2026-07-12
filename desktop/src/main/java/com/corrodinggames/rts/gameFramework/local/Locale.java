package com.corrodinggames.rts.gameFramework.local;

import android.os.Build;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.SettingsEngine;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.h.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/h/a.class */
public final class Locale {

    /* JADX INFO: renamed from: a */
    static ResourceBundle resourceBundle;

    /* JADX INFO: renamed from: b */
    static boolean forceEnglish;

    /* JADX INFO: renamed from: d */
    public static String overrideLanguageCode;

    /* JADX INFO: renamed from: c */
    public static int reloadCount = 0;

    /* JADX INFO: renamed from: e */
    static Pattern mapNamePattern = Pattern.compile("(.*)(\\(.*\\)( *\\[by.*\\])?)");

    /* JADX INFO: renamed from: f */
    static final Pattern inlineBlockPattern = Pattern.compile("\\[i:([^\\]]*?)\\]");

    /* JADX INFO: renamed from: a */
    public static void initialize() {
        reload();
    }

    /* JADX INFO: renamed from: b */
    static ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            reload();
        }
        return resourceBundle;
    }

    /* JADX INFO: renamed from: a */
    static PropertyResourceBundle loadBundleFromPath(String str) {
        AssetInputStream assetInputStreamOpenFileByPath = FileHelper.openFileByPath("translations/" + str);
        if (assetInputStreamOpenFileByPath == null) {
            return null;
        }
        PropertyResourceBundle propertyResourceBundle = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(assetInputStreamOpenFileByPath, "UTF-8");
            propertyResourceBundle = new PropertyResourceBundle(inputStreamReader);
            inputStreamReader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return propertyResourceBundle;
    }

    /* JADX INFO: renamed from: a */
    public static String buildLocaleString(String str, java.util.Locale locale, boolean z, boolean z2) {
        String country;
        String variant;
        if (locale == java.util.Locale.ROOT) {
            return str;
        }
        String language = locale.getLanguage();
        if (z) {
            country = locale.getCountry();
        } else {
            country = VariableScope.nullOrMissingString;
        }
        if (z2) {
            variant = locale.getVariant();
        } else {
            variant = VariableScope.nullOrMissingString;
        }
        if (language.equals(VariableScope.nullOrMissingString) && country.equals(VariableScope.nullOrMissingString) && variant.equals(VariableScope.nullOrMissingString)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        sb.append('_');
        if (!variant.equals(VariableScope.nullOrMissingString)) {
            sb.append(language).append('_').append(country.toLowerCase(java.util.Locale.ROOT)).append('_').append(variant.toLowerCase(java.util.Locale.ROOT));
        } else if (!country.equals(VariableScope.nullOrMissingString)) {
            sb.append(language).append('_').append(country.toLowerCase(java.util.Locale.ROOT));
        } else {
            sb.append(language);
        }
        return sb.toString();
    }

    /* JADX INFO: renamed from: a */
    static ResourceBundle loadBundle(String str, java.util.Locale locale) {
        String str2 = buildLocaleString(str, java.util.Locale.ROOT, false, false) + ".properties";
        PropertyResourceBundle propertyResourceBundleLoadBundleFromPath = loadBundleFromPath(str2);
        if (propertyResourceBundleLoadBundleFromPath == null) {
            throw new RuntimeException("Root locate file:" + str2 + " is missing, it is required");
        }
        if (java.util.Locale.ROOT.equals(locale)) {
            GameEngine.log("Locale: Using " + str2 + " as locale");
            return propertyResourceBundleLoadBundleFromPath;
        }
        String str3 = buildLocaleString(str, locale, true, true) + ".properties";
        PropertyResourceBundle propertyResourceBundleLoadBundleFromPath2 = loadBundleFromPath(str3);
        if (propertyResourceBundleLoadBundleFromPath2 == null) {
            GameEngine.log("Locale: No locale for " + str3 + " checking locale without variant ");
            str3 = buildLocaleString(str, locale, true, false) + ".properties";
            propertyResourceBundleLoadBundleFromPath2 = loadBundleFromPath(str3);
            if (propertyResourceBundleLoadBundleFromPath2 == null) {
                GameEngine.log("Locale: No locale for " + str3 + " checking locale without variant or country");
                str3 = buildLocaleString(str, locale, false, false) + ".properties";
                propertyResourceBundleLoadBundleFromPath2 = loadBundleFromPath(str3);
                if (propertyResourceBundleLoadBundleFromPath2 == null) {
                    GameEngine.log("Locale: No locale for " + str3 + " using base locale");
                    return propertyResourceBundleLoadBundleFromPath;
                }
            }
        }
        GameEngine.log("Locale: Using " + str3 + " as locale");
        return new MergedResourceBundle(propertyResourceBundleLoadBundleFromPath2, propertyResourceBundleLoadBundleFromPath);
    }

    /* JADX INFO: renamed from: c */
    public static String getLanguage() {
        if (overrideLanguageCode != null) {
            return overrideLanguageCode;
        }
        return getDefaultLocale().getLanguage();
    }

    /* JADX INFO: renamed from: d */
    public static java.util.Locale getDefaultLocale() {
        GameEngine gameEngine = GameEngine.getInstance();
        SettingsEngine settingsEngine = null;
        if (gameEngine != null) {
            settingsEngine = gameEngine.settingsEngine;
        }
        boolean z = false;
        if (settingsEngine != null && settingsEngine.forceEnglish) {
            z = true;
        }
        if (z) {
            return java.util.Locale.ROOT;
        }
        return java.util.Locale.getDefault();
    }

    /* JADX INFO: renamed from: e */
    public static synchronized void reload() {
        reloadCount++;
        GameEngine gameEngine = GameEngine.getInstance();
        SettingsEngine settingsEngine = null;
        if (gameEngine != null) {
            settingsEngine = gameEngine.settingsEngine;
        }
        boolean z = false;
        if (settingsEngine != null && settingsEngine.forceEnglish) {
            z = true;
        }
        if (resourceBundle != null && forceEnglish == z) {
            GameEngine.log("Locale.reload: skipping reload");
        }
        if (Build.VERSION.SDK_INT >= 9) {
            ResourceBundle.clearCache();
        }
        if (z) {
            GameEngine.log("Locale: forceEnglish");
            resourceBundle = loadBundle("Strings", java.util.Locale.ROOT);
        } else if (overrideLanguageCode != null) {
            resourceBundle = loadBundle("Strings", new java.util.Locale(overrideLanguageCode));
        } else if (settingsEngine != null && settingsEngine.overrideLanguageCode != null && !settingsEngine.overrideLanguageCode.equals(VariableScope.nullOrMissingString)) {
            resourceBundle = loadBundle("Strings", new java.util.Locale(settingsEngine.overrideLanguageCode));
        } else {
            java.util.Locale locale = java.util.Locale.getDefault();
            GameEngine.log("Locale: default targetLocale:" + locale);
            if (locale != null) {
                GameEngine.log("Locale: default targetLocale ISO3:" + locale.getISO3Language());
            }
            resourceBundle = loadBundle("Strings", locale);
        }
        forceEnglish = z;
        if (0 != 0) {
        }
    }

    /* JADX INFO: renamed from: d */
    private static final String getRawString(String str) {
        try {
            String string = getResourceBundle().getString(str);
            if (string.contains("[") || string.contains("]")) {
                string = string.replace("[[", "{{").replace("]]", "}}").replace("[", "{{").replace("]", "}}");
            }
            if (string.contains("{") || string.contains("}")) {
                string = string.replace("}}  {{", "}}{{").replace("}} {{", "}}{{").replace("}}{{", "\n-").replace("{{", "-").replace("}}", VariableScope.nullOrMissingString);
            }
            return string;
        } catch (NullPointerException e) {
            throw new RuntimeException("NullPointer with key:" + str + " locale:" + getResourceBundle().getLocale().toString(), e);
        }
    }

    /* JADX INFO: renamed from: e */
    private static final boolean hasString(String str) {
        try {
            getResourceBundle().getString(str);
            return true;
        } catch (MissingResourceException e) {
            return false;
        }
    }

    /* JADX INFO: renamed from: a */
    public static final String getFormattedString(String str, String str2, Object... objArr) {
        try {
            return get(str, objArr);
        } catch (MissingResourceException e) {
            return str2;
        }
    }

    /* JADX INFO: renamed from: a */
    public static final String get(String str, Object... objArr) {
        String rawString = getRawString(str);
        if (objArr.length == 0) {
            return rawString;
        }
        return new MessageFormat(rawString).format(objArr, new StringBuffer(), (FieldPosition) null).toString();
    }

    /* JADX INFO: renamed from: b */
    public static final String translateMapName(String str) {
        if (str == null) {
            return null;
        }
        String strGroup = str;
        String strGroup2 = null;
        Matcher matcher = mapNamePattern.matcher(str);
        if (matcher.matches()) {
            strGroup = matcher.group(1);
            strGroup2 = matcher.group(2);
        }
        String str2 = "maps.name." + strGroup.trim().replace(" ", "_").replace(".tmx", VariableScope.nullOrMissingString).toLowerCase(java.util.Locale.ENGLISH);
        if (hasString(str2)) {
            String strReplace = get(str2, new Object[0]);
            if (strGroup2 != null) {
                strReplace = strReplace + strGroup2;
            }
            GameEngine.log("translated:" + strReplace);
            if (strReplace != null) {
                strReplace = strReplace.replace("_", " ");
            }
            return strReplace;
        }
        return str;
    }

    /* JADX INFO: renamed from: c */
    public static String convertInlineBlocks(String str) {
        if (!str.contains("[i:")) {
            return str;
        }
        int i = 0;
        StringBuffer stringBuffer = new StringBuffer();
        Matcher matcher = inlineBlockPattern.matcher(str);
        while (matcher.find()) {
            i++;
            if (i > 100) {
                GameEngine.logColored("convertInlineBlocks: Too many loops while parsing: " + str);
                return str;
            }
            String strGroup = matcher.group(1);
            String formattedString = getFormattedString(strGroup, null, new Object[0]);
            if (formattedString == null) {
                GameEngine.log("convertInlineBlocks: No key:" + strGroup);
                formattedString = "[No key: " + strGroup + "]";
            }
            matcher.appendReplacement(stringBuffer, formattedString);
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }
}
