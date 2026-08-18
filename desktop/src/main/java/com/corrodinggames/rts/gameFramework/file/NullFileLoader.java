package com.corrodinggames.rts.gameFramework.file;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.e.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/e/f.class */
public class NullFileLoader extends FileLoader {
    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String getGameDataPath() {
        return "/[NONE]/";
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String[] listDir(String str, boolean z) {
        return null;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public boolean isZip() {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String getStorageTypeName() {
        return null;
    }
}
