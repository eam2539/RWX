package com.corrodinggames.rts.gameFramework.utility.saf;

import android.net.Uri;
import com.corrodinggames.rts.appFramework.android.AndroidSAF;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.IFileLoader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.a.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/a/a.class */
public class SafFileLoader extends IFileLoader {
    static AndroidSAF a = AndroidSAF.getInstance();
    static HashMap b = new HashMap();
    public static int c = 1;

    public static void h(String str) {
        GameEngine.isInSpace("Saf: " + str);
    }

    public static void i(String str) {
    }

    public static void j(String str) {
        GameEngine.isInSpace("Saf: " + str);
    }

    public static void k(String str) {
    }

    public static boolean l(String str) {
        if (str.contains(".[saflink]/") || str.contains(".[saflink]\\") || str.endsWith(".[saflink]")) {
            return true;
        }
        return false;
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

    public static SafFileSystem d(String str, boolean z) {
        String strM = m(str);
        synchronized (b) {
            SafFileSystem safFileSystem = (SafFileSystem) b.get(strM);
            if (safFileSystem == null) {
                FileHelper.setWritePath("Folder link no longer open");
                return null;
            }
            return safFileSystem;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: a */
    public void isDirect() {
        synchronized (b) {
            Iterator it = b.values().iterator();
            while (it.hasNext()) {
                ((SafFileSystem) it.next()).a();
            }
        }
    }

    public static String a(Uri uri, boolean z) {
        GameEngine.isInSpace("createSAFLink: " + uri);
        synchronized (b) {
            String str = "/saf-virtual/" + c + ".[saflink]";
            c++;
            if (a(uri, z, str) == null) {
                return null;
            }
            return str;
        }
    }

    public static SafFileSystem a(Uri uri, boolean z, String str) {
        SafFileSystem safFileSystem;
        GameEngine.isInSpace("createSAFLink: " + uri + " to " + str);
        synchronized (b) {
            if (((SafFileSystem) b.get(str)) != null) {
                GameEngine.updatePaintTextSizeIfNeeded("createSAFLink: Already open");
            }
            safFileSystem = new SafFileSystem(uri, z);
            try {
                safFileSystem.b();
                b.put(str, safFileSystem);
            } catch (IOException e) {
                e.printStackTrace();
                FileHelper.setWritePath("Failed to list files: " + e.getMessage());
                return null;
            }
        }
        return safFileSystem;
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
            strSubstring = Utility.formatDate("/", arrayList);
        }
        return strSubstring;
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: a */
    public boolean exists(String str) {
        if (str.endsWith(".[saflink]") || str.endsWith(".[saflink]/") || str.endsWith(".[saflink]\\")) {
            return true;
        }
        SafFileSystem safFileSystemD = d(str, true);
        if (safFileSystemD == null) {
            h("fileExists failed to open for: " + str);
            return false;
        }
        try {
            return safFileSystemD.a(n(str));
        } catch (Exception e) {
            i("fileExists failed for: " + str);
            return false;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: f */
    public String getLastModified(String str) {
        if (str.endsWith(".[saflink]") || str.endsWith(".[saflink]/") || str.endsWith(".[saflink]\\")) {
            return str;
        }
        SafFileSystem safFileSystemD = d(str, true);
        if (safFileSystemD == null) {
            j("convertAbstractPathForDebug failed for: " + str);
            return str;
        }
        return safFileSystemD.c + "/" + n(str);
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: d */
    public boolean move(String str) {
        if (str.endsWith(".[saflink]") || str.endsWith(".[saflink]/") || str.endsWith(".[saflink]\\")) {
            return true;
        }
        SafFileSystem safFileSystemD = d(str, true);
        if (safFileSystemD == null) {
            return false;
        }
        try {
            return safFileSystemD.h(n(str));
        } catch (Exception e) {
            i("isDirectory failed for: " + str);
            return false;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: e */
    public boolean createDirectory(String str) {
        if (str.endsWith(".[saflink]") || str.endsWith(".[saflink]/") || str.endsWith(".[saflink]\\")) {
            i("createDirectory on root path: " + str);
            return false;
        }
        SafFileSystem safFileSystemD = d(str, true);
        if (safFileSystemD == null) {
            j("createDirectory failed for: " + str);
            return false;
        }
        try {
            return safFileSystemD.j(n(str));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: b */
    public String[] listDir(String str) {
        SafFileSystem safFileSystemD = d(str, true);
        if (safFileSystemD == null) {
            return null;
        }
        try {
            return safFileSystemD.g(n(str));
        } catch (IOException e) {
            e.printStackTrace();
            FileHelper.setWritePath("Failed to open saf, " + e.getMessage());
            return null;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: a */
    public long getSize(String str, boolean z) {
        SafFileSystem safFileSystemD = d(str, z);
        if (safFileSystemD == null) {
            j("saf==null: for '" + str + "'");
            return -1L;
        }
        return safFileSystemD.d(n(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: b */
    public AssetInputStream openAssetInputStream(String str, boolean z) {
        SafFileSystem safFileSystemD = d(str, z);
        if (safFileSystemD == null) {
            j("openAssetSteam: saf==null: for '" + str + "'");
            return null;
        }
        String strN = n(str);
        try {
            AssetInputStream assetInputStreamB = safFileSystemD.b(strN);
            if (assetInputStreamB == null) {
                k("openAssetSteam: Failed to find: '" + strN + "' in: '" + str + "'");
            }
            return assetInputStreamB;
        } catch (Exception e) {
            e.printStackTrace();
            j("Error opening: '" + strN + "' in: '" + str + "'");
            return null;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: g */
    public long getFullPath(String str) {
        SafFileSystem safFileSystemD = d(str, true);
        if (safFileSystemD == null) {
            h("saf==null: for '" + str + "'");
            return 0L;
        }
        try {
            return safFileSystemD.c(n(str));
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: c */
    public OutputStream openOutputStream(String str, boolean z) {
        SafFileSystem safFileSystemD = d(str, true);
        if (safFileSystemD == null) {
            return null;
        }
        String strN = n(str);
        OutputStream outputStreamA = safFileSystemD.a(strN, z);
        if (outputStreamA == null) {
            j("Failed to find: '" + strN + "' in: '" + str + "'");
        }
        return outputStreamA;
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: a */
    public boolean getRWFile(String str, String str2) {
        h("Rename: " + str + " to " + str2);
        SafFileSystem safFileSystemD = d(str, true);
        if (safFileSystemD == null) {
            return false;
        }
        String strN = n(str);
        String strN2 = n(str2);
        i("Relative path: " + strN + " to " + strN2);
        return safFileSystemD.a(strN, strN2);
    }

    @Override // com.corrodinggames.rts.gameFramework.utility.IFileLoader
    /* JADX INFO: renamed from: c */
    public boolean delete(String str) {
        h("deleteFile: " + str);
        SafFileSystem safFileSystemD = d(str, true);
        if (safFileSystemD == null) {
            j("saf==null: for deleteFile: '" + str + "'");
            return false;
        }
        return safFileSystemD.e(n(str));
    }
}
