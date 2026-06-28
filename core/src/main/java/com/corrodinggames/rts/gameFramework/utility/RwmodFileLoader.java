package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.ag */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/ag.class */
public class RwmodFileLoader extends IFileLoader {
    static HashMap a = new HashMap();

    public static void h(String str) {
        GameEngine.log("Zip: " + str);
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: a */
    public boolean exists(String str) {
        if (str.endsWith(".rwmod") || str.endsWith(".rwmod/") || str.endsWith(".rwmod\\")) {
            return true;
        }
        ZipHelper zipHelperD = d(str, true);
        if (zipHelperD == null) {
            return false;
        }
        return zipHelperD.c(l(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: f */
    public String getLastModified(String str) {
        return str;
    }

    public static boolean i(String str) {
        if (str.contains(".rwmod/") || str.contains(".rwmod\\") || str.endsWith(".rwmod")) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: d */
    public boolean move(String str) {
        if (str.endsWith(".rwmod") || str.endsWith(".rwmod/") || str.endsWith(".rwmod\\")) {
            return true;
        }
        ZipHelper zipHelperD = d(str, true);
        if (zipHelperD == null) {
            return false;
        }
        return zipHelperD.d(l(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: e */
    public boolean createDirectory(String str) {
        h("createDirectory not supported in zip files: " + str);
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: b */
    public String[] listDir(String str) {
        ZipHelper zipHelperD = d(str, true);
        if (zipHelperD == null) {
            return null;
        }
        return zipHelperD.e(l(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: a */
    public long getSize(String str, boolean z) {
        ZipHelper zipHelperD = d(str, z);
        if (zipHelperD == null) {
            return -1L;
        }
        return zipHelperD.h(l(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: b */
    public AssetInputStream openAssetInputStream(String str, boolean z) {
        ZipHelper zipHelperD = d(str, z);
        if (zipHelperD == null) {
            return null;
        }
        return zipHelperD.i(l(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: g */
    public long getFullPath(String str) {
        String strJ = j(str);
        IFileLoader zipFileLoaderForPath = FileLoaderFactory.getZipFileLoaderForPath(strJ);
        if (zipFileLoaderForPath != null) {
            return zipFileLoaderForPath.getFullPath(strJ);
        }
        return new File(strJ).lastModified();
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: c */
    public OutputStream openOutputStream(String str, boolean z) {
        h("writableOutputSteam not supported in zip files: " + str);
        return null;
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: a */
    public boolean getRWFile(String str, String str2) {
        h("Rename not supported in zip files: " + str + " to " + str2);
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: c */
    public boolean delete(String str) {
        h("Delete not supported in zip files: " + str);
        return false;
    }

    public static String j(String str) {
        int iIndexOf = str.indexOf(".rwmod/");
        int iIndexOf2 = str.indexOf(".rwmod\\");
        if (iIndexOf2 != -1 && (iIndexOf2 < iIndexOf || iIndexOf == -1)) {
            iIndexOf = iIndexOf2;
        }
        if (iIndexOf == -1 && str.endsWith(".rwmod")) {
            iIndexOf = str.length() - ".rwmod".length();
        }
        if (iIndexOf == -1) {
            throw new RuntimeException("Could not find .rwmod in path: " + str);
        }
        return str.substring(0, iIndexOf + ".rwmod".length());
    }

    public static ZipHelper d(String str, boolean z) {
        ZipHelper zipHelper;
        String strConvertAbstractPath;
        String strJ = j(str);
        synchronized (a) {
            ZipHelper zipHelper2 = (ZipHelper) a.get(strJ);
            if (zipHelper2 == null) {
                if (z) {
                    strConvertAbstractPath = strJ;
                } else {
                    strConvertAbstractPath = FileHelper.convertAbstractPath(strJ);
                }
                try {
                    try {
                        zipHelper2 = new ZipHelper(strJ, strConvertAbstractPath);
                        a.put(strJ, zipHelper2);
                    } catch (IOException e) {
                        h("Failed to open source zip: '" + strConvertAbstractPath + "'");
                        e.printStackTrace();
                        String str2 = "Failed to open zip, " + e.getMessage();
                        if (FileHelper.isDirectory(strJ)) {
                            h("isDirectory: " + strJ);
                            str2 = "Failed to open .rwmod file (Appears to FastArrayList a directory!). Please remove .rwmod from any folder names.";
                        }
                        FileHelper.setWritePath(str2 + VariableScope.nullOrMissingString);
                        return null;
                    }
                } catch (IllegalArgumentException e2) {
                    h("Failed to open source zip: '" + strConvertAbstractPath + "'");
                    e2.printStackTrace();
                    FileHelper.setWritePath("Failed to open zip, " + e2.getMessage());
                    return null;
                }
            }
            zipHelper = zipHelper2;
        }
        return zipHelper;
    }

    public static void e(String str, boolean z) {
        final String strJ = j(str);
        synchronized (a) {
            final ZipHelper zipHelper = (ZipHelper) a.remove(strJ);
            if (zipHelper != null) {
                GameEngine.log("Closing zip file: " + strJ);
                new Thread(new Runnable() { // from class: com.corrodinggames.rts.gameFramework.utility.ag.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            Thread.sleep(1500L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        GameEngine.log("Running delayed close of zip: " + strJ);
                        zipHelper.a();
                    }
                }).start();
            }
        }
    }

    public void k(String str) {
        e(str, false);
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: a */
    public void isDirect() {
    }

    public static String l(String str) {
        String strSubstring = str.substring(j(str).length());
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
                h("getPathInZip: Backtracking attempt out of zip: " + strSubstring);
            }
            strSubstring = Utility.formatDate("/", arrayList);
        }
        return strSubstring;
    }
}
