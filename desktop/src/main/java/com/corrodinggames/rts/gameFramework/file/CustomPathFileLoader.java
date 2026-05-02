package com.corrodinggames.rts.gameFramework.file;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.e.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/e/d.class */
public class CustomPathFileLoader extends FileLoader {

    /* JADX INFO: renamed from: g */
    String dirPath;

    /* JADX INFO: renamed from: h */
    String name;

    /* JADX INFO: renamed from: i */
    String mapPath;

    public CustomPathFileLoader(String str, String str2) {
        this.dirPath = str;
        this.name = str2;
        if (!this.dirPath.endsWith("/") && !this.dirPath.endsWith("\\")) {
            this.dirPath += "/";
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String b() {
        return this.dirPath;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String d() {
        return this.name;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public boolean e() {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    /* JADX INFO: renamed from: e */
    public String getFileName(String str) {
        String fileName = super.getFileName(str);
        if (this.mapPath != null && fileName != null && fileName.startsWith(this.dirPath)) {
            String strSubstring = fileName.substring(this.dirPath.length());
            if (strSubstring.startsWith("/") || strSubstring.startsWith("\\")) {
                strSubstring = strSubstring.substring(1);
            }
            fileName = this.mapPath + strSubstring;
        }
        return fileName;
    }
}
