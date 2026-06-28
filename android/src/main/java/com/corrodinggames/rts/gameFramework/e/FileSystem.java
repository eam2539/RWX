package com.corrodinggames.rts.gameFramework.e;

import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;

public final class FileSystem {
    private FileSystem() {
    }

    public static AssetInputStream k(String path) {
        return FileHelper.openAsset(path);
    }
}
