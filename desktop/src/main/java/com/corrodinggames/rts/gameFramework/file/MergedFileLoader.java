package com.corrodinggames.rts.gameFramework.file;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import java.io.File;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.e.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/e/e.class */
public class MergedFileLoader extends FileLoader {

    /* JADX INFO: renamed from: g */
    FileLoader fileLoader1;

    /* JADX INFO: renamed from: h */
    FileLoader fileLoader2;

    /* JADX INFO: renamed from: i */
    String tag1;

    /* JADX INFO: renamed from: j */
    String tag2;

    public MergedFileLoader(FileLoader fileLoader, String str, FileLoader fileLoader2, String str2) {
        this.fileLoader1 = fileLoader;
        this.tag1 = str;
        this.fileLoader2 = fileLoader2;
        this.tag2 = str2;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: a */
    public String getLastError() {
        String lastError = this.fileLoader1.getLastError();
        String lastError2 = this.fileLoader2.getLastError();
        if (lastError != null) {
            return lastError;
        }
        return lastError2;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: a */
    public void setLastError(String str) {
        this.fileLoader1.setLastError(str);
        this.fileLoader2.setLastError(str);
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: a */
    public String findFileExtension(String str, String str2) {
        return getPrimaryFileLoaderForPath(str).findFileExtension(str, str2);
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: b */
    public boolean isAbstractPath(String str) {
        return getPrimaryFileLoaderForPath(str).isAbstractPath(fixPath(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: c */
    public boolean isAbsolutePath(String str) {
        return getPrimaryFileLoaderForPath(str).isAbsolutePath(fixPath(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: d */
    public String applyModPath(String str) {
        return getPrimaryFileLoaderForPath(str).applyModPath(fixPath(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: f */
    public String convertAbstractPath(String str) {
        return getPrimaryFileLoaderForPath(str).convertAbstractPath(fixPath(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: e */
    public String getFileName(String str) {
        return getPrimaryFileLoaderForPath(str).getFileName(fixPath(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: a */
    public boolean isDirectory(String str, boolean z) {
        FileLoader fileLoaderForPath = getFileLoaderForPath(str);
        String strFixPath = fixPath(str);
        if (fileLoaderForPath != null) {
            return fileLoaderForPath.isDirectory(fixPath(strFixPath), z);
        }
        boolean zIsDirectory = this.fileLoader1.isDirectory(fixPath(strFixPath), z);
        if (this.fileLoader2.isDirectory(fixPath(strFixPath), z)) {
            zIsDirectory = true;
        }
        return zIsDirectory;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: g */
    public boolean fileExists(String str) {
        return getPrimaryFileLoaderForPath(str).fileExists(fixPath(str));
    }

    /* JADX INFO: renamed from: q */
    public String fixPath(String str) {
        if (str == null) {
            return null;
        }
        int iIndexOf = str.indexOf(this.tag1);
        if (iIndexOf != -1) {
            String str2 = str.substring(0, iIndexOf) + str.substring(iIndexOf + this.tag1.length());
            if (str2.contains(this.tag1) || str2.contains(this.tag2)) {
                GameEngine.log("fixPath: double tag for: " + str);
            }
            return str2;
        }
        int iIndexOf2 = str.indexOf(this.tag2);
        if (iIndexOf2 != -1) {
            String str3 = str.substring(0, iIndexOf2) + str.substring(iIndexOf2 + this.tag2.length());
            if (str3.contains(this.tag1) || str3.contains(this.tag2)) {
                GameEngine.log("fixPath: double tag for: " + str);
            }
            return str3;
        }
        return str;
    }

    /* JADX INFO: renamed from: r */
    private FileLoader getFileLoaderForPath(String str) {
        if (str == null) {
            return null;
        }
        if (str.contains(this.tag1)) {
            return this.fileLoader1;
        }
        if (str.contains(this.tag2)) {
            return this.fileLoader2;
        }
        return null;
    }

    /* JADX INFO: renamed from: s */
    private FileLoader getPrimaryFileLoaderForPath(String str) {
        FileLoader fileLoaderForPath = getFileLoaderForPath(str);
        if (fileLoaderForPath != null) {
            return fileLoaderForPath;
        }
        return this.fileLoader1;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String[] b(String str, boolean z) {
        FileLoader fileLoaderForPath = getFileLoaderForPath(str);
        String strFixPath = fixPath(str);
        if (fileLoaderForPath != null) {
            return fileLoaderForPath.b(strFixPath, z);
        }
        String[] strArrB = this.fileLoader1.b(strFixPath, z);
        String[] strArrB2 = this.fileLoader2.b(strFixPath, z);
        if (strArrB == null && strArrB2 == null) {
            return null;
        }
        if (strArrB == null) {
            strArrB = new String[0];
        }
        if (strArrB2 == null) {
            strArrB2 = new String[0];
        }
        String[] strArr = new String[strArrB.length + strArrB2.length];
        for (int i = 0; i < strArrB.length; i++) {
            strArr[i] = this.tag1 + strArrB[i];
        }
        for (int i2 = 0; i2 < strArrB2.length; i2++) {
            strArr[i2 + strArrB.length] = this.tag2 + strArrB2[i2];
        }
        return strArr;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: i */
    public AssetInputStream openAsset(String str) {
        return this.fileLoader1.openAsset(str);
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: j */
    public AssetInputStream openAssetSteam(String str) {
        return getPrimaryFileLoaderForPath(str).openAssetSteam(fixPath(str));
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String b() {
        return this.fileLoader1.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: a */
    public File getRWFile(String str, String str2, boolean z) {
        return getPrimaryFileLoaderForPath(str).getRWFile(fixPath(str), str2, z);
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: m */
    public String getStorageTypeForPath(String str) {
        return getPrimaryFileLoaderForPath(str).getStorageTypeForPath(str);
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String d() {
        String strD = this.fileLoader1.d();
        if (this.fileLoader2.d() != null) {
            strD = strD + " and " + this.fileLoader2.d();
        }
        return strD;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public boolean e() {
        return this.fileLoader1.e() || this.fileLoader2.e();
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String o(String str) {
        if (str.startsWith("/") && this.tag1.endsWith("/")) {
            return "/" + this.tag1 + str.substring(1);
        }
        return this.tag1 + str;
    }
}
