package com.corrodinggames.rts.gameFramework.utility.saf;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.IFileLoader;
import io.github.rwx.SafPlatformBridge;

import java.io.OutputStream;
import java.util.ArrayList;

/* Core fallback for Android Storage Access Framework paths. */
public class SafFileLoader extends IFileLoader {
    public static void h(String str) {
        GameEngine.log("Saf: " + str);
    }

    public static void i(String str) {
    }

    public static void j(String str) {
        GameEngine.log("Saf: " + str);
    }

    public static void k(String str) {
    }

    public static boolean l(String str) {
        return str.contains(".[saflink]/") || str.contains(".[saflink]\\") || str.endsWith(".[saflink]");
    }

    public static String m(String str) {
        int iIndexOf = str.indexOf(".[saflink]/");
        int iIndexOf2 = str.indexOf(".[saflink]\\");
        if (iIndexOf2 != -1 && (iIndexOf2 < iIndexOf || iIndexOf == -1)) {
            iIndexOf = iIndexOf2;
        }
        if (iIndexOf == -1 && str.endsWith(".[saflink]")) {
            iIndexOf = str.length() - ".[saflink]".length();
        }
        if (iIndexOf == -1) {
            throw new RuntimeException("Could not find saf link in path: " + str);
        }
        return str.substring(0, iIndexOf + ".[saflink]".length());
    }

    public static String n(String str) {
        String strSubstring = str.substring(m(str).length());
        if (strSubstring.startsWith("/") || strSubstring.startsWith("\\")) {
            strSubstring = strSubstring.substring(1);
        }
        if (strSubstring.startsWith("/") || strSubstring.startsWith("\\")) {
            strSubstring = strSubstring.substring(1);
        }
        if (strSubstring.contains("\\")) {
            strSubstring = strSubstring.replace("\\", "/");
        }
        if (strSubstring.contains("..")) {
            String[] strArrSplitByChar = Utility.splitByChar(strSubstring, '/');
            ArrayList arrayList = new ArrayList(strArrSplitByChar.length);
            int i = 0;
            for (int length = strArrSplitByChar.length - 1; length >= 0; length--) {
                if (strArrSplitByChar[length].equals("..")) {
                    i++;
                } else if (i > 0) {
                    i--;
                } else {
                    arrayList.add(0, strArrSplitByChar[length]);
                }
            }
            if (i != 0) {
                j("getPathInZip: Backtracking attempt out of zip: " + strSubstring);
            }
            strSubstring = Utility.joinStrings("/", arrayList);
        }
        return strSubstring;
    }

    public static String a(String str, boolean z) {
        String link = SafPlatformBridge.registerTree(str);
        if (link == null) {
            FileHelper.setLastError("Unable to register SAF folder");
        }
        return link;
    }

    @Override
    public void closeAll() {
    }

    @Override
    public boolean exists(String str) {
        if (str.endsWith(".[saflink]") || str.endsWith(".[saflink]/") || str.endsWith(".[saflink]\\")) {
            return SafPlatformBridge.exists(str);
        }
        return SafPlatformBridge.exists(str);
    }

    @Override
    public String convertAbstractPathForDebug(String str) {
        return str;
    }

    @Override
    public boolean isDirectory(String str) {
        return SafPlatformBridge.isDirectory(str);
    }

    @Override
    public boolean createDirectory(String str) {
        return SafPlatformBridge.createDirectory(str);
    }

    @Override
    public String[] listDir(String str) {
        return SafPlatformBridge.list(str);
    }

    @Override
    public long getSize(String str, boolean z) {
        return SafPlatformBridge.size(str);
    }

    @Override
    public AssetInputStream openAssetInputStream(String str, boolean z) {
        java.io.InputStream input = SafPlatformBridge.openInput(str);
        if (input == null) {
            return null;
        }
        try {
            return new AssetInputStream(input, str);
        } catch (java.io.FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public long getLastModified(String str) {
        return SafPlatformBridge.lastModified(str);
    }

    @Override
    public OutputStream openOutputStream(String str, boolean z) {
        return SafPlatformBridge.openOutput(str, z);
    }

    @Override
    public boolean rename(String str, String str2) {
        return SafPlatformBridge.rename(str, str2);
    }

    @Override
    public boolean delete(String str) {
        return SafPlatformBridge.delete(str);
    }
}
