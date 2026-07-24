package com.corrodinggames.rts.gameFramework.utility.saf;

import android.content.Context;
import android.net.Uri;
import com.corrodinggames.rts.appFramework.AppFrameworkUtils;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/a/b.class */
class SafFileSystem {
    Uri a;
    Uri b;
    String c;
    boolean d;
    SafFile e;
    boolean f = false;
    int g = 1;

    public SafFileSystem(Uri uri, boolean z) {
        this.a = uri;
        this.b = SafFileLoader.a.buildDocumentUriUsingTree(uri);
        this.c = SafFileLoader.a.getReadablePath(c(), uri);
        this.d = z;
        SafFileLoader.h("== new SafLink write:" + z + " ==");
        SafFileLoader.h("root:" + this.a);
        SafFileLoader.h("rootDocument:" + this.b);
        SafFileLoader.h("shownUrl:" + this.c);
        this.e = new SafFile(this, VariableScope.nullOrMissingString, this.b, true);
    }

    public void a() {
        this.f = true;
        this.g++;
    }

    public void b() throws IOException {
        SafFileLoader.h("== testRoot ==");
        SafFileLoader.a.listWithDetails(c(), this.b);
    }

    public Context c() {
        return AppFrameworkUtils.getContext();
    }

    public boolean a(String str) {
        boolean z = false;
        if ("mod-info.txt".equals(str)) {
            z = true;
        }
        if (z) {
            return SafFileLoader.a.exists(c(), f(str));
        }
        if (k(str) == null) {
            return false;
        }
        return true;
    }

    public AssetInputStream b(String str) {
        Uri uriF;
        boolean z = false;
        if ("mod-info.txt".equals(str)) {
            z = true;
        }
        if (!z) {
            SafFile safFileK = k(str);
            if (safFileK == null) {
                return null;
            }
            uriF = safFileK.b;
        } else {
            uriF = f(str);
        }
        if (uriF == null) {
            return null;
        }
        try {
            InputStream inputStream = SafFileLoader.a.read(c(), uriF);
            if (inputStream == null) {
                return null;
            }
            return new AssetInputStream(inputStream, this.a + "/" + str);
        } catch (FileNotFoundException e) {
            SafFileLoader.j("openAssetSteam: " + e.getMessage() + " (file: " + str + ")");
            return null;
        } catch (IllegalArgumentException e2) {
            SafFileLoader.j("openAssetSteam: " + e2.getMessage() + " (file: " + str + ")");
            return null;
        }
    }

    public long c(String str) throws IOException {
        Uri uriF = f(str);
        if (uriF == null) {
            SafFileLoader.h("getLastModified file missing: " + str);
            return 0L;
        }
        return SafFileLoader.a.getLastModified(c(), uriF);
    }

    public long d(String str) {
        Uri uriF = f(str);
        if (uriF == null) {
            SafFileLoader.h("getEntrySize file missing: " + str);
            return -1L;
        }
        return SafFileLoader.a.getFileSize(c(), uriF);
    }

    public OutputStream a(String str, boolean z) {
        SafFileLoader.i("writableOutputSteam:" + str);
        Uri uriF = f(str);
        if (uriF == null) {
            String name = new File(str).getName();
            Uri uriI = i(str);
            SafFileLoader.i("writableOutputSteam creating: " + name + " in " + uriI);
            if (uriI == null) {
                SafFileLoader.j("writableOutputSteam: Parent folder not found for: " + str);
                return null;
            }
            try {
                uriF = SafFileLoader.a.createFile(c(), uriI, VariableScope.nullOrMissingString, name);
                SafFileLoader.i("newFileUri: " + uriF);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        String str2 = "w";
        if (z) {
            str2 = "wa";
        }
        try {
            OutputStream outputStreamWrite = SafFileLoader.a.write(c(), uriF, str2);
            a();
            return outputStreamWrite;
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public boolean e(String str) {
        if (!this.d) {
            SafFileLoader.j("deleteFile: Not open as writable");
            return false;
        }
        Uri uriF = f(str);
        if (uriF == null) {
            SafFileLoader.j("deleteFile: fileUri==null for:" + str);
            return false;
        }
        if (SafFileLoader.a.isDirectory(c(), uriF)) {
            throw new RuntimeException("Attempted to delete folder at: " + str + " url:" + uriF);
        }
        try {
            boolean zDeleteFile = SafFileLoader.a.deleteFile(c(), uriF);
            a();
            return zDeleteFile;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean a(String str, String str2) {
        if (!this.d) {
            SafFileLoader.j("renameFile: Not open as writable");
            return false;
        }
        Uri uriF = f(str);
        if (uriF == null) {
            SafFileLoader.j("renameFile: fileUri==null for:" + str);
            return false;
        }
        String fileNameWithoutExtension = Utility.getFileName(str2);
        SafFileLoader.i("Rename: " + uriF + " to " + fileNameWithoutExtension);
        try {
            Uri uriRenameFile = SafFileLoader.a.renameFile(c(), uriF, fileNameWithoutExtension);
            a();
            return uriRenameFile != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Uri f(String str) {
        SafFile safFileK = k(str);
        if (safFileK == null) {
            return null;
        }
        return safFileK.b;
    }

    public String[] g(String str) throws IOException {
        SafFile safFileK = k(str);
        if (safFileK == null || !safFileK.c) {
            return null;
        }
        HashMap mapA = safFileK.a();
        ArrayList arrayList = new ArrayList();
        Iterator it = mapA.keySet().iterator();
        while (it.hasNext()) {
            arrayList.add((String) it.next());
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public boolean h(String str) {
        if (str.equals("/") || str.equals(VariableScope.nullOrMissingString)) {
            return true;
        }
        SafFile safFileK = k(str);
        if (safFileK == null) {
            return false;
        }
        return safFileK.c;
    }

    public Uri i(String str) {
        String parent = new File(str).getParent();
        if (parent == null) {
            parent = VariableScope.nullOrMissingString;
        }
        Uri uriF = f(parent);
        if (uriF == null) {
            SafFileLoader.j("createDirectory: Parent folder: " + parent + " not found");
        }
        return uriF;
    }

    public boolean j(String str) throws FileNotFoundException {
        String name = new File(str).getName();
        Uri uriI = i(str);
        if (uriI == null) {
            return false;
        }
        Uri uriCreateDirectory = SafFileLoader.a.createDirectory(c(), uriI, name);
        a();
        return uriCreateDirectory != null;
    }

    private SafFile k(String str) {
        return l(str);
    }

    private SafFile l(String str) {
        String[] strArrSplit = str.split("[\\\\/]");
        SafFile safFile = this.e;
        for (String str2 : strArrSplit) {
            if (!str2.trim().equals(VariableScope.nullOrMissingString)) {
                try {
                    SafFile safFile2 = (SafFile) safFile.a().get(str2);
                    if (safFile2 != null) {
                        safFile = safFile2;
                    } else {
                        SafFile safFile3 = (SafFile) safFile.e.get(str2.toLowerCase(Locale.ROOT));
                        if (safFile3 != null) {
                            safFile = safFile3;
                        } else {
                            SafFileLoader.i("child null for: " + str);
                            SafFileLoader.i("element: " + str2);
                            return null;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return safFile;
    }
}
