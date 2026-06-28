package io.github.rwx.map;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;

import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MapMetadata {
    private MapMetadata() {
    }

    public static int getNumberOfPlayersInMap(String str) {
        String fileNameWithoutExtension = null;
        if (str != null) {
            fileNameWithoutExtension = Utility.getFileName(str);
        }
        if (fileNameWithoutExtension != null) {
            Matcher matcher = Pattern.compile("^ *\\[([^\\]]*)\\].*").matcher(fileNameWithoutExtension);
            if (matcher.matches()) {
                for (String str2 : matcher.group(1).split(";")) {
                    if (str2.startsWith("p") && str2.length() >= 2) {
                        String strSubstring = str2.substring(1);
                        try {
                            return Integer.parseInt(strSubstring);
                        } catch (NumberFormatException e) {
                            GameEngine.log("getNumberOfPlayersInMap: NumberFormatException:" + strSubstring);
                            return -1;
                        }
                    }
                }
            }
        }
        GameEngine.log("getNumberOfPlayersInMap: fail to match:" + fileNameWithoutExtension);
        return -1;
    }

    public static String getMapName(String str) {
        if (str == null) {
            return null;
        }
        if (str.contains(File.separator)) {
            String[] strArrSplit = str.split(Pattern.quote(File.separator));
            str = strArrSplit[strArrSplit.length - 1];
        }
        if (str.contains("/")) {
            String[] strArrSplit2 = str.split("/");
            str = strArrSplit2[strArrSplit2.length - 1];
        }
        String strGroup = null;
        Matcher matcher = Pattern.compile("^l\\d*;\\[.*\\](.+)\\.tmx").matcher(str);
        if (matcher.matches()) {
            strGroup = capitalizeFirst(matcher.group(1));
        }
        if (strGroup == null) {
            Matcher matcher2 = Pattern.compile("^l\\d*;(.+)\\.tmx").matcher(str);
            if (matcher2.matches()) {
                strGroup = capitalizeFirst(matcher2.group(1));
            }
        }
        if (strGroup == null) {
            Matcher matcher3 = Pattern.compile("^ *\\[.*\\](.+)\\.tmx").matcher(str);
            if (matcher3.matches()) {
                strGroup = capitalizeFirst(matcher3.group(1));
            }
        }
        if (strGroup == null) {
            Matcher matcher4 = Pattern.compile("(.*)\\.tmx").matcher(str);
            if (matcher4.matches()) {
                strGroup = capitalizeFirst(matcher4.group(1));
            }
        }
        if (strGroup == null) {
            strGroup = str;
        }
        String strReplace = strGroup.replace('_', ' ');
        if (strReplace.endsWith(".rwsave")) {
            strReplace = strReplace.replace(".rwsave", VariableScope.nullOrMissingString);
        }
        return strReplace;
    }

    private static String capitalizeFirst(String str) {
        if (str.length() >= 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }

    public static String getMapThumbnail(String str) {
        return str.replace(".tmx", VariableScope.nullOrMissingString) + "_map.png";
    }

    public static String getMapNameFromPath(String str) {
        if (str == null) {
            return null;
        }
        if (str.contains("/MOD|")) {
            return str.substring(str.indexOf("/MOD|"));
        }
        if (str.contains("/NEW_PATH|")) {
            return str.substring(str.indexOf("/NEW_PATH|"));
        }
        String[] strArrSplit = str.split("/");
        return strArrSplit[strArrSplit.length - 1];
    }

    public static boolean isDemoMap(String str, String str2) {
        Matcher matcher = Pattern.compile(".*\\[(.*)\\].*").matcher(str);
        if (matcher.matches() && (matcher.group(1).toLowerCase(Locale.ENGLISH) + "|").contains("demo|")) {
            return true;
        }
        return FileHelper.fileExists(str2.replace(".tmx", VariableScope.nullOrMissingString) + "_demo");
    }

    public static boolean isSkirmishMap(String str) {
        return str.contains("skirmish/");
    }

    public static boolean isFromSdCard(String str) {
        return str.contains("SD/");
    }
}
