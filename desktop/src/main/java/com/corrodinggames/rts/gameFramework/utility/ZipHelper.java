package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.appFramework.AppFrameworkUtils;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.ah */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/ah.class */
public class ZipHelper {
    String a = VariableScope.nullOrMissingString;
    String b;
    ZipFile c;
    String[] d;
    boolean e;

    public ZipHelper(String str, String str2) throws IOException {
        this.b = str;
        GameEngine.logWarningAndStack("Opening new zip at: " + str2);
        IFileLoader zipFileLoaderForPath = FileLoaderFactory.getZipFileLoaderForPath(str2);
        if (zipFileLoaderForPath != null) {
            GameEngine.log("Temp file needed for zip with SAF interface");
            if (!GameEngine.isAndroidPlatform()) {
                throw new IOException("Failed to open source zip with mapper: " + str2);
            }
            long jA = PerformanceProfiler.a();
            AssetInputStream assetInputStreamOpenAssetInputStream = zipFileLoaderForPath.openAssetInputStream(str2, true);
            if (assetInputStreamOpenAssetInputStream == null) {
                throw new IOException("Failed to open file of zip: " + str2);
            }
            this.c = a(assetInputStreamOpenAssetInputStream, (Charset) null);
            GameEngine.log("Streamed zip open took:" + PerformanceProfiler.a(PerformanceProfiler.a(jA)));
        } else {
            this.c = new ZipFile(str2);
        }
        try {
            b();
        } catch (IllegalArgumentException e) {
            RwmodFileLoader.h("Failed to open source zip with unicode encoding, attempting with ISO-8859-1");
            Charset charsetForName = Charset.forName("ISO-8859-1");
            try {
                if (zipFileLoaderForPath != null) {
                    GameEngine.log("Temp file needed for zip with SAF interface");
                    if (!GameEngine.isAndroidPlatform()) {
                        throw new IOException("Failed to open source zip with mapper: " + str2);
                    }
                    long jA2 = PerformanceProfiler.a();
                    this.c = a(zipFileLoaderForPath.openAssetInputStream(str2, true), charsetForName);
                    GameEngine.log("Streamed zip open took:" + PerformanceProfiler.a(PerformanceProfiler.a(jA2)));
                } else {
                    this.c = a(str2, charsetForName);
                }
                b();
            } catch (RuntimeException e2) {
                e.printStackTrace();
                throw new IOException("Failed to open source zip with unicode and ISO-8859-1 encoding", e2);
            }
        }
    }

    public void a() {
        if (!this.e) {
            this.e = true;
            if (this.c != null) {
                try {
                    this.c.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ZipFile a(InputStream inputStream, Charset charset) throws IOException {
        File fileCreateTempFileInContext = FileHelper.createTempFileInContext(AppFrameworkUtils.getContext(), "safMod", "zip");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileCreateTempFileInContext);
            Utility.copyStream(inputStream, fileOutputStream);
            fileOutputStream.close();
            inputStream.close();
            if (charset == null) {
                ZipFile zipFile = new ZipFile(fileCreateTempFileInContext);
                fileCreateTempFileInContext.delete();
                return zipFile;
            }
            ZipFile zipFileA = a(fileCreateTempFileInContext.getAbsolutePath(), charset);
            fileCreateTempFileInContext.delete();
            return zipFileA;
        } catch (Throwable th) {
            fileCreateTempFileInContext.delete();
            throw th;
        }
    }

    public static ZipFile a(String str, Charset charset) throws IOException {
        Constructor declaredConstructor = null;
        try {
            declaredConstructor = ZipFile.class.getDeclaredConstructor(String.class, Charset.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e2) {
            e2.printStackTrace();
        }
        if (declaredConstructor == null) {
            throw new IOException("Failed to open source zip with unicode encoding, and no method for ISO-8859-1");
        }
        try {
            return (ZipFile) declaredConstructor.newInstance(str, charset);
        } catch (IllegalAccessException e3) {
            throw new IOException(e3);
        } catch (IllegalArgumentException e4) {
            throw new IOException(e4);
        } catch (InstantiationException e5) {
            throw new IOException(e5);
        } catch (InvocationTargetException e6) {
            throw new IOException(e6);
        }
    }

    public void b() {
        long jA = PerformanceProfiler.a();
        ArrayList arrayList = new ArrayList();
        Enumeration<? extends ZipEntry> enumerationEntries = this.c.entries();
        while (enumerationEntries.hasMoreElements()) {
            String name = enumerationEntries.nextElement().getName();
            if (name == null) {
                throw new RuntimeException("filePath==null");
            }
            arrayList.add(name);
        }
        this.d = (String[]) arrayList.toArray(new String[0]);
        this.a = VariableScope.nullOrMissingString;
        String[] strArrE = e(VariableScope.nullOrMissingString);
        if (strArrE.length == 1 && d(strArrE[0])) {
            this.a = strArrE[0] + "/";
            for (int i = 0; i < this.d.length; i++) {
                if (this.d[i].startsWith(this.a)) {
                    this.d[i] = this.d[i].substring(this.a.length());
                }
            }
        }
        double dA = PerformanceProfiler.a(jA);
        if (dA > 3.0d) {
            GameEngine.log("zip: buildCache for: " + this.b + ", took:" + PerformanceProfiler.a(dA));
        }
    }

    public void a(String str) {
        GameEngine.log("Zip: " + str);
    }

    public boolean b(String str) {
        for (String str2 : this.d) {
            if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean c(String str) {
        for (String str2 : this.d) {
            if (str2.equals(str)) {
                return true;
            }
        }
        for (String str3 : this.d) {
            if (str3.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean d(String str) {
        if (!str.endsWith("/")) {
            str = str + "/";
        }
        if (str.equals("/")) {
            return true;
        }
        for (String str2 : this.d) {
            if (str2.contains(str)) {
                return true;
            }
        }
        return false;
    }

    public String[] e(String str) {
        if (str.equals(VariableScope.nullOrMissingString) || str.equals("/") || str.equals("\\")) {
            str = VariableScope.nullOrMissingString;
        } else if (!str.endsWith("/")) {
            str = str + "/";
        }
        ArrayList arrayList = new ArrayList();
        for (String str2 : this.d) {
            if (str.equals(VariableScope.nullOrMissingString) || str2.startsWith(str)) {
                String strSubstring = str2.substring(str.length());
                if (strSubstring.length() != 0 && !strSubstring.equals("..")) {
                    if (strSubstring.contains("/")) {
                        String strSubstring2 = strSubstring.substring(0, strSubstring.indexOf("/"));
                        if (!arrayList.contains(strSubstring2)) {
                            arrayList.add(strSubstring2);
                        }
                    } else {
                        arrayList.add(strSubstring);
                    }
                }
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public ZipEntry f(String str) {
        ZipEntry zipEntryNextElement = null;
        String str2 = this.a + str;
        ZipEntry entry = null;
        IllegalArgumentException illegalArgumentException = null;
        try {
            entry = this.c.getEntry(str2);
        } catch (IllegalArgumentException e) {
            illegalArgumentException = e;
        }
        if (entry == null && b(str) && !d(str)) {
            Enumeration<? extends ZipEntry> enumerationEntries = this.c.entries();
            while (enumerationEntries.hasMoreElements()) {
                try {
                    zipEntryNextElement = enumerationEntries.nextElement();
                } catch (IllegalArgumentException e2) {
                    e2.printStackTrace();
                }
                if (zipEntryNextElement.getName().equals(str2)) {
                    return zipEntryNextElement;
                }
            }
            a("getEntry: Still did not find file after workaround");
        }
        if (illegalArgumentException != null) {
            throw new RuntimeException("Failed to decode data in zip: " + str + " (Check zip encoding, utf-8 is recommended)", illegalArgumentException);
        }
        return entry;
    }

    public String g(String str) {
        String str2 = str;
        if (!str2.endsWith("/")) {
            str2 = str2 + "/";
        }
        for (String str3 : this.d) {
            if (str3.equals(str)) {
                return str3;
            }
        }
        for (String str4 : this.d) {
            if (str4.equals(str2)) {
                return str4;
            }
        }
        for (String str5 : this.d) {
            if (str5.equalsIgnoreCase(str)) {
                return str5;
            }
        }
        for (String str6 : this.d) {
            if (str6.equalsIgnoreCase(str2)) {
                return str6;
            }
        }
        return str;
    }

    public long h(String str) {
        ZipEntry zipEntryF = f(str);
        if (zipEntryF == null) {
            a("getEntrySize: File not found: " + str);
            return -1L;
        }
        return zipEntryF.getSize();
    }

    public AssetInputStream i(String str) {
        ZipEntry zipEntryF = f(str);
        if (zipEntryF == null) {
            zipEntryF = f(g(str));
        }
        if (zipEntryF == null) {
            return null;
        }
        try {
            try {
                return new AssetInputStream(this.c.getInputStream(zipEntryF), this.b + "/" + str);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }
}
