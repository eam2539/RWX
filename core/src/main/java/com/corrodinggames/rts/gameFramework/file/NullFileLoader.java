package com.corrodinggames.rts.gameFramework.file;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.e.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/e/f.class */
public class NullFileLoader extends FileLoader {
    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String b() {
        return "/[NONE]/";
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String[] b(String str, boolean z) {
        return null;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public boolean e() {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.file.FileLoader
    public String d() {
        return null;
    }
}
